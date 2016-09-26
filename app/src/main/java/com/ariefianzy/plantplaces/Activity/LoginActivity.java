package com.ariefianzy.plantplaces.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Helper.ImeiManager;
import com.ariefianzy.plantplaces.Item.Data;
import com.ariefianzy.plantplaces.MainActivity;
import com.ariefianzy.plantplaces.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {
    //UI Widgets
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private Button mDaftarButton;
    private ImeiManager dataImei;
    private TextView mRequestPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Locate the UI widgets.
        mUsernameEditText = (EditText) findViewById(R.id.txtUsername);
        mPasswordEditText = (EditText) findViewById(R.id.txtPassword);
        mLoginButton = (Button) findViewById(R.id.btnLogin);
        mDaftarButton = (Button) findViewById(R.id.btnDaftar);
        mRequestPasswordTextView = (TextView) findViewById(R.id.txtReqPass);

        checkIMEI();
        mRequestPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(),ForgotPasswordActivity.class));
            }
        });
        mDaftarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(),DaftarActivity.class));
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set up a progress dialog
                final String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                if (mUsernameEditText.getText().toString().trim().equals("")) {
                    mUsernameEditText.setError("Username harus diisi!");
                }
                if (mPasswordEditText.getText().toString().trim().equals("")) {
                    mPasswordEditText.setError("Password harus diisi!");
                } else {
                    final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();
                    /**
                     * Cek apakah login yang dimasukkan benar
                     */
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                // Hooray! The user is logged in.
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Anda berhasil login", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            } else {
                                dialog.dismiss();
                                // Signup failed. Look at the ParseException to see what happened.
                                Toast.makeText(LoginActivity.this, "Gagal login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void checkIMEI(){
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("IMEI", mngr.getDeviceId());
        dataImei = new ImeiManager(getApplicationContext());
        dataImei.checkImei();
        Log.d("data imei", "ini " + dataImei.isLoggedIn());
        if(!dataImei.isLoggedIn()) {
            new Data(mngr.getDeviceId(), this);
        }
    }
}
