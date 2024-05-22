package org.example.lab1;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class GraphUI extends Application {

    private Graph graph = new Graph();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("图");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // 载入文件
        Button loadFileButton = new Button("载入文件");
        TextArea fileContent = new TextArea();
        fileContent.setEditable(false);
        loadFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                List<String> words = GraphUtils.readWordsFromFile(file);
                graph.createGraph(words);
                graph.setWords(words);
                fileContent.setText(String.join(" ", words));
            }
        });

        // 展示有向图按钮
        Button displayGraphButton = new Button("有向图展示");
        displayGraphButton.setOnAction(e -> {
            String dotPath = "graph.dot";
            String imagePath = "graph.png";
            graph.showDirectedGraph(graph);
            graph.generateDotFile(dotPath);
            graph.renderGraph(dotPath, imagePath);
            // 创建新窗口显示图像
            showImageInNewStage(imagePath);
        });

        // 查询桥接词
        TextField word1Field = new TextField();
        word1Field.setPromptText("Word 1");
        TextField word2Field = new TextField();
        word2Field.setPromptText("Word 2");
        Button queryBridgeWordsButton = new Button("查询桥接词");
        TextArea bridgeWordsOutput = new TextArea();
        bridgeWordsOutput.setEditable(false);
        queryBridgeWordsButton.setOnAction(e -> {
            String bridgeWords = graph.queryBridgeWords(word1Field.getText(), word2Field.getText());
            bridgeWordsOutput.setText(bridgeWords != null ? bridgeWords : "No bridge words found.");
        });


        // 根据bridge word生成新文本
        TextField inputTextField = new TextField();
        inputTextField.setPromptText("放入文本");
        Button generateNewTextButton = new Button("生成了新文本");
        TextArea newTextOutput = new TextArea();
        newTextOutput.setEditable(false);
        generateNewTextButton.setOnAction(e -> {
            String newText = graph.generateNewText(inputTextField.getText());
            newTextOutput.setText(newText);
        });

        // 计算两个单词之间的最短路径
        TextField startNodeField = new TextField();
        startNodeField.setPromptText("起始节点");
        TextField endNodeField = new TextField();
        endNodeField.setPromptText("终止节点");
        Button calcShortestPathButton = new Button("计算最短路径");
        TextArea shortestPathOutput = new TextArea();
        shortestPathOutput.setEditable(false);
        calcShortestPathButton.setOnAction(e -> {
            String startNode = startNodeField.getText().trim();
            String endNode = endNodeField.getText().trim();

            if (startNode.isEmpty()) {
                shortestPathOutput.setText("请输入起始节点的名称：");
                return;
            }

            if (endNode.isEmpty()) {
                // 计算从起始节点到图中所有其他节点的最短路径
                Map<String, List<List<String>>> allPaths = graph.calcAllShortestPathsToAll(startNode);
                StringBuilder allPathsOutput = new StringBuilder();
                if (allPaths.isEmpty()) {
                    shortestPathOutput.setText("路径不存在");
                }
                else{
                    allPaths.forEach((target, paths) -> {
                        allPathsOutput.append("从 ").append(startNode).append(" 到 ").append(target).append(" 的最短路径:\n");
                        for (List<String> path : paths) {
                            allPathsOutput.append(String.join(" -> ", path)).append("\n");
                        }
                        allPathsOutput.append("\n");

                    });
                    shortestPathOutput.setText(allPathsOutput.toString());
                }
            } else {
                // 计算从起始节点到终止节点的最短路径
                List<List<String>> paths = graph.calcAllShortestPaths(startNode, endNode);
                if (paths.isEmpty() || paths.get(0).contains("路径不存在")) {
                    shortestPathOutput.setText("未找到路径，可能起始或终止单词不存在于图中。");
                } else {
                    StringBuilder pathsOutput = new StringBuilder();
                    for (List<String> path : paths) {
                        pathsOutput.append(String.join(" -> ", path)).append("\n");
                    }
                    shortestPathOutput.setText(pathsOutput.toString());
                }
            }
        });

        // 随机游走
        Button continueWalkButton = new Button("随机游走");
        Button stopWalkButton = new Button("停止游走");
        Button restartWalkButton = new Button("重新游走");
        TextArea randomWalkOutput = new TextArea();
        randomWalkOutput.setEditable(false);
        continueWalkButton.setOnAction(e -> {
            String result = graph.randomWalk();
            if (result.contains("游走结束")) {
                continueWalkButton.setDisable(true); // 游走结束时禁用按钮
                randomWalkOutput.appendText(result);
            }
            else randomWalkOutput.setText(result);
        });

        stopWalkButton.setOnAction(e -> {
            continueWalkButton.setDisable(true); // 停止游走并禁用继续按钮
            randomWalkOutput.setText(randomWalkOutput.getText() + "\n游走已停止.");
        });
        restartWalkButton.setOnAction(e -> {
            graph.resetWalk(); // 重置游走状态
            randomWalkOutput.setText("游走已重置，可以重新开始。\n");
            continueWalkButton.setDisable(false);  // 启用继续游走按钮
            stopWalkButton.setDisable(false);  // 保证停止按钮也是可用的
        });
        // Layout positioning
        grid.add(loadFileButton, 0, 0);
        grid.add(fileContent, 1, 0);
        grid.add(displayGraphButton, 0, 1);
        grid.add(word1Field, 0, 2);
        grid.add(word2Field, 1, 2);
        grid.add(queryBridgeWordsButton, 0, 3);
        grid.add(bridgeWordsOutput, 1, 3);
        grid.add(inputTextField, 0, 4);
        grid.add(generateNewTextButton, 1, 4);
        grid.add(newTextOutput, 1, 5);
        grid.add(startNodeField, 0, 6);
        grid.add(endNodeField, 1, 6);
        grid.add(calcShortestPathButton, 0, 7);
        grid.add(shortestPathOutput, 1, 7);
        grid.add(continueWalkButton, 0, 8);
        grid.add(stopWalkButton, 1, 8);
        grid.add(restartWalkButton, 2, 8);
        grid.add(randomWalkOutput, 0, 9, 3, 1);

        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void showImageInNewStage(String imagePath) {
        int i=0;
        Stage newStage = new Stage();
        newStage.setTitle("生成的图：");
        ImageView imageView = new ImageView(new Image("file:" + imagePath));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(600);
        imageView.setFitWidth(800);
        StackPane pane = new StackPane(imageView);
        Scene newScene = new Scene(pane, imageView.getFitWidth(), imageView.getFitHeight());
        newStage.setScene(newScene);
        newStage.show();
    }

}
