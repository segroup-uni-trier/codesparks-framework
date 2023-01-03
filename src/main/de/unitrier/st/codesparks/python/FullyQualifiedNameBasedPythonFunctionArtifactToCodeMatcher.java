package java_code.core.matching;

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
import de.unitrier.st.insituprofiling.core.IProfilingResult;
import de.unitrier.st.insituprofiling.core.IProfilingResultToCodeMatcher;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import java_code.core.PythonProfilingFunction;

import java.util.*;

/**
 * @author Lucas Kreber, 2019
 * @author Oliver Moseler, 2023
 */
public class FullyQualifiedNameBasedPythonFunctionArtifactToCodeMatcher implements IProfilingResultToCodeMatcher
{
    @Override
    public Collection<AProfilingArtifact> matchResultsToCodeFiles(
            final IProfilingResult profilingResult,
            final Project project,
            final VirtualFile... files
    )
    {
        final Collection<AProfilingArtifact> matchingResults = new ArrayList<>();
        if (profilingResult == null)
        {
            return matchingResults;
        }

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
                AProfilingArtifact artifact = profilingResult.getArtifact(identifier);
                if (artifact == null)
                {
                    artifact = new PythonProfilingFunction(pyFunctionName, identifier);
                }
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
     * @param filePath The path of the file (module) in which the function is defined.
     * @param lineNumber The corresponding line number of the function in the file.
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
