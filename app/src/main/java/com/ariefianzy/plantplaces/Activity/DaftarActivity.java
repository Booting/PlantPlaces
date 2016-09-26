package com.ariefianzy.plantplaces.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ariefianzy.plantplaces.Helper.ImeiManager;
import com.ariefianzy.plantplaces.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class DaftarActivity extends Activity {
    //UI Widgets
    private EditText mEmailEditText;
    private EditText mUsernameEditText;
    private EditText mPhoneEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mDaftarButton;

    private TelephonyManager mngr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(DaftarActivity.this).create();
        alertDialog.setTitle("Attention!");
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Satu device hanya dapat mendaftar satu akun !");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "EXIT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("imei", mngr.getDeviceId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listImei, ParseException e) {
                dialog.dismiss();
                if (listImei.size() != 0) {
                    alertDialog.show();
                }
            }
        });

        // Locate the UI widgets.
        mEmailEditText = (EditText) findViewById(R.id.txtEmail);
        mUsernameEditText = (EditText) findViewById(R.id.txtUsername);
        mPasswordEditText = (EditText) findViewById(R.id.txtPassword);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.txtConfirmPassword);
        mPhoneEditText = (EditText) findViewById(R.id.txtPhone);
        mDaftarButton = (Button) findViewById(R.id.btnDaftar);

        /**
         * Ketika tombol Daftar di klik
         */
        mDaftarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEmailEditText.getText().toString().trim().equals("")){
                    mEmailEditText.setError( "Email harus diisi!" );
                }if(mUsernameEditText.getText().toString().trim().equals("")) {
                    mUsernameEditText.setError( "Username harus diisi!" );
                }if(mPhoneEditText.getText().toString().trim().equals("")) {
                    mPhoneEditText.setError( "Phone harus diisi!" );
                }if(mPasswordEditText.getText().toString().trim().equals("")) {
                    mPasswordEditText.setError( "Password harus diisi!" );
                }if(!mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString())) {
                    mConfirmPasswordEditText.setError( "Confirmation Password harus sama!" );
                }else{
                    // Set up a progress dialog
                    final ProgressDialog dialog = new ProgressDialog(DaftarActivity.this);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    String email = mEmailEditText.getText().toString();
                    String username = mUsernameEditText.getText().toString();
                    String password = mPasswordEditText.getText().toString();
                    String phone = mPhoneEditText.getText().toString();

                    /**
                     * Upload data pendaftaran ke server parse
                     */
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.put("phone", phone);
                    user.put("imei",mngr.getDeviceId());
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                dialog.dismiss();
                                Toast.makeText(DaftarActivity.this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                dialog.dismiss();
                                Toast.makeText(DaftarActivity.this, "Gagal mendaftar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
}
