package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

import javax.swing.*;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualizationLabelFactory extends AVisualizationSequence implements IArtifactVisualizationLabelFactory
{
    protected final IMetricIdentifier primaryMetricIdentifier;

    public IMetricIdentifier getPrimaryMetricIdentifier()
    {
        return primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
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