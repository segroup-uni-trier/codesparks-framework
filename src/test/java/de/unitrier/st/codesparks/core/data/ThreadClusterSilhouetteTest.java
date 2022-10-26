/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public final class ThreadClusterSilhouetteTest
{
    private static final AMetricIdentifier testMetric = new AMetricIdentifier()
    {
        @Override
        public String getIdentifier()
        {
            return "testMetric";
        }

        @Override
        public String getName()
        {
            return "Test metric";
        }

        @Override
        public String getDisplayString()
        {
            return "Test metric";
        }

        @Override
        public String getShortDisplayString()
        {
            return "Test metric";
        }

        @Override
        public Class<Double> getMetricValueType()
        {
            return Double.class;
        }

//        @Override
//        public boolean isNumerical()
//        {
//            return true;
//        }

        @Override
        public boolean isRelative()
        {
            return false;
        }
    };

    private static AThreadArtifact t1;
    private static AThreadArtifact t2;
    private static AThreadArtifact t3;
    private static AThreadArtifact t4;
    private static AThreadArtifact t5;

    private static ThreadArtifactCluster a1;
    private static ThreadArtifactCluster a2;

    private static ThreadArtifactCluster b1;
    private static ThreadArtifactCluster b2;

    private static ThreadArtifactClustering A;
    private static ThreadArtifactClustering B;

    @BeforeClass
    public static void setup()
    {
        t1 = new DefaultThreadArtifact("t1");
        t1.setNumericalMetricValue(testMetric, 0.9);
        t2 = new DefaultThreadArtifact("t2");
        t2.setNumericalMetricValue(testMetric, 1.1);
        t3 = new DefaultThreadArtifact("t3");
        t3.setNumericalMetricValue(testMetric, 3.0);
        t4 = new DefaultThreadArtifact("t4");
        t4.setNumericalMetricValue(testMetric, 3.1);
        t5 = new DefaultThreadArtifact("t5");
        t5.setNumericalMetricValue(testMetric, 2.9);

        a1 = new ThreadArtifactCluster();
        a1.add(t1);
        a1.add(t2);

        a2 = new ThreadArtifactCluster();
        a2.add(t3);
        a2.add(t4);
        a2.add(t5);

        A = new ThreadArtifactClustering(null);
        A.add(a1);
        A.add(a2);

        b1 = new ThreadArtifactCluster();
        b1.add(t1);
        b1.add(t2);
        b1.add(t5);

        b2 = new ThreadArtifactCluster();
        b2.add(t3);
        b2.add(t4);

        B = new ThreadArtifactClustering(null);
        B.add(b1);
        B.add(b2);
    }

    @Test
    public void testDistThreadT1toT2()
    {
        final double dist = t1.dist(t2, testMetric);
        Assert.assertEquals(0.2, dist, 0.00001);
    }

    @Test
    public void testDistToAllOtherObjectsInTheSameCluster()
    {
        final double distA1T1 = a1.dist(t1, testMetric);
        Assert.assertEquals(0.2, distA1T1, 0.00001);
        final double distA1T2 = a1.dist(t2, testMetric);
        Assert.assertEquals(0.2, distA1T2, 0.00001);
        final double distA2T3 = a2.dist(t3, testMetric);
        Assert.assertEquals(0.1, distA2T3, 0.00001);
        final double distA2T4 = a2.dist(t4, testMetric);
        Assert.assertEquals(0.15, distA2T4, 0.00001);
        final double distA2T5 = a2.dist(t5, testMetric);
        Assert.assertEquals(0.15, distA2T5, 0.00001);

        final double distB1T1 = b1.dist(t1, testMetric);
        Assert.assertEquals(1.1, distB1T1, 0.00001);
        final double distB1T2 = b1.dist(t2, testMetric);
        Assert.assertEquals(1.0, distB1T2, 0.00001);
        final double distB1T5 = b1.dist(t5, testMetric);
        Assert.assertEquals(1.9, distB1T5, 0.00001);
        final double distB2T3 = b2.dist(t3, testMetric);
        Assert.assertEquals(0.1, distB2T3, 0.00001);
        final double distB2T4 = b2.dist(t4, testMetric);
        Assert.assertEquals(0.1, distB2T4, 0.00001);
    }

    @Test
    public void testDistToAllOtherObjectsInTheSameClusterOnASingleElementCluster()
    {
        AThreadArtifact thr = new DefaultThreadArtifact("thr");
        thr.setNumericalMetricValue(testMetric, 1D);
        ThreadArtifactCluster c = new ThreadArtifactCluster();
        c.add(thr);

        final double dist = c.dist(thr, testMetric);
        Assert.assertEquals(0D, dist, 0.00001);
    }

    @Test
    public void distFromT1ToNearestClusterConcerningClusteringA()
    {
        final double dist = A.distToNearestCluster(t1, testMetric);
        Assert.assertEquals(2.1, dist, 0.00001);
    }

    @Test
    public void distFromT2ToNearestClusterConcerningClusteringA()
    {
        final double dist = A.distToNearestCluster(t2, testMetric);
        Assert.assertEquals(1.9, dist, 0.00001);
    }

    @Test
    public void distFromT3ToNearestClusterConcerningClusteringA()
    {
        final double dist = A.distToNearestCluster(t3, testMetric);
        Assert.assertEquals(2D, dist, 0.00001);
    }

    @Test
    public void distFromT4ToNearestClusterConcerningClusteringA()
    {
        final double dist = A.distToNearestCluster(t4, testMetric);
        Assert.assertEquals(2.1, dist, 0.00001);
    }

    @Test
    public void distFromT5ToNearestClusterConcerningClusteringA()
    {
        final double dist = A.distToNearestCluster(t5, testMetric);
        Assert.assertEquals(1.9, dist, 0.00001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteWhereThreadNotInPassedCluster()
    {
        A.silhouette(t1, a2, testMetric);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteWherePassedClusterNotInTheClustering()
    {
        A.silhouette(t1, b1, testMetric);
    }

    @Test
    public void testSilhouetteWhereOnlyOneThreadIsInTheCluster()
    {
        AThreadArtifact thr = new DefaultThreadArtifact("thr");
        thr.setNumericalMetricValue(testMetric, 1D);
        ThreadArtifactCluster c = new ThreadArtifactCluster();
        c.add(thr);
        ThreadArtifactClustering A = new ThreadArtifactClustering(null);
        A.add(c);
        final double silhouette = A.silhouette(thr, c, testMetric);
        Assert.assertEquals(0D, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT1inA()
    {
        final double silhouette = A.silhouette(t1, a1, testMetric);
        Assert.assertEquals(1.9 / 2.1, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT2inA()
    {
        final double silhouette = A.silhouette(t2, a1, testMetric);
        Assert.assertEquals(1.7 / 1.9, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT3inA()
    {
        final double silhouette = A.silhouette(t3, a2, testMetric);
        Assert.assertEquals(1.9 / 2.0, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT4inA()
    {
        final double silhouette = A.silhouette(t4, a2, testMetric);
        Assert.assertEquals(1.95 / 2.1, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT5inA()
    {
        final double silhouette = A.silhouette(t5, a2, testMetric);
        Assert.assertEquals(1.75 / 1.9, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT1inB()
    {
        final double silhouette = B.silhouette(t1, b1, testMetric);
        Assert.assertEquals(1.05 / 2.15, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT2inB()
    {
        final double silhouette = B.silhouette(t2, b1, testMetric);
        Assert.assertEquals(0.95 / 1.95, silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT5inB() // This is the interesting test because t5 actually does not fit to the cluster b1
    {
        final double silhouette = B.silhouette(t5, b1, testMetric);
        Assert.assertEquals((0.15 - 1.9) / 1.9, silhouette, 0.00001); // A value close to -1 denotes that t5 put in cluster b1 is a bad choice!
    }

    @Test
    public void testSilhouetteOfT3inB()
    {
        final double silhouette = B.silhouette(t3, b2, testMetric);
        Assert.assertEquals((4.1 / 3 - 0.1) / (4.1 / 3), silhouette, 0.00001);
    }

    @Test
    public void testSilhouetteOfT4inB()
    {
        final double silhouette = B.silhouette(t4, b2, testMetric);
        Assert.assertEquals((4.4 / 3 - 0.1) / (4.4 / 3), silhouette, 0.00001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void silhouetteCoefficientWhereThePassedClusterNotInTheClustering()
    {
        A.meanSilhouetteCoefficientOfCluster(b1, testMetric);
    }

    @Test
    public void silhouetteCoefficientOfClusterA1()
    {
        final double silhouetteCoefficient = A.meanSilhouetteCoefficientOfCluster(a1, testMetric);
        final double expected = (1.9 / 2.1 + 1.7 / 1.9) / a1.size();
        Assert.assertEquals(expected, silhouetteCoefficient, 0.00001);
    }

    @Test
    public void silhouetteCoefficientOfClusterA2()
    {
        final double silhouetteCoefficient = A.meanSilhouetteCoefficientOfCluster(a2, testMetric);
        final double expected = (1.9 / 2.0 + 1.95 / 2.1 + 1.75 / 1.9) / a2.size();
        Assert.assertEquals(expected, silhouetteCoefficient, 0.00001);
    }

    @Test
    public void silhouetteCoefficientOfClusterB1()
    {
        final double silhouetteCoefficient = B.meanSilhouetteCoefficientOfCluster(b1, testMetric);
        final double expected = (1.05 / 2.15 + 0.95 / 1.95 + (0.15 - 1.9) / 1.9) / b1.size();
        Assert.assertEquals(expected, silhouetteCoefficient, 0.00001);
    }

    @Test
    public void silhouetteCoefficientOfClusterB2()
    {
        final double silhouetteCoefficient = B.meanSilhouetteCoefficientOfCluster(b2, testMetric);
        final double expected = ((4.1 / 3 - 0.1) / (4.1 / 3) + (4.4 / 3 - 0.1) / (4.4 / 3)) / b2.size();
        Assert.assertEquals(expected, silhouetteCoefficient, 0.00001);
    }

    @Test
    public void meanElementSilhouetteCoefficientOfClusteringA()
    {
        final double silhouetteCoefficient = A.silhouetteCoefficientAsMeanOfEachElementSilhouette(testMetric);
        final double expected = (1.9 / 2.1 + 1.7 / 1.9 + 1.9 / 2.0 + 1.95 / 2.1 + 1.75 / 1.9) / (a1.size() + a2.size());
        Assert.assertEquals(expected, silhouetteCoefficient, 0.00001);
    }

    @Test
    public void meanElementSilhouetteCoefficientOfClusteringB()
    {
        final double silhouetteCoefficient = B.silhouetteCoefficientAsMeanOfEachElementSilhouette(testMetric);
        final double expected =
                (1.05 / 2.15 + 0.95 / 1.95 + (0.15 - 1.9) / 1.9 + (4.1 / 3 - 0.1) / (4.1 / 3) + (4.4 / 3 - 0.1) / (4.4 / 3)) / (b1.size() + b2.size());
        Assert.assertEquals(expected, silhouetteCoefficient, 0.00001);
    }

    @Test
    public void meanElementSilhouetteCoefficient()
    {
        final double silhouetteCoefficientA = A.silhouetteCoefficientAsMeanOfEachElementSilhouette(testMetric);
        final double silhouetteCoefficientB = B.silhouetteCoefficientAsMeanOfEachElementSilhouette(testMetric);
        Assert.assertTrue(silhouetteCoefficientA > silhouetteCoefficientB); // A is the better clustering!
    }

    @Test
    public void meanClusterSilhouetteCoefficient()
    {
        final double silhouetteCoefficientA = A.silhouetteCoefficientAsMeanOfEachClusterSilhouette(testMetric);
        final double silhouetteCoefficientB = B.silhouetteCoefficientAsMeanOfEachClusterSilhouette(testMetric);
        Assert.assertTrue(silhouetteCoefficientA > silhouetteCoefficientB); // A is the better clustering!
    }
}
