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

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ABaseArtifact extends AMetricArtifact implements IDisplayable, ICodeSparksThreadFilterable
{
    private final Map<String, ACodeSparksThread> threadMap;
    private final Class<? extends ACodeSparksThread> threadClass;

    ABaseArtifact(final String name, final String identifier)
    {
        this(name, identifier, DefaultCodeSparksThread.class);
    }

    ABaseArtifact(final String name, final String identifier, final Class<? extends ACodeSparksThread> threadClass)
    {
        super(name, identifier);
        this.threadClass = threadClass;
        threadMap = new HashMap<>();
    }

    protected int lineNumber;

    public int getLineNumber() { return lineNumber; }

    protected String fileName;

    public String getFileName() { return fileName; }

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
     * Display strings
     */

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(name, maxLen);
    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier)
    {
        return name;
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

    public synchronized void increaseNumericalMetricValueThread(final IMetricIdentifier metricIdentifier, final String threadIdentifier, double toIncrease)
    {
        synchronized (threadMapLock)
        {
            ACodeSparksThread codeSparksThread = threadMap.get(threadIdentifier);
            if (codeSparksThread == null)
            {
                try
                {
                    final Constructor<? extends ACodeSparksThread> constructor = threadClass.getConstructor(String.class);
                    codeSparksThread = constructor.newInstance(threadIdentifier);
                    threadMap.put(threadIdentifier, codeSparksThread);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }

            assert codeSparksThread != null;

            codeSparksThread.increaseNumericalMetricValue(metricIdentifier, toIncrease);

            double threadMetricValue = codeSparksThread.getNumericalMetricValue(metricIdentifier);

//            assertSecondaryMetricValue(threadMetricValue, "thread");
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
     // TODO: enable assertion again!

//    @Deprecated
//    void assertSecondaryMetricValue(double secondaryMetricValue, String name)
//    {
//        double epsilon = .0000000000000001;
//        assert secondaryMetricValue - epsilon <= metricValue : "secondary metric value (" + name + ") larger than total metric value (" +
//                secondaryMetricValue + " > " + metricValue + ")";
//    }
}
