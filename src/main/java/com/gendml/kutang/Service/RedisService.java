package com.gendml.kutang.Service;

/**
 * @author Зөндөө
 * @create 2021-11-24 19:50
 */
public interface RedisService {
    /**
     * 根据key读取数据
     */
    public Object get(final String key);


    /**
     * 写入数据
     */
    public boolean set(final String key, Object value);

}
