package com.yelona.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.TintAwareDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;


import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yelona.LoginActivity;
import com.yelona.R;
import com.yelona.RateAndReviewActivity;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;


public class ReviewFragment extends Fragment {


    private Context context = getActivity();
    private HashMap<String, String> userDetails = new HashMap<String, String>();


    private SQLiteDatabase sd;

    private int count = 1;
    private TextView nodata;
    private Button btnRateAndReview;
    private RecyclerView recyclerview_review;
    private ArrayList<ProductReviewInfo> listProductReviewInfo = new ArrayList<ProductReviewInfo>();
    private String TAG = ReviewFragment.class.getSimpleName();
    private SpotsDialog pDialog;
    private LinearLayout llNoReview;
    private TextView txtRatingAverage;
    private TextView txtRatingsCount;
    private ProgressBar pb_rating1;
    private ProgressBar pb_rating2;
    private ProgressBar pb_rating3;
    private ProgressBar pb_rating4;
    private ProgressBar pb_rating5;
    private ProductReviewRecyclerViewAdapter adapter;
    private SessionManager sessionManager;

    int star_1 = 0, star_2 = 0, star_3 = 0, star_4 = 0, star_5 = 0;

    public ReviewFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        btnRateAndReview = (Button) rootView.findViewById(R.id.btnRateAndReview);
        llNoReview = (LinearLayout) rootView.findViewById(R.id.llNoReview);


        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getSessionDetails();
        btnRateAndReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userDetails.get(SessionManager.KEY_USERID).equals("0")) {
                    sessionManager.setNewUserSession("RateAndReviewActivity");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();


                } else {
                    Intent intent = new Intent(getActivity(), RateAndReviewActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });

        recyclerview_review = (RecyclerView) rootView.findViewById(R.id.recyclerview_review);
        txtRatingAverage = (TextView) rootView.findViewById(R.id.txtRatingAverage);
        txtRatingsCount = (TextView) rootView.findViewById(R.id.txtRatingsCount);


        txtRatingAverage.setText(userDetails.get(SessionManager.KEY_PRODUCT_RATING));

        pb_rating1 = (ProgressBar) rootView.findViewById(R.id.pb_rating1);
        pb_rating2 = (ProgressBar) rootView.findViewById(R.id.pb_rating2);
        pb_rating3 = (ProgressBar) rootView.findViewById(R.id.pb_rating3);
        pb_rating4 = (ProgressBar) rootView.findViewById(R.id.pb_rating4);
        pb_rating5 = (ProgressBar) rootView.findViewById(R.id.pb_rating5);


        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerview_review.setLayoutManager(layoutManager);
        recyclerview_review.setItemAnimator(new DefaultItemAnimator());


        pDialog = new SpotsDialog(getActivity());
        pDialog.setCancelable(false);

        getAllReviewInfoFromServer();


        return rootView;

    }

    private void getAllReviewInfoFromServer() {
        showDialog();
        String url_getPrductReview = AllKeys.WEBSITE + "getAllReviewsByProduct/" + userDetails.get(SessionManager.KEY_PRODUCT_ID) + "";
        Log.d(TAG, "url_GetProductReviews" + url_getPrductReview);
        StringRequest str_getProductReview = new StringRequest(Request.Method.GET, url_getPrductReview, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response ProductReview : " + response);

                listProductReviewInfo.clear();
                if (response.contains("created_at")) {
                    try {
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject c = arr.getJSONObject(i);
                            ProductReviewInfo pri = new ProductReviewInfo(c.getString(AllKeys.TAG_REVIEW), c.getString(AllKeys.TAG_REVIEW_CREATED_AT), c.getString(AllKeys.TAG_REVIEW_STARS), c.getString(AllKeys.TAG_REVIEW_USER_NAME));
                            listProductReviewInfo.add(pri);
                            if (c.getString(AllKeys.TAG_REVIEW_STARS).equals("1")) {
                                ++star_1;
                            } else if (c.getString(AllKeys.TAG_REVIEW_STARS).equals("2")) {
                                ++star_2;

                            } else if (c.getString(AllKeys.TAG_REVIEW_STARS).equals("3")) {
                                ++star_3;
                            } else if (c.getString(AllKeys.TAG_REVIEW_STARS).equals("4")) {
                                ++star_4;

                            } else if (c.getString(AllKeys.TAG_REVIEW_STARS).equals("5")) {
                                ++star_5;
                            }


                        }

                        try {
                            int a=(star_1*1+star_2*2+star_3*3+star_4*4+star_5*5)/arr.length();
                            txtRatingAverage.setText(String.valueOf(a));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        llNoReview.setVisibility(View.GONE);
                        recyclerview_review.setVisibility(View.VISIBLE);






                        txtRatingsCount.setText(listProductReviewInfo.size() + " ratings");
                        adapter = new ProductReviewRecyclerViewAdapter();
                        recyclerview_review.setAdapter(adapter);


                        pb_rating1.setProgress(setRating(star_1));
                        pb_rating2.setProgress(setRating(star_2));
                        pb_rating3.setProgress(setRating(star_3));
                        pb_rating4.setProgress(setRating(star_4));
                        pb_rating5.setProgress(setRating(star_5));

                        pb_rating1.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.rating1), android.graphics.PorterDuff.Mode.SRC_ATOP);
                        pb_rating2.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.rating2), android.graphics.PorterDuff.Mode.SRC_ATOP);
                        pb_rating3.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.rating3), android.graphics.PorterDuff.Mode.SRC_ATOP);
                        pb_rating4.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.rating4), android.graphics.PorterDuff.Mode.SRC_ATOP);
                        pb_rating5.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.rating5), android.graphics.PorterDuff.Mode.SRC_ATOP);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    llNoReview.setVisibility(View.VISIBLE);
                    recyclerview_review.setVisibility(View.GONE);
                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();

                } else {
                    getAllReviewInfoFromServer();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getProductReview);


    }


    private int setRating(int rating) {

        int percentage = 0;
        percentage = (rating * 100) / listProductReviewInfo.size();

        return percentage;
    }

    public void showDialog() {

        if (!pDialog.isShowing()) {

            pDialog.show();
        }


    }

    public void hideDialog() {
        try {
            if (pDialog.isShowing()) {
                pDialog.dismiss();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Produc Review Related Pojo Class
     */
    public class ProductReviewInfo {
        String Review;
        String ReviewDate;
        String ReviewStars;
        String CustomerName;

        public String getReview() {
            return Review;
        }

        public void setReview(String review) {
            Review = review;
        }

        public String getReviewDate() {
            return ReviewDate;
        }

        public void setReviewDate(String reviewDate) {
            ReviewDate = reviewDate;
        }

        public String getReviewStars() {
            return ReviewStars;
        }

        public void setReviewStars(String reviewStars) {
            ReviewStars = reviewStars;
        }

        public String getCustomerName() {
            return CustomerName;
        }

        public void setCustomerName(String customerName) {
            CustomerName = customerName;
        }


        public ProductReviewInfo(String review, String reviewDate, String reviewStars, String customerName) {
            Review = review;
            ReviewDate = reviewDate;
            ReviewStars = reviewStars;
            CustomerName = customerName;
        }


    }

    public class ProductReviewRecyclerViewAdapter extends RecyclerView.Adapter<ProductReviewRecyclerViewAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtReviewDate, txtReview, txtUserName;
            private RatingBar rbProductRating;

            public MyViewHolder(View itemView) {
                super(itemView);


                rbProductRating = (RatingBar) itemView.findViewById(R.id.rbProductRating);
                txtReviewDate = (TextView) itemView.findViewById(R.id.txtReviewDate);

                txtReview = (TextView) itemView.findViewById(R.id.txtReview);
                txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);

            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getActivity()).inflate(R.layout.row_single_review, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.rbProductRating.setRating(Float.parseFloat(listProductReviewInfo.get(position).getReviewStars()));
            holder.txtReviewDate.setText(listProductReviewInfo.get(position).getReviewDate());
            holder.txtReview.setText(listProductReviewInfo.get(position).getReview());
            if (listProductReviewInfo.get(position).getCustomerName().equals(userDetails.get(SessionManager.KEY_USER_FIRSTNAME))) {
                holder.txtUserName.setText(listProductReviewInfo.get(position).getCustomerName() + " ( you )");
            } else {
                holder.txtUserName.setText(listProductReviewInfo.get(position).getCustomerName());
            }


        }

        @Override
        public int getItemCount() {
            return listProductReviewInfo.size();
        }
    }


}
