package com.ariefianzy.plantplaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Activity.LoginActivity;
import com.ariefianzy.plantplaces.Activity.MapsImageActivity;
import com.ariefianzy.plantplaces.Activity.MenuActivity;
import com.ariefianzy.plantplaces.Helper.ImeiManager;
import com.ariefianzy.plantplaces.Item.Data;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, LocationListener {

    private Marker myLocation;
    private GoogleMap mMap;
    private MarkerOptions a;
    private Circle circle;
    private int i;
    private List<Marker> marker;
    /**
     * Connect dengan Google Play services untuk mendapatkan lokasi dari gps
     */
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private ImeiManager dataImei;

    private String mLastUpdateTime;
    private boolean gpsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marker = new ArrayList<Marker>();
        mLastUpdateTime = "";
        buildGoogleApiClient();
        setUpMapIfNeeded();
        checkUserSession();
        checkGPS();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(1, 1, 1, "Show Image Location");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == 1){
            startActivity(new Intent(this,MapsImageActivity.class));
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            ParseUser.logOut();
            dialog.dismiss();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    public void menuButtonHandler(View view) {
        if(mCurrentLocation != null && !mLastUpdateTime.equals("")) {
            float[] distance = new float[2];
            for (i = 0; i < Data.dataObject.size(); i++) {
                Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                        Data.dataObject.get(i).latitude, Data.dataObject.get(i).longitude, distance);
                if(distance[0] < circle.getRadius()){
                    break;
                }
            }

            if( distance[0] > circle.getRadius()  ){
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Attention!");
                alertDialog.setIcon(R.drawable.ic_warning);
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Anda berada di luar radius object");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
            } else {

                Bundle data = new Bundle();
                data.putString("location", String.valueOf(mCurrentLocation.getLatitude()) + " , " + String.valueOf(mCurrentLocation.getLongitude()));
                data.putString("time", mLastUpdateTime);
                data.putString("latitude", String.valueOf(mCurrentLocation.getLatitude()));
                data.putString("longitude", String.valueOf(mCurrentLocation.getLongitude()));
                Intent my = new Intent(MainActivity.this, MenuActivity.class);
                my.putExtras(data);
                startActivity(my);
            }
        } else {
            Toast.makeText(MainActivity.this, "Anda tidak mendapatkan lokasi", Toast.LENGTH_SHORT).show();
        }

    }
    public void checkUserSession() {
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            new Data(this,"loadObject");
        } else {
            // Start and intent for the logged out activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        setUpMapIfNeeded();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
                myLocation = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                .title(ParseUser.getCurrentUser().getUsername())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 17));
            }
        }

        startLocationUpdates();
    }

    private void updateMarkerPosition() {
        if (mCurrentLocation != null && mMap != null) {
            myLocation.setPosition(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public void populateMarker() {
        for (i = 0; i < Data.dataObject.size(); i++) {
            marker.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Data.dataObject.get(i).latitude, Data.dataObject.get(i).longitude))
            ));
            marker.get(i).showInfoWindow();
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(Data.dataObject.get(i).latitude,Data.dataObject.get(i).longitude))
                    .radius(500)
                    .fillColor(Color.argb(51,166,209,35))
                    .strokeColor(Color.TRANSPARENT));
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
        updateMarkerPosition();
    }

    @Override
    protected void onRestart() {
        checkGPS();
        if (!gpsStatus) {
            displayPromptForEnablingGPS();
        }
        super.onRestart();
    }

    public void displayPromptForEnablingGPS() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Attention!");
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Anda belum mengaktifkan GPS device");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CLOSE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();
    }
    private void checkGPS(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
