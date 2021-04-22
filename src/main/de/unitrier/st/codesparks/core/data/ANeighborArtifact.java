package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

/**
 * Created by Oliver Moseler on 22.09.2014.
 */
/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ANeighborArtifact extends AArtifact
{
    protected ANeighborArtifact(
            final String identifier
            , final String name
            , final Class<? extends AThreadArtifact> threadArtifactClass
            , final int lineNumber
    )
    {
        super(identifier, name, threadArtifactClass);
        this.lineNumber = lineNumber;
    }

    public double getNumericalMetricValueRelativeTo(final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        if (!metricIdentifier.isNumerical())
        {
            return Double.NaN;
        }
        final double metricValue = getNumericalMetricValue(metricIdentifier);
        final double artifactNumericalMetricValue = artifact.getNumericalMetricValue(metricIdentifier);
        return metricValue / artifactNumericalMetricValue;
    }

    public String getDisplayStringRelativeTo(final AArtifact artifact, final AMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(getDisplayStringRelativeTo(artifact, metricIdentifier), maxLen);
    }

    public String getDisplayStringRelativeTo(final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        String metricValueString;
        if (metricIdentifier.isNumerical())
        {
            final double valueRelativeTo = getNumericalMetricValueRelativeTo(artifact, metricIdentifier);
            metricValueString = CoreUtil.formatPercentage(valueRelativeTo);
        } else
        {
            metricValueString = getMetricValue(metricIdentifier).toString();
        }
        return name + " - " + metricIdentifier.getDisplayString() + ": " + metricValueString;
    }
}
