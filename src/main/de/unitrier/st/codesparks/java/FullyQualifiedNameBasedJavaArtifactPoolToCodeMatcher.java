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
package de.unitrier.st.codesparks.java;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import de.unitrier.st.codesparks.core.matching.AArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.core.matching.ArtifactPoolToCodeMatcherUtil;
import de.unitrier.st.codesparks.core.data.IArtifactPool;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * It is assumed that each artifact is identified by its fully qualified name and stored in
 * the artifact pool correspondingly. That is, getIdentifier() should yield an artifact's fully
 * qualified name and consequently, an artifact can be retrieved (looked up) in the artifact pool by its identifier.
 */
public final class FullyQualifiedNameBasedJavaArtifactPoolToCodeMatcher extends AArtifactPoolToCodeMatcher
{
    /**
     * @param classes The classes extending AArtifact.
     */
    @SafeVarargs
    public FullyQualifiedNameBasedJavaArtifactPoolToCodeMatcher(final Class<? extends AArtifact>... classes)
    {
        super(classes);
    }

    /**
     * All psi-class and psi-method elements from the PSI trees of each given (virtual) file are traversed. Thereby, the fully qualified name of the psi
     * elements are computed.
     * Finally, the artifact pool is queried for a matching artifact, i.e., with the computed fully qualified name (identifier). Moreover, the successors of
     * a method artifact are matched according to their line in the body of the corresponding method.
     *
     * @param artifactPool The artifact pool.
     * @param project      The corresponding project.
     * @param files        The virtual files. Typically, currently opened by the source-code editors.
     * @return A list of matched artifacts. If a psi-class or psi-method element could not be matched, a dummy artifact is instantiated which will be
     * associated with the psi element.
     */
    @Override
    public Collection<AArtifact> matchArtifactsToCodeFiles(
            final IArtifactPool artifactPool,
            final Project project,
            final VirtualFile... files
    )
    {
        final Collection<AArtifact> matchedArtifacts = new ArrayList<>();
        if (artifactPool == null)
        {
            return matchedArtifacts;
        }

        // Find the concrete artifact class that is annotated with 'JavaMethodArtifact'.
        final Class<? extends AArtifact> methodArtifactClass = ArtifactPoolToCodeMatcherUtil
                .findClassWithAnnotation(JavaMethodArtifact.class, artifactClasses);
        // Find the concrete artifact class that is annotated with 'JavaClassArtifact'.
        final Class<? extends AArtifact> classArtifactClass = ArtifactPoolToCodeMatcherUtil
                .findClassWithAnnotation(JavaClassArtifact.class, artifactClasses);
        // Find the concrete artifact class that is annotated with 'JavaPackageArtifact'.
        final Class<? extends AArtifact> packageArtifactClass = ArtifactPoolToCodeMatcherUtil
                .findClassWithAnnotation(JavaPackageArtifact.class, artifactClasses);

        // An instance of 'JavaPsiClassNameHelper' should only be used once per matching procedure because it has an internal state.
        final JavaPsiClassNameHelper javaPsiClassNameHelper = new JavaPsiClassNameHelper();
        final PsiManager psiManager = PsiManager.getInstance(project);
        for (final VirtualFile file : files)
        {
            final PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
                //noinspection UnnecessaryLocalVariable
                final PsiFile thePsiFile = psiManager.findFile(file);
                return thePsiFile;
            });
            // Match methods in the file
            if (methodArtifactClass != null)
            {
                final Collection<PsiMethod> psiMethods = JavaArtifactPoolToCodeMatcherUtil.getJavaMethodsFrom(psiFile);
                // Search the Java psi methods for matching artifacts by identifier (fully qualified name).
                for (final PsiMethod psiMethod : psiMethods)
                {
                    final String fullyQualifiedClassNameOfMethod = ApplicationManager.getApplication().runReadAction(
                            (Computable<String>) () -> javaPsiClassNameHelper.computeFullyQualifiedClassName(psiMethod)
                    );
                    final boolean isConstructor = ApplicationManager.getApplication().runReadAction((Computable<Boolean>) psiMethod::isConstructor);
                    final String name;
                    if (isConstructor)
                    {
                        name = "<init>";
                    } else
                    {
                        name = ApplicationManager.getApplication().runReadAction((Computable<String>) psiMethod::getName);
                    }
                    final String parameters = JavaUtil.computePsiMethodParameterString(psiMethod);
                    String methodIdentifier = JavaUtil.computeMethodIdentifier(name, parameters, fullyQualifiedClassNameOfMethod);

                    AArtifact methodArtifact = artifactPool.getArtifact(methodIdentifier);
                    if (methodArtifact == null)
                    {
                        // Match synthetic methods, i.e. when the first parameter of an inner class is the outer class!
                        int index = fullyQualifiedClassNameOfMethod.lastIndexOf("$");
                        if (index > 0)
                        { // We are facing an inner class
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

                            methodIdentifier = JavaUtil.computeMethodIdentifier(name, strb.toString(), fullyQualifiedClassNameOfMethod);
                            methodArtifact = artifactPool.getArtifact(methodIdentifier);
                            if (methodArtifact == null)
                            {
                                methodArtifact = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(methodArtifactClass, name, methodIdentifier);
                            }
                        } else
                        {
                            methodArtifact = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(methodArtifactClass, name, methodIdentifier);
                        }
                    }
                    assert methodArtifact != null;
                    final PsiParameterList parameterList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiParameterList>) psiMethod::getParameterList);
                    methodArtifact.setVisPsiElement(parameterList);
                    matchedArtifacts.add(methodArtifact);

                    // Match the successors, e.g. callees, of a method artifact; line based in the body of a method.
                    final Map<Integer, List<ANeighborArtifact>> successorMap = methodArtifact.getSuccessors();
                    if (successorMap.isEmpty())
                    {
                        continue;
                    }
                    final PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
                    final Document document = psiDocumentManager.getDocument(psiFile);
                    if (document == null)
                    {
                        continue;
                    }
                    // The successors of a method are assumed to be stored in a hashmap that associates a line in the body of a method
                    // to a list of successors in that line.
                    for (final Map.Entry<Integer, List<ANeighborArtifact>> successorEntry : successorMap.entrySet())
                    {
                        final Integer lineNumber = successorEntry.getKey();
                        int lineStartOffset;
                        try
                        {
                            lineStartOffset = document.getLineStartOffset(lineNumber);
                        } catch (IndexOutOfBoundsException e)
                        {
                            continue;
                        }
                        final PsiElement psiElement = ApplicationManager.getApplication().runReadAction(
                                (Computable<PsiElement>) () -> psiFile.findElementAt(lineStartOffset));
                        assert psiElement != null;
                        final PsiElement sibling = psiElement.getPrevSibling();
                        final PsiElement visPsiElement = sibling != null ? sibling : psiElement;
                        successorEntry.getValue().forEach(neighbor -> neighbor.setVisPsiElement(visPsiElement));
                    }
                }
            }
            // Match classes in the file
            if (classArtifactClass != null)
            {
                final Collection<PsiClass> psiClasses = JavaArtifactPoolToCodeMatcherUtil.getJavaClassesFrom(psiFile);
                for (final PsiClass psiClass : psiClasses)
                {
                    String psiClassQualifiedName =
                            ApplicationManager.getApplication().runReadAction(
                                    (Computable<String>) () -> javaPsiClassNameHelper.computeFullyQualifiedClassName(psiClass)
                            );
                    psiClassQualifiedName = psiClassQualifiedName.replaceAll("[$]", ".");
                    AArtifact classArtifact = artifactPool.getArtifact(psiClassQualifiedName);
                    if (classArtifact == null)
                    {
                        classArtifact = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(classArtifactClass, psiClassQualifiedName);
                    }
                    assert classArtifact != null;
                    final PsiReferenceList implementsList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiReferenceList>) psiClass::getImplementsList);
                    classArtifact.setVisPsiElement(implementsList);
                    matchedArtifacts.add(classArtifact);
                }
            }
            // Match packages in the file
            if (packageArtifactClass != null)
            {
                final Collection<PsiPackageStatement> psiPackageStatements = JavaArtifactPoolToCodeMatcherUtil.getPackageStatementsFrom(psiFile);
                for (final PsiPackageStatement psiPackage : psiPackageStatements)
                {
                    final String packageName = psiPackage.getPackageName();
                    AArtifact packageArtifact = artifactPool.getArtifact(packageName);
                    if (packageArtifact == null)
                    {
                        packageArtifact = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(packageArtifactClass, packageName);
                    }
                    final PsiElement lastChild = psiPackage.getLastChild();
                    packageArtifact.setVisPsiElement(lastChild);
                    matchedArtifacts.add(packageArtifact);
                }
            }
        }
        return matchedArtifacts;
    }
}
