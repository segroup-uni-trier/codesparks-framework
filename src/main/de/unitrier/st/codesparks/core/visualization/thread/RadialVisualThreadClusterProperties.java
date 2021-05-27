/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

import java.util.Arrays;

public class RadialVisualThreadClusterProperties extends VisualThreadClusterProperties
{
    private final int numberOfThreads;
    private double numberOfThreadsInClusterRatio;
    private double avgMetricValue;
    //private double maxMetricValue = Double.MIN_VALUE;
    private double sumMetricValue;
    private double arcStartAngle;
    private double arcAngle;
    private boolean isSelected;
    private double completeFilteredNumericalMetricValue;

    public RadialVisualThreadClusterProperties(
            final ThreadArtifactCluster cluster
            , final JBColor color
            , final int totalNumberOfThreadArtifacts
            //        , final AMetricIdentifier metricIdentifier
    )
    {
        super(cluster);
        setColor(color);
        numberOfThreads = cluster.size();
        numberOfThreadsInClusterRatio = cluster.size() / (double) totalNumberOfThreadArtifacts;
//        for (final AThreadArtifact thread : cluster)
//        {
//            final double metricValue = thread.getNumericalMetricValue(metricIdentifier);
//            maxMetricValue = Math.max(maxMetricValue, metricValue);
//        }
    }

    double getNumberOfSelectedThreadsRatio(final ThreadArtifactCluster cluster, final int totalNumberOfThreadArtifacts, final boolean ignoreFilter)
    {
        final long count = cluster.stream().filter(thread -> thread.isSelected() || ignoreFilter).count();
        return count / (double) totalNumberOfThreadArtifacts;
    }

    double getAverageMetricValueOfSelectedThreads(final ThreadArtifactCluster cluster, final AMetricIdentifier metricIdentifier, final boolean ignoreFilter)
    {
        double sum = 0;
        int threads = 0;
        for (final AThreadArtifact threadArtifact : cluster)
        {
            if (threadArtifact.isSelected() || ignoreFilter)
            {
                sum += threadArtifact.getNumericalMetricValue(metricIdentifier);
                threads++;
            }
        }
        avgMetricValue = sum / threads;
        return avgMetricValue;//sum / threads;
    }

    double getSumMetricValueOfSelectedThreads(final ThreadArtifactCluster threadCluster, final AMetricIdentifier metricIdentifier,
                                              final boolean ignoreFilter)
    {
        double sum = 0;
        for (final AThreadArtifact threadArtifact : threadCluster)
        {
            if (threadArtifact.isSelected() || ignoreFilter)
            {
                sum += threadArtifact.getNumericalMetricValue(metricIdentifier);
            }
        }
        sumMetricValue = sum;
        return sumMetricValue;
    }

    @SuppressWarnings("unused")
    private double calculateFilteredMedianNumericalMetricRatio(final ThreadArtifactCluster threadCluster, final AMetricIdentifier metricIdentifier,
                                                               final boolean ignoreFilter)
    {
        int unfilteredThreads = 0;
        for (final AThreadArtifact threadArtifact : threadCluster)
        {
            if (threadArtifact.isFiltered() && !ignoreFilter)
                continue;

            unfilteredThreads++;
        }

        final Double[] metricArray = new Double[threadCluster.size()];
        for (int i = 0; i < unfilteredThreads; i++)
        {
            final AThreadArtifact threadArtifact = threadCluster.get(i);
            if (threadArtifact.isFiltered() && ignoreFilter)
                continue;

            metricArray[i] = threadArtifact.getNumericalMetricValue(metricIdentifier);
        }

        Arrays.sort(metricArray);
        double median;
        if (metricArray.length % 2 == 0)
            median = (metricArray[metricArray.length / 2] + metricArray[metricArray.length / 2 - 1]) / 2;
        else
            median = metricArray[metricArray.length / 2];

        return median;
    }


    public int getNumberOfThreads()
    {
        return numberOfThreads;
    }

    public double getNumberOfThreadsInClusterRatio()
    {
        return numberOfThreadsInClusterRatio;
    }

    public double getAvgMetricValue()
    {
        return avgMetricValue;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

    public void setSelected(boolean selected)
    {
        isSelected = selected;
    }

    double getArcStartAngle()
    {
        return arcStartAngle;
    }

    double getArcAngle()
    {
        return arcAngle;
    }

    public void setArcStartAngle(double arcStartAngle)
    {
        this.arcStartAngle = arcStartAngle;
    }

    public void setArcAngle(double arcAngle)
    {
        this.arcAngle = arcAngle;
    }

    public void setNumberOfThreadsInClusterRatio(double numberOfThreadsInClusterRatio)
    {
        this.numberOfThreadsInClusterRatio = numberOfThreadsInClusterRatio;
    }

    public void setAvgMetricValue(double avgMetricValue)
    {
        this.avgMetricValue = avgMetricValue;
    }

    public double getCompleteFilteredNumericalMetricValue()
    {
        return completeFilteredNumericalMetricValue;
    }

    public void setCompleteFilteredNumericalMetricValue(double completeFilteredNumericalMetricValue)
    {
        this.completeFilteredNumericalMetricValue = completeFilteredNumericalMetricValue;
    }

    double getSumMetricValue()
    {
        return sumMetricValue;
    }

    public void setSumMetricValue(double sumMetricValue)
    {
        this.sumMetricValue = sumMetricValue;
    }
}
