package de.unitrier.st.codesparks.core.data;

import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.ICodeSparksThreadFilterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ABaseArtifact implements IDisplayable, ICodeSparksThreadFilterable
{
    private final Map<String, ACodeSparksThread> threadMap;
    private final Class<? extends ACodeSparksThread> threadClass;

    ABaseArtifact(final String name, final String identifier)
    {
        this(name, identifier, DefaultCodeSparksThread.class);
    }

    ABaseArtifact(final String name, final String identifier, final Class<? extends ACodeSparksThread> threadClass)
    {
        this.name = name == null ? "" : name;
        this.identifier = identifier == null ? "" : identifier;
        this.threadClass = threadClass;
        threadMap = new HashMap<>();
        metrics = new HashMap<>();
        metricValue = 0D;
        metricValueSelf = 0D;
    }

    protected final String name;

    public String getName()
    {
        return name;
    }

    protected final String identifier;

    public String getIdentifier()
    {
        return identifier;
    }

    protected int lineNumber;

    public int getLineNumber() { return lineNumber; }

    protected String fileName;

    public String getFileName() { return fileName; }

    /*
     * MetricValue
     */

    Map<Class<? extends Metric<?>>, Metric<?>> metrics; // TODO: OM!

    private final Object metricsLock = new Object();

    public void addMetric(final Metric<?> metric)
    {
        synchronized (metricsLock)
        {
            //noinspection unchecked
            final Class<? extends Metric<?>> aClass = (Class<? extends Metric<?>>) metric.getClass();
            metrics.put(aClass, metric);
        }
    }

    public Object getMetricValue(final Class<? extends Metric<?>> metricClass)
    {
        synchronized (metricsLock)
        {
            final Metric<?> metric = metrics.get(metricClass);

            if (metric == null)
            {
                return null;
            }

            return metric.getValue();
        }
    }

    public void setMetricValue(final Class<? extends Metric<?>> metricClass, final Object value)
    {
        synchronized (metricsLock)
        {
            final Metric<?> metric = metrics.get(metricClass);

            if (metric == null)
            {
                return;
            }

            //metric.setValue(value);
        }
    }

    public void increaseNumericalMetricValue(final Class<? extends NumericalMetric> numericalMetricClass, final double toIncrease)
    {
        synchronized (metricsLock)
        {
            NumericalMetric metric = (NumericalMetric) metrics.get(numericalMetricClass);

            if (metric == null)
            {

                try
                {
                    final Constructor<? extends NumericalMetric> constructor = numericalMetricClass.getConstructor();
                    metric = constructor.newInstance();

                    metrics.put(numericalMetricClass, metric);

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }

            assert metric != null;

            metric.increaseNumericalValue(toIncrease);
        }
    }

    public double getNumericalMetricValue(final Class<? extends NumericalMetric> numericalMetricClass)
    {
        synchronized (metricsLock)
        {
            final NumericalMetric metric = (NumericalMetric) metrics.get(numericalMetricClass);

            if (metric == null)
            {
                return Double.NaN;
            }

            return metric.getValue();
        }
    }

    public void setNumericalMetricValue(final Class<? extends NumericalMetric> numericalMetricClass, final double metricValue)
    {
        synchronized (metricsLock)
        {
            NumericalMetric metric = (NumericalMetric) metrics.get(numericalMetricClass);

            if (metric == null)
            {
                try
                {
                    final Constructor<? extends NumericalMetric> constructor = numericalMetricClass.getConstructor();
                    metric = constructor.newInstance();

                    metrics.put(numericalMetricClass, metric);

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }

            assert metric != null;

            metric.setValue(metricValue);
        }
    }

    public String getMetricValueText(final Class<? extends Metric<?>> metricClass)
    {
        synchronized (metricsLock)
        {
            final Metric<?> metric = metrics.get(metricClass);

            if (metric == null)
            {
                return "n/a";
            }

            return metric.toString();
        }
    }

    /*
    Deprecated methods
     */


    double metricValue;

    private final Object metricValueLock = new Object();

//    @Deprecated
//    public void increaseMetricValue(double toIncrease)
//    {
//        synchronized (metricValueLock)
//        {
//            this.metricValue += toIncrease;
//        }
//    }

    @Deprecated
    public double getMetricValue()
    {
        synchronized (metricValueLock)
        {
            return metricValue;
        }
    }

    @Deprecated
    public String getMetricValueText()
    {
        synchronized (metricValueLock)
        {
            return Double.toString(metricValue);
        }
    }

    @Deprecated
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

    @Deprecated
    public double getMetricValueSelf()
    {
        synchronized (metricValueSelfLock)
        {
            return metricValueSelf;
        }
    }

    @Deprecated
    public String getMetricValueSelfText()
    {
        synchronized (metricValueSelfLock)
        {
            return Double.toString(metricValueSelf);
        }
    }

    @Deprecated
    public void setMetricValueSelf(double metricValueSelf)
    {
        synchronized (metricValueSelfLock)
        {
            this.metricValueSelf = metricValueSelf;
        }
    }

    @Deprecated
    public void increaseMetricValueSelf(double toIncrease)
    {
        synchronized (metricValueSelfLock)
        {
            metricValueSelf += toIncrease;
            assertSecondaryMetricValue(metricValueSelf, "self");
        }
    }

    @Deprecated
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

    @Override
    public String getDisplayString(final int maxLen)
    {
        return CoreUtil.reduceToLength(name, maxLen);
    }

    @Override
    public String getDisplayString()
    {
        return name;
    }

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

    private final Object threadMapLock = new Object();

    public Collection<ACodeSparksThread> getThreadArtifacts()
    {
        synchronized (threadMapLock)
        {
            return threadMap.values();
        }
    }

    public Map<String, List<ACodeSparksThread>> getThreadTypeLists()
    {
        return getThreadTypeLists(s -> {
            int index = s.indexOf(":");
            //noinspection UnnecessaryLocalVariable
            String substring = s.substring(0, index);
            return substring;
        });
    }

    public Map<String, List<ACodeSparksThread>> getThreadTypeLists(final Function<String, String> threadIdentifierProcessor)
    {
        Collection<ACodeSparksThread> threadArtifacts = getThreadArtifacts();
        Map<String, List<ACodeSparksThread>> threadTypeLists = new ConcurrentHashMap<>();
        for (ACodeSparksThread codeSparksThread : threadArtifacts)
        {
            String identifier = codeSparksThread.getIdentifier();
            String processed = threadIdentifierProcessor.apply(identifier);
            List<ACodeSparksThread> threadArtifactList = threadTypeLists.getOrDefault(processed, new ArrayList<>());
            threadArtifactList.add(codeSparksThread);
            threadTypeLists.put(processed, threadArtifactList);
        }
        return threadTypeLists;
    }

    public ACodeSparksThread getThreadArtifact(String identifier)
    {
        synchronized (threadMapLock)
        {
            return threadMap.get(identifier);
        }
    }

    @Deprecated
    public void addThreadArtifact(ACodeSparksThread codeSparksThread)
    {
        synchronized (threadMapLock)
        {
            threadMap.put(codeSparksThread.getIdentifier(), codeSparksThread);
        }
    }

    @Deprecated
    public synchronized void increaseMetricValueSelfForThread(String identifier, double toIncrease)
    {
        synchronized (threadMapLock)
        {
            ACodeSparksThread codeSparksThread = threadMap.get(identifier);
            if (codeSparksThread == null)
            {
                try
                {
                    final Constructor<? extends ACodeSparksThread> constructor = threadClass.getConstructor(String.class,
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

    @Deprecated
    public synchronized void increaseMetricValueThread(String identifier, double toIncrease)
    {
        synchronized (threadMapLock)
        {
            ACodeSparksThread codeSparksThread = threadMap.get(identifier);
            if (codeSparksThread == null)
            {
                try
                {
                    final Constructor<? extends ACodeSparksThread> constructor = threadClass.getConstructor(String.class,
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
        synchronized (threadMapLock)
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
        return getThreadArtifacts().stream().map(ACodeSparksThread::getIdentifier).collect(Collectors.toSet());
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
