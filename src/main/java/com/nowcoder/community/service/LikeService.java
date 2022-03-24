package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞
    public void like(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLike(entityType,entityId);
        // 查一下userId是否在set中已判断用户是否点赞
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        if(isMember) { // 如果在里面，说明点过赞了，再点的话我们要把它拿出去（取消点赞）
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    // 查询某实体点赞数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLike(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某user对某实体点赞状态 本来想返回boolean的，但是为了以后扩展可能增加其他状态比如点踩。用boolean就不能表示第三种状态了
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLike(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }
}
