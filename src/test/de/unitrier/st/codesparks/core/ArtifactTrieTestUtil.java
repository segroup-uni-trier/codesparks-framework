/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.ArtifactTrie;
import de.unitrier.st.codesparks.core.data.ArtifactTrieEdge;
import de.unitrier.st.codesparks.core.data.ArtifactTrieNode;

public final class ArtifactTrieTestUtil
{
    private ArtifactTrieTestUtil() {}

    private static final String rootLabel = "root";
    private static final int rootId = rootLabel.hashCode();

    public static ArtifactTrie constructFirstTrieManually()
    {
        ArtifactTrie profilingArtifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        // Add first stack trace
        ArtifactTrieNode root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        ArtifactTrieNode rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootABC = profilingArtifactTrie.addVertex("rootabc".hashCode(), "c");
        profilingArtifactTrie.addEdge(rootAB, rootABC, new ArtifactTrieEdge(rootAB, rootABC));
        // add second stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        // add third stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        // add fourth stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootABA = profilingArtifactTrie.addVertex("rootaba".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        // add fifth stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        rootABA = profilingArtifactTrie.addVertex("rootaba".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        // add sixth stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAA = profilingArtifactTrie.addVertex("rootaa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAB = profilingArtifactTrie.addVertex("rootaab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootAA, rootAAB, new ArtifactTrieEdge(rootAA, rootAAB));
        // add seventh stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        ArtifactTrieNode rootB = profilingArtifactTrie.addVertex("rootb".hashCode(), "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));
        ArtifactTrieNode rootBA = profilingArtifactTrie.addVertex("rootba".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootB, rootBA, new ArtifactTrieEdge(rootB, rootBA));
        // add eighth stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootB = profilingArtifactTrie.addVertex("rootb".hashCode(), "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));
        ArtifactTrieNode rootBC = profilingArtifactTrie.addVertex("rootbc".hashCode(), "c");
        profilingArtifactTrie.addEdge(rootB, rootBC, new ArtifactTrieEdge(rootB, rootBC));
        ArtifactTrieNode rootBCA = profilingArtifactTrie.addVertex("rootbca".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootBC, rootBCA, new ArtifactTrieEdge(rootBC, rootBCA));
        return profilingArtifactTrie;
    }

    public static ArtifactTrie constructSecondTrieManually()
    {
        ArtifactTrie profilingArtifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        // Add first stack trace
        ArtifactTrieNode root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        ArtifactTrieNode rootB = profilingArtifactTrie.addVertex("rootb".hashCode(), "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));
        ArtifactTrieNode rootBC = profilingArtifactTrie.addVertex("rootbc".hashCode(), "c");
        profilingArtifactTrie.addEdge(rootB, rootBC, new ArtifactTrieEdge(rootB, rootBC));
        ArtifactTrieNode rootBCD = profilingArtifactTrie.addVertex("rootbcd".hashCode(), "d");
        profilingArtifactTrie.addEdge(rootBC, rootBCD, new ArtifactTrieEdge(rootBC, rootBCD));
        // add second stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        ArtifactTrieNode rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        // add third stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootABA = profilingArtifactTrie.addVertex("rootaba".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        ArtifactTrieNode rootABAC = profilingArtifactTrie.addVertex("rootabac".hashCode(), "c");
        profilingArtifactTrie.addEdge(rootABA, rootABAC, new ArtifactTrieEdge(rootABA, rootABAC));
        // add fourth stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        rootABA = profilingArtifactTrie.addVertex("rootaba".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        ArtifactTrieNode rootABAB = profilingArtifactTrie.addVertex("rootabab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootABA, rootABAB, new ArtifactTrieEdge(rootABA, rootABAB));
        ArtifactTrieNode rootABABA = profilingArtifactTrie.addVertex("rootababa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootABAB, rootABABA, new ArtifactTrieEdge(rootABAB, rootABABA));
        ArtifactTrieNode rootABABAC = profilingArtifactTrie.addVertex("rootababac".hashCode(), "c");
        profilingArtifactTrie.addEdge(rootABABA, rootABABAC, new ArtifactTrieEdge(rootABABA, rootABABAC));
        ArtifactTrieNode rootABABACA = profilingArtifactTrie.addVertex("rootababaca".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootABABAC, rootABABACA, new ArtifactTrieEdge(rootABABAC, rootABABACA));
        // add fifth stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        rootABA = profilingArtifactTrie.addVertex("rootaba".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        rootABAB = profilingArtifactTrie.addVertex("rootabab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootABA, rootABAB, new ArtifactTrieEdge(rootABA, rootABAB));
        rootABABA = profilingArtifactTrie.addVertex("rootababa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootABAB, rootABABA, new ArtifactTrieEdge(rootABAB, rootABABA));
        rootABABAC = profilingArtifactTrie.addVertex("rootababac".hashCode(), "c");
        profilingArtifactTrie.addEdge(rootABABA, rootABABAC, new ArtifactTrieEdge(rootABABA, rootABABAC));
        // add sixth stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAA = profilingArtifactTrie.addVertex("rootaa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAB = profilingArtifactTrie.addVertex("rootaab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootAA, rootAAB, new ArtifactTrieEdge(rootAA, rootAAB));
        ArtifactTrieNode rootAABA = profilingArtifactTrie.addVertex("rootaaba".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAAB, rootAABA, new ArtifactTrieEdge(rootAAB, rootAABA));
        ArtifactTrieNode rootAABAB = profilingArtifactTrie.addVertex("rootaabab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootAABA, rootAABAB, new ArtifactTrieEdge(rootAABA, rootAABAB));
        ArtifactTrieNode rootAABABA = profilingArtifactTrie.addVertex("rootaababa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAABAB, rootAABABA, new ArtifactTrieEdge(rootAABAB, rootAABABA));
        ArtifactTrieNode rootAABABAC = profilingArtifactTrie.addVertex("rootaababac".hashCode(), "c");
        profilingArtifactTrie.addEdge(rootAABABA, rootAABABAC, new ArtifactTrieEdge(rootAABABA, rootAABABAC));
        ArtifactTrieNode rootAABABACA = profilingArtifactTrie.addVertex("rootaababaca".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAABABAC, rootAABABACA, new ArtifactTrieEdge(rootAABABAC, rootAABABACA));
        // add seventh stack trace
        root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAA = profilingArtifactTrie.addVertex("rootaa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAA = profilingArtifactTrie.addVertex("rootaaa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAA, rootAAA, new ArtifactTrieEdge(rootAA, rootAAA));
        ArtifactTrieNode rootAAAA = profilingArtifactTrie.addVertex("rootaaaa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootAAA, rootAAAA, new ArtifactTrieEdge(rootAAA, rootAAAA));
        return profilingArtifactTrie;
    }

    public static ArtifactTrie constructIntersectingTrieManually()
    {
        ArtifactTrie profilingArtifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);

        ArtifactTrieNode root = profilingArtifactTrie.addVertex(rootId, rootLabel);
        ArtifactTrieNode rootB = profilingArtifactTrie.addVertex("rootb".hashCode(), "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));

        ArtifactTrieNode rootA = profilingArtifactTrie.addVertex("roota".hashCode(), "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAB = profilingArtifactTrie.addVertex("rootab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootAA = profilingArtifactTrie.addVertex("rootaa".hashCode(), "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAB = profilingArtifactTrie.addVertex("rootaab".hashCode(), "b");
        profilingArtifactTrie.addEdge(rootAA, rootAAB, new ArtifactTrieEdge(rootAA, rootAAB));

        return profilingArtifactTrie;
    }
}
