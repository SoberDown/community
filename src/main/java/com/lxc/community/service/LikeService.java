package com.lxc.community.service;
//点赞功能
import com.lxc.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId 点赞的那个人
     * @param entityType
     * @param entityId
     * @param entityUserId 被赞的那个人,拥有实体的那个作者
     */
    public void like(int userId,int entityType,int entityId,int entityUserId){
//        public void like(int userId,int entityType,int entityId){
//        初版代码
//        String entityLikeKey = RedisKeyUtil.getEntityLike(entityType,entityId);
//        //判断userId在不在entityLikeKey的set集合里
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember) {//若为true,则把这个id从set取出来,取消点赞
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        } else {//若为false,则表示没有点赞,把userId添加到set集合里
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }

        //重构代码 使用事务管理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //查看当前用户有没有被这个实体点过赞 注意把查询放到事务之外,redis的事务中查询,会在事务结束后再进行
                boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);

                //开启事务
                operations.multi();
                if (isMember) {//若为true,则表示已经点赞
                    operations.opsForSet().remove(entityLikeKey, userId);//点赞数-1,把userId从set集合里取出
                    operations.opsForValue().decrement(userLikeKey);//对被点赞的那个实体的点赞数量进行-1
                } else {//若为false,则表示没有点赞
                    operations.opsForSet().add(entityLikeKey, userId);//点赞数+1,把userId添加到set集合里
                    operations.opsForValue().increment(userLikeKey);//对被点赞的那个实体的点赞数量进行+1
                }
                //关闭事务
                return operations.exec();
            }
        });
    }

    //查询实体点赞数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某个实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;//点赞了返回1,没点赞返回0
    }

    //查询某个用户获得的赞数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
