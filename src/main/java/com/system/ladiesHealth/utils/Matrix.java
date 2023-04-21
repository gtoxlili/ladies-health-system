package com.system.ladiesHealth.utils;

import java.util.List;

public class Matrix {

    // 计算两个向量的余弦相似度
    public static Double cosineSimilarity(List<Double> vector1, List<Double> vector2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
            normA += Math.pow(vector1.get(i), 2);
            normB += Math.pow(vector2.get(i), 2);
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        } else {
            return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        }
    }
}
