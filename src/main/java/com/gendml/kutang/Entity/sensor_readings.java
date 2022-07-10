package com.gendml.kutang.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Зөндөө
 * @create 2022-04-13 13:32
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@TableName("sensor_readings") //通过此注解，确定该实体类对应的表
public class sensor_readings {
    private Integer device_id;
    private Long time;
    private Float tds;
    private Float water_temp;
    private Float water_level;
    private Float turbidity;
    private Long memory;
    private Long memory_used;
    private Integer cpu;
    private Float device_temp;
    private Float device_humidity;
    private Integer Is_has_smog;
    private Integer Is_has_fire;
}
