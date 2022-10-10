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
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.IArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Oliver Moseler
 */
public final class JavaArtifactPoolToCodeMatcher implements IArtifactPoolToCodeMatcher
{
    private final Class<? extends AArtifact>[] artifactClasses;

    /**
     * @param classes The classes extending AArtifact.
     */
    @SafeVarargs
    public JavaArtifactPoolToCodeMatcher(final Class<? extends AArtifact>... classes)
    {
        this.artifactClasses = classes;
    }

    private Collection<PsiClass> getClassesFrom(PsiFile psiFile)
    {
        return ApplicationManager.getApplication().runReadAction(
                (Computable<Collection<PsiClass>>) () -> {
                    Collection<PsiClass> psiClasses = PsiTreeUtil.findChildrenOfType(psiFile, PsiClass.class);
                    psiClasses.removeIf(psiClass -> psiClass instanceof PsiAnonymousClass || psiClass instanceof
                            PsiTypeParameter);
                    return psiClasses;
                });
    }

    private Collection<PsiMethod> getMethodsFrom(PsiFile psiFile)
    {
        return ApplicationManager.getApplication().runReadAction(
                (Computable<Collection<PsiMethod>>) () -> PsiTreeUtil.findChildrenOfType(psiFile, PsiMethod.class)
        );
    }

    private AArtifact instantiateArtifact(
            final Class<? extends AArtifact> artifactClass
            , final String... constructorParameters
    )
    {
        AArtifact artifact = null;
        Constructor<?>[] constructors = artifactClass.getConstructors();
        Optional<Constructor<?>> first =
                Arrays.stream(constructors).
                        filter(constructor -> constructor.getParameterCount() == constructorParameters.length)
                        .findFirst();
        if (first.isPresent())
        {
            Constructor<?> constructor = first.get();
            try
            {
                artifact = (AArtifact) constructor.newInstance((Object[]) constructorParameters);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        return artifact;
    }

    @SafeVarargs
    private Class<? extends AArtifact> findClassWithAnnotation(
            final Class<? extends Annotation> annotation
            , final Class<? extends AArtifact>... artifactClasses
    )
    {
        if (artifactClasses == null)
        {
            return null;
        }
        Optional<Class<? extends AArtifact>> first =
                Arrays.stream(artifactClasses)
                        .filter(aClass -> aClass.isAnnotationPresent(annotation))
                        .findFirst();
        return first.orElse(null);
    }

    @Override
    public Collection<AArtifact> matchArtifactsToCodeFiles(
            final IArtifactPool artifactPool
            , final Project project
            , final VirtualFile... files
    )
    {
        Collection<AArtifact> matchedProfilingResults = new ArrayList<>();
        if (artifactPool == null)
        {
            return matchedProfilingResults;
        }

        Class<? extends AArtifact> methodArtifactClass = findClassWithAnnotation(JavaMethodArtifact.class, artifactClasses);
        Class<? extends AArtifact> classArtifactClass = findClassWithAnnotation(JavaClassArtifact.class, artifactClasses);

        if (methodArtifactClass == null && classArtifactClass == null)
        {
            return matchedProfilingResults;
        }

        final JavaPsiClassNameHelper javaPsiClassNameHelper = new JavaPsiClassNameHelper();
        for (VirtualFile file : files)
        {
            PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
                PsiManager psiManager = PsiManager.getInstance(project);
                return psiManager.findFile(file);
            });
            // Match methods
            if (methodArtifactClass != null)
            {
                Collection<PsiMethod> psiMethods = getMethodsFrom(psiFile);
                for (PsiMethod psiMethod : psiMethods)
                {
                    String fullyQualifiedClassNameOfMethod = ApplicationManager.getApplication().runReadAction(
                            (Computable<String>) () -> javaPsiClassNameHelper.computeFullyQualifiedClassName(psiMethod));
                    String name = ApplicationManager.getApplication().runReadAction((Computable<String>) psiMethod::getName);
                    Boolean isConstructor =
                            ApplicationManager.getApplication().runReadAction((Computable<Boolean>) psiMethod::isConstructor);
                    if (isConstructor)
                    {
                        name = "<init>";
                    }
                    String parameters = JavaUtil.computePsiMethodParameterString(psiMethod);
                    String identifier = JavaUtil.computeMethodIdentifier(name, parameters,
                            fullyQualifiedClassNameOfMethod);

                    AArtifact profilingMethod = artifactPool.getArtifact(identifier);
                    if (profilingMethod == null)
                    {
                        // Match synthetic methods, i.e. when the first parameter of an inner class is the outer class!
                        int index = fullyQualifiedClassNameOfMethod.lastIndexOf("$");
                        if (index > 0)
                        { // We are concerned with an inner class
                            String outerClass = fullyQualifiedClassNameOfMethod.substring(0, index);

                            StringBuilder strb = new StringBuilder(parameters);
                            String append;
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
                                profilingMethod = instantiateArtifact(methodArtifactClass, name, identifier);
                            }
                        } else
                        {
                            profilingMethod = instantiateArtifact(methodArtifactClass, name, identifier);
                        }
                    }
                    assert profilingMethod != null;
                    PsiParameterList parameterList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiParameterList>) psiMethod::getParameterList);
                    profilingMethod.setVisPsiElement(parameterList);
                    matchedProfilingResults.add(profilingMethod);

                    // Match method invocations
                    Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
                    if (document == null)
                    {
                        continue;
                    }
                    Map<Integer, List<ANeighborArtifact>> successors = profilingMethod.getSuccessors();
                    for (Map.Entry<Integer, List<ANeighborArtifact>> successorEntry : successors.entrySet())
                    {
                        int lineStartOffset;
                        try
                        {
                            lineStartOffset = document.getLineStartOffset(successorEntry.getKey());
                        } catch (IndexOutOfBoundsException e)
                        {
                            continue;
                        }
                        PsiElement psiElement = ApplicationManager.getApplication().runReadAction(
                                (Computable<PsiElement>) () -> psiFile.findElementAt(lineStartOffset));
                        assert psiElement != null;
                        PsiElement sibling = psiElement.getPrevSibling();
                        successorEntry.getValue()
                                .forEach(neighbor -> neighbor.setVisPsiElement(sibling != null ? sibling : psiElement));//setInvocationLineElement(sibling !=
                        // null ? sibling : psiElement));
                    }
                }
            }
            // Match classes
            if (classArtifactClass != null)
            {
                Collection<PsiClass> psiClasses = getClassesFrom(psiFile);
                for (PsiClass psiClass : psiClasses)
                {
                    String psiClassQualifiedName =
                            ApplicationManager.getApplication().runReadAction(
                                    (Computable<String>) () -> javaPsiClassNameHelper.computeFullyQualifiedClassName(psiClass));

                    psiClassQualifiedName = psiClassQualifiedName.replaceAll("[$]", ".");

                    AArtifact profilingClass = artifactPool.getArtifact(psiClassQualifiedName);


                    if (profilingClass == null)
                    {
                        profilingClass = instantiateArtifact(classArtifactClass, psiClassQualifiedName);
                    }
                    assert profilingClass != null;
                    PsiReferenceList implementsList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiReferenceList>) psiClass::getImplementsList);
                    profilingClass.setVisPsiElement(implementsList);
                    matchedProfilingResults.add(profilingClass);
                }
            }
        }
        return matchedProfilingResults;
    }
}
