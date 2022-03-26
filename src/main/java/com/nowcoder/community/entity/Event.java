package com.nowcoder.community.entity;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Event { // 封装事件对象

    private String topic; // 事件类型
    private int userId;   // 事件触发人
    private int entityType; // 实体类型，事件作用在什么实体上
    private int entityId; // 实体的
    private int entityUserId; // 实体的作者id

    // 未来可能会开发其他事件类型，但我们没法预判，就放到这里面
    private Map<String, Object> data = new HashMap<>();

    // 这样做的好处是可以连续   x.setXXX().setXXX().setXXX()...
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key,value);
        return this;
    }
}
