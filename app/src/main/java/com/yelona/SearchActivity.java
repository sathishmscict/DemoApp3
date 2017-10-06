package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yelona.app.MyApplication;

import com.yelona.database.dbhandler;

import com.yelona.databinding.ContentSearchBinding;
import com.yelona.databinding.RowItemBinding;
import com.yelona.helper.AllKeys;
import com.yelona.pojo.SearchData;



import com.yelona.session.SessionManager;

import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;


public class SearchActivity extends AppCompatActivity {


    //Searching Related Keys
    public static final String TAG_SEARCH_ARRAY = "data";
    public static final String TAG_SEARCH_FLAG = "flag";
    public static final String TAG_SEARCH_MESSAGE = "message";
    public static final String TAG_SEARCH_ID = "id";
    public static final String TAG_SEARCH_PRODUCTID = "product_id";
    public static final String TAG_SEARCH_NAME = "name";
    public static final String TAG_SEARCH_CATEGORYID = "category_id";
    public static final String TAG_SEARCH_CATEGORYNAME = "category_name";
    public static final String TAG_SEARCH_CATEGORY_TYPE = "category_type";
    public static final String TAG_SEARCH_CATEGORY_TYPE_NAME = "category_type_name";
    public static final String TAG_SEARCH_IS_ACTIVE_SELLER = "is_active_seller";
    public static final String TAG_SEARCH_IS_ADMIN_ACTIVE = "is_active_admin";
    public static final String TAG_SEARCH_SKU = "sku";
    public static final String TAG_SEARCH_PRICE = "price";
    public static final String TAG_SEARCH_IS_PARENT = "is_parent";


    ContentSearchBinding activityMainBinding;
    ListAdapter adapter;

    List<String> arrayList = new ArrayList<>();
    private String TAG = SearchActivity.class.getSimpleName();
    private ArrayList<SearchData> list_SearchData = new ArrayList<SearchData>();
    private Context context = this;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.content_search);

        activityMainBinding.search.setActivated(true);
        activityMainBinding.search.setQueryHint(getResources().getString(R.string.search_hint));
        activityMainBinding.search.onActionViewExpanded();
        activityMainBinding.search.setIconified(false);
        activityMainBinding.search.clearFocus();


        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        activityMainBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                try {
                    getProductDataFromServer(newText);

                    adapter.getFilter().filter(newText);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });


        pDialog = new SpotsDialog(context);
        pDialog.setCancelable(false);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();


        db = new dbhandler(context);


        ivBack = (ImageView)findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intet = new Intent(context , NewDashBoardActivity.class);
                startActivity(intet);
                finish();
            }
        });



        getProductDataFromServer("");


    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {
            getMenuInflater().inflate(R.menu.menu_search, menu);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }*/


    public static String convertToJsonFormat(String json_data) {

        String response = "{\"Maindata\":" + json_data + "]}";
        return response;

    }


    private void getProductDataFromServer(final String str) {
        String url_getData = AllKeys.RESOURSES+"api/search";

        StringRequest request = new StringRequest(Request.Method.POST, url_getData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.contains("flag")) {

                    list_SearchData.clear();
                    arrayList.clear();
                    try {
                       /* response = convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");*/

                        response = convertToJsonFormat(response);//{"Maindata": {"flag":true,"message":"Product search successfully.","data":[{"id":40,"product_id":166,"name":"Women's Fabric Casual Shoes - Blue & Red","category_id":16,"category_name":"Footwear Womens","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Women's Casual Shoes","price":449,"is_parent":1},{"id":24,"product_id":148,"name":"Lenovo C40-30 aio i3 Desktop","category_id":66,"category_name":"Laptops","category_type":1,"category_type_name":"electronics","is_active_seller":1,"is_active_admin":1,"sku":"Lenovo C40-30 aio i3 Desktop1","price":39999,"is_parent":1},{"id":22,"product_id":144,"name":"savaliya sarees Red colore saree","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"savaliya sarees Red colore saree 09","price":749,"is_parent":1},{"id":41,"product_id":167,"name":"Baby Bucket Sandal (L.Pink)","category_id":110,"category_name":"Kids Footwear","category_type":6,"category_type_name":"Baby & Kids","is_active_seller":1,"is_active_admin":1,"sku":"Baby Bucket Sandal","price":299,"is_parent":1},{"id":19,"product_id":139,"name":"New Designer Bolliwood Saree","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Bolliwood_Saree_CYNE","price":899,"is_parent":1},{"id":25,"product_id":149,"name":"Dell Inspiron 3148","category_id":66,"category_name":"Laptops","category_type":1,"category_type_name":"electronics","is_active_seller":1,"is_active_admin":1,"sku":"Dell Inspiron 3148 1","price":42599,"is_parent":1},{"id":14,"product_id":126,"name":"bestdeal sonam dress materials","category_id":190,"category_name":"Dress Materials","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"kano7","price":1599,"is_parent":1},{"id":44,"product_id":170,"name":"Baby Sandlas","category_id":110,"category_name":"Kids Footwear","category_type":6,"category_type_name":"Baby & Kids","is_active_seller":1,"is_active_admin":1,"sku":"Baby Sandlas","price":449,"is_parent":1},{"id":79,"product_id":206,"name":"Lancer Men's Grey and Green Shoes","category_id":15,"category_name":"Footwear Man","category_type":2,"category_type_name":"Men","is_active_seller":1,"is_active_admin":1,"sku":"Lancer Men's Shoes","price":799,"is_parent":1},{"id":48,"product_id":175,"name":"Women's Faux Leather Wedges","category_id":16,"category_name":"Footwear Womens","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Faux Leather Wedges","price":599,"is_parent":1},{"id":126,"product_id":264,"name":"Terabyte 1.5M HDMI Male To Male Cable USB 3.0 Original","category_id":163,"category_name":"Cables & Chargers","category_type":1,"category_type_name":"electronics","is_active_seller":1,"is_active_admin":1,"sku":"TB15HDMI","price":199,"is_parent":1},{"id":105,"product_id":234,"name":"Matrixx X5 Wireless Bluetooth Speaker - Black","category_id":180,"category_name":"Speakers & Headphones","category_type":1,"category_type_name":"electronics","is_active_seller":1,"is_active_admin":1,"sku":"UW0018","price":1449,"is_parent":1},{"id":110,"product_id":244,"name":"Exclusive Designer Green and Yellow Lehenga Choli","category_id":194,"category_name":"Lehengas","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"KEDAR_PERROT","price":989,"is_parent":1},{"id":108,"product_id":242,"name":"Designer Black Salwar Suit by Moradiya Export","category_id":190,"category_name":"Dress Materials","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"blackdog","price":1199,"is_parent":1},{"id":116,"product_id":250,"name":"Designer Pista Frock by GRAVITY FABRICS","category_id":191,"category_name":"Ethnic Suits","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"FROCK_PISTA","price":999,"is_parent":1},{"id":120,"product_id":254,"name":"ORANGE FLOWER BOLLYWOOD SAREE","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"ORANGE_FLOWER","price":999,"is_parent":1},{"id":123,"product_id":257,"name":"RED FLOWER BOLLYWOOD SAREE","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"RED_FLOWER","price":999,"is_parent":1},{"id":144,"product_id":282,"name":"TheEmpire Casual, Formal, Festive Printed Women's Kurti","category_id":193,"category_name":"Kurta\/Kurtis","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"LibertyKurti","price":999,"is_parent":1},{"id":143,"product_id":281,"name":"TheEmpire Casual Printed Women's Kurti","category_id":193,"category_name":"Kurta\/Kurtis","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"REDCHANDERYJEQKURTI","price":999,"is_parent":1},{"id":145,"product_id":283,"name":"TheEmpire Casual Printed Women's Kurti","category_id":193,"category_name":"Kurta\/Kurtis","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"YellowWoodenKurti","price":1999,"is_parent":1},{"id":172,"product_id":314,"name":"print sarees13","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"maharani weightless cream red","price":499,"is_parent":1},{"id":129,"product_id":267,"name":"bestdeal 102","category_id":193,"category_name":"Kurta\/Kurtis","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Crepe102","price":499,"is_parent":1},{"id":130,"product_id":268,"name":"bestdeal 103","category_id":193,"category_name":"Kurta\/Kurtis","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Crepe103","price":499,"is_parent":1},{"id":132,"product_id":270,"name":"bestdeal 105","category_id":193,"category_name":"Kurta\/Kurtis","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Crepe105","price":499,"is_parent":1},{"id":190,"product_id":356,"name":"TheEmpire Casual Wear Blue Coloured Rayon Stitched Kurti","category_id":193,"category_name":"Kurta\/Kurtis","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"BlueRayonChanderiKurti","price":1499,"is_parent":1},{"id":183,"product_id":341,"name":"TheEmpire Festive Wear Beige Coloured Georgette Gown","category_id":198,"category_name":"Gowans","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"FourSequinGown","price":3999,"is_parent":1},{"id":227,"product_id":445,"name":"Bollywood Designer Printed Sarees by Moradiya Export","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Mordiya_29","price":1149,"is_parent":1},{"id":204,"product_id":393,"name":"Bollywood Designer Printed Sarees by Moradiya Export","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Mordiya_17","price":949,"is_parent":1},{"id":206,"product_id":395,"name":"Bollywood Designer Printed Sarees by Moradiya Export","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Mordiya_15","price":949,"is_parent":1},{"id":222,"product_id":440,"name":"Bollywood Designer Printed Sarees by Moradiya Export","category_id":192,"category_name":"Sarees","category_type":3,"category_type_name":"Woman","is_active_seller":1,"is_active_admin":1,"sku":"Mordiya_24","price":949,"is_parent":1}]}]}
                        response = response.replace("Maindata\": {", "Maindata\": [{");
                        JSONObject obj = new JSONObject(response);
                        JSONArray arrMain = obj.getJSONArray("Maindata");


                        for (int j = 0; j < arrMain.length(); j++) {

                            JSONObject c = arrMain.getJSONObject(j);

                            JSONArray arr = c.getJSONArray("data");

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject cc = arr.getJSONObject(i);
                                arrayList.add(cc.getString(TAG_SEARCH_NAME));

                                //SearchData(String search_flag, String search_id, String search_product_id, String search_name, String search_category_id, String search_category_name, String search_category_type, String search_category_type_name, String search_is_active_seller, String search_is_active_admin, String search_sku, String search_price) {

                                SearchData sc = new SearchData("true", String.valueOf(cc.getInt(TAG_SEARCH_ID)), String.valueOf(cc.getInt(TAG_SEARCH_PRODUCTID)), cc.getString(TAG_SEARCH_NAME), String.valueOf(cc.getInt(TAG_SEARCH_CATEGORYID)), cc.getString(TAG_SEARCH_CATEGORYNAME), cc.getString(TAG_SEARCH_CATEGORY_TYPE), cc.getString(TAG_SEARCH_CATEGORY_TYPE_NAME), cc.getString(TAG_SEARCH_IS_ACTIVE_SELLER), cc.getString(TAG_SEARCH_IS_ADMIN_ACTIVE), cc.getString(TAG_SEARCH_SKU), String.valueOf(cc.getInt(TAG_SEARCH_PRICE)));

                                list_SearchData.add(sc);


                            }


                        }


                        adapter = new ListAdapter(context, arrayList);
                        activityMainBinding.listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                Log.d(TAG, "Search Response : " + response);
                //Toast.makeText(SearchActivity.this, "Search : " + response, Toast.LENGTH_SHORT).show();


                //  adapter = new ListAdapter(context,arrayList);
                // activityMainBinding.listView.setAdapter(adapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

             //   Toast.makeText(SearchActivity.this, "Errro :" + error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Errro :" + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("q", str);
                params.put("limit", "30");

                Log.d(TAG, "Params : " + params.toString());

                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(request);


    }


    public class ListAdapter extends BaseAdapter implements Filterable {

        List<String> mData;
        List<String> mStringFilterList;
        ListAdapter.ValueFilter valueFilter;
        private LayoutInflater inflater;
        private Context context;


        public ListAdapter(Context context, List<String> cancel_type) {
            mData = cancel_type;
            mStringFilterList = cancel_type;
            this.context = context;
        }


        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            if (inflater == null) {
                inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            RowItemBinding rowItemBinding = DataBindingUtil.inflate(inflater, R.layout.row_item, parent, false);
            rowItemBinding.stringName.setText(mData.get(position));

            rowItemBinding.stringName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  Toast.makeText(context, "Selecte : " + list_SearchData.get(position).getSearch_product_id(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Data : " + mData.get(position) + list_SearchData.get(position).getSearch_product_id());

              /*      Intent intent = new Intent(context , SingleItemActivity.class);
                    startActivity(intent);
                    finish();*/

                    try {
                        //Toast.makeText(_context, "Name : " + pd.getProductname() + " Id : " + pd.getProductid(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Clicked On Name : " + list_SearchData.get(position).getSearch_name() + " Id : " + list_SearchData.get(position).getSearch_product_id());
                        Intent intent = new Intent(context,
                                SingleItemActivity.class);


                        sessionmanager.setProductDetails(list_SearchData.get(position).getSearch_product_id(), "-1", "", "", "0");


                        //sessionmanager.setCategoryTypeAndIdDetails(categoryType, categoryId, categoryName);

                        sessionmanager.setCategoryTypeAndIdDetails("category",list_SearchData.get(position).getSearch_category_id(),list_SearchData.get(position).getSearch_category_name());



                        //intent.putExtra("ProductId", pd.getProductid());
                        intent.putExtra("ActivityName", "ItemDisplayActivity");
                        sessionmanager.setActivityName("");
                        context.startActivity(intent);
                        //finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });


            return rowItemBinding.getRoot();
        }

        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    List<String> filterList = new ArrayList<>();
                    for (int i = 0; i < mStringFilterList.size(); i++) {
                        if ((mStringFilterList.get(i).toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(mStringFilterList.get(i));
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = mStringFilterList.size();
                    results.values = mStringFilterList;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                mData = (List<String>) results.values;
                notifyDataSetChanged();
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Intent i = new Intent(context, NewDashBoardActivity.class);
            startActivity(i);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(context, NewDashBoardActivity.class);
        startActivity(i);
        finish();
    }
}
