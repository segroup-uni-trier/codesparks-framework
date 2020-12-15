/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerItem;

import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IDataVisualizer
{
    Collection<EditorCoverLayerItem> createVisualizations(final Project project, final Collection<AArtifact> matchedArtifacts);
}
