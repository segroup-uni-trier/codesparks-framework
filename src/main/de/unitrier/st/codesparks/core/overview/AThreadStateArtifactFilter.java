package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AThreadStateArtifactFilter
{
    private final Map<Integer, String> states;
    private final Map<Integer, Boolean> checked;

    public AThreadStateArtifactFilter(Map<Integer, String> states)
    {
        this.states = states;
        this.checked = new HashMap<>();
        for (Integer value : states.keySet())
        {
            checked.put(value, true);
        }
    }

    public AThreadStateArtifactFilter(Map<Integer, String> states, int... checkedStates)
    {
        this(states);
        List<Integer> checkedStatesList = Arrays.stream(checkedStates).boxed().collect(Collectors.toList());
        for (Integer state : states.keySet())
        {
            boolean stateChecked = checkedStatesList.contains(state);
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

    public abstract Collection<AProfilingArtifact> filterArtifact(final Collection<? extends AProfilingArtifact> artifacts);

}
