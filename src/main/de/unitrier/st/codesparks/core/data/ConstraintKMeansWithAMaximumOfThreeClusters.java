/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.data;

import java.util.*;

/**
 * Implements a (partially constraint) k-means clustering for k = 3 and utilizing the two factors: metricValue and callSite
 */
public class ConstraintKMeansWithAMaximumOfThreeClusters extends AThreadArtifactClusteringStrategy
{
    private static final Map<AMetricIdentifier, AThreadArtifactClusteringStrategy> strategies = new HashMap<>();

    public static AThreadArtifactClusteringStrategy getInstance(final AMetricIdentifier metricIdentifier)
    {
        synchronized (ConstraintKMeansWithAMaximumOfThreeClusters.class)
        {
            AThreadArtifactClusteringStrategy instance = strategies.get(metricIdentifier);
            if (instance == null)
            {
                instance = new ConstraintKMeansWithAMaximumOfThreeClusters(metricIdentifier);
                strategies.put(metricIdentifier, instance);
            }
            return instance;
        }
    }

    private ConstraintKMeansWithAMaximumOfThreeClusters(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    private List<PointCluster> initClusters(List<Point> points, int k)
    {
        points.sort(Comparator.comparingDouble(o -> o.x * o.x + o.y * o.y));

        final List<PointCluster> clusters = new ArrayList<>(k);

        if (k > 0)
        { // max value
            final Point centroid = points.get(points.size() - 1);
            final PointCluster artifactCluster = new PointCluster(centroid);
            clusters.add(artifactCluster);
        }
        if (k > 1)
        { // min value
            final Point centroid = points.get(0);
            final PointCluster artifactCluster = new PointCluster(centroid);
            clusters.add(artifactCluster);
        }
        if (k > 2)
        {
            final Point centroid = points.get(points.size() / 2);
            final PointCluster artifactCluster = new PointCluster(centroid);
            clusters.add(artifactCluster);
        }

        return clusters;
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
        final int k = 3;
        final int maxIterations = 100;

        final ThreadArtifactClustering clustering = new ThreadArtifactClustering();

        int size = threadArtifacts.size();

        if (size < 1)
        {
            return clustering;
        }

        int kToUse = Math.min(size, k);

        final List<Point> points = createPoints(threadArtifacts, this.getMetricIdentifier());

        final List<PointCluster> pointClusters = initClusters(points, kToUse);

        int iterations = 0;
        boolean clusterChanged = true;

        final double epsilon = Math.max(1D / (2 * points.size()), 0.01);
        final Map<Point, List<Point>> mustMatchPointListsMap = new HashMap<>();
        for (final Point p : points)
        {
            mustMatchPointListsMap.put(p, new ArrayList<>());
            for (final Point q : points)
            {
                if (p == q) continue;
//                if (p != q)
//                {
//                double distance = distance(p,q);//Math.sqrt(distance(p, q));//Math.abs(p.getY() - q.getY());
//                double distance = mustMatchDistance(p,q);
                double distance = Math.abs(p.getY() - q.getY());
                if (distance <= epsilon)
                {
                    mustMatchPointListsMap.get(p).add(q);
                }
//                }
            }
        }

        while (clusterChanged && iterations <= maxIterations)
        {
            clusterChanged = calculateClustersForPoints(points, pointClusters, mustMatchPointListsMap);
            if (clusterChanged)
            {
                calculateNewCentroids(pointClusters);
                iterations++;
            }
        }

        for (final PointCluster pointCluster : pointClusters)
        {
            final ThreadArtifactCluster cluster = new ThreadArtifactCluster();
            for (final Point point : pointCluster.getPoints())
            {
                cluster.add(point.getThreadArtifact());
            }
            if (!cluster.isEmpty())
            {
                clustering.add(cluster);
            }
        }

        return clustering;
    }

    private double mustMatchDistance(Point p1, Point p2)
    {
//        ThreadArtifact threadArtifact1 = p1.getThreadArtifact();
//        if (threadArtifact1 != null)
//        {
//            ThreadArtifact threadArtifact2 = p2.getThreadArtifact();
//            if (threadArtifact2 != null)
//            {
//                String callSite1 = threadArtifact1.getCallSite();
//                String callSite2 = threadArtifact2.getCallSite();
//                if (callSite1 != null && !callSite1.equals(callSite2)) return Double.MAX_VALUE;
//            }
//        }
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    private boolean calculateClustersForPoints(final List<Point> points
            , final List<PointCluster> clusters
            , final Map<Point, List<Point>> mustMatchPointListsMap
    )
    {
        boolean clusterChanged = false;
        for (Point p : points)
        {
            double minDistance = Double.MAX_VALUE;
            int clusterToAddPointToIndex = 0;
            int oldPointClusterIndex = p.getClusterIndex();
            for (int currentClusterIndex = 0; currentClusterIndex < clusters.size(); currentClusterIndex++)
            {
                final double distance = distance(p, clusters.get(currentClusterIndex).getCentroid());
                final boolean constraintViolated = isConstraintViolated(mustMatchPointListsMap, clusters, currentClusterIndex, p);
                if (distance < minDistance && !constraintViolated)
                {
                    clusterToAddPointToIndex = currentClusterIndex;
                    minDistance = distance;
                }
            }
            if (oldPointClusterIndex != clusterToAddPointToIndex)
            {
                clusterChanged = true;
                p.setClusterIndex(clusterToAddPointToIndex);
            }

            clusters.get(clusterToAddPointToIndex).add(p);
        }
        return clusterChanged;
    }

    private double distance(Point p1, Point p2)
    {
//        ThreadArtifact threadArtifact1 = p1.getThreadArtifact();
//        if (threadArtifact1 != null)
//        {
//            ThreadArtifact threadArtifact2 = p2.getThreadArtifact();
//            if (threadArtifact2 != null)
//            {
//                String callSite1 = threadArtifact1.getCallSite();
//                String callSite2 = threadArtifact2.getCallSite();
//                if (callSite1 != null && !callSite1.equals(callSite2)) return Double.MAX_VALUE;
//            }
//        }
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    private boolean isConstraintViolated(final Map<Point, List<Point>> mustMatchPointListsMap,
                                         final List<PointCluster> clusters,
                                         final int currentClusterIndex,
                                         final Point currentPoint)
    {
        final List<Point> mustMatchPointList = mustMatchPointListsMap.get(currentPoint);
        final PointCluster currentCluster = clusters.get(currentClusterIndex);

        // There is at least one must matching point in another cluster
        // => violation
        for (PointCluster pointCluster : clusters)
        {
            if (currentCluster == pointCluster)
            {
                continue;
            }
//            if (currentCluster != pointCluster)
//            {
            for (Point point : mustMatchPointList)
            {
                if (pointCluster.getPoints().contains(point))
                {
                    return true;
                }
            }
//            }
        }

        return false;
    }

    private void calculateNewCentroids(final Collection<PointCluster> pointClusters)
    {
        for (final PointCluster pointCluster : pointClusters)
        {
            double x = 0;
            double y = 0;
            for (Point p : pointCluster.getPoints())
            {
                x += p.getX();
                y += p.getY();
            }
            int clusterSize = pointCluster.getPoints().size();
            double averageX = x / clusterSize;
            double averageY = y / clusterSize;

            Point avgCentroid = new Point(averageX, averageY);

            Point centroid = pointCluster.getCentroid();

            for (Point p : pointCluster.getPoints())
            {
                if (distance(p, avgCentroid) <= distance(centroid, avgCentroid))
                {
                    centroid = p;
                }
            }

            pointCluster.setCentroid(centroid);
            pointCluster.clear();
        }
    }

    private List<Point> createPoints(final Collection<AThreadArtifact> threadArtifacts, final AMetricIdentifier metricIdentifier)
    {
        final ArrayList<Point> points = new ArrayList<>();
        for (final AThreadArtifact threadArtifact : threadArtifacts)
        {
            final Point point = new Point(threadArtifact, metricIdentifier);
            points.add(point);
        }
        return points;
    }

    private final static class PointCluster
    {
        private Point centroid;
        //        private List<Point> clusterPoints;
        private final Set<Point> clusterPoints;

        PointCluster(final Point centroid)
        {
            this.centroid = centroid;
//            clusterPoints = new ArrayList<>();
            clusterPoints = new HashSet<>();
        }

        Point getCentroid()
        {
            return this.centroid;
        }

        void setCentroid(final Point centroid)
        {
            this.centroid = centroid;
        }

        void add(Point p)
        {
            clusterPoints.add(p);
        }

//        List<Point> getPoints()
//        {
//            return clusterPoints;
//        }

        Set<Point> getPoints()
        {
            return clusterPoints;
        }

        void clear()
        {
            clusterPoints.clear();
        }
    }

    private final static class Point
    {
        private final double x;
        private final double y;
        private int clusterIndex;
        private final AThreadArtifact codeSparksThread;

        Point(final AThreadArtifact codeSparksThread, final AMetricIdentifier metricIdentifier)
        {
            this.x = 0;//getCharSum(threadArtifact.getCallSite());
            this.y = ((int) (100 * codeSparksThread.getNumericalMetricValue(metricIdentifier))) / 100D;
            this.codeSparksThread = codeSparksThread;
            clusterIndex = -1;
        }

        Point(double x, double y)
        {
            this.codeSparksThread = null;
            this.x = x;
            this.y = y;
        }

        int getClusterIndex()
        {
            return clusterIndex;
        }

        void setClusterIndex(final int clusterIndex)
        {
            this.clusterIndex = clusterIndex;
        }

        public double getX()
        {
            return x;
        }

        public double getY()
        {
            return y;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return Objects.equals(codeSparksThread, point.codeSparksThread);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(codeSparksThread);
        }

        AThreadArtifact getThreadArtifact()
        {
            return codeSparksThread;
        }
    }
}
