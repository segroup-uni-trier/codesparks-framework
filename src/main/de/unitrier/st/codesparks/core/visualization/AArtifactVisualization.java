package de.unitrier.st.codesparks.core.visualization;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;
import de.unitrier.st.codesparks.core.data.ASourceCodeArtifact;

import javax.swing.*;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualization extends JLabel
{
    protected PsiElement psiElement;
    protected ASourceCodeArtifact artifact;

    public PsiElement getPsiElement()
    {
        return this.psiElement;
    }

    protected AArtifactVisualization(ASourceCodeArtifact artifact)
    {
        this.artifact = artifact;
        this.psiElement = artifact.getVisPsiElement();
    }

    public AArtifact getArtifact()
    {
        return artifact;
    }
}
