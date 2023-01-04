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
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;

import java.lang.annotation.Annotation;

public final class JavaUtil
{
    private static final String[] primitiveDataTypeNames = new String[]
            {"byte", "short", "int", "long", "float", "double", "boolean", "char"};

    private JavaUtil() {}

    public static String checkAndReplaceConstructorName(final String methodString)
    {
        final int index = methodString.indexOf("<");
        if (index < 0)
        {
            return methodString;
        }
        int indexOfBrace = methodString.indexOf("(");
        String withoutParameters = methodString.substring(0, indexOfBrace);
        int lastIndexOfDot = withoutParameters.lastIndexOf(".");
        String className = withoutParameters.substring(0, lastIndexOfDot);
        int indexToConstructorName;
        int classLastIndexOfDot = className.lastIndexOf(".");
        if (classLastIndexOfDot < 0)
        {
            int indexOfPercent = className.indexOf("%");
            indexToConstructorName = indexOfPercent + 1;
        } else
        {
            indexToConstructorName = classLastIndexOfDot + 1;
        }
        String constructorName = className.substring(indexToConstructorName).trim();
        int indexClose = methodString.indexOf(">");
        String toReplace = methodString.substring(index, indexClose + 1);
        int indexOfInnerClassDollar = constructorName.indexOf("$");
        if (indexOfInnerClassDollar > 0)
        {
            constructorName = constructorName.substring(indexOfInnerClassDollar + 1);
        }
        return methodString.replaceFirst(toReplace, constructorName);
    }

    public static String computeMethodIdentifier(final String name, final String parameters, final String fullyQualifiedClassName)
    {
        String identifier = String.format("%s.%s%s", fullyQualifiedClassName, name, parameters);
        return checkInnerClassConstructorsFirstParameter(identifier);
    }

    public static String checkInnerClassConstructorsFirstParameter(final String identifier)
    {
        int braceIndex = identifier.indexOf("(");
        String classAndMethodName = identifier.substring(0, braceIndex);
        int lastIndexOfDot = classAndMethodName.lastIndexOf(".");
        String fullyQualifiedClassName = classAndMethodName.substring(0, lastIndexOfDot);
        if (fullyQualifiedClassName.contains("$"))
        {
            int indexOfDollar = fullyQualifiedClassName.indexOf("$");
            String outerClass = fullyQualifiedClassName.substring(0, indexOfDollar);
            String innerClass = fullyQualifiedClassName.substring(indexOfDollar + 1);
            String methodName = classAndMethodName.substring(lastIndexOfDot + 1);
            String parameters = identifier.substring(braceIndex);
            if (innerClass.equals(methodName))
            {// Constructor, so check if the first parameter has the same type as the outer class
                String[] parameterTypes = parameters.split(",");
                if (parameterTypes.length > 0)
                {
                    String firstParameterType = parameterTypes[0].substring(1).trim();
                    if (firstParameterType.equals(outerClass))
                    {// First parameter is of type of the outer class so remove it
                        StringBuilder stringBuilder = new StringBuilder("(");
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
            return String.format("%s.%s%s", fullyQualifiedClassName, methodName, parameters);
        }
        return identifier;
    }

    public static String computePsiMethodParameterString(final PsiMethod method)
    {
        final StringBuilder psiParameterBuilder = new StringBuilder("(");
        final PsiParameter[] psiParameters =
                ApplicationManager.getApplication().runReadAction((Computable<PsiParameter[]>) () -> method.getParameterList().getParameters());
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        assert project != null;
        for (final PsiParameter parameter : psiParameters)
        {
            String type = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
                final PsiType parameterType = parameter.getType();
                String parameterTypeCanonicalText = parameterType.getCanonicalText();
                if (!parameterTypeCanonicalText.contains("."))
                {
                    for (final String typeName : primitiveDataTypeNames)
                    {
                        if (parameterTypeCanonicalText.equals(typeName))
                        {
                            return parameterTypeCanonicalText;
                        }
                    }
                    final PsiType[] superTypes = parameterType.getSuperTypes();
                    if (superTypes.length == 0)
                    {
                        return parameterTypeCanonicalText;
                    }
                    return superTypes[0].getCanonicalText();
                } else
                {
                    if (parameterType.getArrayDimensions() > 0)
                    { // Is an array type!
                        return parameterTypeCanonicalText;
                    }
                    final PsiFile containingFile = parameter.getContainingFile();

                    final String packageName = ((PsiJavaFile) containingFile).getPackageName();
                    final String className = containingFile.getName().replaceAll(".(java|class)", "");

                    final String containingClassType = packageName + "." + className;

                    if (!parameterTypeCanonicalText.startsWith(containingClassType)
                            || parameterTypeCanonicalText.equals(containingClassType))
                    {
                        return parameterTypeCanonicalText;
                    }

                    // The parameter is of type of inner class!
                    String presentableText = parameterType.getPresentableText();
                    final StringBuilder strb = new StringBuilder(parameterTypeCanonicalText);
                    while (!className.equals(presentableText))
                    {
                        int index = parameterTypeCanonicalText.lastIndexOf(".");
                        if (index < 0) break;
                        strb.setCharAt(index, '$');
                        parameterTypeCanonicalText = strb.toString();
                        presentableText = parameterTypeCanonicalText.substring(parameterTypeCanonicalText.lastIndexOf(".") + 1,
                                parameterTypeCanonicalText.lastIndexOf("$"));
                    }
                }
                return parameterTypeCanonicalText;
            });
            if (type.contains("<"))
            {
                type = type.substring(0, type.indexOf('<'));
            }
            psiParameterBuilder.append(type);
            psiParameterBuilder.append(", ");
        }
        final int len = psiParameterBuilder.length();
        if (len > 1)
        {
            psiParameterBuilder.delete(len - 2, len);
        }
        psiParameterBuilder.append(")");
        return psiParameterBuilder.toString();
    }

    public static boolean isClassArtifact(final AArtifact artifact)
    {
        return isArtifactOfType(artifact, JavaClassArtifact.class);
    }

    public static boolean isMethodArtifact(final AArtifact artifact)
    {
        return isArtifactOfType(artifact, JavaMethodArtifact.class);
    }

    public static boolean isArtifactOfType(final AArtifact artifact, final Class<? extends Annotation> annotationType)
    {
        final Class<? extends AArtifact> artifactClass = artifact.getClass();
        //noinspection UnnecessaryLocalVariable
        final boolean isArtifactOfType = artifactClass.isAnnotationPresent(annotationType);
        return isArtifactOfType;
    }
}
