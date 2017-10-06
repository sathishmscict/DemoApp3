package com.yelona.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yelona.R;
import com.yelona.helper.NetConnectivity;
import com.yelona.session.SessionManager;

import java.util.HashMap;


public class FragmentOffers extends android.support.v4.app.Fragment {



	private Context context=getActivity();
	private TextView walletamt;
	private SessionManager sessionManager;
	private HashMap<String, String> userDetails;


	public FragmentOffers() {
		// Required empty public constructor
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_offers, container,
				false);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}



		sessionManager = new SessionManager(getActivity());
		userDetails = new HashMap<String, String>();
		userDetails = sessionManager.getSessionDetails();


		walletamt =(TextView)rootView.findViewById(R.id.walletamt);


		if(NetConnectivity.isOnline(getActivity()))
		{

			//new GetWalletAmountDetailsFromServer().execute();
		}
		else
		{
			Toast.makeText(getActivity(), "Please enable wifi or mobile data", Toast.LENGTH_SHORT).show();
		}






		// Inflate the layout for this fragment
		return rootView;
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

	/** Called when leaving the activity */
	@Override
	public void onPause() {
		super.onPause();
	}

	/** Called when returning to the activity */
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