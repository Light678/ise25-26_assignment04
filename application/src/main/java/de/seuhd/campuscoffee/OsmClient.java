package com.campuscoffee.osm;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class OsmClient {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openstreetmap.org")
            .defaultHeader(HttpHeaders.USER_AGENT, "CampusCoffee/1.0")
            .build();

    public String fetchNodeXml(long nodeId) {
        return webClient.get()
                .uri("/api/0.6/node/{id}.xml", nodeId)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(s -> s.value() == 404, r -> r.createException().map(ex -> new OsmNotFoundException(nodeId)))
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(400)))
                .block();
    }
}
