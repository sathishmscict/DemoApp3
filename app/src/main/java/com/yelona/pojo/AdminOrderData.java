package com.yelona.pojo;

/**
 * Created by chiranjeevi mateti on 11-Jan-17.
 */

public class AdminOrderData {

    String orderid;
    String ordertime;
    String orderdate;
    String price;



    String address;
    String clientname;

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public AdminOrderData(String orderid, String ordertime, String orderdate, String price, String address, String clientname) {
        this.orderid = orderid;
        this.ordertime = ordertime;
        this.orderdate = orderdate;
        this.price = price;
        this.address = address;
        this.clientname = clientname;
    }





    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
