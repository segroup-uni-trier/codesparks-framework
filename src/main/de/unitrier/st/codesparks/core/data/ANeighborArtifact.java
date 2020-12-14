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
            final String name
            , final String identifier
            , final int lineNumber
    )
    {
        super(name, identifier);
        this.lineNumber = lineNumber;
    }

    public double getNumericalMetricValueRelativeTo(final ACodeSparksArtifact aCodeSparksArtifact, final IMetricIdentifier metricIdentifier)
    {
        final double metricValue = getNumericalMetricValue(metricIdentifier);
        final double artifactNumericalMetricValue = aCodeSparksArtifact.getNumericalMetricValue(metricIdentifier);
        return metricValue / artifactNumericalMetricValue;
    }

    public String getDisplayStringRelativeTo(final ACodeSparksArtifact artifact, final IMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(getDisplayStringRelativeTo(artifact, metricIdentifier), maxLen);
    }

    public String getDisplayStringRelativeTo(final ACodeSparksArtifact artifact, final IMetricIdentifier metricIdentifier)
    {
        final double numericalMetricValueRelativeTo = getNumericalMetricValueRelativeTo(artifact, metricIdentifier);
        return name + " - " + metricIdentifier.getDisplayString() + ": " + CoreUtil.formatPercentage(numericalMetricValueRelativeTo);
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
