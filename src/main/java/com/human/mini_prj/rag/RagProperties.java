package com.human.mini_prj.rag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "rag")
public record RagProperties(@DefaultValue("http://localhost:8000") String baseUrl,
                            String serviceToken,
                            @DefaultValue("4") int topK,
                            @DefaultValue("30s") Duration timeout
) {
}