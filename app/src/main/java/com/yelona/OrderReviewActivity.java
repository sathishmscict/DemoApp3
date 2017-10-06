package com.yelona;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class OrderReviewActivity extends AppCompatActivity {
    private Context context = this;
    private String TAG = OrderReviewActivity.class.getSimpleName();
    private String ACTIVITYNAME;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();

    public ArrayList<String> list_cartItemsId = new ArrayList<String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private RecyclerView recyclerview_cartlist;
    private LinearLayout llmain;
    private Button btnContinueShopping;
    private LinearLayout llEmptyCart;
    private ArrayList<ProductData> list_Products = new ArrayList<ProductData>();
    private CartListRecyclerViewAdapter adapter;
    private Button btnCheckOut;
    private TextView txtTotal;
    private TextView txtPriceDetails;
    private Double totalPriceInRs = 0.00;
    private TextView txtPriceItemsCount;
    private TextView txtTotalPrice;
    private TextView txtGrandTotal;
    private TextView txtTotalMLMDiscount;
    private TextView txtTotalDeliveryCharges;

    private Integer TOTAL_PRICE = 0, TOTAL_DELIVERY_CHARGES = 0;
    private Double  TOTAL_MLM_DISCOUNT = 0.00,GRAND_TOTAL=0.00;


    private LinearLayout llBillling;

    private TextView txtShippingName, txtShippingAddress, txtShippingMobile;
    private Button btnChangeShippingAddress;

    private RadioGroup rdGrpPaymentMode;
    private RadioButton rdOnline, rdCOD;
    private LinearLayout llCOD;
    private TextInputLayout input_layout_edtOTP;
    private EditText edtOTP;
    private Button btnVerify;
    private TextView txtResendCode;
    private int VERIFICATION_CODE = 0;
    private String image_url;

    private boolean IsOutOfStock = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review);


        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("");

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


        // setTitle("My Cart");

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();

        try {
            ACTIVITYNAME = getIntent().getStringExtra("ActivityName");
        } catch (Exception e) {
            ACTIVITYNAME = "";
            e.printStackTrace();
        }


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        txtShippingName = (TextView) findViewById(R.id.txtShippingName);
        txtShippingAddress = (TextView) findViewById(R.id.txtShippingAddress);
        txtShippingMobile = (TextView) findViewById(R.id.txtShippingMobile);
        btnChangeShippingAddress = (Button) findViewById(R.id.btnChangeShippingAddress);

        rdGrpPaymentMode = (RadioGroup) findViewById(R.id.rdGrpPaymenMode);
        rdCOD = (RadioButton) findViewById(R.id.rdCOD);
        rdOnline = (RadioButton) findViewById(R.id.rdOnline);

        llCOD = (LinearLayout) findViewById(R.id.llCOD);
        llCOD.setVisibility(View.GONE);

        input_layout_edtOTP = (TextInputLayout) findViewById(R.id.input_layout_edtOTP);
        edtOTP = (EditText) findViewById(R.id.edtOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        txtResendCode = (TextView) findViewById(R.id.txtResendCode);


        rdGrpPaymentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (radioGroup.getCheckedRadioButtonId() == R.id.rdCOD) {

                    llCOD.setVisibility(View.VISIBLE);


                    SendSMSToUser();


                    //  sendSMSToUSer();


                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rdOnline) {
                    llCOD.setVisibility(View.GONE);
                }
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (VERIFICATION_CODE == Integer.parseInt(edtOTP.getText().toString())) {
                    txtResendCode.setText("Verified successfully");
                    txtResendCode.setClickable(false);

                    hideKeyboard();
                }


            }
        });

        txtResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();
                SendSMSToUser();
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


        txtShippingName.setText(userDetails.get(SessionManager.KEY_SHIPPING_NAME));
        txtShippingAddress.setText(userDetails.get(SessionManager.KEY_SHIPPING_ADDRESS));
        txtShippingMobile.setText(userDetails.get(SessionManager.KEY_SHIPPING_MOBILENO));


        recyclerview_cartlist = (RecyclerView) findViewById(R.id.recyclerview_cartlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerview_cartlist.setLayoutManager(layoutManager);

        // btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll);
        llmain = (LinearLayout) findViewById(R.id.llmain);
        llEmptyCart = (LinearLayout) findViewById(R.id.llemptyCart);

        llBillling = (LinearLayout) findViewById(R.id.llBillling);
        btnContinueShopping = (Button) findViewById(R.id.btnContinueShopping);
        btnCheckOut = (Button) findViewById(R.id.btnCheckOut);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtPriceDetails = (TextView) findViewById(R.id.txtPriceDetails);


        txtPriceItemsCount = (TextView) findViewById(R.id.txtPriceItemsCount);
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);

        txtGrandTotal = (TextView) findViewById(R.id.txtGrandTotal);
        txtTotalDeliveryCharges = (TextView) findViewById(R.id.txtTotalDeliveryCharge);
        txtTotalMLMDiscount = (TextView) findViewById(R.id.txtTotalMLMDiscont);



        Button btnEmptyCartContinue = (Button) findViewById(R.id.btnContinue);
        btnEmptyCartContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, NewDashBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NewDashBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (IsOutOfStock == false) {

                    if (userDetails.get(SessionManager.KEY_CHECKOUT_TYPE).equals("single"))
                    {
                        sessionmanager.setOrderTotalDetails(String.valueOf(TOTAL_PRICE), String.valueOf(GRAND_TOTAL), String.valueOf(TOTAL_DELIVERY_CHARGES), "1",String.valueOf(TOTAL_MLM_DISCOUNT));
                    }
                    else
                    {
                        sessionmanager.setOrderTotalDetails(String.valueOf(TOTAL_PRICE), String.valueOf(GRAND_TOTAL), String.valueOf(TOTAL_DELIVERY_CHARGES), String.valueOf(list_cartItemsId.size()),String.valueOf(TOTAL_MLM_DISCOUNT));
                    }



                    Intent intent = new Intent(context, ManagePaymentsActivity.class);
                    startActivity(intent);
                    finish();


                } else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderReviewActivity.this);
                    alertDialogBuilder.setTitle("Order Info");
                    alertDialogBuilder.setMessage("Some of the products are not available in stock,Please move to  wishlist or remove from the cart ");
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {

                                    dialog.cancel();


                                }
                            });


                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }

            }
        });

        //Get Cart Item details from sesion
        setTitleWithCount();

        getAllProductDetailsFromServer();
        getWalletBalance();


    }
    //onCrate completed

    /**
     * Get User wallet amount
     */
    private void getWalletBalance()
    {

        showDialog();

        String url_getWallet = AllKeys.WEBSITE + "getWalletBalance/" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetWalletBalance : " + url_getWallet);
        StringRequest str_getWalletBalance = new StringRequest(Request.Method.GET, url_getWallet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Responce Get Balance : " + response);


                sessionmanager.setWalletAmount(response);
                userDetails = sessionmanager.getSessionDetails();

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

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void SendSMSToUser() {
        Random R = new Random();
        if (VERIFICATION_CODE == 0) {
            VERIFICATION_CODE = R.nextInt(9999 - 1000) + 1000;
        }


        Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();

        //http://arham.dnsitexperts.com/yelona/index.php/welcome/sendSMS/9723613143/1234
        String url_SendSMS = AllKeys.WEBSITE + "sendSMS/" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "/" + VERIFICATION_CODE + "";
        StringRequest str_sendSmsRequest = new StringRequest(Request.Method.GET, url_SendSMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.contains("successfully")) {
                    Toast.makeText(context, "Please verify your code send to your mobile no.", Toast.LENGTH_SHORT).show();
                } else {
                    txtResendCode.performClick();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_sendSmsRequest);

    }

    private void setTitleWithCount()
    {

        userDetails = sessionmanager.getSessionDetails();
        try {

            //GRAND_TOTAL = TOTAL_PRICE.intValue() + TOTAL_DELIVERY_CHARGES;
            txtTotalPrice.setText("\u20b9 " + TOTAL_PRICE);
            NumberFormat formatter = null;
            try {
                 formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                String output = formatter.format(TOTAL_MLM_DISCOUNT);

                txtTotalMLMDiscount.setText("\u20b9 "+output);
            } catch (Exception e) {
                e.printStackTrace();
                txtTotalMLMDiscount.setText("\u20b9 0.00");
            }

            try {
                txtGrandTotal.setText("\u20b9 " + formatter.format(GRAND_TOTAL));
            } catch (Exception e) {
                e.printStackTrace();
            }


            //txtTotalMLMDiscount.setText("\u20b9 "+TOTAL_MLM_DISCOUNT);
            if (TOTAL_DELIVERY_CHARGES == 0) {
                txtTotalDeliveryCharges.setText("Free");
            } else {
                txtTotalDeliveryCharges.setText("\u20b9 " + TOTAL_DELIVERY_CHARGES);
            }

            if (userDetails.get(SessionManager.KEY_CARTITEMS_ID).equals("")) {
                setTitle("My Cart");
            } else {

                String all_cart_ids = userDetails.get(SessionManager.KEY_CARTITEMS_ID);
                all_cart_ids = all_cart_ids.replace(" ", "");
                list_cartItemsId = new ArrayList<>(Arrays.asList(all_cart_ids.split(",")));

                setTitle("My Cart(" + list_cartItemsId.size() + ")");

            }

            if (userDetails.get(SessionManager.KEY_CHECKOUT_TYPE).equals("single")) {
                txtPriceItemsCount.setText("Price (1 item)");
            } else {
                txtPriceItemsCount.setText("Price (" + list_cartItemsId.size() + " items)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setTitle("My Cart");
        }
    }

    private void getAllProductDetailsFromServer()
    {


        showDialog();


        String url_getProducts = AllKeys.WEBSITE + "getCartDataByUserid/" + userDetails.get(SessionManager.KEY_USERID) + "";


        Log.d(TAG, "URL get WishList: " + url_getProducts);
        StringRequest str_get_products = new StringRequest(Request.Method.GET, url_getProducts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAll Hot Deals Products : " + response);
                list_Products.clear();
                if (response.contains("product_id")) {

                    try {

                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject c = arr.getJSONObject(i);

                            if (userDetails.get(SessionManager.KEY_CHECKOUT_TYPE).equals("single")) {
                                if (userDetails.get(SessionManager.KEY_PRODUCT_ID).equals(c.getString(AllKeys.TAG_CHECKOUT_PRODUCTID))) {

                                    //ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHIPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), String.valueOf(c.getInt(AllKeys.TAG_PRICE) * c.getInt(AllKeys.TAG_QUANTITY)), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), String.valueOf(c.getInt(AllKeys.TAG_MRP) * c.getInt(AllKeys.TAG_QUANTITY)), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), c.getString(AllKeys.TAG_QUANTITY));
                                    ProductData prod = new ProductData(c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER), c.getString(AllKeys.TAG_CHECKOUT_PRODUCTID), c.getString(AllKeys.TAG_CHECKOUT_PRODUCT_NAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), String.valueOf(c.getInt(AllKeys.TAG_PRICE)), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), String.valueOf(c.getInt(AllKeys.TAG_MRP)), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), c.getString(AllKeys.TAG_QUANTITY), c.getString(AllKeys.TAG_MLM_DISCOUNT));


                                    //ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHOPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), AllKeys.RESOURSES + c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), c.getString(AllKeys.TAG_PRICE), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), c.getString(AllKeys.TAG_MRP), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY));
                                    totalPriceInRs = totalPriceInRs + Double.parseDouble(c.getString(AllKeys.TAG_PRICE));


                                    list_Products.add(prod);
                                    if (c.getInt(AllKeys.TAG_MLM_DISCOUNT) != 0) {
                                        //TOTAL_MLM_DISCOUNT =TOTAL_MLM_DISCOUNT+ (int)((c.getDouble(AllKeys.TAG_PRICE) *  c.getDouble(AllKeys.TAG_QUANTITY))*c.getInt(AllKeys.TAG_MLM_DISCOUNT)) / 100;
                                        if(Double.parseDouble(userDetails.get(SessionManager.KEY_WALLET_BALANCE)) >= TOTAL_MLM_DISCOUNT)
                                        {
                                            //TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT + (int) ((c.getDouble(AllKeys.TAG_PRICE) * c.getDouble(AllKeys.TAG_QUANTITY)) * c.getInt(AllKeys.TAG_MLM_DISCOUNT)) / 100;
                                            TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT + ((c.getDouble(AllKeys.TAG_PRICE) * c.getDouble(AllKeys.TAG_MLM_DISCOUNT)) / 100) * c.getDouble(AllKeys.TAG_QUANTITY);
                                        }

                                    }


                                    TOTAL_PRICE = TOTAL_PRICE + c.getInt(AllKeys.TAG_PRICE) * c.getInt(AllKeys.TAG_QUANTITY);
                                    TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES + c.getInt(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER);
                                    //MLM_DISCOUNT = MLM_DISCOUNT+(c.get);
                                }


                            } else {
                                //ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHIPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), String.valueOf(c.getInt(AllKeys.TAG_PRICE) * c.getInt(AllKeys.TAG_QUANTITY)), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), String.valueOf(c.getInt(AllKeys.TAG_MRP) * c.getInt(AllKeys.TAG_QUANTITY)), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), c.getString(AllKeys.TAG_QUANTITY));
                                ProductData prod = new ProductData(c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER), c.getString(AllKeys.TAG_CHECKOUT_PRODUCTID), c.getString(AllKeys.TAG_CHECKOUT_PRODUCT_NAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), String.valueOf(c.getInt(AllKeys.TAG_PRICE)), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), String.valueOf(c.getInt(AllKeys.TAG_MRP)), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), c.getString(AllKeys.TAG_QUANTITY), c.getString(AllKeys.TAG_MLM_DISCOUNT));


                                //ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHOPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), AllKeys.RESOURSES + c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), c.getString(AllKeys.TAG_PRICE), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), c.getString(AllKeys.TAG_MRP), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY));
                                totalPriceInRs = totalPriceInRs + Double.parseDouble(c.getString(AllKeys.TAG_PRICE));


                                list_Products.add(prod);

                                if (c.getInt(AllKeys.TAG_MLM_DISCOUNT) != 0) {
                                    TOTAL_MLM_DISCOUNT =TOTAL_MLM_DISCOUNT+ ((c.getDouble(AllKeys.TAG_PRICE) * c.getDouble(AllKeys.TAG_MLM_DISCOUNT)) / 100)*c.getDouble(AllKeys.TAG_QUANTITY);
                                }


                                TOTAL_PRICE = TOTAL_PRICE + c.getInt(AllKeys.TAG_PRICE) * c.getInt(AllKeys.TAG_QUANTITY);
                                TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES + c.getInt(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER);
                                //MLM_DISCOUNT = MLM_DISCOUNT+(c.get);

                            }


                        }

                        GRAND_TOTAL = TOTAL_PRICE + TOTAL_DELIVERY_CHARGES -TOTAL_MLM_DISCOUNT;
                        setTitleWithCount();


                        adapter = new CartListRecyclerViewAdapter(context);
                        recyclerview_cartlist.setAdapter(adapter);
                        txtTotal.setText("Rs. " + totalPriceInRs.intValue());

                        llBillling.setVisibility(View.VISIBLE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                    llmain.setVisibility(View.VISIBLE);
                    llEmptyCart.setVisibility(View.GONE);
                } else {
                    llmain.setVisibility(View.GONE);
                    llEmptyCart.setVisibility(View.VISIBLE);
                    sessionmanager.setCartItemsIdDetails("");
                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError)

                {
                    hideDialog();

                } else {
                    getAllProductDetailsFromServer();

                }

                Log.d(TAG, "Error in get WishList : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = null;
            try {
                i = new Intent(context, Class.forName(context.getPackageName() + "." + userDetails.get(SessionManager.KEY_ACTIVITY_NAME)));
                // i.putExtra("ActivityName",ACTIVITYNAME);
                startActivity(i);
                finish();
            } catch (ClassNotFoundException e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error in Converting Class : " + e.getMessage());
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = null;
        try {
            i = new Intent(context, Class.forName(context.getPackageName() + "." + userDetails.get(SessionManager.KEY_ACTIVITY_NAME)));
            //i.putExtra("ActivityName",ACTIVITYNAME);
            startActivity(i);
            finish();
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error in Converting Class : " + e.getMessage());
            e.printStackTrace();
        }


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


   /* public class CartListRecyclerViewAdapter extends RecyclerView.Adapter<OrderReviewActivity.CartListRecyclerViewAdapter.MyViewHolder> {


        private final Context _context;
        private final ArrayList<ProductData> list_NewProcuts;
        private final LayoutInflater inflater;
        private final SessionManager sessionmanager;

        private HashMap<String, String> userDetails = new HashMap<String, String>();
        //private String TAG = OrderReviewActivity.CartListRecyclerViewAdapter.class.getSimpleName();

        public CartListRecyclerViewAdapter(Context context, ArrayList<ProductData> listNewProduct) {
            this._context = context;
            this.list_NewProcuts = listNewProduct;
            inflater = LayoutInflater.from(context);

            sessionmanager = new SessionManager(context);
            userDetails = sessionmanager.getSessionDetails();


        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final RatingBar ratingBar;
            private final TextView txtPrice, txtName, txtDelete, txtProductMRP, txtOffer, txtAdd, txtRemove, txtQuantity, txtStockStatus, txtDeliveryCharge;
            private final ImageView imgItem;
            private final CardView crdProduct;
            private final Button btnWishlist, btnRemoveFromCart;

            public MyViewHolder(View itemView) {
                super(itemView);
                crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
                imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
                txtName = (TextView) itemView.findViewById(R.id.txtItemName);
                txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
                ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
                txtDelete = (TextView) itemView.findViewById(R.id.txtDelete);
                txtProductMRP = (TextView) itemView.findViewById(R.id.txtProductMRP);
                txtDeliveryCharge = (TextView) itemView.findViewById(R.id.txtDeliveryCharge);

                txtOffer = (TextView) itemView.findViewById(R.id.txtOffer);
                btnWishlist = (Button) itemView.findViewById(R.id.btnWishlist);
                btnRemoveFromCart = (Button) itemView.findViewById(R.id.btnRemove);


                txtAdd = (TextView) itemView.findViewById(R.id.txtAdd);
                txtRemove = (TextView) itemView.findViewById(R.id.txtRemove);
                txtQuantity = (TextView) itemView.findViewById(R.id.txtCounter);
                txtStockStatus = (TextView) itemView.findViewById(R.id.txtStockStatus);



          *//*  if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard") || userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("category")) {
                // sets width to wrap content and height to 10 dp ->
                crdProduct.setLayoutParams(new CardView.LayoutParams(
                        CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.WRAP_CONTENT));
                crdProduct.setContentPadding(4, 4, 4, 4);
                crdProduct.setCardElevation(8);
            } else {
                crdProduct.setCardElevation(4);
            }*//*


            }
        }

        @Override
        public OrderReviewActivity.CartListRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = inflater.inflate(R.layout.row_single_product_cartlist, parent, false);

            OrderReviewActivity.CartListRecyclerViewAdapter.MyViewHolder viewFolder = new OrderReviewActivity.CartListRecyclerViewAdapter.MyViewHolder(v);
            return viewFolder;
        }

        @Override
        public void onBindViewHolder(final OrderReviewActivity.CartListRecyclerViewAdapter.MyViewHolder holder, final int position) {

            final ProductData pd = list_NewProcuts.get(position);

            holder.txtName.setText(pd.getProductname());
            // holder.txtPrice.setText(Html.fromHtml("<b>" + pd.getMrp() + "</b>" + " <del>" + pd.getPrice() + "</del>"));

            int qty_prod = Integer.parseInt(pd.getQuantity());
            final int unitprice = (int) (Double.parseDouble(pd.getPrice()) * qty_prod);
            final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) * qty_prod);

            pd.setMrp(String.valueOf(mrpprice));
            pd.setPrice(String.valueOf(unitprice));

            list_NewProcuts.set(position, pd);

            holder.txtPrice.setText("\u20b9 " + unitprice);
            holder.txtProductMRP.setText("\u20b9 " + mrpprice);

            if (pd.getShippingcost().equals("0")) {
                holder.txtDeliveryCharge.setText("Free Delivery");
            } else {
                holder.txtDeliveryCharge.setText("Delivery Charge : " + pd.getShippingcost());
            }


            //   pd.setPrice(String.valueOf(unitprice));
            //  pd.setMrp(String.valueOf(mrpprice));

            //  list_NewProcuts.set(position,pd);


            //Check Product availability
            if (pd.getInventory().equals("0")) {
                //SetVisible out of stock
                holder.txtStockStatus.setVisibility(View.VISIBLE);
                holder.txtStockStatus.setText("Out Of Stock");

            } else {
                //SetInVisible out of stock
                holder.txtStockStatus.setVisibility(View.GONE);
            }

            holder.txtQuantity.setText(pd.getQuantity());


            holder.txtRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    try {
                        int qty_prod = Integer.parseInt(pd.getQuantity());

                        if (qty_prod > 1) {
                            showDialog();


                            *//**
     * Update Cart Details send to server
     *//*
                            final int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty_prod);
                            final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty_prod);

                            final int qty = --qty_prod;
                            String url_addtowishlist = AllKeys.WEBSITE + "manageCart/update/" + userDetails.get(SessionManager.KEY_USERID) + "/" + pd.getProductid() + "/" + qty + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "";
                            Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
                            final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d(TAG, "Response ManageWishList : " + response);

                                    if (response.equals("1")) {


                                        //Toast.makeText(_context, "Cart details updated", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "cart details successfully updated");


                                        //--qty;

                                        holder.txtQuantity.setText(" " + qty + " ");
                                        pd.setQuantity(String.valueOf(qty));


                                        //Double price = Double.parseDouble(pd.getPrice()) * Double.parseDouble(String.valueOf(qty));
                                        //int unitprice = (int) (Double.parseDouble(pd.getPrice())/qty);

                                        int price = unitprice * qty;
                                        holder.txtPrice.setText("\u20b9 " + String.valueOf(price));
                                        //Double mrp = Double.parseDouble(pd.getMrp()) * Double.parseDouble(String.valueOf(qty));
                                        int mrp = mrpprice * qty;
                                        holder.txtProductMRP.setText("\u20b9 " + String.valueOf(mrp));


                                        pd.setPrice(String.valueOf(price));
                                        pd.setMrp(String.valueOf(mrp));
                                        pd.setQuantity(String.valueOf(qty));
                                        Log.d(TAG, "Remove Position :" + position);
                                        list_NewProcuts.set(position, pd);


                                        Toast.makeText(_context, "You've changed  '" + pd.getProductname() + "' quantity to '" + qty + "'", Toast.LENGTH_SHORT).show();


                                        TOTAL_PRICE = TOTAL_PRICE - (Integer.parseInt(pd.getPrice()) / qty);
                                        GRAND_TOTAL = GRAND_TOTAL - (Integer.parseInt(pd.getPrice()) / qty);
                                        //TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES - Integer.parseInt(pd.getShippingcost());


                                        setTitleWithCount();


                                    } else {
                                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                                    }
                                    hideDialog();


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {


                                    Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                                    hideDialog();
                                    Toast.makeText(OrderReviewActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                                    hideDialog();
                                }
                            });
                            MyApplication.getInstance().addToRequestQueue(str_addToCartList);


                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.txtAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int qty_prod = Integer.parseInt(pd.getQuantity());
                        int max_quantity = 1;
                        if (qty_prod >= 1 && qty_prod < Integer.parseInt(pd.getInventory())) {
                            showDialog();

                        *//*int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty);
                        int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty);

                        ++qty;
                        holder.txtQuantity.setText(" " + qty + " ");
                                               int price = unitprice * qty;
                        holder.txtPrice.setText("\u20b9 " + String.valueOf(price));
                                               int mrp = mrpprice * qty;
                        holder.txtProductMRP.setText("\u20b9 " + String.valueOf(mrp));


                        pd.setPrice(String.valueOf(price));
                        pd.setMrp(String.valueOf(mrp));
                        pd.setQuantity(String.valueOf(qty));
                        list_NewProcuts.set(position, pd);*//*

                            */

    /**
     * Update Cart Details send to server
     *//*
                            final int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty_prod);
                            final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty_prod);

                            final int qty = ++qty_prod;
                            String url_addtowishlist = AllKeys.WEBSITE + "manageCart/update/" + userDetails.get(SessionManager.KEY_USERID) + "/" + pd.getProductid() + "/" + qty + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "";
                            Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
                            final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d(TAG, "Response ManageWishList : " + response);

                                    if (response.equals("1")) {


                                        // Toast.makeText(_context, "Cart details updated", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "cart details successfully updated");

                                        holder.txtQuantity.setText(" " + qty + " ");
                                        int price = unitprice * qty;
                                        holder.txtPrice.setText("\u20b9 " + String.valueOf(price));
                                        int mrp = mrpprice * qty;
                                        holder.txtProductMRP.setText("\u20b9 " + String.valueOf(mrp));


                                        pd.setPrice(String.valueOf(price));
                                        pd.setMrp(String.valueOf(mrp));
                                        pd.setQuantity(String.valueOf(qty));
                                        Log.d(TAG, "Add Position :" + position);
                                        list_NewProcuts.set(position, pd);

                                        Toast.makeText(_context, "You've changed  '" + pd.getProductname() + "' quantity to '" + qty + "'", Toast.LENGTH_SHORT).show();

                                        GRAND_TOTAL = GRAND_TOTAL + (Integer.parseInt(pd.getPrice()) / qty);
                                        TOTAL_PRICE = TOTAL_PRICE + (Integer.parseInt(pd.getPrice()) / qty);
                                        // TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES + Integer.parseInt(pd.getShippingcost());

                                        setTitleWithCount();


                                    } else {
                                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                                    }
                                    hideDialog();


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    hideDialog();
                                    Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                                    Toast.makeText(OrderReviewActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                                }
                            });
                            MyApplication.getInstance().addToRequestQueue(str_addToCartList);

                        } else {

                            //Toast.makeText(_context, "Sorry, you cannot cart more than " + pd.getInventory() + " quantity ", Toast.LENGTH_SHORT).show();

                            Toast.makeText(_context, "We're sorry! Only " + pd.getInventory() + " units of " + pd.getProductname() + " for each customer.", Toast.LENGTH_LONG).show();
                            //Snackbar.make(coordinatorLayout,""+getString(R.string.maxquantity),Snackbar.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                }
            });


            holder.btnRemoveFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dbhandler db = new dbhandler(_context);
                    SQLiteDatabase sd = db.getReadableDatabase();
                    sd = db.getWritableDatabase();

                    // TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES-(Integer.parseInt(pd.getShippingcost()));
                    //  GRAND_TOTAL = GRAND_TOTAL-(Integer.parseInt(pd.getPrice()));
                    //TOTAL_PRICE = TOTAL_PRICE-(Integer.parseInt(pd.getPrice()));


                    holder.btnRemoveFromCart.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.anim_item));

                    removeCartDetailsToServer(pd.getProductid(), "remove", position, pd);


                }
            });
            holder.btnWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //holder.btnRemove.performClick();

                    holder.btnWishlist.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.anim_item));
                    dbhandler db = new dbhandler(_context);


                    sendCartToWishlistDetailsToServcer(pd.getProductid(), dbhandler.convertEncodedString(db.getDateTime()), pd.getPrice());


                }
            });


            try {

                String image_url = pd.getImage_url();
                if (image_url.contains("images") && image_url.contains("__w-200")) {
                    int pos = image_url.lastIndexOf("/");
                    //pos = image_url.inde

                                    *//*if (image_url.contains("600")) {
                                        image_url = image_url.substring(pos, image_url.length());
                                        image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                                    }
                                     else*//*
                    if (image_url.contains("400")) {

                        image_url = image_url.substring(pos, image_url.length());
                        image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                    } else {
                        image_url = image_url.substring(pos, image_url.length());
                        image_url = AllKeys.RESOURSES + "uploads/images/w200" + image_url;
                    }


                    // Log.d(TAG, "New Url : " + image_url);
                } else {
                    image_url = AllKeys.RESOURSES + image_url;
                }

                Glide.with(_context).load(image_url).crossFade().into(holder.imgItem);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Double offer = Double.parseDouble(pd.getPrice()) * 100 / Double.parseDouble(pd.getMrp());
            int percentage = 100 - offer.intValue();
            if (percentage == 0 || percentage < 0) {
                holder.txtOffer.setVisibility(View.GONE);
            } else {
                //  holder.txtOffer.setVisibility(View.GONE);
                holder.txtOffer.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.fadein));

                if (percentage <= 9) {
                    holder.txtOffer.setText(" " + percentage + " % off");
                } else {
                    holder.txtOffer.setText("" + percentage + " % off");
                }
            }

            holder.imgItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Toast.makeText(_context, "Name : " + pd.getProductname() + " Id : " + pd.getProductid(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(_context,
                                SingleItemActivity.class);
                        sessionmanager.setProductDetails(pd.getProductid(), "1");


                        //intent.putExtra("ProductId", pd.getProductid());
                        intent.putExtra("ActivityName", "CheckoutActivity");
                        _context.startActivity(intent);
                        //finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        }

        private void sendCartToWishlistDetailsToServcer(final String ProductId, final String dateTime, final String ProductPrice) {

            String url_movecarttowishlist = AllKeys.WEBSITE + "moveCartToWishList/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId + "/" + dateTime + "/" + ProductPrice + "";

            Log.d(TAG, "URL  MoveCartToWishList " + url_movecarttowishlist);

            StringRequest str_moveCartToWishList = new StringRequest(Request.Method.GET, url_movecarttowishlist, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response.equals("1")) {
                        Toast.makeText(_context, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error in moveCartToWishList : " + error.getMessage());

                    sendCartToWishlistDetailsToServcer(ProductId, dateTime, ProductPrice);
                }
            });

            MyApplication.getInstance().addToRequestQueue(str_moveCartToWishList);


        }



        private void removeCartDetailsToServer(final String ProductId, final String type, final int position, final ProductData pd) {


            showDialog();
            String url_addtowishlist = AllKeys.WEBSITE + "manageCart/" + type + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId;

            Log.d(TAG, "URL AddToCartList : " + url_addtowishlist);
            final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d(TAG, "Response ManageCartList : " + response);

                    if (response.equals("1")) {


                        list_NewProcuts.remove(position);
                        Toast.makeText(_context, "Item deleted from  cart", Toast.LENGTH_SHORT).show();
                        notifyItemRemoved(position);
                        // notifyDataSetChanged();
                        try {
                            list_cartItemsId.remove(list_cartItemsId.indexOf(pd.getProductid()));
                            if (list_cartItemsId.size() == 0) {
                                Intent intent = new Intent(_context, CheckoutActivity.class);
                                _context.startActivity(intent);
                                sessionmanager.setCartItemsIdDetails("");
                            } else {
                                String data = list_cartItemsId.toString();

                                data = data.substring(1, data.length() - 1);
                                sessionmanager.setCartItemsIdDetails(data);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        GRAND_TOTAL = GRAND_TOTAL - Integer.parseInt(pd.getPrice());
                        TOTAL_PRICE = TOTAL_PRICE - Integer.parseInt(pd.getPrice());
                        TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES - Integer.parseInt(pd.getShippingcost());
                        GRAND_TOTAL = GRAND_TOTAL - Integer.parseInt(pd.getShippingcost());


                        setTitleWithCount();


                    } else {
                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                    }
                    hideDialog();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    hideDialog();
                    if (error instanceof ServerError) {
                        hideDialog();


                    } else {
                        removeCartDetailsToServer(ProductId, type, position, pd);
                    }
                    Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                }
            });
            MyApplication.getInstance().addToRequestQueue(str_addToCartList);

        }

        @Override
        public int getItemCount() {
            return list_NewProcuts.size();
        }
    }*/

    // public class CartListRecyclerViewAdapter extends RecyclerView.Adapter<OrderReviewActivity.CartListRecyclerViewAdapter.MyViewHolder>
    public class CartListRecyclerViewAdapter extends RecyclerView.Adapter<CartListRecyclerViewAdapter.MyViewHolder>
    {


        private final Context _context;
        // private final ArrayList<ProductData> list_NewProcuts;
        private final LayoutInflater inflater;
        private final SessionManager sessionmanager;

        private HashMap<String, String> userDetails = new HashMap<String, String>();
        private String TAG = CartListRecyclerViewAdapter.class.getSimpleName();

        public CartListRecyclerViewAdapter(Context context) {
            this._context = context;
            //this.list_NewProcuts = listNewProduct;
            inflater = LayoutInflater.from(context);

            sessionmanager = new SessionManager(context);
            userDetails = sessionmanager.getSessionDetails();


        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final RatingBar ratingBar;
            private final TextView txtTotalItemPrice, txtPrice;
            private final TextView txtProductMRPSingle, txtProductMRPTotal;

            private final TextView txtName, txtDelete, txtOffer, txtAdd, txtRemove, txtQuantity, txtStockStatus, txtDeliveryCharge,txtMLMDiscount;
            private final ImageView imgItem;
            private final CardView crdProduct;
            private final Button btnWishlist, btnRemoveFromCart;

            public MyViewHolder(View itemView) {
                super(itemView);
                crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
                imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
                txtName = (TextView) itemView.findViewById(R.id.txtItemName);
                txtTotalItemPrice = (TextView) itemView.findViewById(R.id.txtTotalPrice);

                txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);

                ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
                txtDelete = (TextView) itemView.findViewById(R.id.txtDelete);
                txtProductMRPTotal = (TextView) itemView.findViewById(R.id.txtProductMRP);
                txtDeliveryCharge = (TextView) itemView.findViewById(R.id.txtDeliveryCharge);

                txtOffer = (TextView) itemView.findViewById(R.id.txtOffer);
                btnWishlist = (Button) itemView.findViewById(R.id.btnWishlist);
                btnRemoveFromCart = (Button) itemView.findViewById(R.id.btnRemove);


                txtAdd = (TextView) itemView.findViewById(R.id.txtAdd);
                txtRemove = (TextView) itemView.findViewById(R.id.txtRemove);
                txtQuantity = (TextView) itemView.findViewById(R.id.txtCounter);
                txtStockStatus = (TextView) itemView.findViewById(R.id.txtStockStatus);

                txtProductMRPSingle = (TextView) itemView.findViewById(R.id.txtProductMRPSingle);
                txtMLMDiscount = (TextView)itemView.findViewById(R.id.txtMLMDiscount);







          /*  if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard") || userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("category")) {
                // sets width to wrap content and height to 10 dp ->
                crdProduct.setLayoutParams(new CardView.LayoutParams(
                        CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.WRAP_CONTENT));
                crdProduct.setContentPadding(4, 4, 4, 4);
                crdProduct.setCardElevation(8);
            } else {
                crdProduct.setCardElevation(4);
            }*/


            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = inflater.inflate(R.layout.row_single_product_cartlist, parent, false);

            MyViewHolder viewFolder = new MyViewHolder(v);
            return viewFolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            ProductData pdd = list_Products.get(position);

            holder.txtName.setText(pdd.getProductname());
            // holder.txtPrice.setText(Html.fromHtml("<b>" + pd.getMrp() + "</b>" + " <del>" + pd.getPrice() + "</del>"));

            int qty_prod = Integer.parseInt(pdd.getQuantity());
            int prod_price = Integer.parseInt(pdd.getPrice());
            int prod_mrp = Integer.parseInt(pdd.getMrp());
            Double prod_price_d = Double.parseDouble(pdd.getPrice());
            Double prod_mrp_d = Double.parseDouble(pdd.getMrp());
            final int unitprice = (int) (Double.parseDouble(pdd.getPrice()) * qty_prod);
            final int mrpprice = (int) (Double.parseDouble(pdd.getMrp()) * qty_prod);
            final int deliveryprice = (int) (Double.parseDouble(pdd.getShippingcost()) * qty_prod);
            final double mlmDisocunt =  ((Double.parseDouble(pdd.getPrice()) * Double.parseDouble(pdd.getMlmDiscount()))/100)*qty_prod;


            holder.txtPrice.setText(String.valueOf(prod_price_d));
            holder.txtProductMRPSingle.setText(String.valueOf(prod_mrp_d));

            if(mlmDisocunt == 0)
            {

                holder.txtMLMDiscount.setText("\u20b9 0");
                holder.txtMLMDiscount.setVisibility(View.GONE);


            }
            else
            {
                holder.txtMLMDiscount.setVisibility(View.VISIBLE);
                holder.txtMLMDiscount.setText("\u20b9 "+String.valueOf(mlmDisocunt));
            }


            // pdd.setMrp(String.valueOf(mrpprice));
            // pdd.setPrice(String.valueOf(unitprice));
            // pdd.setShippingcost(String.valueOf(deliveryprice));

            // list_Products.set(position, pdd);
            final ProductData pd = list_Products.get(position);

            holder.txtTotalItemPrice.setText("\u20b9 " + unitprice);
            holder.txtProductMRPTotal.setText("\u20b9 " + mrpprice);

            if (deliveryprice == 0) {
                holder.txtDeliveryCharge.setText("Free Delivery");
            } else {
                holder.txtDeliveryCharge.setText("Delivery Charge : " + deliveryprice);
            }


            //   pd.setPrice(String.valueOf(unitprice));
            //  pd.setMrp(String.valueOf(mrpprice));

            //  list_NewProcuts.set(position,pd);


            //Check Product availability
            if (pd.getInventory().equals("0")) {
                //SetVisible out of stock
                holder.txtStockStatus.setVisibility(View.VISIBLE);
                holder.txtStockStatus.setText("Out Of Stock");
                IsOutOfStock = true;

            } else {
                //SetInVisible out of stock
                holder.txtStockStatus.setVisibility(View.GONE);
            }

            holder.txtQuantity.setText(pd.getQuantity());


            holder.txtRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    try {
                        int qty_prod = Integer.parseInt(pd.getQuantity());

                        if (qty_prod > 1) {
                            showDialog();


                            /**
                             * Update Cart Details send to server
                             */
                            /*final int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty_prod);
                            final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty_prod);*/

                            final int unitprice = (int) (Double.parseDouble(pd.getPrice()));
                            final int mrpprice = (int) (Double.parseDouble(pd.getMrp()));
                            final double mlmdiscoiunt = Double.parseDouble(pd.getMlmDiscount());
                            int shippingcost;
                            try {
                                shippingcost = (int) (Double.parseDouble(pd.getShippingcost()) / qty_prod);
                            } catch (NumberFormatException e) {
                                shippingcost = 0;
                                e.printStackTrace();
                            }

                            final int qty = --qty_prod;
                            String url_addtowishlist = AllKeys.WEBSITE + "manageCart/update/" + userDetails.get(SessionManager.KEY_USERID) + "/" + pd.getProductid() + "/" + qty + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "";
                            Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
                            final int finalShippingcost = shippingcost;
                            final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d(TAG, "Response ManageWishList : " + response);

                                    if (response.equals("1")) {


                                        //Toast.makeText(_context, "Cart details updated", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "cart details successfully updated");


                                        //--qty;

                                        holder.txtQuantity.setText(" " + qty + " ");
                                        // pd.setQuantity(String.valueOf(qty));


                                        //Double price = Double.parseDouble(pd.getPrice()) * Double.parseDouble(String.valueOf(qty));
                                        //int unitprice = (int) (Double.parseDouble(pd.getPrice())/qty);

                                        int price = unitprice * qty;
                                        holder.txtTotalItemPrice.setText("\u20b9 " + String.valueOf(price));
                                        //Double mrp = Double.parseDouble(pd.getMrp()) * Double.parseDouble(String.valueOf(qty));
                                        int mrp = mrpprice * qty;
                                        holder.txtProductMRPTotal.setText("\u20b9 " + String.valueOf(mrp));

                                        double mlm = ((unitprice * mlmdiscoiunt) / 100) * qty;


                                        if (String.valueOf(mlm).equals("0.00") || String.valueOf(mlm).equals("0")) {
                                            holder.txtMLMDiscount.setText("\u20b9 0");
                                        } else {
                                            holder.txtMLMDiscount.setText("\u20b9 " + String.valueOf(mlm));

                                        }


                                        int shippingcost_total = finalShippingcost * qty;

                                        if (shippingcost_total == 0) {
                                            holder.txtDeliveryCharge.setText("Free Delivery");
                                        } else {
                                            holder.txtDeliveryCharge.setText("Delivery Charge : " + shippingcost_total);
                                        }


                                        pd.setPrice(String.valueOf(unitprice));
                                        pd.setMrp(String.valueOf(mrpprice));
                                        pd.setQuantity(String.valueOf(qty));
                                        pd.setShippingcost(String.valueOf(finalShippingcost));
                                        Log.d(TAG, "Remove Position :" + position);
                                        list_Products.set(position, pd);


                                        Toast.makeText(_context, "You've changed  '" + pd.getProductname() + "' quantity to '" + qty + "'", Toast.LENGTH_SHORT).show();


                                        TOTAL_PRICE = TOTAL_PRICE - unitprice;
                                        GRAND_TOTAL = GRAND_TOTAL - unitprice;
                                        GRAND_TOTAL = GRAND_TOTAL + (mlm / qty);



                                        if (Double.parseDouble(userDetails.get(SessionManager.KEY_WALLET_BALANCE)) >= TOTAL_MLM_DISCOUNT) {

                                            Double currentDiscount = (mlm / qty);
                                            Double differenceAmount = Double.parseDouble(userDetails.get(SessionManager.KEY_WALLET_BALANCE)) - TOTAL_MLM_DISCOUNT;

                                            if (differenceAmount > currentDiscount) {
                                                TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT - (mlm / qty);
                                            } else {
                                                TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT - differenceAmount;
                                            }


                                        }


                                        // TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT - (mlm / qty);

                                        try {

                                            TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES - finalShippingcost;
                                            GRAND_TOTAL = GRAND_TOTAL - finalShippingcost;
                                        } catch (Exception e) {
                                            Log.d(TAG, "Error in Convert ShippingCost : " + e.getMessage());

                                        }

                                        notifyDataSetChanged();
                                        setTitleWithCount();


                                    } else {
                                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                                    }
                                    hideDialog();


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {


                                    Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                                    hideDialog();
                                    Toast.makeText(OrderReviewActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                                    hideDialog();
                                }
                            });
                            MyApplication.getInstance().addToRequestQueue(str_addToCartList);


                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.txtAdd.setOnClickListener
                    (
                            new View.OnClickListener()

                            {
                                @Override
                                public void onClick(View view) {
                                    try
                                    {
                                        int qty_prod = Integer.parseInt(pd.getQuantity());

                                        if (qty_prod >= 1 && qty_prod < Integer.parseInt(pd.getInventory())) {
                                            showDialog();

                                            /**
                                             * Update Cart Details send to server
                                             */
                          /*  final int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty_prod);
                            final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty_prod);*/
                                            final int unitprice = (int) (Double.parseDouble(pd.getPrice()));
                                            final int mrpprice = (int) (Double.parseDouble(pd.getMrp()));
                                            final double mlmdiscoiunt = Double.parseDouble(pd.getMlmDiscount());
                                            int shippingcost;
                                            try {
                                                shippingcost = (int) (Double.parseDouble(pd.getShippingcost()) / qty_prod);
                                            } catch (NumberFormatException e) {
                                                shippingcost = 0;
                                                e.printStackTrace();
                                            }

                                            final int qty = ++qty_prod;
                                            String url_addtowishlist = AllKeys.WEBSITE + "manageCart/update/" + userDetails.get(SessionManager.KEY_USERID) + "/" + pd.getProductid() + "/" + qty + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "";
                                            Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
                                            final int finalShippingcost = shippingcost;
                                            final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    Log.d(TAG, "Response ManageWishList : " + response);

                                                    if (response.equals("1")) {


                                                        // Toast.makeText(_context, "Cart details updated", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "cart details successfully updated");

                                                        holder.txtQuantity.setText(" " + qty + " ");
                                                        int price = unitprice * qty;
                                                        holder.txtTotalItemPrice.setText("\u20b9 " + String.valueOf(price));
                                                        int mrp = mrpprice * qty;
                                                        holder.txtProductMRPTotal.setText("\u20b9 " + String.valueOf(mrp));
                                                        int shippingcost_total = finalShippingcost * qty;

                                                        double mlm = ((unitprice * mlmdiscoiunt) / 100) * qty;

                                                        if (String.valueOf(mlm).equals("0.00") || String.valueOf(mlm).equals("0")) {
                                                            holder.txtMLMDiscount.setText("\u20b9 0");
                                                        } else {
                                                            holder.txtMLMDiscount.setText("\u20b9 " + String.valueOf(mlm));

                                                        }


                                                        if (shippingcost_total == 0) {
                                                            holder.txtDeliveryCharge.setText("Free Delivery");
                                                        } else {
                                                            holder.txtDeliveryCharge.setText("Delivery Charge : " + shippingcost_total);
                                                        }


                                                        pd.setPrice(String.valueOf(unitprice));
                                                        pd.setMrp(String.valueOf(mrpprice));
                                                        pd.setQuantity(String.valueOf(qty));
                                                        pd.setShippingcost(String.valueOf(finalShippingcost));
                                                        //pd.setMlmDiscount(String.valueOf(finalShippingcost));
                                                        Log.d(TAG, "Add Position :" + position);
                                                        list_Products.set(position, pd);

                                                        Toast.makeText(_context, "You've changed  '" + pd.getProductname() + "' quantity to '" + qty + "'", Toast.LENGTH_SHORT).show();

                                        /*GRAND_TOTAL = GRAND_TOTAL + (Integer.parseInt(pd.getPrice()) / qty);
                                        TOTAL_PRICE = TOTAL_PRICE + (Integer.parseInt(pd.getPrice()) / qty);
                                        try {
                                            TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES + (Integer.parseInt(pd.getShippingcost()) / qty);

                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }*/
                                                        GRAND_TOTAL = GRAND_TOTAL + unitprice;
                                                        GRAND_TOTAL = (GRAND_TOTAL - (mlm / qty));
                                                        TOTAL_PRICE = TOTAL_PRICE + unitprice;

                                                        if (Double.parseDouble(userDetails.get(SessionManager.KEY_WALLET_BALANCE)) >= TOTAL_MLM_DISCOUNT) {

                                                            Double currentDiscount = (mlm / qty);
                                                            Double differenceAmount = Double.parseDouble(userDetails.get(SessionManager.KEY_WALLET_BALANCE)) - TOTAL_MLM_DISCOUNT;

                                                            if (differenceAmount > currentDiscount) {
                                                                TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT + (mlm / qty);
                                                            } else {
                                                                TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT + differenceAmount;
                                                            }


                                                        }


                                                        try {
                                                            TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES + (finalShippingcost);
                                                            GRAND_TOTAL = GRAND_TOTAL + (finalShippingcost);
                                                        } catch (NumberFormatException e) {
                                                            e.printStackTrace();
                                                        }


                                                        notifyDataSetChanged();

                                                        setTitleWithCount();


                                                    } else {
                                                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                                                    }
                                                    hideDialog();


                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                    hideDialog();
                                                    Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                                                    Toast.makeText(OrderReviewActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            MyApplication.getInstance().addToRequestQueue(str_addToCartList);

                                        } else {

                                            //Toast.makeText(_context, "Sorry, you cannot cart more than " + pd.getInventory() + " quantity ", Toast.LENGTH_SHORT).show();

                                            Toast.makeText(_context, "We're sorry! Only " + pd.getInventory() + " units of " + pd.getProductname() + " for each customer.", Toast.LENGTH_LONG).show();
                                            //Snackbar.make(coordinatorLayout,""+getString(R.string.maxquantity),Snackbar.LENGTH_LONG).show();
                                        }
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                                        hideDialog();
                                    }
                                }
                            });


            holder.btnRemoveFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dbhandler db = new dbhandler(_context);
                    // SQLiteDatabase sd = db.getReadableDatabase();
                    // sd = db.getWritableDatabase();

                    // TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES-(Integer.parseInt(pd.getShippingcost()));
                    //  GRAND_TOTAL = GRAND_TOTAL-(Integer.parseInt(pd.getPrice()));
                    //TOTAL_PRICE = TOTAL_PRICE-(Integer.parseInt(pd.getPrice()));


                    try {
                        holder.btnRemoveFromCart.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.anim_item));

                        removeCartDetailsToServer(pd.getProductid(), "remove", position, pd);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }


                }
            });
            holder.btnWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //holder.btnRemove.performClick();

                    holder.btnWishlist.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.anim_item));
                    dbhandler db = new dbhandler(_context);


                    sendCartToWishlistDetailsToServcer(pd.getProductid(), dbhandler.convertEncodedString(db.getDateTime()), pd.getPrice(), pd);


                }
            });


            try {

                image_url = pd.getImage_url();
                if (image_url.contains("images") && image_url.contains("__w-200")) {
                    int pos = image_url.lastIndexOf("/");
                    //pos = image_url.inde

                                    /*if (image_url.contains("600")) {
                                        image_url = image_url.substring(pos, image_url.length());
                                        image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                                    }
                                     else*/
                    if (image_url.contains("400")) {

                        image_url = image_url.substring(pos, image_url.length());
                        image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                    } else {
                        image_url = image_url.substring(pos, image_url.length());
                        image_url = AllKeys.RESOURSES + "uploads/images/w200" + image_url;
                    }


                    // Log.d(TAG, "New Url : " + image_url);
                } else {
                    image_url = AllKeys.RESOURSES + image_url;
                }

                Glide.with(_context).load(image_url).crossFade().into(holder.imgItem);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Double offer = Double.parseDouble(pd.getPrice()) * 100 / Double.parseDouble(pd.getMrp());
            int percentage = 100 - offer.intValue();
            if (percentage == 0 || percentage < 0) {
                holder.txtOffer.setVisibility(View.GONE);
            } else {
                //  holder.txtOffer.setVisibility(View.GONE);
                holder.txtOffer.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.fadein));

                if (percentage <= 9) {
                    holder.txtOffer.setText(" " + percentage + " % off");
                } else {
                    holder.txtOffer.setText("" + percentage + " % off");
                }
            }

            holder.imgItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Toast.makeText(_context, "Name : " + pd.getProductname() + " Id : " + pd.getProductid(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(_context,
                                SingleItemActivity.class);
                        sessionmanager.setProductDetails(pd.getProductid(), "1", userDetails.get(SessionManager.KEY_PRODUCT_DESCR), image_url, userDetails.get(SessionManager.KEY_PRODUCT_RATING));


                        //intent.putExtra("ProductId", pd.getProductid());
                        intent.putExtra("ActivityName", "CheckoutActivity");
                        _context.startActivity(intent);
                        //finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        }

        private void sendCartToWishlistDetailsToServcer(final String ProductId, final String dateTime, final String ProductPrice, final ProductData pd) {

            String url_movecarttowishlist = AllKeys.WEBSITE + "moveCartToWishList/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId + "/" + dateTime + "/" + ProductPrice + "";

            Log.d(TAG, "URL  MoveCartToWishList " + url_movecarttowishlist);

            StringRequest str_moveCartToWishList = new StringRequest(Request.Method.GET, url_movecarttowishlist, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response.equals("1")) {


                        GRAND_TOTAL = GRAND_TOTAL - (Integer.parseInt(pd.getPrice()) * Integer.parseInt(pd.getQuantity()));
                        TOTAL_PRICE = TOTAL_PRICE - (Integer.parseInt(pd.getPrice()) * Integer.parseInt(pd.getQuantity()));


                        if(!String.valueOf(pd.getMlmDiscount()).equals("0") && !String.valueOf(pd.getMlmDiscount()).equals("0.00") )
                        {
                            double mlm = ((Double.parseDouble(pd.getPrice()) * Double.parseDouble(pd.getMlmDiscount())) / 100) * Double.parseDouble(pd.getQuantity());
                            // mlm = ((Integer.parseInt(pd.getPrice()) * Integer.parseInt(pd.getMlmDiscount())) / 100) * Integer.parseInt(pd.getQuantity());


                            if(TOTAL_MLM_DISCOUNT >= mlm)
                            {
                                TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT - mlm;
                                GRAND_TOTAL = GRAND_TOTAL + mlm;
                            }
                            else
                            {

                                TOTAL_MLM_DISCOUNT =0.00;
                                GRAND_TOTAL = GRAND_TOTAL  + (mlm - TOTAL_MLM_DISCOUNT);
                            }


                        }

                        TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES - (Integer.parseInt(pd.getShippingcost()));
                        GRAND_TOTAL = GRAND_TOTAL - (Integer.parseInt(pd.getShippingcost()));

                        IsOutOfStock = false;
                        setTitleWithCount();



                        list_Products.remove(list_Products.indexOf(pd));
                        list_cartItemsId.remove(list_cartItemsId.indexOf(ProductId));
                        IsOutOfStock = false;


                        notifyDataSetChanged();

                        notifyDataSetChanged();






                        Toast.makeText(_context, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error in moveCartToWishList : " + error.getMessage());

                    sendCartToWishlistDetailsToServcer(ProductId, dateTime, ProductPrice, pd);
                }
            });

            MyApplication.getInstance().addToRequestQueue(str_moveCartToWishList);


        }


        /**
         * Remove item details from cart details
         *
         * @param ProductId
         * @param type
         * @param position
         */
        private void removeCartDetailsToServer(final String ProductId, final String type, final int position, final ProductData pd) {


            showDialog();
            String url_addtowishlist = AllKeys.WEBSITE + "manageCart/" + type + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId;

            Log.d(TAG, "URL AddToCartList : " + url_addtowishlist);
            final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d(TAG, "Response ManageCartList : " + response);

                    if (response.equals("1")) {


                        try {


                            GRAND_TOTAL = GRAND_TOTAL - (Integer.parseInt(pd.getPrice()) * Integer.parseInt(pd.getQuantity()));
                            TOTAL_PRICE = TOTAL_PRICE - (Integer.parseInt(pd.getPrice()) * Integer.parseInt(pd.getQuantity()));


                            if(!String.valueOf(pd.getMlmDiscount()).equals("0") && !String.valueOf(pd.getMlmDiscount()).equals("0.00") )
                            {
                                double mlm = ((Double.parseDouble(pd.getPrice()) * Double.parseDouble(pd.getMlmDiscount())) / 100) * Double.parseDouble(pd.getQuantity());
                                // mlm = ((Integer.parseInt(pd.getPrice()) * Integer.parseInt(pd.getMlmDiscount())) / 100) * Integer.parseInt(pd.getQuantity());


                                if(TOTAL_MLM_DISCOUNT >= mlm)
                                {
                                    TOTAL_MLM_DISCOUNT = TOTAL_MLM_DISCOUNT - mlm;
                                    GRAND_TOTAL = GRAND_TOTAL + mlm;
                                }
                                else
                                {

                                    TOTAL_MLM_DISCOUNT =0.00;
                                    GRAND_TOTAL = GRAND_TOTAL  + (mlm - TOTAL_MLM_DISCOUNT);
                                }


                            }



                            TOTAL_DELIVERY_CHARGES = TOTAL_DELIVERY_CHARGES - (Integer.parseInt(pd.getShippingcost()));
                            GRAND_TOTAL = GRAND_TOTAL - (Integer.parseInt(pd.getShippingcost()));

                            list_cartItemsId.remove(list_cartItemsId.indexOf(pd.getProductid()));
                            list_Products.remove(list_Products.indexOf(pd));
                            IsOutOfStock = false;


                            Toast.makeText(_context, "Item deleted from  cart", Toast.LENGTH_SHORT).show();
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            try {
                                if (list_cartItemsId.size() == 0) {
                                    Intent intent = new Intent(_context, CheckoutActivity.class);
                                    _context.startActivity(intent);
                                    sessionmanager.setCartItemsIdDetails("");
                                } else {
                                    String data = list_cartItemsId.toString();

                                    data = data.substring(1, data.length() - 1);
                                    sessionmanager.setCartItemsIdDetails(data);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            setTitleWithCount();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }


                    } else {
                        Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                    }
                    hideDialog();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    hideDialog();
                    if (error instanceof ServerError) {
                        hideDialog();


                    } else {
                        removeCartDetailsToServer(ProductId, type, position, pd);
                    }
                    Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                }
            });
            MyApplication.getInstance().addToRequestQueue(str_addToCartList);

        }

        @Override
        public int getItemCount() {
            return list_Products.size();
        }
    }




            /*Complete displat Product detials Adapter*/

}
