package com.example.Nuno.myapplication.housync_backend;

/**
 * Created by Nuno on 20/02/2016.
 */
public class HouSyncUser {

    private int userId = 0;
    private String userName = null;
    private String email = null;
    String phone = null;
    String snapshot = null;

    private int errorCode = 0;

    public HouSyncUser(int userId) {
        this.userId = userId;
    }

    public HouSyncUser() {

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }



}
