/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerItem;

import java.util.Collection;

public interface IDataVisualizer
{
    Collection<EditorCoverLayerItem> createVisualizations(final Project project, final Collection<AArtifact> matchedArtifacts);
}
