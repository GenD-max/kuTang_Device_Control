package com.gendml.kutang.utils;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Зөндөө
 * @create 2021-08-28 21:21
 */
public class mp3Player {
    public static void playMp3(String fileName) throws FileNotFoundException, JavaLayerException {
        BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(fileName));
        Player player = new Player(buffer);
        player.play();
    }
}
