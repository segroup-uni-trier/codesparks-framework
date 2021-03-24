package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.ArtifactTrie;

import javax.swing.*;
import java.awt.*;

import static de.unitrier.st.codesparks.core.visualization.VisConstants.*;

public class ArtifactTrieMetricVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    public ArtifactTrieMetricVisualizationLabelFactory(final AMetricIdentifier artifactTrieMetricIdentifier)
    {
        super(artifactTrieMetricIdentifier);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        int lineHeight = VisConstants.getLineHeight();

        final CodeSparksGraphics graphics = getGraphics(500, lineHeight);

        graphics.setColor(BORDER_COLOR);
        graphics.drawRect(X_OFFSET, 0, RECTANGLE_WIDTH, lineHeight);

        final ArtifactTrie trie = ((ArtifactTrie) artifact.getMetricValue(primaryMetricIdentifier));

        final long rootCnt = trie.getRoot().getCnt();

        final int fontHeight = 11;
        graphics.setColor(new JBColor(JBColor.DARK_GRAY, JBColor.ORANGE));
        Font font = new Font("Arial", Font.BOLD, fontHeight);  // TODO: support different font sizes
        graphics.setFont(font);
        graphics.drawString("CCT root:" + rootCnt, X_OFFSET + 3, lineHeight / 2 + 3);

        return makeLabel(graphics, X_OFFSET + RECTANGLE_WIDTH + X_OFFSET);
    }
}
