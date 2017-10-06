package com.yelona;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.bumptech.glide.GenericTranscodeRequest;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.NetConnectivity;
import com.yelona.helper.ServiceHandler;
import com.yelona.session.SessionManager;


import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;

public class VerificationActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    private String title = "VERIFICATION PROCESS";
    private TextView txtcode;
    private TextView txtverify;
    private TextView txt;
    private TextView txtresend;
    private TextView txterror;
    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails;
    private int counter;
    private Timer timer;

    dbhandler dh;
    SQLiteDatabase sd;
    private String TAG = VerificationActivity.class.getSimpleName();
    private SpotsDialog spotDialog;
    private String response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        //setContentView(R.layout.demo_layout);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);


        dh = new dbhandler(getApplicationContext());
        sd = dh.getReadableDatabase();
        sd = dh.getWritableDatabase();
        txtcode = (TextView) findViewById(R.id.txtcode);
        txtverify = (TextView) findViewById(R.id.txtverify);
        txt = (TextView) findViewById(R.id.textView2);
        txtresend = (TextView) findViewById(R.id.txtresend);
        txterror = (TextView) findViewById(R.id.txterror);

        txtcode.setClickable(false);
        txtcode.setDuplicateParentStateEnabled(false);
        // txtcode.setEnabled(false);

        sessionmanager = new SessionManager(context);

        userDetails = new HashMap<String, String>();

        userDetails = sessionmanager.getSessionDetails();

        /*counter = Integer.parseInt(userDetails
                .get(SessionManager.KEY_VERIFICATION_COUNTER));*/


        setTitle("Verify Your Mobile Number");


        if (NetConnectivity.isOnline(context)) {
            timer = new Timer();
            TimerTask hourlyTask = new TimerTask() {
                @Override
                public void run() {
                    // your code here...

                    userDetails = sessionmanager.getSessionDetails();

                    try {
                        userDetails.get(SessionManager.KEY_CODE);

                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    if (userDetails.get(SessionManager.KEY_RECEIVECODE)
                            .length() == 4) {
                        if (userDetails.get(SessionManager.KEY_RECEIVECODE)
                                .equals(userDetails
                                        .get(SessionManager.KEY_CODE))) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        txtcode.setText(""
                                                + userDetails
                                                .get(SessionManager.KEY_RECEIVECODE));
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();

                                    }
                                    // serviceP.asmx/SetStudentVerificationStatusUpdate?type=varemp&empid=string&mobileno=string&status=string&clientid=string&branch=string


                                    try {
                                        timer.cancel();
                                        timer.purge();

                                        timer = null;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });

							/*
                             * if(timer != null) { timer.cancel(); timer = null;
							 * }
							 */

                        }
                    }

                }
            };

            // schedule the task to run starting now and then every hour...
            timer.schedule(hourlyTask, 0l, 1000 * 5); // 1000*10*60 every 10
            // minut
        }

        txtresend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // String msgurl =
                // AllKeys.TAG_WEBSITE+"?action=mobverify&userid="+
                // userDetails.get(SessionManager.KEY_UID) +"&code="+ Vcode +"";

                //new sendSmsToUser().execute();

                if (userDetails.get(SessionManager.KEY_USER_MOBILE).toString().length() == 10) {

//                    sendSMSToUser();
                    new sendSmsToUserUser().execute();

                } else {
                    showDialogAsAskMobileno();
                }

                txterror.setVisibility(View.GONE);


            }
        });

        if (NetConnectivity.isOnline(context)) {

            //new sendSmsToUser().execute();
            if (userDetails.get(SessionManager.KEY_USER_MOBILE).toString().length() == 10) {

                //sendSMSToUser();
                new sendSmsToUserUser().execute();
            } else {
                showDialogAsAskMobileno();
            }
        } else {
            Toast.makeText(context, "Please enable internet",
                    Toast.LENGTH_SHORT).show();
        }

		/*txt.setText("A verification code is being sent to your mobile number "
                + userDetails.get(SessionManager.KEY_MOBILE)
				+ ". To verify your mobile number, please enter the code once it  arrives.");
		*/


        if (userDetails.get(SessionManager.KEY_USER_MOBILE).equals("")) {
            txt.setText("+91 97236 13143");
        } else {
            txt.setText("+91 " + userDetails.get(SessionManager.KEY_USER_MOBILE));

        }


        txtverify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String currentcode = txtcode.getText().toString();

                // CheckVerification(currentcode);

                if (userDetails.get(SessionManager.KEY_CODE)
                        .equals(currentcode)) {

                    // serviceP.asmx/SetStudentVerificationStatusUpdate?type=varemp&empid=string&mobileno=string&status=string&clientid=string&branch=string


                    try {
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                //  txtcode.setText(userDetails.get(SessionManager.KEY_RECEIVECODE));
                            }
                        });
                        timer.cancel();
                        timer.purge();

                        timer = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    String url_update_status = AllKeys.WEBSITE + "updateVerificationStatus/" + userDetails.get(SessionManager.KEY_USERID) + "/1";
                    Log.d(TAG, "URL update verification_status : " + url_update_status);
                    StringRequest str_sendsms = new StringRequest(Request.Method.GET, url_update_status, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.contains("success")) {
                                Intent ii = new Intent(context, NewDashBoardActivity.class);
                                startActivity(ii);
                                finish();

                                sessionmanager.CheckSMSVerificationActivity("",
                                        "1");

                            } else {
                                Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                            }


                            Log.d(TAG, "Send SMS Response :" + response);
                            hideDialog();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            try {

                                hideDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();


                            Log.d(TAG, "Error Response :" + error.getMessage());
                        }
                    });
                    MyApplication.getInstance().addToRequestQueue(str_sendsms);


                } else {
                    Toast.makeText(context, "Invalid code", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });

    }

    public void showDialogAsAskMobileno() {
        final Dialog dialog = new Dialog(context);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Mobile No");
        dialog.setContentView(R.layout.custom_dialog_mobile);


        try {
            LinearLayout llmain = (LinearLayout) dialog.findViewById(R.id.llmain);
            llmain.setVisibility(LinearLayout.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
            animation.setDuration(1000);
            llmain.setAnimation(animation);
            llmain.animate();
            animation.start();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        final EditText edtMobile = (EditText) dialog.findViewById(R.id.edtMobile);
        final TextInputLayout edtmobileWrapper = (TextInputLayout) dialog.findViewById(R.id.edtmobileWrapper);
        final Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMobile.getText().toString().equals("")) {
                    edtmobileWrapper.setError("Enter mobile no");
                    edtmobileWrapper.setErrorEnabled(true);

                } else {
                    if (edtMobile.getText().toString().length() != 10) {

                        edtmobileWrapper.setError("Invalid mobile no");
                        edtmobileWrapper.setErrorEnabled(true);
                    } else {
                        edtmobileWrapper.setErrorEnabled(false);
                        String url_update_status = AllKeys.WEBSITE + "updateUserMobileno/" + userDetails.get(SessionManager.KEY_USERID) + "/" + edtMobile.getText().toString() + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "";
                        Log.d(TAG, "URL update verification_status : " + url_update_status);
                        StringRequest str_sendsms = new StringRequest(Request.Method.GET, url_update_status, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if (response.contains("success")) {

                                    sessionmanager.setUserMobile(edtMobile.getText().toString());
                                    userDetails = sessionmanager.getSessionDetails();
                                    if (userDetails.get(SessionManager.KEY_USER_MOBILE).equals("")) {
                                        txt.setText("+91 97236 13143");
                                    } else {
                                        txt.setText("+91 " + userDetails.get(SessionManager.KEY_USER_MOBILE));

                                    }

                                    //sendSMSToUser();
                                    new sendSmsToUserUser().execute();

                                    dialog.cancel();
                                    dialog.dismiss();


                                } else if (response.contains("Mobile already exist")) {
                                    edtmobileWrapper.setError(response.toString());
                                    edtmobileWrapper.setErrorEnabled(true);
                                }


                                Log.d(TAG, "Send SMS Response :" + response);
                                hideDialog();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                try {

                                    hideDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();


                                Log.d(TAG, "Error Response :" + error.getMessage());
                            }
                        });
                        MyApplication.getInstance().addToRequestQueue(str_sendsms);

                    }


                }


            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setLayout(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        dialog.show();


    }

    public void showDialogAsAskMobileno1() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_mobile, null);
        dialogBuilder.setView(dialogView);

        final EditText edtMobile = (EditText) dialogView.findViewById(R.id.edtMobile);
        final TextInputLayout edtmobileWrapper = (TextInputLayout) dialogView.findViewById(R.id.edtmobileWrapper);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Mobile No");
        //dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if (edtMobile.getText().toString().equals("")) {
                    edtmobileWrapper.setError("Enter mobile no");
                    edtmobileWrapper.setErrorEnabled(true);

                } else {
                    if (edtMobile.getText().toString().length() != 10) {

                        edtmobileWrapper.setError("Invalid mobile no");
                        edtmobileWrapper.setErrorEnabled(true);
                    } else {
                        edtmobileWrapper.setErrorEnabled(false);
                        String url_update_status = AllKeys.WEBSITE + "updateMobileno/" + userDetails.get(SessionManager.KEY_USERID) + "/" + edtMobile.getText().toString() + "";
                        Log.d(TAG, "URL update verification_status : " + url_update_status);
                        StringRequest str_sendsms = new StringRequest(Request.Method.GET, url_update_status, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if (response.contains("success")) {

                                    sessionmanager.setUserMobile(edtMobile.getText().toString());
                                    userDetails = sessionmanager.getSessionDetails();
                                    if (userDetails.get(SessionManager.KEY_USER_MOBILE).equals("")) {
                                        txt.setText("+91 97236 13143");
                                    } else {
                                        txt.setText("+91 " + userDetails.get(SessionManager.KEY_USER_MOBILE));

                                    }
                                    //sendSMSToUser();
                                    new sendSmsToUserUser().execute();

                                    dialog.cancel();
                                    dialog.dismiss();


                                } else if (response.contains("Mobile already exist")) {
                                    edtmobileWrapper.setError(response.toString());
                                    edtmobileWrapper.setErrorEnabled(true);
                                }


                                Log.d(TAG, "Send SMS Response :" + response);
                                hideDialog();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                try {

                                    hideDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();


                                Log.d(TAG, "Error Response :" + error.getMessage());
                            }
                        });
                        MyApplication.getInstance().addToRequestQueue(str_sendsms);

                    }


                }


            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                finish();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);


        b.show();
    }

	/*
     * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.verification, menu); return true; }
	 */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
       /* if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Toast.makeText(getApplicationContext(), "Please Complete Verification",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
        // intent = new Intent(getApplicationContext(),RegisterActivity.class);


        startActivity(intent);
        finish();
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }


    public class sendSmsToUserUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String sendsms = "";

            if (userDetails.get(SessionManager.KEY_CODE).equals("0")) {
                Random r = new Random();
                int code = r.nextInt(9999 - 1000) + 1000;
                Log.d(TAG, "Verification Code : " + code);
                sendsms = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + code + "/otp";


                Log.d(TAG, "URL sendSMS : " + sendsms);
                sessionmanager.createUserSendSmsUrl("" + code, sendsms);
            } else {

                userDetails = sessionmanager.getSessionDetails();
                sendsms = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + userDetails.get(SessionManager.KEY_CODE) + "/otp";
                //sendsms = userDetails.get(SessionManager.KEY_SMSURL);
                Log.d(TAG, "URL sendSMS : " + sendsms);
           /* if (userDetails.get(SessionManager.KEY_SMSURL).contains("")) {
                Random r = new Random();
                int code = r.nextInt(9999 - 1000) + 1000;
                Log.d(TAG, "Verification Code : " + code);
                sendsms = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + code;
                Log.d(TAG, "URL sendSMS : " + sendsms);
                sessionmanager.createUserSendSmsUrl("" + code, sendsms);

            }*/


            }

            Log.d(TAG, "sendsms res : " + sendsms);


            ServiceHandler sh = new ServiceHandler();
            response = sh.makeServiceCall(sendsms, ServiceHandler.GET);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            Log.d(TAG, "Send SMS Response :" + response);
            if (!response.contains("sent successfully")) {
                Toast.makeText(context, "Error in message sending,try again...", Toast.LENGTH_SHORT).show();

            }


            hideDialog();
        }
    }

    /**
     * Send SMS To User
     */

    public void sendSMSToUser() {


        String sendsms = "";

        if (userDetails.get(SessionManager.KEY_CODE).equals("0")) {
            Random r = new Random();
            int code = r.nextInt(9999 - 1000) + 1000;
            Log.d(TAG, "Verification Code : " + code);
            sendsms = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + code + "/otp";


            Log.d(TAG, "URL sendSMS : " + sendsms);
            sessionmanager.createUserSendSmsUrl("" + code, sendsms);
        } else {

            userDetails = sessionmanager.getSessionDetails();
            sendsms = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + userDetails.get(SessionManager.KEY_CODE) + "/otp";
            //sendsms = userDetails.get(SessionManager.KEY_SMSURL);
            Log.d(TAG, "URL sendSMS : " + sendsms);
           /* if (userDetails.get(SessionManager.KEY_SMSURL).contains("")) {
                Random r = new Random();
                int code = r.nextInt(9999 - 1000) + 1000;
                Log.d(TAG, "Verification Code : " + code);
                sendsms = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + code;
                Log.d(TAG, "URL sendSMS : " + sendsms);
                sessionmanager.createUserSendSmsUrl("" + code, sendsms);

            }*/


        }

        Log.d(TAG, "sendsms res : " + sendsms);

        showDialog();


        StringRequest str_sendsms = new StringRequest(Request.Method.GET, sendsms, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Send SMS Response :" + response);
                if (!response.contains("sent successfully")) {
                    Toast.makeText(context, "Error in message sending,try again...", Toast.LENGTH_SHORT).show();

                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {

                    hideDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();


                Log.d(TAG, "Error Response :" + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_sendsms);


    }

    private void showDialog() {
        if (!spotDialog.isShowing()) {
            spotDialog.show();

        }
    }

    private void hideDialog() {
        if (spotDialog.isShowing()) {
            spotDialog.dismiss();


        }
    }
    //Complete Send sms to user


}
