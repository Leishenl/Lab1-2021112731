package org.example.lab1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 读取文本.
 */

public class GraphUtils {
  /**
  * 读取文本.
  */

  public static List<String> readWordsFromFile(File file) {
    // 确保包括对文本的预处理，如下面的示例
    List<String> words = new ArrayList<>();
    try (Scanner scanner = new Scanner(file, "UTF-8")) {
      scanner.useDelimiter("\\s+|,\\s*|\\.\\s*"); // 使用空白符或逗号、点后跟空白符作为分隔符
      while (scanner.hasNext()) {
        String word = scanner.next().toLowerCase().replaceAll("[^a-zA-Z]", ""); // 清洗非字母字符
        if (!word.isEmpty()) {
          words.add(word);
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }
    return words;
  }
}
