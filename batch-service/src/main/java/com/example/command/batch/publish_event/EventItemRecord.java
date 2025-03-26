package com.example.command.batch.publish_event;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface EventItemRecord {

    @JsonIgnore
    Long getPartitionKey();
}
