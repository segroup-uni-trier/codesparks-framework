package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultCodeSparksThread extends ACodeSparksThread
{
    public DefaultCodeSparksThread(String identifier)
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
}
