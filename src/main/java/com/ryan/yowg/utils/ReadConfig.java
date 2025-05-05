package com.ryan.yowg.utils;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

import java.io.File;
import java.util.List;

public class ReadConfig {
    private static final String CONF_FILE = System.getProperty("user.home")+ "/Program/yo-wg/conf/application.conf";

    static File configFile = new File(CONF_FILE);
    static Config config = ConfigFactory.parseFile(configFile);

    public static List<String> getListWg(){
        return config.getStringList("app.list_wg");
    }

    public static String getPassword(){
        return config.getString("app.password");
    }
}
