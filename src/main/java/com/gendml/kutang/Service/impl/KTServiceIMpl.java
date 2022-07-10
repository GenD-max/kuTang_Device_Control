package com.gendml.kutang.Service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import com.gendml.kutang.Entity.*;
import com.gendml.kutang.Service.KTService;
import com.gendml.kutang.Service.RedisService;
import com.gendml.kutang.utils.RaspiUtil;
import com.gendml.kutang.utils.SendEmailUtil;
import com.gendml.kutang.utils.gpioUtil;
import com.gendml.kutang.utils.mp3Player;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.system.SystemInfo;
import javazoom.jl.decoder.JavaLayerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static com.gendml.kutang.utils.SerialCommunicate.getRS232State;
import static com.gendml.kutang.utils.SerialCommunicate.getSerial;

/**
 * @author Зөндөө
 * @create 2021-08-29 21:03
 */
@Service
@Slf4j
public class KTServiceIMpl implements KTService {

    @Autowired
    RedisService redisService;

    //rs232继电器指令 16进制
    public static final byte[] open1 = ByteArrayUtil.hexStringToByteArray("01050000FF008C3A");
    public static final byte[] close1 = ByteArrayUtil.hexStringToByteArray("010500000000CDCA");
    public static final byte[] open2 = ByteArrayUtil.hexStringToByteArray("01050001FF00DDFA");
    public static final byte[] close2 = ByteArrayUtil.hexStringToByteArray("0105000100009C0A");
    public static final byte[] open3 = ByteArrayUtil.hexStringToByteArray("01050002FF002DFA");
    public static final byte[] close3 = ByteArrayUtil.hexStringToByteArray("0105000200006C0A");
    public static final byte[] open4 = ByteArrayUtil.hexStringToByteArray("01050003FF007C3A");
    public static final byte[] close4 = ByteArrayUtil.hexStringToByteArray("0105000300003DCA");
    public static final byte[] openAll = ByteArrayUtil.hexStringToByteArray("010F0000001002FFFFE390");
    public static final byte[] closeAll = ByteArrayUtil.hexStringToByteArray("010F00000010020000E220");
    public static final byte[] readState = ByteArrayUtil.hexStringToByteArray("010300000001840A");

    //创建gpio控制器
    private final GpioController gpio = gpioUtil.getGpioController();
    private final GpioPinDigitalOutput pin19 = gpioUtil.getGPD19();//蜂鸣器
    private final GpioPinDigitalOutput pinR = gpioUtil.getGPD_R();
    private final GpioPinDigitalOutput pinG = gpioUtil.getGPD_G();
    private final GpioPinDigitalOutput pinB = gpioUtil.getGPD_B();
//    private final GpioPinDigital pin15 = gpioUtil.getGPD15();//烟雾传感器


    public String serial_lastStr0 = new String();//用于存放从串口获取的水质数据
    public String serial_lastStr1 = new String();//用于存放从串口获取的水位数据
    //得到操作串口
    final ResourceBundle resourceBundleBaiduAI = ResourceBundle.getBundle("BaiduAI");//读取配置文件
    final ResourceBundle resourceBundleSerial = ResourceBundle.getBundle("RaspiSerial");//读取配置文件
    final ResourceBundle receiver = ResourceBundle.getBundle("Receiver");//读取配置文件
    final Serial serial1 = getSerial(resourceBundleSerial.getString("WaterQualitySensorSerial"), Baud._115200);//水质数据
    final Serial serial0 = getSerial(resourceBundleSerial.getString("WaterLevelSensorSerial"), Baud._115200);//水位数据
//    final Serial USB01 = getSerial(resourceBundleSerial.getString("RS232RelaySerial"), Baud._9600);//rs232通讯继电器
    static Serial USB01;
    static {
        for (int i = 0; i <= 1; i++) {
            Serial tmp = getSerial("/dev/ttyUSB" + i, Baud._9600);
            if (tmp != null) {
                USB01 = tmp;
                break;
            }
        }
        if (USB01 == null) {
            System.out.println("未找到串口");
            System.exit(-1);
        }
    }
    @Override
    public R openBuzzer() {
        pin19.high();
        return new R(200,"开启成功！");
    }

    @Override
    public R closeBuzzer() {
        pin19.low();
        return new R(200,"开启成功！");
    }

    @Override
    public R activeBuzzer() {
        pin19.pulse(3000,true);
        return new R(200,"开启成功！");
    }

    @Override
    public R activeBuzzer_DeviceTempRemind() throws InterruptedException {
        pin19.pulse(600,true);
        Thread.sleep(500);
        pin19.pulse(600,true);
        Thread.sleep(500);
        pin19.pulse(600,true);
        Thread.sleep(500);
        return new R(200,"开启成功！");
    }

    @Override
    public R openLight(int id) {
        switch (id){
            case 1:pinG.low();pinB.low();pinR.high();return new R(200,"开启成功！");
            case 2:pinR.low();pinB.low();pinG.high();return new R(200,"开启成功！");
            case 3:pinR.low();pinG.low();pinB.high();return new R(200,"开启成功！");
            default:return new R(500,"未查找到！");
        }
    }

    @Override
    public R readSmogSensor() throws IOException, InterruptedException {
        Raspi raspi = getRaspiInfo();
        if (raspi.getIsHasSmog().equals("0")) {//报警
            return new R(200, "查询成功！", "YES");
        } else if (raspi.getIsHasSmog().equals("1")) return new R(200, "查询成功！", "NO");//正常
        return new R(200, "查询成功！", "NO");//正常
    }

    @Override
    public R readFireSensor() throws IOException, InterruptedException {
        Raspi raspi = getRaspiInfo();
        if (raspi.getIsHasFire().equals("0")) {//报警
            return new R(200, "查询成功！", "YES");
        } else if (raspi.getIsHasSmog().equals("1")) return new R(200, "查询成功！", "NO");//正常
        return new R(200, "查询成功！", "NO");//正常
    }


    @Override
    public Relay getAllRelayStates() throws InterruptedException, IOException {
        USB01.write(readState);
        return getRS232State(ByteArrayUtil.toHexString(USB01.read()));
    }

    @Override
    public R closeAllRelay() throws InterruptedException, IOException {
        USB01.write(closeAll);
        return new R(200, "操作成功！");
    }

    @Override
    public R openAllRelay() throws InterruptedException, IOException {
        USB01.write(openAll);
        return new R(200, "操作成功！");
    }

    @Override
    public R closeRelay(int id) throws InterruptedException, IOException, JavaLayerException {
        switch (id) {
            case 1:
                USB01.write(close1);
                BroadcastInfo("水泵关闭");
                return new R(200, "操作成功！");
            case 2:
                BroadcastInfo("水阀关闭");
                USB01.write(close2);
                return new R(200, "操作成功！");
            case 3:
                USB01.write(close3);
                return new R(200, "操作成功！");
            case 4:
                USB01.write(close4);
                return new R(200, "操作成功！");
            default:
                log.info("串口不存在！");
                return new R(500, "串口不存在！");
        }
    }

    @Override
    public R openRelay(int id) throws InterruptedException, IOException, JavaLayerException {
        switch (id) {
            case 1:
                BroadcastInfo("正在打开水泵");
                USB01.write(open1);
                return new R(200, "操作成功！");
            case 2:
                BroadcastInfo("正在打开水阀");
                USB01.write(open2);
                return new R(200, "操作成功！");
            case 3:
                USB01.write(open3);
                return new R(200, "操作成功！");
            case 4:
                USB01.write(open4);
                return new R(200, "操作成功！");
            default:
                log.info("串口不存在！");
                return new R(500, "串口不存在！");
        }
    }

    //获取水位数据
    @Override
    public String readWaterLevelArduino() throws IOException {
        assert serial0 != null;
        serial0.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                //从单片机读取
                try {
                    serial_lastStr1 = event.getAsciiString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return serial_lastStr1;
    }

    //获取水质数据
    @Override
    public String readWaterQualityArduino() throws IOException {
        assert serial1 != null;
        serial1.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                //从单片机读取
                try {
                    serial_lastStr0 = event.getAsciiString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return serial_lastStr0;
    }

    @Override
    public Water getWaterInfo() throws IOException {
        String Info;
        String WaterLevel;
        String WaterQuality;
        WaterLevel = readWaterLevelArduino();
        WaterQuality = readWaterQualityArduino();
        if (WaterLevel.length() > 2 && WaterQuality.length() > 2) {
            WaterLevel = WaterLevel.substring(0, WaterLevel.length() - 2);
            WaterQuality = WaterQuality.substring(0, WaterQuality.length() - 2);
            Info = WaterQuality + "," + WaterLevel ;
            String[] waterInfoArray = Info.split(",");
            return new Water(waterInfoArray[0], waterInfoArray[1], waterInfoArray[2], waterInfoArray[3]);
        }
        return new Water("0", "0", "0", "0");
    }

    @Override
    public Raspi getRaspiInfo() throws IOException, InterruptedException {
        String Arduino_Level;
        Arduino_Level = readWaterLevelArduino();
        if(Arduino_Level.length() > 2){
            Arduino_Level = Arduino_Level.substring(0,Arduino_Level.length()-2);
            String[] RaspiOtherInfo = Arduino_Level.split(",");
            String TotalMemory;
            String UsedMemory;
            String CPUUtility;
            TotalMemory = String.valueOf(SystemInfo.getMemoryTotal());
            UsedMemory = String.valueOf(SystemInfo.getMemoryUsed());
            CPUUtility = String.valueOf(RaspiUtil.getMemory());
            return new Raspi(TotalMemory, UsedMemory, CPUUtility,RaspiOtherInfo[1],RaspiOtherInfo[2],RaspiOtherInfo[3],RaspiOtherInfo[4]);
        }
        return new Raspi("0", "0", "0","0", "0","1","1");
    }

    @Override
    public Map<String,Object> getAllInfo() throws IOException, InterruptedException {
        Map<String, Object> map = new HashMap<>();
        map.put("DeviceNumber","6874651");//设备编号
        map.put("Timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("WaterInfo", getWaterInfo());
        map.put("RaspiInfo", getRaspiInfo());
        return map;
    }

    //获取实时数据 从redis中读取
    @Override
    public sensor_readings getsensorReadings(Long device_id) {
        String key = "KuTangRedisInfo_" + String.valueOf(device_id);//
        sensor_readings resObj= (sensor_readings) redisService.get(key);
        if(resObj != null){
//            sensor_readings rs = JSONObject.parseObject(resObjString,sensor_readings.class);
            return resObj;
        }
        return null;
    }

    //查询故障监测 从redis中读取
    @Override
    public int faultDetect(Long device_id){
        Object resObj = redisService.get("KuTangFaultDetction_" + String.valueOf(device_id));
        if (resObj != null) {
            return Integer.parseInt(resObj.toString());
        }
        return -1;//非法状态
    }

    //查询环境评分 从redis读取
    public int envEva(Long device_id){
        Object resObj = redisService.get("KuTangEnvEvaluate_" + String.valueOf(device_id));
        if (resObj != null) {
            return Integer.parseInt(resObj.toString());
        }
        return 0;
    }

    //语音播报
    @Override
    public R Broadcast() throws IOException, InterruptedException {
        {
            // 初始化一个AipSpeech
            AipSpeech client = new AipSpeech(resourceBundleBaiduAI.getString("appld"),resourceBundleBaiduAI.getString("apiKey"),resourceBundleBaiduAI.getString("secretKey"));
            // 调用接口
            // 设置可选参数
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("spd", "6");
            options.put("pit", "5");
            options.put("per", "106");
            Water water = getWaterInfo();
            Raspi raspi = getRaspiInfo();
            String targetContext = "库塘实时信息播报：" +
                    "您的库塘当前水位" + water.getLevel() + "厘米，"
                    + "水温：" + water.getTemp() + "摄氏度，"
                    + "环境湿度：" + raspi.getRaspiHumidity()+"%，"
                    + "溶解固体量：" + water.getTDS() + "ppm，"
                    + "浑浊度：" + water.getTurbidity() + "度，"
                    + "设备温度：" + raspi.getRaspiTemps() + "摄氏度，"
                    + "设备系统占用率："+ raspi.getCPUUtility() + "%，"
                    + "设备故障检测：" + (faultDetect(6874651L) == 1 ? "正常运行，" : "存在故障，")
                    + "当前环境综合评分：" + envEva(6874651L) + "分。";
            System.out.println(targetContext);
            String targetFile = "src/main/resources/output.mp3";
            String promptFile = "src/main/resources/music.mp3";
            TtsResponse res = client.synthesis(targetContext, "zh", 1, options);
            byte[] data = res.getData();
            org.json.JSONObject res1 = res.getResult();
            if (data != null) {
                try {
                    Util.writeBytesToFileSystem(data, targetFile);
                    //执行播放
                    Thread.sleep(200);
                    mp3Player.playMp3(promptFile);
                    Thread.sleep(200);
                    mp3Player.playMp3(targetFile);
                    return new R(200,"播报成功！");
                } catch (IOException | JavaLayerException | InterruptedException e) {
                    e.printStackTrace();
                    return new R(500,"播报失败！");
                }
            }
            if (res1 != null) {
                System.out.println(res1.toString(2));
            }
        }
        return new R(500,"播报失败！");
    }


    //语音播报文字
    @Override
    public R BroadcastInfo(String targetInfo) throws IOException, InterruptedException {
        {
            // 初始化一个AipSpeech
            AipSpeech client = new AipSpeech(resourceBundleBaiduAI.getString("appld"),resourceBundleBaiduAI.getString("apiKey"),resourceBundleBaiduAI.getString("secretKey"));
            // 调用接口
            // 设置可选参数
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("spd", "6");
            options.put("pit", "5");
            options.put("per", "106");
            Water water = getWaterInfo();
            String targetFile = "src/main/resources/output.mp3";
            TtsResponse res = client.synthesis(targetInfo, "zh", 1, options);
            byte[] data = res.getData();
            org.json.JSONObject res1 = res.getResult();
            if (data != null) {
                try {
                    Util.writeBytesToFileSystem(data, targetFile);
                    //执行播放
                    Thread.sleep(200);
                    mp3Player.playMp3(targetFile);
                    return new R(200,"播报成功！");
                } catch (IOException | JavaLayerException | InterruptedException e) {
                    e.printStackTrace();
                    return new R(500,"播报失败！");
                }
            }
            if (res1 != null) {
                System.out.println(res1.toString(2));
            }
        }
        return new R(500,"播报失败！");
    }

    @Override
    public R getAllInfoEmail(String emailAccount) throws IOException, InterruptedException {
        sensor_readings sr = getsensorReadings(6874651L);
        if(sr != null){
            String targetContext = "库塘设备编号为" + sr.getDevice_id() + "的用户，您好！<br/>"
                    + "您的库塘信息如下：<br/>"
                    + "水位：" + sr.getWater_level() + "CM<br/>"
                    + "水温：" + sr.getWater_temp() + "℃<br/>"
                    + "环境湿度：" + sr.getDevice_humidity() +"%<br/>"
                    + "溶解固体量：" + sr.getTds() + "ppm<br/>"
                    + "浑浊度：" + sr.getTurbidity() + "JTU<br/>"
                    + "设备温度：" + sr.getDevice_temp() + "℃<br/>"
                    + "设备系统占用率：" + sr.getCpu() +  "%<br/>"
                    + "设备故障检测：" + (faultDetect(6874651L) == 1 ? "正常运行<br/>" : "存在故障<br/>")
                    + "当前环境综合评分：" + envEva(6874651L) + "分<br/>"
                    + "感谢您的使用！祝您生活愉快!<br/>";
            return SendEmailUtil.sendEmail(emailAccount,"《库安卫士》——库塘信息查询",targetContext);
        }
        return new R(500,"fail","");
    }
//<br/>

}
