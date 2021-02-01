
/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.unitrier.st.codesparks.core.data.ArtifactTrie;
import de.unitrier.st.codesparks.core.data.ArtifactTrieNode;
import de.unitrier.st.codesparks.core.data.DataUtil;
import de.unitrier.st.codesparks.core.data.StatisticDFSArtifactTrieTraversalListener;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
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

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();
        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        final int totalNrOfNodes = artifactTrie.vertexSet().size();

        Assert.assertEquals(totalNrOfNodes, nrOfInnerNodes + nrOfLeafNodes);
    }

    @Test
    public void testNumberOfLeafAndInnerNodes2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();
        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        final int totalNrOfNodes = artifactTrie.vertexSet().size();

        Assert.assertEquals(totalNrOfNodes, nrOfInnerNodes + nrOfLeafNodes);
    }

    @Test
    public void testNumberOfLeafNodes()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        Assert.assertEquals(5, nrOfLeafNodes);
    }

    @Test
    public void testNumberOfLeafNodes2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int nrOfLeafNodes = statisticDFSArtifactTrieVisitor.getNrOfLeafNodes();

        Assert.assertEquals(5, nrOfLeafNodes);
    }

    @Test
    public void testNumberOfInnerNodes()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();

        Assert.assertEquals(6, nrOfInnerNodes);
    }

    @Test
    public void testNumberOfInnerNodes2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int nrOfInnerNodes = statisticDFSArtifactTrieVisitor.getNrOfInnerNodes();

        Assert.assertEquals(16, nrOfInnerNodes);
    }

    @Test
    public void testMaxDepth()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int maxDepth = statisticDFSArtifactTrieVisitor.getMaxDepth();

        Assert.assertEquals(3, maxDepth);
    }

    @Test
    public void testMaxDepth2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

        final int maxDepth = statisticDFSArtifactTrieVisitor.getMaxDepth();

        Assert.assertEquals(8, maxDepth);
    }

    @Test
    public void testNodesPerLevel()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

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

        final StatisticDFSArtifactTrieTraversalListener statisticDFSArtifactTrieVisitor = new StatisticDFSArtifactTrieTraversalListener();

        artifactTrie.traverse(statisticDFSArtifactTrieVisitor);

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

    @Test
    public void testVertexLabelsSet()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final Set<String> vertexLabelsSet = artifactTrie.getVertexLabelsSet();

        Assert.assertEquals(4, vertexLabelsSet.size());

        final Set<String> strings = new HashSet<>(4);

        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("root");

        Assert.assertEquals(vertexLabelsSet, strings);
    }

    @Test
    public void testVertexLabelsSet2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final Set<String> vertexLabelsSet = artifactTrie.getVertexLabelsSet();

        Assert.assertEquals(5, vertexLabelsSet.size());

        final Set<String> strings = new HashSet<>(5);

        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");
        strings.add("root");

        Assert.assertEquals(vertexLabelsSet, strings);
    }

    @Test
    public void testVertexLabelsMultiSet()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructFirstTrieManually();

        final Multiset<String> labelsMultiSet = artifactTrie.getVertexLabelsMultiSet();

        Assert.assertEquals(11, labelsMultiSet.size());

        final HashMultiset<String> strings = HashMultiset.create();

        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");

        strings.add("b");
        strings.add("b");
        strings.add("b");

        strings.add("c");
        strings.add("c");

        strings.add("root");

        Assert.assertEquals(labelsMultiSet, strings);
    }

    @Test
    public void testVertexLabelsMultiSet2()
    {
        final ArtifactTrie artifactTrie = ArtifactTrieTestUtil.constructSecondTrieManually();

        final Multiset<String> labelsMultiSet = artifactTrie.getVertexLabelsMultiSet();

        Assert.assertEquals(21, labelsMultiSet.size());

        final HashMultiset<String> strings = HashMultiset.create();

        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");
        strings.add("a");

        strings.add("b");
        strings.add("b");
        strings.add("b");
        strings.add("b");
        strings.add("b");

        strings.add("c");
        strings.add("c");
        strings.add("c");
        strings.add("c");

        strings.add("d");

        strings.add("root");

        Assert.assertEquals(labelsMultiSet, strings);
    }

    @Test
    public void testMultisetJaccard()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();

        final double jaccard = DataUtil.multisetJaccard(t1, t2);

        Assert.assertEquals((double) 2 / 3, jaccard, 1e6);
    }

    @Test
    public void testJaccard()
    {
        final ArtifactTrie t1 = ArtifactTrieTestUtil.constructFirstTrieManually();
        final ArtifactTrie t2 = ArtifactTrieTestUtil.constructSecondTrieManually();

        final double jaccard = DataUtil.jaccard(t1, t2);

        Assert.assertEquals((double) 3 / 4, jaccard, 1e6);
    }

}
