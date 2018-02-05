package com.yelona.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yelona.R;
import com.yelona.app.MyApplication;
import com.yelona.helper.AllKeys;
import com.yelona.helper.NetConnectivity;
import com.yelona.session.SessionManager;

import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;


public class FragmentWallet extends android.support.v4.app.Fragment {


    private Context context = getActivity();
    private TextView walletamt;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private SpotsDialog spotDialog;
    private String TAG = FragmentWallet.class.getSimpleName();


    public FragmentWallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wallet, container,
                false);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        sessionManager = new SessionManager(getActivity());
        userDetails = new HashMap<String, String>();
        userDetails = sessionManager.getSessionDetails();


        walletamt = (TextView) rootView.findViewById(R.id.walletamt);


        if (NetConnectivity.isOnline(getActivity())) {

            //new GetWalletAmountDetailsFromServer().execute();
            if(userDetails.get(SessionManager.KEY_USERID).equals("0"))
            {
                //Snackbar.make()
                Toast.makeText(getActivity(), "Please login into your account and get wallet details", Toast.LENGTH_SHORT).show();
                walletamt.setText(" -");
            }
            else
            {
            getWalletBalance();
                
            }
        } else {
            Toast.makeText(getActivity(), "Please enable wifi or mobile data", Toast.LENGTH_SHORT).show();
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    private void showDialog() {

        spotDialog = new SpotsDialog(getActivity());
        spotDialog.setCancelable(false);
        if (!spotDialog.isShowing()) {
            spotDialog.show();

        }
    }

    private void hideDialog() {
        if (spotDialog.isShowing()) {
            spotDialog.dismiss();


        }
    }

    private void getWalletBalance() {

        showDialog();

        String url_getWallet = AllKeys.WEBSITE + "getWalletBalance/" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetWalletBalance : " + url_getWallet);
        StringRequest str_getWalletBalance = new StringRequest(Request.Method.GET, url_getWallet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Responce Get Balance : " + response);

                walletamt.setText(""+response);

                sessionManager.setWalletAmount(response);

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {


                    hideDialog();
                } else {
                    getWalletBalance();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getWalletBalance);


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // pDialog.dismiss();
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
    }


	/*private class GetWalletAmountDetailsFromServer extends AsyncTask<Void, Void, Void>
    {
		private ProgressDialog pDialog;


		String walletjsonStr;
		private JSONArray walletDetails;




		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// /Write here statements

			ServiceHandler sh = new ServiceHandler();
			String url = Allkeys.TAG_WEBSITE
					+ "GetMyWallete?action=mywallete&custid="+ userDetails.get(SessionManager.KEY_USERID) +"";
			Log.d("URL GetWallet",url);
			walletjsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

			if (walletjsonStr != null && !walletjsonStr.equals("")) {
				//jsonStr = "{\"" + Allkeys.GENERAL_ARRAY + "\":" + jsonStr + "}";

				try {
					Log.d("Wallet Response: ", "> " + walletjsonStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Getting JSON Array node
			}


			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			// Write statement after background process execution


			try {


				walletamt.setText("\u20b9 "+walletjsonStr);

				sessionManager.storewalletpoinst(""+walletjsonStr);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		}
	}*/

}