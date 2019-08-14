package com.creativesaif.expert_internet_admin.ClientList;

import android.os.Parcel;
import android.os.Parcelable;

public class Client implements Parcelable {

    private String id, mode, name, phone, address, email, int_conn_type, wan_ip, subnet, default_gateway,
    dns1, dns2, onu_mac, speed, fee, bill_type, alert, reg_date, active_date, inactive_date;

    public Client(){

    }

    public Client(String id, String mode, String name, String phone, String address, String email, String int_conn_type, String wan_ip, String subnet, String default_gateway, String dns1, String dns2, String onu_mac, String speed, String fee, String bill_type, String alert, String reg_date, String active_date, String inactive_date) {
        this.id = id;
        this.mode = mode;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.int_conn_type = int_conn_type;
        this.wan_ip = wan_ip;
        this.subnet = subnet;
        this.default_gateway = default_gateway;
        this.dns1 = dns1;
        this.dns2 = dns2;
        this.onu_mac = onu_mac;
        this.speed = speed;
        this.fee = fee;
        this.bill_type = bill_type;
        this.alert = alert;
        this.reg_date = reg_date;
        this.active_date = active_date;
        this.inactive_date = inactive_date;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInt_conn_type() {
        return int_conn_type;
    }

    public void setInt_conn_type(String int_conn_type) {
        this.int_conn_type = int_conn_type;
    }

    public String getWan_ip() {
        return wan_ip;
    }

    public void setWan_ip(String wan_ip) {
        this.wan_ip = wan_ip;
    }

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public String getDefault_gateway() {
        return default_gateway;
    }

    public void setDefault_gateway(String default_gateway) {
        this.default_gateway = default_gateway;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    public String getOnu_mac() {
        return onu_mac;
    }

    public void setOnu_mac(String onu_mac) {
        this.onu_mac = onu_mac;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getActive_date() {
        return active_date;
    }

    public void setActive_date(String active_date) {
        this.active_date = active_date;
    }

    public String getInactive_date() {
        return inactive_date;
    }

    public void setInactive_date(String inactive_date) {
        this.inactive_date = inactive_date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.mode);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeString(this.email);
        dest.writeString(this.int_conn_type);
        dest.writeString(this.wan_ip);
        dest.writeString(this.subnet);
        dest.writeString(this.default_gateway);
        dest.writeString(this.dns1);
        dest.writeString(this.dns2);
        dest.writeString(this.onu_mac);
        dest.writeString(this.speed);
        dest.writeString(this.fee);
        dest.writeString(this.bill_type);
        dest.writeString(this.alert);
        dest.writeString(this.reg_date);
        dest.writeString(this.active_date);
        dest.writeString(this.inactive_date);
    }

    protected Client(Parcel in) {
        this.id = in.readString();
        this.mode = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.email = in.readString();
        this.int_conn_type = in.readString();
        this.wan_ip = in.readString();
        this.subnet = in.readString();
        this.default_gateway = in.readString();
        this.dns1 = in.readString();
        this.dns2 = in.readString();
        this.onu_mac = in.readString();
        this.speed = in.readString();
        this.fee = in.readString();
        this.bill_type = in.readString();
        this.alert = in.readString();
        this.reg_date = in.readString();
        this.active_date = in.readString();
        this.inactive_date = in.readString();
    }

    public static final Parcelable.Creator<Client> CREATOR = new Parcelable.Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel source) {
            return new Client(source);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
}
