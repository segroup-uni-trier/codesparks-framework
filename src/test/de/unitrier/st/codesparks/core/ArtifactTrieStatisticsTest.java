
/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.ArtifactTrie;
import de.unitrier.st.codesparks.core.data.ArtifactTrieNode;
import de.unitrier.st.codesparks.core.data.StatisticDFSArtifactTrieVisitor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2021
 */
public class ArtifactTrieStatisticsTest
{
    @BeforeClass
    public static void setup()
    {

    }

    @Test
    public void testNumberOfLeafAndInnerNodes()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();
        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        final int totalNrOfNodes = artifactTrie.vertexSet().size();

        Assert.assertEquals(totalNrOfNodes, nrOfInnerNodes + nrOfLeafNodes);
    }

    @Test
    public void testNumberOfLeafAndInnerNodes2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();
        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        final int totalNrOfNodes = artifactTrie.vertexSet().size();

        Assert.assertEquals(totalNrOfNodes, nrOfInnerNodes + nrOfLeafNodes);
    }

    @Test
    public void testNumberOfLeafNodes()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        Assert.assertEquals(5, nrOfLeafNodes);
    }

    @Test
    public void testNumberOfLeafNodes2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        Assert.assertEquals(5, nrOfLeafNodes);
    }

    @Test
    public void testNumberOfInnerNodes()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();

        Assert.assertEquals(6, nrOfInnerNodes);
    }

    @Test
    public void testNumberOfInnerNodes2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();

        Assert.assertEquals(16, nrOfInnerNodes);
    }

    @Test
    public void testMaxDepth()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int maxDepth = statisticDFSArtifactTrieVisitor.getMaxDepth();

        Assert.assertEquals(3, maxDepth);
    }

    @Test
    public void testMaxDepth2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final int maxDepth = statisticDFSArtifactTrieVisitor.getMaxDepth();

        Assert.assertEquals(8, maxDepth);
    }

    @Test
    public void testNodesPerLevel()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final Set<ArtifactTrieNode> nodesOfLevelZero = statisticDFSArtifactTrieVisitor.getNodesOfLevel(0);

        Assert.assertEquals(nodesOfLevelZero.size(), 1);

        final Set<ArtifactTrieNode> nodesOfLevelOne = statisticDFSArtifactTrieVisitor.getNodesOfLevel(1);

        Assert.assertEquals(nodesOfLevelOne.size(), 2);

        final Set<ArtifactTrieNode> nodesOfLevelTwo = statisticDFSArtifactTrieVisitor.getNodesOfLevel(2);

        Assert.assertEquals(nodesOfLevelTwo.size(), 4);

        final Set<ArtifactTrieNode> nodesOfLevelThree = statisticDFSArtifactTrieVisitor.getNodesOfLevel(3);

        Assert.assertEquals(nodesOfLevelThree.size(), 4);

        final int totalNrOfNodes = artifactTrie.vertexSet().size();

        Assert.assertEquals(totalNrOfNodes, nodesOfLevelZero.size() + nodesOfLevelOne.size() + nodesOfLevelTwo.size() + nodesOfLevelThree.size());
    }

    @Test
    public void testNodesPerLevel2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieVisitor statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieVisitor();

        artifactTrie.accept(statisticDFSArtifactTrieVisitor);

        final Set<ArtifactTrieNode> nodesOfLevelZero = statisticDFSArtifactTrieVisitor.getNodesOfLevel(0);

        Assert.assertEquals(nodesOfLevelZero.size(), 1);

        final Set<ArtifactTrieNode> nodesOfLevelOne = statisticDFSArtifactTrieVisitor.getNodesOfLevel(1);

        Assert.assertEquals(nodesOfLevelOne.size(), 2);

        final Set<ArtifactTrieNode> nodesOfLevelTwo = statisticDFSArtifactTrieVisitor.getNodesOfLevel(2);

        Assert.assertEquals(nodesOfLevelTwo.size(), 3);

        final Set<ArtifactTrieNode> nodesOfLevelThree = statisticDFSArtifactTrieVisitor.getNodesOfLevel(3);

        Assert.assertEquals(nodesOfLevelThree.size(), 4);

        final Set<ArtifactTrieNode> nodesOfLevelFour = statisticDFSArtifactTrieVisitor.getNodesOfLevel(4);

        Assert.assertEquals(nodesOfLevelFour.size(), 4);

        final Set<ArtifactTrieNode> nodesOfLevelFive = statisticDFSArtifactTrieVisitor.getNodesOfLevel(5);

        Assert.assertEquals(nodesOfLevelFive.size(), 2);

        final Set<ArtifactTrieNode> nodesOfLevelSix = statisticDFSArtifactTrieVisitor.getNodesOfLevel(6);

        Assert.assertEquals(nodesOfLevelSix.size(), 2);

        final Set<ArtifactTrieNode> nodesOfLevelSeven = statisticDFSArtifactTrieVisitor.getNodesOfLevel(7);

        Assert.assertEquals(nodesOfLevelSeven.size(), 2);

        final Set<ArtifactTrieNode> nodesOfLevelEight = statisticDFSArtifactTrieVisitor.getNodesOfLevel(8);

        Assert.assertEquals(nodesOfLevelEight.size(), 1);


        final int totalNrOfNodes = artifactTrie.vertexSet().size();

        Assert.assertEquals(totalNrOfNodes,
                nodesOfLevelZero.size()
                        + nodesOfLevelOne.size()
                        + nodesOfLevelTwo.size()
                        + nodesOfLevelThree.size()
                        + nodesOfLevelFour.size()
                        + nodesOfLevelFive.size()
                        + nodesOfLevelSix.size()
                        + nodesOfLevelSeven.size()
                        + nodesOfLevelEight.size()
        );
    }


}
