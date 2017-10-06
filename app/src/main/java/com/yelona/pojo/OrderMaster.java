package com.yelona.pojo;

/**
 * Created by PC2 on 27-Mar-17.
 */

public class OrderMaster {

    String orderId;
    String price;
    String quantity;
    String shipping_charge;
    String total;
    String shipping_address;
    String order_date;
    String delivered_date;
    String ItemName;
    String ItemSize;
    String order_status;
    String productImageURL;

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    String trackingId;

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
    }



    public OrderMaster(String orderId, String price, String quantity, String shipping_charge, String total, String shipping_address, String order_date, String delivered_date, String order_status, String dispatch_date, String completed_date, String itemName, String itemSize,String productImageURL,String trackingId) {
        this.orderId = orderId;
        this.price = price;
        this.quantity = quantity;
        this.shipping_charge = shipping_charge;
        this.total = total;
        this.shipping_address = shipping_address;
        this.order_date = order_date;
        this.delivered_date = delivered_date;
        this.order_status = order_status;
        this.dispatch_date = dispatch_date;
        this.completed_date = completed_date;
        this.ItemName = itemName;
        this.ItemSize = itemSize;
        this.productImageURL = productImageURL;
        this.trackingId = trackingId;

    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemSize() {
        return ItemSize;
    }

    public void setItemSize(String itemSize) {
        ItemSize = itemSize;
    }


    public String getDispatch_date() {
        return dispatch_date;
    }

    public void setDispatch_date(String dispatch_date) {
        this.dispatch_date = dispatch_date;
    }

    public String getCompleted_date() {
        return completed_date;
    }

    public void setCompleted_date(String completed_date) {
        this.completed_date = completed_date;
    }

    String dispatch_date;
    String completed_date;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(String shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDelivered_date() {
        return delivered_date;
    }

    public void setDelivered_date(String delivered_date) {
        this.delivered_date = delivered_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }


}
