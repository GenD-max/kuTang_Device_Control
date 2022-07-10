package com.gendml.kutang.utils;

import com.gendml.kutang.Entity.Relay;
import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Зөндөө
 * @create 2021-08-29 21:05
 */
@Slf4j
public class SerialCommunicate {

    //得到串口
    public static  Serial getSerial(String serialName, Baud boud) {
        // 创建串行通信类的实例
        final Serial serial = SerialFactory.createInstance();

        try {
            // 创建串行配置对象
            SerialConfig config = new SerialConfig();

            // 设置默认串行设置（设备、波特率、流量控制等）
            //
            // 默认情况下，使用 Raspberry Pi 上的 DEFAULT com 端口（暴露在 GPIO 头上）
            // 注意：此实用程序方法将确定默认串行端口
            //       检测到的平台和电路板模型。适用于所有 Raspberry Pi 型号
            //      除了 3B，它将返回“devttyAMA0”。对于树莓派
            //       模型 3B 可能会返回“devttyS0”或“devttyAMA0”，具体取决于
            //       环境配置。
            config.device(serialName)//串口名
                    .baud(boud)//波特率
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

            // 解析可选的命令参数选项以覆盖默认串行设置。
            CommandArgumentParser.getSerialConfig(config);


            // 使用配置设置打开默认串行设备端口
            serial.open(config);
            System.out.println(serial);

            return serial;

        } catch (IOException ex) {
            System.out.println(" ==>> 串行设置失败 : " + ex.getMessage());
            return null;
        }
    }

    //获取rs232继电器状态
    public static Relay getRS232State(String res) {
        if (res.length() == 14) {
            res = res.substring(9, 10);
            log.info(res);
            switch (res) {
                case "1":
                    return new Relay("HIGH", "LOW", "LOW", "LOW");//0001
                case "2":
                    return new Relay("LOW", "HIGH", "LOW", "LOW");//0010
                case "3":
                    return new Relay("HIGH", "HIGH", "LOW", "LOW");//0011
                case "4":
                    return new Relay("LOW", "LOW", "HIGH", "LOW");//0100
                case "5":
                    return new Relay("HIGH", "LOW", "HIGH", "LOW");//0101
                case "6":
                    return new Relay("LOW", "HIGH", "HIGH", "LOW");//0110
                case "7":
                    return new Relay("HIGH", "HIGH", "HIGH", "LOW");//0111
                case "8":
                    return new Relay("LOW", "LOW", "LOW", "HIGH");//1000
                case "9":
                    return new Relay("HIGH", "LOW", "LOW", "HIGH");//1001
                case "a":
                    return new Relay("LOW", "HIGH", "LOW", "HIGH");//1010
                case "b":
                    return new Relay("HIGH", "HIGH", "LOW", "HIGH");//1011
                case "c":
                    return new Relay("LOW", "LOW", "HIGH", "HIGH");//1100
                case "d":
                    return new Relay("HIGH", "LOW", "HIGH", "HIGH");//1101
                case "e":
                    return new Relay("LOW", "HIGH", "HIGH", "HIGH");//1110
                case "f":
                    return new Relay("HIGH", "HIGH", "HIGH", "HIGH");//1111
                default:
                    return new Relay("LOW", "LOW", "LOW", "LOW");
            }
        }
        return new Relay();
    }
}
