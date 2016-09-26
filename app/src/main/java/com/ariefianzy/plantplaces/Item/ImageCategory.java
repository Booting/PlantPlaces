package com.ariefianzy.plantplaces.Item;

public class ImageCategory {
    private String cat;
    private String url;

    public ImageCategory(String cat, String url){
        this.cat = cat;
        this.url = url;
    }

    public String getCat() {
        return cat;
    }

    public String getUrl() {
        return url;
    }
}
