package org.example.lab1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 表示一个有向图，其中节点是单词，边表示文本中的出现.
 */

public class Graph {
  private Map<String, Map<String, Integer>> wordsMap; //图结构
  private String currentNode;
  private boolean walking = false;

  public boolean isWalking() {
    return walking;
  }

  private SecureRandom random = new SecureRandom();

  private List<String> path = new ArrayList<>();

  private Set<String> visitedEdges = new HashSet<>();

  private List<String> words;

  public List<String> getWords() {
    return Collections.unmodifiableList(words);
  }

  public void setWords(List<String> words) {
    this.words = new ArrayList<>(words);
  }

  public Map<String, Map<String, Integer>> getWordsMap() {
    return Collections.unmodifiableMap(wordsMap);
  }

  /**set方法.
   *
   * @param wordsMap 要设置图
   */
  public void setWordsMap(Map<String, Map<String, Integer>> wordsMap) {
    Map<String, Map<String, Integer>> copy = new HashMap<>();
    for (Map.Entry<String, Map<String, Integer>> entry : wordsMap.entrySet()) {
      copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
    }
    this.wordsMap = copy;
  }

  public Graph() {

    wordsMap = new HashMap<>();
  }
  /**
   * 添加边.
   */

  public void addEdge(String source, String destination) {
    source = source.toLowerCase();
    destination = destination.toLowerCase();

    Map<String, Integer> neighbors = this.wordsMap.get(source);
    if (neighbors == null) {
      neighbors = new HashMap<>();
      this.wordsMap.put(source, neighbors);
    }
    Integer count = neighbors.get(destination);
    if (count == null) {
      neighbors.put(destination, 1);
    } else {
      neighbors.put(destination, count + 1);
    }
  }
  /**
  * 根据单词列表生成有向图.
  */

  public void createGraph(List<String> words) {
    for (int i = 0; i < words.size() - 1; i++) {
      String word1 = words.get(i);
      String word2 = words.get(i + 1);
      addEdge(word1, word2);
    }
  }

  /**
   * 获取节点的邻居及其权重.
   */
  public Map<String, Integer> getNeighbors(String node) {
    node = node.toLowerCase();
    if (this.wordsMap.containsKey(node)) {
      return this.wordsMap.get(node);
    } else {
      return new HashMap<>();
    }
  }
  /**
  * 打印图的信息.
  */

  public void showDirectedGraph(Graph p) {
    System.out.println("-------------------------------有向带权图------------------------------");
    for (String node : p.getWordsMap().keySet()) {
      System.out.print(node + "->");
      System.out.print("Neighbors: ");
      Map<String, Integer> neighbors = getNeighbors(node);
      for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
        String neighbor = neighborEntry.getKey();
        int weight = neighborEntry.getValue();
        System.out.print("(" + neighbor + "," + weight + ")  ");
      }
      System.out.println();
    }
    System.out.println("----------------------------------------------------------------------");
  }
  /**
  * 生成dot文件.
  */

  public void generateDotFile(String fileName) {
    // 校验文件名是否合法，防止路径遍历攻击
    if (!isValidFileName(fileName)) {
      System.err.println("Invalid file name: " + fileName);
      return;
    }
    try {
      Path baseDir = Paths.get("").toAbsolutePath().normalize();
      Path filePath = baseDir.resolve(fileName); // 使用基础目录来创建完整路径

      try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
        writer.write("digraph G {\n");
        for (Map.Entry<String, Map<String, Integer>> entry : wordsMap.entrySet()) {
          String node = entry.getKey();
          Map<String, Integer> neighbors = entry.getValue();
          for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
            String neighbor = neighborEntry.getKey();
            int weight = neighborEntry.getValue();
            writer.write("    \"" + node + "\" -> \"" + neighbor
                    + "\" [label=\"" + weight + "\"];\n");
          }
        }
        writer.write("}\n");
        System.out.println("DOT file has been generated: " + fileName);
      } catch (IOException e) {
        System.err.println("Error writing DOT file: " + e.getMessage());
      }
    } catch (InvalidPathException e) {
      System.err.println("Invalid file path: " + e.getMessage());
    }
  }

  private boolean isValidFileName(String fileName) {
    // 检查文件名是否合法，防止路径遍历攻击
    try {
      // 使用更严格的白名单验证文件名和路径
      if (!fileName.matches("[a-zA-Z0-9_\\.]+")) {
        System.err.println("Invalid file name: " + fileName);
        return false;
      }
      // 获取当前工作目录的绝对路径并标准化
      Path baseDir = Paths.get("").toAbsolutePath().normalize();
      // 构建文件路径，并确保路径安全
      Path filePath = baseDir.resolve(fileName).normalize();
      // 检查标准化后的文件路径是否在预期的基础目录内
      if (!filePath.startsWith(baseDir)) {
        System.err.println("Invalid file name: " + fileName);
        return false; // 如果不在预期的基础目录内，则认为文件名非法
      }
      return true; // 文件名合法
    } catch (InvalidPathException e) {
      System.err.println("Invalid file path: " + e.getMessage());
      return false; // 如果路径无效，则认为文件名非法
    }
  }


  /**
  * 调用Graphviz来生成图像的方法.
  */

  public void renderGraph(String dotFilePath, String outputFilePath) {
    try {
      String dotCommand = "C:\\Program Files\\Graphviz\\bin\\dot";
      ProcessBuilder pb = new ProcessBuilder(dotCommand, "-Tpng",
              dotFilePath, "-o", outputFilePath);
      pb.inheritIO();
      Process process = pb.start();
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      System.err.println("Error rendering graph: " + e.getMessage());
    }
  }
  /**
  * 桥接词查询.
  */

  public String queryBridgeWords(String word1, String word2) {
    word1 = word1.toLowerCase();
    word2 = word2.toLowerCase();

    // 检查输入的单词是否在图中出现
    if (!this.words.contains(word1) || !this.words.contains(word2)) {
      return "No word1 or word2 in the graph!";
    }
    List<String> bridgeWords = new ArrayList<>();
    Map<String, Integer> neighbors1 = this.getNeighbors(word1);
    // 在图中查找桥接词
    for (String neighborWord : neighbors1.keySet()) {
      Map<String, Integer> neighborsBridgeWord = this.getNeighbors(neighborWord);
      if (neighborsBridgeWord.containsKey(word2)) {
        bridgeWords.add(neighborWord);
      }
    }
    // 根据查找结果返回相应的消息
    if (bridgeWords.isEmpty()) {
      return "No bridge words from " + word1 + " to " + word2 + "!";
    } else {
      return "The bridge words from " + word1 + " to " + word2 + " are: "
              + String.join(", ", bridgeWords) + ".";
    }
  }
  /**
  * 生成新文本.
  */

  public String generateNewText(String inputText) {
    String[] newWords = inputText.split("\\s+");
    StringBuilder newText = new StringBuilder();
    for (int i = 0; i < newWords.length - 1; i++) {
      String word1 = newWords[i];
      String word2 = newWords[i + 1];
      newText.append(word1).append(" "); // 将单词1添加到新文本中
      // 查找桥接词
      String bridgeWords = queryBridgeWords(word1, word2);
      if (bridgeWords.contains("are")) {
        // 提取 'are: ' 后面的所有文本直到句点前
        String bridgeWordsPart = bridgeWords.substring(bridgeWords.indexOf("are: ") + 5,
                bridgeWords.length() - 1);
        // 分割提取的部分来获取单独的桥接词
        String[] bridgeWord = bridgeWordsPart.split(", ");
        String wordsWithBrackets = Arrays.toString(bridgeWord);
        // 去除首尾的方括号
        String trimmedWords = wordsWithBrackets.substring(1, wordsWithBrackets.length() - 1);

        // 使用逗号分割字符串为数组
        String[] wordsArray = trimmedWords.split(", ");

        // 随机选择一个元素
        String selectedWord = wordsArray[random.nextInt(wordsArray.length)]; // 随机选取一个元素

        newText.append(selectedWord).append(" "); // 将桥接词添加到新文本中
      }
    }
    newText.append(newWords[newWords.length - 1]); // 将最后一个单词添加到新文本中
    return newText.toString();
  }
  /**
  * 最短路径.
  */

  public List<List<String>> calcAllShortestPaths(String word1, String word2) {
    word1 = word1.toLowerCase();
    word2 = word2.toLowerCase();
    Set<String> allNodes = getAllNodes();
    if (wordsMap.isEmpty()) {
      List<List<String>> result = new ArrayList<>();
      result.add(Arrays.asList("图不存在"));
      return result;  // 返回包含错误消息的列表
    }
    // 检查源节点和目标节点是否存在于图中
    if (!allNodes.contains(word1)) {
      List<List<String>> result = new ArrayList<>();
      result.add(Arrays.asList("word1节点不存在"));
      return result;  // 返回包含错误消息的列表
    }
    if (!allNodes.contains(word2)) {
      List<List<String>> result = new ArrayList<>();
      result.add(Arrays.asList("word2节点不存在"));
      return result;  // 返回包含错误消息的列表
    }
    // 初始化距离和前驱节点
    Map<String, Integer> distance = new HashMap<>();  // 存储每个节点的最短距离
    Map<String, List<String>> predecessors = new HashMap<>();  // 存储每个节点的前驱节点列表
    for (String node : this.words) {
      distance.put(node, Integer.MAX_VALUE);  // 初始化所有节点的距离为无限大
      predecessors.put(node, new ArrayList<>());  // 初始化前驱节点列表为空
    }
    distance.put(word1, 0);  // 起点的距离设为0
    // 使用一个集合来模拟优先队列
    Set<String> unvisited = new HashSet<>(this.words);
    // Dijkstra算法
    while (!unvisited.isEmpty()) {
      // 找到未访问节点中距离最小的节点
      String u = null;
      int minDistance = Integer.MAX_VALUE;
      for (String node : unvisited) {
        int dist = distance.get(node);
        if (dist < minDistance) {
          minDistance = dist;
          u = node;
        }
      }
      if (u == null) {
        break; // 如果找不到这样的节点，退出循环
      }
      unvisited.remove(u);  // 标记节点为已访问
      Map<String, Integer> neighbors = this.wordsMap.get(u);
      if (neighbors != null) {
        for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
          String v = entry.getKey();
          int weight = entry.getValue();
          int newdistance = distance.get(u) + weight;
          if (newdistance < distance.get(v)) {
            distance.put(v, newdistance);
            predecessors.get(v).clear();
            predecessors.get(v).add(u);
          } else if (newdistance == distance.get(v)) {
            predecessors.get(v).add(u);  // 添加额外的前驱节点
          }
        }
      }
    }

    // 构建所有最短路径
    List<List<String>> allPaths = new ArrayList<>();  // 存储所有最短路径
    List<Integer> pathLengths = new ArrayList<>();  // 存储路径长度

    List<String> path = new ArrayList<>();  // 当前路径
    List<Iterator<String>> iterators = new ArrayList<>();  // 存储每个节点的前驱节点的迭代器

    path.add(word2);  // 从终点开始构建路径
    iterators.add(predecessors.get(word2).iterator());  // 初始化迭代器

    while (!path.isEmpty()) {
      if (path.get(path.size() - 1).equals(word1)) {  // 找到一条完整路径
        List<String> fullPath = new ArrayList<>(path);
        Collections.reverse(fullPath);  // 反转路径
        int length = calculatePathLength(fullPath, this.wordsMap);  // 计算路径长度
        allPaths.add(fullPath);  // 添加到所有路径列表中
        pathLengths.add(length);  // 存储路径长度
        path.remove(path.size() - 1);  // 回溯
        iterators.remove(iterators.size() - 1);  // 移除对应的迭代器
      } else {
        Iterator<String> it = iterators.get(iterators.size() - 1);
        if (it.hasNext()) {  // 还有前驱节点可以遍历
          String predecessor = it.next();
          path.add(predecessor);  // 添加前驱节点到路径中
          iterators.add(predecessors.get(predecessor).iterator());  // 为前驱节点添加新的迭代器
        } else {  // 当前节点的前驱节点遍历完毕
          path.remove(path.size() - 1);  // 回溯
          iterators.remove(iterators.size() - 1);  // 移除对应的迭代器
        }
      }
    }
    if (!allPaths.isEmpty()) {
      for (int i = 0; i < allPaths.size(); i++) {
        List<String> p = allPaths.get(i);
        int length = pathLengths.get(i);
        allPaths.get(i).add("路径长度为：" + length);
        System.out.println(String.join(" -> ", p) + " (Length: " + length + ")");
      }
    } else {
      List<List<String>> result = new ArrayList<>();
      result.add(Arrays.asList("没有找到路径"));
      return result;  // 返回包含错误消息的列表
    }
    return allPaths;
  }

  private int calculatePathLength(List<String> path, Map<String, Map<String, Integer>> wordsMap) {
    int length = 0;
    for (int i = 0; i < path.size() - 1; i++) {
      String currentNode = path.get(i);
      String nextNode = path.get(i + 1);
      Map<String, Integer> neighbors = wordsMap.get(currentNode);
      length += neighbors.get(nextNode);  // 加上当前边的权重
    }
    return length;
  }
  /**
  * 计算所有最短路径.
  */

  public Map<String, List<List<String>>> calcAllShortestPathsToAll(String word1) {
    word1 = word1.toLowerCase();
    Map<String, List<List<String>>> allPathsToAll = new HashMap<>();
    Set<String> allNodes = getAllNodes();
    // 检查源节点是否存在于图中
    if (!allNodes.contains(word1)) {
      return allPathsToAll;  // 返回空的路径集合
    }
    // 对每一个节点（除了自己），计算从word1到该节点的最短路径
    for (String target : allNodes) {
      if (!target.equals(word1)) {
        List<List<String>> paths = calcAllShortestPaths(word1, target);
        allPathsToAll.put(target, paths);
      }
    }

    return allPathsToAll;
  }
  /**
  * 获取所有节点.
  */

  public Set<String> getAllNodes() {
    Set<String> allNodes = new HashSet<>(this.wordsMap.keySet());  // 先添加所有的键（源节点）

    // 现在添加所有的值（目标节点），确保没有遗漏
    for (Map<String, Integer> neighbors : this.wordsMap.values()) {
      allNodes.addAll(neighbors.keySet());
    }
    return allNodes;
  }
  /**
  * 随机游走.
  */

  public String randomWalk() {
    if (!walking) {
      List<String> nodes = new ArrayList<>(wordsMap.keySet());
      if (nodes.isEmpty()) {
        return "图中没有节点";
      }
      currentNode = nodes.get(random.nextInt(nodes.size()));
      path.clear();
      path.add(currentNode);
      visitedEdges.clear();
      walking = true;
    }

    Map<String, Integer> neighbors = getNeighbors(currentNode);
    if (neighbors.isEmpty()) {
      walking = false;
      writePathToFile();
      return "游走结束,不存在边";
    }

    List<String> availableNeighbors = new ArrayList<>(neighbors.keySet());
    String nextNode = availableNeighbors.get(random.nextInt(availableNeighbors.size()));
    String edge = currentNode + "->" + nextNode;

    if (visitedEdges.contains(edge)) {
      walking = false;
      path.add(nextNode);
      writePathToFile();
      String end = " -> " + nextNode;
      return end + "游走结束，发现重复边";
    }

    visitedEdges.add(edge);
    path.add(nextNode);
    currentNode = nextNode;
    return String.join(" -> ", path);
  }


  private void writePathToFile() {
    try (FileWriter writer = new FileWriter("random_walk.txt", StandardCharsets.UTF_8, false)) {
      for (int i = 0; i < path.size(); i++) {
        writer.write(path.get(i));
        if (i < path.size() - 1) {
          writer.write(" -> "); // 添加节点之间的箭头
        }
      }
      writer.write("\n"); // 换行符表示路径结束
      System.out.println("路径已写入random_walk.txt");
    } catch (IOException e) {
      System.err.println("无法写入文件: " + e.getMessage());
    }

  }
  /**
  * 重置随机游走状态.
  */

  public void resetWalk() {
    walking = false;
    currentNode = null;
    path.clear();
    visitedEdges.clear();
  }
}
