/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.java;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import de.unitrier.st.codesparks.core.CoreUtil;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class JavaUtil
{
    private static final String[] primitiveDataTypeNames = new String[]
            {"byte", "short", "int", "long", "float", "double", "boolean", "char"};

    private JavaUtil() {}

    public static String checkAndReplaceConstructorName(final String methodString)
    {
        int index = methodString.indexOf("<");
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

    private static int appendArrayBraces(final StringBuilder strb, int nr)
    {
        while (nr > 0)
        {
            strb.append("[]");
            nr--;
        }
        return nr;
    }

    public static String retrieveMethodSignature(final Element method)
    {
        String methodSig = method.getAttributeValue("signature");
        int index = methodSig.lastIndexOf(')');

        if (index > 0)
        { // Cut off return value
            methodSig = methodSig.substring(0, index + 1);
        }
        //methodSig = methodSig.replaceAll("I", "int");
        methodSig = methodSig.replaceAll("/", ".");


        char[] chars = methodSig.toCharArray();
        int numberOfBraces = 0;

        StringBuilder strb = new StringBuilder();

        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];
            switch (c)
            {
                case '(':
                    strb.append(c);
                    break;
                case (')'):
                    if (strb.length() > 1)
                    {
                        strb.delete(strb.length() - 2, strb.length()); // cut off last parameter comma
                    }
                    strb.append(c);
                    break;
                case ('L'):
                    int startOverIndex = methodSig.indexOf(';', i);
                    String referenceType = methodSig.substring(i + 1, startOverIndex);
                    strb.append(referenceType);
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    i = startOverIndex; // Skip the rest of the reference type information
                    break;
                case ('I'):
                    strb.append("int");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('Z'):
                    strb.append("boolean");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('J'):
                    strb.append("long");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('B'):
                    strb.append("byte");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('C'):
                    strb.append("char");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('D'):
                    strb.append("double");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('F'):
                    strb.append("float");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('S'):
                    strb.append("short");
                    numberOfBraces = appendArrayBraces(strb, numberOfBraces);
                    strb.append(", ");
                    break;
                case ('['):
                    numberOfBraces++;
                    break;
                default:
                    break;
            }
        }
        return strb.toString();
    }

    public static String retrieveFullyQualifiedClassName(final Element method)
    {
        String fullyQualifiedClassName = method.getAttributeValue("class");
        fullyQualifiedClassName = fullyQualifiedClassName.substring(1, fullyQualifiedClassName.length() - 1);
        fullyQualifiedClassName = fullyQualifiedClassName.replaceAll("/", ".");
        return fullyQualifiedClassName;
    }

    public static String retrieveMethodName(final Element method)
    {
        if (method == null)
        {
            return "";
        }
        final String text = method.getText();
        if (text == null)
        {
            return "";
        }
        return text;
    }

    public static String computeMethodIdentifier(final String name, final String parameters, final String fullyQualifiedClassName)
    {
        String identifier = String.format("%s.%s%s", fullyQualifiedClassName, name, parameters);
        return checkInnerClassConstructorsFirstParameter(identifier);
    }

    public static String computeMethodIdentifier(final Element methodElement)
    {
        String methodSignature = JavaUtil.retrieveMethodSignature(methodElement);
        String fullyQualifiedClassName = JavaUtil.retrieveFullyQualifiedClassName(methodElement);
        String methodName = JavaUtil.retrieveMethodName(methodElement);
        String identifier = String.format("%s.%s%s", fullyQualifiedClassName, methodName, methodSignature);
        return checkInnerClassConstructorsFirstParameter(identifier);
    }

    public static String computeProfilingClassIdentifier(final Element methodElement)
    {
        final String fullyQualifiedClassName = JavaUtil.retrieveFullyQualifiedClassName(methodElement);
        return computeProfilingClassIdentifier(fullyQualifiedClassName);
    }

    public static String computeProfilingClassIdentifier(final String fullyQualifiedClassName)
    {  // Need to replace the $ sign here because in the psi tree gives a qualified name with '.' instead of '$'
        // for inner classes
        //noinspection UnnecessaryLocalVariable
        final String classIdentifier = fullyQualifiedClassName.replaceAll("\\$", ".");
        return classIdentifier;
    }

    private static String checkInnerClassConstructorsFirstParameter(final String identifier)
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

    public static String getSampleFilePath(final Project project)
    {
        final String sampleFilePath =
                System.getProperty("user.home")
                        .concat(File.separator)
                        .concat(".codesparks")
                        .concat(File.separator)
                        .concat("stack-sampling")
                        .concat(File.separator)
                        .concat(project.getName());
        try
        {
            final Path path = Paths.get(sampleFilePath);
            if (!Files.exists(path))
            {
                Files.createDirectories(path);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return sampleFilePath;
    }

    public static String computePsiMethodParameterString(final PsiMethod method)
    {
        final StringBuilder psiParameterBuilder = new StringBuilder("(");
        final PsiParameter[] psiParameters =
                ApplicationManager.getApplication().runReadAction((Computable<PsiParameter[]>) () -> method.getParameterList().getParameters());
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        for (final PsiParameter parameter : psiParameters)
        {
            // final PsiParameter workParameter = parameter;

            String type = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
//            String type = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
//                PsiType parameterType = workParameter.getType();
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
//                    PsiFile containingFile = workParameter.getContainingFile();
                    final PsiFile containingFile = parameter.getContainingFile();

                    final String packageName = ((PsiJavaFile) containingFile).getPackageName();
                    final String className = containingFile.getName().replaceAll(".(java|class)", "");

                    final String containingClassType = packageName + "." + className;

                    if (!parameterTypeCanonicalText.startsWith(containingClassType)
                            || parameterTypeCanonicalText.equals(containingClassType))
                    {
                        return parameterTypeCanonicalText;
                    }

                    // The parameter is of type of an inner class!
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








}
