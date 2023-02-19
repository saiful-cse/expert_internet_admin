package com.creativesaif.expert_internet_admin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Salary {
    @SerializedName("jwt")
    @Expose
    private String jwt;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("admin_id")
    @Expose
    private String admin_id;

    @SerializedName("employee_id")
    @Expose
    private String employee_id;

    @SerializedName("month")
    @Expose
    private String month;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("date")
    @Expose
    private String date;

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getEmployee_id(){
        return employee_id;
    }
    public void setEmployee_id(String employee_id){
        this.employee_id = employee_id;
    }

    public String getAdmin_id(){
        return admin_id;
    }
    public void setAdmin_id(String admin_id){
        this.admin_id = admin_id;
    }

    public String getMonth(){
        return month;
    }
    public void setMonth(String month){
        this.month = month;
    }

    public String getAmount(){
        return amount;
    }
    public void setAmount(String amount){
        this.amount = amount;
    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getJwt() {
        return jwt;
    }
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
