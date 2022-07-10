package com.gendml.kutang.Controller;

import com.gendml.kutang.Entity.R;
import com.gendml.kutang.Entity.Relay;
import com.gendml.kutang.Entity.sensor_readings;
import com.gendml.kutang.Service.KTService;
import com.pi4j.system.SystemInfo;
import javazoom.jl.decoder.JavaLayerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author Зөндөө
 * @create 2021-08-24 18:37
 */
//跨域
@CrossOrigin
@Slf4j
@RestController
public class KTDController{

    @Autowired
    KTService ktService;

    //查询设备在线
    @GetMapping("/getDeviceState/{DeviceID}")
    public R getDeviceState(@PathVariable String DeviceID) throws IOException, InterruptedException {
        if (SystemInfo.getOsName() != null && DeviceID.equals("6874651")) return new R(200,"查询成功！","ON");
        return new R(500,"查询失败！","OFF");
    }

    //激活蜂鸣器三秒
    @PostMapping("/activeBuzzer")
    public R openBuzzer(){
        return ktService.activeBuzzer();
    }

    //打开RGB灯
    @PostMapping("/openLight/{id}")
    public R openLight(@PathVariable("id") int id){
        return ktService.openLight(id);
    }

//    //查看设备是否有火焰，烟雾
//    @GetMapping("/getWarningSensorState")
//    public List<Map<String, R>> getSomgSensorState() throws IOException, InterruptedException {
//        Map<String, R> map = new HashMap<>();
//        map.put("SmogSenor",ktService.readSmogSensor());
//        map.put("FireSenor",ktService.readSmogSensor());
//        return Arrays.asList(map);
//    }

    //获取水体+系统所有信息
//    @GetMapping("/getAllInfo")
//    public List<Map<String, Object>> getAllInfoger() throws IOException, InterruptedException {
//        List<Map<String, Object>> list = new ArrayList<>();
//        list.add(ktService.getAllInfo());
//        return list;
//    }

    //获取所有继电器的状态
    @GetMapping("/getAllRelayState")
    public Relay getAllRelayState() throws InterruptedException, IOException {
        return ktService.getAllRelayStates();
    }

    //打开继电器
    @PostMapping("/openRelay/{id}")
    public R openRealy(@PathVariable("id") int id,
                            Model model) throws InterruptedException, IOException, JavaLayerException {
        return ktService.openRelay(id);
    }

    //关闭继电器
    @PostMapping("/closeRelay/{id}")
    public R closeRelay(@PathVariable("id") int id,
                             Model model) throws InterruptedException, IOException, JavaLayerException {
        return ktService.closeRelay(id);
    }

    //打开所有继电器
    @PostMapping("/openAllRelay")
    public R openAllRelay() throws InterruptedException, IOException {
        return ktService.openAllRelay();
    }

    //关闭所有继电器
    @PostMapping("/closeAllRelay")
    public R closeAllRelay() throws InterruptedException, IOException {
        return ktService.closeAllRelay();
    }

    //库塘实时播报
    @PostMapping("/WaterInfoBroadcast")
    public R waterInfoBroadcast() throws IOException, InterruptedException {
        return ktService.Broadcast();
    }

    //邮件查询
    @PostMapping("/queryByEmail")
    public R queryByEmail() throws IOException, InterruptedException {
        return ktService.getAllInfoEmail("2457870242@qq.com");
    }

    //查询实时数据(redis)
    @GetMapping("/queryNow")
    public R queryNow(){
        sensor_readings sr = ktService.getsensorReadings(6874651L);
        if(sr != null){
            return new R(200,"success",sr);
        }
        return new R(500,"fail","");
    }


    @GetMapping("/faultDetect")
    public R faultDetect(){
        int res = ktService.faultDetect(6874651L);
        return new R(200,"fail",res);
    }

    @GetMapping("/envEva")
    public R envEva(){
        int res = ktService.envEva(6874651L);
        return new R(200,"fail",res);
    }

}

