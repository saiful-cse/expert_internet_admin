package com.creativesaif.expert_internet_admin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Package {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("pkg_id")
    @Expose
    private String pkgId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("speed")
    @Expose
    private String speed;
    @SerializedName("price")
    @Expose
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPkgId() {
        return pkgId;
    }

    public void setPkgId(String pkgId) {
        this.pkgId = pkgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

