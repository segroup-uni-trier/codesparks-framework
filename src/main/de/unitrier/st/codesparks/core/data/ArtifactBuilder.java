package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArtifactBuilder
{
    private static final class DefaultArtifact extends AArtifact
    {
        public DefaultArtifact(final String name, final String identifier)
        {
            super(name, identifier);
        }

        @Override
        public void navigate() { CodeSparksLogger.addText("Navigation is unavailable for default artifacts."); }
    }

    private AArtifact artifact;

    public ArtifactBuilder(final String name, final String identifier, final Class<? extends AArtifact> artifactClass)
    {
        try
        {
            final Constructor<? extends AArtifact> constructor = artifactClass.getDeclaredConstructor(String.class, String.class);
            artifact = constructor.newInstance(name, identifier);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
        {
            CodeSparksLogger.addText("%s: %s", getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public ArtifactBuilder(final String name, final String identifier)
    {
        artifact = new DefaultArtifact(name, identifier);
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

    public ArtifactBuilder setMetricValue(double metricValue)
    {
        artifact.metricValue = metricValue;
        return this;
    }

    public AArtifact get()
    {
        return artifact;
    }

}
