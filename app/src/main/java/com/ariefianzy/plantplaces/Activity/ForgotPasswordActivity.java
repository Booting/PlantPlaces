package com.ariefianzy.plantplaces.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Item.AllData;
import com.ariefianzy.plantplaces.R;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ForgotPasswordActivity extends Activity {
    private Button mSubmitButton;
    private EditText mEmailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mSubmitButton = (Button) findViewById(R.id.btnSubmit);
        mEmailEditText = (EditText) findViewById(R.id.txtEmail);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailEditText.getText().toString();
                if (mEmailEditText.getText().toString().trim().equals("")) {
                    mEmailEditText.setError("Email harus diisi!");
                }else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                    query.whereEqualTo("email", email);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e != null) {
                                req(email);
                            } else {
                                displayAlert();
                            }
                        }
                    });
                }
            }
        });

    }
    public void displayAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Attention!");
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Email anda tidak terdaftar di server");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();
    }
    public void req(String email){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Requesting...");
        dialog.setCancelable(false);
        dialog.show();
        ParseObject req = new ParseObject("RequestPassword");
        req.put("email", email);
        req.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(ForgotPasswordActivity.this, "Berhasil request", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
