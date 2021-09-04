package com.creativesaif.expert_internet_admin.ClientList;

import android.os.Parcel;
import android.os.Parcelable;

public class Client {

    private String id, mode, name, area, phone, total_alert_client, username, payment_method;

    public Client(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotal_alert_client() {
        return total_alert_client;
    }

    public void setTotal_alert_client(String total_alert_client) {
        this.total_alert_client = total_alert_client;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

}
