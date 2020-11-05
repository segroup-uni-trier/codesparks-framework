package de.unitrier.st.insituprofiling.core.data;

import com.intellij.psi.PsiElement;
import de.unitrier.st.insituprofiling.core.CoreUtil;
import de.unitrier.st.insituprofiling.core.IThreadArtifactFilterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ABaseProfilingArtifact implements IDisplayable, IThreadArtifactFilterable
{
    private final Map<String, ThreadArtifact> threadMap;
    private final Class<? extends ThreadArtifact> threadArtifactClass;

    ABaseProfilingArtifact(String name, String identifier)
    {
        this(name, identifier, DefaultThreadArtifact.class);
    }

    ABaseProfilingArtifact(String name, String identifier, Class<? extends ThreadArtifact> threadArtifactClass)
    {
        this.name = name == null ? "" : name;
        this.identifier = identifier == null ? "" : identifier;
        this.threadArtifactClass = threadArtifactClass;
        metricValue = 0D;
        metricValueSelf = 0D;
        threadMap = new HashMap<>();
    }

    protected final String name;

    public String getName()
    {
        return name;
    }

    protected String identifier;

    public String getIdentifier()
    {
        return identifier;
    }

    /*
     * MetricValue
     */

    double metricValue;

    private final Object metricValueLock = new Object();

    public void increaseMetricValue(double toIncrease)
    {
        synchronized (metricValueLock)
        {
            this.metricValue += toIncrease;
        }
    }

    public double getMetricValue()
    {
        synchronized (metricValueLock)
        {
            return metricValue;
        }
    }

    public String getMetricValueText()
    {
        synchronized (metricValueLock)
        {
            return Double.toString(metricValue);
        }
    }

    public void setMetricValue(double metricValue)
    {
        synchronized (metricValueLock)
        {
            this.metricValue = metricValue;
        }
    }

    /*
     * MetricValueSelf
     */

    double metricValueSelf;

    private final Object metricValueSelfLock = new Object();

    public double getMetricValueSelf()
    {
        synchronized (metricValueSelfLock)
        {
            return metricValueSelf;
        }
    }

    public String getMetricValueSelfText()
    {
        synchronized (metricValueSelfLock)
        {
            return Double.toString(metricValueSelf);
        }
    }

    public void setMetricValueSelf(double metricValueSelf)
    {
        synchronized (metricValueSelfLock)
        {
            this.metricValueSelf = metricValueSelf;
        }
    }

    public void increaseMetricValueSelf(double toIncrease)
    {
        synchronized (metricValueSelfLock)
        {
            metricValueSelf += toIncrease;
            assertSecondaryMetricValue(metricValueSelf, "self");
        }
    }

    @SuppressWarnings("unused")
    public void decreaseMetricValueSelf(double toDecrease)
    {
        synchronized (metricValueSelfLock)
        {
            metricValueSelf -= toDecrease;
        }
    }

    /*
     * Psi
     */

    private PsiElement visPsiElement;

    private final Object visPsiElementLock = new Object();

    public PsiElement getVisPsiElement()
    {
        synchronized (visPsiElementLock)
        {
            return visPsiElement;
        }
    }

    public void setVisPsiElement(PsiElement visPsiElement)
    {
        synchronized (visPsiElementLock)
        {
            this.visPsiElement = visPsiElement;
        }
    }

    /*
     * Visualization strings
     */

    public String getMetricValueString()
    {
        return String.format("%s => METRIC-VALUE: %s", name, CoreUtil.formatPercentage(metricValue));
    }

    /*
     * Threads
     */

    public Collection<ThreadArtifact> getThreadArtifacts()
    {
        synchronized (threadMap)
        {
            return threadMap.values();
        }
    }

    public abstract Map<String, List<ThreadArtifact>> getThreadTypeLists();

    public ThreadArtifact getThreadArtifact(String identifier)
    {
        synchronized (threadMap)
        {
            return threadMap.get(identifier);
        }
    }

    @Deprecated
    public void addThreadArtifact(ThreadArtifact threadArtifact)
    {
        synchronized (threadMap)
        {
            threadMap.put(threadArtifact.getIdentifier(), threadArtifact);
        }
    }

    public synchronized void increaseMetricValueSelfForThread(String identifier, double toIncrease)
    {
        synchronized (threadMap)
        {
            ThreadArtifact threadArtifact = threadMap.get(identifier);
            if (threadArtifact == null)
            {
                try
                {
                    final Constructor<? extends ThreadArtifact> constructor = threadArtifactClass.getConstructor(String.class,
                            double.class);
                    threadArtifact = constructor.newInstance(identifier, toIncrease);
                    threadMap.put(identifier, threadArtifact);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }

            assert threadArtifact != null;

            threadArtifact.increaseMetricValueSelf(toIncrease);

            double threadMetricValue = threadArtifact.getMetricValue();

            assertSecondaryMetricValue(threadMetricValue, "thread");
        }
    }

    public synchronized void increaseMetricValueThread(String identifier, double toIncrease)
    {
        synchronized (threadMap)
        {
            ThreadArtifact threadArtifact = threadMap.get(identifier);
            if (threadArtifact == null)
            {
                try
                {
                    final Constructor<? extends ThreadArtifact> constructor = threadArtifactClass.getConstructor(String.class,
                            double.class);
                    threadArtifact = constructor.newInstance(identifier, toIncrease);
                    threadMap.put(identifier, threadArtifact);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                threadArtifact.increaseMetricValue(toIncrease);
            }

            assert threadArtifact != null;

            double threadMetricValue = threadArtifact.getMetricValue();

            assertSecondaryMetricValue(threadMetricValue, "thread");
        }
    }

    public int getNumberOfThreads()
    {
        synchronized (threadMap)
        {
            return threadMap.size();
        }
    }

    @Override
    public void applyThreadArtifactFilter(IThreadArtifactFilter threadArtifactFilter)
    {
        final Set<String> threadArtifactIdentifiers = getThreadArtifactIdentifiers();
        final Set<String> filteredThreadArtifactIdentifiers = threadArtifactFilter.getFilteredThreadArtifactIdentifiers();
        filteredThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String filteredThreadArtifactIdentifier : filteredThreadArtifactIdentifiers)
        {
            getThreadArtifact(filteredThreadArtifactIdentifier).setFiltered(true);
        }

        final Set<String> selectedThreadArtifactIdentifiers = threadArtifactFilter.getSelectedThreadArtifactIdentifiers();
        selectedThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String selectedThreadArtifactIdentifier : selectedThreadArtifactIdentifiers)
        {
            getThreadArtifact(selectedThreadArtifactIdentifier).setFiltered(false);
        }
    }

    public Set<String> getThreadArtifactIdentifiers()
    {
        return getThreadArtifacts().stream().map(ThreadArtifact::getIdentifier).collect(Collectors.toSet());
    }

    /*
     * Helpers
     */
    void assertSecondaryMetricValue(double secondaryMetricValue, String name)
    {
        double epsilon = .0000000000000001;
        assert secondaryMetricValue - epsilon <= metricValue : "secondary metric value (" + name + ") larger than total metric value (" +
                secondaryMetricValue + " > " + metricValue + ")";
    }
}
