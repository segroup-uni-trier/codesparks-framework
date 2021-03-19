package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import javax.swing.*;

/*
 * Copyright (C) 2021, Oliver Moseler
 */
public abstract class AArtifactVisualizationLabelFactory extends AVisualizationSequence implements IArtifactVisualizationLabelFactory
{
    protected final AMetricIdentifier primaryMetricIdentifier;
    protected final int X_OFFSET_LEFT;

    public AMetricIdentifier getPrimaryMetricIdentifier()
    {
        return primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.X_OFFSET_LEFT = 0;
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(sequence);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.X_OFFSET_LEFT = 0;
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(sequence);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.X_OFFSET_LEFT = xOffsetLeft;
    }

    protected JLabel emptyLabel()
    {
        JLabel jLabel = new JLabel();
        jLabel.setIcon(new ImageIcon());
        jLabel.setSize(0, 0);
        return jLabel;
    }
}