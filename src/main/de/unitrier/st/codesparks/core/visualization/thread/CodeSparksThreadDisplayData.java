package de.unitrier.st.codesparks.core.visualization.thread;

public class CodeSparksThreadDisplayData
{
    private double metricValueSum;
    private double metricValueAvg;
    private int numberOfThreads;
    private int numberOfThreadTypes;

    double getMetricValueSum()
    {
        return metricValueSum;
    }

    void setMetricValueSum(double metricValueSum)
    {
        this.metricValueSum = metricValueSum;
    }

    public int getNumberOfThreads()
    {
        return numberOfThreads;
    }

    void setNumberOfThreads(int numberOfThreads)
    {
        this.numberOfThreads = numberOfThreads;
    }

    double getMetricValueAvg()
    {
        return metricValueAvg;
    }

    void setMetricValueAvg(double metricValueAvg)
    {
        this.metricValueAvg = metricValueAvg;
    }

    public int getNumberOfThreadTypes()
    {
        return numberOfThreadTypes;
    }

    public void setNumberOfThreadTypes(int numberOfThreadTypes)
    {
        this.numberOfThreadTypes = numberOfThreadTypes;
    }
}
