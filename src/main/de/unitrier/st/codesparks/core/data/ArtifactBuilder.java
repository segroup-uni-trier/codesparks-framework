package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactBuilder
{
    private static final class DefaultCodeSparksArtifact extends ACodeSparksArtifact
    {
        public DefaultCodeSparksArtifact(final String name, final String identifier)
        {
            super(name, identifier);
        }

        @Override
        public void navigate() { CodeSparksLogger.addText("Navigation is unavailable for default artifacts."); }
    }

    private ACodeSparksArtifact artifact;

    public ArtifactBuilder(final String name, final String identifier, final Class<? extends ACodeSparksArtifact> artifactClass)
    {
        try
        {
            final Constructor<? extends ACodeSparksArtifact> constructor = artifactClass.getDeclaredConstructor(String.class, String.class);
            artifact = constructor.newInstance(name, identifier);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
        {
            CodeSparksLogger.addText("%s: %s", getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public ArtifactBuilder(final String name, final String identifier)
    {
        artifact = new DefaultCodeSparksArtifact(name, identifier);
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

    public ArtifactBuilder setNumericMetricValue(final IMetricIdentifier metricIdentifier, double metricValue)
    {
        artifact.setNumericalMetricValue(metricIdentifier, metricValue);
        return this;
    }

    public ACodeSparksArtifact get()
    {
        return artifact;
    }

}
