package com.creativesaif.expert_internet_admin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SalaryWrapper {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("salaries")
    @Expose
    private ArrayList<Salary> salaries = null;

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

    public ArrayList<Salary> getSalaries() {
        return salaries;
    }

    public void setSalaries(ArrayList<Salary> salaries) {
        this.salaries = salaries;
    }
}
