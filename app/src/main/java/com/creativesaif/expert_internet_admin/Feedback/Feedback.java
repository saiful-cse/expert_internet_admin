package com.creativesaif.expert_internet_admin.Feedback;

public class Feedback {

    private String id, client_id, created_at, feedback;

    Feedback(){

    }

    public Feedback(String id, String client_id, String created_at, String feedback) {
        this.id = id;
        this.client_id = client_id;
        this.created_at = created_at;
        this.feedback = feedback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
