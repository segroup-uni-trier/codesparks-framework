package de.unitrier.st.insituprofiling.core.visualization.thread;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;

import java.util.Set;

public class DefaultRadialThreadArtifactVisualizationDisplayData implements IRadialThreadArtifactVisualizationDisplayData
{
    @Override
    public ThreadArtifactDisplayData getSelectedThreadData(AProfilingArtifact artifact, Set<ThreadArtifact> selectedThreadArtifacts)
    {
        final ThreadArtifactDisplayData threadArtifactDisplayData = new ThreadArtifactDisplayData();

        double sum = 0;
        for (ThreadArtifact selectedThreadArtifact : selectedThreadArtifacts)
        {
            sum += selectedThreadArtifact.getMetricValue();
        }

        threadArtifactDisplayData.setMetricValueSum(sum);
        threadArtifactDisplayData.setMetricValueAvg(sum / selectedThreadArtifacts.size());
        threadArtifactDisplayData.setNumberOfThreads(selectedThreadArtifacts.size());
        threadArtifactDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact,
                selectedThreadArtifacts));

        return threadArtifactDisplayData;
    }

    @Override
    public ThreadArtifactDisplayData getHoveredThreadData(AProfilingArtifact artifact, Set<ThreadArtifact> hoveredThreadArtifacts)
    {
        final ThreadArtifactDisplayData threadArtifactDisplayData = new ThreadArtifactDisplayData();

        double sum = 0;
        for (ThreadArtifact selectedThreadArtifact : hoveredThreadArtifacts)
        {
            sum += selectedThreadArtifact.getMetricValue();
        }

        threadArtifactDisplayData.setMetricValueSum(sum);
        threadArtifactDisplayData.setMetricValueAvg(sum / hoveredThreadArtifacts.size());
        threadArtifactDisplayData.setNumberOfThreads(hoveredThreadArtifacts.size());
        threadArtifactDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact,
                hoveredThreadArtifacts));

        return threadArtifactDisplayData;
    }
}