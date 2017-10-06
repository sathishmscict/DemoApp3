package com.yelona;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.yelona.session.SessionManager;




import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Satish Gadde on 02-09-2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails;

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        Context context = this;
       // sendRegistrationToServer(refreshedToken, context);

    }

    public String onTokenRefreshNew(Context context) {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token new : " + refreshedToken);


        //sendRegistrationToServer(refreshedToken, context);

        return  refreshedToken;

    }
/*
    private void sendRegistrationToServer(final String token, Context context) {
        //You can implement this method to store the token on your server
        //Not required for current project

        try {
            sessionmanager = new SessionManager(context);
            userDetails = new HashMap<String, String>();
            userDetails = sessionmanager.getUserDetails();
            //Log.i(TAG, "registering device (regId = " + regId + ")");
            String serverUrl = AllKeys.WEBSITE_NEW + "UpdateTokenDetails";
            Log.d("Server URL : ", serverUrl);


            StringRequest str_sendfcm = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d(TAG,"FCM Response : " + response);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d(TAG,"FCM Errorb bResponse : " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("type", "updatetoken");
                    params.put("mobiletype", "android");
                    params.put("tokenid", token);
                    params.put("custid", userDetails.get(SessionManager.KEY_CHILDRENID));
                    Log.d(TAG , " Params "+ params.toString());
                    return params;
                }
            };

            MyApplication.getInstance().addToRequestQueue(str_sendfcm);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Send FCM Error : "+ e.getMessage());
        }


    }*/



}
