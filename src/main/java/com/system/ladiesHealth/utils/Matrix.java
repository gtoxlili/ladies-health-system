package com.system.ladiesHealth.utils;

// --add-modules jdk.incubator.vector

import com.system.ladiesHealth.exception.BusinessException;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.List;

public class Matrix {

    // 计算两个向量的余弦相似度
    public static Double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1 == null || v2 == null || v1.size() != v2.size()) {
            throw new BusinessException("Input vectors must have the same size and cannot be null.");
        }
        return cosineSimilarity(v1.stream().mapToDouble(Double::doubleValue).toArray(), v2.stream().mapToDouble(Double::doubleValue).toArray());
    }

    public static Double cosineSimilarity(double[] v1, double[] v2) {

        VectorSpecies<Double> species = DoubleVector.SPECIES_PREFERRED;

        double dotProduct = 0.0;
        double v1Magnitude = 0.0;
        double v2Magnitude = 0.0;

        for (int i = 0; i < v1.length; i += species.length()) {
            DoubleVector v1Vector = DoubleVector.fromArray(species, v1, i);
            DoubleVector v2Vector = DoubleVector.fromArray(species, v2, i);

            dotProduct += v1Vector.mul(v2Vector).reduceLanes(VectorOperators.ADD);

            v1Magnitude += v1Vector.mul(v1Vector).reduceLanes(VectorOperators.ADD);
            v2Magnitude += v2Vector.mul(v2Vector).reduceLanes(VectorOperators.ADD);
        }

        if (v1Magnitude == 0.0 || v2Magnitude == 0.0) {
            throw new BusinessException("Input vectors must not be zero vectors.");
        }
        return dotProduct / (Math.sqrt(v1Magnitude) * Math.sqrt(v2Magnitude));
    }

}
