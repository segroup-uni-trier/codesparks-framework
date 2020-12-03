package de.unitrier.st.codesparks.core.visualization;

import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

import javax.swing.*;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualization extends JLabel
{
    protected PsiElement psiElement;
    protected ACodeSparksArtifact artifact;

    public PsiElement getPsiElement()
    {
        return this.psiElement;
    }

    protected AArtifactVisualization(ACodeSparksArtifact artifact)
    {
        this.artifact = artifact;
        this.psiElement = artifact.getVisPsiElement();
    }

    public ACodeSparksArtifact getArtifact()
    {
        return artifact;
    }
}
