package com.yelona.pojo;

/**
 * Created by chiranjeevi mateti on 10-Jan-17.
 */

public class ClientMaster {

    String ClientName,Mobile,Email,Address,ClientId,CompanyName;

    public ClientMaster(String clientName, String mobile, String email, String address, String clientId, String companyName) {
        ClientName = clientName;
        Mobile = mobile;
        Email = email;
        Address = address;
        ClientId = clientId;
        CompanyName = companyName;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }



}
