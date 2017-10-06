package com.yelona.pojo;

/**
 * Created by Sathish Gadde on 30-Jan-17.
 */

public class ProductData {

    String shippingcost;
    String productid;
    String category_id;
    String image_url;
    String seller_id;
    String price;
    String offer_value;
    String offer_tag;
    String color;
    String weight;
    String height;
    String length;
    String width;
    String size;
    String mrp;
    String ship_date;
    String brand;
    String inventory;
    String committed_qty;
    String productname;
    String description;



    String mlmDiscount;

    String quantity;





    boolean selectionStatus;


    String userid;

    /*Construtor for display single product images in SingleItemActivity*/
    public ProductData(String image_url) {
        this.image_url = image_url;
    }
    public ProductData(String image_url, boolean selectionStatus) {
        this.image_url = image_url;
        this.selectionStatus = selectionStatus;
    }

    public ProductData(String productid, String pname, String image_url, String mrp, String price, String userid,String Quantity,String inventory) {
        this.productid = productid;
        this.image_url = image_url;
        this.mrp = mrp;
        this.price = price;
        this.userid = userid;
        this.productname =pname;
        this.quantity = Quantity;
        this.inventory = inventory;

    }

    public ProductData(String shippingcost, String productid, String productname, String description, String category_id, String image_url, String seller_id, String price, String offer_value, String offer_tag, String color, String weight, String height, String length, String width, String size, String mrp, String ship_date, String brand, String inventory, String committed_qty,String quantity,String mlmDiscount) {
        this.shippingcost = shippingcost;
        this.productid = productid;
        this.productname = productname;
        this.description = description;
        this.category_id = category_id;
        this.image_url = image_url;
        this.seller_id = seller_id;
        this.price = price;
        this.offer_value = offer_value;
        this.offer_tag = offer_tag;
        this.color = color;
        this.weight = weight;
        this.height = height;
        this.length = length;
        this.width = width;
        this.size = size;
        this.mrp = mrp;
        this.ship_date = ship_date;
        this.brand = brand;
        this.inventory = inventory;
        this.committed_qty = committed_qty;


        this.quantity = quantity;

        this.mlmDiscount = mlmDiscount;


    }

    public String getMlmDiscount() {
        return mlmDiscount;
    }

    public void setMlmDiscount(String mlmDiscount) {
        this.mlmDiscount = mlmDiscount;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isSelectionStatus() {
        return selectionStatus;
    }

    public void setSelectionStatus(boolean selectionStatus) {
        this.selectionStatus = selectionStatus;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getShippingcost() {
        return shippingcost;
    }

    public void setShippingcost(String shoppingcost) {
        this.shippingcost = shoppingcost;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOffer_value() {
        return offer_value;
    }

    public void setOffer_value(String offer_value) {
        this.offer_value = offer_value;
    }

    public String getOffer_tag() {
        return offer_tag;
    }

    public void setOffer_tag(String offer_tag) {
        this.offer_tag = offer_tag;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getShip_date() {
        return ship_date;
    }

    public void setShip_date(String ship_date) {
        this.ship_date = ship_date;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public String getCommitted_qty() {
        return committed_qty;
    }

    public void setCommitted_qty(String committed_qty) {
        this.committed_qty = committed_qty;
    }

}
