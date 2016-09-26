package com.ariefianzy.plantplaces.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Helper.ImeiManager;
import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.Item.AlbumStorageDirFactory;
import com.ariefianzy.plantplaces.Item.BaseAlbumDirFactory;
import com.ariefianzy.plantplaces.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /**
     * Connect dengan Google Play services untuk mendapatkan lokasi dari gps
     */
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    //UI Widget
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mLastUpdateTextView;
    private Button mTakePhotoButton;
    private Button mViewImageButton;
    private Button mViewMapButton;

    private String mLastUpdateTime;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    //Request code camera
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private boolean data = false;
    ImeiManager dataImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Locate the UI widgets.
        mLatitudeTextView = (TextView) findViewById(R.id.txtLatitude);
        mLongitudeTextView = (TextView) findViewById(R.id.txtLongitude);
        mLastUpdateTextView = (TextView) findViewById(R.id.txtLastUpdate);
        mTakePhotoButton = (Button) findViewById(R.id.btnPhoto);
        mViewImageButton = (Button) findViewById(R.id.btnViewImage);
        mViewMapButton = (Button) findViewById(R.id.btnViewMap);

        mLastUpdateTime = "";

        mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        buildGoogleApiClient();

        checkIMEI();

        /**
         * Ketika tombol Take Photo di click
         */
        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentLocation != null && !mLastUpdateTime.equals("")){
                    dispatchTakePictureIntent();
                }else{
                    Toast.makeText(MainActivity2.this, "Anda tidak mendapatkan lokasi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this, ShowImageActivity.class));
            }
        });

        mViewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this, MapsImageActivity.class));
            }
        });
    }

    private void checkIMEI(){
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("IMEI",mngr.getDeviceId());
        dataImei = new ImeiManager(getApplicationContext());
        dataImei.checkImei();
        Log.d("data imei", "ini "+dataImei.isLoggedIn());
        if(dataImei.isLoggedIn()) {
            checkUserSession();
        } else {
            new Data(mngr.getDeviceId(), this);
            checkUserSession();
        }
    }

    private void checkUserSession(){
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            new Data(this);
        } else {
            // Start and intent for the logged out activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void updateUI(){
        if (mCurrentLocation != null) {
            mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
            mLastUpdateTextView.setText(mLastUpdateTime);
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
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final ProgressDialog dialog = new ProgressDialog(MainActivity2.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            dataImei.logout();
            ParseUser.logOut();
            dialog.dismiss();
            Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
        updateUI();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
            updateUI();
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    /*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 100);
        }else{
            Toast.makeText(this, "Tidak dapat menjalankan kamera !", Toast.LENGTH_SHORT).show();
        }
    }
    */
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /* Nama album foto */
    private String getAlbumName() {
        return getString(R.string.nama_album);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    /**
     * Menerima hasil dari kamera
     * @param requestCode = nomer request intent
     * @param resultCode = apakah OK atau tidak
     * @param data = hasil yang didapat
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //handleBigCameraPhoto();
                galleryAddPic();
                Bundle extras = new Bundle();
                extras.putString("location",String.valueOf(mCurrentLocation.getLatitude())+" , "+String.valueOf(mCurrentLocation.getLongitude()));
                extras.putString("time",mLastUpdateTime);
                extras.putString("path", mCurrentPhotoPath);
                extras.putString("latitude", String.valueOf(mCurrentLocation.getLatitude()));
                extras.putString("longitude", String.valueOf(mCurrentLocation.getLongitude()));
                Intent my = new Intent(MainActivity2.this, PhotoActivity.class);
                my.putExtras(extras);
                startActivity(my);
            }else
                Toast.makeText(MainActivity2.this, "Tidak mendapatkan foto", Toast.LENGTH_SHORT).show();
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("Data", true);
        super.onSaveInstanceState(savedInstanceState);
    }
}
