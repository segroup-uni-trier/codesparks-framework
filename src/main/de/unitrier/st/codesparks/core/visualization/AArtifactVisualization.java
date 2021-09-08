/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;

public abstract class AArtifactVisualization extends JLabel
{
    protected PsiElement psiElement;
    protected final AArtifact artifact;

    public PsiElement getPsiElement()
    {
        return this.psiElement;
    }

    protected AArtifactVisualization(AArtifact artifact)
    {
        this.artifact = artifact;
        this.psiElement = artifact.getVisPsiElement();
    }

    public AArtifact getArtifact()
    {
        return artifact;
    }
}
