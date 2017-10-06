package com.yelona.pojo;

/**
 * Created by Sathish Gadde on 16-Feb-17.
 */

public class Addresses {

    String id;
    String ares;
    String street;
    String landmark;
    String city;
    String state;
    String address1;
    String address2;
    String pincode;
    String name;


    String email;
    String mobileno;
    String type;
    String created_at;
    String updated_at;

    public Addresses(String id, String ares, String street, String landmark, String city, String state, String address1, String address2, String pincode, String name, String email, String mobileno, String type, String created_at, String updated_at)
    {
        this.id = id;
        this.ares = ares;
        this.street = street;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.address1 = address1;
        this.address2 = address2;
        this.pincode = pincode;
        this.name = name;
        this.email = email;
        this.mobileno = mobileno;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAres() {
        return ares;
    }

    public void setAres(String ares) {
        this.ares = ares;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


}
