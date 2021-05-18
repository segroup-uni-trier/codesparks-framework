/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.ThreadVisualizationUtil;

import java.awt.*;
import java.util.List;
import java.util.Optional;
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

            final AMetricIdentifier metricIdentifier = threadClusterButton.getMetricIdentifier();

            //final double threadFilteredTotalMetricValueOfArtifact = artifact.getThreadFilteredTotalNumericalMetricValue(metricIdentifier, createDisabledViz);

            final double totalOfSelected =
                    selectedThreadArtifacts.stream().filter(threadExecutingArtifact -> createDisabledViz || !threadExecutingArtifact.isFiltered())
                            .mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier)).sum();

            /*
             * An attempt to interpret the full rectangle/bar width not as 100% runtime but as the max runtime sum of the clusters.
             */
//            double maxClusterSum = Double.MIN_VALUE;
//            for (final ThreadArtifactCluster threadArtifactCluster : clustering)
//            {
//                final double sumOfCluster =
//                        threadArtifactCluster.stream().filter(threadExecutingArtifact -> createDisabledViz || !threadExecutingArtifact.isFiltered())
//                                .mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier)).sum();
//                maxClusterSum = Math.max(maxClusterSum, sumOfCluster);
//            }
//            final double totalOfSelected = maxClusterSum;


            double percent = ThreadVisualizationUtil.getMetricValueSumOfSelectedThreadsOfTheClusterRelativeToTotal(
                    metricIdentifier
                    , selectedThreadArtifacts
                    , selectedThreadArtifactsOfCluster
                    , totalOfSelected
                    , createDisabledViz
            );

            final Rectangle boundsRectangle = threadClusterButton.getBoundsRectangle();

            // This represents linear scaling of the bar width
            final int sumWidth = (int) (boundsRectangle.width * percent);//ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, boundsRectangle
            // .width);


            //double linearEulerInterpolationOfPercent = MathUtil.linearInterpolation(1, Math.exp(1D), 0, 1, percent);
            //double lnScaledPercent = Math.log(linearEulerInterpolationOfPercent);
            //final int lnScaledSumWidth = (int) (boundsRectangle.width * lnScaledPercent);

//            final int sqrtScaledSumWidth = (int) (boundsRectangle.width * Math.sqrt(percent));

            final JBColor color = threadClusterButton.getColor();
            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(color, .35f);
            final Graphics2D graphics = (Graphics2D) g;
            graphics.setColor(backgroundMetricColor);
            graphics.fillRect(0, 0, sumWidth, boundsRectangle.height);
//            graphics.fillRect(0, 0, lnScaledSumWidth, boundsRectangle.height);
//            graphics.fillRect(0, 0, sqrtScaledSumWidth, boundsRectangle.height);

            percent = ThreadVisualizationUtil.getMetricValueAverageOfSelectedThreadsOfTheClusterRelativeToTotal(
                    metricIdentifier
                    , selectedThreadArtifacts
                    , selectedThreadArtifactsOfCluster
                    , totalOfSelected
                    , createDisabledViz
            );

            final int avgWidth = (int) (boundsRectangle.width * percent);//ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, boundsRectangle
            // .width);

//            linearEulerInterpolationOfPercent = MathUtil.linearInterpolation(1, Math.exp(1D), 0, 1, percent);
//            lnScaledPercent = Math.log(linearEulerInterpolationOfPercent);
//            final int lnScaledAvgWidth = (int) (boundsRectangle.width * lnScaledPercent);

//            final int sqrtScaledAvgWidth = (int) (boundsRectangle.width * Math.sqrt(percent));


            graphics.setColor(color);
            graphics.fillRect(0, 0, avgWidth, boundsRectangle.height);
//            graphics.fillRect(0, 0, lnScaledAvgWidth, boundsRectangle.height);
//            graphics.fillRect(0, 0, sqrtScaledAvgWidth, boundsRectangle.height);
        }
    }
}
