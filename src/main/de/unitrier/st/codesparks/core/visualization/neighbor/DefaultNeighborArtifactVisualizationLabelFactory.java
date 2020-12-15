package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.openapi.ui.popup.*;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.CoreUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.ui.JBColor.WHITE;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultNeighborArtifactVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    public DefaultNeighborArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public DefaultNeighborArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createNeighborArtifactLabel(
            final AArtifact artifact
            , final List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
    {
        final double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(artifact, primaryMetricIdentifier);
        final Color metricColor = VisualizationUtil.getMetricColor(threadFilteredMetricValue);

        int numberOfCalleesInSameLine = threadFilteredNeighborArtifactsOfLine.size();

        long selftimeCnt = threadFilteredNeighborArtifactsOfLine
                .stream()
                .filter(npa -> npa.getName().toLowerCase().startsWith("self"))
                .count();

        numberOfCalleesInSameLine -= selftimeCnt;

        int lineHeight = VisConstants.getLineHeight();
        // Initial visualization area.

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, VisConstants.CALLEE_X_OFFSET + VisConstants.INVOCATION_WIDTH + 7, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        Graphics2D graphics = (Graphics2D) bi.getGraphics();

        VisualizationUtil.drawTransparentBackground(graphics, bi);

        final Color neighborBackgroundColor = VisualizationUtil.getBackgroundMetricColor(metricColor, .25f);
        int numberOfShadowCalleesToDraw = Math.min(numberOfCalleesInSameLine, 3);

        // Draw the hints of multiple callees in a single line of code
        for (int i = numberOfShadowCalleesToDraw - 1; i > 0; i--)
        {
            int xOffset = i * 2; // 4, 2, 0
            int yOffset = 4 - i * 2; // 0, 2, 4
            Rectangle rect = new Rectangle(VisConstants.CALLEE_X_OFFSET + xOffset, yOffset, VisConstants.INVOCATION_WIDTH, lineHeight - 6);
            graphics.setColor(WHITE);
            VisualizationUtil.fillRectangle(graphics, rect);
            graphics.setColor(neighborBackgroundColor);
            VisualizationUtil.fillRectangle(graphics, rect);
            graphics.setColor(VisConstants.BORDER_COLOR);
            VisualizationUtil.drawRectangle(graphics, rect);
        }

        PsiElement psiElement = null;

        double calleeRuntimeSum = 0D;

        Map<String, Double> methodRuntimes = new HashMap<>();

        for (ANeighborArtifact neighborArtifact : threadFilteredNeighborArtifactsOfLine)
        {

            double neighborRuntime = 0D;
            Collection<AThreadArtifact> codeSparksThreads = neighborArtifact.getThreadArtifacts();
            for (AThreadArtifact codeSparksThread : codeSparksThreads)
            {
                if (!codeSparksThread.isFiltered())
                {
                    neighborRuntime += codeSparksThread.getNumericalMetricValue(primaryMetricIdentifier) * neighborArtifact.getNumericalMetricValue(primaryMetricIdentifier);
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

        // Draw background
        Rectangle calleeVisualizationArea = new Rectangle(VisConstants.CALLEE_X_OFFSET, VisConstants.CALLEE_Y_OFFSET, VisConstants.INVOCATION_WIDTH,
                lineHeight - 6);
        graphics.setColor(WHITE);
        VisualizationUtil.fillRectangle(graphics, calleeVisualizationArea);
        graphics.setColor(neighborBackgroundColor);
        VisualizationUtil.fillRectangle(graphics, calleeVisualizationArea);

        // Draw the bar
        int barWidth = (int) Math.round(VisConstants.INVOCATION_WIDTH * calleeRuntimeSum / threadFilteredMetricValue);
        Rectangle calleeRuntimeArea = new Rectangle(VisConstants.CALLEE_X_OFFSET, VisConstants.CALLEE_Y_OFFSET, barWidth, lineHeight - 6);
        graphics.setColor(metricColor);
        VisualizationUtil.fillRectangle(graphics, calleeRuntimeArea);

        // Draw the border.
        graphics.setColor(VisConstants.BORDER_COLOR);
        VisualizationUtil.drawRectangle(graphics, calleeVisualizationArea);
        BufferedImage subimage = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        jLabel.addMouseListener(new DefaultArtifactCalleeVisualizationMouseListener(methodRuntimes, threadFilteredMetricValue, jLabel));

        return jLabel;
    }

    private static final class DefaultArtifactCalleeVisualizationMouseListener extends MouseAdapter
    {
        private final JLabel visualizationLabel;
        private JBPopup popup = null;
        private final Map<String, Double> methodRuntimes;
        private final double totalMetricValue;

        DefaultArtifactCalleeVisualizationMouseListener(Map<String, Double> methodRuntimes,
                                                        double totalMetricValue, JLabel visualizationLabel)
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
            for (Map.Entry<String, Double> stringDoubleEntry : methodRuntimes.entrySet())
            {
                strb.append(stringDoubleEntry.getKey());
                strb.append("=");
                strb.append(stringDoubleEntry.getValue());
                strb.append(",");
            }
            strb.deleteCharAt(strb.length() - 1);
            UserActivityLogger.getInstance().log(UserActivityEnum.CalleeTooltipClicked, strb.toString());


            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            double calleeMetricSum = 0D;
            Graphics g = visualizationLabel.getGraphics();
            //Map<String, Double> methodRuntimes = calleeVisualization.getMethodRuntimes();

            LinkedHashMap<String, Double> collect = methodRuntimes.entrySet().stream()
                    .sorted((o1, o2) -> -1 * Double.compare(o1.getValue(), o2.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o, LinkedHashMap::new));

            int lineHeight = VisConstants.getLineHeight();
            for (Map.Entry<String, Double> entry : collect.entrySet())
            {
                final JTextArea jTextArea = new JTextArea();
                jTextArea.setEditable(false);
                String calleeName = entry.getKey(); // JavaUtil.checkAndReplaceConstructorName(calleeName)
                jTextArea.setText(CoreUtil.formatPercentageWithLeadingWhitespace(entry.getValue() / totalMetricValue) + " " + calleeName);
                jTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                double finalCalleeMetricSum = calleeMetricSum;
                MouseAdapter mouseAdapter = new MouseAdapter()
                {
                    private Font defaultFont;
                    private Font underlinedFont;

                    @Override
                    public void mouseEntered(MouseEvent e)
                    {
                        if (defaultFont == null || underlinedFont == null)
                        {
                            defaultFont = jTextArea.getFont();
                            Map<TextAttribute, Object> map = new HashMap<>();
                            map.put(TextAttribute.FONT, defaultFont);
                            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                            underlinedFont = Font.getFont(map);
                        }
                        jTextArea.setFont(underlinedFont);

                        int xoffset = (int) (VisConstants.INVOCATION_WIDTH * finalCalleeMetricSum / totalMetricValue);
                        Graphics graphics = visualizationLabel.getGraphics();
                        graphics.setColor(new JBColor(new Color(203, 119, 48), new Color(203, 119, 48)));
                        int width = (int) (VisConstants.INVOCATION_WIDTH * entry.getValue() / totalMetricValue);
                        graphics.drawRect(VisConstants.CALLEE_X_OFFSET + xoffset, VisConstants.CALLEE_Y_OFFSET, width, lineHeight - 6);
                        graphics.drawRect(VisConstants.CALLEE_X_OFFSET + xoffset + 1, VisConstants.CALLEE_Y_OFFSET + 1, width - 2, lineHeight - 8);
                    }

                    @Override
                    public void mouseExited(MouseEvent e)
                    {
                        visualizationLabel.paint(g);
                        jTextArea.setFont(defaultFont);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        String artifactName = jTextArea.getText();
                        if (artifactName == null || artifactName.isEmpty()) return;
                        if (artifactName.contains("selftime"))
                        {
                            return;
                        }
                        int indexOfPercent = artifactName.indexOf("%");
                        String identifier = artifactName.substring(indexOfPercent + 1).trim();
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

            ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance().
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