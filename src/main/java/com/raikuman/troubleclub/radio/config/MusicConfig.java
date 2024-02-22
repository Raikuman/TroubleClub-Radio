package com.raikuman.troubleclub.radio.config;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class MusicConfig implements Config {

    @Override
    public String fileName() {
        return "music";
    }

    @Override
    public LinkedHashMap<String, String> configs() {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        configMap.put("defaultvolume", "25");

        return configMap;
    }
}
