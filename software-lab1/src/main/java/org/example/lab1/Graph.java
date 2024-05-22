package org.example.lab1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Graph {
    private Map<String, Map<String, Integer>> wordsMap;
    private String currentNode;
    private boolean walking = false;

    public boolean isWalking() {
        return walking;
    }
    private Random random = new Random();
    private List<String> path = new ArrayList<>();
    private Set<String> visitedEdges = new HashSet<>();
    private List<String> Words;
    public List<String> getWords() {
        return Words;
    }
    public void setWords(List<String> words) {
        Words = words;
    }
    public void setcurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }
    public Map<String, Map<String, Integer>> getWordsMap() {
        return wordsMap;
    }

    public void setWordsMap(Map<String, Map<String, Integer>> wordsMap) {
        this.wordsMap = wordsMap;
    }
    public Graph() {
        wordsMap = new HashMap<>();
    }

    // 添加边，更新权重
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

    // 根据单词列表生成有向图
    public void createGraph(List<String> words) {
        for (int i = 0; i < words.size() - 1; i++) {
            String word1 = words.get(i);
            String word2 = words.get(i + 1);
            addEdge(word1, word2);
        }
    }

    // 获取节点的邻居及其权重
    public Map<String, Integer> getNeighbors(String node) {
        node = node.toLowerCase();
        if (this.wordsMap.containsKey(node)) {
            return this.wordsMap.get(node);
        } else {
            return new HashMap<>();
        }
    }
    // 打印图的信息
    public void showDirectedGraph(Graph p) {
        System.out.println("-------------------------------有向带权图------------------------------");
        for (String node : p.getWordsMap().keySet()) {
            System.out.print(node+"->");
            System.out.print("Neighbors: ");
            Map<String, Integer> neighbors = getNeighbors(node);
            for (String neighbor : neighbors.keySet()) {
                int weight = neighbors.get(neighbor);
                System.out.print("(" + neighbor + "," + weight+")  ");
            }
            System.out.println();
        }
      
    }
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
            System.err.println("Error writing DOT file: " + e.getMessage());
        }
    }

    // 调用Graphviz来生成图像的方法
    public void renderGraph(String dotFilePath, String outputFilePath) {
        try {
            String dotCommand = "C:\\Program Files\\Graphviz\\bin\\dot";
            ProcessBuilder pb = new ProcessBuilder(dotCommand, "-Tpng", dotFilePath, "-o", outputFilePath);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error rendering graph : " + e.getMessage());
        }
    }

    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        // 检查输入的单词是否在图中出现
        if (!this.wordsMap.containsKey(word1) || !this.wordsMap.containsKey(word2)) {
            return "No word1 or word2 in the graph!";
        }

        List<String> bridgeWords = new ArrayList<>();
        Map<String, Integer> neighbors1 = this.getNeighbors(word1);

        // 在图中查找桥接词
        for (String neighbor_Word : neighbors1.keySet()) {
            Map<String, Integer> neighbors_BridgeWord = this.getNeighbors(neighbor_Word);
            if (neighbors_BridgeWord.containsKey(word2)) {
                bridgeWords.add(neighbor_Word);
            }
        }

        // 根据查找结果返回相应的消息
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords) + ".";
        }
    }
    public String generateNewText(String inputText) {
        System.out.println("----------------------------生成了新文本-------------------------");
        String[] new_words = inputText.split("\\s+");
        // 对每对相邻单词进行处理
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < new_words.length - 1; i++) {
            String word1 =new_words[i];
            String word2 = new_words[i + 1];
            newText.append(word1).append(" "); // 将单词1添加到新文本中
            // 查找桥接词
            String bridgeWords = queryBridgeWords(word1, word2);
            if(bridgeWords.contains("are")){
                // 提取 'are: ' 后面的所有文本直到句点前
                String bridgeWordsPart = bridgeWords.substring(bridgeWords.indexOf("are: ") + 5, bridgeWords.length() - 1);
                // 分割提取的部分来获取单独的桥接词
                String[] bridgeWord = bridgeWordsPart.split(", ");
                String wordsWithBrackets = Arrays.toString(bridgeWord);
                // 去除首尾的方括号
                String trimmedWords = wordsWithBrackets.substring(1, wordsWithBrackets.length() - 1);

                // 使用逗号分割字符串为数组
                String[] wordsArray = trimmedWords.split(", ");

                // 随机选择一个元素
                Random random = new Random();
                String selectedWord = wordsArray[random.nextInt(wordsArray.length)]; // 随机选取一个元素

                newText.append(selectedWord).append(" "); // 将桥接词添加到新文本中
            }
        }
        newText.append(new_words[new_words.length - 1]); // 将最后一个单词添加到新文本中
        return newText.toString();
    }


    public List<List<String>> calcAllShortestPaths(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        Set<String> allNodes = getAllNodes();
        // 检查源节点和目标节点是否存在于图中
        if (!allNodes.contains(word1) || !allNodes.contains(word2)) {
            List<List<String>> result = new ArrayList<>();
            result.add(Arrays.asList("节点不存在"));
            return result;  // 返回包含错误消息的列表
        }
        // 初始化距离和前驱节点
        Map<String, Integer> distance = new HashMap<>();  // 存储每个节点的最短距离
        Map<String, List<String>> predecessors = new HashMap<>();  // 存储每个节点的前驱节点列表
        Set<String> visited = new HashSet<>();  // 记录已访问的节点
        for (String node : this.Words) {
            distance.put(node, Integer.MAX_VALUE);  // 初始化所有节点的距离为无限大
            predecessors.put(node, new ArrayList<>());  // 初始化前驱节点列表为空
        }
        distance.put(word1, 0);  // 起点的距离设为0
        // 使用一个集合来模拟优先队列
        Set<String> unvisited = new HashSet<>(this.Words);
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
            visited.add(u);
            Map<String, Integer> neighbors = this.wordsMap.get(u);
            if (neighbors != null) {
                for (String v : neighbors.keySet()) {
                    int weight = neighbors.get(v);
                    int new_distance = distance.get(u) + weight;
                    if (new_distance < distance.get(v)) {  // 找到更短的路径
                        distance.put(v, new_distance);
                        predecessors.get(v).clear();  // 清空之前的前驱节点
                        predecessors.get(v).add(u);  // 添加新的前驱节点
                    } else if (new_distance == distance.get(v)) {  // 找到相同长度的路径
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
                allPaths.add(fullPath);  // 添加到所有路径列表中
                int length = fullPath.size() - 1;  // 路径长度为节点数减一
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
        // 打印所有最短路径
        System.out.print("Shortest paths from " + word1 + " to " + word2 + ": ");
        System.out.println();
        if (!allPaths.isEmpty()) {
            for (int i = 0; i < allPaths.size(); i++) {
                List<String> p = allPaths.get(i);
                int length = pathLengths.get(i);
                allPaths.get(i).add("路径长度为："+ length);
                System.out.println(String.join(" -> ", p) + " (Length: " + length + ")");
            }
        } else {
            List<List<String>> result = new ArrayList<>();
            result.add(Arrays.asList("没有找到路径"));
            return result;  // 返回包含错误消息的列表
        }
        System.out.println("---------------------------------------------------------------");
        return allPaths;
    }

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
    public Set<String> getAllNodes() {
        Set<String> allNodes = new HashSet<>(this.wordsMap.keySet());  // 先添加所有的键（源节点）

        // 现在添加所有的值（目标节点），确保没有遗漏
        for (Map<String, Integer> neighbors : this.wordsMap.values()) {
            allNodes.addAll(neighbors.keySet());
        }

        return allNodes;
    }
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
            return end+"游走结束，发现重复边";
        }

        visitedEdges.add(edge);
        path.add(nextNode);
        currentNode = nextNode;
        return String.join(" -> ", path);
    }


    private void writePathToFile() {
        try (FileWriter writer = new FileWriter("random_walk.txt", false)) { // 确保不是追加模式
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
    public void resetWalk() {
        walking = false;
        currentNode = null;
        path.clear();
        visitedEdges.clear();
    }
}
