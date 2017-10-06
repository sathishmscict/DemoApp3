package com.yelona.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.yelona.R;
import com.yelona.pojo.AdminOrderData;

import java.util.ArrayList;

/**
 * Created by chiranjeevi mateti on 10-Jan-17.
 */

public class AdminOrderSummaryRecyclerViewAdapter extends RecyclerView.Adapter<AdminOrderSummaryRecyclerViewAdapter.MyViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;


    ArrayList<AdminOrderData> listClientMaster;

    public AdminOrderSummaryRecyclerViewAdapter(Context context, ArrayList<AdminOrderData> lstclients) {
        this.context = context;
        this.listClientMaster = lstclients;
        inflater = LayoutInflater.from(context);


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView  txtAddress, txtTime,txtCientName,txtOrderId;



        //private final EditText edtOrderId;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtOrderId = (TextView) itemView.findViewById(R.id.txtOrderID);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtTime= (TextView) itemView.findViewById(R.id.txtTime);
            txtCientName = (TextView)itemView.findViewById(R.id.txtCientName);




        }
    }

    @Override
    public AdminOrderSummaryRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_admin_single_order, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdminOrderSummaryRecyclerViewAdapter.MyViewHolder holder, int position) {

        AdminOrderData cm = listClientMaster.get(position);

        holder.txtOrderId.setText("Order Id : "+cm.getOrderid());
        holder.txtAddress.setText(cm.getAddress());
        holder.txtTime.setText(cm.getOrdertime());
        holder.txtCientName.setText(cm.getClientname());




    }

    @Override
    public int getItemCount() {
        return listClientMaster.size();
    }

}
