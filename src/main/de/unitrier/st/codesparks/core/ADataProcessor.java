/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.data.IArtifactPool;

public abstract class ADataProcessor extends Task.WithResult<Boolean, Exception> implements IDataProcessor
{
    protected final IArtifactPool artifactPool;

    public ADataProcessor(final Project project, final String title, final boolean canBeCancelled)
    {
        super(project, title, canBeCancelled);
        artifactPool = createArtifactPoolInstance();
    }

    protected abstract IArtifactPool createArtifactPoolInstance();

    public IArtifactPool getArtifactPool()
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
