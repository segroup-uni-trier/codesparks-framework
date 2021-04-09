package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

/*
 * Copyright (c), Oliver Moseler, 2021
 */
public abstract class AThreadArtifact extends AArtifact
{
    private String callSite;
    private boolean filtered;

//    public AThreadArtifact(final String identifier)
//    {
//        this(identifier, null);
//    }

    public AThreadArtifact(final String identifier, final Class<? extends AThreadArtifact> threadArtifactClass)
    {
        super(identifier, identifier, threadArtifactClass);
        filtered = false;
    }

    public String getCallSite()
    {
        return callSite;
    }

    public void setCallSite(final String callSite)
    {
        this.callSite = callSite;
    }

    public boolean isFiltered()
    {
        return filtered;
    }

    public void setFiltered(final boolean filtered)
    {
        this.filtered = filtered;
    }

    /**
     * Euclidean distance
     * @param thr The thread artifact to get the distance to
     * @param metricIdentifier The metric identifier. Note, has to be numerical.
     * @return The euclidean distance from this thread artifact to thr
     */
    public double dist(final AThreadArtifact thr, final AMetricIdentifier metricIdentifier)
    {
        final double numericalMetricValue = this.getNumericalMetricValue(metricIdentifier);
        final double numericalMetricValue1 = thr.getNumericalMetricValue(metricIdentifier);
        //noinspection UnnecessaryLocalVariable debugging purposes
        final double dist = Math.abs(numericalMetricValue1 - numericalMetricValue); // Euclidean distance
        return dist;
    }

    @Override
    public String getDisplayString(final AMetricIdentifier metricIdentifier, int maxLen)
    {
        String metricValueString;
        if (metricIdentifier.isNumerical())
        {
            final double metricValue = getNumericalMetricValue(metricIdentifier);
            if (metricIdentifier.isRelative())
            {
                metricValueString = CoreUtil.formatPercentageWithLeadingWhitespace(metricValue);
            } else
            {
                metricValueString = Double.toString(metricValue);
            }
        } else
        {
            metricValueString = getMetricValue(metricIdentifier).toString();
        }
        final String reduce = CoreUtil.reduceToLength(identifier, maxLen);
        return metricValueString + " " + reduce;
    }

    @Override
    public String getDisplayString(final AMetricIdentifier metricIdentifier)
    {
        return getDisplayString(metricIdentifier, 39);
    }
}
