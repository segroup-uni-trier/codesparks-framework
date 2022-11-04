/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.st.codesparks.java;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.CoreUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public final class JavaArtifactNavigationUtil
{
    private JavaArtifactNavigationUtil() {}

    /**
     * Only for class internal use. Try to navigate to the psi element given as parameter.
     * If this is not possible, find the next parent that is navigatable and try to navigate to that psi element.
     *
     * @param elementAt The psi element to navigate to.
     */
    private static boolean navigateToNextNavigatablePsi(final PsiElement elementAt)
    {
        if (elementAt instanceof NavigatablePsiElement)
        {
            ((NavigatablePsiElement) elementAt).navigate(true);
        } else
        {
            final NavigatablePsiElement navigatablePsiElement = PsiTreeUtil.getParentOfType(elementAt, NavigatablePsiElement.class);
            if (navigatablePsiElement == null)
            {
                return false;
            }
            // assert navigatablePsiElement != null;
            navigatablePsiElement.navigate(true);
        }
        return true;
    }

    /**
     * @param methodIdentifier Fully qualified name of the method.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToMethod(final String methodIdentifier)
    {
        if ("selftime".equals(methodIdentifier) || "self-time".equals(methodIdentifier))
        {
            return false;
        }
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final String mMethodIdentifier = JavaUtil.checkAndReplaceConstructorName(methodIdentifier);
        final int braceIndex = mMethodIdentifier.indexOf("(");
        if (braceIndex < 0)
        { // No method
            return false;
        }
        final String classAndMethodName = mMethodIdentifier.substring(0, braceIndex);
        final int lastIndexOfDot = classAndMethodName.lastIndexOf(".");
        final String fullyQualifiedClassName = classAndMethodName.substring(0, lastIndexOfDot);
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        if (fullyQualifiedClassName.contains("$"))
        {
            final int indexOfDollar = fullyQualifiedClassName.indexOf("$");
            final String outerClass = fullyQualifiedClassName.substring(0, indexOfDollar);
            final String innerClass = fullyQualifiedClassName.substring(indexOfDollar + 1);
            final PsiClass psiClass = javaPsiFacade.findClass(outerClass, GlobalSearchScope.allScope(project));
            if (psiClass == null)
            {
                return false;
            }
            final String methodName = classAndMethodName.substring(lastIndexOfDot + 1);
            String parameters = mMethodIdentifier.substring(braceIndex);
            if (innerClass.equals(methodName))
            {// Constructor, so check if the first parameter has the same type as the outer class
                final String[] parameterTypes = parameters.split(",");
                if (parameterTypes.length > 0)
                {
                    final String firstParameterType = parameterTypes[0].substring(1).trim();
                    if (firstParameterType.equals(outerClass))
                    {// First parameter is of type of the outer class so remove it
                        final StringBuilder stringBuilder = new StringBuilder("(");
                        for (int i = 1; i < parameterTypes.length; i++) // Start with index 1 so that the first
                        // parameter will be removed
                        {
                            stringBuilder.append(parameterTypes[i].trim());
                            stringBuilder.append(", ");
                        }
                        if (stringBuilder.length() > 1)
                        {
                            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                        }
                        parameters = stringBuilder.toString();
                    }
                }
            }
            final Collection<PsiMethod> psiMethods = PsiTreeUtil.findChildrenOfType(psiClass, PsiMethod.class);
            final JavaPsiClassNameHelper javaPsiClassNameHelper = new JavaPsiClassNameHelper();
            for (final PsiMethod psiMethod : psiMethods)
            {
                // TODO: there is a side effect in the computeFullyQualifiedClassName method so that it is necessary to compute the class
                //  name of all methods since the anonymous class indices are wrong otherwise (they won't count up correctly)!
                final String className = javaPsiClassNameHelper.computeFullyQualifiedClassName(psiMethod); // Do not move this statement after the
                // 'if' because of the side effect mentioned in the above TODO
                final String psiMethodName = psiMethod.getName();
                if (!psiMethodName.equals(methodName))
                {
                    continue;
                }
                if (!className.equals(fullyQualifiedClassName))
                {
                    continue;
                }
                final String psiParameterString = JavaUtil.computePsiMethodParameterString(psiMethod);
                if (psiParameterString.equals(parameters))
                {
                    psiMethod.navigate(true);
//                    return true;
                    break;
                }
            }
        } else
        {
            final PsiClass psiClass = javaPsiFacade.findClass(fullyQualifiedClassName, GlobalSearchScope.allScope(project));
            if (psiClass == null)
            {
                return false;
            }
            final String methodName = classAndMethodName.substring(lastIndexOfDot + 1);
            final PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
            if (methods.length < 1)
            {
                // For example if you want to navigate to a default constructor which doesn't exist in the code!
                psiClass.navigate(true);
                return true;
            }
            final String parameters = mMethodIdentifier.substring(braceIndex);
            boolean methodFound = false;
            for (final PsiMethod psiMethod : methods)
            {
                final String psiParameterString = JavaUtil.computePsiMethodParameterString(psiMethod);
                if (!psiParameterString.equals(parameters))
                {
                    continue;
                }
                psiMethod.navigate(true);
                methodFound = true;
                break;
            }
            if (!methodFound)
            { // If the method could not be found go to its class instead
                psiClass.navigate(true);
                //return true;
            }
        }
        return true;
    }

    /**
     * @param clazzIdentifier The fully qualified class name.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToClass(final String clazzIdentifier)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return false;
        }
        psiClass.navigate(true);
        return true;
    }

    /**
     * @param fileName   The canonical path of the Java file.
     * @param lineNumber The line number within the Java file.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToLineInFile(final String fileName, final int lineNumber)
    {
        final VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        final Path path = Paths.get(fileName);
        final VirtualFile virtualFile = virtualFileManager.findFileByNioPath(path);
        if (virtualFile == null)
        {
            return false;
        }
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final PsiManager psiManager = PsiManager.getInstance(project);
        final PsiFile psiFile = psiManager.findFile(virtualFile);
        if (psiFile == null)
        {
            return false;
        }
        final PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        final Document document = psiDocumentManager.getDocument(psiFile);
        if (document == null)
        {
            return false;
        }
        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return false;
        }
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final PsiElement psiElement = ApplicationManager.getApplication().runReadAction(
                (Computable<PsiElement>) () -> psiFile.findElementAt(lineStartOffset)
        );
        return navigateToNextNavigatablePsi(psiElement);
    }

    /**
     * @param clazzIdentifier The fully qualified class name.
     * @param lineNumber      The line number within the Java class.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToLineInClass(final String clazzIdentifier, final int lineNumber)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return false;
        }
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final PsiFile containingFile = psiClass.getContainingFile();
        final Document document = documentManager.getDocument(containingFile);
        if (document == null)
        {
            return false;
        }
        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return false;
        }
        final int lineOffset = document.getLineStartOffset(lineNumber - 2);
        final PsiElement elementAt = psiClass.findElementAt(lineOffset);
        return navigateToNextNavigatablePsi(elementAt);
    }

    /**
     * @param clazzIdentifier The fully qualified name of the class in which the lambda is defined.
     * @param lineNumber      The line number that is somewhere in the body of the searched lambda expression.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToLambdaInClassFromLine(final String clazzIdentifier, final int lineNumber)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return false;
        }
        final PsiFile containingFile = psiClass.getContainingFile();
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final Document document = documentManager.getDocument(containingFile);
        if (document == null)
        {
            return false;
        }
        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return false;
        }
        final int lineOffset = document.getLineStartOffset(lineNumber - 2);
        final PsiElement elementAt = psiClass.findElementAt(lineOffset);
        final PsiNewExpression newThreadExpression = PsiTreeUtil.getParentOfType(elementAt, PsiNewExpression.class);
        if (newThreadExpression == null)
        {
            return false;
        }
        final PsiType type = newThreadExpression.getType();
        if (type == null)
        {
            return false;
        }
        final String canonicalText = type.getCanonicalText();
        if ("java.lang.Thread".equals(canonicalText))
        {
            final PsiLambdaExpression lambdaExpression = PsiTreeUtil.findChildOfType(newThreadExpression, PsiLambdaExpression.class);
            if (lambdaExpression == null)
            {
                return false;
            }
            lambdaExpression.navigate(true);
        }
        return true;
    }
}
