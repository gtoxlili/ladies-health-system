package com.system.ladiesHealth.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.system.ladiesHealth.component.client.MicroservicesClient;
import com.system.ladiesHealth.component.client.OpenAIClient;
import com.system.ladiesHealth.dao.DiseaseRepository;
import com.system.ladiesHealth.dao.InquiryTopicsRepository;
import com.system.ladiesHealth.domain.po.DiseasePO;
import com.system.ladiesHealth.domain.po.InquiryRecordPO;
import com.system.ladiesHealth.domain.po.InquiryTopicsPO;
import com.system.ladiesHealth.domain.pojo.openAI.CompletionsReq;
import com.system.ladiesHealth.domain.pojo.openAI.CompletionsRes;
import com.system.ladiesHealth.domain.pojo.openAI.EmbeddingsReq;
import com.system.ladiesHealth.domain.vo.*;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.exception.BusinessException;
import com.system.ladiesHealth.utils.Matrix;
import com.system.ladiesHealth.utils.RollbackUtil;
import com.system.ladiesHealth.utils.convert.InquiryConvert;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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
    private MicroservicesClient microservicesClient;

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

    @Autowired
    private RollbackUtil rollbackUtil;

    // 新增主题
    // 入参 首次问诊信息
    // 出参 主题id
    public Res<String> registerInquiry(String message, String createUserId) {

        SignReportVO signReportVO = personalService.getSignReport(createUserId).getData();

        InquiryTopicsPO inquiryTopicsPO = new InquiryTopicsPO();
        inquiryTopicsPO.setPhysicalCondition(signReportVO.generateReport());
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
            records.get(records.size() - 1).setMessage(message);
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
        return Res.ok(inquiryTopicsRepository.findAllByCreateUserIdAndDelTimeIsNull(userId).stream().map(inquiryConvert::generateInquiryTopicsVOByInquiryTopicsPO).collect(Collectors.toList()));
    }

    public SseEmitter getCompletions(String topicId) {
        SseEmitter emitter = new SseEmitter() {
            @Override
            protected void extendResponse(@NotNull ServerHttpResponse outputMessage) {
                super.extendResponse(outputMessage);

                HttpHeaders headers = outputMessage.getHeaders();
                if (headers.getContentType() == null) {
                    headers.setContentType(org.springframework.http.MediaType.TEXT_EVENT_STREAM);
                }
                // Nginx SSE
                headers.set("X-Accel-Buffering", "no");
            }
        };

        // 日志记录
        emitter.onCompletion(() -> log.info("TopicId: {} SSE Completion", topicId));
        emitter.onTimeout(() -> log.info("TopicId: {} SSE Timeout", topicId));

        taskExecutor.execute(() -> {
            sseTask(topicId, emitter);
            sseSendEnd(emitter);
        });

        return emitter;
    }

    public Res<List<DiseaseVO>> getDiseases(String topicId) {
        InquiryTopicsPO inquiryTopicsPO = inquiryTopicsRepository.findById(topicId).orElseThrow(() -> new BusinessException("问诊会话不存在"));
        List<DiseasePO> diseases = inquiryTopicsPO.getDiseases();
        List<DiseaseVO> diseaseVOS = new ArrayList<>();
        for (DiseasePO disease : diseases) {
            DiseaseVO diseaseVO = new DiseaseVO();
            StringBuilder sb = new StringBuilder();
            for (String sub : disease.getContent().split(" ")) {
                if (sub.startsWith("疾病名称：")) {
                    diseaseVO.setName(sub.substring(5));
                } else {
                    sb.append(sub).append("\n");
                }
            }
            diseaseVO.setDetail(sb.toString());
            diseaseVOS.add(diseaseVO);
        }
        return Res.ok(diseaseVOS);
    }

    public Res<OperateVO> deleteInquiryTopic(@PathVariable String topicId) {
        InquiryTopicsPO inquiryTopicsPO = inquiryTopicsRepository.findById(topicId).orElseThrow(() -> new BusinessException("问诊会话不存在"));
        inquiryTopicsPO.setDelTime(DateUtil.date());
        for (InquiryRecordPO record : inquiryTopicsPO.getRecords()) {
            record.setDelTime(DateUtil.date());
        }
        inquiryTopicsRepository.save(inquiryTopicsPO);
        return Res.ok(
                rollbackUtil.builder("注销用户", () -> {
                    inquiryTopicsPO.setDelTime(null);
                    for (InquiryRecordPO record : inquiryTopicsPO.getRecords()) {
                        record.setDelTime(null);
                    }
                    inquiryTopicsRepository.save(inquiryTopicsPO);
                })
        );
    }


    /*
    ------------------------------- 私有实现 -------------------------------
     */

    @SneakyThrows
    private void sseSendEnd(SseEmitter emitter) {
        emitter.send(SseEmitter.event().name("end").data(""));
        emitter.complete();
    }

    @SneakyThrows
    private void sseTask(String topicId, SseEmitter emitter) {
        InquiryTopicsPO inquiryTopicsPO = inquiryTopicsRepository.findById(topicId).orElse(null);

        if (inquiryTopicsPO == null) {
            emitter.send(SseEmitter.event().name("error").data("问诊会话不存在"));
            return;
        }
        if (inquiryTopicsPO.getRecords().get(inquiryTopicsPO.getRecords().size() - 1).getRole().equals("assistant")) {
            return;
        }

        List<CompletionsReq.Messages> prompt = new ArrayList<>();
        prompt.add(new CompletionsReq.Messages("system", "你是一个专业的医师，我会给你提供一些病人的基本信息，以及一些可能与患者疾病相关的医学知识。请根据这些信息，给出一个合理的给出具体病情分析和建议。同时，请拒绝回答与医学无关的问题。"));

        StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(inquiryTopicsPO.getPhysicalCondition())) {
            sb.append("病人基本信息：\n\n").append(inquiryTopicsPO.getPhysicalCondition()).append("\n\n");
        }
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
        inquiryTopicsPO.getRecords().add(new InquiryRecordPO("assistant", target.toString()));
        inquiryTopicsRepository.save(inquiryTopicsPO);
    }


    // 计算问题相似度
    public List<Double> getVector(String message) {
        EmbeddingsReq<String> embeddingsReq = new EmbeddingsReq<>(message);
        return microservicesClient.embedding(embeddingsReq).getData();
    }

    // 归纳问题主题
    private String getTopicTitle(String message) {
        String prompt = String.format("现有问诊会话问题：%s\n\n请帮我对此总结此次会话的主题，要求尽量简短，并且无其余语句以及标点符号直接输出主题名称即可。", message);
        String result = openAIClient.completion(new CompletionsReq(prompt)).getChoices()[0].getMessage().getContent();
        return ReUtil.replaceAll(result, "[\\pP\\p{Punct}]", "");
    }

    /*
    根据用户输入的 p，计算问题的相似度，返回相似度最高的前K个 Disease
     */
    @SneakyThrows
    public List<DiseasePO> topKSimilarDisease(List<Double> vector, int k) {
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
