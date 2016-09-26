package com.ariefianzy.plantplaces.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Item.AlbumStorageDirFactory;
import com.ariefianzy.plantplaces.Item.AllData;
import com.ariefianzy.plantplaces.Item.BaseAlbumDirFactory;
import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.Item.ImageCategory;
import com.ariefianzy.plantplaces.R;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuActivity extends Activity {

    private ImageView imgCatA, imgCatB, imgCatC, imgCatD;
    private String urlA, urlB, urlC, urlD;
    private String category;
    private View showall;

    private List<ImageCategory> data = new ArrayList<>();

    //Request code camera
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private String location, latitude, longitude, time;

    private Bundle extras = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        showall = findViewById(R.id.showall);
        Intent my = getIntent();
        extras = my.getExtras();
        location = extras.getString("location");
        latitude = extras.getString("latitude");
        longitude = extras.getString("longitude");
        time = extras.getString("time");

        imgCatA = (ImageView) findViewById(R.id.imgCatA);
        imgCatB = (ImageView) findViewById(R.id.imgCatB);
        imgCatC = (ImageView) findViewById(R.id.imgCatC);
        imgCatD = (ImageView) findViewById(R.id.imgCatD);

        mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ShowImageActivity.class));
            }
        });
        loadData();

        imgCatA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlA == null) {
                    category = "A";
                    dispatchTakePictureIntent();
                } else {
                    category = "A";
                    popUp(urlA);
                }
            }
        });
        imgCatB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlB == null) {
                    category = "B";
                    dispatchTakePictureIntent();
                } else {
                    category = "B";
                    popUp(urlB);
                }
            }
        });
        imgCatC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlC == null) {
                    category = "C";
                    dispatchTakePictureIntent();
                } else {
                    category = "C";
                    popUp(urlC);
                }
            }
        });
        imgCatD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlD == null) {
                    category = "D";
                    dispatchTakePictureIntent();
                } else {
                    category = "D";
                    popUp(urlD);
                }
            }
        });
    }
    public void popUp(String url) {
        DialogFragment newFragment = new MyPopUp(url);
        newFragment.show(getFragmentManager(),"myPopUp");
    }

    public class MyPopUp extends DialogFragment {
        String url;
        public MyPopUp(String url){
            this.url = url;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select")
                    .setItems(R.array.select, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if(which == 0){
                                dispatchTakePictureIntent();
                            }else if(which == 1){
                                viewImage(url);
                            }else{
                                Intent my = new Intent(getApplicationContext(),ImageCategoryActivity.class);
                                my.putExtras(extras);
                                startActivity(my);
                            }
                        }
                    });
            return builder.create();
        }
    }
    private void viewImage(String url){
        Bundle data = new Bundle();
        data.putString("url",url);
        Intent my = new Intent(this,LargePhotoActivity.class);
        my.putExtras(data);
        startActivity(my);
    }
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
                extras.putString("path", mCurrentPhotoPath);
                extras.putString("category", category);
                Intent my = new Intent(MenuActivity.this, PhotoActivity.class);
                my.putExtras(extras);
                startActivity(my);
            }else
                Toast.makeText(MenuActivity.this, "Tidak mendapatkan foto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    private void loadData(){
        data.clear();
        imgCatA.setBackgroundResource(R.drawable.images);
        imgCatB.setBackgroundResource(R.drawable.images);
        imgCatC.setBackgroundResource(R.drawable.images);
        imgCatD.setBackgroundResource(R.drawable.images);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading data...");
        dialog.setCancelable(false);
        dialog.show();

        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("_User");
        innerQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereMatchesQuery("author", innerQuery);
        query.addAscendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                // comments now contains the comments for posts with images.
                dialog.dismiss();
                Data.allData.clear();
                Data.URLS.clear();

                if(e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        data.add(new ImageCategory(list.get(i).getString("category"),list.get(i).getParseFile("photo").getUrl()));
                    }
                    for (int i = list.size()-1; i >= 0; i--) {
                        Data.URLS.add(list.get(i).getParseFile("photo").getUrl());
                        LatLng lokasi = new LatLng(Double.parseDouble(list.get(i).getString("latitude")),Double.parseDouble(list.get(i).getString("longitude")));
                        Data.allData.add(new AllData(list.get(i).getString("category"),list.get(i).getParseFile("photo").getUrl(),lokasi));
                    }
                }else {
                    Toast.makeText(MenuActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                setImage();
            }
        });
    }

    private void setImage(){
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).getCat().equals("A")){
                urlA = data.get(i).getUrl();
                Picasso.with(this)
                        .load(data.get(i).getUrl())
                        .placeholder(R.drawable.placeholder )
                        .fit()
                        .into(imgCatA);
            }else if(data.get(i).getCat().equals("B")){
                urlB = data.get(i).getUrl();
                Picasso.with(this)
                        .load(data.get(i).getUrl())
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .into(imgCatB);
            }else if(data.get(i).getCat().equals("C")) {
                urlC = data.get(i).getUrl();
                Picasso.with(this)
                        .load(data.get(i).getUrl())
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .into(imgCatC);
            }else if(data.get(i).getCat().equals("D")) {
                urlD = data.get(i).getUrl();
                Picasso.with(this)
                        .load(data.get(i).getUrl())
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .into(imgCatD);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        menu.add(1, 1, 1, "Show All Saved Image");
        menu.add(1, 2, 2, "Show Image Location");
        menu.add(1,3,3,"Logout");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == 1){
            Intent my = new Intent(this,ShowImageActivity.class);
            my.putExtras(extras);
            startActivity(my);
        }
        if(id == 2){
            startActivity(new Intent(this,MapsImageActivity.class));
        }
        if(id == 3){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            ParseUser.logOut();
            dialog.dismiss();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.refresh) {
            loadData();
        }

        return super.onOptionsItemSelected(item);
    }
}
