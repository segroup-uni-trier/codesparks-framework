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
package de.unitrier.st.codesparks.core.editorcoverlayer;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;

import javax.swing.*;

public class EditorCoverLayerItem
{
    private boolean valid;
    private final SmartPsiElementPointer<PsiElement> smartPsiElementPointer;
    private final JComponent component;

    public JComponent getComponent()
    {
        return component;
    }

    public EditorCoverLayerItem(final PsiElement psiElement, final JComponent component)
    {
        if (psiElement != null)
        {
            smartPsiElementPointer =
                    SmartPointerManager.getInstance(psiElement.getProject()).createSmartPsiElementPointer(psiElement,
                            psiElement.getContainingFile());
        } else
        {
            smartPsiElementPointer = null;
        }
        this.component = component;
        valid = true;
    }

    PsiElement getPositionalElement()
    {
        if (smartPsiElementPointer == null)
        {
            return null;
        }
        return smartPsiElementPointer.getElement();
    }

    boolean isValid()
    { // In case the smartPsiElementPointer is not null and its respective psi element is valid
        if (smartPsiElementPointer == null)
        {
            valid = false;
        } else
        {
            final PsiElement psiElement = smartPsiElementPointer.getElement(); // Return a psi element
            if (psiElement == null || !psiElement.isValid())
            {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * @return The offset of the current editor-cover layer item within the underling editor. Indeed, will return the offset of the
     * respective end of the line where the text range of the psi element ends.
     */
    int getOffset(final Editor editor)
    {
        try
        {
            final PsiElement psiElement = smartPsiElementPointer.getElement();
            if (psiElement == null || !psiElement.isValid())
            {
                return -1;
            }
            final TextRange range = psiElement.getTextRange();
            final int offset = range.getEndOffset(); // initial offset is where the text of the positional element ends
            final int lineNumber = editor.getDocument().getLineNumber(offset); // Get the line of the offset in which the text of the positional
            // element ends
            final int lineEndOffset = editor.getDocument().getLineEndOffset(lineNumber); // get the offset of the end of that line
            return Math.min(lineEndOffset, editor.getDocument().getTextLength());
        } catch (IndexOutOfBoundsException e)
        {
            return -1;
        }
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditorCoverLayerItem that = (EditorCoverLayerItem) o;
        if (valid != that.valid) return false;
        return smartPsiElementPointer.equals(that.smartPsiElementPointer);
    }

    @Override
    public int hashCode()
    { // do not addLayerItem component to the hashCode calculus since an editor cover layer item should be considered equal when at the
        // same position (-> smartPsiElementPointer)
        int result = (valid ? 1 : 0);
        result = 31 * result + smartPsiElementPointer.hashCode();
        return result;
    }
}
