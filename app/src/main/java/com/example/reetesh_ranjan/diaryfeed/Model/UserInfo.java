package com.example.reetesh_ranjan.diaryfeed.Model;

public class UserInfo {
    String uid,name,email,mobile_no,profilePicUrl;

    public UserInfo(){

    }

    public UserInfo(String uid, String name, String email, String mobile_no, String profilePicUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mobile_no = mobile_no;
        this.profilePicUrl = profilePicUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
