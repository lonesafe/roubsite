package com.roubsite.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class YmlUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(YmlUtils.class);
    private static Map<String, Object> config = new HashMap<>();

    static {
        config = new HashMap<>();
        //从classpath下获取配置文件路径
        Yaml yaml = new Yaml();
        //获取class下所有yml文件
        String[] ymlList = new File(YmlUtils.class.getResource("/").getPath()).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml") || name.endsWith(".yaml");
            }
        });
        //通过yaml对象将配置文件的输入流转换成map原始map对象
        for (String ymlName : ymlList) {
            System.out.println(YmlUtils.class.getResource("/").getPath() + ymlName);
            String yamlContent = new FileUtils().fileGetContent(YmlUtils.class.getResource("/").getPath() + ymlName);
            config.putAll(yaml.load(yamlContent));
        }
    }

    public static String getConfig(String[] keys, String defaultValue) {
        String value = "";
        Map<String,Object> temp = new HashMap(config);
        int count = keys.length;
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
                try {
                    Object obj = temp.get(keys[i]);
                    if(StringUtils.isNotEmptyObject(obj)){
                        value = String.valueOf(obj);
                    }else{
                        return "";
                    }
                } catch (Exception e) {
                    LOGGER.error("获取配置项错误", e);
                }
            } else {
                try {
                    temp = (Map) temp.get(keys[i]);
                } catch (Exception e) {
                    LOGGER.error("获取配置项错误", e);
                }
            }
        }
        return value;
    }
}
