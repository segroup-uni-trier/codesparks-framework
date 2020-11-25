package de.unitrier.st.codesparks.core.data;

import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;

/**
 * Created by Oliver Moseler on 22.09.2014.
 */
public abstract class ANeighborArtifact extends ABaseArtifact
{
    //private double relativeMetricValue;
    //private final int invocationLine;

    protected ANeighborArtifact(final String name, final String identifier, final int lineNumber)
    {
        super(name, identifier);
        //this.relativeMetricValue = 0d;
        //this.invocationLine = invocationLine;
        this.lineNumber = lineNumber;
    }

//    public int getInvocationLine()
//    {
//        return lineNumber;
//    }

//    public void setRelativeMetricValue(final IMetricIdentifier metricIdentifier, final double total)
//    {
//        double numericalMetricValue = getNumericalMetricValue(metricIdentifier);
//        relativeMetricValue = numericalMetricValue / total;
//    }
//
//    protected double getRelativeMetricValue()
//    {
//        return relativeMetricValue;
//    }

    public PsiElement getInvocationLineElement()
    {
        return getVisPsiElement();
    }

    public void setInvocationLineElement(final PsiElement invocationLineElement)
    {
        setVisPsiElement(invocationLineElement);
    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(getDisplayString(metricIdentifier), maxLen);
    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier)
    {
        return name + " - " + metricIdentifier.getDisplayString() + ": " + CoreUtil.formatPercentage(getNumericalMetricValue(metricIdentifier));
    }
}
