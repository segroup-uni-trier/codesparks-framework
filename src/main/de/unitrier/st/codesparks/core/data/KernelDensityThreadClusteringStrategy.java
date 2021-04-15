/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

public class KernelDensityThreadClusteringStrategy extends AThreadArtifactClusteringStrategy
{
    public KernelDensityThreadClusteringStrategy(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
        final ThreadArtifactClustering threadArtifactClusters = new ThreadArtifactClustering();

//        SparkConf conf = new SparkConf().setAppName("CodeSparks");
//        JavaSparkContext jsc = new JavaSparkContext(conf);
//
//        JavaRDD<Double> data = jsc.parallelize(
//                Arrays.asList(1.0, 1.0, 1.0, 2.0, 3.0, 4.0, 5.0, 5.0, 6.0, 7.0, 8.0, 9.0, 9.0));
//
//        // Note, only gaussian kernel available
//        final KernelDensity kernelDensity = new KernelDensity()
//                .setSample(data)
//                .setBandwidth(1.0); // Sets the bandwidth (standard deviation) of the Gaussian kernel (default: 1.0).
//
//        final int size = threadArtifacts.size();
//        double[] points = new double[size];
//
//        final AMetricIdentifier metricIdentifier = getMetricIdentifier();
//        int i = 0;
//        for (final AThreadArtifact threadArtifact : threadArtifacts)
//        {
//            final double numericalMetricValue = threadArtifact.getNumericalMetricValue(metricIdentifier);
//            points[i++] = numericalMetricValue;
//        }
//
//        final double[] estimate = kernelDensity.estimate(points);


        return threadArtifactClusters;
    }
}
