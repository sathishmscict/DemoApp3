package com.yelona;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.math.BigDecimal;
import android.icu.util.Currency;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.StartCheckoutEvent;
import com.payu.india.CallBackHandler.OnetapCallback;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Extras.PayUSdkDetails;
import com.payu.india.Interfaces.OneClickPaymentListener;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.wang.avi.AVLoadingIndicatorView;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.ServiceHandler;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class ManagePaymentsActivity extends AppCompatActivity implements OneClickPaymentListener {


    private Context context = this;
    private String TAG = ManagePaymentsActivity.class.getSimpleName();
    private String ACTIVITYNAME;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();

    public ArrayList<String> list_cartItemsId = new ArrayList<String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private CardView cvCOD;
    private RadioGroup rdGrpPaymentMode;
    private RadioButton rdOnline, rdCOD;
    private LinearLayout llCOD;
    private TextInputLayout input_layout_edtOTP;
    private EditText edtOTP;
    private Button btnVerify;
    private TextView txtResendCode;
    private int VERIFICATION_CODE = 0;
    private Button btnPlaceOrder;
    private AVLoadingIndicatorView progress;
    private TextView txtsmsdescr;
    private TextView txtPriceTotal;
    private TextView txtGrandTotal;
    private TextView txtTotalDeliveryCharges;
    private TextView txtTotalMLMDiscont;

    private boolean isVerified = false;
    private int PAYMENT_TYPE = 0;
    private String PAYMENT_ID = "";
    private String PAYMENT_RESPONSE = "";



    /*payubiz Related keys*/

    private String merchantKey, userCredentials;
    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;

    // This sets the configuration
    private PayuConfig payuConfig;


    // Used when generating hash from SDK
    private PayUChecksum checksum;
    private TextView txtPriceItemsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("");
        //toolbar.setBackgroundColor(getResources().getColor(R.color.yelona_price_text_color));
        // toolbar.setLogo(R.drawable.icon_back6);

        setSupportActionBar(toolbar);

        setTitle("Payments");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        rdGrpPaymentMode = (RadioGroup) findViewById(R.id.rdGrpPaymenMode);
        rdCOD = (RadioButton) findViewById(R.id.rdCOD);
        rdOnline = (RadioButton) findViewById(R.id.rdOnline);

        llCOD = (LinearLayout) findViewById(R.id.llCOD);
        // llCOD.setVisibility(View.GONE);


        input_layout_edtOTP = (TextInputLayout) findViewById(R.id.input_layout_edtOTP);
        edtOTP = (EditText) findViewById(R.id.edtOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        txtResendCode = (TextView) findViewById(R.id.txtResendCode);
        progress = (AVLoadingIndicatorView) findViewById(R.id.progress);


        cvCOD = (CardView) findViewById(R.id.cvCOD);
        cvCOD.setVisibility(View.GONE);

        txtsmsdescr = (TextView) findViewById(R.id.txtsmsdescr);


        txtPriceItemsCount = (TextView) findViewById(R.id.txtPriceItemsCount);
        txtPriceTotal = (TextView) findViewById(R.id.txtTotalPrice);

        txtGrandTotal = (TextView) findViewById(R.id.txtGrandTotal);
        txtTotalDeliveryCharges = (TextView) findViewById(R.id.txtTotalDeliveryCharge);

        txtTotalMLMDiscont = (TextView) findViewById(R.id.txtTotalMLMDiscont);


        txtGrandTotal.setText("\u20b9 " + dbhandler.convertedDoubleAmount(userDetails.get(SessionManager.KEY_GRAND_TOTAL)));

        txtPriceTotal.setText("\u20b9 " + dbhandler.convertedDoubleAmount(userDetails.get(SessionManager.KEY_PRICE_TOTAL)));



        if (userDetails.get(SessionManager.KEY_MLM_DISCOUNT).equals("0")) {
            txtTotalMLMDiscont.setText("0");

        } else {


            txtTotalMLMDiscont.setText("\u20b9 "+dbhandler.convertedDoubleAmount(userDetails.get(SessionManager.KEY_MLM_DISCOUNT)));
        }


        if (userDetails.get(SessionManager.KEY_DELIVERY_CHARGES_TOTAL).equals("0")) {
            txtTotalDeliveryCharges.setText("Free");
        } else {
            txtTotalDeliveryCharges.setText("\u20b9 " + userDetails.get(SessionManager.KEY_DELIVERY_CHARGES_TOTAL));
        }

        if (userDetails.get(SessionManager.KEY_CARTITEMS_ID).equals("")) {
            setTitle("My Cart");
        } else {

            String all_cart_ids = userDetails.get(SessionManager.KEY_CARTITEMS_ID);
            all_cart_ids = all_cart_ids.replace(" ", "");
            list_cartItemsId = new ArrayList<>(Arrays.asList(all_cart_ids.split(",")));

        }

        if (userDetails.get(SessionManager.KEY_CHECKOUT_TYPE).equals("single")) {
            txtPriceItemsCount.setText("Price (1 item)");
        } else {
            txtPriceItemsCount.setText("Price (" + list_cartItemsId.size() + " items)");
        }


        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);

        DisablePlaceOrderButton();


        // lets tell the people what version of sdk we are using
        PayUSdkDetails payUSdkDetails = new PayUSdkDetails();

        //TODO Must write this code if integrating One Tap payments
        OnetapCallback.setOneTapCallback(this);

        //TODO Must write below code in your activity to set up initial context for PayU
        Payu.setInstance(this);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (PAYMENT_TYPE == 1) {
                    if (userDetails.get(SessionManager.KEY_CHECKOUT_TYPE).equals("single")) {
                        sendBuyNowDetailsToServerUsing_POST();
                    } else {
                        sendCheckoutDetailsToServerUsing_POST();
                    }


                } else {

                    navigateToBaseActivity();

                }
                // Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        rdGrpPaymentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (radioGroup.getCheckedRadioButtonId() == R.id.rdCOD) {


                    if (isVerified == false) {
                        cvCOD.setVisibility(View.VISIBLE);
                        progress.show();
                        //SendSMSToUser();
                        new SendSMSToUserNew().execute();
                        btnPlaceOrder.setText("Place Order");
                        txtResendCode.setText("Please wait...");
                        //  sendSMSToUSer();
                        DisablePlaceOrderButton();
                        btnVerify.setEnabled(true);
                        PAYMENT_TYPE = 1;
                    } else {
                        PAYMENT_TYPE = 2;

                        EnablePlaceOrderButton();
                        txtResendCode.setText("Verified successfully");
                        txtResendCode.setEnabled(false);
                        btnVerify.setEnabled(false);
                    }


                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rdOnline) {
                    cvCOD.setVisibility(View.GONE);
                    btnPlaceOrder.setText("Pay Now");
                    btnPlaceOrder.setEnabled(true);
                    PAYMENT_TYPE = 2;

                    EnablePlaceOrderButton();
                }
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isVerified == false) {

                    if(!edtOTP.getText().toString().equals(""))
                    {

                        if (VERIFICATION_CODE == Integer.parseInt(edtOTP.getText().toString())) {

                            txtResendCode.setText("Verified successfully");
                            txtResendCode.setClickable(false);
                            btnPlaceOrder.setEnabled(true);
                            EnablePlaceOrderButton();
                            input_layout_edtOTP.setErrorEnabled(false);
                            txtsmsdescr.setText("Please verify your code send to your mobile no.");

                        } else
                        {
                            input_layout_edtOTP.setErrorEnabled(true);
                            input_layout_edtOTP.setError("Invalid code");

                            DisablePlaceOrderButton();

                            txtResendCode.setText("Resend Code");
                            txtsmsdescr.setText("Invalid Code");
                        }
                        input_layout_edtOTP.setErrorEnabled(false);
                    }
                    else
                    {

                        input_layout_edtOTP.setErrorEnabled(true);
                        input_layout_edtOTP.setError("Please enter OTP");
                        txtsmsdescr.setText("Please enter OTP");
                    }


                } else {
                    txtResendCode.setEnabled(false);

                    EnablePlaceOrderButton();
                }


            }
        });

        txtResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();
                progress.show();
                txtResendCode.setText("Please wait...");
                //SendSMSToUser();
                new SendSMSToUserNew().execute();
            }
        });


    }

    public void showDialog() {

        if (!pDialog.isShowing()) {

            pDialog.show();
        }


    }

    public void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();

        }
    }


   /* private void sendCheckoutDetailsToServerUsing_GET() {
        showDialog();

        String url_checkout_order;
        if (PAYMENT_TYPE == 1) {
            url_checkout_order = AllKeys.WEBSITE + "CheckoutOrder/" + userDetails.get(SessionManager.KEY_USERID) + "/" + PAYMENT_TYPE + "/" + userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_NAME)) + "/" + userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_EMAIL));

        } else {
            url_checkout_order = AllKeys.WEBSITE + "CheckoutOrder/" + userDetails.get(SessionManager.KEY_USERID) + "/" + PAYMENT_TYPE + "/" + userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_NAME)) + "/" + userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_EMAIL)) + "/" + PAYMENT_ID + "/" + PAYMENT_RESPONSE + "";

        }


        Log.d(TAG, "URL Checkout Order : " + url_checkout_order);

        StringRequest str_checkout_order = new StringRequest(Request.Method.GET, url_checkout_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "CheckoutOrder Response : " + response.toString());


                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                if (response.contains("success")) {

                    Intent ii = new Intent(context, OrderSuccessfullActivity.class);
                    startActivity(ii);

                    finish();
                    sessionmanager.setCartItemsIdDetails("");

                } else {

                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                }

                hideDialog();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    hideDialog();
                } else {
                    sendCheckoutDetailsToServerUsing_GET();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_checkout_order);
    }*/

    /**
     * Send BuuyNow Product Details To Server
     */
    private void sendBuyNowDetailsToServerUsing_POST() {
        showDialog();

        String url_checkout_order;
        if (PAYMENT_TYPE == 1) {
            url_checkout_order = AllKeys.WEBSITE + "BuyNow/" + userDetails.get(SessionManager.KEY_USERID) + "/" + PAYMENT_TYPE + "/" + userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_NAME)) + "/" + userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_EMAIL));
            url_checkout_order = AllKeys.WEBSITE + "BuyNow";

        } else {
            url_checkout_order = AllKeys.WEBSITE + "BuyNow/" + userDetails.get(SessionManager.KEY_USERID) + "/" + PAYMENT_TYPE + "/" + userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_NAME)) + "/" + userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_EMAIL)) + "/" + PAYMENT_ID + "/" + PAYMENT_RESPONSE + "";
            url_checkout_order = AllKeys.WEBSITE + "BuyNow";
        }


        Log.d(TAG, "URL Checkout Order : " + url_checkout_order);

        StringRequest str_checkout_order = new StringRequest(Request.Method.POST, url_checkout_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "CheckoutOrder Response : " + response.toString());


                //    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                if (response.contains("success")) {

                    Intent ii = new Intent(context, OrderSuccessfullActivity.class);
                    startActivity(ii);

                    finish();
                    sessionmanager.setCartItemsIdDetails("");

                } else {

                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                }

                hideDialog();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    hideDialog();
                } else {
                    sendBuyNowDetailsToServerUsing_POST();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("userid", userDetails.get(SessionManager.KEY_USERID));
                params.put("productid", userDetails.get(SessionManager.KEY_PRODUCT_ID));

                params.put("payment_type", String.valueOf(PAYMENT_TYPE));
                params.put("address_id", userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID));
                params.put("shipping_name", userDetails.get(SessionManager.KEY_SHIPPING_NAME));
                params.put("shipping_mobile", userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO));
                params.put("shipping_email", userDetails.get(SessionManager.KEY_SHIPPING_EMAIL));

                if (PAYMENT_RESPONSE.contains("{")) {
                    params.put("paymentid", PAYMENT_ID);
                    params.put("payment_response", "[" + PAYMENT_RESPONSE + "]");
                } /*else {
                    params.put("payment_response", null);
                    params.put("paymentid", "");
                }*/


                Log.d(TAG, "Param : " + params.toString());


                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(str_checkout_order);
    }


    /**
     * Complete Sending Product Details to Server
     */


    /**
     * Send multiple products checkout
     */
    private void sendCheckoutDetailsToServerUsing_POST() {
        showDialog();

        String url_checkout_order;
        if (PAYMENT_TYPE == 1) {
            url_checkout_order = AllKeys.WEBSITE + "CheckoutOrder/" + userDetails.get(SessionManager.KEY_USERID) + "/" + PAYMENT_TYPE + "/" + userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_NAME)) + "/" + userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_EMAIL));
            url_checkout_order = AllKeys.WEBSITE + "CheckoutOrder";

        } else {
            url_checkout_order = AllKeys.WEBSITE + "CheckoutOrder/" + userDetails.get(SessionManager.KEY_USERID) + "/" + PAYMENT_TYPE + "/" + userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_NAME)) + "/" + userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO) + "/" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_SHIPPING_EMAIL)) + "/" + PAYMENT_ID + "/" + PAYMENT_RESPONSE + "";
            url_checkout_order = AllKeys.WEBSITE + "CheckoutOrder";
        }


        Log.d(TAG, "URL Checkout Order : " + url_checkout_order);

        StringRequest str_checkout_order = new StringRequest(Request.Method.POST, url_checkout_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "CheckoutOrder Response : " + response.toString());


                //    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();


                if (response.contains("success")) {

                    Answers.getInstance().logStartCheckout(new StartCheckoutEvent());





                    Intent ii = new Intent(context, OrderSuccessfullActivity.class);
                    startActivity(ii);

                    finish();
                    sessionmanager.setCartItemsIdDetails("");

                } else {

                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                }

                hideDialog();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    hideDialog();
                } else {
                    sendCheckoutDetailsToServerUsing_POST();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("userid", userDetails.get(SessionManager.KEY_USERID));
                params.put("payment_type", String.valueOf(PAYMENT_TYPE));
                params.put("address_id", userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS_ID));
                params.put("shipping_name", userDetails.get(SessionManager.KEY_SHIPPING_NAME));
                params.put("shipping_mobile", userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO));
                params.put("shipping_email", userDetails.get(SessionManager.KEY_SHIPPING_EMAIL));

                if (PAYMENT_RESPONSE.contains("{")) {
                    params.put("paymentid", PAYMENT_ID);
                    params.put("payment_response", "[" + PAYMENT_RESPONSE + "]");
                } /*else {
                    params.put("payment_response", null);
                    params.put("paymentid", "");
                }*/


                Log.d(TAG, "Parama : " + params.toString());


                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(str_checkout_order);
    }

    private void DisablePlaceOrderButton() {
        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setSelected(false);
        btnPlaceOrder.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));

    }

    private void EnablePlaceOrderButton() {
        btnPlaceOrder.setEnabled(true);
        btnPlaceOrder.setSelected(true);
        btnPlaceOrder.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

    }

    public class SendSMSToUserNew extends AsyncTask<Void,Void,Void>
    {

        String response="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
            Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();
            progress.show();

        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                Random R = new Random();
                if (VERIFICATION_CODE == 0) {
                    VERIFICATION_CODE = R.nextInt(9999 - 1000) + 1000;
                }


                //http://arham.dnsitexperts.com/yelona/index.php/welcome/sendSMS/9723613143/1234
                String url_SendSMS = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + VERIFICATION_CODE + "/otp";
                Log.d(TAG,"Send SMS To USer : "+url_SendSMS);



                ServiceHandler sh= new ServiceHandler();
                response  = sh.makeServiceCall(url_SendSMS,ServiceHandler.GET);
            } catch (Exception e) {
                e.printStackTrace();

                throw new RuntimeException(e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



            Log.d(TAG, "Response SMS Sending : " + response);

            if (response.contains("successfully")) {
                Toast.makeText(context, "Please verify your code send to your mobile no.", Toast.LENGTH_SHORT).show();
                txtResendCode.setText("Resend");

                txtsmsdescr.setText("Please verify your code send to your mobile no.");
                progress.hide();

            } else {
                Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
            }


            hideDialog();



        }
    }

    //OnCreate completed
    private void SendSMSToUser()
    {


        Random R = new Random();
        if (VERIFICATION_CODE == 0) {
            VERIFICATION_CODE = R.nextInt(9999 - 1000) + 1000;
        }


        Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();

        //http://arham.dnsitexperts.com/yelona/index.php/welcome/sendSMS/9723613143/1234
        String url_SendSMS = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + VERIFICATION_CODE + "/otp";
        Log.d(TAG,"Send SMS To USer : "+url_SendSMS);

        StringRequest str_sendSmsRequest = new StringRequest(Request.Method.GET, url_SendSMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response SMS Sending : " + response);

                if (response.contains("successfully")) {
                    Toast.makeText(context, "Please verify your code send to your mobile no.", Toast.LENGTH_SHORT).show();
                    txtResendCode.setText("Resend");

                    txtsmsdescr.setText("Please verify your code send to your mobile no.");

                } else {
                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                }
                progress.hide();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError || error instanceof NetworkError) {
                    progress.hide();
                    txtResendCode.setText("Resend");
                    txtsmsdescr.setText("Please verify your code send to your mobile no.");
                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                } else {
                 //  SendSMSToUser();
                    new SendSMSToUserNew().execute();

                }


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_sendSmsRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            try {


                Intent intent = new Intent(context, OrderReviewActivity.class);

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


            Intent intent = new Intent(context, OrderReviewActivity.class);

            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*PayuBix Online Payment Related Code*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */
                /*new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*/
                Log.d("MainActivity", "Transaction payu_response : Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"));

                PAYMENT_RESPONSE = data.getStringExtra("payu_response");
                //PAYMENT_RESPONSE = null;
//{"id":6063679442,"mode":"CC","status":"failure","unmappedstatus":"failed","key":"46Hobg","txnid":"YLN7751490875921681","transaction_fee":"1.00","amount":"1.00","discount":"0.00","additional_charges":"0.00","addedon":"2017-03-30 17:43:27","productinfo":"Yelona Products","email":"sathishmicit2012@gmail.com","udf1":"udf1","udf2":"udf2","udf3":"udf3","udf4":"udf4","udf5":"udf5","hash":"493773dae9a852b45add6071799e0349135e4bdca59597f977914e0768761389a1528f8cb28cff967187b2bc43300d62e5c41d070ccb716e0e5a77b313d65c6d","field9":"International Cards not allowed on the merchant.","payment_source":"payu","ibibo_code":"CC","error_code":"E903","Error_Message":"International cards are not accepted","is_seamless":1,"surl":"https://payu.herokuapp.com/success","furl":"https://payu.herokuapp.com/failure"}

                showDialog();
                Toast.makeText(this, "Transaction success", Toast.LENGTH_SHORT).show();

                if (userDetails.get(SessionManager.KEY_CHECKOUT_TYPE).equals("single")) {
                    sendBuyNowDetailsToServerUsing_POST();
                } else {
                    sendCheckoutDetailsToServerUsing_POST();
                }


            } else {
                Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
                Toast.makeText(context, "Transaction failed, try again...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * This method prepares all the payments params to be sent to PayuBaseActivity.java
     */
    public void navigateToBaseActivity() {

        merchantKey = AllKeys.PAYBIZ_KEY;
        String amount = userDetails.get(SessionManager.KEY_GRAND_TOTAL);
      //  amount = "1";
        String email = userDetails.get(SessionManager.KEY_USER_EMAIL);

        String value = AllKeys.PAYBIZ_ENVIRONMENT;
        int environment;
        String TEST_ENVIRONMENT = getResources().getString(R.string.test);
        if (value.equals(TEST_ENVIRONMENT))
            environment = PayuConstants.STAGING_ENV;
        else
            environment = PayuConstants.PRODUCTION_ENV;

        userCredentials = merchantKey + ":" + email;

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        /**
         * For Test Environment, merchantKey = "gtKFFx"
         * For Production Environment, merchantKey should be your live key or for testing in live you can use "0MQaQP"
         */
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo("Yelona Products");
        mPaymentParams.setFirstName(userDetails.get(SessionManager.KEY_USERNAME));
        mPaymentParams.setEmail(userDetails.get(SessionManager.KEY_USER_EMAIL));

        /*
        * Transaction Id should be kept unique for each transaction.
        * */
        PAYMENT_ID = "YLN" + userDetails.get(SessionManager.KEY_USERID) + System.currentTimeMillis();
        mPaymentParams.setTxnId(PAYMENT_ID);

        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");

        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        /**
         * These are used for store card feature. If you are not using it then user_credentials = "default"
         * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
         * here merchant_key = your merchant key,
         * user_id = unique id related to user like, email, phone number, etc.
         * */

        mPaymentParams.setUserCredentials(userCredentials);

        //TODO Pass this param only if using offer key
        //mPaymentParams.setOfferKey("cardnumber@8370");

        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
      //  generateHashFromServer(mPaymentParams);

        /**
         * Below approach for generating hash is not recommended. However, this approach can be used to test in PRODUCTION_ENV
         * if your server side hash generation code is not completely setup. While going live this approach for hash generation
         * should not be used.
         * */
        String salt = "4pVz5T4Z";
        generateHashFromSDK(mPaymentParams, salt);

    }

    /******************************
     * Client hash generation
     ***********************************/
    // Do not use this, you may use this only for testing.
    // lets generate hashes.
    // This should be done from server side..
    // Do not keep salt anywhere in app.
    public void generateHashFromSDK(PaymentParams mPaymentParams, String salt) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }

    /**
     * This method generates hash from server.
     *
     * @param mPaymentParams payments params used for hash generation
     */
    public void generateHashFromServer(PaymentParams mPaymentParams) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
//key=46Hobg&amount=1&txnid=YLN1487745261152&email=xyz@gmail.com&productinfo=product_info&firstname=firstname&udf1=udf1&udf2=udf2&udf3=udf3&udf4=udf4&udf5=udf5&user_credentials=46Hobg:xyz@gmail.com
        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ManagePaymentsActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected PayuHashes doInBackground(String... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {

                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");
                url = new URL(AllKeys.TAG_PAYMENT_HASH_GENERATION);


                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }
                //   Toast.makeText(MainActivity.this, "Data : "+responseStringBuffer.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Data : ", "Data " + responseStringBuffer.toString());

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        //TODO Below three hashes are mandatory for payment flow and needs to be generated at merchant server
                        /**
                         * Payment hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_hash -
                         *
                         * sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT)
                         *
                         */
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));
                            break;
                        /**
                         * vas_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating vas_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be "default"
                         *
                         */
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            break;
                        /**
                         * payment_related_details_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_related_details_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                            break;

                        //TODO Below hashes only needs to be generated if you are using Store card feature
                        /**
                         * delete_user_card_hash is used while deleting a stored card.
                         * Below is formula for generating delete_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        /**
                         * get_user_cards_hash is used while fetching all the cards corresponding to a user.
                         * Below is formula for generating get_user_cards_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        /**
                         * edit_user_card_hash is used while editing details of existing stored card.
                         * Below is formula for generating edit_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        /**
                         * save_user_card_hash is used while saving card to the vault
                         * Below is formula for generating save_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;

                        //TODO This hash needs to be generated if you are using any offer key
                        /**
                         * check_offer_status_hash is used while using check_offer_status api
                         * Below is formula for generating check_offer_status_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be Offer Key.
                         *
                         */
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);

            progressDialog.dismiss();
            launchSdkUI(payuHashes);
        }
    }

    /**
     * This method adds the Payuhashes and other required params to intent and launches the PayuBaseActivity.java
     *
     * @param payuHashes it contains all the hashes generated from merchant server
     */
    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);

        //Lets fetch all the one click card tokens first
        fetchMerchantHashes(intent);
       //finish();

    }

    //TODO This method is used if integrating One Tap Payments

    /**
     * This method stores merchantHash and cardToken on merchant server.
     *
     * @param cardToken    card token received in transaction response
     * @param merchantHash merchantHash received in transaction response
     */
    private void storeMerchantHash(String cardToken, String merchantHash) {

        final String postParams = "merchant_key=" + AllKeys.PAYBIZ_KEY + "&user_credentials=" + userCredentials + "&card_token=" + cardToken + "&merchant_hash=" + merchantHash;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    //TODO Deploy a file on your server for storing cardToken and merchantHash nad replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/store_merchant_hash");
                    url = new URL(AllKeys.TAG_PAYMENT_HASH_GENERATION);


                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }


    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method fetches merchantHash and cardToken already stored on merchant server.
     */
    private void fetchMerchantHashes(final Intent intent) {
        // now make the api call.
        final String postParams = "merchant_key=" + AllKeys.PAYBIZ_KEY + "&user_credentials=" + userCredentials;
        final Intent baseActivityIntent = intent;
        new AsyncTask<Void, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());

                    HashMap<String, String> cardTokens = new HashMap<String, String>();
                    JSONArray oneClickCardsArray = response.getJSONArray("data");
                    int arrayLength;
                    if ((arrayLength = oneClickCardsArray.length()) >= 1) {
                        for (int i = 0; i < arrayLength; i++) {
                            cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                        }
                        return cardTokens;
                    }
                    // pass these to next activity

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> oneClickTokens) {
                super.onPostExecute(oneClickTokens);

                baseActivityIntent.putExtra(PayuConstants.ONE_CLICK_CARD_TOKENS, oneClickTokens);
                startActivityForResult(baseActivityIntent, PayuConstants.PAYU_REQUEST_CODE);
            }
        }.execute();
    }

    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method deletes merchantHash and cardToken from server side file.
     *
     * @param cardToken cardToken of card whose merchantHash and cardToken needs to be deleted from merchant server
     */
    private void deleteMerchantHash(String cardToken) {

        final String postParams = "card_token=" + cardToken;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/delete_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }

    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method prepares a HashMap of cardToken as key and merchantHash as value.
     *
     * @param merchantKey     merchant key used
     * @param userCredentials unique credentials of the user usually of the form key:userId
     */
    public HashMap<String, String> getAllOneClickHashHelper(String merchantKey, String userCredentials) {

        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials;
        HashMap<String, String> cardTokens = new HashMap<String, String>();

        try {
            //TODO Replace below url with your server side file url.
            URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

            byte[] postParamsByte = postParams.getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postParamsByte);

            InputStream responseInputStream = conn.getInputStream();
            StringBuffer responseStringBuffer = new StringBuffer();
            byte[] byteContainer = new byte[1024];
            for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                responseStringBuffer.append(new String(byteContainer, 0, i));
            }

            JSONObject response = new JSONObject(responseStringBuffer.toString());

            JSONArray oneClickCardsArray = response.getJSONArray("data");
            int arrayLength;
            if ((arrayLength = oneClickCardsArray.length()) >= 1) {
                for (int i = 0; i < arrayLength; i++) {
                    cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                }

            }
            // pass these to next activity

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardTokens;
    }

    //TODO This method is used only if integrating One Tap Payments

    /**
     * Returns a HashMap object of cardToken and one click hash from merchant server.
     * <p>
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * The function should return a cardToken and corresponding one click hash as a hashMap.
     *
     * @param userCreds a string giving the user credentials of user.
     * @return the Hash Map of cardToken and one Click hash.
     **/

    public HashMap<String, String> getAllOneClickHash(String userCreds) {
        // 1. GET http request from your server
        // GET params - merchant_key, user_credentials.
        // 2. In response we get a
        // this is a sample code for fetching one click hash from merchant server.
        return getAllOneClickHashHelper(AllKeys.PAYBIZ_KEY, userCreds);
    }

    //TODO This method is used only if integrating One Tap Payments

    public void getOneClickHash(String cardToken, String merchantKey, String userCredentials) {

    }


    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function save the oneClickHash corresponding to its cardToken
     *
     * @param cardToken    a string containing the card token
     * @param oneClickHash a string containing the one click hash.
     **/


    public void saveOneClickHash(String cardToken, String oneClickHash) {
        // 1. POST http request to your server
        // POST params - merchant_key, user_credentials,card_token,merchant_hash.
        // 2. In this POST method the oneclickhash is stored corresponding to card token in merchant server.
        // this is a sample code for storing one click hash on merchant server.

        storeMerchantHash(cardToken, oneClickHash);

    }

    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function delete’s the oneClickHash from the merchant server
     *
     * @param cardToken       a string containing the card token
     * @param userCredentials a string containing the user credentials.
     **/


    public void deleteOneClickHash(String cardToken, String userCredentials) {

        // 1. POST http request to your server
        // POST params  - merchant_hash.
        // 2. In this POST method the oneclickhash is deleted in merchant server.
        // this is a sample code for deleting one click hash from merchant server.

        deleteMerchantHash(cardToken);

    }
    /*Complete Payubiz online payment related code*/


}
