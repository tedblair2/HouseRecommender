package com.example.reaste.Model;

import java.util.List;

public class Posts {
    String title;
    String agency;
    String location;
    String postid;
    String publisherid;
    String units;
    String bedrooms;
    String type;
    String price;
    String amenities;

    public Posts() {
    }

    public Posts(String title, String agency, String location, String postid, String publisherid, String units, String bedrooms, String type, String price, String amenities) {
        this.title = title;
        this.agency = agency;
        this.location = location;
        this.postid = postid;
        this.publisherid = publisherid;
        this.units = units;
        this.bedrooms = bedrooms;
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmenities() {
        return amenities;
    }
    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }
}
