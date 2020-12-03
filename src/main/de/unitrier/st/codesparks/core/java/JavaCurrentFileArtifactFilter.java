package de.unitrier.st.codesparks.core.java;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.overview.ICurrentFileArtifactFilter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
    public Collection<AArtifact> filterArtifact(final Collection<? extends AArtifact> artifacts, final PsiFile psiFile)
    {
        final Collection<PsiClass> childrenOfType = PsiTreeUtil.findChildrenOfType(psiFile, PsiClass.class);
        final Set<String> qualifiedNames = new HashSet<>();
        for (PsiClass psiClass : childrenOfType)
        {
            final String qualifiedName = psiClass.getQualifiedName();
            if (qualifiedName == null || qualifiedName.equals(""))
            {
                continue;
            }
            qualifiedNames.add(qualifiedName);
        }

        @SuppressWarnings("UnnecessaryLocalVariable") // Not inlined due to debugging reasons, i.e to be able to set a line breakpoint and inspect the value
        // of variable 'collect'!
        Collection<AArtifact> collect = artifacts.stream()
                .filter(artifact -> qualifiedNames.stream()
                        .anyMatch(qualifiedName -> artifactStringFunc.apply(artifact).equals(qualifiedName) // Java classes
                                || (artifactStringFunc.apply(artifact).toLowerCase().startsWith(qualifiedName.toLowerCase() + ".")
                                || artifactStringFunc.apply(artifact).toLowerCase().startsWith(qualifiedName.toLowerCase() + "$"))))
                .collect(Collectors.toList());

        return collect;
    }
}
