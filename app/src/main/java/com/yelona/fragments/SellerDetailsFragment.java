package com.yelona.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelona.R;
import com.yelona.helper.AllKeys;
import com.yelona.session.SessionManager;

import java.util.HashMap;


public class SellerDetailsFragment extends Fragment {

    private ListView lvdata2;


    private TextView nodata;
    private EditText edtSellerCompnay;
    private EditText edtSellerName;
    private EditText edtSellerEmail;
    private EditText edtSellerAddress;
    private TextView txtSellerCode;
    private ImageView imgSellerAvatar;
    private TextView txtRating;
    private TextView txtSellerRatingsCount;
    private Context context = getActivity();
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = SellerDetailsFragment.class.getSimpleName();


    public SellerDetailsFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_seller_dertails, container, false);

        edtSellerCompnay = (EditText) rootView.findViewById(R.id.edtSellerCompnay);
        edtSellerName = (EditText) rootView.findViewById(R.id.edtSellerName);
        edtSellerEmail = (EditText) rootView.findViewById(R.id.edtSellerEmail);
        edtSellerAddress = (EditText) rootView.findViewById(R.id.edtSellerAddress);
        txtSellerCode = (TextView) rootView.findViewById(R.id.txtSellerCode);
        imgSellerAvatar = (ImageView) rootView.findViewById(R.id.imgSellerAvatar);

        txtRating = (TextView) rootView.findViewById(R.id.txtSellerRating);
        txtSellerRatingsCount = (TextView) rootView.findViewById(R.id.txtSellerRatingsCount);


        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getSessionDetails();


        if (userDetails.get(SessionManager.KEY_SELLER_COMPANY_NAME).equals(""))
            edtSellerCompnay.setText("-");
        else
            edtSellerCompnay.setText(userDetails.get(SessionManager.KEY_SELLER_COMPANY_NAME));

        if (userDetails.get(SessionManager.KEY_SELLER_NAME).equals(""))
            edtSellerName.setText("-");
        else
            edtSellerName.setText(userDetails.get(SessionManager.KEY_SELLER_NAME));



        if (userDetails.get(SessionManager.KEY_SELLER_EMAIL).equals(""))
            edtSellerEmail.setText("-");
        else
            edtSellerEmail.setText(userDetails.get(SessionManager.KEY_SELLER_EMAIL));

        if (userDetails.get(SessionManager.KEY_SELLER_ADDRESS).equals(""))
            edtSellerAddress.setText("-");
        else
            edtSellerAddress.setText(userDetails.get(SessionManager.KEY_SELLER_ADDRESS));

        txtSellerCode.setText("Seller Code - " + userDetails.get(SessionManager.KEY_SELLER_CODE));


        edtSellerCompnay.setClickable(false);
        edtSellerCompnay.setClickable(false);
        edtSellerCompnay.setFocusable(false);

        edtSellerName.setClickable(false);
        edtSellerName.setClickable(false);
        edtSellerName.setFocusable(false);


        edtSellerEmail.setClickable(false);
        edtSellerEmail.setClickable(false);
        edtSellerEmail.setFocusable(false);

        edtSellerAddress.setClickable(false);
        edtSellerAddress.setClickable(false);
        edtSellerAddress.setFocusable(false);


        //glide image loading

        try {

            Glide.with(getActivity()).load(getFormattedImageURL(userDetails.get(SessionManager.KEY_SELLER_AVATAR), "200")).placeholder(R.drawable.icon_userlogo).error(R.drawable.icon_userlogo).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade(R.anim.fadein, 2000).into(imgSellerAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }


        txtRating.setText(userDetails.get(SessionManager.KEY_SELLER_RATING));
        txtSellerRatingsCount.setText("0 ratings");


        return rootView;
    }

    private String getFormattedImageURL(String image_url, String size) {

        if (size.equals("200")) {
            if (image_url.contains("images") && image_url.contains("__w-200")) {
                int pos = image_url.lastIndexOf("/");
                //pos = image_url.inde
                if (image_url.contains("200")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w200" + image_url;
                } else if (image_url.contains("400")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                } else {
                    image_url = AllKeys.RESOURSES + image_url;

                }


                Log.d(TAG, "New Url : " + image_url);
            } else {
                image_url = AllKeys.RESOURSES + image_url;
            }

        } else {

            if (image_url.contains("images") && image_url.contains("__w-200")) {
                int pos = image_url.lastIndexOf("/");
                //pos = image_url.inde
                if (image_url.contains("900")) {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w900" + image_url;
                } else if (image_url.contains("800")) {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w800" + image_url;
                } else if (image_url.contains("600")) {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                } else if (image_url.contains("400")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                } else {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w200" + image_url;
                }


                Log.d(TAG, "New Url : " + image_url);
            } else {
                image_url = AllKeys.RESOURSES + image_url;
            }
        }


        Log.d(TAG, "final image path :" + image_url);
        return image_url;
    }


}
