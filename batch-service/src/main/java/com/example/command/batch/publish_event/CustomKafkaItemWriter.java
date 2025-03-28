
package com.example.command.batch.publish_event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.KeyValueItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.Assert;


@Slf4j
@Setter
public class CustomKafkaItemWriter<K, T> extends KeyValueItemWriter<K, T> {
    protected KafkaTemplate<K, T> kafkaTemplate;
    protected final List<CompletableFuture<SendResult<K, T>>> completableFutures = new ArrayList();
    private long timeout = -1L;
    private String topic;

    public CustomKafkaItemWriter() {
    }

    @SneakyThrows
    protected void writeKeyValue(K key, T value) {

        if (this.delete) {
            this.completableFutures.add(this.kafkaTemplate.send(this.topic ,key, null));
        } else {
            this.completableFutures.add(this.kafkaTemplate.send(this.topic, key, value));
        }
    }

    protected void flush() throws Exception {
        this.kafkaTemplate.flush();
        Iterator var1 = this.completableFutures.iterator();

        while(var1.hasNext()) {
            CompletableFuture<SendResult<K, String>> future = (CompletableFuture)var1.next();
            if (this.timeout >= 0L) {
                future.get(this.timeout, TimeUnit.MILLISECONDS);
            } else {
                future.get();
            }
        }

        this.completableFutures.clear();
    }

    protected void init() {
        Assert.state(this.kafkaTemplate != null, "KafkaTemplate must not be null.");
    }
}
