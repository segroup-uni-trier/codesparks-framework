/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.ThreadVisualizationUtil;

import java.awt.*;
import java.util.Set;

public class SumAvgClusterButtonFillStrategy implements IThreadClusterButtonFillStrategy
{
    private static IThreadClusterButtonFillStrategy instance;

    public static IThreadClusterButtonFillStrategy getInstance()
    {
        if (instance == null)
        {
            synchronized (SumAvgClusterButtonFillStrategy.class)
            {
                if (instance == null)
                {
                    instance = new SumAvgClusterButtonFillStrategy();
                }
            }
        }
        return instance;
    }

    private SumAvgClusterButtonFillStrategy() {}

    @Override
    public void fillThreadClusterButton(final ThreadClusterButton threadClusterButton, final Graphics g)
    {
        final Graphics2D graphics = (Graphics2D) g;

        final IThreadSelectable threadSelectable = threadClusterButton.getThreadSelectable();
        if (threadSelectable != null)
        {
            final ThreadArtifactCluster cluster = threadClusterButton.getCluster();
            final Set<AThreadArtifact> selectedThreadArtifactsOfCluster = threadSelectable.getSelectedThreadArtifactsOfCluster(cluster);
            final Set<AThreadArtifact> selectedThreadArtifacts = threadSelectable.getSelectedThreadArtifacts();
            final AArtifact artifact = threadClusterButton.getArtifact();
            boolean createDisabledViz = artifact.getThreadArtifacts().stream().allMatch(AThreadArtifact::isFiltered);

            final AMetricIdentifier metricIdentifier = threadClusterButton.getMetricIdentifier();

            //final double threadFilteredTotalMetricValueOfArtifact = artifact.getThreadFilteredTotalNumericalMetricValue(metricIdentifier, createDisabledViz);

            final double totalOfSelected =
                    selectedThreadArtifacts.stream().filter(threadExecutingArtifact -> createDisabledViz || !threadExecutingArtifact.isFiltered())
                            .mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier)).sum();

            double percent = ThreadVisualizationUtil.getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(
                    metricIdentifier
                    , selectedThreadArtifacts
                    , selectedThreadArtifactsOfCluster
                    , totalOfSelected
                    , createDisabledViz
            );

            final Rectangle boundsRectangle = threadClusterButton.getBoundsRectangle();

            final int sumWidth = (int) (boundsRectangle.width * percent);//ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, boundsRectangle
            // .width);

            final JBColor color = threadClusterButton.getColor();

            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(color, .35f);
            graphics.setColor(backgroundMetricColor);
            graphics.fillRect(0, 0, sumWidth, boundsRectangle.height);

            percent = ThreadVisualizationUtil.getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(
                    metricIdentifier
                    , selectedThreadArtifacts
                    , selectedThreadArtifactsOfCluster
                    , totalOfSelected
                    , createDisabledViz
            );

            final int avgWidth = (int) (boundsRectangle.width * percent);//ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, boundsRectangle
            // .width);

            graphics.setColor(color);
            graphics.fillRect(0, 0, avgWidth, boundsRectangle.height);
        }
    }
}
