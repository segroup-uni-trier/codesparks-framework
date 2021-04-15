/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import weka.clusterers.SimpleKMeans;
import weka.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WekaKMeans extends KThreadArtifactClusteringStrategy
{
    public WekaKMeans(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    public WekaKMeans(final AMetricIdentifier metricIdentifier, final int k)
    {
        super(metricIdentifier, k);
    }

    private static class ThreadInstance extends DenseInstance
    {
        private final AThreadArtifact threadArtifact;

        public ThreadInstance(final int numAttributes, final AThreadArtifact threadArtifact)
        {
            super(numAttributes);
            this.threadArtifact = threadArtifact;
        }

        AThreadArtifact getThreadArtifact()
        {
            return threadArtifact;
        }
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
        final ThreadArtifactClustering threadArtifactClusters = new ThreadArtifactClustering();
        try
        {
            Attribute metricAttribute = new Attribute(getMetricIdentifier().getName());
            final ArrayList<Attribute> attributes = new ArrayList<>(1);
            attributes.add(metricAttribute);
            final Instances instances = new Instances("threads", attributes, threadArtifacts.size())
            {
                @Override
                public boolean add(final Instance instance)
                {
                    m_Instances.add(instance);
                    return true;
                }
            };

            for (final AThreadArtifact threadArtifact : threadArtifacts)
            {
                final ThreadInstance instance = new ThreadInstance(1, threadArtifact);
                instance.setValue(metricAttribute, threadArtifact.getNumericalMetricValue(getMetricIdentifier()));
                instances.add(instance);
            }

            final SimpleKMeans simpleKMeans = new SimpleKMeans();
            simpleKMeans.setPreserveInstancesOrder(true);
            simpleKMeans.setNumClusters(k);
            simpleKMeans.setMaxIterations(100);
//            final SelectedTag initializationMethod = simpleKMeans.getInitializationMethod();
//            System.out.println(initializationMethod);

            //simpleKMeans.setInitializationMethod(new SelectedTag(SimpleKMeans.KMEANS_PLUS_PLUS, SimpleKMeans.TAGS_SELECTION));

            simpleKMeans.buildClusterer(instances);

            final int[] assignments = simpleKMeans.getAssignments();
            final Map<Integer, ThreadArtifactCluster> clusters = new HashMap<>(k);
            for (int i = 0; i < assignments.length; i++)
            {
                int assignment = assignments[i];
                final ThreadArtifactCluster orDefault = clusters.getOrDefault(assignment, new ThreadArtifactCluster());
                orDefault.add(((ThreadInstance) instances.get(i)).getThreadArtifact());
                clusters.put(assignment, orDefault);
            }

            threadArtifactClusters.addAll(clusters.values());

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println(threadArtifactClusters.toString(getMetricIdentifier()));

        return threadArtifactClusters;
    }
}
