package com.creativesaif.expert_internet_admin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trns {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("jwt")
    @Expose
    private String jwt;
    @SerializedName("txn_type")
    @Expose
    private String txnType;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("admin_id")
    @Expose
    private String adminId;

    @SerializedName("client_id")
    @Expose
    private String clientId;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

