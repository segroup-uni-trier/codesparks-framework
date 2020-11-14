package de.unitrier.st.codesparks.core.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArtifactBuilder
{
    private AArtifact artifact;

    public ArtifactBuilder(final String name, final String identifier, final Class<? extends AArtifact> artifactClass)
    {
        try
        {
            final Constructor<? extends AArtifact> constructor = artifactClass.getDeclaredConstructor(String.class, String.class);
            artifact = constructor.newInstance(name, identifier);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
        {
            e.printStackTrace();
        }
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
