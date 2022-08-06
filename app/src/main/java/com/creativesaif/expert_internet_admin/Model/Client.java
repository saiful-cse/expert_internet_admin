package com.creativesaif.expert_internet_admin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Client {

        @SerializedName("jwt")
        @Expose
        private String jwt;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("mode")
        @Expose
        private String mode;
        @SerializedName("registered")
        @Expose
        private String registered;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("zone")
        @Expose
        private String zone;
        @SerializedName("area")
        @Expose
        private String area;
        @SerializedName("disable_date")
        @Expose
        private String disableDate;
        @SerializedName("ppp_name")
        @Expose
        private String pppName;
        @SerializedName("ppp_pass")
        @Expose
        private String pppPass;

        @SerializedName("pkg_id")
        @Expose
        private String pkgId;
        @SerializedName("sms")
        @Expose
        private String sms;
        @SerializedName("reg_date")
        @Expose
        private String regDate;
        @SerializedName("expire_date")
        @Expose
        private String expireDate;
        @SerializedName("payment_method")
        @Expose
        private String paymentMethod;
        @SerializedName("ex_pppname")
        @Expose
        private String exPppname;

        @SerializedName("search_key")
        @Expose
        private String searchKey;

        @SerializedName("action_type")
        @Expose
        private String actionType;

        public String getActionType(){
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getJwt() {
            return jwt;
        }

        public void setJwt(String jwt) {
            this.jwt = jwt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisableDate() {
            return disableDate;
        }

        public void setDisableDate(String disableDate) {
            this.disableDate = disableDate;
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

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }
        public String getPkgId() {
            return pkgId;
        }

        public void setPkgId(String pkgId) {
            this.pkgId = pkgId;
        }

        public String getSms() {
            return sms;
        }

        public void setSms(String sms) {
            this.sms = sms;
        }

        public String getRegDate() {
            return regDate;
        }

        public void setRegDate(String regDate) {
            this.regDate = regDate;
        }

        public String getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getExPppname() {
            return exPppname;
        }

        public void setExPppname(String exPppname) {
            this.exPppname = exPppname;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getSearchKey(){
            return searchKey;
        }

        public void setSearchKey(String searchKey){
            this.searchKey = searchKey;
        }
}
