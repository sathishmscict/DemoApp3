package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yelona.database.dbhandler;
import com.yelona.session.SessionManager;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class ManagePaymentInformation extends AppCompatActivity {

    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private SpotsDialog pDialog;
    private dbhandler db;

    private TextView txtBillingName;
    private TextView txtBillingAddress;
    private TextView txtBillingMobile;
    private Button btnChangeBillingAddress;
    private CoordinatorLayout coordinatorLayout;
    private TextView txtShippingName, txtShippingAddress, txtShippingMobile;
    private Button btnChangeShippingAddress;
    private RadioGroup rdGrpPaymentMode;
    private RadioButton rdOnline, rdCOD;
    private LinearLayout llCOD;
    private TextInputLayout input_layout_edtOTP;
    private EditText edtOTP;
    private Button btnVerify;
    private TextView txtResendCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_payment_information);
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

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(back_button);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();
        db = new dbhandler(context);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        txtBillingName = (TextView) findViewById(R.id.txtBillingName);
        txtBillingAddress = (TextView) findViewById(R.id.txtBillingAddress);
        txtBillingMobile = (TextView) findViewById(R.id.txtBillingMobile);
        btnChangeBillingAddress = (Button) findViewById(R.id.btnChangeBillingAddress);

        txtShippingName = (TextView) findViewById(R.id.txtShippingName);
        txtShippingAddress = (TextView) findViewById(R.id.txtShippingAddress);
        txtShippingMobile = (TextView) findViewById(R.id.txtShippingMobile);
        btnChangeShippingAddress = (Button) findViewById(R.id.btnChangeShippingAddress);


        txtBillingName.setText(userDetails.get(SessionManager.KEY_BILLING_NAME));
        txtBillingAddress.setText(userDetails.get(SessionManager.KEY_BILLING_ADDRESS));
        txtBillingMobile.setText(userDetails.get(SessionManager.KEY_BILLING_MOBILENO));


        txtShippingName.setText(userDetails.get(SessionManager.KEY_SHIPPING_NAME));
        txtShippingAddress.setText(userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS));
        txtShippingMobile.setText(userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO));


        rdGrpPaymentMode = (RadioGroup) findViewById(R.id.rdGrpPaymenMode);
        rdCOD = (RadioButton) findViewById(R.id.rdCOD);
        rdOnline = (RadioButton) findViewById(R.id.rdOnline);

        llCOD = (LinearLayout) findViewById(R.id.llCOD);
        llCOD.setVisibility(View.GONE);

        input_layout_edtOTP = (TextInputLayout) findViewById(R.id.input_layout_edtOTP);
        edtOTP = (EditText) findViewById(R.id.edtOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        txtResendCode = (TextView) findViewById(R.id.txtResendCode);



        btnChangeBillingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ManageBillingAddressActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnChangeShippingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ManageShippingAddressActivity.class);
                startActivity(intent);
                finish();

            }
        });


        rdGrpPaymentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (radioGroup.getCheckedRadioButtonId() == R.id.rdCOD) {

                    llCOD.setVisibility(View.VISIBLE);



                  //  sendSMSToUSer();


                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rdOnline) {
                    llCOD.setVisibility(View.GONE);
                }
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        txtResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();
            }
        });


    }
//onCreate Completed


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(context, ManageShippingAddressActivity.class);
            startActivity(intent);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(context, ManageShippingAddressActivity.class);
        startActivity(intent);
        finish();

    }
}
