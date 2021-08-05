/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArtifactBuilder
{
    private AArtifact artifact;

    public ArtifactBuilder(final String identifier, final String name, final Class<? extends AArtifact> artifactClass)
    {
        try
        {
            final Constructor<? extends AArtifact> constructor = artifactClass.getDeclaredConstructor(String.class, String.class);
            artifact = constructor.newInstance(identifier, name);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
        {
            CodeSparksLogger.addText("%s: %s", getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public ArtifactBuilder(final String identifier, final String name)
    {
        artifact = new DefaultArtifact(identifier, name);
    }

    public ArtifactBuilder setFileName(String fileName)
    {
        artifact.fileName = fileName;
        return this;
    }

    public ArtifactBuilder setLineNumber(int lineNumber)
    {
        artifact.lineNumber = lineNumber;
        return this;
    }

    public ArtifactBuilder setNumericMetricValue(final AMetricIdentifier metricIdentifier, double metricValue)
    {
        artifact.setNumericalMetricValue(metricIdentifier, metricValue);
        return this;
    }

    public AArtifact get()
    {
        return artifact;
    }

}
