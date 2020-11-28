package com.creativesaif.expert_internet_admin.ClientList;

import android.os.Parcel;
import android.os.Parcelable;

public class Client implements Parcelable {

    private String id, mode, name, area, phone, total_alert_client, username;

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

    public static Creator<Client> getCREATOR() {
        return CREATOR;
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
        dest.writeString(this.total_alert_client);
        dest.writeString(this.username);
    }

    protected Client(Parcel in) {
        this.id = in.readString();
        this.mode = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.total_alert_client = in.readString();
        this.username = in.readString();
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
