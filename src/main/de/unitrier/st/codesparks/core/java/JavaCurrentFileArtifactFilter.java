/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.java;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.ArtifactPoolManager;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.overview.ICurrentFileArtifactFilter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JavaCurrentFileArtifactFilter implements ICurrentFileArtifactFilter
{
    private static final Map<Function<AArtifact, String>, ICurrentFileArtifactFilter> instances = new HashMap<>();

    public static ICurrentFileArtifactFilter getInstance(final Function<AArtifact, String> artifactStringFunc)
    {
        synchronized (JavaCurrentFileArtifactFilter.class)
        {
            ICurrentFileArtifactFilter filter = instances.get(artifactStringFunc);
            if (filter == null)
            {
                filter = new JavaCurrentFileArtifactFilter(artifactStringFunc);
                instances.put(artifactStringFunc, filter);
            }
            return filter;
        }
    }

    public static ICurrentFileArtifactFilter getInstance()
    {
        return getInstance(AArtifact::getIdentifier);
    }

    private final Function<AArtifact, String> artifactStringFunc;

    private JavaCurrentFileArtifactFilter(Function<AArtifact, String> artifactStringFunc)
    {
        this.artifactStringFunc = artifactStringFunc;
    }

    @Override
    public Collection<? extends AArtifact> filterArtifact(final Collection<? extends AArtifact> artifacts, final PsiFile psiFile)
    {
        final Optional<? extends AArtifact> first = artifacts.stream().findFirst();
        if (first.isEmpty())
        {
            return artifacts;
        }

        final Collection<PsiClass> childrenOfType = PsiTreeUtil.findChildrenOfType(psiFile, PsiClass.class);
        final Set<String> qualifiedNames = new HashSet<>();
        for (final PsiClass psiClass : childrenOfType)
        {
            final String qualifiedName = psiClass.getQualifiedName();
            if (qualifiedName == null || qualifiedName.equals(""))
            {
                continue;
            }
            qualifiedNames.add(qualifiedName);
        }

        final Function<AArtifact, Boolean> f = (artifact) -> qualifiedNames.stream()
                .anyMatch(qualifiedName -> artifactStringFunc.apply(artifact).equals(qualifiedName) // Java classes
                        || (artifactStringFunc.apply(artifact).toLowerCase().startsWith(qualifiedName.toLowerCase() + ".")
                        || artifactStringFunc.apply(artifact).toLowerCase().startsWith(qualifiedName.toLowerCase() + "$"))
                );

        Collection<AArtifact> filtered;
        if (first.get() instanceof AThreadArtifact)
        { // Only thread artifacts
            final IArtifactPool artifactPool = ArtifactPoolManager.getInstance().getArtifactPool();
            final Set<AArtifact> setOfNonThreadArtifacts =
//                    artifactPool.getMapOfArtifacts().values().stream().flatMap(Collection::stream)
//                            .filter(artifact -> !(artifact instanceof AThreadArtifact)).collect(Collectors.toSet());
                    artifactPool.getAllArtifacts()
                            .stream()
                            .filter(artifact -> !(artifact instanceof AThreadArtifact))
                            .collect(Collectors.toSet());

            filtered = artifacts.stream()
                    .filter(thread -> setOfNonThreadArtifacts.stream().anyMatch(
                            artifact -> artifact.getThreadArtifact(thread.getIdentifier()) != null && f.apply(artifact)
                    )).collect(Collectors.toList());
        } else
        { // Non thread artifacts
            filtered = artifacts.stream().filter(f::apply).collect(Collectors.toList());
        }
        return filtered;
    }
}
