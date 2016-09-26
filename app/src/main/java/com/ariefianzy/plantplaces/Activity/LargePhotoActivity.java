package com.ariefianzy.plantplaces.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.ariefianzy.plantplaces.Helper.Touch;
import com.ariefianzy.plantplaces.Helper.TouchImageView;
import com.ariefianzy.plantplaces.R;
import com.squareup.picasso.Picasso;

public class LargePhotoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_photo);

        TouchImageView largePhotoImageView = (TouchImageView) findViewById(R.id.imgLargePhoto);
        String url;

        /**
         * Mengambil data dari bundle yang dikirim dari intent sebelumnya
         */
        Intent my = getIntent();
        Bundle data = my.getExtras();
        url = data.getString("url","coba");

        /**
         * Set gambar
         */
        Picasso.with(this) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .fit() //
                .into(largePhotoImageView);
    }
}
