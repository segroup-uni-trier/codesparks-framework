/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.ThreadVisualizationUtil;

import java.awt.*;
import java.util.List;
import java.util.Optional;
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
        final List<IThreadSelectable> threadSelectables = threadClusterButton.getThreadSelectables();
        final Optional<IThreadSelectable> any = threadSelectables.stream().findAny();
        if (any.isPresent())
        {
            final IThreadSelectable threadSelectable = any.get();
            final ThreadArtifactCluster cluster = threadClusterButton.getCluster();
            final Set<AThreadArtifact> selectedThreadArtifactsOfCluster = threadSelectable.getSelectedThreadArtifactsOfCluster(cluster);
            final Set<AThreadArtifact> selectedThreadArtifacts = threadSelectable.getSelectedThreadArtifacts();
            final AArtifact artifact = threadClusterButton.getArtifact();
            boolean createDisabledViz = artifact.getThreadArtifacts().stream().allMatch(AThreadArtifact::isFiltered);

            final long numberOfFilteredThreadsOfCluster =
                    selectedThreadArtifactsOfCluster.stream().filter(clusterThread -> (createDisabledViz || !clusterThread.isFiltered())).count();

            final double totalNumberOfFilteredThreads =
                    (double) selectedThreadArtifacts.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()))
                            .count();

            double percent = numberOfFilteredThreadsOfCluster / totalNumberOfFilteredThreads;

            final Rectangle boundsRectangle = threadClusterButton.getBoundsRectangle();
            final int numberOfThreadsWidth = (int) (boundsRectangle.width * percent);//ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent,
            // boundsRectangle.width);

            final JBColor color = threadClusterButton.getColor();
            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(color, .35f);

            final Graphics2D graphics = (Graphics2D) g;

            graphics.setColor(backgroundMetricColor);
            graphics.fillRect(boundsRectangle.width - numberOfThreadsWidth, 0, numberOfThreadsWidth, boundsRectangle.height);

            // TODO: Number of types analogous to sum/avg approach, i.e. #threads low saturated and #types fully saturated because it always holds #threads
            //  >= #types


            final int numberOfFilteredThreadTypesInCluster = ThreadVisualizationUtil.getNumberOfFilteredThreadTypesInSelection(artifact,
                    selectedThreadArtifactsOfCluster);
            //getNumberOfFilteredThreadTypesOfCluster(artifact, cluster);
            percent = numberOfFilteredThreadTypesInCluster / totalNumberOfFilteredThreads;

            final int numberOfThreadTypesWidth = (int) (boundsRectangle.width * percent);
            graphics.setColor(color);
            graphics.fillRect(boundsRectangle.width - numberOfThreadTypesWidth, 0, numberOfThreadTypesWidth, boundsRectangle.height);
        }
    }
}
