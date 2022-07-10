package com.gendml.kutang.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Зөндөө
 * @create 2021-09-02 9:37
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Raspi {
    private String Timestamp;//时间戳
    private String TotalMemory;//最大内存
    private String UsedMemory;//占用内存
    private String CPUUtility;//CPU占用率
    private String RaspiTemps;//箱内温度
    private String RaspiHumidity;//箱内湿度
    private String IsHasSmog;//是否有烟雾
    private String  IsHasFire;//是否有火焰


    public Raspi(String totalMemory, String usedMemory, String cPUUtility, String raspiTemps, String raspiHumidity, String isHasSmog, String isHasFire) {
        TotalMemory = totalMemory;
        UsedMemory = usedMemory;
        CPUUtility = cPUUtility;
        RaspiTemps = raspiTemps;
        RaspiHumidity = raspiHumidity;
        IsHasSmog = isHasSmog;
        IsHasFire = isHasFire;
    }

    @Override
    public String toString() {
        return TotalMemory + "," + UsedMemory + "," + CPUUtility + "," + RaspiTemps + "," + RaspiHumidity + "," + IsHasSmog + "," + IsHasFire;
    }
}
