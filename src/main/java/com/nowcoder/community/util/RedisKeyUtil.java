package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    // 某个实体的赞
    // key:                            value:
    // like:entity:entityType:entityId -> set(userId)
    // 当我们需要统计点赞数量时， key直接调用set统计数量的方法，如果将来需要知道谁给我点赞，这些userId我们也可以从set里取到；
    // 而且也可以通过判断userId是否在集合中判断用户是否点赞
    public static String getEntityLike(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
