package com.example.reaste.Model;

public class Users {
    String email;
    String name;
    String phone;
    String userid;
    String profileimage;

    public Users() {
    }

    public Users(String email, String name, String phone, String userid, String profileimage) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userid = userid;
        this.profileimage = profileimage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
