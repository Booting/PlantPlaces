package com.ariefianzy.plantplaces.Item;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Helper.ImeiManager;
import com.ariefianzy.plantplaces.Item.MapData;
import com.ariefianzy.plantplaces.MainActivity;
import com.ariefianzy.plantplaces.R;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public final class Data {
    public static final List<String> URLS = new ArrayList<String>();
    public static final List<MapData> data = new ArrayList<MapData>();
    public static final List<AllData> allData = new ArrayList<AllData>();
    public static final List<LatLng> dataObject = new ArrayList<LatLng>();

    public String IMEI;
    public static boolean status = false;
    private ImeiManager dataImei;


    public Data(){

    }
    public Data(final String imei, final Context context){
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Checking IMEI...");
        dialog.setCancelable(false);
        dialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("IMEI");
        query.whereEqualTo("IMEI", imei);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listImei, ParseException e) {
                dialog.dismiss();
                if (listImei.size() != 0) {
                    dataImei = new ImeiManager(context);
                    dataImei.saveImei(imei);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Attention!");
                    alertDialog.setIcon(R.drawable.ic_warning);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Nomer IMEI device anda tidak terdaftar di server");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "EXIT",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((Activity) context).finish();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }
    public void loadData(Context context){
        URLS.clear();
        allData.clear();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading data...");
        dialog.setCancelable(false);
        dialog.show();

        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("_User");
        innerQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereMatchesQuery("author", innerQuery);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                dialog.dismiss();
                if(e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        URLS.add(list.get(i).getParseFile("photo").getUrl());
                        LatLng lokasi = new LatLng(Double.parseDouble(list.get(i).getString("latitude")),Double.parseDouble(list.get(i).getString("longitude")));
                        allData.add(new AllData(list.get(i).getString("category"),list.get(i).getParseFile("photo").getUrl(),lokasi));
                    }
                }else {

                }
            }
        });
    }
    public void loadMapData(final Context context){
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("object");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                dialog.dismiss();
                if (list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        LatLng lokasi = new LatLng(Double.parseDouble(list.get(i).getString("latitude")),Double.parseDouble(list.get(i).getString("longitude")));
                        dataObject.add(lokasi);
                    }
                    ((MainActivity) context).populateMarker();
                    loadData(context);
                } else {

                }
            }
        });
    }
    public Data(final Context context, String load){
        if(load.equals("loadObject")){
            loadMapData(context);
        }
    }
    public Data(final Context context) {
        // No instances.
        URLS.clear();
        data.clear();
        allData.clear();
        loadData(context);
        /*
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading data...");
        dialog.setCancelable(false);
        dialog.show();

        /**
         * Query data dari class Image di server parse.com

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.d("Plant Places", "Retrieved " + list.size());
                    for (int i = 0; i < list.size(); i++) {
                        URLS.add(list.get(i).getParseFile("photo").getUrl());
                        LatLng lokasi = new LatLng(Double.parseDouble(list.get(i).getString("latitude")),Double.parseDouble(list.get(i).getString("longitude")));
                        data.add(new MapData(list.get(i).getParseFile("photo").getUrl(),lokasi));
                        Log.d("hasil url", URLS.get(i));
                    }
                    ((MainActivity) context).populateMarker();
                    dialog.dismiss();
                    status = true;
                } else {
                    Log.d("Plant Places", "Error: " + e.getMessage());
                    Toast.makeText(context, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        */
    }
}
