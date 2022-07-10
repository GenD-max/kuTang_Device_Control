package com.gendml.kutang.utils;

import com.aliyun.tea.*;
import com.aliyun.dyvmsapi20170525.*;
import com.aliyun.dyvmsapi20170525.models.*;
import com.aliyun.teaopenapi.*;
import com.aliyun.teaopenapi.models.Config;

/**
 * @author Зөндөө
 * @create 2021-09-14 22:32
 */
public class PhoneRemind {
    public static com.aliyun.dyvmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dyvmsapi.aliyuncs.com";
        return new com.aliyun.dyvmsapi20170525.Client(config);
    }
    public static void  callPhonebyNumber(String number) throws Exception {
        com.aliyun.dyvmsapi20170525.Client client = createClient("LTAI5tAZD3w1rc393p91RrTe", "mnb4n05QthBhniyVwZaOngZmmeNQHm");
        SingleCallByVoiceRequest singleCallByVoiceRequest = new SingleCallByVoiceRequest()
                .setCalledNumber(number)
                .setVoiceCode("be165bb1-79bb-42ed-bf92-4608aa8008af.wav")
                .setPlayTimes(1);
        // 复制代码运行请自行打印 API 的返回值
        client.singleCallByVoice(singleCallByVoiceRequest);
    }
}
