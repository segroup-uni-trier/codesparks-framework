/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.IThreadSelectableIndexProvider;
import de.unitrier.st.codesparks.core.visualization.thread.ThreadVisualizationUtil;

import java.awt.*;
import java.util.Set;

public class TotalNumberOfThreadsAndThreadTypesButtonFillStrategy implements IThreadClusterButtonFillStrategy
{
    private static IThreadClusterButtonFillStrategy instance;

    public static IThreadClusterButtonFillStrategy getInstance()
    {
        if (instance == null)
        {
            synchronized (TotalNumberOfThreadsAndThreadTypesButtonFillStrategy.class)
            {
                if (instance == null)
                {
                    instance = new TotalNumberOfThreadsAndThreadTypesButtonFillStrategy();
                }
            }
        }
        return instance;
    }

    private TotalNumberOfThreadsAndThreadTypesButtonFillStrategy() {}

    @Override
    public void fillThreadClusterButton(final ThreadClusterButton threadClusterButton, final ThreadArtifactClustering clustering, final Graphics g)
    {
        final IThreadSelectableIndexProvider selectableIndexProvider = threadClusterButton.getSelectableIndexProvider();
        final int index = selectableIndexProvider.getThreadSelectableIndex();
        if (index >= 0)
        {
            final IThreadSelectable threadSelectable = threadClusterButton.getThreadSelectables().get(index);
            final ThreadArtifactCluster cluster = threadClusterButton.getCluster();
            final boolean createDisabledViz = threadClusterButton.createDisabledViz();
            Set<AThreadArtifact> selectedThreadArtifacts;
            Set<AThreadArtifact> selectedThreadArtifactsOfCluster;
            if (createDisabledViz)
            { // In case to create a disabled viz, all thread artifacts are filtered!
                selectedThreadArtifacts = threadSelectable.getFilteredThreadArtifacts();
                selectedThreadArtifactsOfCluster = threadSelectable.getFilteredThreadArtifactsOfCluster(cluster);
            } else
            {
                selectedThreadArtifacts = threadSelectable.getSelectedThreadArtifacts();
                selectedThreadArtifactsOfCluster = threadSelectable.getSelectedThreadArtifactsOfCluster(cluster);
            }

            final int numberOfSelectedThreadsOfCluster = selectedThreadArtifactsOfCluster.size();

            final double totalNumberOfSelectedThreads = selectedThreadArtifacts.size();

            double percent = numberOfSelectedThreadsOfCluster / totalNumberOfSelectedThreads;

            final Rectangle boundsRectangle = threadClusterButton.getBoundsRectangle();
            final int numberOfThreadsWidth = (int) (boundsRectangle.width * percent);

            final JBColor color = threadClusterButton.getColor();
            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(color, .35f);

            final Graphics2D graphics = (Graphics2D) g;

            graphics.setColor(backgroundMetricColor);
            graphics.fillRect(boundsRectangle.width - numberOfThreadsWidth, 0, numberOfThreadsWidth, boundsRectangle.height);

            final AArtifact artifact = threadClusterButton.getArtifact();
            final AMetricIdentifier metricIdentifier = threadClusterButton.getMetricIdentifier();
            final int numberOfSelectedThreadTypesInCluster =
                    ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact,
                            metricIdentifier, selectedThreadArtifactsOfCluster, true);

            percent = numberOfSelectedThreadTypesInCluster / totalNumberOfSelectedThreads;

            final int numberOfThreadTypesWidth = (int) (boundsRectangle.width * percent);
            graphics.setColor(color);
            graphics.fillRect(boundsRectangle.width - numberOfThreadTypesWidth, 0, numberOfThreadTypesWidth, boundsRectangle.height);
        }
    }
}
