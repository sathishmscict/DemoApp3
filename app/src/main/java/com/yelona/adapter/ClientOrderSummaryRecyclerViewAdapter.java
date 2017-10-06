package com.yelona.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.yelona.R;
import com.yelona.pojo.ClientOrderData;

import java.util.ArrayList;

/**
 * Created by chiranjeevi mateti on 10-Jan-17.
 */

public class ClientOrderSummaryRecyclerViewAdapter extends RecyclerView.Adapter<ClientOrderSummaryRecyclerViewAdapter.MyViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;


    ArrayList<ClientOrderData> listOrderData;

    public ClientOrderSummaryRecyclerViewAdapter(Context context, ArrayList<ClientOrderData> lstclientorderdata) {
        this.context = context;
        this.listOrderData = lstclientorderdata;
        inflater = LayoutInflater.from(context);


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView  txtMonth,txtDay,txtOrderId;



        //private final EditText edtOrderId;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtOrderId = (TextView) itemView.findViewById(R.id.txtOrderID);
            txtDay = (TextView) itemView.findViewById(R.id.txtDay);
            txtMonth= (TextView) itemView.findViewById(R.id.txtMonth);




        }
    }

    @Override
    public ClientOrderSummaryRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_client_single_order, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ClientOrderSummaryRecyclerViewAdapter.MyViewHolder holder, int position) {

        ClientOrderData cm = listOrderData.get(position);

        holder.txtOrderId.setText("Order Id : "+cm.getOrderid());

        holder.txtDay.setText(cm.getOrderday());
        holder.txtMonth.setText(cm.getOrdermonth()+", 2017");




    }

    @Override
    public int getItemCount() {
        return listOrderData.size();
    }

}
