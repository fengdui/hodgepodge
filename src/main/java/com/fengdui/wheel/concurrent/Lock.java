package com.fengdui.wheel.concurrent;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author fengdui1
 * @since 2022/2/24 4:43 下午
 */
@Slf4j
public class Lock {
    private RedisTemplate<String, String> redisTemplate;
    public boolean lock(String key, long leaseTime) {
        try {
            long current = System.currentTimeMillis();
            RedisScript<Long> script = RedisScript.of(
                    "if (redis.call('exists', KEYS[1]) == 0) then " +
                            "redis.call('hset', KEYS[1], ARGV[2], 1); " +
                            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                            "return nil; " +
                            "end; " +
                            "if (redis.call('hexists', KEYS[1], ARGV[2]) >= 1) then " +
                            "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
                            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                            "return nil; " +
                            "end; " +
                            "return redis.call('pttl', KEYS[1]);", Long.class);
            String id = UUID.randomUUID().toString();
            Long expire = redisTemplate.execute(script, Lists.newArrayList(key), new Object[]{ leaseTime, id });
            if (expire == null) {
                log.info("get lock key - {} by thread - {} cost {}.", key, id, System.currentTimeMillis() - current);
                return true;
            } else {
                Semaphore semaphore = new Semaphore(0);
                while (true) {
                    expire = redisTemplate.execute(script, Lists.newArrayList(key), new Object[]{ leaseTime, id });
                    if (expire == null) {
                        log.info("get lock key - {} by thread - {} cost {}.", key, id, System.currentTimeMillis() - current);
                        return true;
                    }
                    if (expire >= 0) {
                        semaphore.tryAcquire(Math.min(expire, 100), TimeUnit.MILLISECONDS);
                    }
                }
            }
        } catch (Exception e) {
            log.error("get lock failure.", e);
            return false;
        }
    }
}
