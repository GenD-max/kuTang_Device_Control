package com.gendml.kutang.Service;

import com.gendml.kutang.Entity.*;
import javazoom.jl.decoder.JavaLayerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Зөндөө
 * @create 2021-08-29 21:02
 */
public interface KTService {
    //打开蜂鸣器
    public R openBuzzer();
    //关闭蜂鸣器
    public R closeBuzzer();
    //激活蜂鸣器3秒
    public R activeBuzzer();
    //激活蜂鸣器三声 用于设备温度提醒
    public R activeBuzzer_DeviceTempRemind() throws InterruptedException;

    //开灯
    public R openLight(int id);

    //烟雾报警
    public R readSmogSensor() throws IOException, InterruptedException;

    //火焰报警
    public R readFireSensor() throws IOException, InterruptedException;

    //获取所有继电器状态
    public Relay getAllRelayStates() throws InterruptedException, IOException;

    //关闭所有继电器
    public R closeAllRelay() throws InterruptedException, IOException;

    //关闭某个继电器
    public R closeRelay(int id) throws InterruptedException, IOException, JavaLayerException;

    //打开所有继电器
    public R openAllRelay() throws InterruptedException, IOException;

    //打开某个继电器
    public R openRelay(int id) throws InterruptedException, IOException, JavaLayerException;

    //读取水位数据
    public String  readWaterLevelArduino() throws IOException;
    //读取水质数据
    public String readWaterQualityArduino() throws IOException;

    //获取水体所有信息
    public Water getWaterInfo() throws IOException;

    //获取系统所有信息
    public Raspi getRaspiInfo() throws IOException, InterruptedException;

    //获取水体+系统所有信息
    public Map<String, Object> getAllInfo() throws IOException, InterruptedException;

    //获取传感器实时信息
    public sensor_readings getsensorReadings(Long device_id);

    //获取故障监测情况
    public int faultDetect(Long device_id);

    //获取环境评分
    public int envEva(Long device_id);

    //播报环境
    public R Broadcast() throws IOException, InterruptedException;

    //播报文字
    public R BroadcastInfo(String targetInfo) throws IOException, InterruptedException;

    //邮件查询所有
    public R getAllInfoEmail(String emailAccount) throws IOException, InterruptedException;


}
