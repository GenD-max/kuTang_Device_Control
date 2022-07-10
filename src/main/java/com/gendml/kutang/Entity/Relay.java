package com.gendml.kutang.Entity;

import lombok.*;

/**
 * @author Зөндөө
 * @create 2021-08-31 21:53
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Relay {
    //代表继电器的状态
    private String relay1;
    private String relay2;
    private String relay3;
    private String relay4;
}
