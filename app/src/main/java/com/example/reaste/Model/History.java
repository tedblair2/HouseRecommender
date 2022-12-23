package com.example.reaste.Model;

public class History {
    String postid;
    String userid;

    public History() {
    }

    public History(String postid, String userid) {
        this.postid = postid;
        this.userid = userid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
