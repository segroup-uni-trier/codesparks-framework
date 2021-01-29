/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

public interface ITrieVisitable
{
    void accept(ATrieVisitorAdapter trieVisitor);
}
