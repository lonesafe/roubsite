package com.roubsite.utils;

public class ClassBean {
    /**
     * 类全路径
     */
    private String classPath = "";
    /**
     * 方法名
     */
    private String method = "";
    /**
     * 所属模板路径
     */
    private String template = "";
    /**
     * 错误信息
     */
    private String erroMessage = "";


    public String getErroMessage() {
        return erroMessage;
    }

    public void setErroMessage(String erroMessage) {
        this.erroMessage = erroMessage;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}
