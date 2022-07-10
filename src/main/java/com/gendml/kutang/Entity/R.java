package com.gendml.kutang.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Зөндөө
 * @create 2021-08-25 11:00
 */

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class R<T> {

    private int code;
    private String msg;
    private T data;

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }
}
