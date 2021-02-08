/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.ArtifactTrie;
import de.unitrier.st.codesparks.core.data.ArtifactTrieDotExportStrategy;
import de.unitrier.st.codesparks.core.data.ArtifactTrieEdge;


import de.unitrier.st.codesparks.core.data.ArtifactTrieUtilKt;
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
        manuallyConstructedIntersectingTrie.export(new ArtifactTrieDotExportStrategy("testresources/manual-intersection-t1-t2.dot"));

        final int nrNodesOfIntersectingTrie = manuallyConstructedIntersectingTrie.vertexSet().size();

        Assert.assertEquals(6, nrNodesOfIntersectingTrie);

        final String artifactIdentifier = "b";

        final ArtifactTrie computedIntersectingTrie = ArtifactTrieUtilKt.intersection(t1, t2, artifactIdentifier);

        Assert.assertNotNull(computedIntersectingTrie);

        computedIntersectingTrie.export(new ArtifactTrieDotExportStrategy("testresources/computed-intersection-t1-t2.dot"));

        Assert.assertEquals(manuallyConstructedIntersectingTrie, computedIntersectingTrie);
    }

    @Test
    public void testArtifactTrieIntersectionT3ToT3()
    {
        final ArtifactTrie t3 = ArtifactTrieTestUtil.constructThirdTrieManually();

        t3.export(new ArtifactTrieDotExportStrategy("testresources/manual-constructed-t3.dot"));

        final ArtifactTrie computedIntersectingTrie = ArtifactTrieUtilKt.intersection(t3, t3, "a");

        Assert.assertNotNull(computedIntersectingTrie);

        computedIntersectingTrie.export(new ArtifactTrieDotExportStrategy("testresources/computed-intersection-t3-t3.dot"));

        final ArtifactTrie manualIntersecting = ArtifactTrieTestUtil.constructIntersectingThirdTrieManually();

        manualIntersecting.export(new ArtifactTrieDotExportStrategy("testresources/manual-intersection-t3-t3.dot"));

        Assert.assertEquals(manualIntersecting, computedIntersectingTrie);
    }

    @Test
    public void testArtifactTrieIntersectionCommutativity()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();

        final String artifactIdentifier = "b";

        final ArtifactTrie intersection = ArtifactTrieUtilKt.intersection(t1, t2, artifactIdentifier);

        final ArtifactTrie intersection1 = ArtifactTrieUtilKt.intersection(t2, t1, artifactIdentifier);

        Assert.assertEquals(intersection, intersection1);
    }

    @Test
    public void getNumberOfNodesOfSubtree()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree3()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree4()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "Doesn't matter what string is in here!";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree5()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree6()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(2, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree7()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(16, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree8()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "d";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(4, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree9()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "Doesn't matter what string is in here!";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtree10()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        artifactTrie.removeVertex(artifactTrie.getRoot());
        final String artifactIdentifier = "root";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtree(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(6, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths3()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(10, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths4()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(8, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths5()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "a";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(5, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths6()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "c";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(18, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths7()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "d";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(21, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths8()
    {
        final ArtifactTrie artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        artifactTrie.removeVertex(artifactTrie.getRoot());
        final String artifactIdentifier = "Doesn't matter what string is in here!";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(0, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths9()
    {
        final ArtifactTrie artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        final String artifactIdentifier = "Any string different from 'root'";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(1, numberOfNodesTill);
    }

    @Test
    public void getNumberOfNodesOfSubtreeIncludingOtherPaths10()
    {
        final ArtifactTrie artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
        final String artifactIdentifier = "root";
        final long numberOfNodesTill = artifactTrie.getNumberOfNodesOfSubtreeIncludingOtherPaths(artifactIdentifier);
        Assert.assertEquals(1, numberOfNodesTill);
    }

    @Test
    public void testArtifactTrieSimilarity()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        final double similarity = ArtifactTrieUtilKt.similarity(t1, t2, artifactIdentifier);
        Assert.assertEquals(1D, similarity, 1e-8);
    }

    @Test
    public void testArtifactTrieDistance()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();
        final String artifactIdentifier = "b";
        Assert.assertEquals(0D, ArtifactTrieUtilKt.distance(t1, t2, artifactIdentifier), 1e-8);
    }
}
