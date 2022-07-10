package com.gendml.kutang.Service.impl;

import com.gendml.kutang.Entity.sensor_readings;
import com.gendml.kutang.Service.KTService;
import com.gendml.kutang.Service.RedisService;
import com.gendml.kutang.mapper.sensor_readings_mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * @author Зөндөө
 * @create 2022-04-17 22:28
 */
@Slf4j
@Service
public class AsyncExecuterService {
    @Autowired
    private sensor_readings_mapper sensorReadingsMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private KTService ktService;


    /**
     * @param entity 存历史数据至Mysql
     */
//    @Async
//    public void saveHistory(sensor_readings entity) {
//        try {
//            Thread.sleep(5000);//5秒存一次历史数据
//            int res = sensorReadingsMapper.insert(entity);
//            if (res == 1) {
//                log.info("存入Mysql历史数据成功！data : {}" + entity.toString());
//            } else log.info("存入Mysql历史数据失败！");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * @param key 存实时数据至Redis
     * @param entity
     */
    //普通异步任务 无法返回结果
    @Async
    public void saveNow(String key, sensor_readings entity) {
        boolean res = redisService.set(key, entity);
        if (res) {
            log.info("存入Redis实时数据成功！key : {}，value : {}", key, entity);
        } else log.info("存入Redis实时数据失败！");
    }


    /**
     * @return 从Arduino实时读取水位数据
     * @throws IOException
     */
    //异步任务 设置携带返回值
    @Async
    public Future<Float> getNowLevel() throws IOException {
        String[] WaterInfo = (ktService.readWaterLevelArduino()).split(",");
        if (WaterInfo.length == 5) {
            return new AsyncResult<>(Float.parseFloat(WaterInfo[0]));
        }
        return new AsyncResult<>(-1F);
    }

    /**
     * @return 从redis中读信息 返回Float
     * @throws IOException
     */
    //异步任务 设置携带返回值
    @Async
    public Future<Float> getValueByRedis(String key) throws IOException {
        Object obj = redisService.get(key);
        if (obj != null) {
            return new AsyncResult<>(Float.valueOf(obj.toString()));
        }
        return new AsyncResult<>(-1F);
    }

}
