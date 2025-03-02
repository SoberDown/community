package com.lxc.community;

import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        //设置变量名
        String redisKey = "test:count";
        //存值
        redisTemplate.opsForValue().set(redisKey,1);
        //取值
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        //自增
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        //自减
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes(){
        //设置变量名
        String redisKey = "test:user";
        //存值
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","张三");
        //取值
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    @Test
    public void testLists(){
        //设置变量名
        String redisKey = "test:ids";
        //存值 leftPush从左边进 rightPush从右边进
        redisTemplate.opsForList().leftPush(redisKey,1);
        redisTemplate.opsForList().leftPush(redisKey,2);
        redisTemplate.opsForList().leftPush(redisKey,3);
        redisTemplate.opsForList().rightPush(redisKey,4);

        //列表常用工具
        System.out.println(redisTemplate.opsForList().size(redisKey));//列表大小
        System.out.println(redisTemplate.opsForList().index(redisKey,0));//获取索引为0的数据
        System.out.println(redisTemplate.opsForList().range(redisKey,0,3));//获取从0~2的数据

        //弹值 leftPop从做弹 rightPop从右弹出
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
    }

    @Test
    public void testSets(){
        //设置变量名
        String redisKey = "test:teachers";
        //存值
        redisTemplate.opsForSet().add(redisKey,"刘备","关羽","张飞");

        //集合常用工具
        System.out.println(redisTemplate.opsForSet().size(redisKey));//集合大小
        System.out.println(redisTemplate.opsForSet().members(redisKey));//展示集合数据

        //弹值
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
    }

    @Test
    public void testSorteSets(){
        //设置变量名
        String redisKey = "test:students";
        //存值
        redisTemplate.opsForZSet().add(redisKey,"刘备",1);
        redisTemplate.opsForZSet().add(redisKey,"关羽",2);
        redisTemplate.opsForZSet().add(redisKey,"张飞",3);
        redisTemplate.opsForZSet().add(redisKey,"赵云",4);
        redisTemplate.opsForZSet().add(redisKey,"诸葛亮",5);

        //集合常用工具
        System.out.println(redisTemplate.opsForZSet().size(redisKey));//集合大小
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));//统计集合有多少数据
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"赵云"));//统计某一个的分数
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"关羽"));//统计某一个的排名 rank默认从小到大 reverseRank则是从大到小
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,2));//安排排序顺序进行展示0~2 range默认从小到大 reverseRange则是从大到小
    }

    @Test
    public void testKeys(){
        //删除test:user
        redisTemplate.delete("test:user");
        //判断某个key存不存在
        System.out.println(redisTemplate.hasKey("test:user"));
        //设置10秒后过期清除
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
    }

    //多次访问同一个key,进行绑定
    @Test
    public void testBoundOperations(){
        String rediskey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(rediskey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    //编程式事务
    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback() {//所以给execute创建个对象obj
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                //启动事务
                redisOperations.multi();

                //在开启事务和提交事务期间,是那命令放到队列,不会马上执行
                redisOperations.opsForSet().add(redisKey,"zs");
                redisOperations.opsForSet().add(redisKey,"ls");
                redisOperations.opsForSet().add(redisKey,"ww");

                System.out.println(redisOperations.opsForSet().members(redisKey));

                //提交事务 这里返回的数据给execute,所以给execute创建个对象
                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }
}
