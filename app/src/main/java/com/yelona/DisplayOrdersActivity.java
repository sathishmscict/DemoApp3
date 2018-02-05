package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yelona.adapter.OrderDisplayRecyclerViewAdapter;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.pojo.OrderMaster;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class DisplayOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerview_orders;
    private Context context = this;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = DisplayOrdersActivity.class.getSimpleName();
    private dbhandler db;
    private ArrayList<OrderMaster> list_OrderData = new ArrayList<OrderMaster>();
    private OrderDisplayRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders);

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


        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }


        setTitle("Orders");

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(back_button);
        } catch (Exception e) {
            e.printStackTrace();
        }


        pDialog = new SpotsDialog(context);
        pDialog.setCancelable(false);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();


        db = new dbhandler(context);


        recyclerview_orders = (RecyclerView) findViewById(R.id.recyclerview_orders);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        // RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);

      /*  LinearLayoutManager layoutManagerrr = (LinearLayoutManager) recyclerview_products
                .getLayoutManager();
        layoutManagerrr.scrollToPositionWithOffset(0, 0);*/
        recyclerview_orders.setLayoutManager(layoutManager);
        recyclerview_orders.addItemDecoration(new dbhandler.GridSpacingItemDecoration(2, db.dpToPx(2), true));

        getAllOrderDetailsFromServer();

    }

    private void getAllOrderDetailsFromServer()
    {
        String url_getOrders = AllKeys.WEBSITE + "getOrdersHistoryByUserId/" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetORderHistory : " + url_getOrders);
        StringRequest str_getOrders = new StringRequest(Request.Method.GET, url_getOrders, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response GetOrders  :" + response);

                list_OrderData.clear();
                if (!response.contains("[]")) {

                    response = dbhandler.convertToJsonFormat(response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject c = arr.getJSONObject(i);


                            // OrderMaster(String orderId, String price, String quantity, String shipping_charge, String total, String shipping_address, String order_date, String delivered_date, String order_status, String dispatch_date, String completed_date) {
                            OrderMaster om = new OrderMaster(c.getString(AllKeys.TAG_ORDER_SINGLE_ORDERID), c.getString(AllKeys.TAG_ORDER_UNIT_PRICE), c.getString(AllKeys.TAG_ORDER_QUANTITY), c.getString(AllKeys.TAG_ORDER_SHIPPING_CHARGE), c.getString(AllKeys.TAG_ORDER_PAYABLE_USER), c.getString(AllKeys.TAG_ORDER_SHIPPING_ADDRESS), c.getString(AllKeys.TAG_ORDER_CREATED_AT), c.getString(AllKeys.TAG_ORDER_DELIVERY_DATE), c.getString(AllKeys.TAG_ORDER_STATUS), c.getString(AllKeys.TAG_ORDER_DISPATCH_DATE), c.getString(AllKeys.TAG_ORDER_COMPLETE_DATE), c.getString(AllKeys.TAG_CHECKOUT_PRODUCT_NAME), c.getString(AllKeys.TAG_ORDER_VARIANT_ID), c.getString(AllKeys.TAG_ORDER_PRODUCT_IMAGE),c.getString(AllKeys.TAG_ORDER_TRACKING_ID));

                            list_OrderData.add(om);
                            
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new OrderDisplayRecyclerViewAdapter(context, list_OrderData);
                    recyclerview_orders.setAdapter(adapter);


                } else {
                    Toast.makeText(context, "No Orders Found", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();

                } else {
                    getAllOrderDetailsFromServer();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getOrders);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent ii = new Intent(context, NewDashBoardActivity.class);
            startActivity(ii);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {
            Intent i = new Intent(context, NewDashBoardActivity.class);
            //i.putExtra("ActivityName",ACTIVITYNAME);
            startActivity(i);
            finish();

        } catch (Exception e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error in Converting Class : " + e.getMessage());
            e.printStackTrace();
        }


    }

}
