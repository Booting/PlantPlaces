package com.ariefianzy.plantplaces.Item;

import com.google.android.gms.maps.model.LatLng;

public class MapData {
    private LatLng lokasi;
    private String photo;

    public MapData(String photo, LatLng lokasi){
        this.lokasi = lokasi;
        this.photo = photo;
    }

    public LatLng getLokasi() {
        return lokasi;
    }

    public String getPhoto() {
        return photo;
    }
}
