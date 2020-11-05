package de.unitrier.st.insituprofiling.core.visualization;

import com.intellij.psi.PsiElement;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

import javax.swing.*;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualization extends JLabel
{
    protected PsiElement psiElement;
    protected AProfilingArtifact artifact;

    public PsiElement getPsiElement()
    {
        return this.psiElement;
    }

    protected AArtifactVisualization(AProfilingArtifact artifact)
    {
        this.artifact = artifact;
        this.psiElement = artifact.getVisPsiElement();
    }

    public AProfilingArtifact getArtifact()
    {
        return artifact;
    }
}
