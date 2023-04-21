package com.system.ladiesHealth.service;

import cn.hutool.json.JSONUtil;
import com.system.ladiesHealth.component.client.OpenAIClient;
import com.system.ladiesHealth.dao.DiseaseRepository;
import com.system.ladiesHealth.domain.po.DiseasePO;
import com.system.ladiesHealth.domain.pojo.openAI.EmbeddingsReq;
import com.system.ladiesHealth.utils.Matrix;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class ConsultationService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private OpenAIClient openAIClient;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    // 计算问题相似度
    public List<Double> getVector(String message) {
        EmbeddingsReq embeddingsReq = new EmbeddingsReq(message, "text-embedding-ada-002");
        return openAIClient.embedding(embeddingsReq).getData().get(0).getEmbedding();
    }

    /*
    根据用户输入的 p，计算问题的相似度，返回相似度最高的前K个 Disease
     */
    @SneakyThrows
    public List<String> topKSimilarDisease(List<Double> vector, int k) {
        @Data
        @AllArgsConstructor
        class PriorityPojo {
            private final double similarity;
            private final String content;
        }
        // 优先队列
        Queue<PriorityPojo> priorityQueue = new PriorityBlockingQueue<>(k, Comparator.comparingDouble(o -> o.similarity));

        long diseaseCount = diseaseRepository.count();
        // 等待锁
        CountDownLatch countDownLatch = new CountDownLatch((int) (diseaseCount / 10));
        for (int i = 0; i * 10L < diseaseCount; i++) {
            int finalI = i;
            taskExecutor.execute(() -> {
                Pageable pageable = Pageable.ofSize(10).withPage(finalI);
                List<DiseasePO> diseasePOs = diseaseRepository.findAll(pageable).getContent();
                for (DiseasePO diseasePO : diseasePOs) {
                    // 计算相似度
                    List<Double> diseaseVector = JSONUtil.toList(JSONUtil.parseArray(diseasePO.getVector()), Double.class);
                    double similarity = Matrix.cosineSimilarity(vector, diseaseVector);
                    PriorityPojo pj = new PriorityPojo(similarity, diseasePO.getContent());
                    if (priorityQueue.size() < k) {
                        priorityQueue.add(pj);
                    } else {
                        if (priorityQueue.peek() != null && priorityQueue.peek().similarity < similarity) {
                            priorityQueue.poll();
                            priorityQueue.add(pj);
                        }
                    }
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        return priorityQueue.stream().map(PriorityPojo::getContent).toList();
    }


}
