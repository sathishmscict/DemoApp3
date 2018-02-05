package com.yelona.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.yelona.R;
import com.yelona.pojo.Notification;

import java.util.List;

/**
 * Created by Satish Gadde on 30-04-2016.
 */
public class NotificaitonRecyclerView extends RecyclerView.Adapter<NotificaitonRecyclerView.MyViewHolder> {

    private List<Notification> notifiationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView notificationid,txttitle,txtmessage,txtdate;

        public MyViewHolder(View view) {
            super(view);

            txttitle= (TextView) view.findViewById(R.id.txtheading);
            txtmessage= (TextView) view.findViewById(R.id.txtnote);
            txtdate = (TextView) view.findViewById(R.id.txtdate);



        }

    }


    public NotificaitonRecyclerView(List<Notification> moviesList) {
        this.notifiationList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notification noti = notifiationList.get(position);

        holder.txttitle.setText(""+noti.getTitle());
        holder.txtmessage.setText(""+noti.getMessage());
        holder.txtdate.setText(""+noti.getDate());




    }

    @Override
    public int getItemCount() {
        return notifiationList.size();
    }
}