package com.gendml.kutang.TCP;


import com.gendml.kutang.Entity.Raspi;
import com.gendml.kutang.Entity.Water;
import com.gendml.kutang.Entity.sensor_readings;
import com.gendml.kutang.Service.KTService;
import com.gendml.kutang.Service.RedisService;
import com.gendml.kutang.Service.impl.AsyncExecuterService;
import com.gendml.kutang.mapper.sensor_readings_mapper;
import com.gendml.kutang.utils.PhoneRemind;
import com.gendml.kutang.utils.SendEmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.gendml.kutang.utils.mp3Player;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

/**
 * @author Зөндөө
 * @create 2021-08-30 20:20
 */
@Slf4j
@Component
public class socket implements ApplicationRunner {

    //读取配置文件
    final private ResourceBundle receiver = ResourceBundle.getBundle("Receiver");

    private float level;//读取实时水位
    private float device_temp;//读取设备实时温度
    private float thresholdLevel = 10;//阈值
    private float thresholdLevel_deviceTemp = 35;//温度阈值

    private String[] textArray;
    //水位阈值提醒标志
    boolean boo = true;
    //设备温度阈值提醒标志
    boolean booDevTemp = true;
    @Autowired
    private KTService ktService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private sensor_readings_mapper sensorReadingsMapper;

    //设置多进程任务
    @Autowired
    AsyncExecuterService asyncExecuterService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //开机播报
        Thread.sleep(200);
        mp3Player.playMp3("src/main/resources/music.mp3");
        Thread.sleep(200);
        ktService.BroadcastInfo("欢迎使用智慧库塘信息监测系统");
        //缓冲标志位 用于加载项目 防止出现启动异常
        boolean flag = true;

        //传入Redis
        while (true) {
            //异步任务读取当前水位
            Future<Float> res = asyncExecuterService.getNowLevel();//从Arduino中实时读取
//            Future<Float> res = asyncExecuterService.getValueByRedis("KuTangNowLevel_6874651");//从redis中读取水位
            level = res.get();
//            System.out.println("level:" + level);

            //①阈值提醒 到达阈值 发送邮件 灯变成红色
            if (level >= 0) {
                if (level < thresholdLevel) {
                    ktService.openLight(2);//小于阈值->绿灯
                    boo = true;//正常水位 标志复原
                }
                if (level >= thresholdLevel) {
                    if(boo){
                        ktService.openLight(1);//到达阈值->红灯
                        ktService.activeBuzzer();//激活蜂鸣器
                        ktService.closeAllRelay();//关闭所有电器
                        ktService.BroadcastInfo("检测到库塘水位已达阈值，请及时处理。");
                        SendEmailUtil.sendEmail(receiver.getString("receiverQQNumber"), "库塘阈值提醒","库塘设备编号为6874651的用户，您好！您的库塘当前水位："+level+"厘米，已到达阈值，请您尽快处理！");
                        boo = false;
                    }
                }
            }

            //②设备温度散热 到达35℃以上自动开启散热风扇
            Future<Float> resTemp = asyncExecuterService.getValueByRedis("KuTangDeviceTemp_6874651");//从redis中读取温度 演示用
            device_temp = resTemp.get();
//            System.out.println("device_temp:" + device_temp);

            if(device_temp >= 0){
                if (device_temp < thresholdLevel_deviceTemp) {
                    ktService.closeRelay(3);//关闭散热风扇
                    booDevTemp = true;
                }
                if (device_temp >= thresholdLevel_deviceTemp) {
                    if(booDevTemp){
                        ktService.activeBuzzer_DeviceTempRemind();//激活蜂鸣器3声
                        ktService.BroadcastInfo("检测到设备温度过高，正在打开散热风扇。");
                        ktService.openRelay(3);//开启散热风扇
                        SendEmailUtil.sendEmail(receiver.getString("receiverQQNumber"), "设备温度提醒","库塘设备编号为6874651的用户，您好！您的设备当前温度："+device_temp+"℃，设备当前温度过高，已为您开启散热风扇！");
                        booDevTemp = false;
                    }
                }
            }

            //③故障检测 电话通知塘主
            if (ktService.faultDetect(6874651L) == 0) {
                log.info("设备存在故障，已自动退出程序。");
                ktService.openBuzzer();//激活蜂鸣器
                ktService.BroadcastInfo("检查到设备存在故障，已自动退出程序。");
                PhoneRemind.callPhonebyNumber(receiver.getString("receiverPhoneNumber"));//电话通知
                System.exit(0);//退出程序
            }

            //④存数据
            Map<String, Object> map = ktService.getAllInfo();
            // String.valueOf(UUID.randomUUID()).substring(0,8);
            String finalInfo = map.get("DeviceNumber") + "," + map.get("Timestamp") + "," + map.get("WaterInfo").toString() + "," + map.get("RaspiInfo").toString();
            textArray = finalInfo.split(",");
            if(flag){
                Thread.sleep(15000);//睡眠三秒 完成缓冲 不然数据异常
                flag = false;
            }
            if (textArray.length == 13){
                //存入Mysql历史数据
                Water waterMysql = (Water) map.get("WaterInfo");
                Raspi raspiMysql = (Raspi) map.get("RaspiInfo");
                sensor_readings sr = new sensor_readings(6874651,System.currentTimeMillis(),Float.parseFloat(waterMysql.getTDS()),Float.parseFloat(waterMysql.getTemp()),Float.parseFloat(waterMysql.getLevel()),Float.parseFloat(waterMysql.getTurbidity()),Long.parseLong(raspiMysql.getTotalMemory()),Long.parseLong(raspiMysql.getUsedMemory()),Integer.parseInt(raspiMysql.getCPUUtility()),Float.parseFloat(raspiMysql.getRaspiTemps()),Float.parseFloat(raspiMysql.getRaspiHumidity()),Integer.parseInt(raspiMysql.getIsHasSmog()),Integer.parseInt(raspiMysql.getIsHasFire()));
                //存入mysql
                int res2 = sensorReadingsMapper.insert(sr);
                if (res2 == 1) {
                    log.info("存入Mysql历史数据成功！data : {}" + sr.toString());
                } else log.info("存入Mysql历史数据失败！");
                //存入Redis实时数据
                asyncExecuterService.saveNow("KuTangRedisInfo_6874651",sr);//所有信息
                Thread.sleep(3000);
            }
        }
    }
}
