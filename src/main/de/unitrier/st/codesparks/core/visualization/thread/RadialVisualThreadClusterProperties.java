package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.CodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

import java.util.Arrays;

public class RadialVisualThreadClusterProperties extends VisualThreadClusterProperties
{

    private int numberOfThreads;
    private double threadRatio;
    private double runtimeRatio;
    private double runtimeRationSum;
    private double arcStartAngle;
    private double arcAngle;
    private boolean isSelected;
    private double completeFilteredRuntime;

    public RadialVisualThreadClusterProperties(CodeSparksThreadCluster cluster, JBColor color, int numberOfArtifactThreads) {
        super(cluster, color);
        calculateNumberOfThreads(cluster);
        calculateThreadRatio(cluster, numberOfArtifactThreads);
        calculateMaxRuntimeRatio(cluster);
    }

    private void calculateNumberOfThreads(CodeSparksThreadCluster cluster) {
        numberOfThreads = cluster.size();
    }

    private void calculateThreadRatio(CodeSparksThreadCluster cluster, int numberOfArtifactThreads) {
        threadRatio = (double) cluster.size() / numberOfArtifactThreads;
    }

    private void calculateMaxRuntimeRatio(CodeSparksThreadCluster cluster) {
        double max = 0;
        for (CodeSparksThread aCluster : cluster) {
            double metricValue = aCluster.getMetricValue();
            if (metricValue > max) {
                max = metricValue;
            }
        }
        runtimeRatio = max;
    }

    private int calculateNumberOfFilteredThreads(CodeSparksThreadCluster cluster, boolean ignoreFilter) {

        int numberOfFilteredThreads = 0;
        for (CodeSparksThread codeSparksThread : cluster) {
            if (!codeSparksThread.isFiltered() || ignoreFilter) {
                numberOfFilteredThreads++;
            }
        }

        return numberOfFilteredThreads;
    }

    double calculateFilteredThreadRatio(CodeSparksThreadCluster cluster, int numberOfArtifactThreads, boolean ignoreFilter) {
        return (double) calculateNumberOfFilteredThreads(cluster, ignoreFilter) / numberOfArtifactThreads;
    }

    double calculateFilteredRuntimeRatio(CodeSparksThreadCluster cluster, boolean ignoreFilter) {
        double max = 0;
        for (CodeSparksThread aCluster : cluster) {
            if (aCluster.isFiltered() && !ignoreFilter)
                continue;

            double metricValue = aCluster.getMetricValue();
            if (metricValue > max) {
                max = metricValue;
            }
        }
        return max;
    }

    double calculateAvgFilteredRuntimeRatio(CodeSparksThreadCluster cluster, boolean ignoreFilter) {
        double sum = 0;
        int threads = 0;
        for (CodeSparksThread aCluster : cluster) {
            if (aCluster.isFiltered() && !ignoreFilter)
                continue;

            sum += aCluster.getMetricValue();
            threads++;
        }
        runtimeRatio = sum/threads;
        return sum/threads;
    }

    double calculateFilteredSumRuntimeRatio(CodeSparksThreadCluster cluster, boolean ignoreFilter) {
        double sum = 0;
        for (CodeSparksThread aCluster : cluster) {
            if (aCluster.isFiltered() && !ignoreFilter)
                continue;

            sum += aCluster.getMetricValue();
        }
        runtimeRationSum = sum;
        return sum;
    }



    @SuppressWarnings("unused")
    private double calculateFilteredMedianRuntimeRatio(CodeSparksThreadCluster cluster, boolean ignoreFilter) {
        int unfilteredThreads = 0;
        for (CodeSparksThread codeSparksThread : cluster) {
            if (codeSparksThread.isFiltered() && !ignoreFilter)
                continue;

            unfilteredThreads++;
        }

        Double[] metricArray = new Double[cluster.size()];
        for (int i = 0; i < unfilteredThreads; i++) {
            if (cluster.get(i).isFiltered() && ignoreFilter)
                continue;

            metricArray[i] = cluster.get(i).getMetricValue();
        }

        Arrays.sort(metricArray);
        double median;
        if (metricArray.length % 2 == 0)
            median = (metricArray[metricArray.length/2] + metricArray[metricArray.length/2 - 1])/2;
        else
            median = metricArray[metricArray.length/2];

        return median;
    }



    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public double getThreadRatio() {
        return threadRatio;
    }

    public double getRuntimeRatio() {
        return runtimeRatio;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    double getArcStartAngle() {
        return arcStartAngle;
    }

    double getArcAngle() {
        return arcAngle;
    }

    public void setArcStartAngle(double arcStartAngle) {
        this.arcStartAngle = arcStartAngle;
    }

    public void setArcAngle(double arcAngle) {
        this.arcAngle = arcAngle;
    }

    public void setThreadRatio(double threadRatio) {
        this.threadRatio = threadRatio;
    }

    public void setRuntimeRatio(double runtimeRatio) {
        this.runtimeRatio = runtimeRatio;
    }

    public double getCompleteFilteredRuntime() {
        return completeFilteredRuntime;
    }

    public void setCompleteFilteredRuntime(double completeFilteredRuntime) {
        this.completeFilteredRuntime = completeFilteredRuntime;
    }

    double getRuntimeRationSum() {
        return runtimeRationSum;
    }

    public void setRuntimeRationSum(double runtimeRationSum) {
        this.runtimeRationSum = runtimeRationSum;
    }
}
