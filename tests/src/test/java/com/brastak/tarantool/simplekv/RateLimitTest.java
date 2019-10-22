package com.brastak.tarantool.simplekv;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitTest extends AbstractKVTest {
    private final static int DEF_REQUESTS = 500;

    private final Client client;

    RateLimitTest() {
        ClientBuilder clientBuilder = ResteasyClientBuilder.newBuilder();
        this.client = clientBuilder.build();
    }

    @BeforeEach
    void setUp() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "load");
        payload.put("value", "simple-value");

        client.target(getBaseServiceUri()).request().buildPost(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE)).invoke();
    }

    @Test
    void testRateLimit() {
        Queue<Integer> statuses = new ConcurrentLinkedQueue<>();

        WebTarget target = this.client.target(getBaseServiceUri()).path("load");
        IntStream.range(0, DEF_REQUESTS).unordered().parallel()
                .mapToObj(unused -> asyncCall(statuses, target))
                .forEach(CompletableFuture::join);

        assertTrue(statuses.stream().anyMatch(s -> s.equals(429)), "Receive no HTTP 429 response with " + DEF_REQUESTS + " requests");
    }

    private CompletableFuture<Void> asyncCall(Queue<Integer> statuses, WebTarget target) {
        return target.request().rx().get().thenAccept(r -> {
            statuses.add(r.getStatus());
            r.close();
        }).toCompletableFuture();
    }

    @AfterEach
    void tearDown() {
        client.target(getBaseServiceUri()).path("load").request().buildDelete().invoke();
    }
}
