package com.system.ladiesHealth.configuration;

import feign.Client;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class OkHttpConfig {

    @Value("${okhttp.proxy.host}")
    private String proxyHost;

    @Value("${okhttp.proxy.port}")
    private Integer proxyPort;

    @Value("${okhttp.read-timeout}")
    private Integer readTimeout;

    @Value("${okhttp.write-timeout}")
    private Integer writeTimeout;

    @Bean
    public OkHttpClient okHttpClient() {
        log.info("OkHttpClient -------Initializing OkHttpClient----------");
        log.info("OkHttpClient proxyHost: {}", proxyHost);
        log.info("OkHttpClient proxyPort: {}", proxyPort);
        log.info("OkHttpClient readTimeout: {}", readTimeout);
        log.info("OkHttpClient writeTimeout: {}", writeTimeout);
        log.info("OkHttpClient ----------End Initializing OkHttpClient----------");
        return new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, proxyPort)))
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                // ignoreSslCertificate

                .build();
    }

    @Bean
    @ConditionalOnMissingBean({Client.class})
    public Client feignClient(OkHttpClient okHttpClient) {
        return new feign.okhttp.OkHttpClient(okHttpClient);
    }
}
