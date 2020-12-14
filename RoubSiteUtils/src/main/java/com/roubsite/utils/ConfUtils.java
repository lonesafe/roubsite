package com.roubsite.utils;

public class ConfUtils {

    /**
     * 兼容properties的获取配置方法（兼容方法，配置项逐步使用yaml方式）
     *
     * @param propertiesKey  properties的key
     * @param PropertiesFile properties文件
     * @param yamlKeys       yaml的keys
     * @return 配置值
     */
    @Deprecated
    public static String getConf(String propertiesKey, String PropertiesFile, String[] yamlKeys, String defaultValue) {
        String value = PropertiesUtil.getString(propertiesKey, PropertiesFile);
        if (StringUtils.isEmpty(value)) {
            value = YmlUtils.getConfig(yamlKeys, defaultValue);
        }
        return value;
    }

    /**
     * 兼容properties的获取配置方法（兼容方法，配置项逐步使用yaml方式）
     *
     * @param propertiesKey  properties的key
     * @param PropertiesFile properties文件
     * @param yamlKeys       yaml的keys
     * @return 配置值
     */
    @Deprecated
    public static String getConf(String propertiesKey, String PropertiesFile, String... yamlKeys) {
        String value = PropertiesUtil.getString(propertiesKey, PropertiesFile);
        if (StringUtils.isEmpty(value)) {
            value = YmlUtils.getConfig(yamlKeys, "");
        }
        return value;
    }

    /**
     * 获取yaml配置项
     *
     * @param yamlKeys 配置项
     * @return 值
     */
    public static String getConf(String[] yamlKeys, String defaultValue) {
        return YmlUtils.getConfig(yamlKeys, defaultValue);
    }
}
