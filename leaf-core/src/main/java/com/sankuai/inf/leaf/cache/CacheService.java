package com.sankuai.inf.leaf.cache;

import com.sankuai.inf.leaf.constant.CacheConstant;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangxinjun
 * @date 2019/11/17
 */
public class CacheService {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取自增ID
     * @param key 业务key
     * @return ID结果集
     */
    public Long getId(String key) {
        String assemblyKey = CacheConstant.INCREMENT_ID_HOWEVER_KEY_PREFIX + ":" + key;
        BoundValueOperations<String, String> valueOperations = stringRedisTemplate.boundValueOps(assemblyKey);
        return valueOperations.increment(1);
    }

    /**
     * 获取自增ID， 并且
     * @param key 业务key
     * @return .
     */
    public Long getIdWithExpired(String key) {
        // 获取当日日期
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        String date = DATE_TIME_FORMATTER.format(now);
        String assemblyKey = CacheConstant.INCREMENT_ID_EXPIRED_KEY_PREFIX  + ":" + key + ":" + date;
        BoundValueOperations<String, String> valueOperations = stringRedisTemplate.boundValueOps(assemblyKey);
        Long id = valueOperations.increment(1);
        Long expire = valueOperations.getExpire();
        if (expire != null && -1 == expire) {
            // 获取第二天
            LocalDateTime nextDay = now.plusDays(1);
            Duration between = Duration.between(now, nextDay);
            // 添加一个固定的时间，防止转换时出现异常
            // 添加随机过期时间，防止同一时间缓存
            long timeout = between.toMillis() + 60 * 1000 + ThreadLocalRandom.current().nextLong(60 * 1000);
            valueOperations.expire(timeout, TimeUnit.MILLISECONDS);
        }
        return id;
    }

}
