/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

public class ThreadArtifactDisplayData
{
    private double metricValueSum;
    private double metricValueAvg;
    private int numberOfThreads;
    private int numberOfThreadTypes;

    double getMetricValueSum()
    {
        return metricValueSum;
    }

    void setMetricValueSum(final double metricValueSum)
    {
        this.metricValueSum = metricValueSum;
    }

    public int getNumberOfThreads()
    {
        return numberOfThreads;
    }

    void setNumberOfThreads(final int numberOfThreads)
    {
        this.numberOfThreads = numberOfThreads;
    }

    double getMetricValueAvg()
    {
        return metricValueAvg;
    }

    void setMetricValueAvg(final double metricValueAvg)
    {
        this.metricValueAvg = metricValueAvg;
    }

    public int getNumberOfThreadTypes()
    {
        return numberOfThreadTypes;
    }

    public void setNumberOfThreadTypes(final int numberOfThreadTypes)
    {
        this.numberOfThreadTypes = numberOfThreadTypes;
    }
}
