package de.unitrier.st.codesparks.core;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ADataProcessor extends Task.WithResult<Boolean, Exception> implements IDataProcessor
{
    protected final AArtifactPool artifactPool;

    public ADataProcessor(final Project project, final String title, final boolean canBeCancelled)
    {
        super(project, title, canBeCancelled);
        artifactPool = createArtifactPoolInstance();
    }

    protected abstract AArtifactPool createArtifactPoolInstance();

    public AArtifactPool getArtifactPool()
    {
        return artifactPool;
    }

    @Override
    public IArtifactPool processData()
    {
        try
        {
            if (ProgressManager.getInstance().run(this))
            {
                return artifactPool;
            }
            return null;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
