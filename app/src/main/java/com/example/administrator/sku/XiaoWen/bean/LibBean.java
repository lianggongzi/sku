package com.example.administrator.sku.XiaoWen.bean;

/**
 * Created by Administrator on 2018\12\6 0006.
 */

public class LibBean {
    String time;
    String lib;
    String sku;

    public LibBean(String time, String lib, String sku) {
        this.time = time;
        this.lib = lib;
        this.sku = sku;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return "LibBean{" +
                "time='" + time + '\'' +
                ", lib='" + lib + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}
