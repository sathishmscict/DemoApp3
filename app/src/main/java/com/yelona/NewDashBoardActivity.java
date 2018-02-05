package com.yelona;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Network;
import android.net.http.SslCertificate;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.crash.FirebaseCrash;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import com.yelona.adapter.DashBoardCategoryDisplayAdapter;
import com.yelona.adapter.FinalDashBoardRecyclerViewAdapter;
import com.yelona.adapter.MainCategoryDisplayAdapter;
import com.yelona.adapter.NewProductsDashBoardRecyclerViewAdapter;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.expandableData.BaseItem;
import com.yelona.expandableData.GroupItem;
import com.yelona.expandableData.Item;

import com.yelona.fragments.FragmentOffers;
import com.yelona.fragments.FragmentProfile;
import com.yelona.fragments.FragmentWallet;
import com.yelona.helper.AllKeys;
import com.yelona.helper.CustomRequest;
import com.yelona.helper.NetConnectivity;
import com.yelona.helper.Utils;
import com.yelona.pojo.DashBoardCategories;
import com.yelona.pojo.MainCategory;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;
import com.yelona.views.LevelBeamView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.OnItemClickListener;

import static com.yelona.R.id.fab;
import static com.yelona.helper.AllKeys.MY_SOCKET_TIMEOUT_MS;
import static com.yelona.helper.AllKeys.PAYBIZ_ENVIRONMENT;
import static com.yelona.helper.AllKeys.TAG_MOBILE_SLIDER_2;

public class NewDashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, BaseSliderView.OnSliderClickListener {

    private SpotsDialog pDialog;
    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private SliderLayout mDemoSlider;
    private String TAG = NewDashBoardActivity.class.getSimpleName();


    private ArrayList<String> list_MainBannerTypeName = new ArrayList<String>();
    private ArrayList<Integer> list_MainBannerTypeId = new ArrayList<Integer>();

    private ArrayList<String> list_DashboardBannerCategotyURL = new ArrayList<String>();
    private ArrayList<Integer> list_DashboardBannerId = new ArrayList<Integer>();

    HashMap<String, String> url_maps = new HashMap<String, String>();

    private EditText txtSearch;


    private ArrayList<ProductData> list_NewProducts = new ArrayList<ProductData>();
    private ArrayList<ProductData> list_BestSellerProducts = new ArrayList<ProductData>();
    private ArrayList<ProductData> list_FeaturedProducts = new ArrayList<ProductData>();
    private ArrayList<ProductData> list_NewArrivalProducts = new ArrayList<ProductData>();
    private ArrayList<ProductData> list_HotDealsProducts = new ArrayList<ProductData>();
    private ArrayList<ProductData> list_SpeacialOfferProducts = new ArrayList<ProductData>();


    private TextView txtBestSellerViewAll;
    private RecyclerView recycler_view_BestSellerProducts;
    private TextView txtNewArrivalViewAll;
    private RecyclerView recycler_view_NewArrivalProducts;


    private ArrayList<Integer> list_SliderId = new ArrayList<Integer>();
    private ArrayList<String> list_SliderImages = new ArrayList<String>();

    private ArrayList<String> list_SliderCategoryURL = new ArrayList<String>();

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
    private ArrayList<String> list_wishListProducts = new ArrayList<String>();
    private ArrayList<String> list_otherSlidersCategoryId = new ArrayList<String>();
    private MenuItem cart;
    private ArrayList<String> list_cartItemsId = new ArrayList<String>();
    private Typeface fonts;
    private RecyclerView recycler_dashboard_categories;
    private ArrayList<DashBoardCategories> list_New_DashBoard_Categories = new ArrayList<DashBoardCategories>();
    private RecyclerView recycler_main_categories;
    private ArrayList<MainCategory> listMainCategories = new ArrayList<MainCategory>();
    private ImageView img_final_banner1, img_final_banner2, img_final_banner3, img_final_banner4, img_final_banner5, img_final_banner6, img_final_banner7, img_final_banner8;
    private RecyclerView recyclerview_new_procuts;
    private TextView txtNewProductsViewAll;
    private SliderLayout mDemoSlider2;
    private TextView txtUsername;
    private TextView txtUseremail;
    private CircleImageView imgProfilePic;
    private boolean IsFirstTime = false;
    private LinearLayout llDashboard;
    private FrameLayout flFragments;

    private BottomBar bottomBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    //  private RecyclerView recyclerview_new_procuts2;

    /*End Expandable ListView*/


    AsyncTask<Void, Void, Void> mRegisterTask;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dash_board);

        try {
            //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error in ", Toast.LENGTH_SHORT).show();
        }


        // TODO: Move this to where you establish a user session

        try {
//Track View Details
            Answers.getInstance().logContentView(new ContentViewEvent());
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("DashBoard")
                    .putContentType("Dashbaord Screen")
                    .putContentId("dashboard-100"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        MyFirebaseInstanceIDService md = new MyFirebaseInstanceIDService();
        md.onTokenRefreshNew(context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

          /* toolbar.setLogo(R.drawable.yelona_full_logo);
            toolbar.setTitle("Yelona");
            toolbar.setSubtitle("Le Na ho To Yahi Se Lo Na");*/


        // toolbar.setSubtitle(getString(R.string.app_subtitle));
        setSupportActionBar(toolbar);
        fonts = Typeface.createFromAsset(getAssets(), "fonts/MavenPro-Regular.ttf");


        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }





       /* SweetAlertDialog sDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        //sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sDialog.getProgressHelper().isSpinning();
        sDialog.setTitleText("Loading");
        sDialog.setCancelable(false);
        sDialog.show();*/


        pDialog = new SpotsDialog(context);
        pDialog.setCancelable(false);

        sessionmanager = new SessionManager(context);

        userDetails = sessionmanager.getSessionDetails();


        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();



        // FirebaseCrash.report(new Exception("My first Android non-fatal error"));


      /*  try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            SearchResponse sr = client.prepareSearch()
                    .setQuery(QueryBuilders.matchQuery("message", "myProduct"))
                    .addAggregation(AggregationBuilders.terms("top_10_states")
                            .field("state").size(10))
                    .execute().actionGet();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);


      /*  swipeRefreshLayout.setRefreshing(true);*/
        swipeRefreshLayout.setOnRefreshListener(NewDashBoardActivity.this);


        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (NetConnectivity.isOnline(context)) {
                                            //new AsyncData1().execute();
                                            swipeRefreshLayout.setRefreshing(false);
                                            /*if (swipeRefreshLayout.isRefreshing()) {
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


        mDemoSlider = (SliderLayout) findViewById(R.id.final_slider1);
        mDemoSlider2 = (SliderLayout) findViewById(R.id.final_slider2);
        txtSearch = (EditText) findViewById(R.id.txtSearch);


        llDashboard = (LinearLayout) findViewById(R.id.llDashboard);
        flFragments = (FrameLayout) findViewById(R.id.container_body);


        recycler_dashboard_categories = (RecyclerView) findViewById(R.id.recycler_dashboard_categories);

        recycler_main_categories = (RecyclerView) findViewById(R.id.recycler_main_categories);


        txtNewProductsViewAll = (TextView) findViewById(R.id.txtNewProductsViewAll);
        ;
        txtBestSellerViewAll = (TextView) findViewById(R.id.txtBestSellerViewAll);
        recycler_view_BestSellerProducts = (RecyclerView) findViewById(R.id.recycler_best_seller_products);

        recycler_view_BestSellerProducts.setNestedScrollingEnabled(false);


        recyclerview_new_procuts = (RecyclerView) findViewById(R.id.recycler_new_products);
        //  recyclerview_new_procuts2 = (RecyclerView) findViewById(R.id.recycler_new_products2);
        recyclerview_new_procuts.setNestedScrollingEnabled(false);

        txtNewArrivalViewAll = (TextView) findViewById(R.id.txtNewArrivalViewAll);
        recycler_view_NewArrivalProducts = (RecyclerView) findViewById(R.id.recycler_new_arrival_products);

        recycler_view_NewArrivalProducts.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager3 = new GridLayoutManager(NewDashBoardActivity.this, 2);
        recycler_view_BestSellerProducts.setLayoutManager(layoutManager3);

        RecyclerView.LayoutManager layoutManager_NP = new GridLayoutManager(NewDashBoardActivity.this, 2);
        recyclerview_new_procuts.setLayoutManager(layoutManager_NP);

        //  RecyclerView.LayoutManager layoutManager_NP2 = new GridLayoutManager(NewDashBoardActivity.this, 2);
        // recyclerview_new_procuts2.setLayoutManager(layoutManager_NP2);


        RecyclerView.LayoutManager layoutManager4 = new GridLayoutManager(NewDashBoardActivity.this, 2);
        recycler_view_NewArrivalProducts.setLayoutManager(layoutManager4);


        RecyclerView.LayoutManager layoutManager_dashboard_categories = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recycler_dashboard_categories.setLayoutManager(layoutManager_dashboard_categories);
        recycler_dashboard_categories.setItemAnimator(new DefaultItemAnimator());

        SnapHelper snapHelper2 = new LinearSnapHelper();
        snapHelper2.attachToRecyclerView(recycler_dashboard_categories);


        RecyclerView.LayoutManager layoutManager_main_categories = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recycler_main_categories.setLayoutManager(layoutManager_main_categories);
        //  recycler_main_categories.setItemAnimator(new DefaultItemAnimator());

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recycler_main_categories);


        recycler_main_categories.addOnItemTouchListener(new dbhandler.RecyclerTouchListener(getApplicationContext(), recycler_main_categories, new dbhandler.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                SetCategoryDetails("category", listMainCategories.get(position).getCategoryId(), listMainCategories.get(position).getCategoryName());

                Intent intent = new Intent(context, SubCategories.class);
                startActivity(intent);
                finish();


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));




       /* recycler_dashboard_categories.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));*/


      /*  mDividerItemDecoration = new DividerItemDecoration(recycler_dashboard_categories.getContext(),
                layoutManager_dashboard_categories.getOrientation());
        recycler_dashboard_categories.addItemDecoration(mDividerItemDecoration);*/


        img_final_banner1 = (ImageView) findViewById(R.id.img_final_banner1);
        img_final_banner2 = (ImageView) findViewById(R.id.img_final_banner2);
        img_final_banner3 = (ImageView) findViewById(R.id.img_final_banner3);
        img_final_banner4 = (ImageView) findViewById(R.id.img_final_banner4);
        img_final_banner5 = (ImageView) findViewById(R.id.img_final_banner5);
        img_final_banner6 = (ImageView) findViewById(R.id.img_final_banner6);
        img_final_banner7 = (ImageView) findViewById(R.id.img_final_banner7);
        img_final_banner8 = (ImageView) findViewById(R.id.img_final_banner8);


        //   txtNewProductsViewAll.setVisibility(View.GONE);
        //  txtFeaturedProductsViewAll.setVisibility(View.GONE);
        //  txtBestSellerViewAll.setVisibility(View.GONE);
        //  txtNewArrivalViewAll.setVisibility(View.GONE);
        // txtHotDealsViewAll.setVisibility(View.GONE);
        // txtSpecialOfferViewAll.setVisibility(View.GONE);


        img_final_banner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // SetCategoryDetails("dashboard", String.valueOf(list_bannerTypeId.get(list_bannerTypeName.indexOf("New Product"))), "New Product");

                try {
                    if (NetConnectivity.isOnline(context)) {

                        if (list_DashboardBannerCategotyURL.size() > 0) {
                            String catid = list_DashboardBannerCategotyURL.get(0);
                            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                            Integer.parseInt(catid);
                            SetCategoryDetails("category", catid, "");
                        }

                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });
        img_final_banner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (NetConnectivity.isOnline(context)) {

                        if (list_DashboardBannerCategotyURL.size() > 0) {
                            String catid = list_DashboardBannerCategotyURL.get(1);
                            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                            Integer.parseInt(catid);
                            SetCategoryDetails("category", catid, "");
                        }
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });
        img_final_banner3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (NetConnectivity.isOnline(context)) {
                        if (list_DashboardBannerCategotyURL.size() > 0) {

                            String catid = list_DashboardBannerCategotyURL.get(2);
                            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                            Integer.parseInt(catid);
                            SetCategoryDetails("category", catid, "");
                        }
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });

        img_final_banner4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (NetConnectivity.isOnline(context)) {

                        if (list_DashboardBannerCategotyURL.size() > 0) {
                            String catid = list_DashboardBannerCategotyURL.get(3);
                            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                            Integer.parseInt(catid);
                            SetCategoryDetails("category", catid, "");
                        }

                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });
        img_final_banner5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (NetConnectivity.isOnline(context)) {

                        if (list_DashboardBannerCategotyURL.size() > 0) {
                            String catid = list_DashboardBannerCategotyURL.get(4);
                            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                            Integer.parseInt(catid);
                            SetCategoryDetails("category", catid, "");
                        }
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });
        img_final_banner6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (NetConnectivity.isOnline(context)) {

                        if (list_DashboardBannerCategotyURL.size() > 0) {
                            String catid = list_DashboardBannerCategotyURL.get(5);
                            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                            Integer.parseInt(catid);
                            SetCategoryDetails("category", catid, "");
                        }
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });
        img_final_banner7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (NetConnectivity.isOnline(context)) {
                        if (list_DashboardBannerCategotyURL.size() > 0) {

                            String catid = list_DashboardBannerCategotyURL.get(6);
                            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                            Integer.parseInt(catid);
                            SetCategoryDetails("category", catid, "");
                        }
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });
        img_final_banner8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (NetConnectivity.isOnline(context)) {

                        String catid = list_DashboardBannerCategotyURL.get(7);
                        catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
                        Integer.parseInt(catid);
                        SetCategoryDetails("category", catid, "");
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });


        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {
            bottomBar.setItems(R.xml.bottombar_tabs_before_login);
        } else {
            bottomBar.setItems(R.xml.bottombar_tabs_after_login);
        }


        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {


                //  Toast.makeText(context, "Tab Listeneer" + getTabDetails(tabId, false), Toast.LENGTH_SHORT).show();

                Fragment fragment = null;

                String title = getString(R.string.app_name);

                if (tabId == R.id.tab_home) {

                    //fragment = new FragmentHome();
                    // Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                    title = "Yelona";


                } else if (tabId == R.id.tab_wallet) {
                    fragment = new FragmentWallet();
                    // Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                    title = "Wallet";


                } else if (tabId == R.id.tab_profile) {

                    if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        fragment = new FragmentProfile();
                        // Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                        title = "Profile";
                    }


                } else if (tabId == R.id.tab_offer) {
                    fragment = new FragmentOffers();
                    // Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                    title = "Offers";


                } else if (tabId == R.id.tab_logout) {

                    if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        sessionmanager.logoutUser();
                        finish();
                    }

                }


                if (fragment != null) {


                    try {


                        getSupportActionBar().setTitle(title);


                        setTitle(title);


                        if (NetConnectivity.isOnline(context)) {


                            FragmentManager fragmentManager = getSupportFragmentManager();

                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            fragmentTransaction.replace(R.id.container_body, fragment);
                            fragmentTransaction.commit();


                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);

                        } else {
                            // checkInternet();
                            Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    llDashboard.setVisibility(View.GONE);
                    flFragments.setVisibility(View.VISIBLE);
                } else {
                    llDashboard.setVisibility(View.VISIBLE);
                    flFragments.setVisibility(View.GONE);
                    setTitle(getString(R.string.app_name));
                }


            }
        });

       /* bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {

                Toast.makeText(getApplicationContext(), "Click Listener" + getTabDetails(tabId, true), Toast.LENGTH_LONG).show();
            }
        });*/

        /*BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_nearby);
        nearby.setBadgeCount(5);*/


        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, SearchActivity.class);
                //Intent i = new Intent(context, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


        txtNewProductsViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (NetConnectivity.isOnline(context)) {

                        if (list_MainBannerTypeId.size() > 0) {

                            int _index = list_MainBannerTypeId.indexOf(7);


                            SetCategoryDetails("dashboard", String.valueOf(list_MainBannerTypeId.get(_index)), list_MainBannerTypeName.get(_index));
                        }
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


                //SetCategoryDetails("dashboard", String.valueOf(list_MainBannerTypeId.get(list_MainBannerTypeName.indexOf("New Product"))), "New Product");
                // Toast.makeText(context, "New Products", Toast.LENGTH_SHORT).show();
            }
        });


        txtBestSellerViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    try {
                        if (NetConnectivity.isOnline(context)) {

                            if (list_MainBannerTypeId.size() > 0) {

                                int _index = list_MainBannerTypeId.indexOf(1);


                                SetCategoryDetails("dashboard", String.valueOf(list_MainBannerTypeId.get(_index)), list_MainBannerTypeName.get(_index));
                            }
                        } else {
                            Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                  /*  int _index = list_MainBannerTypeId.indexOf(1);


                    SetCategoryDetails("dashboard", String.valueOf(list_MainBannerTypeId.get(_index)), list_MainBannerTypeName.get(_index));

                 */

                } catch (Exception e) {
                    //  SetCategoryDetails("dashboard", String.valueOf(list_MainBannerTypeId.get(list_MainBannerTypeName.indexOf("Best Seller"))), "Best Seller");
                    e.printStackTrace();
                }
                // Toast.makeText(context, "Best Seller Or BestSales", Toast.LENGTH_SHORT).show();
            }
        });

        txtNewArrivalViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (NetConnectivity.isOnline(context)) {

                        if (list_MainBannerTypeId.size() > 0) {

                            int _index = list_MainBannerTypeId.indexOf(2);


                            SetCategoryDetails("dashboard", String.valueOf(list_MainBannerTypeId.get(_index)), list_MainBannerTypeName.get(_index));
                        }
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            /*    int _index = list_MainBannerTypeId.indexOf(2);


                SetCategoryDetails("dashboard", String.valueOf(list_MainBannerTypeId.get(_index)), list_MainBannerTypeName.get(_index));
*/

                // Toast.makeText(context, "New Arrival", Toast.LENGTH_SHORT).show();
            }
        });


        // FillDashBoardSliders();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setVisibility(View.VISIBLE);

        //app:headerLayout="@layout/nav_header_menu"
        //  View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_dash_board);

        try {


           /* txtUsername = (TextView) headerLayout.findViewById(R.id.txtname);
            txtUseremail = (TextView) headerLayout.findViewById(R.id.txtemail);
            imgProfilePic = (ImageView) headerLayout.findViewById(R.id.imgProfilePic);
*/
            txtUsername = (TextView) findViewById(R.id.txtname);
            txtUseremail = (TextView) findViewById(R.id.txtemail);
            imgProfilePic = (CircleImageView) findViewById(R.id.imgProfilePic);


            Log.d(TAG, "User Logo : " + userDetails.get(SessionManager.KEY_USER_AVATAR));

            String image_url = userDetails.get(SessionManager.KEY_USER_AVATAR);

            if (image_url.contains("uploads") && !image_url.contains("yelona.com")) {
                image_url = AllKeys.RESOURSES + image_url;

            }


            try {
                SetUserProfilePictireFromBase64EnodedString();
            } catch (Exception e) {
                e.printStackTrace();
                // Glide.with(context).load(image_url).crossFade().placeholder(R.drawable.icon_userlogo).error(R.drawable.icon_userlogo).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgProfilePic);
            }



       /*     imgProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    *//*Fragment fragment = new FragmentProfile();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();


                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);*//*


                    getMenuInflater().inflate(R.menu.activity_dash_board_drawer, menu);
                    MenuItem mProfileFrag = menu.findItem(R.id.nav_profile);

                    onNavigationItemSelected(mProfileFrag);


                    *//*MenuItem mDefaultFrag = (MenuItem) navigationView.findViewById(R.id.nav_profile);
                    onNavigationItemSelected(mDefaultFrag);*//*


                }
            });*/


            if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {

                txtUsername.setText("Welcome to Yelona");
                txtUseremail.setText("");
            } else {
                txtUseremail.setText("" + userDetails.get(SessionManager.KEY_USER_EMAIL));
                txtUsername.setText("" + userDetails.get(SessionManager.KEY_USER_FIRSTNAME));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


     /*   *//**
         * Get All Slider Master Related Data
         *//*
        //Check Category details inserted or not
        String query = "select * from " + dbhandler.TABLE_CATEGORY + "";
        Cursor cursor_checkCategory = sd.rawQuery(query, null);
        if (cursor_checkCategory.getCount() <= 0) {

            getAllCategoryDetailsFromServer();
        }


       if (!userDetails.get(SessionManager.KEY_USERID).equals("0"))
       {

            // TODO: Use the current user's information
            // You can call any combination of these three methods
            Crashlytics.setUserIdentifier(userDetails.get(SessionManager.KEY_USERID));
            Crashlytics.setUserEmail(userDetails.get(SessionManager.KEY_USER_EMAIL) + " : " + userDetails.get(SessionManager.KEY_USER_MOBILE));
            Crashlytics.setUserName(userDetails.get(SessionManager.KEY_USER_FIRSTNAME));


            if (userDetails.get(SessionManager.KEY_VERSTATUS).equals("1")) {

                if (userDetails.get(SessionManager.KEY_NEWACTIVITY_NAME).equals("")) {
                    getAllSliderMasterDetailsFromServer();

                    getAllWishListDetailsFromServer();
                    //Here check userid
                    getAllCartItemDetailsFromServer(false);
                    confMenu();
                } else {
                    try {


                        Intent i = null;
                        try {
                            i = new Intent(context, Class.forName(context.getPackageName() + "." + userDetails.get(SessionManager.KEY_NEWACTIVITY_NAME)));
                            //i.putExtra("ActivityName", getIntent().getStringExtra("ActivityName"));
                            sessionmanager.setNewUserSession("");
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

            } else {
                Intent ii = new Intent(context, VerificationActivity.class);
                // Intent ii = new Intent(context, LoginActivity.class);
                startActivity(ii);
                finish();

            }


        } else
            {

            getAllSliderMasterDetailsFromServer();
            confMenu();

        }*/


    }
    //onCraete Completed


    /**
     * Update FCM token onm service
     */

    private void UpdateFcmTokenDetailsToServer()
    {
        showDialog();

        String fcm_tokenid = "";
        try {
            MyFirebaseInstanceIDService mid = new MyFirebaseInstanceIDService();
            fcm_tokenid = String.valueOf(mid.onTokenRefreshNew(context));

        } catch (Exception e) {
            fcm_tokenid = "";
            e.printStackTrace();
        }




        String url = null;
        if(userDetails.get(SessionManager.KEY_USERID).equals("0"))
        {


            try {
                String userid = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                //+Build.MODEL
                userid = userid+Build.BRAND;
                userid = userid.replace(" ","");
                url = AllKeys.WEBSITE + "updateFCM/get/"+ userid +"/android/"+ dbhandler.convertEncodedString(fcm_tokenid) +"";

                // params.put("userid","787");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else
        {
            url = AllKeys.WEBSITE + "updateFCM/get/"+ userDetails.get(SessionManager.KEY_USERID) +"/android/"+ fcm_tokenid +"";

            //params.put("userid", userDetails.get(SessionManager.KEY_USERID));
        }







        //String url = AllKeys.WEBSITE + "updateSellerFCM?device_type=android&sellerid=" + userDetails.get(SessionManager.KEY_SELLER_ID) + "&fcm=" + fcm_tokenid + "";


        Log.d(TAG, "URL  updateFCM : " + url);


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response updateFCM : " + response.toString());
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();
                } else {
                    UpdateFcmTokenDetailsToServer();
                }
            }
        }) /*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();

                String fcm_tokenid = "";
                try {
                    MyFirebaseInstanceIDService mid = new MyFirebaseInstanceIDService();
                    fcm_tokenid = String.valueOf(mid.onTokenRefreshNew(context));

                } catch (Exception e) {
                    fcm_tokenid = "";
                    e.printStackTrace();
                }



                params.put("device_type", "android");
                params.put("calltype", "post");


                if(userDetails.get(SessionManager.KEY_USERID).equals("0"))
                {

                    params.put("userid", Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID)+Build.MODEL);
                   // params.put("userid","787");


                }
                else
                {

                params.put("userid", userDetails.get(SessionManager.KEY_USERID));
                }

                params.put("fcm", dbhandler.convertEncodedString(fcm_tokenid));

                Log.d(TAG, "updateFCM Params :" + params.toString());


                return params;
            }
        }*/;

        MyApplication.getInstance().addToRequestQueue(request);


    }
    //Complete Update FCM on Server

    private void getAllCartItemDetailsFromServer(final boolean isCheckout) {
/*
        private void manageAddToCartDetailsToServer(final String ProductId, final String Quantity, final String Type)
        {
*/

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


                            sessionmanager.setCartItemsIdDetails("");
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
                    hideDialog();
                } else {

                    getAllCartItemDetailsFromServer(isCheckout);
                }

                Log.d(TAG, "Error in ManageWishList : " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_manageCart);


    }


    private void SetUserProfilePictireFromBase64EnodedString() {
        try {

            try {
                if (!userDetails.get(SessionManager.KEY_USER_AVATAR).equals(""))
                {

                    String userprofile = userDetails.get(SessionManager.KEY_USER_AVATAR);
                    if (!userprofile.equals("") && !userprofile.equals("null"))
                    {
                        if (!userprofile.contains("google") && !userprofile.contains("facebook")) {
                            userprofile = AllKeys.RESOURSES + userprofile;

                        } else if (userprofile.contains("google")) {
                            userprofile = userprofile.replace("sz=50", "sz=200");
                        } else if (userprofile.contains("facebook")) {
                            userprofile = userprofile.replace("normal", "large");

                        }
                       /* else
                        {
                            userprofile = AllKeys.RESOURSES+userprofile;

                        }*/

                        try {
                            Bitmap image = dbhandler.getBitmapFromURL(userprofile);
                            String enc = dbhandler.getStringImage(image);
                            sessionmanager.setEncodedImage(enc);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Glide.with(context).load(userprofile).crossFade().placeholder(R.drawable.icon_userlogo).error(R.drawable.icon_userlogo).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgProfilePic);
                        }
                    }


                    URL url = new URL(userprofile);
                    Log.d("Image URL : ", "" + userprofile);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    String enc = dbhandler.getStringImage(image);
                    sessionmanager.setEncodedImage(enc);
                    imgProfilePic.setImageBitmap(image);
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

    private void getAllWishListDetailsFromServer() {
        String url_getProducts = AllKeys.WEBSITE + "getWishlistByUserid/" + userDetails.get(SessionManager.KEY_USERID) + "";


        Log.d(TAG, "URL get WishList: " + url_getProducts);
        StringRequest str_get_products = new StringRequest(Request.Method.GET, url_getProducts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAll Hot Deals Products : " + response);
                list_wishListProducts.clear();
                // list_wishListProducts.add("items");


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


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null) {
                    getAllWishListDetailsFromServer();

                } else {

                    hideDialog();
                }
                Log.d(TAG, "Error in get WishList : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);
    }

    private void SetCategoryDetails(String categoryType, String categoryId, String categoryName) {

        Log.d(TAG, "Item Click Data : CategoryType : " + categoryType + " CategoryId : " + categoryId + " CategoryName : " + categoryName);

        // sessionmanager.setCategoryTypeAndIdDetails("dashboard",String.valueOf(list_bannerTypeId.get(list_bannerTypeName.indexOf("Hot Deals"))),"Hot Deals");
        sessionmanager.setCategoryTypeAndIdDetails(categoryType, categoryId, categoryName);

        Intent intent = new Intent(context, ItemDisplayActivity.class);
        startActivity(intent);
        finish();


    }
//onCreate Completed]


    @Override
    public void onRefresh() {
        if (NetConnectivity.isOnline(context)) {
            //new AsyncData1().execute();
            //  swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);
            //  swipeRefreshLayout.setRefreshing(true);
            //swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);


            Log.d("swipe from ", "onrefresh method");

            LoadDataFromServer();


        } else {
            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }


    public void LoadDataFromServer() {
        /**
         * Get All Slider Master Related Data
         */
        //Check Category details inserted or not
        String query = "select * from " + dbhandler.TABLE_CATEGORY + "";
        Cursor cursor_checkCategory = sd.rawQuery(query, null);
        if (cursor_checkCategory.getCount() <= 0) {

            getAllCategoryDetailsFromServer();
        }

        UpdateFcmTokenDetailsToServer();
        if (!userDetails.get(SessionManager.KEY_USERID).equals("0"))
        {

            // TODO: Use the current user's information
            // You can call any combination of these three methods
            Crashlytics.setUserIdentifier(userDetails.get(SessionManager.KEY_USERID));
            Crashlytics.setUserEmail(userDetails.get(SessionManager.KEY_USER_EMAIL) + " : " + userDetails.get(SessionManager.KEY_USER_MOBILE));
            Crashlytics.setUserName(userDetails.get(SessionManager.KEY_USER_FIRSTNAME));


            if (userDetails.get(SessionManager.KEY_VERSTATUS).equals("1"))
            {


                if (userDetails.get(SessionManager.KEY_NOTIFICATION_STATUS).equals("true") && !userDetails.get(SessionManager.KEY_PRODUCT_ID).equals("0"))
                {
                    sessionmanager.setNotificationStatus("false",userDetails.get(SessionManager.KEY_PRODUCT_ID),userDetails.get(SessionManager.KEY_CATEGORY_ID));

                    sessionmanager.setCategoryTypeAndIdDetails("category", userDetails.get(SessionManager.KEY_CATEGORY_ID), "");

                    Intent intent = new Intent(context,
                            SingleItemActivity.class);

                    intent.putExtra("ActivityName", "ItemDisplayActivity");
                    sessionmanager.setActivityName("ItemDisplayActivity");
                    startActivity(intent);
                    finish();

                }
                else
                {
                    sessionmanager.setCategoryTypeAndIdDetails("", "", "");


                    if (userDetails.get(SessionManager.KEY_NEWACTIVITY_NAME).equals("")) {

                      //  UpdateFcmTokenDetailsToServer();
                        getAllSliderMasterDetailsFromServer();

                        getAllWishListDetailsFromServer();
                        //Here check userid
                        getAllCartItemDetailsFromServer(false);
                        confMenu();
                    } else
                    {
                        try {


                            Intent i = null;
                            try {
                                i = new Intent(context, Class.forName(context.getPackageName() + "." + userDetails.get(SessionManager.KEY_NEWACTIVITY_NAME)));
                                //i.putExtra("ActivityName", getIntent().getStringExtra("ActivityName"));
                                sessionmanager.setNewUserSession("");
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

                }


            } else {
                Intent ii = new Intent(context, VerificationActivity.class);
                // Intent ii = new Intent(context, LoginActivity.class);
                startActivity(ii);
                finish();

            }


        } else {

            if (userDetails.get(SessionManager.KEY_NOTIFICATION_STATUS).equals("true") && !userDetails.get(SessionManager.KEY_PRODUCT_ID).equals("0"))
            {
                sessionmanager.setNotificationStatus("false",userDetails.get(SessionManager.KEY_PRODUCT_ID),userDetails.get(SessionManager.KEY_CATEGORY_ID));

                sessionmanager.setCategoryTypeAndIdDetails("category", userDetails.get(SessionManager.KEY_CATEGORY_ID), "");

                Intent intent = new Intent(context,
                        SingleItemActivity.class);

                intent.putExtra("ActivityName", "ItemDisplayActivity");
                sessionmanager.setActivityName("ItemDisplayActivity");
                startActivity(intent);
                finish();

            }else
            {

                getAllSliderMasterDetailsFromServer();
                confMenu();
            }



        }
    }


    /**
     * Get All New Product Details From Server
     */
    private void getAllNewProductsFromServer() {

        showDialog();
        int catid = list_MainBannerTypeName.indexOf("New Product");
        catid = list_MainBannerTypeId.get(list_MainBannerTypeId.indexOf(7));


        String url_get_new_arrival_products = AllKeys.WEBSITE + "dashboard_product_category_type/" + catid + "/HtoL";

        //dashboard_product_category_type($catid="",$min_amount="0",$maximum_amount="0",$offset="10",$page="0", $type="",$filter_status="",$filter_sizes="")
        url_get_new_arrival_products = AllKeys.WEBSITE + "dashboard_product_category_type/" + catid + "/0/100000/10/0/HtoL/false/";


        Log.d(TAG, "URL get New Product : " + url_get_new_arrival_products);
        StringRequest str_get_products = new StringRequest(Request.Method.GET, url_get_new_arrival_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAll New Product : " + response);
                list_NewProducts.clear();
                if (response.contains("shipping_cost")) {

                    try {

                        String totalrecords = response.substring(response.indexOf(":") + 1, response.indexOf("{") - 1);

                        response = response.replace("TotalRecords:" + totalrecords, "");

                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject c = arr.getJSONObject(i);
                            //  public ProductData(String shoppingcost, String productid, String productname, String description, String category_id, String image_url, String seller_id, String price, String offer_value, String offer_tag, String color, String weight, String height, String length, String width, String size, String mrp, String ship_date, String brand, String inventory, String committed_qty) {
                            ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHIPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), c.getString(AllKeys.TAG_PRICE), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), c.getString(AllKeys.TAG_MRP), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), "", "");
                            list_NewProducts.add(prod);

                            if (i == 3) {

                                FinalDashBoardRecyclerViewAdapter adapter_NewProduct = new FinalDashBoardRecyclerViewAdapter(context, list_NewProducts, list_wishListProducts);
                                // recycler_view_NewProducts.setAdapter(adapter_NewProduct);
                                recyclerview_new_procuts.setAdapter(adapter_NewProduct);
                                //   recyclerview_new_procuts2.setAdapter(adapter_NewProduct);
                                break;

                            }

                        }
                        // FinalDashBoardRecyclerViewAdapter adapter_NewProduct = new FinalDashBoardRecyclerViewAdapter(context, list_NewProducts, list_wishListProducts);
                        //recycler_view_NewProducts.setAdapter(adapter_NewProduct);
                        // FinalDashBoardRecyclerViewAdapter adapter_NewProduct = new FinalDashBoardRecyclerViewAdapter(context, list_NewProducts, list_wishListProducts);
                        // recycler_view_NewProducts.setAdapter(adapter_NewProduct);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();

                } else {
                    getAllNewProductsFromServer();
                }
                Log.d(TAG, "Error in get New Product : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);

    }

    /**
     * Complete Get All New Products Details from server
     */


    /**
     * Complete GEt all Featured Product dEtails From Server
     */

    /**
     * Get All Best Seller Product Detauls From Server
     */
    private void getAllBestSellerProductsFromServer() {

        showDialog();
        int catid = list_MainBannerTypeName.indexOf("BestSales");
        //catid = list_MainBannerTypeId.indexOf(1);
        catid = list_MainBannerTypeId.get(list_MainBannerTypeId.indexOf(1));


        String url_get_new_arrival_products = AllKeys.WEBSITE + "dashboard_product_category_type/" + catid + "/HtoL";

        url_get_new_arrival_products = AllKeys.WEBSITE + "dashboard_product_category_type/" + catid + "/0/100000/10/0/HtoL/false/";


        Log.d(TAG, "URL get BestSales : " + url_get_new_arrival_products);
        StringRequest str_get_products = new StringRequest(Request.Method.GET, url_get_new_arrival_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAll BestSales : " + response);
                list_BestSellerProducts.clear();
                if (response.contains("shipping_cost")) {

                    try {
                        String totalrecords = response.substring(response.indexOf(":") + 1, response.indexOf("{") - 1);

                        response = response.replace("TotalRecords:" + totalrecords, "");


                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject c = arr.getJSONObject(i);
                            //  public ProductData(String shoppingcost, String productid, String productname, String description, String category_id, String image_url, String seller_id, String price, String offer_value, String offer_tag, String color, String weight, String height, String length, String width, String size, String mrp, String ship_date, String brand, String inventory, String committed_qty) {
                            ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHIPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), c.getString(AllKeys.TAG_PRICE), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), c.getString(AllKeys.TAG_MRP), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), "", "");
                            list_BestSellerProducts.add(prod);

                            if (i == 3) {

                                FinalDashBoardRecyclerViewAdapter adapter_NewProduct = new FinalDashBoardRecyclerViewAdapter(context, list_BestSellerProducts, list_wishListProducts);
                                recycler_view_BestSellerProducts.setAdapter(adapter_NewProduct);

                                break;

                            }

                        }
                        //NewProductsDashBoardRecyclerViewAdapter adapter_NewProduct = new NewProductsDashBoardRecyclerViewAdapter(context, list_BestSellerProducts, list_wishListProducts);
                        //recycler_view_BestSellerProducts.setAdapter(adapter_NewProduct);

                        // FinalDashBoardRecyclerViewAdapter adapter_NewProduct = new FinalDashBoardRecyclerViewAdapter(context, list_BestSellerProducts, list_wishListProducts);
                        // recycler_view_BestSellerProducts.setAdapter(adapter_NewProduct);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {

                    hideDialog();
                } else {
                    getAllBestSellerProductsFromServer();

                }
                Log.d(TAG, "Error in get BestSales : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);

    }

    /**
     * Complete Get All Best Seller Details From Server
     */


    /**
     * Get All New Arrival Product Detauls From Server
     */
    private void getAllNewArrivalProductsFromServer() {

        int catid = list_MainBannerTypeName.indexOf("New Arrival");
        catid = list_MainBannerTypeId.indexOf(2);
        catid = list_MainBannerTypeId.get(list_MainBannerTypeId.indexOf(2));


        String url_get_new_arrival_products = AllKeys.WEBSITE + "dashboard_product_category_type/" + catid + "/HtoL";

        url_get_new_arrival_products = AllKeys.WEBSITE + "dashboard_product_category_type/" + catid + "/0/100000/10/0/HtoL/false/";


        Log.d(TAG, "URL getNewArrivalProducts : " + url_get_new_arrival_products);
        StringRequest str_get_products = new StringRequest(Request.Method.GET, url_get_new_arrival_products, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAllNewArrivalProducts : " + response);
                list_NewArrivalProducts.clear();
                if (response.contains("shipping_cost")) {

                    try {

                        String totalrecords = response.substring(response.indexOf(":") + 1, response.indexOf("{") - 1);

                        response = response.replace("TotalRecords:" + totalrecords, "");


                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject c = arr.getJSONObject(i);
                            //  public ProductData(String shoppingcost, String productid, String productname, String description, String category_id, String image_url, String seller_id, String price, String offer_value, String offer_tag, String color, String weight, String height, String length, String width, String size, String mrp, String ship_date, String brand, String inventory, String committed_qty) {
                            ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHIPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), c.getString(AllKeys.TAG_PRICE), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), c.getString(AllKeys.TAG_MRP), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY), "", "");
                            list_NewArrivalProducts.add(prod);

                            if (i == 3) {

                                FinalDashBoardRecyclerViewAdapter adapter_NewProduct = new FinalDashBoardRecyclerViewAdapter(context, list_NewArrivalProducts, list_wishListProducts);
                                recycler_view_NewArrivalProducts.setAdapter(adapter_NewProduct);
                                break;

                            }

                        }
                      /*  NewProductsDashBoardRecyclerViewAdapter adapter_NewProduct = new NewProductsDashBoardRecyclerViewAdapter(context, list_NewArrivalProducts, list_wishListProducts);
                        recycler_view_NewArrivalProducts.setAdapter(adapter_NewProduct);*/

                        FinalDashBoardRecyclerViewAdapter adapter_NewProduct = new FinalDashBoardRecyclerViewAdapter(context, list_NewArrivalProducts, list_wishListProducts);
                        recycler_view_NewArrivalProducts.setAdapter(adapter_NewProduct);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Error in getNewArrivalItemsData : " + error.getMessage());
                if (error instanceof ServerError || error instanceof NetworkError) {


                    hideDialog();
                } else {
                    getAllNewArrivalProductsFromServer();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);

    }

    /**
     * Complete Get All New Arrival Product Details From Server
     */


    private void getAllCategoryDetailsFromServer() {
        showDialog();

        String url_getallcategories = AllKeys.WEBSITE + "getAllCategory";
        Log.d(TAG, "url GetAllCategories :" + url_getallcategories);
        StringRequest str_getallcategories = new StringRequest(Request.Method.GET, url_getallcategories, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "GetAllCategories Response :" + response);
                sd.delete(dbhandler.TABLE_CATEGORY, null, null);
                if (response.contains("name")) {

                    try {
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            try {
                                ContentValues cv = new ContentValues();
                                cv.put(dbhandler.CATEGORY_ID, c.getString(AllKeys.TAG_CATEGORY_ID));
                                cv.put(dbhandler.CATEGORY_NAME, c.getString(AllKeys.TAG_CATEGORY_NAME));
                                cv.put(dbhandler.CATEGORY_PARENTID, c.getString(AllKeys.TAG_CATEGORY_PARENTID));
                                cv.put(dbhandler.CATEGORY_TYPE, c.getString(AllKeys.TAG_CATEGORY_TYPE));
                                cv.put(dbhandler.CATEGORY_DELETED_AT, c.getString(AllKeys.TAG_CATEGORY_DELETED_AT));
                                cv.put(dbhandler.CATEGORY_CREATED_AT, c.getString(AllKeys.TAG_CATEGORY_CREATED_AT));
                                cv.put(dbhandler.CATEGORY_UPDATED_AT, c.getString(AllKeys.TAG_CATEGORY_UPDATED_AT));
                                cv.put(dbhandler.CATEGORY_MLM_DISCOUNT, c.getString(AllKeys.TAG_CATEGORY_MLM_DISCOUNT));
                                cv.put(dbhandler.CATEGORY_SEQUENCE_NO, c.getString(AllKeys.TAG_CATEGORY_SEQUENCE_NO));
                                cv.put(dbhandler.CATEGORY_SHIPPING_COST, c.getString(AllKeys.TAG_CATEGORY_SHIPING_COST));
                                cv.put(dbhandler.CATEGORY_SHIPING_COST_SELLER, c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_SELLER));
                                cv.put(dbhandler.CATEGORY_SHIPPING_COST_BUYER, c.getString(AllKeys.TAG_CATEGORY_SHIPPING_COST_BUYER));

                                sd.insert(dbhandler.TABLE_CATEGORY, null, cv);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error in Inserting Data : " + e.getMessage());
                                FirebaseCrash.report(new Exception("Error Inserting Category Data : " + e.getMessage()));
                                hideDialog();
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                }
                hideDialog();
                confMenu();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();
                } else {
                    getAllCategoryDetailsFromServer();

                }


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getallcategories);

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //satish

        //Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();

        if (list_SliderCategoryURL.size() > 0) {

            int id = list_SliderId.indexOf(Integer.parseInt(slider.getBundle().get("extra").toString()));
            String catid = list_SliderCategoryURL.get(id);
            catid = catid.substring(catid.lastIndexOf("=") + 1, catid.length());
            Integer.parseInt(catid);
            SetCategoryDetails("category", catid, "");

        }


    }

    private void FillDashBoardSliders()
    {
        showDialog();

        //HashMap<String, String> url_maps = new HashMap<String, String>();

       /* url_maps.clear();
        url_maps.put("Hannibal", "http://www.jepsylifestyle.com/Photos/Banner/banner_02.jpg");
        url_maps.put("Big Bang Theory", "http://www.jepsylifestyle.com/images/thumb/t-3.jpg");
        url_maps.put("House of Cards", "https://s-media-cache-ak0.pinimg.com/originals/d2/3f/a0/d23fa03aefa1f661405548750500c01a.jpg");
        url_maps.put("Game of Thrones", "http://media.indipepper.com/2014/05/1394024215326-BeSummerReady_980x459_980_459_mini-770x459.jpg");
*/
       /* url_maps.clear();
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal", R.drawable.slider_1);
        file_maps.put("Big Bang Theory", R.drawable.bigbang);
        file_maps.put("House of Cards", R.drawable.slider_1);
        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);*/

      /*  try {
            url_maps.put("sda", list_otherSliders.get(3));
            url_maps.put("sda", list_otherSliders.get(4));
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(context);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(AllKeys.RESOURSES + url_maps.get(name))
                    /*.image(url_maps.get(name))*/
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);


            String sd2 = list_SliderImages.get(list_SliderImages.indexOf(url_maps.get(name)));


            int sd = (list_SliderId.indexOf(list_SliderImages.indexOf(url_maps.get(name))));

            if ((list_SliderId.get(list_SliderImages.indexOf(url_maps.get(name))) % 2) != 0) {
                mDemoSlider.addSlider(textSliderView);
            } else {
                mDemoSlider2.addSlider(textSliderView);

            }


        }


      /*  for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
                 *//*   .setOnSliderClickListener(Newthis);*//*

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
            mDemoSlider2.addSlider(textSliderView);
        }*/

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000);

        mDemoSlider2.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider2.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider2.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider2.setDuration(5000);


//        mDemoSlider.addOnPageChangeListener(ge);
        hideDialog();

    }


    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * /**
     * Get All SlideTypes From Server
     */
    private void getAllSliderMasterDetailsFromServer() {


        showDialog();
        String url_getallslodertypes = AllKeys.WEBSITE + "getAllSliderType";
        Log.d(TAG, "URL GetAllSliders : " + url_getallslodertypes);
        StringRequest str_getallsliders = new StringRequest(Request.Method.GET, url_getallslodertypes, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response getAllSliderType : " + response);

                if (response.contains("typeName")) {


                    list_MainBannerTypeId.clear();
                    list_MainBannerTypeName.clear();
                    list_New_DashBoard_Categories.clear();
                    try {
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject c = arr.getJSONObject(i);

                            list_MainBannerTypeId.add(c.getInt(AllKeys.TAG_BANNER_TYPEID));
                            list_MainBannerTypeName.add(c.getString(AllKeys.TAG_BANNER_NAME));

                            if (!c.getString(AllKeys.TAG_BANNER_NAME).equals("Main Slider Product") && !c.getString(AllKeys.TAG_BANNER_NAME).equals("Mobile Slider One") && !c.getString(AllKeys.TAG_BANNER_NAME).equals("Mobile Slider Two") && !c.getString(AllKeys.TAG_BANNER_NAME).equals("Banner")) {
                                DashBoardCategories dc = new DashBoardCategories(c.getString(AllKeys.TAG_BANNER_NAME), c.getString(AllKeys.TAG_BANNER_TYPEID));
                                list_New_DashBoard_Categories.add(dc);
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    DashBoardCategoryDisplayAdapter dbAdapter = new DashBoardCategoryDisplayAdapter(context, list_New_DashBoard_Categories);
                    recycler_dashboard_categories.setAdapter(dbAdapter);


                    int bannerid = list_MainBannerTypeName.indexOf(AllKeys.TAG_MOBILE_SLIDER_1);
                    bannerid = list_MainBannerTypeId.get(bannerid);


                    getSlidersByTypeDetailsFromServer(bannerid);


                    getAllNewProductsFromServer();

                    getAllBestSellerProductsFromServer();
                    getAllNewArrivalProductsFromServer();


                    //Get Between Single Sliders
                    bannerid = list_MainBannerTypeName.indexOf(TAG_MOBILE_SLIDER_2);
                    bannerid = list_MainBannerTypeId.get(bannerid);

                    getSlidersByTypeDetailsFromServer(bannerid);


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();

                } else {
                    getAllSliderMasterDetailsFromServer();

                }
                Log.d(TAG, "Error getallsliderType : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getallsliders);


    }

    /**
     * Get DashBoard Slider From SErver
     *
     * @param bannerid
     */
    private void getSlidersByTypeDetailsFromServer(final int bannerid)
    {
        showDialog();
        final String url_getSldierByType = AllKeys.WEBSITE + "getSlidersByType/" + bannerid + "";
        Log.d(TAG, "url getSlidersByType : " + url_getSldierByType);
        StringRequest str_getSlidersByType = new StringRequest(Request.Method.GET, url_getSldierByType, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Resposne getSlidersByType : " + response);


                if (response.contains("type")) {


                    if (list_MainBannerTypeName.get(list_MainBannerTypeId.indexOf(bannerid)).equals(AllKeys.TAG_MOBILE_SLIDER_1)) {
                        try {
                            response = dbhandler.convertToJsonFormat(response);
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("data");

                            list_DashboardBannerCategotyURL.clear();
                            list_DashboardBannerId.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject c = arr.getJSONObject(i);


                                String url_image_url = c.getString(AllKeys.TAG_BANNER_IMAGE);

                                list_DashboardBannerCategotyURL.add(c.getString(AllKeys.TAG_BANNER_CATEGORYLINK));
                                list_DashboardBannerId.add(c.getInt(AllKeys.TAG_BANNER_TYPEID));


                                //  url_image_url = "http://www.yelona.com" + url_image_url;
                                // url_maps.put(c.getString(AllKeys.TAG_BANNER_TYPEID), url_image_url);
                                if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("1")) {
                                    //Glide.with(context).load(AllKeys.RESOURSES+c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().placeholder(R.drawable.icon_userlogo).error(R.drawable.icon_userlogo).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgProfilePic);
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner1);


                                } else if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("2")) {
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner2);
                                } else if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("3")) {
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner3);
                                } else if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("4")) {
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner4);
                                } else if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("5")) {
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner5);
                                } else if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("6")) {
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner6);
                                } else if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("7")) {
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner7);
                                } else if (c.getString(AllKeys.TAG_BANNER_SQQUENCE).equals("8")) {
                                    Glide.with(context).load(AllKeys.RESOURSES + c.getString(AllKeys.TAG_BANNER_IMAGE)).crossFade().error(R.drawable.yelona_blur_150).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_final_banner8);
                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideDialog();
                        }


                    } else if (list_MainBannerTypeName.get(list_MainBannerTypeId.indexOf(bannerid)).equals(AllKeys.TAG_MOBILE_SLIDER_2)) {
                        try {
                            response = dbhandler.convertToJsonFormat(response);
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("data");
                            url_maps.clear();
                            list_SliderId.clear();
                            list_SliderImages.clear();

                            list_SliderCategoryURL.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject c = arr.getJSONObject(i);


                                String url_image_url = c.getString(AllKeys.TAG_BANNER_IMAGE);
                                //  url_image_url = "http://www.yelona.com" + url_image_url;
                                //url_maps.put(c.getString(AllKeys.TAG_BANNER_IMAGE), url_image_url);
                                url_maps.put(c.getString(AllKeys.TAG_BANNER_TYPEID), url_image_url);

                                list_SliderCategoryURL.add(c.getString(AllKeys.TAG_BANNER_CATEGORYLINK));

                                list_SliderId.add(c.getInt(AllKeys.TAG_BANNER_TYPEID));
                                list_SliderImages.add(url_image_url);


                            }

                            FillDashBoardSliders();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideDialog();
                        }


                    }


                }


                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Error getSlidersByType : " + error.getMessage());
                if (error instanceof ServerError) {
                    hideDialog();
                } else {
                    getSlidersByTypeDetailsFromServer(bannerid);

                }

            }

        });
        str_getSlidersByType.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(str_getSlidersByType);

    }
    /**
     * Complete Get All Dashboard Sliders details from server
     */


    /**
     * Get Other Slider From SErver
     *
     * @param bannerid
     */
   /* private void getOtherSlidersDetailsFromServer(final int bannerid)
    {

        showDialog();
        final String url_getSldierByType = AllKeys.WEBSITE + "getSlidersByType/" + bannerid + "";
        Log.d(TAG, "url get Other Sliders ByType : " + url_getSldierByType);
        StringRequest str_getSlidersByType = new StringRequest(Request.Method.GET, url_getSldierByType, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Resposne get Other SlidersByType : " + response);


                if (response.contains("type")) {
                    try {
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        list_otherSliders.clear();
                        list_otherSlidersCategoryId.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);


                            String url_image_url = c.getString(AllKeys.TAG_BANNER_IMAGE);
                            url_image_url = "http://www.yelona.com" + url_image_url;
                            list_otherSliders.add(url_image_url);

                            list_otherSlidersCategoryId.add(c.getString(AllKeys.TAG_BANNER_CATEGORYLINK));


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (list_otherSliders.size() > 0) {

                        try {
                            //banner1.setVisibility(View.VISIBLE);
                            Glide.with(context).load(list_otherSliders.get(0)).into(banner1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // banner1.setVisibility(View.GONE);
                        }


                        try {
                            Glide.with(context).load(list_otherSliders.get(1)).into(banner2);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        try {
                            Glide.with(context).load(list_otherSliders.get(2)).into(banner3);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        try {
                            Glide.with(context).load(list_otherSliders.get(3)).into(banner4);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                }


                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null) {
                    getOtherSlidersDetailsFromServer(bannerid);
                } else {

                    hideDialog();
                }

                Log.d(TAG, "Error get Other SlidersByType : " + error.getMessage());

            }

        });
        MyApplication.getInstance().addToRequestQueue(str_getSlidersByType);

    }*/
    /**
     * Complete Get Other  Sliders details from server
     */


    /**
     * Complete get all slidetype Detials from server
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
        if (doubleBackToExitPressedOnce) {
            finish();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        try {
            getMenuInflater().inflate(R.menu.item_display, menu);
            cart = (MenuItem) menu.findItem(R.id.menu_addtocart);

            MenuItem menu_serach = (MenuItem) menu.findItem(R.id.menu_search);
            menu_serach.setVisible(false);

            LayerDrawable icon = (LayerDrawable) cart.getIcon();

            LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();


//int orderid=db.getMaxOrderId();


            //  String query = "select * from " + db.TABLE_ORDER;


            //  Cursor cc = sd.rawQuery(query, null);
            //  int numorder = cc.getCount();
            //sessionmanager.StoreOrderDetails(""+numorder);
            // System.out.println("" + numorder);

            setAddToCartBadget();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.menu_whishlist) {


            Intent intent = new Intent(context, WishListActivity.class);

            //intent = new Intent(context, OrderSuccessfullActivity.class);
            sessionmanager.setActivityName(TAG);
            //intent.putExtra("ActivityName", TAG);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.menu_addtocart) {

            getAllCartItemDetailsFromServer(true);

            /*Intent intent = new Intent(context, CheckoutActivity.class);
            // intent.putExtra("ActivityName", TAG);
            sessionmanager.setActivityName(TAG);
            sessionmanager.setCheckoutType("multiple", "0");
            startActivity(intent);
            finish();*/

        }

        return super.onOptionsItemSelected(item);
    }

    private void setAddToCartBadget() {

        //    String query = "select * from " + db.TABLE_ORDER;


        LayerDrawable icon = null;
        try {
            icon = (LayerDrawable) cart.getIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  Cursor cc = sd.rawQuery(query, null);
        //int numorder = cc.getCount();
        //sessionmanager.StoreOrderDetails(""+numorder);
        // System.out.println("" + numorder);

        try {
            Utils.setBadgeCount(NewDashBoardActivity.this, icon, list_cartItemsId.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // startCountAnimation();

       /* LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
        iv.setBackgroundResource(R.drawable.ic_wishlist_white);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        icon.setActionView(iv);*/


    }


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
            /*if(swipeRefreshLayout.isRefreshing())
            {
                swipeRefreshLayout.setRefreshing(false);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Expandable ListView
     */

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
            listMainCategories.clear();

            while (cc.moveToNext()) {
                int getCategoryId = cc.getInt(cc.getColumnIndex(dbhandler.CATEGORY_ID));

                String upperString = cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME));
                upperString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1);

                firstLevelCategoryName.add(upperString);
                firstLEvelCategoryId.add(getCategoryId);

                //MainCategory mc= new MainCategory(String.valueOf(getCategoryId),upperString,"",cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)));
                if (upperString.toLowerCase().equals("men")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_men));
                    listMainCategories.add(mc);
                } else if (upperString.toLowerCase().equals("women")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_women));
                    listMainCategories.add(mc);
                } else if (upperString.toLowerCase().equals("jewellery")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_jeweller));
                    listMainCategories.add(mc);
                } else if (upperString.toLowerCase().equals("electronics")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_electronic));
                    listMainCategories.add(mc);
                } else if (upperString.toLowerCase().equals("baby&kids")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_kids));
                    listMainCategories.add(mc);
                } else if (upperString.toLowerCase().equals("home & kitchen")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_kitchen));
                    listMainCategories.add(mc);
                } else if (upperString.toLowerCase().equals("auto&sports")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_sports));
                    listMainCategories.add(mc);
                } else if (upperString.toLowerCase().equals("books&media")) {

                    MainCategory mc = new MainCategory(String.valueOf(getCategoryId), upperString, "", cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_PARENTID)), context.getResources().getDrawable(R.drawable.category_book_media));
                    listMainCategories.add(mc);
                }


                //Check SubCategory Exist or Not..If exist then add as GroupItem()
                String query_supercategory = "select * from Categorymaster where parentid=" + getCategoryId + " order by " + dbhandler.CATEGORY_SEQUENCE_NO + " asc";
                Log.d(TAG, "Query : " + query_categories);
                Cursor cursor_supercategory = sd.rawQuery(query_supercategory, null);
                if (cursor_supercategory.getCount() <= 0) {

                    //rootMenu.add(new Item(cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME))));
                    rootMenu.add(new Item(upperString));

                } else {
                    //rootMenu.add(new GroupItem(cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME))));
                    rootMenu.add(new GroupItem(upperString));
                }

            }

            MainCategoryDisplayAdapter main_category_adapter = new MainCategoryDisplayAdapter(context, listMainCategories);
            recycler_main_categories.setAdapter(main_category_adapter);


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
                convertView = LayoutInflater.from(NewDashBoardActivity.this).inflate(R.layout.data_item, null);
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

                fonts = Typeface.createFromAsset(getAssets(), "fonts/MavenPro-Regular.ttf");
                viewHolder.nameView.setTypeface(Typeface.DEFAULT_BOLD);
                viewHolder.nameView.setTypeface(fonts);
            } else if (itemInfo.getLevel() == 1) {
                viewHolder.nameView.setTypeface(fonts);
            } else {
                viewHolder.nameView.setTypeface(fonts);

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

                String query = "select * from Categorymaster where parentid=" + firstLEvelCategoryId.get(firstLevelCategoryName.indexOf(menuItem)) + " Order By " + dbhandler.CATEGORY_SEQUENCE_NO + " asc";
                Cursor cursor_category = sd.rawQuery(query, null);
                if (cursor_category.getCount() > 0) {
                    List<BaseItem> list = new ArrayList<>();
                    // secondLEvelCategoryId.clear();
                    //secondLevelCategoryName.clear();

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
                    thirdLEvelCategoryId.clear();
                    thirdtLevelCategoryName.clear();

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


   /* public String getTabDetails(int menuItemId, boolean isReselection)
    {
        String message = "Content for ";

        switch (menuItemId) {
          *//*  case R.id.tab_recents:
                message += "recents";
                break;*//*
            case R.id.tab_home:
                message += "home";
                break;
            case R.id.tab_wallet:
                message += "vallet";
                break;
            case R.id.tab_profile:
                message += "profile";
                break;
            case R.id.tab_offer:
                message += "offer";
                break;
            case R.id.tab_logout:
                sessionmanager.logoutUser();
                finish();
                *//*Intent ii = new Intent(context, LoginActivity.class);
                startActivity(ii);
                finish();*//*
                break;
            *//*case R.id.tab_food:
                message += "food";
                break;*//*
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }*/

}

