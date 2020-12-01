package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

import java.util.Arrays;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class RadialVisualThreadClusterProperties extends VisualThreadClusterProperties
{
    private int numberOfThreads;
    private double threadRatio;
    private double numericalMetricRatio;
    private double numericalMetricRationSum;
    private double arcStartAngle;
    private double arcAngle;
    private boolean isSelected;
    private double completeFilteredNumericalMetricValue;

    public RadialVisualThreadClusterProperties(
            final CodeSparksThreadCluster cluster
            , final JBColor color
            , final int numberOfArtifactThreads
            , final IMetricIdentifier metricIdentifier)
    {
        super(cluster, color);
        calculateNumberOfThreads(cluster);
        calculateThreadRatio(cluster, numberOfArtifactThreads);
        calculateMaxNumericalMetricRatio(cluster, metricIdentifier);
    }

    private void calculateNumberOfThreads(CodeSparksThreadCluster cluster)
    {
        numberOfThreads = cluster.size();
    }

    private void calculateThreadRatio(CodeSparksThreadCluster cluster, int numberOfArtifactThreads)
    {
        threadRatio = (double) cluster.size() / numberOfArtifactThreads;
    }

    private void calculateMaxNumericalMetricRatio(CodeSparksThreadCluster threadCluster, final IMetricIdentifier metricIdentifier)
    {
        double max = 0;
        for (ACodeSparksThread thread : threadCluster)
        {
            double metricValue = thread.getNumericalMetricValue(metricIdentifier);
            if (metricValue > max)
            {
                max = metricValue;
            }
        }
        numericalMetricRatio = max;
    }

    private int calculateNumberOfFilteredThreads(CodeSparksThreadCluster cluster, boolean ignoreFilter)
    {

        int numberOfFilteredThreads = 0;
        for (ACodeSparksThread codeSparksThread : cluster)
        {
            if (!codeSparksThread.isFiltered() || ignoreFilter)
            {
                numberOfFilteredThreads++;
            }
        }

        return numberOfFilteredThreads;
    }

    double calculateFilteredThreadRatio(CodeSparksThreadCluster cluster, int numberOfArtifactThreads, boolean ignoreFilter)
    {
        return (double) calculateNumberOfFilteredThreads(cluster, ignoreFilter) / numberOfArtifactThreads;
    }

//    double calculateFilteredRuntimeRatio(CodeSparksThreadCluster cluster, boolean ignoreFilter)
//    {
//        double max = 0;
//        for (ACodeSparksThread aCluster : cluster)
//        {
//            if (aCluster.isFiltered() && !ignoreFilter)
//                continue;
//
//            double metricValue = aCluster.getMetricValue();
//            if (metricValue > max)
//            {
//                max = metricValue;
//            }
//        }
//        return max;
//    }

    double calculateAvgFilteredNumericalMetricRatio(CodeSparksThreadCluster cluster, final IMetricIdentifier metricIdentifier, boolean ignoreFilter)
    {
        double sum = 0;
        int threads = 0;
        for (ACodeSparksThread codeSparksThread : cluster)
        {
            if (codeSparksThread.isFiltered() && !ignoreFilter)
                continue;

            sum += codeSparksThread.getNumericalMetricValue(metricIdentifier);
            threads++;
        }
        numericalMetricRatio = sum / threads;
        return sum / threads;
    }

    double calculateFilteredSumNumericalMetricRatio(CodeSparksThreadCluster threadCluster, final IMetricIdentifier metricIdentifier, boolean ignoreFilter)
    {
        double sum = 0;
        for (ACodeSparksThread codeSparksThread : threadCluster)
        {
            if (codeSparksThread.isFiltered() && !ignoreFilter)
                continue;

            sum += codeSparksThread.getNumericalMetricValue(metricIdentifier);
        }
        numericalMetricRationSum = sum;
        return sum;
    }


    @SuppressWarnings("unused")
    private double calculateFilteredMedianNumericalMetricRatio(CodeSparksThreadCluster threadCluster, final IMetricIdentifier metricIdentifier,
                                                               boolean ignoreFilter)
    {
        int unfilteredThreads = 0;
        for (ACodeSparksThread codeSparksThread : threadCluster)
        {
            if (codeSparksThread.isFiltered() && !ignoreFilter)
                continue;

            unfilteredThreads++;
        }

        Double[] metricArray = new Double[threadCluster.size()];
        for (int i = 0; i < unfilteredThreads; i++)
        {
            ACodeSparksThread codeSparksThread = threadCluster.get(i);
            if (codeSparksThread.isFiltered() && ignoreFilter)
                continue;

            metricArray[i] = codeSparksThread.getNumericalMetricValue(metricIdentifier);
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

    public double getThreadRatio()
    {
        return threadRatio;
    }

    public double getNumericalMetricRatio()
    {
        return numericalMetricRatio;
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

    public void setThreadRatio(double threadRatio)
    {
        this.threadRatio = threadRatio;
    }

    public void setNumericalMetricRatio(double numericalMetricRatio)
    {
        this.numericalMetricRatio = numericalMetricRatio;
    }

    public double getCompleteFilteredNumericalMetricValue()
    {
        return completeFilteredNumericalMetricValue;
    }

    public void setCompleteFilteredNumericalMetricValue(double completeFilteredNumericalMetricValue)
    {
        this.completeFilteredNumericalMetricValue = completeFilteredNumericalMetricValue;
    }

    double getNumericalMetricRationSum()
    {
        return numericalMetricRationSum;
    }

    public void setNumericalMetricRationSum(double numericalMetricRationSum)
    {
        this.numericalMetricRationSum = numericalMetricRationSum;
    }
}
