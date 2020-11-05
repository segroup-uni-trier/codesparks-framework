package de.unitrier.st.insituprofiling.core.visualization;

public abstract class AVisualizationSequence
{
    private final int sequence;
    private final boolean isDefault;

    protected AVisualizationSequence()
    {
        this.sequence = -1;
        this.isDefault = false;
    }

    protected AVisualizationSequence(int sequence, boolean isDefault)
    {
        this.sequence = Math.max(sequence, -1);
        this.isDefault = isDefault;
    }

    public int getSequence()
    {
        return sequence;
    }

    public boolean isDefault()
    {
        return isDefault;
    }
}
