package com.yelona.pojo;

/**
 * Created by SATHISH on 07-Apr-17.
 */

public class SearchData {

    String search_flag, search_id, search_product_id, search_name, search_category_id, search_category_name, search_category_type, search_category_type_name, search_is_active_seller, search_is_active_admin, search_sku, search_price;



    public SearchData(String search_flag, String search_id, String search_product_id, String search_name, String search_category_id, String search_category_name, String search_category_type, String search_category_type_name, String search_is_active_seller, String search_is_active_admin, String search_sku, String search_price) {
        this.search_flag = search_flag;
        this.search_id = search_id;
        this.search_product_id = search_product_id;
        this.search_name = search_name;
        this.search_category_id = search_category_id;
        this.search_category_name = search_category_name;
        this.search_category_type = search_category_type;
        this.search_category_type_name = search_category_type_name;
        this.search_is_active_seller = search_is_active_seller;
        this.search_is_active_admin = search_is_active_admin;
        this.search_sku = search_sku;
        this.search_price = search_price;
    }

    public String getSearch_flag() {
        return search_flag;
    }

    public void setSearch_flag(String search_flag) {
        this.search_flag = search_flag;
    }

    public String getSearch_id() {
        return search_id;
    }

    public void setSearch_id(String search_id) {
        this.search_id = search_id;
    }

    public String getSearch_product_id() {
        return search_product_id;
    }

    public void setSearch_product_id(String search_product_id) {
        this.search_product_id = search_product_id;
    }

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }

    public String getSearch_category_id() {
        return search_category_id;
    }

    public void setSearch_category_id(String search_category_id) {
        this.search_category_id = search_category_id;
    }

    public String getSearch_category_name() {
        return search_category_name;
    }

    public void setSearch_category_name(String search_category_name) {
        this.search_category_name = search_category_name;
    }

    public String getSearch_category_type() {
        return search_category_type;
    }

    public void setSearch_category_type(String search_category_type) {
        this.search_category_type = search_category_type;
    }

    public String getSearch_category_type_name() {
        return search_category_type_name;
    }

    public void setSearch_category_type_name(String search_category_type_name) {
        this.search_category_type_name = search_category_type_name;
    }

    public String getSearch_is_active_seller() {
        return search_is_active_seller;
    }

    public void setSearch_is_active_seller(String search_is_active_seller) {
        this.search_is_active_seller = search_is_active_seller;
    }

    public String getSearch_is_active_admin() {
        return search_is_active_admin;
    }

    public void setSearch_is_active_admin(String search_is_active_admin) {
        this.search_is_active_admin = search_is_active_admin;
    }

    public String getSearch_sku() {
        return search_sku;
    }

    public void setSearch_sku(String search_sku) {
        this.search_sku = search_sku;
    }

    public String getSearch_price() {
        return search_price;
    }

    public void setSearch_price(String search_price) {
        this.search_price = search_price;
    }
}
