package com.system.ts.android.bean;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class SearchBean {
    private String tkCode = "";
    private String tkName = "";

    public SearchBean(String tkCode, String tkName) {
        this.tkCode = tkCode;
        this.tkName = tkName;
    }

    public String getTkCode() {
        return tkCode;
    }

    public void setTkCode(String tkCode) {
        this.tkCode = tkCode;
    }

    public String getTkName() {
        return tkName;
    }

    public void setTkName(String tkName) {
        this.tkName = tkName;
    }
}
