package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ArtifactTrie;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

import static de.unitrier.st.codesparks.core.visualization.VisConstants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ArtifactTrieMetricVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    public ArtifactTrieMetricVisualizationLabelFactory(final IMetricIdentifier artifactTrieMetricIdentifier)
    {
        super(artifactTrieMetricIdentifier);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        int lineHeight = VisConstants.getLineHeight();
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, 500, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        VisualizationUtil.drawTransparentBackground(graphics, bi);

        graphics.setColor(BORDER_COLOR);
        graphics.drawRect(X_OFFSET, 0, RECTANGLE_WIDTH, lineHeight);

        final ArtifactTrie trie = ((ArtifactTrie) artifact.getMetricValue(primaryMetricIdentifier));

        final long rootCnt = trie.getRoot().getCnt();

        final int fontHeight = 11;
        graphics.setColor(new JBColor(JBColor.DARK_GRAY, JBColor.ORANGE));
        Font font = new Font("Arial", Font.BOLD, fontHeight);  // TODO: support different font sizes
        graphics.setFont(font);
        graphics.drawString("CCT root:" + rootCnt, X_OFFSET + 3, lineHeight / 2 + 3);


        BufferedImage subImage = bi.getSubimage(0, 0, X_OFFSET + RECTANGLE_WIDTH + X_OFFSET, bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subImage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());

        return jLabel;
    }
}
