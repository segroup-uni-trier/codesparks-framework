/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.ArtifactTrie;
import de.unitrier.st.codesparks.core.data.ArtifactTrieDotExportStrategy;
import de.unitrier.st.codesparks.core.data.DataUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ArtifactTrieIntersectionTest
{
    @BeforeClass
    public static void setup()
    {

    }

    @Test
    public void testArtifactTrieIntersection()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();

        final ArtifactTrie manuallyConstructedIntersectingTrie = ArtifactTrieTestUtil.constructIntersectingTrieManually();
        manuallyConstructedIntersectingTrie.export(new ArtifactTrieDotExportStrategy("testresources/ManuallyConstructedIntersectingTrie.dot"));

        final int nrNodesOfIntersectingTrie = manuallyConstructedIntersectingTrie.vertexSet().size();

        Assert.assertEquals(6, nrNodesOfIntersectingTrie);

        final String artifactIdentifier = "b";

        final ArtifactTrie computedIntersectingTrie = DataUtil.intersection(t1, t2, artifactIdentifier);

        Assert.assertNotNull(computedIntersectingTrie);

        computedIntersectingTrie.export(new ArtifactTrieDotExportStrategy("testresources/ComputedIntersectingTrie.dot"));

        Assert.assertEquals(manuallyConstructedIntersectingTrie, computedIntersectingTrie);
    }

    @Test
    public void testArtifactTrieSimilarity()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        final double similarity = DataUtil.similarity(t1, t2, artifactIdentifier);
        Assert.assertEquals(0.75, similarity, 1e-8);
    }

    @Test
    public void testArtifactTrieDistance()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        Assert.assertEquals(0.25, DataUtil.distance(t1, t2, artifactIdentifier), 1e-8);
    }
}
