package com.ariefianzy.plantplaces.Item;

import com.google.android.gms.maps.model.LatLng;

public class AllData {
    private LatLng lokasi;
    private String photo;
    private String category;

    public AllData(String category, String photo, LatLng lokasi){
        this.lokasi = lokasi;
        this.photo = photo;
        this.category = category;
    }

    public LatLng getLokasi() {
        return lokasi;
    }

    public String getPhoto() {
        return photo;
    }

    public String getCategory() {
        return category;
    }
}
