package com.yelona.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yelona.ItemDisplayActivity;
import com.yelona.R;
import com.yelona.fragments.DescriptionFragment;
import com.yelona.pojo.DashBoardCategories;
import com.yelona.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PC2 on 17-Mar-17.
 */

public class DashBoardCategoryDisplayAdapter extends RecyclerView.Adapter<DashBoardCategoryDisplayAdapter.MyViewHolder> {


    private final Context _context;
    private final ArrayList<DashBoardCategories> listDashBoradCategoryData;
    private final SessionManager sesssionManager;
    private final HashMap<String, String> userDetails;

    public DashBoardCategoryDisplayAdapter(Context context, ArrayList<DashBoardCategories> listCategoriies) {
        this._context = context;
        this.listDashBoradCategoryData = listCategoriies;


        sesssionManager = new SessionManager(_context);
        userDetails = sesssionManager.getSessionDetails();


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtCategory;
        private final View divider;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtCategory = (TextView) itemView.findViewById(R.id.txtCategory);
            divider = (View) itemView.findViewById(R.id.divider);


        }
    }


    @Override
    public DashBoardCategoryDisplayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(_context).inflate(R.layout.row_single_dashboard_category, parent, false);


        MyViewHolder myviewHolder = new MyViewHolder(view);
        return myviewHolder;
    }

    @Override
    public void onBindViewHolder(DashBoardCategoryDisplayAdapter.MyViewHolder holder, final int position) {

        if(!listDashBoradCategoryData.get(position).getCategoryName().equals("Main Slider Product") && !listDashBoradCategoryData.get(position).getCategoryName().equals("Mobile Slider One") && !listDashBoradCategoryData.get(position).getCategoryName().equals("Mobile Slider Two") && !listDashBoradCategoryData.get(position).getCategoryName().equals("Banner"))
        {
            holder.txtCategory.setText(listDashBoradCategoryData.get(position).getCategoryName());
        }

        if (position == listDashBoradCategoryData.size()-1)
        {
            holder.divider.setVisibility(View.GONE);
        }else
        {
            holder.divider.setVisibility(View.VISIBLE);
        }



        holder.txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sesssionManager.setCategoryTypeAndIdDetails("dashboard", listDashBoradCategoryData.get(position).getCategoryId(), listDashBoradCategoryData.get(position).getCategoryName());

                Intent intetn = new Intent(_context, ItemDisplayActivity.class);


                _context.startActivity(intetn);


            }
        });

    }

    @Override
    public int getItemCount() {
        return listDashBoradCategoryData.size();
    }


}
