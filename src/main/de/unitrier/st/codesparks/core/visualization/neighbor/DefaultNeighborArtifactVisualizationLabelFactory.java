/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.openapi.ui.popup.*;
import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.ui.JBColor.WHITE;

public class DefaultNeighborArtifactVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public DefaultNeighborArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public DefaultNeighborArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createNeighborArtifactLabel(
            final AArtifact artifact
            , List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
    {
        threadFilteredNeighborArtifactsOfLine =
                threadFilteredNeighborArtifactsOfLine
                        .stream()
                        .filter(neighbor -> neighbor.getNumericalMetricValue(primaryMetricIdentifier) > 0)
                        .collect(Collectors.toList());
        if (threadFilteredNeighborArtifactsOfLine.isEmpty())
        {
            return emptyLabel();
        }

        final double threadFilteredMetricValue = DataUtil.getThreadFilteredRelativeNumericMetricValueOf(artifact, primaryMetricIdentifier);
        final Color metricColor = VisualizationUtil.getMetricColor(threadFilteredMetricValue);

        int numberOfCalleesInSameLine = threadFilteredNeighborArtifactsOfLine.size();

        final long selftimeCnt = threadFilteredNeighborArtifactsOfLine
                .stream()
                .filter(npa -> npa.getName().toLowerCase().startsWith("self"))
                .count();

        numberOfCalleesInSameLine -= selftimeCnt;

        // Initial visualization area.
        final int visualizationArea = VisConstants.CALLEE_X_OFFSET + VisConstants.INVOCATION_WIDTH + 7;
        final int lineHeight = VisConstants.getLineHeight();

        final CodeSparksGraphics graphics = getGraphics(visualizationArea, lineHeight);

        final Color neighborBackgroundColor = VisualizationUtil.getBackgroundMetricColor(metricColor, .25f);
        final int numberOfShadowCalleesToDraw = Math.min(numberOfCalleesInSameLine, 3);

        // Draw the hints of multiple callees in a single line of code
        for (int i = numberOfShadowCalleesToDraw - 1; i > 0; i--)
        {
            final int xOffset = i * 2; // 4, 2, 0
            final int yOffset = 4 - i * 2; // 0, 2, 4

            graphics.setColor(VisConstants.BORDER_COLOR);
            graphics.drawLine(
                    VisConstants.CALLEE_X_OFFSET + xOffset
                    , yOffset
                    , VisConstants.CALLEE_X_OFFSET + xOffset + VisConstants.INVOCATION_WIDTH
                    , yOffset);
            graphics.drawLine(
                    VisConstants.CALLEE_X_OFFSET + xOffset + VisConstants.INVOCATION_WIDTH
                    , yOffset
                    , VisConstants.CALLEE_X_OFFSET + xOffset + VisConstants.INVOCATION_WIDTH
                    , yOffset + lineHeight - 6);
        }

        PsiElement psiElement = null;
        double calleeRuntimeSum = 0D;

        final Map<String, Double> methodRuntimes = new HashMap<>();

        for (final ANeighborArtifact neighborArtifact : threadFilteredNeighborArtifactsOfLine)
        {
            double neighborRuntime = 0D;
            final Collection<AThreadArtifact> threadArtifacts = neighborArtifact.getThreadArtifacts();
            for (final AThreadArtifact threadArtifact : threadArtifacts)
            {
                if (!threadArtifact.isFiltered())
                {
                    neighborRuntime += threadArtifact.getNumericalMetricValue(primaryMetricIdentifier)
                            * neighborArtifact.getNumericalMetricValue(primaryMetricIdentifier);
                }
            }

            calleeRuntimeSum += neighborRuntime;

            // Draw the text.
            methodRuntimes.put(neighborArtifact.getName(), neighborRuntime);
            if (psiElement == null)
            {
                psiElement = neighborArtifact.getVisPsiElement();//neighborArtifact.getInvocationLineElement();
            }
        }

        // The background drawing area
        final Rectangle calleeVisualizationArea = new Rectangle(
                VisConstants.CALLEE_X_OFFSET
                , VisConstants.CALLEE_Y_OFFSET
                , VisConstants.INVOCATION_WIDTH
                , lineHeight - 6);
        // Draw the background
        graphics.fillRectangle(calleeVisualizationArea, WHITE); // Will prevent the background from summing up its color intensity on repainting
        graphics.fillRectangle(calleeVisualizationArea, neighborBackgroundColor);
        // Draw the metric bar
        final int barWidth = (int) Math.round(VisConstants.INVOCATION_WIDTH * calleeRuntimeSum / threadFilteredMetricValue);
        final Rectangle calleeRuntimeArea = new Rectangle(VisConstants.CALLEE_X_OFFSET, VisConstants.CALLEE_Y_OFFSET, barWidth, lineHeight - 6);
        graphics.fillRectangle(calleeRuntimeArea, metricColor);
        // Draw the border.
        graphics.drawRectangle(calleeVisualizationArea, VisConstants.BORDER_COLOR);

        // Get the label
        final JLabel jLabel = makeLabel(graphics);
        jLabel.addMouseListener(new DefaultArtifactCalleeVisualizationMouseListener(methodRuntimes, threadFilteredMetricValue, jLabel));
        return jLabel;
    }

    private static final class DefaultArtifactCalleeVisualizationMouseListener extends MouseAdapter
    {
        private final JLabel visualizationLabel;
        private JBPopup popup = null;
        private final Map<String, Double> methodRuntimes;
        private final double totalMetricValue;

        DefaultArtifactCalleeVisualizationMouseListener(final Map<String, Double> methodRuntimes,
                                                        final double totalMetricValue,
                                                        final JLabel visualizationLabel
        )
        {
            this.methodRuntimes = methodRuntimes;
            this.totalMetricValue = totalMetricValue;
            this.visualizationLabel = visualizationLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            // User Activity Logging
            final StringBuilder strb = new StringBuilder();
            for (final Map.Entry<String, Double> stringDoubleEntry : methodRuntimes.entrySet())
            {
                strb.append(stringDoubleEntry.getKey());
                strb.append("=");
                strb.append(stringDoubleEntry.getValue());
                strb.append(",");
            }
            strb.deleteCharAt(strb.length() - 1);
            UserActivityLogger.getInstance().log(UserActivityEnum.CalleeTooltipClicked, strb.toString());

            final JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            double calleeMetricSum = 0D;
            final Graphics g = visualizationLabel.getGraphics();

            final LinkedHashMap<String, Double> collect = methodRuntimes.entrySet().stream()
                    .sorted((o1, o2) -> -1 * Double.compare(o1.getValue(), o2.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o, LinkedHashMap::new));

            final int lineHeight = VisConstants.getLineHeight();
            for (final Map.Entry<String, Double> entry : collect.entrySet())
            {
                final JTextArea jTextArea = new JTextArea();
                jTextArea.setEditable(false);
                final String calleeName = entry.getKey(); // JavaUtil.checkAndReplaceConstructorName(calleeName)
                jTextArea.setText(CoreUtil.formatPercentageWithLeadingWhitespace(entry.getValue() / totalMetricValue) + " " + calleeName);
                jTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final double finalCalleeMetricSum = calleeMetricSum;
                final MouseAdapter mouseAdapter = new MouseAdapter()
                {
                    private Font defaultFont;
                    private Font underlinedFont;

                    @Override
                    public void mouseEntered(MouseEvent e)
                    {
                        if (defaultFont == null || underlinedFont == null)
                        {
                            defaultFont = jTextArea.getFont();
                            final Map<TextAttribute, Object> map = new HashMap<>();
                            map.put(TextAttribute.FONT, defaultFont);
                            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                            underlinedFont = Font.getFont(map);
                        }
                        jTextArea.setFont(underlinedFont);

                        final int xOffset = (int) (VisConstants.INVOCATION_WIDTH * finalCalleeMetricSum / totalMetricValue);
                        final Graphics graphics = visualizationLabel.getGraphics();
                        graphics.setColor(VisConstants.ORANGE);
                        final int width = (int) (VisConstants.INVOCATION_WIDTH * entry.getValue() / totalMetricValue);
                        graphics.drawRect(VisConstants.CALLEE_X_OFFSET + xOffset, VisConstants.CALLEE_Y_OFFSET, width, lineHeight - 6);
                        graphics.drawRect(VisConstants.CALLEE_X_OFFSET + xOffset + 1, VisConstants.CALLEE_Y_OFFSET + 1, width - 2, lineHeight - 8);
                    }

                    @Override
                    public void mouseExited(MouseEvent e)
                    {
                        visualizationLabel.paint(g); // Do NOT replace with repaint as it will flicker
                        jTextArea.setFont(defaultFont);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        final String artifactName = jTextArea.getText();
                        if (artifactName == null || artifactName.isEmpty()) return;
                        if (artifactName.contains("selftime"))
                        {
                            return;
                        }
                        final int indexOfPercent = artifactName.indexOf("%");
                        final String identifier = artifactName.substring(indexOfPercent + 1).trim();
                        CoreUtil.navigate(identifier);
                        UserActivityLogger.getInstance().log(UserActivityEnum.CalleeTooltipNavigated, identifier);
                        if (popup != null)
                        {
                            popup.cancel();
                        }
                    }
                };
                jTextArea.addMouseListener(mouseAdapter);
                panel.add(jTextArea);
                calleeMetricSum += entry.getValue();
            }

            final ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance().
                    createComponentPopupBuilder(panel, null).setShowShadow(true);
            popup = componentPopupBuilder.createPopup();
            popup.pack(false, true);
            popup.canClose();
            popup.moveToFitScreen();
            popup.showUnderneathOf((Component) e.getSource());
            popup.addListener(new JBPopupListener()
            {
                @Override
                public void onClosed(@NotNull LightweightWindowEvent event)
                {
                    visualizationLabel.paint(g);
                }
            });
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            VisualizationUtil.setCursorRecursively(visualizationLabel, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            VisualizationUtil.setCursorRecursively(visualizationLabel, Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }
    }
}