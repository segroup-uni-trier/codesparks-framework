package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

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
        String identifier = getIdentifier();
        double percentage = getNumericalMetricValue(metricIdentifier);
        String formatPercentage = CoreUtil.formatPercentageWithLeadingWhitespace(percentage);
        //        for (int i = 0; i < 6 - formatPercentage.length(); i++)
//        {
//            stringBuilder.append(" ");
//        }
//        stringBuilder.append(formatPercentage);
        String str = formatPercentage + " " + CoreUtil.reduceToLength(identifier, maxLen);
        return str;
    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier)
    {
        return getDisplayString(metricIdentifier, 39);
    }
}
