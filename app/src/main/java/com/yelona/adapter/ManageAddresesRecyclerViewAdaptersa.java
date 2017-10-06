package com.yelona.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yelona.ManageShippingAddressActivity;
import com.yelona.R;
import com.yelona.pojo.Addresses;
import com.yelona.session.SessionManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sathish Gadde on 16-Feb-17.
 */

public class ManageAddresesRecyclerViewAdaptersa extends RecyclerView.Adapter<ManageAddresesRecyclerViewAdaptersa.MyViewHolder> {


    private final Context _context;
    private final ArrayList<Addresses> list_addresses;
    private final LayoutInflater inflater;
    private final SessionManager sessionManager;
    private String address_type = "";
    private HashMap<String, String> userDetails = new HashMap<String, String>();


    public ManageAddresesRecyclerViewAdaptersa(Context context, ArrayList<Addresses> list_addresses, String type) {
        this._context = context;
        this.list_addresses = list_addresses;
        this.address_type = type;
        inflater = LayoutInflater.from(context);
        sessionManager = new SessionManager(context);
        userDetails = sessionManager.getSessionDetails();


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final TextView txtAddress;
        private final Button btnDeliverHere, btnEdit;
        private final TextView txtMobile;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtname);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtMobile = (TextView) itemView.findViewById(R.id.txtMobile);
            btnDeliverHere = (Button) itemView.findViewById(R.id.btnDeleverHere);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);


        }
    }

    @Override
    public ManageAddresesRecyclerViewAdaptersa.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_single_address, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ManageAddresesRecyclerViewAdaptersa.MyViewHolder holder, int position) {


        final Addresses ad = list_addresses.get(position);

        holder.txtName.setText(ad.getName());
        holder.txtAddress.setText(ad.getAddress1() + "," + ad.getAddress2() + ",\n" + ad.getCity() + "," + ad.getState() + "-" + ad.getPincode());

        holder.txtMobile.setText(ad.getMobileno());
        holder.btnDeliverHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(_context, "Deliever Here", Toast.LENGTH_SHORT).show();
                if (address_type.equals("billing")) {
                    sessionManager.setBillingAddress(userDetails.get(SessionManager.KEY_USERNAME), userDetails.get(SessionManager.KEY_USER_EMAIL), userDetails.get(SessionManager.KEY_USER_MOBILE), ad.getAddress1() + ad.getAddress2() + ad.getCity() + ad.getPincode(), ad.getState());


                    Intent ii = new Intent(_context, ManageShippingAddressActivity.class);
                    _context.startActivity(ii);

                } else {

                }
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(_context, "Edit", Toast.LENGTH_SHORT).show();

                sessionManager.setBillingAddress(userDetails.get(SessionManager.KEY_USERNAME), userDetails.get(SessionManager.KEY_USER_EMAIL), userDetails.get(SessionManager.KEY_USER_MOBILE), ad.getAddress1() + ad.getAddress2() + ad.getCity() + ad.getPincode(), ad.getState());


                Intent ii = new Intent(_context, ManageShippingAddressActivity.class);
                _context.startActivity(ii);


            }
        });

    }

    @Override
    public int getItemCount() {
        return list_addresses.size();
    }
}
