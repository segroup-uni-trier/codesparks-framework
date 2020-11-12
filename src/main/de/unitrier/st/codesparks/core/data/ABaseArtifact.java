package de.unitrier.st.codesparks.core.data;

import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.ICodeSparksThreadFilterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ABaseArtifact implements IDisplayable, ICodeSparksThreadFilterable
{
    private final Map<String, CodeSparksThread> threadMap;
    private final Class<? extends CodeSparksThread> threadClass;

    ABaseArtifact(String name, String identifier)
    {
        this(name, identifier, DefaultCodeSparksThread.class);
    }

    ABaseArtifact(String name, String identifier, Class<? extends CodeSparksThread> threadClass)
    {
        this.name = name == null ? "" : name;
        this.identifier = identifier == null ? "" : identifier;
        this.threadClass = threadClass;
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

    public boolean hasThreads()
    {
        return !getThreadArtifacts().isEmpty();
    }

    public Collection<CodeSparksThread> getThreadArtifacts()
    {
        synchronized (threadMap)
        {
            return threadMap.values();
        }
    }

    public abstract Map<String, List<CodeSparksThread>> getThreadTypeLists();

    public CodeSparksThread getThreadArtifact(String identifier)
    {
        synchronized (threadMap)
        {
            return threadMap.get(identifier);
        }
    }

    @Deprecated
    public void addThreadArtifact(CodeSparksThread codeSparksThread)
    {
        synchronized (threadMap)
        {
            threadMap.put(codeSparksThread.getIdentifier(), codeSparksThread);
        }
    }

    public synchronized void increaseMetricValueSelfForThread(String identifier, double toIncrease)
    {
        synchronized (threadMap)
        {
            CodeSparksThread codeSparksThread = threadMap.get(identifier);
            if (codeSparksThread == null)
            {
                try
                {
                    final Constructor<? extends CodeSparksThread> constructor = threadClass.getConstructor(String.class,
                            double.class);
                    codeSparksThread = constructor.newInstance(identifier, toIncrease);
                    threadMap.put(identifier, codeSparksThread);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }

            assert codeSparksThread != null;

            codeSparksThread.increaseMetricValueSelf(toIncrease);

            double threadMetricValue = codeSparksThread.getMetricValue();

            assertSecondaryMetricValue(threadMetricValue, "thread");
        }
    }

    public synchronized void increaseMetricValueThread(String identifier, double toIncrease)
    {
        synchronized (threadMap)
        {
            CodeSparksThread codeSparksThread = threadMap.get(identifier);
            if (codeSparksThread == null)
            {
                try
                {
                    final Constructor<? extends CodeSparksThread> constructor = threadClass.getConstructor(String.class,
                            double.class);
                    codeSparksThread = constructor.newInstance(identifier, toIncrease);
                    threadMap.put(identifier, codeSparksThread);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                codeSparksThread.increaseMetricValue(toIncrease);
            }

            assert codeSparksThread != null;

            double threadMetricValue = codeSparksThread.getMetricValue();

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
    public void applyThreadFilter(ICodeSparksThreadFilter threadFilter)
    {
        final Set<String> threadArtifactIdentifiers = getCodeSparksThreadIdentifiers();
        final Set<String> filteredThreadArtifactIdentifiers = threadFilter.getFilteredThreadIdentifiers();
        filteredThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String filteredThreadArtifactIdentifier : filteredThreadArtifactIdentifiers)
        {
            getThreadArtifact(filteredThreadArtifactIdentifier).setFiltered(true);
        }

        final Set<String> selectedThreadArtifactIdentifiers = threadFilter.getSelectedThreadIdentifiers();
        selectedThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String selectedThreadArtifactIdentifier : selectedThreadArtifactIdentifiers)
        {
            getThreadArtifact(selectedThreadArtifactIdentifier).setFiltered(false);
        }
    }

    public Set<String> getCodeSparksThreadIdentifiers()
    {
        return getThreadArtifacts().stream().map(CodeSparksThread::getIdentifier).collect(Collectors.toSet());
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
