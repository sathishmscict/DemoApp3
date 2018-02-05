package com.yelona.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.yelona.R;
import com.yelona.pojo.ClientMaster;

import java.util.ArrayList;

/**
 * Created by chiranjeevi mateti on 10-Jan-17.
 */

public class ClientMasterRecyclerViewAdapter extends RecyclerView.Adapter<ClientMasterRecyclerViewAdapter.MyViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final String nextvisibility;

    ArrayList<ClientMaster> listClientMaster;

    public ClientMasterRecyclerViewAdapter(Context context, ArrayList<ClientMaster> lstclients,String nextVisibility) {
        this.context = context;
        this.listClientMaster = lstclients;
        inflater = LayoutInflater.from(context);
        this.nextvisibility = nextVisibility;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtClientName, txtAddress, txtMobile;

        private final ImageView imgNext;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtClientName = (TextView) itemView.findViewById(R.id.txtCientName);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtMobile = (TextView) itemView.findViewById(R.id.txtMobile);
            imgNext = (ImageView) itemView.findViewById(R.id.imgNext);

            if(nextvisibility.equals("1")){
            imgNext.setVisibility(View.VISIBLE);
            }
            else
            {
                imgNext.setVisibility(View.GONE);

            }

        }
    }

    @Override
    public ClientMasterRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_single_client, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ClientMasterRecyclerViewAdapter.MyViewHolder holder, int position) {

        ClientMaster cm = listClientMaster.get(position);

        holder.txtClientName.setText(cm.getClientName());
        holder.txtAddress.setText(cm.getAddress());
        holder.txtMobile.setText(" "+cm.getMobile());

        holder.txtMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.CALL_PHONE)) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setTitle("Permission necessary");
                            alertBuilder.setMessage("CALL_PHONE permission is necessary");
                            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CALL_PHONE}, 121);
                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();

                        } else {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CALL_PHONE}, 121);
                        }

                    }
                    else
                    {
                        try {
                            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.txtMobile.getText().toString()));
                          //  context.startActivity(in);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(context, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    try {
                        Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.txtMobile.getText().toString()));
                       // context.startActivity(in);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        holder.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                Intent ii = new Intent(context, OrderSummaryActivity.class);
                context.startActivity(ii);*/


            }
        });
    }

    @Override
    public int getItemCount() {
        return listClientMaster.size();
    }

}
