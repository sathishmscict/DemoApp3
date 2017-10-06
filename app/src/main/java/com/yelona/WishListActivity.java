package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yelona.adapter.WishListRecyclerViewAdapter;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.Utils;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class WishListActivity extends AppCompatActivity {

    private RecyclerView recyclerview_wishlist;
    private Context context = this;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private String TAG = WishListActivity.class.getSimpleName();
    private ArrayList<ProductData> list_Products = new ArrayList<ProductData>();
    private WishListRecyclerViewAdapter adapter;
    private String ACTIVITYNAME;
    private Button btnDeleteAll;
    private LinearLayout llmain;
    private LinearLayout llEmptyCart;
    private Button btnContinue;
    private MenuItem cart;

    public ArrayList<String> list_cartItemsId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //Get Cart Item details from sesion
        try {
            list_cartItemsId  = new ArrayList<>(Arrays.asList(userDetails.get(SessionManager.KEY_CARTITEMS_ID).split(",")));

        } catch (Exception e) {
            e.printStackTrace();
        }


        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();

        try {
            ACTIVITYNAME = getIntent().getStringExtra("ActivityName");
        } catch (Exception e) {
            ACTIVITYNAME = "";
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerview_wishlist = (RecyclerView) findViewById(R.id.recyclerview_wishlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerview_wishlist.setLayoutManager(layoutManager);

        btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll);
        llmain = (LinearLayout) findViewById(R.id.llmain);
        llEmptyCart = (LinearLayout) findViewById(R.id.llemptyCart);

        btnContinue = (Button) findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NewDashBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendWishListDetailsToServer(db, sd, "0", "0", "removeall", 0);

            }
        });
        getAllProductDetailsFromServer();

    }
    //onCreate Completed

    private void getAllProductDetailsFromServer()
    {


        showDialog();


        String url_getProducts = AllKeys.WEBSITE + "getWishlistByUserid/"+ userDetails.get(SessionManager.KEY_USERID) +"";


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
                            //  public ProductData(String shoppingcost, String productid, String productname, String description, String category_id, String image_url, String seller_id, String price, String offer_value, String offer_tag, String color, String weight, String height, String length, String width, String size, String mrp, String ship_date, String brand, String inventory, String committed_qty) {
                            ProductData prod = new ProductData(c.getString(AllKeys.TAG_WISHLIST_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_MRP), c.getString(AllKeys.TAG_PRICE), userDetails.get(SessionManager.KEY_USERID),"1",c.getString(AllKeys.TAG_INVENTORY));
                            //ProductData prod = new ProductData(c.getString(AllKeys.TAG_SHOPPINFCOST), c.getString(AllKeys.TAG_PRODUCTID), c.getString(AllKeys.TAG_PRODUCTNAME), c.getString(AllKeys.TAG_DESCRIPTION), c.getString(AllKeys.TAG_PRODUCT_CATEGORYID), AllKeys.RESOURSES + c.getString(AllKeys.TAG_IMAGE_URL), c.getString(AllKeys.TAG_SELLERID), c.getString(AllKeys.TAG_PRICE), c.getString(AllKeys.TAG_OFFER_VALUE), c.getString(AllKeys.TAG_OFFER_TAG), c.getString(AllKeys.TAG_COLOR), c.getString(AllKeys.TAG_WEIGHT), c.getString(AllKeys.TAG_HEIGHT), c.getString(AllKeys.TAG_LENGTH), c.getString(AllKeys.TAG_WIDTH), c.getString(AllKeys.TAG_SIZE), c.getString(AllKeys.TAG_MRP), c.getString(AllKeys.TAG_SHIP_DATE), c.getString(AllKeys.TAG_BRAND), c.getString(AllKeys.TAG_INVENTORY), c.getString(AllKeys.TAG_COMITTED_BY));
                            list_Products.add(prod);


                        }
                        adapter = new WishListRecyclerViewAdapter(context, list_Products, "grid");
                        recyclerview_wishlist.setAdapter(adapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                    llmain.setVisibility(View.VISIBLE);
                    llEmptyCart.setVisibility(View.GONE);
                } else {
                    llmain.setVisibility(View.GONE);
                    llEmptyCart.setVisibility(View.VISIBLE);
                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

              /*  if (error == null)

                {
                    getAllProductDetailsFromServer();
                } else {

                    hideDialog();
                }*/
                if(error instanceof ServerError)
                {
                   hideDialog();
                }
                else
                {

                getAllProductDetailsFromServer();
                }

                Log.d(TAG, "Error in get WishList : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_get_products);

    }


    private void sendWishListDetailsToServer(final dbhandler db, final SQLiteDatabase sd, final String ProductId, final String ProductPrice, final String type, final int position) {


        String url_addtowishlist = AllKeys.WEBSITE + "manageWishlist/" + type + "/"+ userDetails.get(SessionManager.KEY_USERID) +"/" + ProductId + "/" + db.convertEncodedString(db.getDateTime()) + "/" + db.convertEncodedString(db.getDateTime()) + "/" + ProductPrice + "";

        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
        StringRequest str_addToWishList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response ManageWishList : " + response);

                if (response.equals("0")) {
                    Toast.makeText(context, "Sorry,try again...", Toast.LENGTH_SHORT).show();
                } else {
                    if (type.equals("add")) {
                        Toast.makeText(context, "Added to your wishlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Removed from your wishlist", Toast.LENGTH_SHORT).show();
                    }


                    llEmptyCart.setVisibility(View.VISIBLE);
                    llmain.setVisibility(View.GONE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null) {
                    sendWishListDetailsToServer(db, sd, ProductId, ProductPrice, type, position);

                }
                Log.d(TAG, "Error in ManageWishList : " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_addToWishList);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        try {
            getMenuInflater().inflate(R.menu.item_display, menu);
            cart = (MenuItem) menu.findItem(R.id.menu_addtocart);

            MenuItem cart_wishlist = (MenuItem) menu.findItem(R.id.menu_whishlist).setVisible(false);

            MenuItem search = (MenuItem) menu.findItem(R.id.menu_search).setVisible(false);

            LayerDrawable icon = (LayerDrawable) cart.getIcon();

            LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();

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

            Intent i = null;
            try {
                try {
                    i = new Intent(context, Class.forName(context.getPackageName() + "." + getIntent().getStringExtra("ActivityName")));
                    i.putExtra("ActivityName",getIntent().getStringExtra("ActivityName"));
                } catch (ClassNotFoundException e) {
                    i = new Intent(context, NewDashBoardActivity.class);
                    e.printStackTrace();
                }
                //sessionmanager.setActivityName(TAG);
                startActivity(i);
                finish();
            } catch (Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error in Converting Class : " + e.getMessage());
                e.printStackTrace();
            }

        }
        else if(item.getItemId() == R.id.menu_addtocart)
        {
            Intent ii =new Intent(context , CheckoutActivity.class);
            ii.putExtra("ActivityName", TAG);
            startActivity(ii);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }


    private void setAddToCartBadget()
    {

        LayerDrawable icon = (LayerDrawable) cart.getIcon();

        Utils.setBadgeCount(WishListActivity.this, icon, list_cartItemsId.size());


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = null;
        try {
            try {
                i = new Intent(context, Class.forName(context.getPackageName() + "." + getIntent().getStringExtra("ActivityName")));
                i.putExtra("ActivityName",getIntent().getStringExtra("ActivityName"));
            } catch (ClassNotFoundException e) {
                i = new Intent(context, NewDashBoardActivity.class);
                e.printStackTrace();
            }
            //sessionmanager.setActivityName(TAG);
            startActivity(i);
            finish();
        } catch (Exception e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error in Converting Class : " + e.getMessage());
            e.printStackTrace();
        }


    }


    public void showDialog() {

        if (!pDialog.isShowing()) {

            pDialog.show();
            pDialog.setCancelable(true);
        }


    }

    public void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog.setCancelable(true);

        }
    }


}
