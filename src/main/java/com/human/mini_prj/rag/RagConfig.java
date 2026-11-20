package com.human.mini_prj.rag;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RagProperties.class)
public class RagConfig {
    @Bean
    public RestClient rafResterClient(RagProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(props.timeout());
        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("X-Service-Token",props.serviceToken())
                .requestFactory(factory)
                .build();
    }
}