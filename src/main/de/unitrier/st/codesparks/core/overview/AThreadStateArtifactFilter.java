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
package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AThreadStateArtifactFilter
{
    private final Map<Integer, String> states;
    private final Map<Integer, Boolean> checked;

    public AThreadStateArtifactFilter(final Map<Integer, String> states)
    {
        this.states = states;
        this.checked = new HashMap<>();
        for (final Integer state : states.keySet())
        {
            checked.put(state, true);
        }
    }

    public AThreadStateArtifactFilter(final Map<Integer, String> states, final int... checkedStates)
    {
        this(states);
        final List<Integer> checkedStatesList = Arrays.stream(checkedStates).boxed().collect(Collectors.toList());
        for (final Integer state : states.keySet())
        {
            final boolean stateChecked = checkedStatesList.contains(state);
            checked.put(state, stateChecked);
        }
    }

    public Collection<String> getStateStrings()
    {
        return states.values();
    }

    public Collection<Integer> getStateCodes()
    {
        return states.keySet();
    }

    public abstract Collection<AArtifact> filterArtifact(final Collection<? extends AArtifact> artifacts);

}
