package com.example.vote.service.impl;

import com.example.vote.config.RedissonBuildFactory;
import com.example.vote.service.IDistributedLockerService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @className: RedisLockImpl
 * @description
 * @author: luffy
 * @date: 2020/4/13 22:12
 * @version:V1.0
 */
@Service
public class RedisLockImpl implements IDistributedLockerService {
    @Override
    public <T> T lock(String lockName, Supplier<T> supplier) {
        return lock(lockName, supplier, 100);
    }

    @Override
    public <T> T lock(String lockName, Supplier<T> supplier, long lockTime) {

        RedissonClient redissonClient = RedissonBuildFactory.getInstance();
        RLock redLock = redissonClient.getLock(lockName);
        try {
            Boolean isSuccess =  redLock.tryLock(lockTime, TimeUnit.SECONDS);
            if(isSuccess){
                try {
                   return supplier.get();
                }finally {
                    redLock.unlock();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
