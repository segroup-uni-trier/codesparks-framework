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
package de.unitrier.st.codesparks.python;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.IArtifactPool;
import de.unitrier.st.codesparks.core.matching.AArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.core.matching.ArtifactPoolToCodeMatcherUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FullyQualifiedNameBasedPythonFunctionArtifactToCodeMatcher extends AArtifactPoolToCodeMatcher
{
    /**
     * @param classes The classes extending AArtifact.
     */
    @SafeVarargs
    public FullyQualifiedNameBasedPythonFunctionArtifactToCodeMatcher(final Class<? extends AArtifact>... classes)
    {
        super(classes);
    }

    @Override
    public Collection<AArtifact> matchArtifactsToCodeFiles(
            final IArtifactPool artifactPool,
            final Project project,
            final VirtualFile... files
    )
    {
        final Collection<AArtifact> matchingResults = new ArrayList<>();
        if (artifactPool == null)
        {
            return matchingResults;
        }

        final Class<? extends AArtifact> pythonFunctionArtifactClass = ArtifactPoolToCodeMatcherUtil
                .findClassWithAnnotation(PythonFunctionArtifact.class, artifactClasses);

        final PsiManager psiManager = PsiManager.getInstance(project);

        for (final VirtualFile file : files)
        {
            final PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () ->
                    psiManager.findFile(file)
            );

            final Collection<PyClass> pyClasses = PsiTreeUtil.findChildrenOfType(psiFile, PyClass.class);
            final Collection<PyFunction> pyFunctionsOfClasses = collectPyFunctions(pyClasses);

            final Collection<PyFunction> pyFunctions = PsiTreeUtil.findChildrenOfType(psiFile, PyFunction.class);
            pyFunctions.addAll(pyFunctionsOfClasses);

            final String psiFileText = psiFile.getText();
            final String filePath = file.getPath();

            for (final PyFunction pyFunction : pyFunctions)
            {
                final int pyFunctionTextOffset = pyFunction.getTextOffset();
                int lineNumber = StringUtil.offsetToLineNumber(psiFileText, pyFunctionTextOffset);
                final String pyFunctionName = pyFunction.getName();
                final String identifier = getPythonFunctionArtifactIdentifier(filePath, lineNumber, pyFunctionName);
                AArtifact artifact = artifactPool.getArtifact(identifier);
                if (artifact == null)
                {
                    // Note, the following function call assumes that there exists a constructor with exactly two parameters.
                    // Here, 'pyFunctionName' and 'identifier'.
                    artifact = ArtifactPoolToCodeMatcherUtil.instantiateArtifact(pythonFunctionArtifactClass, identifier, pyFunctionName);
                }
                // Alternatively:
                // final AArtifact artifactPoolOrCreateArtifact = artifactPool.getOrCreateArtifact(pythonFunctionArtifactClass, identifier, pyFunctionName);
                final PsiElement firstChild = pyFunction.getFirstChild();
                artifact.setVisPsiElement(firstChild);
                matchingResults.add(artifact);
            }
        }

        return matchingResults;
    }

    private static Collection<PyFunction> collectPyFunctions(final Collection<PyClass> pyClasses)
    {
        final List<PyFunction> pyFunctions = new ArrayList<>();
        for (final PyClass pyClass : pyClasses)
        {
            final PyFunction[] pyMethods = pyClass.getMethods();
            pyFunctions.addAll(Arrays.asList(pyMethods));
            final PyClass[] nestedPyClasses = pyClass.getNestedClasses();
            final Collection<PyFunction> nestedPyFunctions = collectPyFunctions(Arrays.asList(nestedPyClasses));
            pyFunctions.addAll(nestedPyFunctions);
        }
        return pyFunctions;
    }

    /**
     * Compute the fully qualified name (identifier) of a Python function. A python function is unambiguously identified by its full module and function
     * name. In addition, the corresponding line number is used. Otherwise, the parameters must match.
     * <p>
     * Note, extend this class and override this method if a different identifier format is required, e.g., using '__qualname__' of Python functions.
     *
     * @param filePath       The path of the file (module) in which the function is defined.
     * @param lineNumber     The corresponding line number of the function in the file.
     * @param pyFunctionName The name of the Pythin function.
     * @return A string representing the fully qualified name of a python function with respect to the given parameters.
     */
    protected String getPythonFunctionArtifactIdentifier(
            final String filePath,
            final int lineNumber,
            final String pyFunctionName
    )
    {
        //noinspection StringBufferReplaceableByString
        final StringBuilder strb = new StringBuilder();
        strb.append(filePath);
        strb.append(":");
        strb.append(lineNumber + 1);
        strb.append(" ");
        strb.append(pyFunctionName);
        return strb.toString();
    }

}
