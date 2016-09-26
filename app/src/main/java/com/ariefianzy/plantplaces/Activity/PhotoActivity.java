package com.ariefianzy.plantplaces.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.Item.Image;
import com.ariefianzy.plantplaces.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class PhotoActivity extends Activity {

    //UI Widgets
    private Button mUploadButton;
    private Button mCancelButton;
    private ImageView mPhotoImageView;

    private String location;
    private String time;
    private String path;
    private String latitude;
    private String longitude;
    private String category;
    private ParseFile photoFile;

    Bitmap dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mUploadButton = (Button) findViewById(R.id.btnUpload);
        mCancelButton = (Button) findViewById(R.id.btnCancel);
        mPhotoImageView = (ImageView) findViewById(R.id.imgPhoto);

        Intent my = getIntent();
        Bundle extras = my.getExtras();
        location = extras.getString("location");
        latitude = extras.getString("latitude");
        longitude = extras.getString("longitude");
        category = extras.getString("category");
        time = extras.getString("time");
        path = extras.getString("path");
        setPic();

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = new ProgressDialog(PhotoActivity.this);
                dialog.setMessage("Uploading...");
                dialog.setCancelable(false);
                dialog.show();
                Image image = new Image();
                try {
                    /**
                     * Mengambil foto dari sdcard
                     */
                    dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path)));

                    /**
                     * Mengecilkan kualitas gambar menjadi 80%
                     * Kemudian menjadikan gambar ke byte
                     */
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    dest.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    byte[] bitmapdata = stream.toByteArray();

                    // Save the scaled image to Parse
                    photoFile = new ParseFile("photo.jpg", bitmapdata);

                    // Upload the image into Parse Cloud
                    photoFile.saveInBackground();

                    image.setAuthor(ParseUser.getCurrentUser());
                    image.setLatitude(latitude);
                    image.setCategory(category);
                    image.setLongitude(longitude);
                    image.setPhotoFile(photoFile);

                    // Create the class and the columns in parse
                    image.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            dialog.dismiss();
                            Toast.makeText(PhotoActivity.this, "Berhasil upload gambar", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void setPic() {
		/* Get the size of the ImageView */
        int targetW = mPhotoImageView.getWidth();
        int targetH = mPhotoImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        addTextImage(bitmap);
    }

    private void addTextImage(Bitmap src){
        /**
         * Proses penambahan lokasi dan waktu pada foto kemudian di simpan ke gallery
         */
        dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        String text1 = "Latitude : "+latitude;
        String text2 = "Longitude : "+longitude;
        String text3 = "Time : "+time;

        Canvas cs = new Canvas(dest);
        cs.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(100);
        paint.setColor(Color.BLUE);
        cs.drawBitmap(src, 0f, 0f, null);
        float y = src.getHeight();
        cs.drawText(text1, 10, y - 225, paint);
        cs.drawText(text2, 10, y - 125, paint);
        cs.drawText(text3, 10, y - 25, paint);

        /**
         * Menampilkan foto di imageView
         */
        mPhotoImageView.setImageBitmap(dest);
    }
}
