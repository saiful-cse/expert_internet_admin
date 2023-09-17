package com.creativesaif.expert_internet_admin.TaskList;

public class Task {

    private String id, assign_by, assign_on, description, completed, created_at, completed_at;

    public Task(String id, String assign_by, String assign_on, String description, String completed, String created_at, String completed_at) {
        this.id = id;
        this.assign_by = assign_by;
        this.assign_on = assign_on;
        this.description = description;
        this.completed = completed;
        this.created_at = created_at;
        this.completed_at = completed_at;
    }

    public Task() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssign_by() {
        return assign_by;
    }

    public void setAssign_by(String assign_by) {
        this.assign_by = assign_by;
    }

    public String getAssign_on() {
        return assign_on;
    }

    public void setAssign_on(String assign_on) {
        this.assign_on = assign_on;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }
}
