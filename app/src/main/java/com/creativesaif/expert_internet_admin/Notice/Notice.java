package com.creativesaif.expert_internet_admin.Notice;

public class Notice {

    private String id, notice, sms, created_at;

    Notice (){

    }

    public Notice(String id, String notice, String sms, String created_at) {
        this.id = id;
        this.notice = notice;
        this.sms = sms;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
