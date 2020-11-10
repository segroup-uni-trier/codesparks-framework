package de.unitrier.st.codesparks.core;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

public abstract class ADataProcessor extends Task.WithResult<Boolean, Exception> implements IDataProcessor
{
    protected AArtifactPool result;

    public ADataProcessor(Project project, String title, boolean canBeCancelled)
    {
        super(project, title, canBeCancelled);
        result = createProfilingResultInstance();
    }

    protected abstract AArtifactPool createProfilingResultInstance();

    public AArtifactPool getProfilingResult()
    {
        return result;
    }

    @Override
    public IArtifactPool processData()
    {
        try
        {
            if (ProgressManager.getInstance().run(this))
            {
                return result;
            }
            return null;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
