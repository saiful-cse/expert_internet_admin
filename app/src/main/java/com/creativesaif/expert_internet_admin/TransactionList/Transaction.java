package com.creativesaif.expert_internet_admin.TransactionList;

public class Transaction {

    private String txn_id, client_id, name, date, credit, debit, details, empid, method;

    public Transaction(){

    }

    public Transaction(String txn_id, String client_id, String name, String date, String credit, String debit, String details, String empid, String method) {
        this.txn_id = txn_id;
        this.client_id = client_id;
        this.name = name;
        this.date = date;
        this.credit = credit;
        this.debit = debit;
        this.details = details;
        this.empid = empid;
        this.method = method;
    }


    public String getTxn_id() {
        return txn_id;
    }

    public void setTxn_id(String txn_id) {
        this.txn_id = txn_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
