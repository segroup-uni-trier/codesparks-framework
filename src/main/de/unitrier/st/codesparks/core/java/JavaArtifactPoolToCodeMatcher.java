/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.java;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import de.unitrier.st.codesparks.core.AArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.core.ArtifactPoolToCodeMatcherUtil;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Oliver Moseler
 */
public final class JavaArtifactPoolToCodeMatcher extends AArtifactPoolToCodeMatcher
{
    /**
     * @param classes The classes extending AArtifact.
     */
    @SafeVarargs
    public JavaArtifactPoolToCodeMatcher(final Class<? extends AArtifact>... classes)
    {
        super(classes);
    }

    @Override
    public Collection<AArtifact> matchArtifactsToCodeFiles(
            final IArtifactPool artifactPool
            , final Project project
            , final VirtualFile... files
    )
    {
        final Collection<AArtifact> matchedProfilingResults = new ArrayList<>();
        if (artifactPool == null)
        {
            return matchedProfilingResults;
        }

        final Class<? extends AArtifact> methodArtifactClass = ArtifactPoolToCodeMatcherUtil.findClassWithAnnotation(JavaMethodArtifact.class, artifactClasses);
        final Class<? extends AArtifact> classArtifactClass = ArtifactPoolToCodeMatcherUtil.findClassWithAnnotation(JavaClassArtifact.class, artifactClasses);

        if (methodArtifactClass == null && classArtifactClass == null)
        {
            return matchedProfilingResults;
        }

        final JavaPsiClassNameHelper javaPsiClassNameHelper = new JavaPsiClassNameHelper();
        for (final VirtualFile file : files)
        {
            final PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
                final PsiManager psiManager = PsiManager.getInstance(project);
                return psiManager.findFile(file);
            });
            // Match methods
            if (methodArtifactClass != null)
            {
                final Collection<PsiMethod> psiMethods = JavaArtifactPoolToCodeMatcherUtil.getJavaMethodsFrom(psiFile);
                // Search the Java psi methods for matching artifacts
                for (final PsiMethod psiMethod : psiMethods)
                {
                    final String fullyQualifiedClassNameOfMethod = ApplicationManager.getApplication().runReadAction(
                            (Computable<String>) () -> javaPsiClassNameHelper.computeFullyQualifiedClassName(psiMethod));
                    final Boolean isConstructor = ApplicationManager.getApplication().runReadAction((Computable<Boolean>) psiMethod::isConstructor);
                    final String name;
                    if (isConstructor)
                    {
                        name = "<init>";
                    } else
                    {
                        name = ApplicationManager.getApplication().runReadAction((Computable<String>) psiMethod::getName);
                    }
                    final String parameters = JavaUtil.computePsiMethodParameterString(psiMethod);
                    String identifier = JavaUtil.computeMethodIdentifier(name, parameters, fullyQualifiedClassNameOfMethod);

                    AArtifact profilingMethod = artifactPool.getArtifact(identifier);
                    if (profilingMethod == null)
                    {
                        // Match synthetic methods, i.e. when the first parameter of an inner class is the outer class!
                        int index = fullyQualifiedClassNameOfMethod.lastIndexOf("$");
                        if (index > 0)
                        { // We are concerned with an inner class
                            final String outerClass = fullyQualifiedClassNameOfMethod.substring(0, index);

                            final StringBuilder strb = new StringBuilder(parameters);
                            final String append;
                            if (parameters.length() > 2)
                            { // there are more parameters
                                append = ", ";
                            } else
                            {
                                append = "";
                            }
                            strb.insert(1, outerClass + append);

                            identifier = JavaUtil.computeMethodIdentifier(name, strb.toString(), fullyQualifiedClassNameOfMethod);
                            profilingMethod = artifactPool.getArtifact(identifier);
                            if (profilingMethod == null)
                            {
                                profilingMethod = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(methodArtifactClass, name, identifier);
                            }
                        } else
                        {
                            profilingMethod = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(methodArtifactClass, name, identifier);
                        }
                    }
                    assert profilingMethod != null;
                    final PsiParameterList parameterList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiParameterList>) psiMethod::getParameterList);
                    profilingMethod.setVisPsiElement(parameterList);
                    matchedProfilingResults.add(profilingMethod);

                    // Match the callees of a method artifact; line based in the body of a method
                    final Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
                    if (document == null)
                    {
                        continue;
                    }
                    final Map<Integer, List<ANeighborArtifact>> successors = profilingMethod.getSuccessors(); // The callees
                    for (final Map.Entry<Integer, List<ANeighborArtifact>> successorEntry : successors.entrySet())
                    {
                        int lineStartOffset;
                        try
                        {
                            lineStartOffset = document.getLineStartOffset(successorEntry.getKey());
                        } catch (IndexOutOfBoundsException e)
                        {
                            continue;
                        }
                        final PsiElement psiElement = ApplicationManager.getApplication().runReadAction(
                                (Computable<PsiElement>) () -> psiFile.findElementAt(lineStartOffset));
                        assert psiElement != null;
                        final PsiElement sibling = psiElement.getPrevSibling();
                        successorEntry.getValue()
                                .forEach(neighbor -> neighbor.setVisPsiElement(sibling != null ? sibling : psiElement));//setInvocationLineElement(sibling !=
                        // null ? sibling : psiElement));
                    }
                }
            }
            // Match classes
            if (classArtifactClass != null)
            {
                final Collection<PsiClass> psiClasses = JavaArtifactPoolToCodeMatcherUtil.getJavaClassesFrom(psiFile);
                for (final PsiClass psiClass : psiClasses)
                {
                    String psiClassQualifiedName =
                            ApplicationManager.getApplication().runReadAction(
                                    (Computable<String>) () -> javaPsiClassNameHelper.computeFullyQualifiedClassName(psiClass));
                    psiClassQualifiedName = psiClassQualifiedName.replaceAll("[$]", ".");
                    AArtifact profilingClass = artifactPool.getArtifact(psiClassQualifiedName);
                    if (profilingClass == null)
                    {
                        profilingClass = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(classArtifactClass, psiClassQualifiedName);
                    }
                    assert profilingClass != null;
                    final PsiReferenceList implementsList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiReferenceList>) psiClass::getImplementsList);
                    profilingClass.setVisPsiElement(implementsList);
                    matchedProfilingResults.add(profilingClass);
                }
            }
        }
        return matchedProfilingResults;
    }
}
