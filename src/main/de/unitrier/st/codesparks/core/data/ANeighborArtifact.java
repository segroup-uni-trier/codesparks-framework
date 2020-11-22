package de.unitrier.st.codesparks.core.data;

import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;

/**
 * Created by Oliver Moseler on 22.09.2014.
 */
public abstract class ANeighborArtifact extends ABaseArtifact
{
    private double relativeMetricValue;
    private final int invocationLine;

    protected ANeighborArtifact(String name, String identifier, int invocationLine)
    {
        super(name, identifier);
        relativeMetricValue = 0d;
        this.invocationLine = invocationLine;
    }

    public int getInvocationLine()
    {
        return invocationLine;
    }

    public void setRelativeMetricValue(final String metricIdentifier, double total)
    {
        double numericalMetricValue = getNumericalMetricValue(metricIdentifier);
        relativeMetricValue = numericalMetricValue / total;
    }

    protected double getRelativeMetricValue()
    {
        return relativeMetricValue;
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
