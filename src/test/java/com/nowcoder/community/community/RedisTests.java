package com.nowcoder.community.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"id","1");
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testList(){
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
    }

    @Test
    public void testSet(){
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey,"liubei","guanyu","zhangfei");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:teachers",10, TimeUnit.SECONDS);
    }

    //编程式事务
    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";

                redisOperations.multi();

                redisOperations.opsForSet().add(redisKey,"zhangsan");
                redisOperations.opsForSet().add(redisKey,"lisi");
                redisOperations.opsForSet().add(redisKey,"wangwu");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                //提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }

    @Test
    public void DivisibleSubset() {
        int[] nums = {6,8,-2,-5,2};
        int k1 = 3;
        int k2 = 4;
        int len = nums.length;
        int[] dp = new int[len];
        Arrays.fill(dp, 0);
        int maxSize = 0;
        int maxVal = dp[0];
        for (int i = 0; i < len; i++) {
            for (int j = len - 1; j >= i; j--) {
                dp[j] = Math.max(dp[j], dp[j - i] + nums[i]);
                if (dp[j] % k1 == 0 && dp[j] % k2 != 0) {
                    if(maxVal < dp[j]){
                        maxVal = dp[j];
                        maxSize = 0;
                    }else if(maxVal == dp[j]){
                        maxSize ++;
                    }
                }
            }
        }
        System.out.println(maxSize);
        System.out.println(maxVal);
    }

}
