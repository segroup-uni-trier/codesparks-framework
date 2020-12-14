package de.unitrier.st.codesparks.core.data;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultThreadArtifact extends AThreadArtifact
{
    public DefaultThreadArtifact(String identifier)
    {
        super(identifier);
    }

//    public DefaultCodeSparksThread(String identifier, double metricValue)
//    {
//        super(identifier, metricValue);
//    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier, int maxLen)
    {
//        final String identifier = getIdentifier();
        final double percentage = getNumericalMetricValue(metricIdentifier);
        final String formatPercentage = CoreUtil.formatPercentageWithLeadingWhitespace(percentage);
        //        for (int i = 0; i < 6 - formatPercentage.length(); i++)
//        {
//            stringBuilder.append(" ");
//        }
//        stringBuilder.append(formatPercentage);
        final String reduce = CoreUtil.reduceToLength(identifier, maxLen);
        return formatPercentage + " " + reduce;
    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier)
    {
        return getDisplayString(metricIdentifier, 39);
    }

    @Override
    public void navigate()
    {
        final PsiElement visPsiElement = getVisPsiElement();
        if (visPsiElement == null)
        {
            return;
        }
        final PsiElement navigationElement = visPsiElement.getNavigationElement();
        if (navigationElement instanceof Navigatable)
        {
            ((Navigatable) navigationElement).navigate(true);
        }
    }
}
