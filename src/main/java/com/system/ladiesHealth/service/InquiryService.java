package com.system.ladiesHealth.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.system.ladiesHealth.component.client.OpenAIClient;
import com.system.ladiesHealth.dao.DiseaseRepository;
import com.system.ladiesHealth.dao.InquiryTopicsRepository;
import com.system.ladiesHealth.domain.po.DiseasePO;
import com.system.ladiesHealth.domain.po.InquiryRecordPO;
import com.system.ladiesHealth.domain.po.InquiryTopicsPO;
import com.system.ladiesHealth.domain.pojo.openAI.CompletionsReq;
import com.system.ladiesHealth.domain.pojo.openAI.CompletionsRes;
import com.system.ladiesHealth.domain.pojo.openAI.EmbeddingsReq;
import com.system.ladiesHealth.domain.vo.InquiryRecordVO;
import com.system.ladiesHealth.domain.vo.InquiryTopicsVO;
import com.system.ladiesHealth.domain.vo.SignReportVO;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.exception.BusinessException;
import com.system.ladiesHealth.utils.Matrix;
import com.system.ladiesHealth.utils.convert.InquiryConvert;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InquiryService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private OpenAIClient openAIClient;

    @Autowired
    private PersonalService personalService;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private InquiryTopicsRepository inquiryTopicsRepository;

    @Autowired
    private InquiryConvert inquiryConvert;

    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${openai.api-key}")
    private String apiKey;

    // 新增主题
    // 入参 首次问诊信息
    // 出参 主题id
    public Res<String> registerInquiry(String message, String createUserId) {

        SignReportVO signReportVO = personalService.getSignReport(createUserId).getData();
        // 构建 message
        String physicalCondition = "年龄：" + signReportVO.getAge() + "岁\n" +
                "身高：" + signReportVO.getHeight() + "cm\n" +
                "体重：" + signReportVO.getWeight() + "kg\n" +
                "血压：" + signReportVO.getBloodPressure() + "mmHg\n" +
                "心率：" + signReportVO.getHeartRate() + "次/分\n" +
                "平均睡眠时长：" + String.format("%.2f", signReportVO.getAvgSleepTime()) + "小时\n" +
                "平均运动时长：" + String.format("%.2f", signReportVO.getAvgExerciseTime()) + "小时\n" +
                "平均每日饮水量：" + String.format("%.2f", signReportVO.getAvgDrinkWater()) + "\n" +
                "平均每日饮水次数：" + String.format("%.2f", signReportVO.getAvgDrinkTimes()) + "次";

        InquiryTopicsPO inquiryTopicsPO = new InquiryTopicsPO();
        inquiryTopicsPO.setPhysicalCondition(physicalCondition);
        inquiryTopicsPO.setTitle(getTopicTitle(message));

        // 计算问题相似度
        List<Double> vector = getVector(message);
        // 归纳问题主题
        inquiryTopicsPO.setDiseases(topKSimilarDisease(vector, 4));

        // 首次会话记录
        InquiryRecordPO inquiryRecordPO = new InquiryRecordPO();
        inquiryRecordPO.setMessage(message);
        inquiryRecordPO.setRole("user");
        inquiryTopicsPO.setRecords(List.of(new InquiryRecordPO("user", message)));

        return Res.ok(inquiryTopicsRepository.save(inquiryTopicsPO).getId());
    }

    // 继续问诊
    // 入参 主题id，问诊信息
    // 出参 主题id
    public Res<String> continueInquiry(String topicId, String message) {
        InquiryTopicsPO inquiryTopicsPO = inquiryTopicsRepository.findById(topicId).orElseThrow(() -> new BusinessException("问诊会话不存在"));
        List<InquiryRecordPO> records = inquiryTopicsPO.getRecords();
        if (records.get(records.size() - 1).getRole().equals("user")) {
            throw new BusinessException("请等待 Bot 回复后再进一步提问");
        } else {
            records.add(new InquiryRecordPO("user", message));
        }
        return Res.ok(inquiryTopicsRepository.save(inquiryTopicsPO).getId());
    }

    // 获取问诊记录
    public Res<List<InquiryRecordVO>> getInquiryRecords(String topicId) {
        InquiryTopicsPO inquiryTopicsPO = inquiryTopicsRepository.findById(topicId).orElseThrow(() -> new BusinessException("问诊会话不存在"));
        return Res.ok(inquiryConvert.generateInquiryRecordVOListByInquiryRecordPOList(inquiryTopicsPO.getRecords()));
    }

    public Res<List<InquiryTopicsVO>> getInquiryTopics(String userId) {
        return Res.ok(inquiryTopicsRepository.findAllByCreateUserId(userId).stream().map(inquiryConvert::generateInquiryTopicsVOByInquiryTopicsPO).collect(Collectors.toList()));
    }

    public SseEmitter getCompletions(String topicId) {
        SseEmitter emitter = new SseEmitter();

        // 日志记录
        emitter.onCompletion(() -> log.info("TopicId: {} SSE Completion", topicId));
        emitter.onTimeout(() -> log.info("TopicId: {} SSE Timeout", topicId));

        taskExecutor.execute(() -> {
            sseTask(topicId, emitter);
            emitter.complete();
        });
        return emitter;
    }


    /*
    ------------------------------- 私有实现 -------------------------------
     */

    @SneakyThrows
    private void sseTask(String topicId, SseEmitter emitter) {
        InquiryTopicsPO inquiryTopicsPO = inquiryTopicsRepository.findById(topicId).orElse(null);

        if (inquiryTopicsPO == null) {
            emitter.send(SseEmitter.event().name("error").data("问诊会话不存在"));
            return;
        }
        if (inquiryTopicsPO.getRecords().get(inquiryTopicsPO.getRecords().size() - 1).getRole().equals("bot")) {
            return;
        }

        List<CompletionsReq.Messages> prompt = new ArrayList<>();
        prompt.add(new CompletionsReq.Messages("system", "你是一个专业的医师，我会给你提供一些病人的基本信息，以及一些可能与患者疾病相关的医学知识。请根据这些信息，给出一个合理的给出具体病情分析和建议。"));

        StringBuilder sb = new StringBuilder();
        sb.append("病人基本信息：\n\n").append(inquiryTopicsPO.getPhysicalCondition()).append("\n\n");
        sb.append("可能相关的医学知识: \n\n");
        for (DiseasePO diseasePO : inquiryTopicsPO.getDiseases()) {
            for (String sub : diseasePO.getContent().split(" ")) {
                if (sub.startsWith("疾病名称") || sub.startsWith("药物治疗") || sub.startsWith("多发群体")
                        || sub.startsWith("高危因素") || sub.startsWith("病因") || sub.startsWith("治疗方案") || sub.startsWith("发病部位")) {
                    sb.append(sub).append("\n");
                }
            }
            sb.append("\n");
        }
        sb.append("患者自述：\n").append(inquiryTopicsPO.getRecords().get(0).getMessage());
        prompt.add(new CompletionsReq.Messages("user", sb.toString()));
        // 排除第一项
        for (InquiryRecordPO record : inquiryTopicsPO.getRecords().subList(1, inquiryTopicsPO.getRecords().size())) {
            prompt.add(new CompletionsReq.Messages(record.getRole(), record.getMessage()));
        }

        CompletionsReq completionsReq = new CompletionsReq("gpt-3.5-turbo", prompt, 0.4, true);
        // 构建 okhttp 请求
        RequestBody requestBody = RequestBody.create(JSONUtil.toJsonStr(completionsReq), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        StringBuilder target = new StringBuilder();

        // 发送请求
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                emitter.send(SseEmitter.event().name("error").data("请求失败"));
                return;
            }
            // 流式读取
            try (BufferedReader reader = new BufferedReader(response.body().charStream())) {
                String line;
                while ((line = reader.readLine()) != null && !line.startsWith("data: [DONE]")) {
                    // 读取到数据
                    if (line.startsWith("data:")) {
                        // 解析数据
                        CompletionsRes completionsRes = JSONUtil.toBean(line.substring(6), CompletionsRes.class);
                        // 发送数据
                        String val = completionsRes.getChoices()[0].getDelta().getContent();
                        if (null == val || val.length() == 0) {
                            continue;
                        }
                        emitter.send(SseEmitter.event().data(val));
                        target.append(val);
                    }
                }
            }
        }

        // 保存问诊记录
        inquiryTopicsPO.getRecords().add(new InquiryRecordPO("bot", target.toString()));
        inquiryTopicsRepository.save(inquiryTopicsPO);
    }


    // 计算问题相似度
    private List<Double> getVector(String message) {
        EmbeddingsReq embeddingsReq = new EmbeddingsReq(message, "text-embedding-ada-002");
        return openAIClient.embedding(embeddingsReq).getData().get(0).getEmbedding();
    }

    // 归纳问题主题
    private String getTopicTitle(String message) {
        String prompt = String.format("现有问诊会话问题：%s\n\n请帮我对此总结此次会话的主题，要求尽量简短，并且无其余语句以及标点符号直接输出主题名称即可.", message);
        String result = openAIClient.completion(new CompletionsReq(prompt)).getChoices()[0].getMessage().getContent();
        return ReUtil.replaceAll(result, "[\\pP\\p{Punct}]", "");
    }

    /*
    根据用户输入的 p，计算问题的相似度，返回相似度最高的前K个 Disease
     */
    @SneakyThrows
    private List<DiseasePO> topKSimilarDisease(List<Double> vector, int k) {
        @Data
        @EqualsAndHashCode(callSuper = true)
        class PriorityPojo extends DiseasePO {
            private double similarity;
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
                    List<Double> diseaseVector = JSONUtil.toList(diseasePO.getVector(), Double.class);
                    double similarity = Matrix.cosineSimilarity(vector, diseaseVector);
                    // obj clone
                    PriorityPojo pj = BeanUtil.copyProperties(diseasePO, PriorityPojo.class);
                    pj.setSimilarity(similarity);
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
        return priorityQueue.stream().map(p -> (DiseasePO) p).collect(Collectors.toList());
    }


}
