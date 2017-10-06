package com.yelona.fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yelona.R;
import com.yelona.session.SessionManager;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.CipherOutputStream;


public class DescriptionFragment extends Fragment {


    private SessionManager sessionManager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();

    private Context context = getActivity();

    private RecyclerView recyclerview_product_info;
    private ArrayList<ProductInformtaion> listProductInfo = new ArrayList<ProductInformtaion>();
    private ProductInfoRecyclerViewAdapter adapter;


    public DescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);


        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getSessionDetails();


        recyclerview_product_info = (RecyclerView) rootView.findViewById(R.id.recyclerview_product_info);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerview_product_info.setLayoutManager(layoutManager);
        recyclerview_product_info.setItemAnimator(new DefaultItemAnimator());


        String descr = userDetails.get(SessionManager.KEY_PRODUCT_DESCR);
        descr = "\t" + descr;
        descr = descr.replace("_x000D_", "");
        descr = descr.replace("\t", "");

        ArrayList<String> asData = new ArrayList<String>(Arrays.asList(descr.split(";")));
        String finalStringData = "";
        listProductInfo.clear();
        for (int i = 0; i < asData.size(); i++) {

            ProductInformtaion pi;
            try {
                ArrayList<String> asSplitredData = new ArrayList<String>(Arrays.asList(asData.get(i).split(":")));


                try {
                    pi = new ProductInformtaion(asSplitredData.get(0), asSplitredData.get(1));
                } catch (Exception e) {
                    pi = new ProductInformtaion(asSplitredData.get(0), "");
                    e.printStackTrace();
                }
                listProductInfo.add(pi);
            } catch (Exception e) {
                //listProductInfo.clear();
                 //pi = new ProductInformtaion("\n" + descr, "");
               // listProductInfo.add(pi);
                e.printStackTrace();
            }

            //finalStringData = finalStringData+String.format("%.20", asSplitredData.get(0))+asSplitredData.get(1);

        }
        adapter = new ProductInfoRecyclerViewAdapter();
        recyclerview_product_info.setAdapter(adapter);


        //descr = descr.replace(";", "\n\n");
        //txtDescrInfo.setText(finalStringData);

        //txtDescrInfo.setText("\tBrand\t\t\t\t\t\t:\t\t" + brand + "\n\n\tSKU   \t\t\t\t\t\t:\t" + sku + "\n\n\t" + descr);


        return rootView;


    }

    /**
     * POJO Class For display information
     */

    public class ProductInformtaion {
        String title, Descr;

        public ProductInformtaion(String title, String descr) {
            this.title = title;
            Descr = descr;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescr() {
            return Descr;
        }

        public void setDescr(String descr) {
            Descr = descr;
        }


    }
    //Complete POJO CLass

    public class ProductInfoRecyclerViewAdapter extends RecyclerView.Adapter<ProductInfoRecyclerViewAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtDescrTitle, txtDescrInfo;

            public MyViewHolder(View itemView) {
                super(itemView);


                txtDescrTitle = (TextView) itemView.findViewById(R.id.txtDescrTitle);
                txtDescrInfo = (TextView) itemView.findViewById(R.id.txtDescrInfo);

            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getActivity()).inflate(R.layout.row_single_product_information, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {


                if (listProductInfo.get(position).getDescr().equals("")) {

                    holder.txtDescrInfo.setVisibility(View.GONE);

                } else {
                    holder.txtDescrInfo.setVisibility(View.VISIBLE);
                }


            holder.txtDescrTitle.setText(listProductInfo.get(position).getTitle());
            holder.txtDescrInfo.setText(listProductInfo.get(position).getDescr());


        }

        @Override
        public int getItemCount() {
            return listProductInfo.size();
        }
    }

}
