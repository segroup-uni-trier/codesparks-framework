package de.unitrier.st.insituprofiling.core.editorcoverlayer;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;

import javax.swing.*;

/*
 * Oliver Moseler, 2020
 */
public class EditorCoverLayerItem
{
    private boolean valid;
    private final SmartPsiElementPointer<PsiElement> smartPsiElementPointer;
    private final JComponent component;

    public JComponent getComponent()
    {
        return component;
    }

    public EditorCoverLayerItem(PsiElement psiElement, JComponent component)
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
            PsiElement psiElement = smartPsiElementPointer.getElement(); // Return a psi element
            if (psiElement == null || !psiElement.isValid())
            {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * @return The offset of the current editor cover layer item within the underling editor. Indeed will return the offset of the
     * respective end of the line where the text range of the psi element ends.
     */
    int getOffset(Editor editor)
    {
        try
        {
            PsiElement psiElement = smartPsiElementPointer.getElement();
            if (psiElement == null || !psiElement.isValid())
            {
                return -1;
            }
            TextRange range = psiElement.getTextRange();
            int offset = range.getEndOffset(); // initial offset is where the text of the positional element ends
            int lineNumber = editor.getDocument().getLineNumber(offset); // Get the line of the offset in which the text of the positional
            // element ends
            int lineEndOffset = editor.getDocument().getLineEndOffset(lineNumber); // get the offset of the end of that line
            return Math.min(lineEndOffset, editor.getDocument().getTextLength());
        } catch (IndexOutOfBoundsException e)
        {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o)
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
        // same position
        // (-> smartPsiElementPointer)
        int result = (valid ? 1 : 0);
        result = 31 * result + smartPsiElementPointer.hashCode();
        return result;
    }
}
