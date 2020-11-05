package de.unitrier.st.insituprofiling.core.data;

import com.intellij.psi.PsiElement;
import de.unitrier.st.insituprofiling.core.CoreUtil;

/**
 * Created by Oliver Moseler on 22.09.2014.
 */
public abstract class ANeighborProfilingArtifact extends ABaseProfilingArtifact
{
    private double relativeMetricValue;
    private final int invocationLine;

    protected ANeighborProfilingArtifact(String name, String identifier, int invocationLine)
    {
        super(name, identifier);
        relativeMetricValue = 0d;
        this.invocationLine = invocationLine;
    }

    public int getInvocationLine()
    {
        return invocationLine;
    }

    public void setRelativeMetricValue(double total)
    {
        relativeMetricValue = metricValue / total;
    }

    protected double getRelativeMetricValue()
    {
        return relativeMetricValue;
    }

    @Override
    public String getMetricValueString()
    {
        return String.format("%s => METRIC-VALUE: %s @LINE: %d", name, CoreUtil.formatPercentage(metricValue), invocationLine);
    }

    public PsiElement getInvocationLineElement()
    {
        return getVisPsiElement();
    }

    public void setInvocationLineElement(PsiElement invocationLineElement)
    {
        setVisPsiElement(invocationLineElement);
    }
}
