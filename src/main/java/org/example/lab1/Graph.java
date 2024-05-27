package org.example.lab1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Graph {
    private Map<String, Map<String, Integer>> wordsMap; // 图结构，存储节点及其邻居和边权重
    private String currentNode;
    private boolean walking = false; // 表示是否正在进行随机游走

    public boolean isWalking() {
        return walking;
    }

    private Random random = new Random();
    private List<String> path = new ArrayList<>(); // 存储随机游走的路径
    private Set<String> visitedEdges = new HashSet<>(); // 跟踪已访问的边，防止重复访问
    private List<String> Words;

    public List<String> getWords() {
        return Words;
    }

    public void setWords(List<String> words) {
        Words = words;
    }

    public Map<String, Map<String, Integer>> getWordsMap() {
        return wordsMap;
    }

    public void setWordsMap(Map<String, Map<String, Integer>> wordsMap) {
        this.wordsMap = wordsMap;
    }

    // 构造函数初始化 wordsMap 为一个 HashMap
    public Graph() {
        wordsMap = new HashMap<>();
    }

    // 添加边，若边已存在则更新权重
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

    // 根据单词列表创建有向图
    public void createGraph(List<String> words) {
        for (int i = 0; i < words.size() - 1; i++) {
            String word1 = words.get(i);
            String word2 = words.get(i + 1);
            addEdge(word1, word2);
        }
    }

    // 获取指定节点的邻居及其权重
    public Map<String, Integer> getNeighbors(String node) {
        node = node.toLowerCase();
        if (this.wordsMap.containsKey(node)) {
            return this.wordsMap.get(node);
        } else {
            return new HashMap<>();
        }
    }

    // 打印有向图信息
    public void showDirectedGraph(Graph p) {
        System.out.println("-------------------------------有向带权图------------------------------");
        for (String node : p.getWordsMap().keySet()) {
            System.out.print(node + "->");
            System.out.print("邻居: ");
            Map<String, Integer> neighbors = getNeighbors(node);
            for (String neighbor : neighbors.keySet()) {
                int weight = neighbors.get(neighbor);
                System.out.print("(" + neighbor + "," + weight + ")  ");
            }
            System.out.println();
        }
        System.out.println("----------------------------------------------------------------------");
    }

    // 生成用于 Graphviz 可视化的 DOT 文件
    public void generateDotFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("digraph G {\n");
            for (String node : this.wordsMap.keySet()) {
                Map<String, Integer> neighbors = this.getNeighbors(node);
                for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                    String neighbor = entry.getKey();
                    int weight = entry.getValue();
                    writer.write("    \"" + node + "\" -> \"" + neighbor + "\" [label=\"" + weight + "\"];\n");
                }
            }
            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("写入 DOT 文件时出错: " + e.getMessage());
        }
    }

    // 使用 Graphviz 渲染图形
    public void renderGraph(String dotFilePath, String outputFilePath) {
        try {
            String dotCommand = "C:\\Program Files\\Graphviz\\bin\\dot";
            ProcessBuilder pb = new ProcessBuilder(dotCommand, "-Tpng", dotFilePath, "-o", outputFilePath);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("渲染图形时出错: " + e.getMessage());
        }
    }

    // 查找两个单词之间的桥接词
    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        // 检查输入单词是否存在于图中
        if (!this.wordsMap.containsKey(word1) || !this.wordsMap.containsKey(word2)) {
            return "图中不存在 word1 或 word2！";
        }

        List<String> bridgeWords = new ArrayList<>();
        Map<String, Integer> neighbors1 = this.getNeighbors(word1);

        // 查找桥接词
        for (String neighbor_Word : neighbors1.keySet()) {
            Map<String, Integer> neighbors_BridgeWord = this.getNeighbors(neighbor_Word);
            if (neighbors_BridgeWord.containsKey(word2)) {
                bridgeWords.add(neighbor_Word);
            }
        }

        // 根据搜索结果返回相应的信息
        if (bridgeWords.isEmpty()) {
            return "从 " + word1 + " 到 " + word2 + " 没有桥接词！";
        } else {
            return "从 " + word1 + " 到 " + word2 + " 的桥接词是: " + String.join(", ", bridgeWords) + "。";
        }
    }

    // 通过在单词对之间插入桥接词生成新文本
    public String generateNewText(String inputText) {
        System.out.println("----------------------------生成新文本-------------------------");
        String[] new_words = inputText.split("\\s+");
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < new_words.length - 1; i++) {
            String word1 = new_words[i];
            String word2 = new_words[i + 1];
            newText.append(word1).append(" ");
            String bridgeWords = queryBridgeWords(word1, word2);
            if (bridgeWords.contains("是")) {
                String bridgeWordsPart = bridgeWords.substring(bridgeWords.indexOf("是: ") + 3, bridgeWords.length() - 1);
                String[] bridgeWord = bridgeWordsPart.split(", ");
                String wordsWithBrackets = Arrays.toString(bridgeWord);
                String trimmedWords = wordsWithBrackets.substring(1, wordsWithBrackets.length() - 1);
                String[] wordsArray = trimmedWords.split(", ");
                Random random = new Random();
                String selectedWord = wordsArray[random.nextInt(wordsArray.length)];
                newText.append(selectedWord).append(" ");
            }
        }
        newText.append(new_words[new_words.length - 1]);
        return newText.toString();
    }

    // 使用改进的 Dijkstra 算法计算两个单词之间的所有最短路径
    public List<List<String>> calcAllShortestPaths(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        Set<String> allNodes = getAllNodes();
        if (!allNodes.contains(word1) || !allNodes.contains(word2)) {
            List<List<String>> result = new ArrayList<>();
            result.add(Arrays.asList("节点不存在"));
            return result;
        }
        Map<String, Integer> distance = new HashMap<>();
        Map<String, List<String>> predecessors = new HashMap<>();
        Set<String> visited = new HashSet<>();
        for (String node : this.Words) {
            distance.put(node, Integer.MAX_VALUE);
            predecessors.put(node, new ArrayList<>());
        }
        distance.put(word1, 0);
        Set<String> unvisited = new HashSet<>(this.Words);
        while (!unvisited.isEmpty()) {
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
                break;
            }
            unvisited.remove(u);
            visited.add(u);
            Map<String, Integer> neighbors = this.wordsMap.get(u);
            if (neighbors != null) {
                for (String v : neighbors.keySet()) {
                    int weight = neighbors.get(v);
                    int new_distance = distance.get(u) + weight;
                    if (new_distance < distance.get(v)) {
                        distance.put(v, new_distance);
                        predecessors.get(v).clear();
                        predecessors.get(v).add(u);
                    } else if (new_distance == distance.get(v)) {
                        predecessors.get(v).add(u);
                    }
                }
            }
        }

        List<List<String>> allPaths = new ArrayList<>();
        List<Integer> pathLengths = new ArrayList<>();
        List<String> path = new ArrayList<>();
        List<Iterator<String>> iterators = new ArrayList<>();
        path.add(word2);
        iterators.add(predecessors.get(word2).iterator());

        while (!path.isEmpty()) {
            if (path.get(path.size() - 1).equals(word1)) {
                List<String> fullPath = new ArrayList<>(path);
                Collections.reverse(fullPath);
                allPaths.add(fullPath);
                int length = fullPath.size() - 1;
                pathLengths.add(length);
                path.remove(path.size() - 1);
                iterators.remove(iterators.size() - 1);
            } else {
                Iterator<String> it = iterators.get(iterators.size() - 1);
                if (it.hasNext()) {
                    String predecessor = it.next();
                    path.add(predecessor);
                    iterators.add(predecessors.get(predecessor).iterator());
                } else {
                    path.remove(path.size() - 1);
                    iterators.remove(iterators.size() - 1);
                }
            }
        }

        System.out.print("从 " + word1 + " 到 " + word2 + " 的最短路径: ");
        System.out.println();
        if (!allPaths.isEmpty()) {
            for (int i = 0; i < allPaths.size(); i++) {
                List<String> p = allPaths.get(i);
                int length = pathLengths.get(i);
                allPaths.get(i).add("路径长度为：" + length);
                System.out.println(String.join(" -> ", p) + " (长度: " + length + ")");
            }
        } else {
            List<List<String>> result = new ArrayList<>();
            result.add(Arrays.asList("没有找到路径"));
            return result;
        }
        System.out.println("---------------------------------------------------------------");
        return allPaths;
    }

    // 计算从给定单词到所有其他节点的所有最短路径
    public Map<String, List<List<String>>> calcAllShortestPathsToAll(String word1) {
        word1 = word1.toLowerCase();
        Map<String, List<List<String>>> allPathsToAll = new HashMap<>();
        Set<String> allNodes = getAllNodes();
        if (!allNodes.contains(word1)) {
            return allPathsToAll;
        }
        for (String target : allNodes) {
            if (!target.equals(word1)) {
                List<List<String>> paths = calcAllShortestPaths(word1, target);
                allPathsToAll.put(target, paths);
            }
        }
        return allPathsToAll;
    }

    // 获取图中的所有节点
    public Set<String> getAllNodes() {
        Set<String> allNodes = new HashSet<>(this.wordsMap.keySet());
        for (Map<String, Integer> neighbors : this.wordsMap.values()) {
            allNodes.addAll(neighbors.keySet());
        }
        return allNodes;
    }

    // 在图中进行随机游走
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
            return "游走结束, 不存在边";
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

    // 将随机游走路径写入文件
    private void writePathToFile() {
        try (FileWriter writer = new FileWriter("random_walk.txt", false)) {
            for (int i = 0; i < path.size(); i++) {
                writer.write(path.get(i));
                if (i < path.size() - 1) {
                    writer.write(" -> ");
                }
            }
            writer.write("\n");
            System.out.println("路径已写入random_walk.txt");
        } catch (IOException e) {
            System.err.println("无法写入文件: " + e.getMessage());
        }
    }

    // 重置随机游走
    public void resetWalk() {
        walking = false;
        currentNode = null;
        path.clear();
        visitedEdges.clear();
    }
}
