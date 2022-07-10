package com.gendml.kutang.utils;
import com.pi4j.io.gpio.*;
/**
 * @author Зөндөө
 * @create 2021-08-25 12:21
 */
public class gpioUtil {
    //用来连接对应的端口
    private static final GpioController gpio = GpioFactory.getInstance();

    public static GpioPinDigitalOutput getGPD_R(){
        GpioPinDigitalOutput PinR = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "R", PinState.LOW);
        PinR.setMode(PinMode.DIGITAL_OUTPUT);
        PinR.setShutdownOptions(true, PinState.LOW);
        return PinR;
    }

    public static GpioPinDigitalOutput getGPD_G() {
        GpioPinDigitalOutput PinG = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "G", PinState.LOW);
        PinG.setMode(PinMode.DIGITAL_OUTPUT);
        PinG.setShutdownOptions(true, PinState.LOW);
        return PinG;
    }

    public static GpioPinDigitalOutput getGPD_B() {
        GpioPinDigitalOutput PinB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "B", PinState.LOW);
        PinB.setMode(PinMode.DIGITAL_OUTPUT);
        PinB.setShutdownOptions(true, PinState.LOW);
        return PinB;
    }

    //蜂鸣器
    public static GpioPinDigitalOutput getGPD19() {
        GpioPinDigitalOutput p3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "Relay3", PinState.LOW);
        p3.setMode(PinMode.DIGITAL_OUTPUT);
        p3.setShutdownOptions(true, PinState.LOW);
        return p3;
    }


    public static GpioController getGpioController() {
        return gpio;
    }
}
