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
package de.unitrier.st.codesparks.core.data;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AThreadSelectable implements IThreadSelectable
{
    protected AThreadSelectable()
    {
        componentsToRepaintOnSelection = new ArrayList<>();
    }

    protected JComponent component;
    protected AThreadSelectable next;

    public void setNext(AThreadSelectable next)
    {
        this.next = next;
    }

    public AThreadSelectable getNext()
    {
        return next;
    }

    public void propagateSelection()
    {
        AThreadSelectable next = this.next;
        while (next != null && next != this)
        {
            next.syncSelection(this);
            next.repaint();
            next = next.getNext();
        }
    }

    public abstract void syncSelection(AThreadSelectable threadSelectable);

    public JComponent getComponent()
    {
        return component;
    }

    public void repaint()
    {
        if (component != null)
        {
            component.repaint();
        }
    }

    protected final List<JComponent> componentsToRepaintOnSelection;

    @Override
    public void registerComponentToRepaintOnSelection(JComponent componentToRepaintOnSelection)
    {
        componentsToRepaintOnSelection.add(componentToRepaintOnSelection);
    }

    @Override
    public void updateAndRepaintRegisteredComponents()
    {
        for (final JComponent jComponent : componentsToRepaintOnSelection)
        {
            jComponent.repaint();
        }
    }

    @Override
    public Set<String> getFilteredThreadArtifactIdentifiers()
    {
        return getFilteredThreadArtifacts().stream().map(AThreadArtifact::getIdentifier).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getSelectedThreadArtifactIdentifiers()
    {
        return getSelectedThreadArtifacts().stream().map(AThreadArtifact::getIdentifier).collect(Collectors.toSet());
    }

    protected abstract Set<AThreadArtifact> getThreadArtifacts(final boolean isSelected);

    @Override
    public Set<AThreadArtifact> getFilteredThreadArtifacts()
    {
        return getThreadArtifacts(false);
    }

    @Override
    public Set<AThreadArtifact> getSelectedThreadArtifacts()
    {
        return getThreadArtifacts(true);
    }

    @Override
    public Set<AThreadArtifact> getSelectedThreadArtifactsOfCluster(final ThreadArtifactCluster cluster)
    {
        final Set<AThreadArtifact> selectedThreadArtifacts = getSelectedThreadArtifacts();
        return cluster.stream().filter(selectedThreadArtifacts::contains).collect(Collectors.toSet());
    }

    @Override
    public Set<AThreadArtifact> getFilteredThreadArtifactsOfCluster(final ThreadArtifactCluster cluster)
    {
        final Set<AThreadArtifact> filteredThreadArtifacts = getFilteredThreadArtifacts();
        return cluster.stream().filter(filteredThreadArtifacts::contains).collect(Collectors.toSet());
    }
}
