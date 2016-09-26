package com.ariefianzy.plantplaces.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ariefianzy.plantplaces.Helper.SampleGridViewAdapter;
import com.ariefianzy.plantplaces.Helper.SampleScrollListener;
import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.R;
import com.parse.ParseUser;

public class ShowImageActivity extends Activity {
    private GridView gv;
    private SampleGridViewAdapter adapter;
    private Bundle extras = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        /**
         * Membuat GridView untuk menampilkan gambar
         */
        Intent my = getIntent();
        extras = my.getExtras();
        createAdapter();

        /**
         * Jika elemen dari gridview di click
         */
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(ShowImageActivity.this, SliderImageActivity.class);
                Bundle data = new Bundle();
                data.putString("url", Data.allData.get(i).getPhoto());
                data.putInt("position", i);
                myIntent.putExtras(data);
                startActivity(myIntent);
            }
        });
    }
    public void createAdapter(){
        adapter = new SampleGridViewAdapter(this);
        gv = (GridView) findViewById(R.id.gridview);
        gv.setAdapter(adapter);
        gv.setOnScrollListener(new SampleScrollListener(this));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_image, menu);
        menu.add(1,1,1,"Menu");
        menu.add(1,2,2,"Show Image Location");
        menu.add(1,3,3,"Image Category");
        menu.add(1,4,4,"Log Out");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == 1){
            onBackPressed();
        }
        if(id == 2){
            startActivity(new Intent(this,MapsImageActivity.class));
        }
        if(id == 3){
            Intent my = new Intent(this,ImageCategoryActivity.class);
            my.putExtras(extras);
            startActivity(my);
        }
        if(id == 4){
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
            new Data().loadData(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
