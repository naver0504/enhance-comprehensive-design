package com.example.command.batch.open_api.all;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JpaItemWriter;

import java.util.List;

public class JpaItemListWriter<T> extends JpaItemWriter<List<T>> {
    private final JpaItemWriter<T> jpaItemWriter;

    public JpaItemListWriter(JpaItemWriter<T> jpaItemWriter) {
        this.jpaItemWriter = jpaItemWriter;
    }

    @Override
    public void write(Chunk<? extends List<T>> items) {
        Chunk<T> totalList = new Chunk<>();

        for (List<T> item : items) {
            totalList.addAll(item);
        }

        jpaItemWriter.write(totalList);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        return;
    }
}
