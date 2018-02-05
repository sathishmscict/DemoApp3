package com.yelona;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.ServiceTestCase;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yelona.animation.ShakeAnimation;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.NetConnectivity;
import com.yelona.helper.ServiceHandler;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class ForgotPassword extends AppCompatActivity {

    private Button btnReset;
    private EditText edtEmail;
    private TextInputLayout edtEmailWrapper;
    private String TAG = ForgotPassword.class.getSimpleName();
    private Context context = this;
    private SpotsDialog pDialog;
    private CoordinatorLayout coordinateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);


        setTitle("Forgot Password");
        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(back_button);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnReset = (Button) findViewById(R.id.btnReset);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtEmailWrapper = (TextInputLayout) findViewById(R.id.edtEmailWrapper);

        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtEmail.getText().toString().equals("")) {

                    edtEmailWrapper.setError("Enter email");
                    edtEmailWrapper.setErrorEnabled(true);
                } else {
                    edtEmailWrapper.setErrorEnabled(false);

                    if (AllKeys.checkEmail(edtEmail.getText().toString())) {
                        edtEmailWrapper.setErrorEnabled(false);

                        if (NetConnectivity.isOnline(context)) {
                            new checkEmialAndSendResetLinkToUser().execute();
                        } else {
                            Snackbar.make(coordinateLayout, getString(R.string.no_network), Snackbar.LENGTH_SHORT).show();

                        }

                    } else {

                        edtEmailWrapper.setErrorEnabled(true);
                        edtEmailWrapper.setError("Invalid Email");

                    }


                }
            }
        });


    }

    public void showDialog() {


        pDialog = new SpotsDialog(context);
        pDialog.setCancelable(false);
        if (!pDialog.isShowing()) {

            pDialog.show();
        }


    }

    public void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();

        }
    }


    private void shakeInit(EditText tt) {


        ShakeAnimation.create().with(tt)
                .setDuration(2000)
                .setRepeatMode(ShakeAnimation.RESTART)
                .setRepeatCount(1)
                .start();

        /*.setRepeatCount(ShakeAnimation.INFINITE)*/
    }


    public class checkEmialAndSendResetLinkToUser extends AsyncTask<Void, Void, Void> {

        private String response = "";

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            showDialog();
        }


        //Firdt Check Goal Master table whether Records exist or mnot
        // ,If records exist themn check subtask master,
        // If records exist then check time master field,
        //


        @Override
        protected Void doInBackground(Void... params) {


            String url_checkDetails = AllKeys.WEBSITE + "InsertForgotPassword/" + dbhandler.convertEncodedString(edtEmail.getText().toString()) + "";
            Log.d(TAG, "URL CheckUserDetails :" + url_checkDetails);

            ServiceHandler sh = new ServiceHandler();
            response = sh.makeServiceCall(url_checkDetails, ServiceHandler.GET);
            Log.d(TAG , "URL CheckUserDetails Response : "+response);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if (response.contains("sent successfully")) {


                Snackbar.make(coordinateLayout, "Reset link send to your registered mobile", Snackbar.LENGTH_LONG).show();


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPassword.this);
                alertDialogBuilder.setMessage("Reset link send to your registered mobile");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                edtEmailWrapper.setErrorEnabled(false);


            }
            else if(response.contains("failed to open stream"))
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPassword.this);
                alertDialogBuilder.setTitle("Server error");
                alertDialogBuilder.setMessage("SMS balance validity has been expired...");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                arg0.cancel();
                                arg0.dismiss();

                                /*Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
            else {

                Snackbar.make(coordinateLayout, "Email id does not exist", Snackbar.LENGTH_LONG).show();
                edtEmailWrapper.setError("Email id does not exist");
                edtEmailWrapper.setErrorEnabled(true);

                shakeInit(edtEmail);
            }
            hideDialog();

        }


    }

    private void checkEmailAnsSendResetLink() {
        showDialog();


        String url_checkDetails = AllKeys.WEBSITE + "InsertForgotPassword/" + dbhandler.convertEncodedString(edtEmail.getText().toString()) + "";
        Log.d(TAG, "URL CheckUserDetails :" + url_checkDetails);
        StringRequest str_checkData = new StringRequest(Request.Method.GET, url_checkDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.contains("sent successfully")) {


                    Snackbar.make(coordinateLayout, "Reset link send to your registered mobile", Snackbar.LENGTH_LONG).show();


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPassword.this);
                    alertDialogBuilder.setMessage("Reset link send to your registered mobile");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    edtEmailWrapper.setErrorEnabled(false);


                } else {

                    Snackbar.make(coordinateLayout, "Email id does not exist", Snackbar.LENGTH_LONG).show();
                    edtEmailWrapper.setError("Email id does not exist");
                    edtEmailWrapper.setErrorEnabled(true);

                    shakeInit(edtEmail);
                }
                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();

                Log.d(TAG, "Forgot Call Response : " + error.getMessage());
                Snackbar.make(coordinateLayout, "Trg again..", Snackbar.LENGTH_SHORT).show();


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_checkData);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {


            Intent i = new Intent(context, LoginActivity.class);
            //i.putExtra("ActivityName",ACTIVITYNAME);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {
            Intent i = new Intent(context, LoginActivity.class);
            //i.putExtra("ActivityName",ACTIVITYNAME);
            startActivity(i);
            finish();
        } catch (Exception e) {

            Log.d(TAG, "Error in Converting Class : " + e.getMessage());
            e.printStackTrace();
        }


    }

}
