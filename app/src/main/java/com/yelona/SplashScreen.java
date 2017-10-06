package com.yelona;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.crash.FirebaseCrash;

import com.mixpanel.android.java_websocket.util.Base64;
import com.yelona.animation.FlipAnimation;
import com.yelona.animation.PulseAnimation;
import com.yelona.animation.RotateAnimation;
import com.yelona.animation.ShakeAnimation;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.NetConnectivity;
import com.yelona.helper.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLEngineResult;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

    private String TAG = SplashScreen.class.getSimpleName();
    private dbhandler db;
    private SQLiteDatabase sd;
    private Context context = this;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);


            }
            return false;
        }
    };
    private ImageView flipImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


     /*   try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        flipImage = (ImageView) findViewById(R.id.imageView);

        // final ImageView flipImage = (ImageView)findViewById(R.id.flip);


        //Animation Types
        // initRotation();
       // initFlip();
        initPulse();
        //initShake();


        if(NetConnectivity.isOnline(context))
        {
            new GetDataFromServer().execute();
        }
        else
        {
            Intent intent = new Intent(context,NewDashBoardActivity.class);
            //intent = new Intent(context,ItemDisplayActivity.class);
            startActivity(intent);

            Toast.makeText(context, ""+getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
        }





       /* new Handler().postDelayed(new Runnable() {

            *//*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             *//*

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, NewDashBoardActivity.class);
                startActivity(i);
                finish();

                // close this activity
                finish();
            }
        }, AUTO_HIDE_DELAY_MILLIS);*/


        //getAllCategoryDetailsFromServer();

        //sendCheckoutDetailsToServerUsing_POST();

    }


    private void initShake() {

        //final ImageView shakeImage = (ImageView)findViewById(R.id.shake);

        ShakeAnimation.create().with(flipImage)
                .setDuration(2000)
                .setRepeatMode(ShakeAnimation.RESTART)
                .setRepeatCount(ShakeAnimation.INFINITE)
                .start();
    }

    private void initRotation() {


        RotateAnimation.create().with(flipImage)
                .setRepeatCount(RotateAnimation.INFINITE)
                .setRepeatMode(RotateAnimation.RESTART)
                .setDuration(2000)
                .start();
    }

    private void initPulse() {

        PulseAnimation.create().with(flipImage)
                .setDuration(600)
                .setRepeatCount(PulseAnimation.INFINITE)
                .setRepeatMode(PulseAnimation.REVERSE)
                .start();


    }

    private void initFlip() {


        FlipAnimation.create().with(flipImage)
                .setDuration(3600)
                .setRepeatCount(FlipAnimation.INFINITE)
                .start();

    }

    private void sendCheckoutDetailsToServerUsing_POST() {


        String url_checkout_order = AllKeys.WEBSITE + "CheckService";


        Log.d(TAG, "URL Checkout Order : " + url_checkout_order);

        StringRequest str_checkout_order = new StringRequest(Request.Method.POST, url_checkout_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "CheckoutOrder Response : " + response.toString());


                //  Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                if (response.contains("success")) {

                    Intent ii = new Intent(context, OrderSuccessfullActivity.class);
                    startActivity(ii);

                    finish();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {

                } else {
                    sendCheckoutDetailsToServerUsing_POST();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("first_name", "sagar");
                params.put("last_name", "gadde");


                String json = "";
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("payment_hash", "ee6368f6f7ef92c8729227e549ab269a7eade5953e795f19f8a4a6ec85ec58ea41d65799d7757de33b32dcfd4064674f2766ed3e32caf3f5391aed54879a93ec");
                    jsonObject.accumulate("get_merchant_ibibo_codes_hash", "a7198675314b3ce0d202fa7a8a9f6ce4639a04a4e5d3bbfab237efb50e4d6c8a595c39136d18a143088cad87b91f8eb50f0fbc66d07b7f3ae5bb74688267cdc3");
                    jsonObject.accumulate("vas_for_mobile_sdk_hash", "24dad4d5026e7e275f96d7a896cb356d93b2ea615d339ffab13b511294057920aba688d1f31992c178b542e3a2a8517fb8ccc3e1a2a75376c357d43819039d42");
                    jsonObject.accumulate("emi_hash", "e60ad85162b4af5e7bd559f066fb1199f1a7fae252689259bcaf168fbf36b1358d51b468ea8b713e4687740a2148221e13780be227cbc71c76bbd29ede710cdf");
                    jsonObject.accumulate("payment_related_details_for_mobile_sdk_hash", "2e2a16df8b60135cb377f6dd70c9351dd352a3d3a88349eb3985a5ec267241e1b4fba067805f53b28abab24a3e5c730c12c506f7757562f31140cbe544fd1239");
                    jsonObject.accumulate("verify_payment_hash", "2e2a16df8b60135cb377f6dd70c9351dd352a3d3a88349eb3985a5ec267241e1b4fba067805f53b28abab24a3e5c730c12c506f7757562f31140cbe544fd1239");
                    jsonObject.accumulate("delete_user_card_hash", "2e2a16df8b60135cb377f6dd70c9351dd352a3d3a88349eb3985a5ec267241e1b4fba067805f53b28abab24a3e5c730c12c506f7757562f31140cbe544fd1239");
                    jsonObject.accumulate("get_user_cards_hash", "2e2a16df8b60135cb377f6dd70c9351dd352a3d3a88349eb3985a5ec267241e1b4fba067805f53b28abab24a3e5c730c12c506f7757562f31140cbe544fd1239");
                    jsonObject.accumulate("edit_user_card_hash", "2e2a16df8b60135cb377f6dd70c9351dd352a3d3a88349eb3985a5ec267241e1b4fba067805f53b28abab24a3e5c730c12c506f7757562f31140cbe544fd1239");
                    jsonObject.accumulate("save_user_card_hash", "2e2a16df8b60135cb377f6dd70c9351dd352a3d3a88349eb3985a5ec267241e1b4fba067805f53b28abab24a3e5c730c12c506f7757562f31140cbe544fd1239");
                    jsonObject.accumulate("send_sms_hash", "2e2a16df8b60135cb377f6dd70c9351dd352a3d3a88349eb3985a5ec267241e1b4fba067805f53b28abab24a3e5c730c12c506f7757562f31140cbe544fd1239");

                    json = json + jsonObject.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                json = "[" + json + "]";
                params.put("data", json);

                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(str_checkout_order);
    }


    public class GetDataFromServer extends AsyncTask<Void, Void, Void> {

        String response;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //initFlip();

        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url_getallcategories = AllKeys.WEBSITE + "getAllCategory";
                Log.d(TAG, "url GetAllCategories :" + url_getallcategories);

                ServiceHandler sh = new ServiceHandler();
                response = sh.makeServiceCall(url_getallcategories, ServiceHandler.GET);


                Log.d(TAG, "GetAllCategories Response :" + response);
                sd.delete(dbhandler.TABLE_CATEGORY, null, null);
                if (response.contains("name")) {

                    try {
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            try {
                                ContentValues cv = new ContentValues();
                                cv.put(dbhandler.CATEGORY_ID, c.getString(AllKeys.TAG_CATEGORY_ID));
                                cv.put(dbhandler.CATEGORY_NAME, c.getString(AllKeys.TAG_CATEGORY_NAME));
                                cv.put(dbhandler.CATEGORY_PARENTID, c.getString(AllKeys.TAG_CATEGORY_PARENTID));
                                cv.put(dbhandler.CATEGORY_TYPE, c.getString(AllKeys.TAG_CATEGORY_TYPE));
                                cv.put(dbhandler.CATEGORY_DELETED_AT, c.getString(AllKeys.TAG_CATEGORY_DELETED_AT));
                                cv.put(dbhandler.CATEGORY_CREATED_AT, c.getString(AllKeys.TAG_CATEGORY_CREATED_AT));
                                cv.put(dbhandler.CATEGORY_UPDATED_AT, c.getString(AllKeys.TAG_CATEGORY_UPDATED_AT));
                                cv.put(dbhandler.CATEGORY_MLM_DISCOUNT, c.getString(AllKeys.TAG_CATEGORY_MLM_DISCOUNT));
                                cv.put(dbhandler.CATEGORY_SEQUENCE_NO, c.getString(AllKeys.TAG_CATEGORY_SEQUENCE_NO));
                                cv.put(dbhandler.CATEGORY_SHIPPING_COST, c.getString(AllKeys.TAG_CATEGORY_SHIPING_COST));
                                cv.put(dbhandler.CATEGORY_SHIPING_COST_SELLER, c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_SELLER));
                                cv.put(dbhandler.CATEGORY_SHIPPING_COST_BUYER, c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER));

                                sd.insert(dbhandler.TABLE_CATEGORY, null, cv);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error in Inserting Data : " + e.getMessage());
                                FirebaseCrash.report(new Exception("Error Inserting Category Data : " + e.getMessage()));

                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



            Intent i = new Intent(SplashScreen.this, NewDashBoardActivity.class);
            startActivity(i);
            finish();


        }

    }

    private void getAllCategoryDetailsFromServer() {

        String url_getallcategories = AllKeys.WEBSITE + "getAllCategory";
        Log.d(TAG, "url GetAllCategories :" + url_getallcategories);
        StringRequest str_getallcategories = new StringRequest(Request.Method.GET, url_getallcategories, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "GetAllCategories Response :" + response);
                sd.delete(dbhandler.TABLE_CATEGORY, null, null);
                if (response.contains("name")) {

                    try {
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            try {
                                ContentValues cv = new ContentValues();
                                cv.put(dbhandler.CATEGORY_ID, c.getString(AllKeys.TAG_CATEGORY_ID));
                                cv.put(dbhandler.CATEGORY_NAME, c.getString(AllKeys.TAG_CATEGORY_NAME));
                                cv.put(dbhandler.CATEGORY_PARENTID, c.getString(AllKeys.TAG_CATEGORY_PARENTID));
                                cv.put(dbhandler.CATEGORY_TYPE, c.getString(AllKeys.TAG_CATEGORY_TYPE));
                                cv.put(dbhandler.CATEGORY_DELETED_AT, c.getString(AllKeys.TAG_CATEGORY_DELETED_AT));
                                cv.put(dbhandler.CATEGORY_CREATED_AT, c.getString(AllKeys.TAG_CATEGORY_CREATED_AT));
                                cv.put(dbhandler.CATEGORY_UPDATED_AT, c.getString(AllKeys.TAG_CATEGORY_UPDATED_AT));
                                cv.put(dbhandler.CATEGORY_MLM_DISCOUNT, c.getString(AllKeys.TAG_CATEGORY_MLM_DISCOUNT));
                                cv.put(dbhandler.CATEGORY_SEQUENCE_NO, c.getString(AllKeys.TAG_CATEGORY_SEQUENCE_NO));
                                cv.put(dbhandler.CATEGORY_SHIPPING_COST, c.getString(AllKeys.TAG_CATEGORY_SHIPING_COST));
                                cv.put(dbhandler.CATEGORY_SHIPING_COST_SELLER, c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_SELLER));
                                cv.put(dbhandler.CATEGORY_SHIPPING_COST_BUYER, c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER));

                                sd.insert(dbhandler.TABLE_CATEGORY, null, cv);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error in Inserting Data : " + e.getMessage());
                                FirebaseCrash.report(new Exception("Error Inserting Category Data : " + e.getMessage()));

                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }


                }

                Intent i = new Intent(SplashScreen.this, NewDashBoardActivity.class);
                startActivity(i);
                finish();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {

                    Log.d(TAG, "Server Error");
                    Intent i = new Intent(SplashScreen.this, NewDashBoardActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    getAllCategoryDetailsFromServer();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getallcategories);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
