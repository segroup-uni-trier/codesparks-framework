/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.thread.ThreadVisualizationUtil;

import java.awt.*;
import java.util.Set;

public class TotalNumberOfThreadsButtonFillStrategy implements IThreadClusterButtonFillStrategy
{
    private static IThreadClusterButtonFillStrategy instance;

    public static IThreadClusterButtonFillStrategy getInstance()
    {
        if (instance == null)
        {
            synchronized (TotalNumberOfThreadsButtonFillStrategy.class)
            {
                if (instance == null)
                {
                    instance = new TotalNumberOfThreadsButtonFillStrategy();
                }
            }
        }
        return instance;
    }

    private TotalNumberOfThreadsButtonFillStrategy() {}

    @Override
    public void fillThreadClusterButton(final ThreadClusterButton threadClusterButton, final Graphics g)
    {
        final IThreadSelectable threadSelectable = threadClusterButton.getThreadSelectable();
        if (threadSelectable != null)
        {
            final ThreadArtifactCluster cluster = threadClusterButton.getCluster();
            final Set<AThreadArtifact> selectedThreadArtifactsOfCluster = threadSelectable.getSelectedThreadArtifactsOfCluster(cluster);
            final Set<AThreadArtifact> selectedThreadArtifacts = threadSelectable.getSelectedThreadArtifacts();
            boolean createDisabledViz = threadClusterButton.getArtifact().getThreadArtifacts().stream().allMatch(AThreadArtifact::isFiltered);

            final long numberOfThreadsOfCluster =
                    selectedThreadArtifactsOfCluster.stream().filter(clusterThread -> (createDisabledViz || !clusterThread.isFiltered())).count();

            final double totalNumberOfFilteredThreads =
                    (double) selectedThreadArtifacts.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()))
                            .count();

            final double percent = numberOfThreadsOfCluster / totalNumberOfFilteredThreads;

            final Rectangle boundsRectangle = threadClusterButton.getBoundsRectangle();
            final int numberOfThreadsWidth = (int) (boundsRectangle.width * percent);//ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent,
            // boundsRectangle.width);

            final JBColor color = threadClusterButton.getColor();
            final Graphics2D graphics = (Graphics2D) g;
            graphics.setColor(color);
            graphics.fillRect(boundsRectangle.width - numberOfThreadsWidth, 0, numberOfThreadsWidth, boundsRectangle.height);

            // TODO: Number of types analogous to sum/avg approach, i.e. #threads low saturated and #types fully saturated because it always holds #threads
            //  >= #types

        }
    }
}
