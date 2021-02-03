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

    public static ArtifactTrie constructFirstTrieManually()
    {
        ArtifactTrie profilingArtifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        String rootStr = "root";
        // Add first stack trace
        ArtifactTrieNode root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        ArtifactTrieNode rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootABC = profilingArtifactTrie.addVertex("rootabc", "c");
        profilingArtifactTrie.addEdge(rootAB, rootABC, new ArtifactTrieEdge(rootAB, rootABC));
        // add second stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        // add third stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        // add fourth stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootABA = profilingArtifactTrie.addVertex("rootaba", "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        // add fifth stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        rootABA = profilingArtifactTrie.addVertex("rootaba", "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        // add sixth stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAA = profilingArtifactTrie.addVertex("rootaa", "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAB = profilingArtifactTrie.addVertex("rootaab", "b");
        profilingArtifactTrie.addEdge(rootAA, rootAAB, new ArtifactTrieEdge(rootAA, rootAAB));
        // add seventh stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        ArtifactTrieNode rootB = profilingArtifactTrie.addVertex("rootb", "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));
        ArtifactTrieNode rootBA = profilingArtifactTrie.addVertex("rootba", "a");
        profilingArtifactTrie.addEdge(rootB, rootBA, new ArtifactTrieEdge(rootB, rootBA));
        // add eighth stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootB = profilingArtifactTrie.addVertex("rootb", "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));
        ArtifactTrieNode rootBC = profilingArtifactTrie.addVertex("rootbc", "c");
        profilingArtifactTrie.addEdge(rootB, rootBC, new ArtifactTrieEdge(rootB, rootBC));
        ArtifactTrieNode rootBCA = profilingArtifactTrie.addVertex("rootbca", "a");
        profilingArtifactTrie.addEdge(rootBC, rootBCA, new ArtifactTrieEdge(rootBC, rootBCA));
        return profilingArtifactTrie;
    }

    public static ArtifactTrie constructSecondTrieManually()
    {
        ArtifactTrie profilingArtifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        String rootStr = "root";
        // Add first stack trace
        ArtifactTrieNode root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        ArtifactTrieNode rootB = profilingArtifactTrie.addVertex("rootb", "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));
        ArtifactTrieNode rootBC = profilingArtifactTrie.addVertex("rootbc", "c");
        profilingArtifactTrie.addEdge(rootB, rootBC, new ArtifactTrieEdge(rootB, rootBC));
        ArtifactTrieNode rootBCD = profilingArtifactTrie.addVertex("rootbcd", "d");
        profilingArtifactTrie.addEdge(rootBC, rootBCD, new ArtifactTrieEdge(rootBC, rootBCD));
        // add second stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        ArtifactTrieNode rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        // add third stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootABA = profilingArtifactTrie.addVertex("rootaba", "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        ArtifactTrieNode rootABAC = profilingArtifactTrie.addVertex("rootabac", "c");
        profilingArtifactTrie.addEdge(rootABA, rootABAC, new ArtifactTrieEdge(rootABA, rootABAC));
        // add fourth stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        rootABA = profilingArtifactTrie.addVertex("rootaba", "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        ArtifactTrieNode rootABAB = profilingArtifactTrie.addVertex("rootabab", "b");
        profilingArtifactTrie.addEdge(rootABA, rootABAB, new ArtifactTrieEdge(rootABA, rootABAB));
        ArtifactTrieNode rootABABA = profilingArtifactTrie.addVertex("rootababa", "a");
        profilingArtifactTrie.addEdge(rootABAB, rootABABA, new ArtifactTrieEdge(rootABAB, rootABABA));
        ArtifactTrieNode rootABABAC = profilingArtifactTrie.addVertex("rootababac", "c");
        profilingArtifactTrie.addEdge(rootABABA, rootABABAC, new ArtifactTrieEdge(rootABABA, rootABABAC));
        ArtifactTrieNode rootABABACA = profilingArtifactTrie.addVertex("rootababaca", "a");
        profilingArtifactTrie.addEdge(rootABABAC, rootABABACA, new ArtifactTrieEdge(rootABABAC, rootABABACA));
        // add fifth stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        rootABA = profilingArtifactTrie.addVertex("rootaba", "a");
        profilingArtifactTrie.addEdge(rootAB, rootABA, new ArtifactTrieEdge(rootAB, rootABA));
        rootABAB = profilingArtifactTrie.addVertex("rootabab", "b");
        profilingArtifactTrie.addEdge(rootABA, rootABAB, new ArtifactTrieEdge(rootABA, rootABAB));
        rootABABA = profilingArtifactTrie.addVertex("rootababa", "a");
        profilingArtifactTrie.addEdge(rootABAB, rootABABA, new ArtifactTrieEdge(rootABAB, rootABABA));
        rootABABAC = profilingArtifactTrie.addVertex("rootababac", "c");
        profilingArtifactTrie.addEdge(rootABABA, rootABABAC, new ArtifactTrieEdge(rootABABA, rootABABAC));
        // add sixth stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAA = profilingArtifactTrie.addVertex("rootaa", "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAB = profilingArtifactTrie.addVertex("rootaab", "b");
        profilingArtifactTrie.addEdge(rootAA, rootAAB, new ArtifactTrieEdge(rootAA, rootAAB));
        ArtifactTrieNode rootAABA = profilingArtifactTrie.addVertex("rootaaba", "a");
        profilingArtifactTrie.addEdge(rootAAB, rootAABA, new ArtifactTrieEdge(rootAAB, rootAABA));
        ArtifactTrieNode rootAABAB = profilingArtifactTrie.addVertex("rootaabab", "b");
        profilingArtifactTrie.addEdge(rootAABA, rootAABAB, new ArtifactTrieEdge(rootAABA, rootAABAB));
        ArtifactTrieNode rootAABABA = profilingArtifactTrie.addVertex("rootaababa", "a");
        profilingArtifactTrie.addEdge(rootAABAB, rootAABABA, new ArtifactTrieEdge(rootAABAB, rootAABABA));
        ArtifactTrieNode rootAABABAC = profilingArtifactTrie.addVertex("rootaababac", "c");
        profilingArtifactTrie.addEdge(rootAABABA, rootAABABAC, new ArtifactTrieEdge(rootAABABA, rootAABABAC));
        ArtifactTrieNode rootAABABACA = profilingArtifactTrie.addVertex("rootaababaca", "a");
        profilingArtifactTrie.addEdge(rootAABABAC, rootAABABACA, new ArtifactTrieEdge(rootAABABAC, rootAABABACA));
        // add seventh stack trace
        root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        rootAA = profilingArtifactTrie.addVertex("rootaa", "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAA = profilingArtifactTrie.addVertex("rootaaa", "a");
        profilingArtifactTrie.addEdge(rootAA, rootAAA, new ArtifactTrieEdge(rootAA, rootAAA));
        ArtifactTrieNode rootAAAA = profilingArtifactTrie.addVertex("rootaaaa", "a");
        profilingArtifactTrie.addEdge(rootAAA, rootAAAA, new ArtifactTrieEdge(rootAAA, rootAAAA));
        return profilingArtifactTrie;
    }

    public static ArtifactTrie constructIntersectingTrieManually()
    {
        ArtifactTrie profilingArtifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        String rootStr = "root";
        // Add first stack trace
        ArtifactTrieNode root = profilingArtifactTrie.addVertex(rootStr, rootStr);
        ArtifactTrieNode rootB = profilingArtifactTrie.addVertex("rootb", "b");
        profilingArtifactTrie.addEdge(root, rootB, new ArtifactTrieEdge(root, rootB));

        ArtifactTrieNode rootA = profilingArtifactTrie.addVertex("roota", "a");
        profilingArtifactTrie.addEdge(root, rootA, new ArtifactTrieEdge(root, rootA));
        ArtifactTrieNode rootAB = profilingArtifactTrie.addVertex("rootab", "b");
        profilingArtifactTrie.addEdge(rootA, rootAB, new ArtifactTrieEdge(rootA, rootAB));
        ArtifactTrieNode rootAA = profilingArtifactTrie.addVertex("rootaa", "a");
        profilingArtifactTrie.addEdge(rootA, rootAA, new ArtifactTrieEdge(rootA, rootAA));
        ArtifactTrieNode rootAAB = profilingArtifactTrie.addVertex("rootaab", "b");
        profilingArtifactTrie.addEdge(rootAA, rootAAB, new ArtifactTrieEdge(rootAA, rootAAB));

        return profilingArtifactTrie;
    }
}
