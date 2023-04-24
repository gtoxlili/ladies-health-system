package com.system.ladiesHealth;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.system.ladiesHealth.component.client.OpenAIClient;
import com.system.ladiesHealth.dao.DiseaseRepository;
import com.system.ladiesHealth.domain.po.DiseasePO;
import com.system.ladiesHealth.domain.pojo.openAI.EmbeddingsReq;
import com.system.ladiesHealth.domain.pojo.openAI.EmbeddingsRes;
import com.system.ladiesHealth.service.InquiryService;
import com.system.ladiesHealth.utils.XXHash;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
public class Initialization {

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private OpenAIClient openAIClient;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    private static final long xxHashSeed = 0x000000009E3779B1L;

    @Autowired
    private InquiryService inquiryService;

//    @Bean
//    @Order
//    public void testDisease() {
//        List<Double> c = consultationService.getVector("怀孕后肚子巨痛怎么办");
//        System.out.println(c);
////        consultationService.topKSimilarDisease(c, 10);
//    }


    // 初始化疾病向量库
    @Bean
    public void initDiseaseVector() {
        if (diseaseRepository.count() != 0) {
            return;
        }
        // 初始化疾病向量库
        // 获取 resources/disease.txt 文件中的数据
        // 将数据存入数据库
        try (InputStream inputStream = this.getClass().getResourceAsStream("/disease.txt")) {
            if (inputStream == null) {
                log.error("disease.txt not found");
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                final String finalLine = line;
                taskExecutor.execute(() -> processLine(finalLine));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 处理每行数据
    public void processLine(String line) {
        String[] split = line.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            // 只需要 疾病名称、高危因素、临床症状及体征、多发群体、
            if (split[i].startsWith("疾病名称") || split[i].startsWith("临床症状及体征") || split[i].startsWith("多发群体")) {
                sb.append(split[i]).append("\n");
            }
        }
        Long id = XXHash.hash64(StrUtil.bytes(sb.toString(), StandardCharsets.UTF_8), 0, sb.length(), xxHashSeed);
        // 如果已经存在
        if (diseaseRepository.existsById(id)) {
            return;
        }
        EmbeddingsRes embeddingsRes = openAIClient.embedding(new EmbeddingsReq(sb.toString(), "text-embedding-ada-002"));
        List<Double> dataBean = embeddingsRes.getData().get(0).getEmbedding();
        DiseasePO diseasePO = new DiseasePO();
        diseasePO.setId(id);
        diseasePO.setContent(line);
        diseasePO.setVector(JSONUtil.toJsonStr(dataBean));
        diseaseRepository.save(diseasePO);
    }
}
