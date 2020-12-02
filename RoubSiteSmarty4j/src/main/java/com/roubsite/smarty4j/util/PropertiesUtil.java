package com.roubsite.smarty4j.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
    public static String getConfigString(String key) {
        return getString(key, "config.properties");
    }

    public final static Logger log = Logger.getLogger(PropertiesUtil.class);

    /**
     * 指定文件读取配置
     *
     * @param key key
     * @param propertiesFile 配置文件
     */
    public static String getString(String key, String propertiesFile) {

        Properties tmpProperties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesFile);
            if(null != inputStream){
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                tmpProperties.load(bf);
            }else{
                log.error("读取配置文件：" + propertiesFile + "失败");
            }
        } catch (Exception e) {
			log.error("获取配置文件：" + propertiesFile + "的配置项失败", e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (Exception e) {
                }
            }
        }
        Object obj;
        return key == null || "".equals(key) ? "" : ((obj = tmpProperties.get(key)) == null ? "" : (String) obj);
    }

}
