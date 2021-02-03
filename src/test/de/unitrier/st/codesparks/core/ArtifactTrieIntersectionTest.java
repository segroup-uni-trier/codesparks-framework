/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.ArtifactTrie;
import de.unitrier.st.codesparks.core.data.ArtifactTrieDotExportStrategy;
import de.unitrier.st.codesparks.core.data.ArtifactTrieEdge;
import de.unitrier.st.codesparks.core.data.ArtifactTrieUtil;
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

        final ArtifactTrie computedIntersectingTrie = ArtifactTrieUtil.intersection(t1, t2, artifactIdentifier);

        Assert.assertNotNull(computedIntersectingTrie);

        computedIntersectingTrie.export(new ArtifactTrieDotExportStrategy("testresources/ComputedIntersectingTrie.dot"));

        Assert.assertEquals(manuallyConstructedIntersectingTrie, computedIntersectingTrie);
    }

    @Test
    public void testArtifactTrieIntersectionCommutativity()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();

        final String artifactIdentifier = "b";

        final ArtifactTrie intersection = ArtifactTrieUtil.intersection(t1, t2, artifactIdentifier);

        final ArtifactTrie intersection1 = ArtifactTrieUtil.intersection(t2, t1, artifactIdentifier);

        Assert.assertEquals(intersection, intersection1);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches3()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches4()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "Doesn't matter what string is in here!";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches5()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches6()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(2, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches7()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(16, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches8()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "d";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(4, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches9()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "Doesn't matter what string is in here!";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTillWithoutDifferentBranches10()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        artifactTrie.removeVertex(artifactTrie.getRoot());
        final String artifactIdentifier = "root";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTillWithoutDifferentBranches(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill3()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(10, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill4()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(8, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill5()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(5, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill6()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(18, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill7()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "d";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(21, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill8()
    {
        final ArtifactTrie artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        artifactTrie.removeVertex(artifactTrie.getRoot());
        final String artifactIdentifier = "Doesn't matter what string is in here!";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill9()
    {
        final ArtifactTrie artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        final String artifactIdentifier = "Any string different from 'root'";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(1, numberOfNodesTill);
    }

    @Test
    public void testNumberOfNodesTill10()
    {
        final ArtifactTrie artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        final String artifactIdentifier = "root";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesTill(artifactIdentifier);
        Assert.assertEquals(1, numberOfNodesTill);
    }

    @Test
    public void testArtifactTrieSimilarity()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        final double similarity = ArtifactTrieUtil.similarity(t1, t2, artifactIdentifier);
        Assert.assertEquals(0.75, similarity, 1e-8);
    }

    @Test
    public void testArtifactTrieDistance()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        Assert.assertEquals(0.25, ArtifactTrieUtil.distance(t1, t2, artifactIdentifier), 1e-8);
    }
}
