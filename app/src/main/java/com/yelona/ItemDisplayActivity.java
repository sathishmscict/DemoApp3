package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.mzelzoghbi.zgallery.ZGallery;
import com.mzelzoghbi.zgallery.entities.ZColor;
import com.yelona.adapter.ContactsAdapter;
import com.yelona.adapter.NewProductsRecyclerViewAdapter;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.expandableData.BaseItem;
import com.yelona.expandableData.GroupItem;
import com.yelona.expandableData.Item;
import com.yelona.helper.AllKeys;
import com.yelona.helper.Contact;
import com.yelona.helper.CustomFonts;
import com.yelona.helper.NetConnectivity;
import com.yelona.helper.Utils;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;
import com.yelona.views.LevelBeamView;
import com.yelona.views.RangeBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.OnItemClickListener;

import static com.yelona.helper.AllKeys.back_button;

public class ItemDisplayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerview_products;
    private Context context = this;
    private dbhandler db;
    private SQLiteDatabase sd;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = ItemDisplayActivity.class.getSimpleName();
    private ArrayList<ProductData> list_Products = new ArrayList<ProductData>();
    private FloatingActionButton fab;
    private TextView txtDisplayData;
    private TextView txtSort;
    private TextView txtFilter;
    private boolean IsGridData = true;
    NewProductsRecyclerViewAdapter adapter_NewProduct;

    private ArrayList<String> list_wishListProducts = new ArrayList<String>();

    private TextView txtnodata;
    private MenuItem cart;
    private ArrayList<String> list_cartItemsId = new ArrayList<String>();
    private CardView cvSorting;

    /*Expandable ListView*/
    private static final int MAX_LEVELS = 3;

    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;

    private ArrayList<String> firstLevelCategoryName = new ArrayList<String>();
    private ArrayList<Integer> firstLEvelCategoryId = new ArrayList<Integer>();

    private ArrayList<String> secondLevelCategoryName = new ArrayList<String>();
    private ArrayList<Integer> secondLEvelCategoryId = new ArrayList<Integer>();

    private ArrayList<String> thirdtLevelCategoryName = new ArrayList<String>();
    private ArrayList<Integer> thirdLEvelCategoryId = new ArrayList<Integer>();
    private MultiLevelListView multiLevelListView;
    private SweetSheet mSweetSheet;
    private RelativeLayout rl;
    private ArrayList<String> sortType = new ArrayList<String>();
    private String SORTING_TYPE = "Default";
    private SweetSheet mSweetSheet3;
    //private TextView size_ss, size_m, size_l, size_xl, size_xxl;
    private RangeBar price_rangebar;
    private boolean Is_Price_Range_Enalbe = false;
    private Button btnPriceMinValue;
    private Button btnPriceMaximumValue;
    private Integer MINIMUM_VALUE_IN_RS = 0, MAXIMUM_VALUE_IN_RS = 0, SELECTED_MAXIMUM_VALUE = 0;
    private ArrayList<String> list_productSize = new ArrayList<String>();
    private ArrayList<String> list_selected_productSize = new ArrayList<String>();
    private CardView crdSize;
    private RecyclerView recyclerview_size;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private Integer OFFSET = 10, PAGENO = 0, TOTAL_PAGES = 0;
    private Integer TOTAL_RECORDS = 0;
    private LinearLayout ll_no_internet;
    private LinearLayout ll_mainUI;
    private Button btnNoNetwork;
    private TextView txtUsername;
    private TextView txtUseremail;
    private CircleImageView imgProfilePic;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerview_products
                        .getLayoutManager();
                layoutManager.scrollToPositionWithOffset(0, 0);
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


        try {
//Track View Details
            Answers.getInstance().logContentView(new ContentViewEvent());
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Items Display")
                    .putContentType("Items Display Screen")
                    .putCustomAttribute("CategoryID", userDetails.get(SessionManager.KEY_CATEGORY_ID))
                    .putCustomAttribute("CategoryName", userDetails.get(SessionManager.KEY_CATEGORY_NAME))
                    .putContentId("ItemsDispaly-100"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        setTitle(userDetails.get(SessionManager.KEY_CATEGORY_NAME));

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        ll_no_internet = (LinearLayout) findViewById(R.id.ll_no_internet);
        btnNoNetwork = (Button) findViewById(R.id.btnNoNetwork);

        btnNoNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoadDataFromServer();
            }
        });

        ll_mainUI = (LinearLayout) findViewById(R.id.llmainUI);
        cvSorting = (CardView) findViewById(R.id.cvSorting);
        txtDisplayData = (TextView) findViewById(R.id.txtDisplayData);
        txtSort = (TextView) findViewById(R.id.txtSort);
        txtFilter = (TextView) findViewById(R.id.txtFilter);


        hideNoInternetUI();
        showInternetUI();


        rl = (RelativeLayout) findViewById(R.id.rl);
        /**
         * Handle Grid and ListView
         */
        txtDisplayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IsGridData == true) {

                    IsGridData = false;
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                    recyclerview_products.setLayoutManager(layoutManager);
                    txtDisplayData.setBackgroundResource(R.drawable.icon_grid);
                    //adapter_NewProduct = new NewProductsRecyclerViewAdapter(context, list_Products, "list", list_wishListProducts);
                    adapter_NewProduct = new NewProductsRecyclerViewAdapter(context, list_Products, "list", list_wishListProducts);
                    recyclerview_products.setAdapter(adapter_NewProduct);


                } else {
                    adapter_NewProduct = new NewProductsRecyclerViewAdapter(context, list_Products, "grid", list_wishListProducts);
                    recyclerview_products.setAdapter(adapter_NewProduct);

                    txtDisplayData.setBackgroundResource(R.drawable.icon_list);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ItemDisplayActivity.this, 2);
                    recyclerview_products.setLayoutManager(layoutManager);
                    recyclerview_products.addItemDecoration(new dbhandler.GridSpacingItemDecoration(2, db.dpToPx(2), true));

                    IsGridData = true;
                }


            }
        });

        setupRecyclerView();
        setupCustomFilterView();

        // mSweetSheet.toggle();
        //txtSort.setTypeface(Typeface.DEFAULT);
        // txtFilter.setTypeface(Typeface.DEFAULT);
        txtSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mSweetSheet3.isShow()) {
                    mSweetSheet3.dismiss();
                }
                mSweetSheet.toggle();


                fab.setVisibility(View.GONE);

                //Toast.makeText(context, "Sort", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Clicked on Sort");
            }
        });
        txtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Filter", Toast.LENGTH_SHORT).show();
                try {
                    Log.d(TAG, "Clicked on Filter");
                    if (mSweetSheet.isShow()) {
                        mSweetSheet.dismiss();
                    }

                    mSweetSheet3.toggle();
                    //   Toast.makeText(context, "Filter", Toast.LENGTH_SHORT).show();

                    fab.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        txtnodata = (TextView) findViewById(R.id.txtnodata);
        txtnodata.setVisibility(View.GONE);
        txtnodata.setTypeface(CustomFonts.typefaceCondensed(context));


        recyclerview_products = (RecyclerView) findViewById(R.id.recyclervieW_products);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        // swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);


        //   swipeRefreshLayout.setRefreshing(true);
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
                                            // swipeRefreshLayout.setRefreshing(true);
                                           /* if (swipeRefreshLayout.isRefreshing()) {
                                                swipeRefreshLayout.setRefreshing(false);
                                            }*/
                                            swipeRefreshLayout.setRefreshing(false);
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




        /*RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);


        recyclerview_products.setLayoutManager(layoutManager);
        recyclerview_products.addItemDecoration(new dbhandler.GridSpacingItemDecoration(2, db.dpToPx(2), true));*/

        LinearLayoutManager layoutManager2 = new GridLayoutManager(ItemDisplayActivity.this, 2);
        recyclerview_products.setLayoutManager(layoutManager2);
        recyclerview_products.addItemDecoration(new dbhandler.GridSpacingItemDecoration(2, db.dpToPx(2), true));


      /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerview_products.setLayoutManager(linearLayoutManager);
        recyclerview_products.setItemAnimator(new DefaultItemAnimator());*/

        // final List<Contact> allContacts = Contact.createContactsList(10, 0);
        // final ContactsAdapter adapter = new ContactsAdapter(allContacts);
        //  recyclerview_products.setAdapter(adapter);


        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager2)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                //  List<Contact> moreContacts = Contact.createContactsList(10, page);
                //  final int curSize = adapter.getItemCount();
                //  allContacts.addAll(moreContacts);

             /*   view.post(new Runnable() {
                    @Override
                    public void run() {
                        //     adapter.notifyItemRangeInserted(curSize, allContacts.size() - 1);
                    }
                });*/

                //Toast.makeText(context, "Page No : "+page, Toast.LENGTH_SHORT).show();
                if (TOTAL_PAGES > PAGENO) {
                    //loadNextDataFromApi(page);
                    ++PAGENO;


                    if (PAGENO * OFFSET < TOTAL_RECORDS) {
                        getAllProductDetailsFromServer();
                        Toast.makeText(context, "Total " + PAGENO * OFFSET + " of " + TOTAL_RECORDS + " Products", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerview_products.addOnScrollListener(scrollListener);
        recyclerview_products.setHasFixedSize(false);
        recyclerview_products.getLayoutManager().setAutoMeasureEnabled(true);
        // recyclerview_products.setNestedScrollingEnabled(false);


        recyclerview_products.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                    //  cvSorting.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                    //cvSorting.setVisibility(View.VISIBLE);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


     /*   recyclerview_products.addOnItemTouchListener(new dbhandler.RecyclerTouchListener(context, recyclerview_products, new dbhandler.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                try {
                    Toast.makeText(context, "Name : " + list_Products.get(position).getProductname() + " Id : " + list_Products.get(position).getProductid(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,
                            SingleItemActivity.class);
                    intent.putExtra("ProductId", list_Products.get(position).getProductid());
                    intent.putExtra("ActivityName", TAG);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        confMenu();
        navigationView.setVisibility(View.VISIBLE);


        try {


           /* txtUsername = (TextView) headerLayout.findViewById(R.id.txtname);
            txtUseremail = (TextView) headerLayout.findViewById(R.id.txtemail);
            imgProfilePic = (ImageView) headerLayout.findViewById(R.id.imgProfilePic);
*/
            txtUsername = (TextView) findViewById(R.id.txtname);
            txtUseremail = (TextView) findViewById(R.id.txtemail);
            imgProfilePic = (CircleImageView) findViewById(R.id.imgProfilePic);


            Log.d(TAG, "User Logog : " + userDetails.get(SessionManager.KEY_USER_AVATAR));
            String image_url = userDetails.get(SessionManager.KEY_USER_AVATAR);


            try {
                SetUserProfilePictireFromBase64EnodedString();
            } catch (Exception e) {
                e.printStackTrace();

                Glide.with(context).load(image_url).crossFade().placeholder(R.drawable.icon_userlogo).error(R.drawable.icon_userlogo).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgProfilePic);
            }

            if(userDetails.get(SessionManager.KEY_USERID).equals("0"))
            {

                txtUsername.setText("Welcome to Yelona");
                txtUseremail.setText("");
            }
            else
            {
                txtUseremail.setText("" + userDetails.get(SessionManager.KEY_USER_EMAIL));
                txtUsername.setText("" + userDetails.get(SessionManager.KEY_USER_FIRSTNAME));
            }





        } catch (Exception e) {
            e.printStackTrace();
        }


        //   LoadDataFromServer();


    }


    private void SetUserProfilePictireFromBase64EnodedString() {
        try {

            try {
                if (!userDetails.get(SessionManager.KEY_USER_AVATAR).equals("")) {

                    String userprofile = userDetails.get(SessionManager.KEY_USER_AVATAR);
                    if (!userprofile.equals("") && !userprofile.equals("null")) {
                        if (!userprofile.contains("google") && !userprofile.contains("facebook")) {
                            userprofile = AllKeys.RESOURSES + userprofile;

                        } else if (userprofile.contains("google")) {
                            userprofile = userprofile.replace("sz=50", "sz=200");
                        } else if (userprofile.contains("facebook")) {
                            userprofile = userprofile.replace("normal", "large");

                        } else {
                            userprofile = AllKeys.RESOURSES + userprofile;

                        }
                        Glide.with(context).load(userprofile).crossFade().placeholder(R.drawable.icon_userlogo).error(R.drawable.icon_userlogo).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgProfilePic);
                    }


                    URL url = new URL(userprofile);
                    Log.d("Image URL : ", "" + userprofile);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    String enc = dbhandler.getStringImage(image);
                    sessionmanager.setEncodedImage(enc);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            userDetails = sessionmanager.getSessionDetails();
            String myBase64Image = userDetails.get(SessionManager.KEY_ENODEDED_STRING);
            if (!myBase64Image.equals("")) {

                Bitmap myBitmapAgain = dbhandler.decodeBase64(myBase64Image);

                imgProfilePic.setImageBitmap(myBitmapAgain);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Decode Img Exception : ", e.getMessage());
        }
    }

    private void LoadDataFromServer() {


        list_Products.clear();
        PAGENO = 0;
        TOTAL_PAGES = 1;
        TOTAL_RECORDS = 0;

        if (!userDetails.get(SessionManager.KEY_USERID).equals("0")) {

            getAllWishListDetailsFromServer();
            //getAllProductDetailsFromServer();
            getAllCartItemDetailsFromServer(false);

            getProductSizeMasterDataFromAPI();
        } else {


            // getAllWishListDetailsFromServer();
            getAllProductDetailsFromServer();
            // getAllCartItemDetailsFromServer();

            getProductSizeMasterDataFromAPI();
        }
    }

    private void showInternetUI() {

        ll_mainUI.setVisibility(View.GONE);
        ll_no_internet.setVisibility(View.VISIBLE);
    }

    private void hideNoInternetUI() {


        ll_mainUI.setVisibility(View.VISIBLE);
        ll_no_internet.setVisibility(View.GONE);

    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

    }


    //getAll Product size data from server
    private void getProductSizeMasterDataFromAPI()
    {
        showDialog();
        hideNoInternetUI();



        String url_getAllSize=url_getAllSize = AllKeys.WEBSITE + "getProductSizes/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/"+ userDetails.get(SessionManager.KEY_CATEGORY_TYPE) +"";;
        /*if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard")) {

                url_getAllSize = AllKeys.WEBSITE + "getProductSizes/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/"+ userDetails.get(SessionManager.KEY_CATEGORY_TYPE) +"";
        }
        else
        {
                url_getAllSize = AllKeys.WEBSITE + "getProductSizes/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/"+ userDetails.get(SessionManager.KEY_CATEGORY_TYPE) +"";
        }*/


        Log.d(TAG, "url GetAllSizes : " + url_getAllSize);

        StringRequest str_getallSizes = new StringRequest(Request.Method.GET, url_getAllSize, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Resposne GetAllSizes : " + response);


                list_productSize.clear();
                if (response.contains("size")) {
                    try {
                        response = dbhandler.convertToJsonFormat(response);

                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            list_productSize.add(c.getString("size"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    crdSize.setVisibility(View.VISIBLE);

                    ProductSizeDisplayAdapter adapter_productsize = new ProductSizeDisplayAdapter(context);
                    recyclerview_size.setAdapter(adapter_productsize);


                } else {
                    crdSize.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {

                    hideDialog();
                    showInternetUI();
                } else {
                    getProductSizeMasterDataFromAPI();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getallSizes);


    }
    //onCreate Completed


    @Override
    public void onRefresh() {
        if (NetConnectivity.isOnline(context)) {
            //new AsyncData1().execute();
            //  swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);
            //    swipeRefreshLayout.setRefreshing(true);
            //swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);


            Log.d("swipe from ", "onrefresh method");

            LoadDataFromServer();


        } else {
            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Set Custom Filter View with Price and Size
     */
    private void setupCustomFilterView()
    {


        mSweetSheet3 = new SweetSheet(rl);
        CustomDelegate customDelegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_filter_custom_view, null, false);
        customDelegate.setCustomView(view);
        mSweetSheet3.setDelegate(customDelegate);
        mSweetSheet3.setBackgroundEffect(new BlurEffect(3));


        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSweetSheet3.dismiss();
            }
        });

         /*Filter Related UI and code*/
        // fonts1 =  Typeface.createFromAsset(getAssets(), "fonts/MavenPro-Regular.ttf");


        crdSize = (CardView) view.findViewById(R.id.crdSize);

        recyclerview_size = (RecyclerView) view.findViewById(R.id.recyclerview_size);
        RecyclerView.LayoutManager layoutManager_size = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerview_size.setLayoutManager(layoutManager_size);
        recyclerview_size.setItemAnimator(new DefaultItemAnimator());


        LinearLayout llPricing = (LinearLayout) view.findViewById(R.id.llPricing);
        // llPricing.setVisibility(View.GONE);
        crdSize.setVisibility(View.VISIBLE);

        ProductSizeDisplayAdapter adapter_productsize = new ProductSizeDisplayAdapter(context);
        recyclerview_size.setAdapter(adapter_productsize);


        Button btnGo = (Button) view.findViewById(R.id.btnGo);
        btnGo.setTypeface(CustomFonts.typefaceCondensed(context));

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                list_Products.clear();
                PAGENO = 0;
                TOTAL_PAGES = 0;
                confMenu();
                adapter_NewProduct.notifyDataSetChanged();


                Is_Price_Range_Enalbe = true;
                getAllProductDetailsFromServer();
                mSweetSheet3.dismiss();


            }
        });


        price_rangebar = (RangeBar) view.findViewById(R.id.rangebar1);


        btnPriceMinValue = (Button) view.findViewById(R.id.rang1);
        btnPriceMaximumValue = (Button) view.findViewById(R.id.rang2);

        btnPriceMinValue.setTypeface(CustomFonts.typefaceCondensed(context));
        btnPriceMaximumValue.setTypeface(CustomFonts.typefaceCondensed(context));


        // Sets the display values of the indices
        price_rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                MINIMUM_VALUE_IN_RS = leftPinIndex;
                //MAXIMUM_VALUE_IN_RS = rightPinIndex;
                SELECTED_MAXIMUM_VALUE = rightPinIndex;

                btnPriceMinValue.setText("MIN \u20b9 " + leftPinIndex);
                btnPriceMaximumValue.setText("MAX \u20b9 " + rightPinIndex);
            }

        });


//        ***********kids**********

        final LinearLayout linear1 = (LinearLayout) view.findViewById(R.id.linear1);
        final LinearLayout linear2 = (LinearLayout) view.findViewById(R.id.linear2);


        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                linear2.setVisibility(View.VISIBLE);
                linear1.setVisibility(View.GONE);

            }
        });

        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                linear2.setVisibility(View.GONE);
                linear1.setVisibility(View.VISIBLE);


            }
        });



        /*Complete Filter Related UI and code*/


    }//complete setup custom view


    /**
     * Setup Custom Sorting Options alnong with RecyclerView
     */
    private void setupRecyclerView() {

        final ArrayList<MenuEntity> list = new ArrayList<>();

       /* MenuEntity menuEntity1 = new MenuEntity();
        menuEntity1.iconId = R.drawable.icon_add;
        menuEntity1.titleColor = 0xff000000;
        menuEntity1.title = "Sort By";*/

        MenuEntity menuEntity_HTL = new MenuEntity();
        menuEntity_HTL.iconId = R.drawable.icon_rupee;
        menuEntity_HTL.titleColor = 0xff000000;
        menuEntity_HTL.title = "Price - HighToLow";

        MenuEntity menuEntityLTH = new MenuEntity();
        menuEntityLTH.iconId = R.drawable.icon_rupee;
        menuEntityLTH.titleColor = 0xff000000;
        menuEntityLTH.title = "Price - LowToHigh";

        MenuEntity menuEntity_NF = new MenuEntity();
        menuEntity_NF.iconId = R.drawable.icon_newset;
        menuEntity_NF.titleColor = 0xff000000;
        menuEntity_NF.title = "Newest First";

        MenuEntity menuEntityDis = new MenuEntity();
        menuEntityDis.iconId = R.drawable.icon_discount;
        menuEntityDis.titleColor = 0xff000000;
        menuEntityDis.title = "Discount";


        // list.add(menuEntity1);
        list.add(menuEntity_HTL);
        list.add(menuEntityLTH);
        list.add(menuEntity_NF);
        list.add(menuEntityDis);


        mSweetSheet = new SweetSheet(rl);

        //Set menu list
        mSweetSheet.setMenuList(list);
        //Delegate to recyclerview
        mSweetSheet.setDelegate(new RecyclerViewDelegate(true));
        //set Background blur effect
        mSweetSheet.setBackgroundEffect(new BlurEffect(3));

        sortType.clear();
        sortType.add("HighToLow");
        sortType.add("LowToHigh");
        sortType.add("NewestFirst");
        sortType.add("Discount");


        //Click Listener
        mSweetSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                Is_Price_Range_Enalbe = false;

                int greenColorValue = Color.parseColor("#4CAF50");

                list.get(position).titleColor = greenColorValue;

                for (int i = 0; i < list.size(); i++) {
                    if (position == i) {
                        list.get(i).titleColor = greenColorValue;
                        SORTING_TYPE = sortType.get(i);

                        list_Products.clear();
                        PAGENO = 0;
                        TOTAL_PAGES = 0;
                        confMenu();

                        getAllProductDetailsFromServer();

//sorting

                    } else {
                        list.get(i).titleColor = 0xff000000;
                    }

                }

                ((RecyclerViewDelegate) mSweetSheet.getDelegate()).notifyDataSetChanged();

                mSweetSheet.dismiss();
                //根据返回值, true 会关闭 SweetSheet ,false 则不会.
                // Toast.makeText(ItemDisplayActivity.this, menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
                Log.d(TAG, menuEntity1.title + "  " + position);

                fab.setVisibility(View.VISIBLE);
                return false;
            }
        });


    }


    /**
     * Expandable ListView
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SetCategoryDetails(String categoryType, String categoryId, String categoryName) {

        Log.d(TAG, "Item Click Data : CategoryType : " + categoryType + " CategoryId : " + categoryId + " CategoryName : " + categoryName);

        // sessionmanager.setCategoryTypeAndIdDetails("dashboard",String.valueOf(list_bannerTypeId.get(list_bannerTypeName.indexOf("Hot Deals"))),"Hot Deals");
        sessionmanager.setCategoryTypeAndIdDetails(categoryType, categoryId, categoryName);
        setTitle(categoryName);
        list_Products.clear();
        PAGENO = 0;
        TOTAL_PAGES = 0;
        confMenu();

        recyclerview_products = (RecyclerView) findViewById(R.id.recyclervieW_products);


      /*  Intent intent = new Intent(context, ItemDisplayActivity.class);
        startActivity(intent);
        finish();
*/
        userDetails = sessionmanager.getSessionDetails();
        getAllProductDetailsFromServer();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


    }

    private void confMenu() {
        multiLevelListView = (MultiLevelListView) findViewById(R.id.multiLevelMenu);

        // custom ListAdapter
        ListAdapter listAdapter = new ListAdapter();

        multiLevelListView.setAdapter(listAdapter);
        multiLevelListView.setOnItemClickListener(mOnItemClickListener);

        List<BaseItem> rootMenu = new ArrayList<>();

        //Set Super Category Data

        String query_categories = "select * from Categorymaster where parentid=0";
        Cursor cc = sd.rawQuery(query_categories, null);
        if (cc.getCount() > 0) {
            rootMenu.clear();
            firstLEvelCategoryId.clear();
            firstLevelCategoryName.clear();
            while (cc.moveToNext()) {
                int getCategoryId = cc.getInt(cc.getColumnIndex(dbhandler.CATEGORY_ID));

                String upperString = cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME));
                upperString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1);

                firstLevelCategoryName.add(upperString);
                firstLEvelCategoryId.add(getCategoryId);

                //Check SubCategory Exist or Not..If exist then add as GroupItem()
                String query_supercategory = "select * from Categorymaster where parentid=" + getCategoryId + " order by " + dbhandler.CATEGORY_SEQUENCE_NO + " asc";
                Cursor cursor_supercategory = sd.rawQuery(query_supercategory, null);
                if (cursor_supercategory.getCount() <= 0) {

                    //rootMenu.add(new Item(cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME))));
                    rootMenu.add(new Item(upperString));

                } else {
                    //rootMenu.add(new GroupItem(cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME))));
                    rootMenu.add(new GroupItem(upperString));
                }

            }

        }

        listAdapter.setDataItems(rootMenu);

        //Complete Super Category Data
    }


    /**
     * Hanlde ItemClickLitener of Expandable ListView
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        private void showItemDescription(Object object, ItemInfo itemInfo) {
            StringBuilder builder = new StringBuilder("\"");
            builder.append(((BaseItem) object).getName());
            builder.append("\" clicked!\n");

            //StringBuilder builder = new StringBuilder();

            builder.append(String.format("level[%d], idx in level[%d/%d]",
                    itemInfo.getLevel() + 1, /*Indexing starts from 0*/
                    itemInfo.getIdxInLevel() + 1 /*Indexing starts from 0*/,
                    itemInfo.getLevelSize()));

            if (itemInfo.isExpandable()) {
                builder.append(String.format(", expanded[%b]", itemInfo.isExpanded()));
            } else {
                // Toast.makeText(NewDashBoardActivity.this, ((BaseItem) object).getName(), Toast.LENGTH_SHORT).show();

                if ((itemInfo.getLevel() + 1) == 2) {
                    SetCategoryDetails("category", String.valueOf(secondLEvelCategoryId.get(secondLevelCategoryName.indexOf(((BaseItem) object).getName()))), ((BaseItem) object).getName());
                } else if ((itemInfo.getLevel() + 1) == 3) {
                    SetCategoryDetails("category", String.valueOf(thirdLEvelCategoryId.get(thirdtLevelCategoryName.indexOf(((BaseItem) object).getName()))), ((BaseItem) object).getName());
                }


            }

            // builder.append(getItemInfoDsc(itemInfo));
//"Beauty" clicked!level[2], idx in level[1/9], expanded[true]

            Log.d(TAG, "Clicked Item Info : " + builder.toString());

        }

        @Override
        public void onItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }

        @Override
        public void onGroupItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }
    };
    //Compelte Handle onItem click Litener


    /**
     * Handle Custom ListAdpater
     */
    private class ListAdapter extends MultiLevelListAdapter {

        private class ViewHolder {
            TextView nameView;
            TextView infoView;
            ImageView arrowView;
            LevelBeamView levelBeamView;
        }

        @Override
        public List<?> getSubObjects(Object object) {
            // DIEKSEKUSI SAAT KLIK PADA GROUP-ITEM

            return getSubItems((BaseItem) object);
        }

        @Override
        public boolean isExpandable(Object object) {


            return ((BaseItem) object) instanceof GroupItem;
        }

        @Override
        public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(ItemDisplayActivity.this).inflate(R.layout.data_item, null);
                //viewHolder.infoView = (TextView) convertView.findViewById(R.id.dataItemInfo);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.dataItemName);
                viewHolder.arrowView = (ImageView) convertView.findViewById(R.id.dataItemArrow);
                viewHolder.levelBeamView = (LevelBeamView) convertView.findViewById(R.id.dataItemLevelBeam);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            //String upperString = ((BaseItem) object).getName();
            //upperString = upperString.substring(0,1).toUpperCase() + upperString.substring(1);


            //viewHolder.nameView.setText(upperString);

            viewHolder.nameView.setText(((BaseItem) object).getName());

            if (itemInfo.getLevel() == 0) {

                viewHolder.nameView.setTypeface(CustomFonts.typefaceCondensed(context));
            } else if (itemInfo.getLevel() == 1) {
                viewHolder.nameView.setTypeface(CustomFonts.typefaceCondensed(context));
            } else {
                viewHolder.nameView.setTypeface(CustomFonts.typefaceCondensed(context));

            }


            //viewHolder.infoView.setText(getItemInfoDsc(itemInfo));

            if (itemInfo.isExpandable()) {
                viewHolder.arrowView.setVisibility(View.VISIBLE);
                viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ?
                        R.drawable.ic_expand_less_minus_circle : R.drawable.ic_expand_more_plus_circle);
                        /*R.drawable.ic_expand_less : R.drawable.ic_expand_more);*/
            } else {
                viewHolder.arrowView.setVisibility(View.GONE);
            }

            viewHolder.levelBeamView.setLevel(itemInfo.getLevel());

            return convertView;
        }
    }
    /**
     * Complete ListAdapter
     */


    /**
     * Fill Data on Expandalbe listview based on category click
     *
     * @param baseItem
     * @return
     */

    public List<BaseItem> getSubItems(BaseItem baseItem) {

        List<BaseItem> result = new ArrayList<>();
        int level = ((GroupItem) baseItem).getLevel() + 1;

        String menuItem = baseItem.getName();

        if (!(baseItem instanceof GroupItem)) {
            throw new IllegalArgumentException("GroupItem required");
        }

        GroupItem groupItem1 = (GroupItem) baseItem;
        if (groupItem1.getLevel() >= MAX_LEVELS) {
            return null;
        }


        switch (level) {
            case LEVEL_1:

                String query = "select * from Categorymaster where parentid=" + firstLEvelCategoryId.get(firstLevelCategoryName.indexOf(menuItem)) + " order by " + dbhandler.CATEGORY_SEQUENCE_NO + " asc";
                Cursor cursor_category = sd.rawQuery(query, null);
                if (cursor_category.getCount() > 0) {
                    List<BaseItem> list = new ArrayList<>();
                    // secondLEvelCategoryId.clear();
                    // secondLevelCategoryName.clear();

                    while (cursor_category.moveToNext()) {

                        secondLevelCategoryName.add(cursor_category.getString(cursor_category.getColumnIndex(dbhandler.CATEGORY_NAME)));
                        secondLEvelCategoryId.add(cursor_category.getInt(cursor_category.getColumnIndex(dbhandler.CATEGORY_ID)));


                        // Setiap membuat groupItem harus di set levelnya
                        // GroupItem groupItem = new GroupItem("GROUP 1");
                        //  groupItem.setLevel(groupItem.getLevel() + 1);

                        // list.add(new Item("ITEM 1"));
                        //list.add(new Item("ITEM 2"));
                        //list.add(groupItem);


                        //Set Category and SubCategort Related DAta
                        String query_categories = "select * from Categorymaster where parentid=" + cursor_category.getInt(cursor_category.getColumnIndex(dbhandler.CATEGORY_ID)) + " order by " + dbhandler.CATEGORY_SEQUENCE_NO + " asc";
                        Cursor cc = sd.rawQuery(query_categories, null);
                        if (cc.getCount() <= 0) {

                            list.add(new Item(cursor_category.getString(cursor_category.getColumnIndex(dbhandler.CATEGORY_NAME))));
                        } else {

                            GroupItem groupItemCategory = new GroupItem(cursor_category.getString(cursor_category.getColumnIndex(dbhandler.CATEGORY_NAME)));
                            groupItemCategory.setLevel(groupItemCategory.getLevel() + 1);
                            list.add(groupItemCategory);


                        }


                        //Complete Set CAtegory and subcategory data


                    }
                    result = list;

                }

                break;

            case LEVEL_2:
                String query_subcategory = "select * from Categorymaster where parentid=" + secondLEvelCategoryId.get(secondLevelCategoryName.indexOf(menuItem)) + " order by " + dbhandler.CATEGORY_SEQUENCE_NO + " asc";
                Cursor cursor_subcategory = sd.rawQuery(query_subcategory, null);
                if (cursor_subcategory.getCount() > 0) {
                    List<BaseItem> list = new ArrayList<>();
                    // thirdLEvelCategoryId.clear();
                    //thirdtLevelCategoryName.clear();

                    while (cursor_subcategory.moveToNext()) {

                        thirdtLevelCategoryName.add(cursor_subcategory.getString(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_NAME)));
                        thirdLEvelCategoryId.add(cursor_subcategory.getInt(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_ID)));


                        // Setiap membuat groupItem harus di set levelnya
                        GroupItem groupItem = new GroupItem("GROUP 1");
                        groupItem.setLevel(groupItem.getLevel() + 1);

                        // list.add(new Item("ITEM 1"));
                        //list.add(new Item("ITEM 2"));
                        //list.add(groupItem);


                        //Set Category and SubCategort Related DAta
                        String query_categories = "select * from Categorymaster where parentid=" + cursor_subcategory.getInt(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_ID)) + " order by " + dbhandler.CATEGORY_SEQUENCE_NO + " asc";
                        Cursor cc = sd.rawQuery(query_categories, null);
                        if (cc.getCount() <= 0) {

                            list.add(new Item(cursor_subcategory.getString(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_NAME))));
                        } else {

                            GroupItem groupItemCategory = new GroupItem(cursor_subcategory.getString(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_NAME)));
                            groupItemCategory.setLevel(groupItemCategory.getLevel() + 1);
                            list.add(groupItemCategory);


                        }


                        //Complete Set CAtegory and subcategory data


                    }
                    result = list;

                }

                break;
        }

        return result;
    }
    //Complet Fill DAta On Category Or Subcategory


    /**
     * OnClick get Item Name From Expandable List View
     *
     * @param itemInfo
     * @return
     */
  /*  private String getItemInfoDsc(ItemInfo itemInfo) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("level[%d], idx in level[%d/%d]",
                itemInfo.getLevel() + 1, *//*Indexing starts from 0*//*
                itemInfo.getIdxInLevel() + 1 *//*Indexing starts from 0*//*,
                itemInfo.getLevelSize()));

        if (itemInfo.isExpandable()) {
            builder.append(String.format(", expanded[%b]", itemInfo.isExpanded()));
        }
        return builder.toString();
    }
*/

    /**
     * Complet Expandable ListView
     */


    /*Complete Setup Expandable ListView*/
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


    /**
     * get All Wishlist Details from server
     */
    private void getAllWishListDetailsFromServer() {
        hideNoInternetUI();
        String url_getProducts = AllKeys.WEBSITE + "getWishlistByUserid/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "";


        Log.d(TAG, "URL get WishList: " + url_getProducts);
        StringRequest str_get_products = new StringRequest(Request.Method.GET, url_getProducts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAll Hot Deals Products : " + response);
                list_wishListProducts.clear();
                list_wishListProducts.add("items");

                if (response.contains("product_id")) {

                    try {

                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            list_wishListProducts.add(c.getString(AllKeys.TAG_WISHLIST_PRODUCTID));

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                }

                hideDialog();
                getAllProductDetailsFromServer();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();
                    showInternetUI();
                } else {
                    getAllWishListDetailsFromServer();
                }


                Log.d(TAG, "Error in get WishList : " + error.getMessage());
                hideDialog();

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);
    }

    /**
     * get All Product Details From Server
     */
    // Send an API request to retrieve appropriate paginated data
    //  --> Send the request including an offset value (i.e `page`) as a query parameter.
    //  --> Deserialize and construct new model objects from the API response
    //  --> Append the new data objects to the existing set of items inside the array of items
    //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    private void getAllProductDetailsFromServer() {


        showDialog();
        hideNoInternetUI();


        String url_getProducts = "";
        String sizes = list_selected_productSize.toString();
        sizes = sizes.replace(", ", ",");
        Log.d(TAG, "Sizes Before : " + sizes);
        sizes = sizes.substring(1, sizes.length() - 1);
        Log.d(TAG, "Sizes After : " + sizes);
        sizes = dbhandler.convertEncodedString(sizes);


        if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard")) {
            if (Is_Price_Range_Enalbe == true) {


                url_getProducts = AllKeys.WEBSITE + "dashboard_product_category_type/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/nosorting/" + MINIMUM_VALUE_IN_RS + "/" + SELECTED_MAXIMUM_VALUE + "/" + Is_Price_Range_Enalbe + "/" + sizes + "/" + OFFSET + "/" + PAGENO + "";

                //ashboard_product_category_type($catid="",$min_amount="0",$maximum_amount="0",$offset="10",$page="0", $type="",$filter_status="",$filter_sizes="")

                url_getProducts = AllKeys.WEBSITE + "dashboard_product_category_type/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/" + MINIMUM_VALUE_IN_RS + "/" + SELECTED_MAXIMUM_VALUE + "/" + OFFSET + "/" + PAGENO + "/nosorting/" + Is_Price_Range_Enalbe + "/" + sizes;


            } else {


                url_getProducts = AllKeys.WEBSITE + "dashboard_product_category_type/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/" + SORTING_TYPE + "/" + MINIMUM_VALUE_IN_RS + "/" + SELECTED_MAXIMUM_VALUE + "/" + Is_Price_Range_Enalbe + "/" + sizes + "/" + OFFSET + "/" + PAGENO + "";
                url_getProducts = AllKeys.WEBSITE + "dashboard_product_category_type/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/" + MINIMUM_VALUE_IN_RS + "/" + SELECTED_MAXIMUM_VALUE + "/" + OFFSET + "/" + PAGENO + "/" + SORTING_TYPE + "/" + Is_Price_Range_Enalbe + "/" + sizes;


            }


        } else {

            if (Is_Price_Range_Enalbe == true) {


                //http://localhost/yelona/index.php/welcome/getProductByCategoryId/190/0/9999/9%2C10%2Cfree/10/0/nosorting/true/1satish
                url_getProducts = AllKeys.WEBSITE + "getProductByCategoryId/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/" + MINIMUM_VALUE_IN_RS + "/" + SELECTED_MAXIMUM_VALUE + "/" + OFFSET + "/" + PAGENO + "/nosorting/" + Is_Price_Range_Enalbe + "/" + sizes + "";
            } else {
                url_getProducts = AllKeys.WEBSITE + "getProductByCategoryId/" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + "/" + MINIMUM_VALUE_IN_RS + "/" + SELECTED_MAXIMUM_VALUE + "/" + OFFSET + "/" + PAGENO + "/" + SORTING_TYPE + "/" + Is_Price_Range_Enalbe + "/" + sizes + "";
            }


        }

        Log.d(TAG, "URL getAll  Items : " + url_getProducts);
        StringRequest str_get_products = new StringRequest(Request.Method.GET, url_getProducts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAll Items : " + response);

                if (PAGENO == 0) {

                    list_Products.clear();
                }


                if (response.contains("shipping_cost")) {


                    try {
                        String totalrecords = response.substring(response.indexOf(":") + 1, response.indexOf("{") - 1);

                        try {
                            TOTAL_RECORDS = Integer.parseInt(totalrecords);
                        } catch (NumberFormatException e) {

                            e.printStackTrace();
                        }

                        TOTAL_PAGES = TOTAL_RECORDS / OFFSET;
                        if ((TOTAL_PAGES % OFFSET) != 0) {
                            ++TOTAL_PAGES;
                        }


                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    response = response.replace("TotalRecords:" + TOTAL_RECORDS, "");


                    try {

                        response = dbhandler.convertToJsonFormat(response);
                        response = response.replace("null", "\"\"");
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        ArrayList<ProductData> list_current_Products = new ArrayList<ProductData>();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            String image_url;

                            if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard")) {
                                image_url = c.getString(AllKeys.TAG_IMAGE_URL);
                                Log.d(TAG, "Image Path : " + image_url);


                            } else {
                                image_url = c.getString(AllKeys.TAG_IMAGE_URL);
                                Log.d(TAG, "Image Path : " + image_url);
                                //http://www.yelona.com/uploads/original/97e55a825f7206dad3faea31fb3533fe.jpg


                            }

                            //  public ProductData(String shoppingcost, String productid, String productname, String description, String category_id, String image_url, String seller_id, String price, String offer_value, String offer_tag, String color, String weight, String height, String length, String width, String size, String mrp, String ship_date, String brand, String inventory, String committed_qty) {
                            Double mrp_rs = c.getDouble(AllKeys.TAG_MRP);
                            Double price_rs = c.getDouble(AllKeys.TAG_PRICE);

                            //Check Max Value Only First Time
                           /* if (PAGENO == 0 && Is_Price_Range_Enalbe == true ) {*/

                            if (MAXIMUM_VALUE_IN_RS < price_rs.intValue()) {
                                MAXIMUM_VALUE_IN_RS = price_rs.intValue();
                            }

                            /*}*/


                            ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHIPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), image_url, c.getString(AllKeys.TAG_SELLERID), String.valueOf(price_rs.intValue()), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), String.valueOf(mrp_rs.intValue()), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), "", "");
                            list_current_Products.add(prod);


                        }

                        if (PAGENO != 0) {

                            final int curSize = adapter_NewProduct.getItemCount();
                            list_Products.addAll(list_current_Products);

                            adapter_NewProduct.notifyItemRangeInserted(curSize, list_Products.size() - 1);


                        } else {
                            list_Products.addAll(list_current_Products);
                            adapter_NewProduct = new NewProductsRecyclerViewAdapter(context, list_Products, "grid", list_wishListProducts);
                            recyclerview_products.setAdapter(adapter_NewProduct);

                        }


                        if (MAXIMUM_VALUE_IN_RS >= 5000) {
                            price_rangebar.setTickEnd(MAXIMUM_VALUE_IN_RS);
                        } else {
                            price_rangebar.setTickEnd(5000);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }

                    txtnodata.setVisibility(View.GONE);
                    recyclerview_products.setVisibility(View.VISIBLE);


                } else if (response.contains("[]")) {
                    txtnodata.setVisibility(View.VISIBLE);
                    recyclerview_products.setVisibility(View.GONE);

                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    throw new RuntimeException(error.getMessage());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }


                Log.d(TAG, "Error in get  ItemsData : " + error.getMessage());

                if (error instanceof ServerError || error instanceof NetworkError) {
                    showInternetUI();
                    hideDialog();
                } else {

                    getAllProductDetailsFromServer();
                }


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        try {
            getMenuInflater().inflate(R.menu.item_display, menu);
            cart = (MenuItem) menu.findItem(R.id.menu_addtocart);

            LayerDrawable icon = (LayerDrawable) cart.getIcon();

            LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();


//int orderid=db.getMaxOrderId();


            //  String query = "select * from " + db.TABLE_ORDER + " where " + db.ORDER_ORDERID + "=" + userDetails.get(SessionManager.KEY_ORDERID) + "";


            //  Cursor cc = sd.rawQuery(query, null);
            // numorder = cc.getCount();
            //sessionmanager.StoreOrderDetails(""+numorder);
            // System.out.println("" + numorder);

            Utils.setBadgeCount(ItemDisplayActivity.this, icon, list_cartItemsId.size());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //getMenuInflater().inflate(R.menu.item_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Intent launchNextActivity = new Intent(context, NewDashBoardActivity.class);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launchNextActivity);

        } else if (item.getItemId() == R.id.menu_search) {
            Intent intent = new Intent(context, SearchActivity.class);
            intent.putExtra("ActivityName", TAG);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.menu_whishlist) {
            Intent intent = new Intent(context, WishListActivity.class);
            intent.putExtra("ActivityName", TAG);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.menu_addtocart) {

            getAllCartItemDetailsFromServer(true);
            /*sessionmanager.setCheckoutType("multiple", "1");
            Intent intent = new Intent(context, CheckoutActivity.class);
            intent.putExtra("ActivityName", TAG);
            startActivity(intent);
            finish();*/

        }
        return super.onOptionsItemSelected(item);
    }

    private void setAddToCartBadget() {

        try {
            LayerDrawable icon = (LayerDrawable) cart.getIcon();

            Utils.setBadgeCount(ItemDisplayActivity.this, icon, list_cartItemsId.size());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onBackPressed() {


        if (mSweetSheet.isShow()) {
            if (mSweetSheet.isShow()) {
                mSweetSheet.dismiss();
            }

        } else {
            super.onBackPressed();

            Intent launchNextActivity = new Intent(context, NewDashBoardActivity.class);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launchNextActivity);
            finish();
        }


    }


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
           /* if(swipeRefreshLayout.isRefreshing())
            {
                swipeRefreshLayout.setRefreshing(false);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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


          /*  if (position == 0) {


                if (IS_SIZE_SELECTED == false) {

                    holder.txtSize.setBackgroundResource(R.drawable.sizeround1);
                    holder.txtSize.setTextColor(Color.parseColor("#585858"));
                    holder.txtSize.setBackgroundResource(R.drawable.sizeround2);
                    holder.txtSize.setTextColor(Color.parseColor("#ffffff"));
                    SELECTED_SIZE = list_productSize.get(position);


                    //holder.txtSize
                }
            }*/

            if (list_selected_productSize.indexOf(list_productSize.get(position)) == -1) {

                holder.txtSize.setBackgroundResource(R.drawable.sizeround1);
                holder.txtSize.setTextColor(Color.parseColor("#585858"));


            } else {
                holder.txtSize.setBackgroundResource(R.drawable.sizeround1);
                holder.txtSize.setTextColor(Color.parseColor("#585858"));
                holder.txtSize.setBackgroundResource(R.drawable.sizeround2);
                holder.txtSize.setTextColor(Color.parseColor("#ffffff"));


            }


            holder.txtSize.setText(list_productSize.get(position));

            holder.txtSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int checkIndex = list_selected_productSize.indexOf(list_productSize.get(position));
                    if (checkIndex == -1) {
                        list_selected_productSize.add(list_productSize.get(position));
                    } else {
                        list_selected_productSize.remove(list_productSize.get(position));
                    }


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
