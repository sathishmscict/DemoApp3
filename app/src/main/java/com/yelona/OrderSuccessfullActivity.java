package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yelona.animation.FlipAnimation;
import com.yelona.database.dbhandler;
import com.yelona.session.SessionManager;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class OrderSuccessfullActivity extends AppCompatActivity {

    private TextView priceInfo;
    private Context context = this;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private TextView txtViewDetails;

    //private TextView txtShippingName, txtShippingAddress, txtShippingMobile;
    private ImageView ivStatus;
    private TextView tvStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_successfull);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        pDialog = new SpotsDialog(context);
        pDialog.setCancelable(false);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(back_button);
        } catch (Exception e) {
            e.printStackTrace();
        }

        priceInfo = (TextView) findViewById(R.id.priceInfo);

        setTitle("Order Success");

        priceInfo.setText("Total price for " + userDetails.get(SessionManager.KEY_TOTAL_ITEMS) + " items : \u20b9 " + userDetails.get(SessionManager.KEY_GRAND_TOTAL) + "");


        ivStatus = (ImageView) findViewById(R.id.ivStatus);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        initFlip(ivStatus);

/*
        txtShippingName = (TextView) findViewById(R.id.txtShippingName);
        txtShippingAddress = (TextView) findViewById(R.id.txtShippingAddress);
        txtShippingMobile = (TextView) findViewById(R.id.txtShippingMobile);


        txtShippingName = (TextView) findViewById(R.id.txtShippingName);
        txtShippingAddress = (TextView) findViewById(R.id.txtShippingAddress);
        txtShippingMobile = (TextView) findViewById(R.id.txtShippingMobile);


        txtShippingName.setText(userDetails.get(SessionManager.KEY_SHIPPING_NAME));
        txtShippingAddress.setText(userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS));
        txtShippingMobile.setText(userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO));*/


        txtViewDetails = (TextView) findViewById(R.id.txtViewDetails);

        txtViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NewDashBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void initFlip(ImageView flipImage) {


        FlipAnimation.create().with(flipImage)
                .setDuration(3600)
                .setRepeatCount(FlipAnimation.INFINITE)
                .start();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            try {

                Intent intent = new Intent(context, NewDashBoardActivity.class);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {


            Intent intent = new Intent(context, NewDashBoardActivity.class);

            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
