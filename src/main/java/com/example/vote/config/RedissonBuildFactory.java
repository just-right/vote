package com.example.vote.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @className: RedissonBuildFactory
 * @description
 * @author: luffy
 * @date: 2020/4/13 22:14
 * @version:V1.0
 */
@Component
public class RedissonBuildFactory {
    private volatile static RedissonClient redissonClient = null;
    @Value("${spring.redis.host}")
    private static String IP;
    @Value("${spring.redis.port}")
    private static int PORT;

    private RedissonBuildFactory(){
        super();
    }

    public static RedissonClient getInstance(){

        if (redissonClient == null ){
            synchronized (RedissonBuildFactory.class){
                if(redissonClient == null){
                    Config config = new Config();
                    config.useSingleServer().setAddress(IP+":"+PORT);
                    redissonClient = Redisson.create(config);
                }
            }
        }
        return redissonClient;
    }

}
