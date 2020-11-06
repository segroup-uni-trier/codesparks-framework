/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerItem;

import java.util.Collection;

public interface IProfilingDataVisualizer
{
    Collection<EditorCoverLayerItem> createVisualizations(final Project project, final Collection<AProfilingArtifact> matchedArtifacts);
}
