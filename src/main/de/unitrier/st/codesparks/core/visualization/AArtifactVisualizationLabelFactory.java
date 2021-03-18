package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import javax.swing.*;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualizationLabelFactory extends AVisualizationSequence implements IArtifactVisualizationLabelFactory
{
    protected final AMetricIdentifier primaryMetricIdentifier;

    public AMetricIdentifier getPrimaryMetricIdentifier()
    {
        return primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(sequence);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected JLabel emptyLabel()
    {
        JLabel jLabel = new JLabel();
        jLabel.setIcon(new ImageIcon());
        jLabel.setSize(0, 0);
        return jLabel;
    }
}