package com.creativesaif.expert_internet_admin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailsWrapper {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("registered")
    @Expose
    private String registered;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("zone")
    @Expose
    private String zone;
    @SerializedName("ppp_name")
    @Expose
    private String pppName;
    @SerializedName("ppp_pass")
    @Expose
    private String pppPass;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("pkg_id")
    @Expose
    private String pkgId;
    @SerializedName("reg_date")
    @Expose
    private String regDate;
    @SerializedName("expire_date")
    @Expose
    private String expireDate;
    @SerializedName("disable_date")
    @Expose
    private String disableDate;
    @SerializedName("take_time")
    @Expose
    private String takeTime;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("packages")
    @Expose
    private List<Package> packages = null;


    //---- Router OS -----
    @SerializedName("router_status")
    @Expose
    private Integer routerStatus;

    @SerializedName("ppp_status")
    @Expose
    private String pppStatus;

    @SerializedName("ppp_activity")
    @Expose
    private String pppActivity;

    @SerializedName("router_mac")
    @Expose
    private String routerMac;

    @SerializedName("last_log_in")
    @Expose
    private String lastLogIn;

    @SerializedName("uptime")
    @Expose
    private String uptime;

    @SerializedName("last_loged_out")
    @Expose
    private String lastLogedOut;

    @SerializedName("download")
    @Expose
    private String download;
    @SerializedName("upload")
    @Expose
    private String upload;

    @SerializedName("connected_ip")
    @Expose
    private String connectedIp;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPppName() {
        return pppName;
    }

    public void setPppName(String pppName) {
        this.pppName = pppName;
    }

    public String getPppPass() {
        return pppPass;
    }

    public void setPppPass(String pppPass) {
        this.pppPass = pppPass;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPkgId() {
        return pkgId;
    }

    public void setPkgId(String pkgId) {
        this.pkgId = pkgId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getDisableDate() {
        return disableDate;
    }

    public void setDisableDate(String disableDate) {
        this.disableDate = disableDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }


    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    // Router OS///
    public Integer getRouterStatus() {
        return routerStatus;
    }
    public void setRouterStatus(Integer routerStatus) {
        this.routerStatus = routerStatus;
    }

    public String getPppStatus() {
        return pppStatus;
    }
    public void setPppStatus(String pppStatus) {
        this.pppStatus = pppStatus;
    }

    public String getPppActivity() {
        return pppActivity;
    }
    public void setPppActivity(String pppActivity) {
        this.pppActivity = pppActivity;
    }

    public String getRouterMac() {
        return routerMac;
    }
    public void setRouterMac(String routerMac) {
        this.routerMac = routerMac;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getLastLogedOut() {
        return lastLogedOut;
    }
    public void setLastLogedOut(String lastLogedOut) {
        this.lastLogedOut = lastLogedOut;
    }

    public String getLastLogIn(){
        return lastLogIn;
    }

    public String getDownload() {
        return download;
    }
    public void setDownload(String download) {
        this.download = download;
    }

    public String getUpload() {
        return upload;
    }
    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getConnectedIp(){
        return connectedIp;
    }
}

