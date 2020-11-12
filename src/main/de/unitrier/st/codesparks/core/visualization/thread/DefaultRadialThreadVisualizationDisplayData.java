package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;

import java.util.Set;

public class DefaultRadialThreadVisualizationDisplayData implements IRadialThreadVisualizationDisplayData
{
    @Override
    public CodeSparksThreadDisplayData getSelectedThreadData(AArtifact artifact, Set<ACodeSparksThread> selectedCodeSparksThreads)
    {
        final CodeSparksThreadDisplayData codeSparksThreadDisplayData = new CodeSparksThreadDisplayData();

        double sum = 0;
        for (ACodeSparksThread selectedCodeSparksThread : selectedCodeSparksThreads)
        {
            sum += selectedCodeSparksThread.getMetricValue();
        }

        codeSparksThreadDisplayData.setMetricValueSum(sum);
        codeSparksThreadDisplayData.setMetricValueAvg(sum / selectedCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreads(selectedCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact,
                selectedCodeSparksThreads));

        return codeSparksThreadDisplayData;
    }

    @Override
    public CodeSparksThreadDisplayData getHoveredThreadData(AArtifact artifact, Set<ACodeSparksThread> hoveredCodeSparksThreads)
    {
        final CodeSparksThreadDisplayData codeSparksThreadDisplayData = new CodeSparksThreadDisplayData();

        double sum = 0;
        for (ACodeSparksThread selectedCodeSparksThread : hoveredCodeSparksThreads)
        {
            sum += selectedCodeSparksThread.getMetricValue();
        }

        codeSparksThreadDisplayData.setMetricValueSum(sum);
        codeSparksThreadDisplayData.setMetricValueAvg(sum / hoveredCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreads(hoveredCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact,
                hoveredCodeSparksThreads));

        return codeSparksThreadDisplayData;
    }
}