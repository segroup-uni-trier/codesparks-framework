/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.DataUtil;
import de.unitrier.st.codesparks.core.visualization.BottomFlowLayout;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class ArtifactCalleeVisualizationWrapper extends AArtifactCalleeVisualization
{
    ArtifactCalleeVisualizationWrapper(AArtifact artifact,
                                       List<ANeighborArtifact> neighborArtifactsOfLine,
//                                       double totalMetricValue,
//                                       Color metricColor,
                                       AArtifactCalleeVisualizationLabelFactory... factories)
    {
        super(artifact);
        assert neighborArtifactsOfLine.size() > 0;


        final double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(artifact);
        final Color metricColor = VisualizationUtil.getPerformanceColor(threadFilteredMetricValue);

        psiElement = neighborArtifactsOfLine.get(0).getVisPsiElement();

        this.setLayout(new BottomFlowLayout());

        if (factories == null || factories.length == 0)
        {
            return;
        }

        int width = 0;
        int height = 0;

        Arrays.sort(factories, Comparator.comparingInt(AArtifactCalleeVisualizationLabelFactory::getSequence));

        for (AArtifactCalleeVisualizationLabelFactory factory : factories)
        {
            JLabel artifactCalleeLabel = factory.createArtifactCalleeLabel(artifact
                    , neighborArtifactsOfLine
                    , threadFilteredMetricValue
                    , metricColor
            );
            if (artifactCalleeLabel == null)
            {
                continue;
            }
            int iconWidth = artifactCalleeLabel.getIcon().getIconWidth();
            width += iconWidth;
            int iconHeight = artifactCalleeLabel.getIcon().getIconHeight();
            height = Math.max(height, iconHeight);
            this.add(artifactCalleeLabel);
        }

        setSize(width, height);
    }

//    private final Map<String, Double> methodRuntimes;
//    private final double totalMetricValue;
//
//    private DefaultArtifactCalleeVisualization(final ImageIcon imageIcon,
//                                               final Map<String, Double> methodRuntimes,
//                                               final double totalMetricValue,
//                                               final PsiElement psiElement)
//    {
//        super.init(imageIcon);
//        this.methodRuntimes = methodRuntimes;
//        this.totalMetricValue = totalMetricValue;
//        this.psiElement = psiElement;
//        this.addMouseListener(new ArtifactCalleeVisualizationMouseListener(this));
//    }
//
//    private Map<String, Double> getMethodRuntimes()
//    {
//        return methodRuntimes;
//    }
//
//    @Override
//    public void repaint()
//    {
//        // !!!! Preventing repaint from being called. Is necessary!
//    }
//
//
//
//    static Collection<DefaultArtifactCalleeVisualization> createArtifactCalleeVisualizations(@NotNull final
//                                                                                             AProfilingArtifact
//                                                                                                     profilingArtifact)
//    {
//        int lineHeight = VisConstants.getLineHeight();
//        final List<DefaultArtifactCalleeVisualization> calleeVisualizations = new ArrayList<>();
//
//        Set<Map.Entry<Integer, List<ANeighborProfilingArtifact>>> threadFilteredSuccessors = profilingArtifact.getSuccessors()
//                .entrySet()
//                .stream()
//                .filter(integerListEntry ->
//                        integerListEntry.getValue().stream().anyMatch(aNeighborProfilingArtifact ->
//                                aNeighborProfilingArtifact.getThreadArtifacts()
//                                        .stream()
//                                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))).collect(Collectors.toSet());
//
//        final double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(profilingArtifact);
//
//        final Color performanceColor = VisualizationUtil.getPerformanceColor(threadFilteredMetricValue);
//
//        for (Map.Entry<Integer, List<ANeighborProfilingArtifact>> entry : threadFilteredSuccessors/*successors.entrySet()*/)
//        {
//            List<ANeighborProfilingArtifact> threadFilteredCalleesOfCurrentLine = entry.getValue()
//                    .stream()
//                    .filter(aNeighborProfilingArtifact -> aNeighborProfilingArtifact.getThreadArtifacts()
//                            .stream().anyMatch(threadArtifact -> !threadArtifact.isFiltered())).collect(Collectors.toList());
//
//            int numberOfCalleesInSameLine = threadFilteredCalleesOfCurrentLine.size();
//            if (threadFilteredCalleesOfCurrentLine.stream().anyMatch(artifact -> "Selftime".equals(artifact.getName())))
//            {
//                numberOfCalleesInSameLine -= 1;
//            }
//            // Initial visualization area.
//            BufferedImage bi = UIUtil.createImage(CALLEE_X_OFFSET + INVOCATION_WIDTH + 7, lineHeight, BufferedImage.TYPE_INT_RGB);
//            Graphics graphics = bi.getGraphics();
//            graphics.setColor(WHITE);
//            graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
//
//            Map<String, Double> methodRuntimes = new HashMap<>();
//
//            final Color calleeBackgroundColor = VisualizationUtil.getBackgroundPerformanceColor(performanceColor, .25f);
//            int numberOfShadowCalleesToDraw = Math.min(numberOfCalleesInSameLine, 3);
//
//            // Draw the hints of multiple callees in a single line of code
//            for (int i = numberOfShadowCalleesToDraw - 1; i > 0; i--)
//            {
//                int xoffset = i * 2; // 4, 2, 0
//                int yoffset = 4 - i * 2; // 0, 2, 4
//                Rectangle rect = new Rectangle(CALLEE_X_OFFSET + xoffset, yoffset, INVOCATION_WIDTH, lineHeight - 6);
//                graphics.setColor(WHITE);
//                VisualizationUtil.fillRectangle(graphics, rect);
//                graphics.setColor(calleeBackgroundColor);
//                VisualizationUtil.fillRectangle(graphics, rect);
//                graphics.setColor(BORDER_COLOR);
//                VisualizationUtil.drawRectangle(graphics, rect);
//            }
//
//            PsiElement psiElement = null;
//
//            double calleeRuntimeSum = 0D;
//
//            for (ANeighborProfilingArtifact callee : threadFilteredCalleesOfCurrentLine)
//            {
//
//                double calleeRuntime = 0D;
//                Collection<ThreadArtifact> threadArtifacts = callee.getThreadArtifacts();
//                for (ThreadArtifact threadArtifact : threadArtifacts)
//                {
//                    if (!threadArtifact.isFiltered())
//                    {
//                        calleeRuntime += threadArtifact.getMetricValue() * callee.getMetricValue();
//                    }
//                }
//
//                calleeRuntimeSum += calleeRuntime;
//
//                // Draw the text.
//                methodRuntimes.put(callee.getName(), calleeRuntime);
//                if (psiElement == null)
//                    psiElement = callee.getInvocationLineElement();
//            }
//
//            // Draw background
//            Rectangle calleeVisualizationArea = new Rectangle(CALLEE_X_OFFSET, CALLEE_Y_OFFSET, INVOCATION_WIDTH, lineHeight - 6);
//            graphics.setColor(WHITE);
//            VisualizationUtil.fillRectangle(graphics, calleeVisualizationArea);
//            graphics.setColor(calleeBackgroundColor);
//            VisualizationUtil.fillRectangle(graphics, calleeVisualizationArea);
//
//            // Draw the bar
//            int barWidth = (int) Math.round(INVOCATION_WIDTH * calleeRuntimeSum / threadFilteredMetricValue);
//            Rectangle calleeRuntimeArea = new Rectangle(CALLEE_X_OFFSET, CALLEE_Y_OFFSET, barWidth, lineHeight - 6);
//            graphics.setColor(performanceColor);
//            VisualizationUtil.fillRectangle(graphics, calleeRuntimeArea);
//
//            // Draw the border.
//            graphics.setColor(BORDER_COLOR);
//            VisualizationUtil.drawRectangle(graphics, calleeVisualizationArea);
//
//            DefaultArtifactCalleeVisualization calleeVisualization = new DefaultArtifactCalleeVisualization(new ImageIcon(bi),
//                    methodRuntimes, threadFilteredMetricValue, psiElement);
//            calleeVisualizations.add(calleeVisualization);
//        }
//        return calleeVisualizations;
//
//    }
}
