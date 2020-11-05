package de.unitrier.st.insituprofiling.core;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

public abstract class AProfilingDataProcessor extends Task.WithResult<Boolean, Exception> implements IProfilingDataProcessor
{
    protected AProfilingResult result;

    public AProfilingDataProcessor(Project project, String title, boolean canBeCancelled)
    {
        super(project, title, canBeCancelled);
        result = createProfilingResultInstance();
    }

    protected abstract AProfilingResult createProfilingResultInstance();

    public AProfilingResult getProfilingResult()
    {
        return result;
    }

    @Override
    public IProfilingResult processData()
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
