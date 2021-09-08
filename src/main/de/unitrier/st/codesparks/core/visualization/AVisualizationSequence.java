/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

public abstract class AVisualizationSequence
{
    private final int sequence;

    protected AVisualizationSequence()
    {
        this.sequence = -1;
    }

    protected AVisualizationSequence(int sequence)
    {
        this.sequence = Math.max(sequence, -1);
    }

    public int getSequence()
    {
        return sequence;
    }
}
