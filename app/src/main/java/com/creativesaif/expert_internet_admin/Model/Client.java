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
        @SerializedName("registered")
        @Expose
        private String registered;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("ex_phone")
        @Expose
        private String exPhone;
        @SerializedName("area")
        @Expose
        private String area;
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


        //---- from router os -----

        @SerializedName("last_log_out")
        @Expose
        private String last_log_out;

        @SerializedName("action")
        @Expose
        private String action;

        public String getAction(){
            return action;
        }

        public void setAction(String action) {
            this.action = action;
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

        // --- From router OS ----


        public String getLast_log_out() {
            return last_log_out;
        }

        public void setLast_log_out(String last_log_out) {
            this.last_log_out = last_log_out;
        }
}
