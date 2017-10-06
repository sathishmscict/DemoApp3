package com.yelona.pojo;

/**
 * Created by chiranjeevi mateti on 12-Jan-17.
 */

public class ClientOrderData {
    String orderid;
    String orderday;
    String ordermonth;

    public ClientOrderData(String orderid, String orderday, String ordermonth, String orderstatus) {
        this.orderid = orderid;
        this.orderday = orderday;
        this.ordermonth = ordermonth;
        this.orderstatus = orderstatus;
    }


    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getOrdermonth() {
        return ordermonth;
    }

    public void setOrdermonth(String ordermonth) {
        this.ordermonth = ordermonth;
    }

    public String getOrderday() {
        return orderday;
    }

    public void setOrderday(String orderday) {
        this.orderday = orderday;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    String orderstatus;

}
