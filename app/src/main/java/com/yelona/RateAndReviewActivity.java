package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.fragments.DescriptionFragment;
import com.yelona.helper.AllKeys;
import com.yelona.session.SessionManager;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class RateAndReviewActivity extends AppCompatActivity {

    private Context context = this;
    private RatingBar rbProductRating;
    private Button btnRateAndReview;
    private EditText edtReview;
    private ImageView imgProduct;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = RateAndReviewActivity.class.getSimpleName();
    private boolean IsReviewButtonClicked = false;
    private SpotsDialog pDialog;
    private int RATING=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_and_review);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(back_button);
        } catch (Exception e) {
            e.printStackTrace();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setTitle("Rate and Review");

        pDialog = new SpotsDialog(context);
        pDialog.setCancelable(false);


        sessionManager = new SessionManager(context);
        userDetails = sessionManager.getSessionDetails();


        rbProductRating = (RatingBar) findViewById(R.id.rbProductRating);
        edtReview = (EditText) findViewById(R.id.edtReview);
        btnRateAndReview = (Button) findViewById(R.id.btnRateAndReview);
        imgProduct = (ImageView) findViewById(R.id.imgProduct);




        Glide.with(context).load(userDetails.get(SessionManager.KEY_PRODUCT_IMAGE_URL)).placeholder(R.drawable.loader_yellow_original_150).error(R.drawable.loader_yellow_original_500).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade(R.anim.fadein, 2000).into(imgProduct);


        rbProductRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                //Toast.makeText(context, "RAting : " + rating, Toast.LENGTH_SHORT).show();

                try {
                    RATING = Math.round(rating);
                    //Toast.makeText(context, "RAting : " + RATING, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendRatingDetialsToServer();


            }
        });

        btnRateAndReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IsReviewButtonClicked = true;

                sendRatingDetialsToServer();

            }
        });


    }

    private void sendRatingDetialsToServer() {
                            //public function InsertReview($userId="",$createdAt="", $updatedAt="", $star="", $starableId="", $review="", $starableType="" )
        String url_ReviewProduct = AllKeys.WEBSITE + "InsertReview/"+ userDetails.get(SessionManager.KEY_USERID) +"/"+ dbhandler.convertEncodedString(dbhandler.getDateTime()) +"/"+ dbhandler.convertEncodedString(dbhandler.getDateTime()) +"/"+ RATING +"/"+ userDetails.get(SessionManager.KEY_PRODUCT_ID) +"/"+ dbhandler.convertEncodedString(edtReview.getText().toString()) +"";
        Log.d(TAG, "URL Review Product : " + url_ReviewProduct);
        StringRequest str_ReviewProduct = new StringRequest(Request.Method.GET, url_ReviewProduct, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("success")) {
                    if (IsReviewButtonClicked) {

                        Toast.makeText(context, "Review Submitted", Toast.LENGTH_SHORT).show();
                        Intent ii = new Intent(context, SingleItemActivity.class);
                        startActivity(ii);
                        finish();

                    }

                } else {
                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();
                } else {
                    sendRatingDetialsToServer();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_ReviewProduct);


    }

    public void showDialog() {

        if (!pDialog.isShowing()) {

            pDialog.show();
        }


    }

    public void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent ii = new Intent(context, SingleItemActivity.class);
        startActivity(ii);
        finish();
    }
}
