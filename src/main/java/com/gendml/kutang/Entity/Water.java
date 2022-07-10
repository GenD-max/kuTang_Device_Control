package com.gendml.kutang.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Зөндөө
 * @create 2021-09-02 9:30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Water {
    private String Timestamp;//时间戳
    private String TDS; //ppm
    private String Temp;//℃ 温度
    private String Turbidity;//NTU 浊度
    private String Level;//cm

    public Water(String TDS, String temp, String turbidity, String level) {
        this.TDS = TDS;
        Temp = temp;
        Turbidity = turbidity;
        Level = level;
    }

    @Override
    public String toString() {
        return TDS + "," + Temp + "," + Turbidity + "," + Level;
    }

}
