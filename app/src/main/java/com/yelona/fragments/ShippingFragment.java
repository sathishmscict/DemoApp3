package com.yelona.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.yelona.R;
import com.yelona.database.dbhandler;
import com.yelona.session.SessionManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class ShippingFragment extends Fragment {

    private ListView lvdata;



    private Context context=getActivity();
    private HashMap<String, String> userDetails=new HashMap<String,String>();

    private TextView nodata;
    private TextView txtShippingCost;
    private TextView txtEstimateArrival;
    private TextView txtReturnPolicy;
    private SessionManager sessionManager;

    public ShippingFragment() {
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
        View rootView=inflater.inflate(R.layout.fragment_shipping, container, false);




        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getSessionDetails();

        txtShippingCost = (TextView)rootView.findViewById(R.id.txtShippingCost);
        txtEstimateArrival = (TextView)rootView.findViewById(R.id.txtEstimateArrival);
        txtReturnPolicy = (TextView)rootView.findViewById(R.id.txtReturnPolicy);


        if(userDetails.get(SessionManager.KEY_DELIVERY_CHARGES).equals("0"))
        {
            txtShippingCost.setText("\u20b9 Free");
        }
        else
        {
            txtShippingCost.setText("\u20b9 "+ userDetails.get(SessionManager.KEY_DELIVERY_CHARGES) +"");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         //dateFormat = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date myDate = dateFormat.parse(dbhandler.getDateTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);


            String date="";
            if(Integer.parseInt(userDetails.get(SessionManager.KEY_SHIPPED_DAYS)) >=7)
            {
                try {
                    calendar.add(Calendar.DAY_OF_YEAR, +Integer.parseInt(userDetails.get(SessionManager.KEY_SHIPPED_DAYS)));
                    Date newDate = calendar.getTime();

                    date = dateFormat.format(newDate);

                    DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    DateFormat targetFormat = new SimpleDateFormat("MMMM dd yyyy");
                    Date Newdate = originalFormat.parse(date);
                    String formattedDate = targetFormat.format(Newdate);  // 20120821


                    txtEstimateArrival.setText(""+ userDetails.get(SessionManager.KEY_SHIPPED_DAYS) +" Days ("+ formattedDate +")");
                    txtReturnPolicy.setText("Seller will accept returns with in a "+ userDetails.get(SessionManager.KEY_SHIPPED_DAYS) +" days from date of delivery of the item");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    calendar.add(Calendar.DAY_OF_YEAR, +7);
                    Date newDate = calendar.getTime();

                    date = dateFormat.format(newDate);

                    DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    DateFormat targetFormat = new SimpleDateFormat("MMMM dd yyyy");
                    Date Newdate = originalFormat.parse(date);
                    String formattedDate = targetFormat.format(Newdate);  // 20120821


                    //String month = date.substring(4,7);


                    txtEstimateArrival.setText("7 Days ("+ formattedDate +")");
                    txtReturnPolicy.setText("Seller will accept returns with in a 7 days from date of delivery of the item");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }












        return rootView;
    }

}
