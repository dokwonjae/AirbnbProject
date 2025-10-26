package com.example.airbnbproject.support;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redissonClient;

    @Value("${redisson.lock.lease-seconds:5}")
    private long leaseSeconds;

    @Value("${redisson.lock.wait-seconds:3}")
    private long waitSeconds;

    /**
     * 분산락으로 임계영역 보호.
     * @param key 락 키 (ex. "accommodation:{id}")
     * @param supplier 임계영역 실행 로직
     * @param <T> 결과 타입
     * @return supplier 결과
     */
    public <T> T withLock(String key, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요. [lockKey=" + key + "]");
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 대기 중 인터럽트가 발생했습니다. 다시 시도해주세요.");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

