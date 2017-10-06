package com.yelona;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.mzelzoghbi.zgallery.ZGallery;
import com.mzelzoghbi.zgallery.entities.ZColor;
import com.yelona.animation.ShakeAnimation;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.fragments.SellerDetailsFragment;
import com.yelona.fragments.DescriptionFragment;
import com.yelona.fragments.ShippingFragment;
import com.yelona.fragments.ReviewFragment;
import com.yelona.helper.AllKeys;
import com.yelona.helper.NetConnectivity;
import com.yelona.helper.Utils;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;


public class SingleItemActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView btnAddToWishList;
    private Button btnAddToCart;
    // private SubsamplingScaleImageView imgItem;
    private ImageView imgItem;


    //private ImageView imgItem;


    private String TAG = SingleItemActivity.class.getSimpleName();


    private SpotsDialog pDialog;
    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private TextView txtProductName;
    private TextView txtProductPrice, txtProductMRP, txtOffer;
    private TextView txtDescr;
    private TextView txtDescrInfo;
    private RecyclerView recyclerview_images;

    public ArrayList<ProductData> list_ProductImages = new ArrayList<ProductData>();
    public ArrayList<String> list_Images = new ArrayList<String>();
    private Gallery gallery;
    private ProductImagesRecyclerViewAdapter_New adapter_ProductImages;
    private String PRODUCT_NAME = "";
    private String PRODUCT_PRICE = "";
    private String QUANTITY = "1", UNIT_PRICE = "", SHIPPING_CHARGE = "", PROMO_VALUE = "", IS_MLM = "0", SELLER_ID = "0";
    private MenuItem cart;
    private CoordinatorLayout coordinateLayout;

    private ArrayList<String> list_cartItemsId = new ArrayList<String>();
    private TextView txtAvailability, txvPincode;
    private Button btnCheckPincode, btnCheckPincodeChange;
    private TextInputLayout edtPincodeWrapper;
    private EditText edtPincode;
    private String CITY_NAME;
    private String SELLER_PINCODE = "";
    private boolean Is_Pincode_Available = false;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private NestedScrollView mScrollView;
    private LinearLayout llBeforePincode;
    private LinearLayout llAfterPincode;
    private RecyclerView recyclerview_size;
    private boolean IS_PRODUCT_SIZE = false;
    private String productSize = "0";
    private CardView crdSize;

    private ArrayList<String> list_productSize = new ArrayList<String>();
    private boolean IS_SIZE_SELECTED = false;
    private String SELECTED_SIZE = "";
    private Button btnBuyNow;
    private LinearLayout ll_mainUI;
    private LinearLayout ll_no_internet;
    private Button btnNoNetwork;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }
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

        sessionmanager.setNotificationStatus("false",userDetails.get(SessionManager.KEY_PRODUCT_ID),userDetails.get(SessionManager.KEY_CATEGORY_ID));

        //Default Set Ad Multiple Products
        sessionmanager.setCheckoutType("multiple", userDetails.get(SessionManager.KEY_PRODUCT_ID));

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        ll_mainUI = (LinearLayout) findViewById(R.id.llmainUI);
        ll_no_internet = (LinearLayout) findViewById(R.id.ll_no_internet);
        btnNoNetwork = (Button) findViewById(R.id.btnNoNetwork);

        btnNoNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoadDataFromServer();
            }
        });



        /*imgItem = (ImageView) findViewById(R.id.imgItem);*/
        txtProductName = (TextView) findViewById(R.id.txtProductName);
        txtProductMRP = (TextView) findViewById(R.id.txtProductMRP);
        txtProductPrice = (TextView) findViewById(R.id.txtProductPrice);
        txtOffer = (TextView) findViewById(R.id.txtOffer);
        btnAddToCart = (Button) findViewById(R.id.btnAddToCart);
        btnAddToWishList = (TextView) findViewById(R.id.txtWishLiast);


        txtDescr = (TextView) findViewById(R.id.txtDescr);
        txtDescrInfo = (TextView) findViewById(R.id.txtDescrInfo);
        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        crdSize = (CardView) findViewById(R.id.crdSize);


        llBeforePincode = (LinearLayout) findViewById(R.id.llBeforePincode);
        llAfterPincode = (LinearLayout) findViewById(R.id.llAfterPincode);


        mScrollView = (NestedScrollView) findViewById(R.id.srollview1);


        txtAvailability = (TextView) findViewById(R.id.txtAvailability);

        edtPincodeWrapper = (TextInputLayout) findViewById(R.id.edtPincodeWrapper);
        edtPincode = (EditText) findViewById(R.id.edtPincode);

        //edtPincodeWrapper2 = (TextInputLayout) findViewById(R.id.edtPincodeWrapper2);
        txvPincode = (TextView) findViewById(R.id.txvPincode);


        viewPager = (ViewPager) findViewById(R.id.viewpager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        btnCheckPincode = (Button) findViewById(R.id.btnCheckPincode);
        btnCheckPincodeChange = (Button) findViewById(R.id.btnCheckPincodeChange);


        btnBuyNow = (Button) findViewById(R.id.btnBuyNow);


        //  btnCheckPincode.setTypeface(CustomFonts.typefaceCondensed(context));

        //edtPincode.setTypeface(CustomFonts.typefaceCondensed(context));
        //txvPincode.setTypeface(CustomFonts.typefaceCondensed(context));

        // btnCheckPincodeChange.setTypeface(CustomFonts.typefaceCondensed(context));

        btnCheckPincodeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPincodeDisable();
            }
        });

        edtPincode.setText(userDetails.get(SessionManager.KEY_USER_PINCODE));
        CITY_NAME = userDetails.get(SessionManager.KEY_USER_CITY);
        btnCheckPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (edtPincode.getText().toString().equals("")) {
                    edtPincodeWrapper.setErrorEnabled(true);
                    edtPincodeWrapper.setError("Enter Pincode");
                    setPincodeDisable();


                } else {
                    edtPincodeWrapper.setErrorEnabled(false);

                    if (btnCheckPincode.getText().equals("change")) {
                        /*InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(6);
                        edtPincode.setFilters(FilterArray);*/

                        txvPincode.setText(Html.fromHtml("Delivers to " + "<b> " + edtPincode.getText().toString() + "," + CITY_NAME + "</b>"));
                        setPincodeEnable();


                    } else {


                        if (SELLER_PINCODE.equals("")) {
                            SELLER_PINCODE = edtPincode.getText().toString();
                        }


                        if (userDetails.get(SessionManager.KEY_USER_PINCODE).equals("")) {

                            getCityNameByPincode();
                            //denish ubhal

                        } else {
                            //Check e-vahan API
                            checkItemAvailabilityOfUserLocation();

                        }

                    }


                }
            }
        });


        btnCheckPincode.performClick();


        //Get Cart Item details from sesion
        try {
            if (!userDetails.get(SessionManager.KEY_CARTITEMS_ID).equals("")) {
                list_cartItemsId = new ArrayList<>(Arrays.asList(userDetails.get(SessionManager.KEY_CARTITEMS_ID).split(",")));

            }
            if (list_cartItemsId.indexOf(userDetails.get(SessionManager.KEY_PRODUCT_ID)) != -1) {
                btnAddToCart.setText("go to cart");
            } else {
                btnAddToCart.setText("add to cart");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (userDetails.get(SessionManager.KEY_WISHLIST_STATUS).equals("-1")) {

//            btnAddToWishList.setText("Add to wishlist");

            btnAddToWishList.setBackgroundResource(R.drawable.ic_wishlist_default);


        } else {

//            btnAddToWishList.setText("view wishlist");
            btnAddToWishList.setBackgroundResource(R.drawable.ic_wishlist_seleceted2);
        }

        imgItem = (ImageView) findViewById(R.id.imgItem);
        gallery = (Gallery) findViewById(R.id.gallery);

        imgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //StartDispalyImages();

                if (list_Images.size() > 0) {
                    galleryActivity();
                } else {
                    Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show();
                }


            }
        });


        btnAddToCart.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (Is_Pincode_Available == true)
                        {
                            if (btnAddToCart.getText().toString().toLowerCase().equals("go to cart")) {

                                Intent ii = new Intent(context, CheckoutActivity.class);
                                startCountAnimation();
                                sessionmanager.setActivityName(userDetails.get(SessionManager.KEY_ACTIVITY_NAME));
                                sessionmanager.setCheckoutType("multiple", userDetails.get(SessionManager.KEY_PRODUCT_ID));
                                startActivity(ii);
                                finish();


                            } else {

                                if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {
                                    sessionmanager.setNewUserSession("SingleItemActivity");
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    manageAddToCartDetailsToServer(userDetails.get(SessionManager.KEY_PRODUCT_ID), QUANTITY, "add");
                                }

                            }


                            setAddToCartBadget();


                        } else
                            {


                            setPincodeDisable();


                            txvPincode.setFocusable(true);
                            txvPincode.setText("Invalid Pincode Provider");

                            Snackbar.make(coordinateLayout, "Invalid Pincode Provider", Snackbar.LENGTH_SHORT).show();


                            if (txvPincode.getText().toString().equals("Invalid Pincode Provider")) {
                                shakeInit(llAfterPincode);
                                llAfterPincode.requestFocus();

                            } else {
                                if (edtPincode.getText().toString().equals("")) {
                                    shakeInit(llBeforePincode);
                                    llBeforePincode.requestFocus();
                                } else {
                                    shakeInit(llAfterPincode);
                                    llAfterPincode.requestFocus();
                                }

                            }


                            setPincodeEnable();
                            //edtPincodeWrapper.setErrorEnabled(true);
                            //edtPincode.setText("Invalid Pincode Provided");

                        }


                    }
                });

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {
                    sessionmanager.setNewUserSession("SingleItemActivity");
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    sessionmanager.setCheckoutType("single", userDetails.get(SessionManager.KEY_PRODUCT_ID));

                    manageAddToCartDetailsToServer(userDetails.get(SessionManager.KEY_PRODUCT_ID), QUANTITY, "add");
                }


            }
        });


        btnAddToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {
                    sessionmanager.setNewUserSession("SingleItemActivity");
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {


                    userDetails = sessionmanager.getSessionDetails();

                    if (userDetails.get(SessionManager.KEY_WISHLIST_STATUS).equals("-1")) {

                        sendWishListDetailsToServer(db, sd, userDetails.get(SessionManager.KEY_PRODUCT_ID), PRODUCT_PRICE, "add");

                    } else {

                        sendWishListDetailsToServer(db, sd, userDetails.get(SessionManager.KEY_PRODUCT_ID), PRODUCT_PRICE, "remove");
                    }


                    btnAddToWishList.startAnimation(AnimationUtils.loadAnimation(context, R.anim.clockwise_refresh));
                }


            }
        });


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Gallery g = (Gallery) findViewById(R.id.gallery);

        // set gallery to left side
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) g.getLayoutParams();
        mlp.setMargins(-(metrics.widthPixels / 2 + (500)), mlp.topMargin,
                mlp.rightMargin, mlp.bottomMargin);


        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                Toast.makeText(getBaseContext(), "pic" + (position + 1) + " selected",
                        Toast.LENGTH_SHORT).show();
                // display the images selected
                //  ImageView imageView = (ImageView) findViewById(R.id.image1);
                // imageView.setImageResource(imageIDs[position]);

                try {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String image_url = "imagE_url";

                                URL myFileUrl = new URL(list_ProductImages.get(position).getImage_url());
                                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                                conn.setDoInput(true);
                                conn.connect();

                                InputStream is = conn.getInputStream();
                                Bitmap bm = BitmapFactory.decodeStream(is);

                                // imgItem.setImage(ImageSource.bitmap(bm));
                                //.placeholder(R.mipmap.ic_launcher)
                                Glide.with(context).load(image_url).error(R.mipmap.ic_launcher).into(imgItem);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        //imgItem = (ImageView) findViewById(R.id.imgItem);

        //imageView.setImage(ImageSource.asset("squirrel.jpg"));

// set the default image display type
        //imgItem.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);


        recyclerview_images = (RecyclerView) findViewById(R.id.recyclerview_images);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerview_images.setLayoutManager(layoutManager2);
        recyclerview_images.setItemAnimator(new DefaultItemAnimator());


        recyclerview_size = (RecyclerView) findViewById(R.id.recyclerview_size);
        RecyclerView.LayoutManager layoutManager_size = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerview_size.setLayoutManager(layoutManager_size);
        recyclerview_size.setItemAnimator(new DefaultItemAnimator());


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);


      //  swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);


        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (NetConnectivity.isOnline(context)) {
                                            //new AsyncData1().execute();
                                          /*  swipeRefreshLayout.setRefreshing(true);
                                            if (swipeRefreshLayout.isRefreshing()) {
                                                swipeRefreshLayout.setRefreshing(false);
                                            }*/
                                            LoadDataFromServer();
                                            //  new SendAllDetialToServer().execute();
                                            Log.d("oncreate", "from run method");


                                        } else {
                                           // swipeRefreshLayout.setRefreshing(false);
                                            //checkInternet();
                                            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
        );


        hideNoInternetUI();


    }

    private void showInternetUI() {

        ll_mainUI.setVisibility(View.GONE);
        ll_no_internet.setVisibility(View.VISIBLE);
    }

    private void hideNoInternetUI() {

        ll_mainUI.setVisibility(View.VISIBLE);
        ll_no_internet.setVisibility(View.GONE);

    }


    private void LoadDataFromServer() {
        getSingleItemDisplayDetailsFromServer();

        getAllCartItemDetailsFromServer(false);
    }

    private void shakeInit(LinearLayout ll) {


        ShakeAnimation.create().with(ll)
                .setDuration(2000)
                .setRepeatMode(ShakeAnimation.RESTART)
                .setRepeatCount(1)
                .start();

        /*.setRepeatCount(ShakeAnimation.INFINITE)*/
    }
//onCreate Completed


    @Override
    public void onRefresh() {
        if (NetConnectivity.isOnline(context)) {
            //new AsyncData1().execute();
            //  swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);
      //      swipeRefreshLayout.setRefreshing(true);
            //swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);


            Log.d("swipe from ", "onrefresh method");

            LoadDataFromServer();


        } else {
            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DescriptionFragment(), "Description");
        adapter.addFragment(new ReviewFragment(), "Review");
        adapter.addFragment(new ShippingFragment(), "Shipping Details");
        adapter.addFragment(new SellerDetailsFragment(), "Seller Details");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void galleryActivity() {

        ArrayList<String> newImages = new ArrayList<>();

        newImages.clear();
        for (int i = 0; i < list_Images.size(); i++) {
            newImages.add(getFormattedImageURL(list_Images.get(i), "0"));


        }

        ZGallery.with(this, newImages)
                .setToolbarTitleColor(ZColor.WHITE)
                .setGalleryBackgroundColor(ZColor.WHITE)
                .setToolbarColorResId(R.color.colorPrimary)
                .setTitle(PRODUCT_NAME)
                .show();
    }


    private void getCityNameByPincode()
    {
        showDialog();
        String url_getCityNameByPincode = AllKeys.URL_CITYNAME_BY_PINCODE_CHECK + "&pin=" + edtPincode.getText().toString() + "";
        url_getCityNameByPincode =AllKeys.URL_CITYNAME_BY_PINCODE_CHECK2+edtPincode.getText().toString();
        Log.d(TAG, "URL GetCityNameByPincode :" + url_getCityNameByPincode);
        StringRequest str_getCityNameByPincode = new StringRequest(Request.Method.GET, url_getCityNameByPincode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.contains("District"))
                {
                    try
                    {

                        /*response = "[" + response + "]";
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("PostOffice");

                        for (int j = 0; j < arr.length(); j++) {
                            try {

                                JSONObject objData = arr.getJSONObject(j);

                                JSONArray arrData = objData.getJSONArray("Data");
                                for (int i = 0; i < arrData.length(); i++) {

                                    CITY_NAME = arrData.getJSONObject(i).getString("City");


                                    sessionmanager.setUserPinocdeAndCity(edtPincode.getText().toString(), CITY_NAME);


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }*/


                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("PostOffice");

                        for (int i = 0; i < arr.length(); i++) {

                            CITY_NAME = arr.getJSONObject(i).getString("District");


                            sessionmanager.setUserPinocdeAndCity(edtPincode.getText().toString(), CITY_NAME);


                        }

                        //Check e-vahan API
                        checkItemAvailabilityOfUserLocation();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    setPincodeEnable();
                    txvPincode.setText(Html.fromHtml("Pincode " + edtPincode.getText().toString() + " is invalid"));


                }
                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    hideDialog();
                } else {
                    getCityNameByPincode();
                }


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getCityNameByPincode);

    }

    private void checkItemAvailabilityOfUserLocation() {

        //http://s.evahanexpress.com/Traking/check_service/check_pincode?to_pin_code=691582&payment_type=COD&from_pin_code=395007&from_city=surat&declared_value=100
        //7196e50f188a07f919efb28c742b8d80


        showDialog();
        String url_check_pincode = AllKeys.URL_PINCODE_CHECK + "/check_pincode?to_pin_code=" + edtPincode.getText().toString() + "&payment_type=COD&from_pin_code=" + SELLER_PINCODE + "&from_city=" + CITY_NAME + "&declared_value=" + PRODUCT_PRICE + "";
        Log.d(TAG, "URL CheckPincode : " + url_check_pincode);

        StringRequest str_CheckPincode = new StringRequest(Request.Method.GET, url_check_pincode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response :" + response.toString());

                response = "[" + response + "]";
                response = dbhandler.convertToJsonFormat(response);

                if (response.contains("\"sucess\": \"yes\"")) {
                    Is_Pincode_Available = true;


                    /*InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(50);
                    edtPincode.setFilters(FilterArray);
                    edtPincode.setText(Html.fromHtml("Delivers to " + "<b> " + edtPincode.getText().toString() + "," + CITY_NAME + "</b>"));*/
                    txvPincode.setText(Html.fromHtml("Delivers to " + "<b> " + edtPincode.getText().toString() + "," + CITY_NAME + "</b>"));

                    setPincodeEnable();


                } else {
                    Is_Pincode_Available = false;

                    edtPincodeWrapper.setErrorEnabled(true);
                    edtPincodeWrapper.setError("Invalid Pincode Provided");

                    setPincodeDisable();

                    /*InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(50);
                    edtPincode.setFilters(FilterArray);*/


                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    hideDialog();
                } else {
                    checkItemAvailabilityOfUserLocation();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_CheckPincode);


    }

    private void setPincodeDisable() {

        edtPincode.setVisibility(View.VISIBLE);
        edtPincodeWrapper.setVisibility(View.VISIBLE);

        btnCheckPincode.setVisibility(View.VISIBLE);
        btnCheckPincodeChange.setVisibility(View.GONE);

        txvPincode.setVisibility(View.GONE);

    }

    private void setPincodeEnable() {
        edtPincode.setVisibility(View.GONE);
        edtPincodeWrapper.setVisibility(View.GONE);
        btnCheckPincode.setVisibility(View.GONE);

        txvPincode.setVisibility(View.VISIBLE);
        btnCheckPincodeChange.setVisibility(View.VISIBLE);

    }


    private void manageAddToCartDetailsToServer(final String ProductId, final String Quantity, final String Type) {
        showDialog();

        String url_addtowishlist = AllKeys.WEBSITE + "manageCart/" + Type + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId + "/" + Quantity + "/" + db.convertEncodedString(db.getDateTime()) + "/" + db.convertEncodedString(db.getDateTime()) + "/" + SELECTED_SIZE + "";
        Log.d(TAG, "URL AddToCart : " + url_addtowishlist);
        StringRequest str_manageCart = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response str_manageCart : " + response);

                //list_cartItemsId.clear();
                if (response.equals("1")) {
                    userDetails = sessionmanager.getSessionDetails();

                    if (userDetails.get(SessionManager.KEY_CHECKOUT_TYPE).equals("single")) {
                        Intent ii = new Intent(context, CheckoutActivity.class);

                        startCountAnimation();
                        sessionmanager.setActivityName(userDetails.get(SessionManager.KEY_ACTIVITY_NAME));

                        startActivity(ii);
                        finish();
                    } else {
                        //Toast.makeText(context, "Added to your cart successfully", Toast.LENGTH_SHORT).show();
                        Snackbar.make(coordinateLayout, "Added to your cart successfully", Snackbar.LENGTH_SHORT).show();
                        btnAddToCart.setText("Go to cart");
                        list_cartItemsId.add(userDetails.get(SessionManager.KEY_PRODUCT_ID));


                        if (list_cartItemsId.size() == 0) {


                            sessionmanager.setCartItemsIdDetails("");
                        } else {

                            String data = list_cartItemsId.toString();

                            data = data.substring(1, data.length() - 1);
                            sessionmanager.setCartItemsIdDetails(data);
                            setAddToCartBadget();
                        }

                    }


                } else {
                    Toast.makeText(context, "Sorry,try again...", Toast.LENGTH_SHORT).show();


                }

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                    hideDialog();
                } else {
                    //manageAddToCartDetailsToServer(ProductId, Quantity, Type);
                    hideDialog();
                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();


                }


                Log.d(TAG, "Error in ManageWishList : " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_manageCart);

    }


    /**
     * Send wishlist details to server
     *
     * @param db
     * @param sd
     * @param ProductId
     * @param ProductPrice
     * @param type
     */
    private void sendWishListDetailsToServer(final dbhandler db, final SQLiteDatabase sd, final String ProductId, final String ProductPrice, final String type) {

        showDialog();

        String url_addtowishlist = AllKeys.WEBSITE + "manageWishlist/" + type + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId + "/" + db.convertEncodedString(db.getDateTime()) + "/" + db.convertEncodedString(db.getDateTime()) + "/" + ProductPrice + "";
        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
        StringRequest str_addToWishList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response ManageWishList : " + response);

                if (response.equals("0")) {
                    Toast.makeText(context, "Sorry,try again...", Toast.LENGTH_SHORT).show();
                } else {


                    if (type.equals("add")) {
                        sessionmanager.setProductDetails(userDetails.get(SessionManager.KEY_PRODUCT_ID), "1", userDetails.get(SessionManager.KEY_PRODUCT_DESCR), userDetails.get(SessionManager.KEY_PRODUCT_IMAGE_URL), userDetails.get(SessionManager.KEY_PRODUCT_RATING));
                        Toast.makeText(context, "Added to your wishlist", Toast.LENGTH_SHORT).show();
                        // btnAddToWishList.setText("view wishlist");
                        btnAddToWishList.setBackgroundResource(R.drawable.ic_wishlist_seleceted2);
                    } else {
                        sessionmanager.setProductDetails(userDetails.get(SessionManager.KEY_PRODUCT_ID), "-1", userDetails.get(SessionManager.KEY_PRODUCT_DESCR), userDetails.get(SessionManager.KEY_PRODUCT_IMAGE_URL), userDetails.get(SessionManager.KEY_PRODUCT_RATING));
                        btnAddToWishList.setBackgroundResource(R.drawable.ic_wishlist_default);

                        Toast.makeText(context, "Removed from your wishlist", Toast.LENGTH_SHORT).show();
                        //  btnAddToWishList.setBackgroundResource(R.drawable.ic_wishlist_default);
                    }

                }

                db.close();
                sd.close();

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();
                } else {
                    sendWishListDetailsToServer(db, sd, ProductId, ProductPrice, type);

                }
                Log.d(TAG, "Error in ManageWishList : " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_addToWishList);

    }


    private void StartDispalyImages() {
        Intent intent = new Intent(context, DisplayImagesActivity.class);

        String images = "";
        for (int i = 0; i < list_ProductImages.size(); i++) {
            if (i == 0) {
                images = list_ProductImages.get(i).getImage_url();
            } else if (i == list_ProductImages.size()) {
                images = images + "," + list_ProductImages.get(i).getImage_url();

            } else {
                images = images + "," + list_ProductImages.get(i).getImage_url();
            }


        }
        intent.putExtra("AllImages", images);
        intent.putExtra("ProductName", PRODUCT_NAME);
        intent.putExtra("ActivityName", getIntent().getStringExtra("ActivityName"));
        //sessionmanager.setActivityName("");
        startActivity(intent);
        finish();

    }


    //Image Adapter
    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private int itemBackground;

        public ImageAdapter(Context c) {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a = obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }

        // returns the number of images
        public int getCount() {
            return list_ProductImages.size();
        }

        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }

        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }

        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);


            try {
                //image_url = getFormattedImageURL(image_url);

                //    URL myFileUrl = new URL(list_ProductImages.get(position).getImage_url());
                //   HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                //   conn.setDoInput(true);
                //  conn.connect();

                //  InputStream is = conn.getInputStream();
                //  Bitmap bm = BitmapFactory.decodeStream(is);
                //  imgItem.setImage(ImageSource.bitmap(bm));
                //.placeholder(R.mipmap.ic_launcher)
                Glide.with(context).load(list_ProductImages.get(position).getImage_url()).error(R.mipmap.ic_launcher).into(imageView);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //imageView.setImageResource(imageIDs[position]);

            imageView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
            //imageView.setLayoutParams(new Gallery.LayoutParams(100,100));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            imageView.setBackgroundResource(itemBackground);


            return imageView;
        }
    }

    //Complete image adapter


    public void showDialog() {

        try {
            if (!pDialog.isShowing()) {

                pDialog.show();
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void hideDialog() {
        try {
            if (pDialog.isShowing()) {
                pDialog.dismiss();

            }
           /* if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //onCreate Completed

    private void getSingleItemDisplayDetailsFromServer()
    {
        showDialog();
        hideNoInternetUI();


        String url_getproduct = AllKeys.WEBSITE + "getProductDetailsByProductId/" + userDetails.get(SessionManager.KEY_PRODUCT_ID) + "";
        Log.d(TAG, "URL SinglItem : " + url_getproduct);
        // url_getproduct = "http://19designs.org/yelona/index.php/welcome/getProductDetailsByProductId/93";
        StringRequest str_singleitem = new StringRequest(Request.Method.GET, url_getproduct, new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {

                Log.d(TAG, "Response :" + response.toString());
                if (response.contains("shipping_cost")) {


                    response = response.replace("null", "\"\"");
                    try {
                        list_ProductImages.clear();
                        list_Images.clear();
                        response = db.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);


                            String image_url = c.getString(AllKeys.TAG_IMAGE_URL);
                            productSize = c.getString(AllKeys.TAG_SIZE);


                            /*if (productSize.equals("0") || productSize.equals("")) {*/
                            if (c.getString(AllKeys.TAG_IS_SIZE).equals("0")) {
                                IS_PRODUCT_SIZE = false;
                            } else {
                                if (productSize.equals("0") || productSize.equals("")) {
                                    IS_PRODUCT_SIZE = false;
                                } else {
                                    IS_PRODUCT_SIZE = true;
                                    list_productSize = new ArrayList<>(Arrays.asList(productSize.split(";")));
                                    Log.d(TAG, "Size Data : " + list_Images.toString());
                                }

                            }


                            try {
                                image_url = getFormattedImageURL(image_url, "600");

                                //db.getBitmapFromURL(image_url);

                                // URL myFileUrl = new URL(image_url);
                                //  HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                                //  conn.setDoInput(true);
                                //  conn.connect();

                                // InputStream is = conn.getInputStream();
                                // Bitmap bm = BitmapFactory.decodeStream(is);
                                //  bm = db.getBitmapFromURL(image_url);

                                //imgItem.setImage(ImageSource.bitmap(bm));

                                //.placeholder(R.mipmap.ic_launcher)
                                Glide.with(context).load(image_url).error(R.drawable.yelona_blur_250).into(imgItem);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error in image loading and binding" + e.getMessage());
                            }
                            SELLER_PINCODE = c.getString(AllKeys.TAG_SELLER_PINCODE);
                            PRODUCT_NAME = c.getString(AllKeys.TAG_PRODUCTNAME);
                            setTitle(PRODUCT_NAME);
                            txtProductName.setText(PRODUCT_NAME);
                            txtProductPrice.setText("\u20b9 " + c.getString(AllKeys.TAG_PRICE));
                            PRODUCT_PRICE = c.getString(AllKeys.TAG_PRICE);
                            txtProductMRP.setText("\u20b9 " + c.getString(AllKeys.TAG_MRP));

                            UNIT_PRICE = c.getString(AllKeys.TAG_PRICE);
                            SHIPPING_CHARGE = c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER);
                            //PROMO_VALUE = c.getString(AllKeys.TAG_PRODUCTID);

                            //Check Inventory if not in inventory then disable addToCart
                            if (c.getString(AllKeys.TAG_INVENTORY).equals("0")) {
                                btnBuyNow.setEnabled(false);
                                btnAddToCart.setText("Out Of Stock");
                                btnAddToCart.setEnabled(false);
                                txtAvailability.setText("Out Of Stock");
                            } else {
                                btnBuyNow.setEnabled(true);
                                txtAvailability.setText("In Stock");
                                txtAvailability.setTextColor(Color.parseColor("#4CAF50"));
                            }

                            SELLER_ID = c.getString(AllKeys.TAG_SELLERID);


                            sessionmanager.setSellerInformation(c.getString(AllKeys.TAG_SELLER_IS_ACTIVE), c.getString(AllKeys.TAG_SELLER_COMPANY_NAME), c.getString(AllKeys.TAG_SELLER_NAME), c.getString(AllKeys.TAG_SELLER_EMAIL), c.getString(AllKeys.TAG_SELLER_ADDRESS), c.getString(AllKeys.TAG_SELLER_CITY), c.getString(AllKeys.TAG_SELLER_STATE), SELLER_ID, c.getString(AllKeys.TAG_SELLER_CODE), c.getString(AllKeys.TAG_SELLER_RATING), c.getString(AllKeys.TAG_SELLER_AVATAR), c.getString(AllKeys.TAG_SELLER_MOBILE), c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER), c.getString(AllKeys.TAG_SHIP_DATE));


                            Double offer = Double.parseDouble(c.getString(AllKeys.TAG_PRICE)) * 100 / Double.parseDouble(c.getString(AllKeys.TAG_MRP));
                            int percentage = 100 - offer.intValue();
                            if (percentage == 0 || percentage < 0) {
                                txtOffer.setVisibility(View.GONE);
                            } else {
                                //  holder.txtOffer.setVisibility(View.GONE);
                                txtOffer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fadein));

                                if (percentage <= 9) {
                                    txtOffer.setText(" " + percentage + " % off");
                                } else {
                                    txtOffer.setText("" + percentage + " % off");
                                }
                            }


                            String descr = c.getString(AllKeys.TAG_DESCRIPTION);


                            descr = "SKU:" + c.getString(AllKeys.TAG_SKU) + ";" + descr;
                            sessionmanager.setProductDetails(userDetails.get(SessionManager.KEY_PRODUCT_ID), userDetails.get(SessionManager.KEY_WISHLIST_STATUS), descr, image_url, c.getString(AllKeys.TAG_STAR_COUNT));
                            descr = descr.replace("_x000D_", "");


                            try {
                                Answers.getInstance().logPurchase(new PurchaseEvent());

                                Answers.getInstance().logPurchase(new PurchaseEvent()
                                        .putItemPrice(BigDecimal.valueOf(Double.parseDouble(UNIT_PRICE)))
                                        .putCurrency(Currency.getInstance("INR"))
                                        .putItemName(PRODUCT_NAME)


                                        .putCustomAttribute("ProductID", userDetails.get(SessionManager.KEY_PRODUCT_ID))
                                        .putCustomAttribute("UserID", userDetails.get(SessionManager.KEY_USERID))
                                        .putItemId(c.getString(AllKeys.TAG_SKU))
                                        .putSuccess(true));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String brand = c.getString(AllKeys.TAG_BRAND);
                            if (brand.equals("")) {
                                brand = "-";

                            }
                            String sku = c.getString(AllKeys.TAG_SKU);
                            if (sku.equals("")) {
                                sku = "-";

                            }


                            descr = descr.replace(";", "\n\n");

                            txtDescrInfo.setText("\tBrand\t\t\t\t\t\t:\t\t" + brand + "\n\n\tSKU   \t\t\t\t\t\t:\t" + sku + "\n\n\t" + descr);

                            String more_images = "/uploads/products/a/p/ap-7_bottom_zps4jbsqiya.jpg,/uploads/products/a/p/ap-7back_zps0pagzzzc.jpg,/uploads/products/a/p/ap-7side_zpswmpqb8bo.jpg,/uploads/products/a/p/ap-7_bottom_zps4jbsqiya.jpg,/uploads/products/a/p/ap-7back_zps0pagzzzc.jpg,/uploads/products/a/p/ap-7side_zpswmpqb8bo.jpg";
                            more_images = c.getString(AllKeys.TAG_IMAGE_URL) + "," + c.getString(AllKeys.TAG_MORE_IMAGES);
                            more_images = c.getString(AllKeys.TAG_MORE_IMAGES);
                            if (more_images.equals("")) {
                                more_images = c.getString(AllKeys.TAG_IMAGE_URL);

                            }
                            if (!more_images.equals("")) {
                                ArrayList<String> myList = new ArrayList<String>(Arrays.asList(more_images.split(",")));


                                try {


                                    //check multiple images are available or not if not available then add first image as default image
                                    if (myList.size() < 0) {

                                        //ProductData pd = new ProductData(image_url, true);
                                        ProductData pd = new ProductData(c.getString(AllKeys.TAG_IMAGE_URL), true);

                                        list_ProductImages.add(pd);
                                        list_Images.add(c.getString(AllKeys.TAG_IMAGE_URL));


                                    } else {
                                        for (int s = 0; s < myList.size(); s++) {


                                            ProductData pd = new ProductData(myList.get(s), false);
                                            list_ProductImages.add(pd);
                                            list_Images.add(myList.get(s));


                                        }

                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "Error in getImages : " + e.getMessage());
                                }

                                recyclerview_images.setVisibility(View.VISIBLE);
                                adapter_ProductImages = new ProductImagesRecyclerViewAdapter_New(context);
                                recyclerview_images.setAdapter(adapter_ProductImages);

                            } else {
                                recyclerview_images.setVisibility(View.GONE);
                            }


                            if (IS_PRODUCT_SIZE) {


                                crdSize.setVisibility(View.VISIBLE);

                                ProductSizeDisplayAdapter adapter_productsize = new ProductSizeDisplayAdapter(context);
                                recyclerview_size.setAdapter(adapter_productsize);
                            } else {
                                crdSize.setVisibility(View.GONE);
                            }


                            try {
                                gallery.setAdapter(new ImageAdapter(SingleItemActivity.this));
                                //gallery.setSelection(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }
                    setAddToCartBadget();


                    /**
                     * Setup Product Details Related Tabs
                     */
                    setupViewPager(viewPager);
                }
                hideDialog();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();
                    showInternetUI();
                } else {
                    getSingleItemDisplayDetailsFromServer();
                }


                Log.d(TAG, "Error in singleItem : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_singleitem);


    }

    private String getFormattedImageURL(String image_url, String size) {

        if (size.equals("200")) {
            if (image_url.contains("images") && image_url.contains("__w-200")) {
                int pos = image_url.lastIndexOf("/");
                //pos = image_url.inde
                if (image_url.contains("200")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w200" + image_url;
                } else if (image_url.contains("400")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                } else {
                    image_url = AllKeys.RESOURSES + image_url;

                }


                Log.d(TAG, "New Url : " + image_url);
            } else {
                image_url = AllKeys.RESOURSES + image_url;
            }

        } else {

            if (image_url.contains("images") && image_url.contains("__w-200")) {
                int pos = image_url.lastIndexOf("/");
                //pos = image_url.inde
                if (image_url.contains("900")) {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w900" + image_url;
                } else if (image_url.contains("800")) {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w800" + image_url;
                } else if (image_url.contains("600")) {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                } else if (image_url.contains("400")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                } else {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w200" + image_url;
                }


                Log.d(TAG, "New Url : " + image_url);
            } else {
                image_url = AllKeys.RESOURSES + image_url;
            }
        }


        Log.d(TAG, "final image path :" + image_url);
        return image_url;
    }

    private void getAllCartItemDetailsFromServer(final boolean isCheckout) {

        hideNoInternetUI();
        showDialog();

        String url_addtowishlist = AllKeys.WEBSITE + "getCartDataByUserid/" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL AddToCart : " + url_addtowishlist);
        StringRequest str_manageCart = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response str_manageCart : " + response);

                list_cartItemsId.clear();
                if (response.contains("user_id")) {

                    response = dbhandler.convertToJsonFormat(response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");


                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);

                            list_cartItemsId.add(c.getString(AllKeys.TAG_CART_PRODUCT_ID));


                        }

                        if (list_cartItemsId.size() == 0) {


                            sessionmanager.setCartItemsIdDetails("0");
                        } else {
                            String data = list_cartItemsId.toString();

                            data = data.substring(1, data.length() - 1);
                            sessionmanager.setCartItemsIdDetails(data);
                        }

                        if (isCheckout == true) {
                            Intent intent = new Intent(context, CheckoutActivity.class);
                            // intent.putExtra("ActivityName", TAG);
                            sessionmanager.setActivityName(TAG);
                            sessionmanager.setCheckoutType("multiple", userDetails.get(SessionManager.KEY_PRODUCT_ID));
                            startActivity(intent);
                            finish();

                        } else {
                            setAddToCartBadget();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideNoInternetUI();
                    showInternetUI();
                } else {
                    getAllCartItemDetailsFromServer(isCheckout);
                }

                setAddToCartBadget();

                Log.d(TAG, "Error in ManageWishList : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_manageCart);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        try {
            getMenuInflater().inflate(R.menu.item_display, menu);
            cart = (MenuItem) menu.findItem(R.id.menu_addtocart);


            LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();


//int orderid=db.getMaxOrderId();


            setAddToCartBadget();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            try {

                //  Log.d(TAG, context.getPackageName() + "." + getIntent().getStringExtra("ActivityName"));

                Intent i = null;
                try {
                    i = new Intent(context, Class.forName(context.getPackageName() + "." + getIntent().getStringExtra("ActivityName")));
                    i.putExtra("ActivityName", getIntent().getStringExtra("ActivityName"));
                } catch (ClassNotFoundException e) {
                    i = new Intent(context, NewDashBoardActivity.class);
                    e.printStackTrace();
                }

                //sessionmanager.setActivityName(TAG);

                startActivity(i);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (item.getItemId() == R.id.menu_whishlist) {
            Intent intent = new Intent(context, WishListActivity.class);
            //intent.putExtra("ActivityName", TAG);
            sessionmanager.setActivityName(TAG);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.menu_addtocart) {


            getAllCartItemDetailsFromServer(true);


        } else if (item.getItemId() == R.id.menu_search) {
            Intent intent = new Intent(context, SearchActivity.class);
            //intent.putExtra("ActivityName", TAG);
            sessionmanager.setActivityName(TAG);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {
            Log.d(TAG, context.getPackageName() + "." + getIntent().getStringExtra("ActivityName"));
            //Log.d(TAG, context.getPackageName() + "." + userDetails.get(SessionManager.KEY_ACTIVITY_NAME));
            Intent i = null;
            try {
                i = new Intent(context, Class.forName(context.getPackageName() + "." + getIntent().getStringExtra("ActivityName")));
                i.putExtra("ActivityName", getIntent().getStringExtra("ActivityName"));
            } catch (ClassNotFoundException e) {
                i = new Intent(context, NewDashBoardActivity.class);
                e.printStackTrace();
            }
            //sessionmanager.setActivityName(TAG);
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ProductImagesRecyclerViewAdapter_New extends RecyclerView.Adapter<ProductImagesRecyclerViewAdapter_New.MyViewHolder>
    {


        private final Context _context;
        // private final ArrayList<ProductData> list_NewProcuts;
        private final LayoutInflater inflater;
        private final SessionManager sessionmanager;


        private HashMap<String, String> userDetails = new HashMap<String, String>();
        private String TAG = com.yelona.adapter.ProductImagesRecyclerViewAdapter.class.getSimpleName();


        // Start with first item selected
        //  private int focusedItem = 0;
        private int selectedItem = 0;

        public ProductImagesRecyclerViewAdapter_New(Context context) {
            this._context = context;
            // this.list_NewProcuts = listNewProduct;
            inflater = LayoutInflater.from(context);

            sessionmanager = new SessionManager(context);
            userDetails = sessionmanager.getSessionDetails();


        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            // private final RatingBar ratingBar;
            //  private final TextView txtPrice, txtName, txtDelete,txtProductMRP;
            private final ImageView imgItem;
            //  private final CardView crdProduct;
            // private final LinearLayout myBackground;

            public MyViewHolder(View itemView) {
                super(itemView);
                //  crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
                imgItem = (ImageView) itemView.findViewById(R.id.imgSingleItem);

            }

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = inflater.inflate(R.layout.row_single_product_images, parent, false);

            MyViewHolder viewFolder = new MyViewHolder(v);
            return viewFolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            final ProductData pd = list_ProductImages.get(position);


            try {
                Glide.with(_context).load(getFormattedImageURL(pd.getImage_url(), "200")).into(holder.imgItem);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.imgItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//
                    //    StartDispalyImages();


                    try {


                        URL myFileUrl = new URL(getFormattedImageURL(pd.getImage_url(), "600"));
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        //imgItem.setImage(ImageSource.bitmap(bm));
                        //.placeholder(R.mipmap.ic_launcher)
                        Glide.with(context).load(getFormattedImageURL(pd.getImage_url(), "600")).error(R.mipmap.ic_launcher).into(imgItem);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }


        @Override
        public int getItemCount() {
            return list_ProductImages.size();
        }
    }

    private void startCountAnimation() {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(0, 600);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {

                //cart.setText("" + (int) animation.getAnimatedValue());
                //  setAddToCartBadget();
            }
        });
        animator.start();
    }

    private void setAddToCartBadget() {

        //    String query = "select * from " + db.TABLE_ORDER;


        try {
            LayerDrawable icon = (LayerDrawable) cart.getIcon();


            Utils.setBadgeCount(SingleItemActivity.this, icon, list_cartItemsId.size());
            // startCountAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //Custom View Pageer


    public class WrapContentHeightViewPager extends ViewPager {

        public WrapContentHeightViewPager(Context context) {
            super(context);
        }

        public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int height = 0;

            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);

                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                int h = child.getMeasuredHeight();

                if (h > height) height = h;
            }

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /*@Override*/
    public boolean onTouch(View v, MotionEvent event) {
        int dragthreshold = 30;

        int downX = 0;

        int downY = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();

                downY = (int) event.getRawY();

                break;

            case MotionEvent.ACTION_MOVE:
                int distanceX = Math.abs((int) event.getRawX() - downX);

                int distanceY = Math.abs((int) event.getRawY() - downY);

                if (distanceY > distanceX && distanceY > dragthreshold) {
                    viewPager.getParent().requestDisallowInterceptTouchEvent(false);

                    mScrollView.getParent().requestDisallowInterceptTouchEvent(true);
                } else if (distanceX > distanceY && distanceX > dragthreshold) {
                    viewPager.getParent().requestDisallowInterceptTouchEvent(true);

                    mScrollView.getParent().requestDisallowInterceptTouchEvent(false);
                }

                break;
            case MotionEvent.ACTION_UP:
                mScrollView.getParent().requestDisallowInterceptTouchEvent(false);

                viewPager.getParent().requestDisallowInterceptTouchEvent(false);

                break;
        }

        return false;
    }

    //Complete Custom View Pager


    /**
     * Display Product Size UI
     */
    public class ProductSizeDisplayAdapter extends RecyclerView.Adapter<ProductSizeDisplayAdapter.MyViewHolder> {


        private final Context _context;


        public ProductSizeDisplayAdapter(Context context) {
            this._context = context;


        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtSize;


            public MyViewHolder(View itemView) {
                super(itemView);
                txtSize = (TextView) itemView.findViewById(R.id.size);


            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(_context).inflate(R.layout.row_single_product_size, parent, false);


            MyViewHolder myviewHolder = new MyViewHolder(view);
            return myviewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {


            if (position == 0) {


                if (IS_SIZE_SELECTED == false) {

                    holder.txtSize.setBackgroundResource(R.drawable.sizeround1);
                    holder.txtSize.setTextColor(Color.parseColor("#585858"));
                    holder.txtSize.setBackgroundResource(R.drawable.sizeround2);
                    holder.txtSize.setTextColor(Color.parseColor("#ffffff"));
                    SELECTED_SIZE = list_productSize.get(position);


                    //holder.txtSize
                }
            }

            if (SELECTED_SIZE.equals(list_productSize.get(position))) {

                holder.txtSize.setBackgroundResource(R.drawable.sizeround1);
                holder.txtSize.setTextColor(Color.parseColor("#585858"));
                holder.txtSize.setBackgroundResource(R.drawable.sizeround2);
                holder.txtSize.setTextColor(Color.parseColor("#ffffff"));


            } else {
                holder.txtSize.setBackgroundResource(R.drawable.sizeround1);
                holder.txtSize.setTextColor(Color.parseColor("#585858"));

                //holder.txtSize.setBackgroundResource(R.drawable.sizeround2);
                //holder.txtSize.setTextColor(Color.parseColor("#ffffff"));


            }


            holder.txtSize.setText(list_productSize.get(position));

            holder.txtSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    IS_SIZE_SELECTED = true;


                    SELECTED_SIZE = list_productSize.get(position);
                    holder.txtSize.setBackgroundResource(R.drawable.sizeround1);
                    holder.txtSize.setTextColor(Color.parseColor("#585858"));
                    holder.txtSize.setBackgroundResource(R.drawable.sizeround2);
                    holder.txtSize.setTextColor(Color.parseColor("#ffffff"));

                    notifyItemChanged(position);
                    notifyDataSetChanged();

                }
            });


        }

        @Override
        public int getItemCount() {
            return list_productSize.size();
        }
    }



/*Complete DIsplay Product Size UI*/
}
