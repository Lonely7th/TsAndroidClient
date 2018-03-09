package com.system.ts.android.bean;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class TkDetailsBean {
    private String cur_min_price;
    private String cur_close_price;
    private String cur_timer;
    private String cur_price_range;
    private String cur_max_price;
    private String cur_total_money;
    private String cur_total_volume;
    private String cur_open_price;

    public TkDetailsBean(String cur_min_price, String cur_close_price, String cur_timer, String cur_price_range, String cur_max_price, String cur_total_money, String cur_total_volume, String cur_open_price) {
        this.cur_min_price = cur_min_price;
        this.cur_close_price = cur_close_price;
        this.cur_timer = cur_timer;
        this.cur_price_range = cur_price_range;
        this.cur_max_price = cur_max_price;
        this.cur_total_money = cur_total_money;
        this.cur_total_volume = cur_total_volume;
        this.cur_open_price = cur_open_price;
    }

    public String getCur_min_price() {
        return cur_min_price;
    }

    public void setCur_min_price(String cur_min_price) {
        this.cur_min_price = cur_min_price;
    }

    public String getCur_close_price() {
        return cur_close_price;
    }

    public void setCur_close_price(String cur_close_price) {
        this.cur_close_price = cur_close_price;
    }

    public String getCur_timer() {
        return cur_timer;
    }

    public void setCur_timer(String cur_timer) {
        this.cur_timer = cur_timer;
    }

    public String getCur_price_range() {
        return cur_price_range;
    }

    public void setCur_price_range(String cur_price_range) {
        this.cur_price_range = cur_price_range;
    }

    public String getCur_max_price() {
        return cur_max_price;
    }

    public void setCur_max_price(String cur_max_price) {
        this.cur_max_price = cur_max_price;
    }

    public String getCur_total_money() {
        return cur_total_money;
    }

    public void setCur_total_money(String cur_total_money) {
        this.cur_total_money = cur_total_money;
    }

    public String getCur_total_volume() {
        return cur_total_volume;
    }

    public void setCur_total_volume(String cur_total_volume) {
        this.cur_total_volume = cur_total_volume;
    }

    public String getCur_open_price() {
        return cur_open_price;
    }

    public void setCur_open_price(String cur_open_price) {
        this.cur_open_price = cur_open_price;
    }
}
