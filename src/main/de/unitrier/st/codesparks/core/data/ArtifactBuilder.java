package de.unitrier.st.codesparks.core.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArtifactBuilder
{
    private AArtifact artifact;

    public ArtifactBuilder(Class<? extends AArtifact> artifactClass)
    {
        try
        {
            final Constructor<? extends AArtifact> constructor = artifactClass.getConstructor();
            artifact = constructor.newInstance();
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

    public ArtifactBuilder setIdentifier(String identifier)
    {
        artifact.identifier = identifier;
        return this;
    }

    public ArtifactBuilder setName(String name)
    {
        artifact.name = name;
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
