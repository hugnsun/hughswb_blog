package com.minzheng.blog.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class RedisUtils {
 private static RedisConfigYmlUtil redisConfigYmlUtil;

 private static final Jedis jedis;

    static {
        // 创建 Jedis 客户端
        jedis = new Jedis(redisConfigYmlUtil.getHost(), redisConfigYmlUtil.getPort());
        // 认证密码
        jedis.auth(redisConfigYmlUtil.getPassword());
    }

    // 字符串
    public static String get(String key) {
        // 获取指定 key 的值
        return jedis.get(key);
    }

    public static void set(String key, String value) {
        // 设置指定 key 的值
        jedis.set(key, value);
    }

    public static void setex(String key, int seconds, String value) {
        // 设置指定 key 的值，并设置过期时间
        jedis.setex(key, seconds, value);
    }

    public static void del(String key) {
        // 删除指定 key
        jedis.del(key);
    }

    public static boolean exists(String key) {
        // 判断指定 key 是否存在
        return jedis.exists(key);
    }

    // 列表
    public static void lpush(String key, String... values) {
        // 将多个值插入到列表头部
        jedis.lpush(key, values);
    }

    public static String lpop(String key) {
        // 弹出列表
        return jedis.lpop(key);
    }

    public static void rpush(String key, String... values) {
// 将多个值插入到列表右侧
        jedis.rpush(key, values);
    }

    public static String rpop(String key) {
// 弹出列表右侧第一个值
        return jedis.rpop(key);
    }

    public static List<String> lrange(String key, long start, long end) {
// 获取指定范围内的列表值
        return jedis.lrange(key, start, end);
    }

    // 集合
    public static void sadd(String key, String... members) {
// 将多个成员添加到集合
        jedis.sadd(key, members);
    }

    public static Set<String> smembers(String key) {
// 获取集合所有成员
        return jedis.smembers(key);
    }

    public static boolean sismember(String key, String member) {
// 判断成员是否在集合中
        return jedis.sismember(key, member);
    }

    public static void srem(String key, String... members) {
// 从集合中移除多个成员
        jedis.srem(key, members);
    }

    // 哈希
    public static void hset(String key, String field, String value) {
// 设置哈希字段的值
        jedis.hset(key, field, value);
    }

    public static String hget(String key, String field) {
// 获取哈希字段的值
        return jedis.hget(key, field);
    }

    public static Map<String, String> hgetAll(String key) {
// 获取哈希所有字段的值
        return jedis.hgetAll(key);
    }

    public static void hdel(String key, String... fields) {
// 删除哈希字段
        jedis.hdel(key, fields);
    }

    // 有序集合
    public static void zadd(String key, double score, String member) {
// 将成员添加到有序集合
        jedis.zadd(key, score, member);
    }

    public static Set<String> zrange(String key, long start, long end) {
// 获取指定范围内的有序集合成员
        return jedis.zrange(key, start, end);
    }

    public static Set<String> zrevrange(String key, long start, long end) {
// 获取指定范围内的有序集合成员，从大到小
        return jedis.zrevrange(key, start, end);
    }

    public static long zrank(String key, String member) {
// 获取成员在有序集合中的排名
        return jedis.zrank(key, member);
    }

    public static long zrevrank(String key, String member) {
// 获取成员在有序集合中的排名，从大到小
        return jedis.zrevrank(key, member);
    }

    public static void zrem(String key, String... members) {
// 从有序集合中移除多个成员
        jedis.zrem(key, members);
    }

    public static void close() {
// 关闭客户端
        jedis.close();
    }
}
