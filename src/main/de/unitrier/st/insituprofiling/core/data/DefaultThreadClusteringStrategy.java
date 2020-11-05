package de.unitrier.st.insituprofiling.core.data;

import java.util.*;

/**
 * Implements a k-means clustering for k = 3 and utilizing the two factors: metricValue and callSite
 */
public class DefaultThreadClusteringStrategy implements IThreadClusteringStrategy
{
    private static volatile IThreadClusteringStrategy instance;

    public static IThreadClusteringStrategy getInstance()
    {
        if (instance == null)
        {
            synchronized (DefaultThreadClusteringStrategy.class)
            {
                if (instance == null)
                {
                    instance = new DefaultThreadClusteringStrategy();
                }
            }
        }
        return instance;
    }

    private DefaultThreadClusteringStrategy() { }

    private List<PointCluster> initClusters(List<Point> points, int k)
    {
        points.sort(Comparator.comparingDouble(o -> o.x * o.x + o.y * o.y));

        List<PointCluster> clusters = new ArrayList<>(k);

        if (k > 0)
        { // max value
            Point centroid = points.get(points.size() - 1);
            PointCluster artifactCluster = new PointCluster(centroid);
            clusters.add(artifactCluster);
        }
        if (k > 1)
        { // min value
            Point centroid = points.get(0);
            PointCluster artifactCluster = new PointCluster(centroid);
            clusters.add(artifactCluster);
        }
        if (k > 2)
        {
            Point centroid = points.get(points.size() / 2);
            PointCluster artifactCluster = new PointCluster(centroid);
            clusters.add(artifactCluster);
        }

        return clusters;
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(Collection<ThreadArtifact> threadArtifacts)
    {
        final int k = 3;
        final int maxIterations = 100;

        ThreadArtifactClustering artifactClusters = new ThreadArtifactClustering();

        int size = threadArtifacts.size();

        if (size < 1)
        {
            return artifactClusters;
        }

        int kToUse = Math.min(size, k);

        List<Point> points = createPoints(threadArtifacts);

        List<PointCluster> pointClusters = initClusters(points, kToUse);

        int iterations = 0;
        boolean clusterChanged = true;

        double epsilon = Math.max(1D / (2 * points.size()), 0.01);
        Map<Point, List<Point>> mustMatchPointListsMap = new HashMap<>();
        for (Point p : points)
        {
            mustMatchPointListsMap.put(p, new ArrayList<>());
            for (Point q : points)
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

        for (PointCluster pointCluster : pointClusters)
        {
            ThreadArtifactCluster artifactCluster = new ThreadArtifactCluster();
            for (Point point : pointCluster.getPoints())
            {
                artifactCluster.add(point.getThreadArtifact());
            }

            artifactClusters.add(artifactCluster);
        }

        return artifactClusters;
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

    private boolean calculateClustersForPoints(List<Point> points, List<PointCluster> clusters,
                                               Map<Point, List<Point>> mustMatchPointListsMap)
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

    private void calculateNewCentroids(Collection<PointCluster> clusters)
    {
        for (PointCluster cluster : clusters)
        {
            double x = 0;
            double y = 0;
            for (Point p : cluster.getPoints())
            {
                x += p.getX();
                y += p.getY();
            }
            int clusterSize = cluster.getPoints().size();
            double averageX = x / clusterSize;
            double averageY = y / clusterSize;

            Point avgCentroid = new Point(averageX, averageY);

            Point centroid = cluster.getCentroid();

            for (Point p : cluster.getPoints())
            {
                if (distance(p, avgCentroid) <= distance(centroid, avgCentroid))
                {
                    centroid = p;
                }
            }

            cluster.setCentroid(centroid);
            cluster.clear();
        }
    }

    private List<Point> createPoints(Collection<ThreadArtifact> threadArtifacts)
    {
        ArrayList<Point> points = new ArrayList<>();
        for (ThreadArtifact threadArtifact : threadArtifacts)
        {
            Point point = new Point(threadArtifact);
            points.add(point);
        }
        return points;
    }

    private final static class PointCluster
    {
        private Point centroid;
        //        private List<Point> clusterPoints;
        private final Set<Point> clusterPoints;

        PointCluster(Point centroid)
        {
            this.centroid = centroid;
//            clusterPoints = new ArrayList<>();
            clusterPoints = new HashSet<>();
        }

        Point getCentroid()
        {
            return this.centroid;
        }

        void setCentroid(Point centroid)
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
        private final ThreadArtifact threadArtifact;

        Point(ThreadArtifact threadArtifact)
        {
            this.x = 0;//getCharSum(threadArtifact.getCallSite());
            this.y = ((int) (100 * threadArtifact.getMetricValue())) / 100D;
            this.threadArtifact = threadArtifact;
            clusterIndex = -1;
        }

        Point(double x, double y)
        {
            this.threadArtifact = null;
            this.x = x;
            this.y = y;
        }

        int getClusterIndex()
        {
            return clusterIndex;
        }

        void setClusterIndex(int clusterIndex)
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
            return Objects.equals(threadArtifact, point.threadArtifact);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(threadArtifact);
        }

        ThreadArtifact getThreadArtifact()
        {
            return threadArtifact;
        }
    }
}
