package com.example.reaste.Model;

import java.util.List;

public class Images {
    List<String> imagelinks;

    public Images() {
    }

    public Images(List<String> imagelinks) {
        this.imagelinks = imagelinks;
    }

    public List<String> getImagelinks() {
        return imagelinks;
    }

    public void setImagelinks(List<String> imagelinks) {
        this.imagelinks = imagelinks;
    }
}
