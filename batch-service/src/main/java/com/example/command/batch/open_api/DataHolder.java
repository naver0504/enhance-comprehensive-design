package com.example.command.batch.open_api;

public interface DataHolder<T> {

    void init();
    Integer getEntityId(T t);
}
