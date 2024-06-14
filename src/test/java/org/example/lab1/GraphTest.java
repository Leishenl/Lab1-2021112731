package org.example.lab1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;


class GraphTest {

  @Test
  void testPathExists() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(Arrays.asList("new", "life", "路径长度为：1"));
    assertEquals(expectedResult, g.calcAllShortestPaths("new", "life"));
  }

  @Test
  void testNoPath() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(Arrays.asList("没有找到路径"));
    assertEquals(expectedResult, g.calcAllShortestPaths("civilizations", "new"));
  }

  @Test
  void testSameNode() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(Arrays.asList("new", "路径长度为：0"));
    assertEquals(expectedResult, g.calcAllShortestPaths("new", "new"));
  }

  @Test
  void testNodeNotInGraph() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(Arrays.asList("word1节点不存在"));
    assertEquals(expectedResult, g.calcAllShortestPaths("hello", "in"));
  }

  @Test
  void testEmptyGraph() {
    Graph g = new Graph();
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(Arrays.asList("图不存在"));
    assertEquals(expectedResult, g.calcAllShortestPaths("new", "in"));
  }
  @Test
  void testpath1() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    String result1 = g.queryBridgeWords("live", "to");
    Assert.assertEquals("No word1 or word2 in the graph!", result1);
  }
  @Test
  void testpath2() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    String result2 = g.queryBridgeWords("civilizations", "life");
    Assert.assertEquals("No bridge words from civilizations to life!", result2);
  }
  @Test
  void testpath3() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    String result3 = g.queryBridgeWords("new", "and");
    Assert.assertEquals("The bridge words from new to and are: life.", result3);
  }
  @Test
  void testpath4() {
    Graph g = new Graph();
    File file = new File("src/data/test.txt");
    g.createGraph(GraphUtils.readWordsFromFile(file));
    g.setWords(GraphUtils.readWordsFromFile(file));
    String result4 = g.queryBridgeWords("worlds", "out");
    Assert.assertEquals("No bridge words from worlds to out!", result4);
  }
}
