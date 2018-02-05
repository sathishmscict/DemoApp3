package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.yelona.helper.AllKeys.back_button;

public class DisplayImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerview_images;
    private Context context = this;
    private SubsamplingScaleImageView imgItem;
    ArrayList<ProductData> list_ProductImages = new ArrayList<ProductData>();
    private String TAG = DisplayImagesActivity.class.getSimpleName();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(back_button);
        } catch (Exception e) {
            e.printStackTrace();
        }



        try {
            setTitle(getIntent().getStringExtra("ProductName"));
        } catch (Exception e) {
            e.printStackTrace();
            setTitle("sat");

        }

        sessionManager = new SessionManager(context);
        imgItem = (SubsamplingScaleImageView) findViewById(R.id.imgItem);

        recyclerview_images = (RecyclerView) findViewById(R.id.recyclerview_images);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerview_images.setLayoutManager(layoutManager2);
        recyclerview_images.setItemAnimator(new DefaultItemAnimator());


        try {
            //image_url = getFormattedImageURL(image_url);

            String getImages = getIntent().getStringExtra("AllImages");


            ArrayList<String> list_images = new ArrayList<String>(Arrays.asList(getImages.split(",")));

            for (int i = 0; i < list_images.size(); i++) {
                ProductData pd = new ProductData(list_images.get(i), true);
                list_ProductImages.add(pd);

            }

            URL myFileUrl = new URL(list_ProductImages.get(0).getImage_url());
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(is);
            imgItem.setImage(ImageSource.bitmap(bm));
            //.placeholder(R.mipmap.ic_launcher)
            // Glide.with(context).load(image_url).error(R.mipmap.ic_launcher).into(imgItem);

            ProductImagesRecyclerViewAdapter_New adapter_ProductImages = new ProductImagesRecyclerViewAdapter_New(context);
            recyclerview_images.setAdapter(adapter_ProductImages);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public class ProductImagesRecyclerViewAdapter_New extends RecyclerView.Adapter<ProductImagesRecyclerViewAdapter_New.MyViewHolder> {


        private final Context _context;
        // private final ArrayList<ProductData> list_NewProcuts;
        private final LayoutInflater inflater;
        private final SessionManager sessionmanager;
        private HashMap<String, String> userDetails = new HashMap<String, String>();

        public ProductImagesRecyclerViewAdapter_New(Context context) {
            this._context = context;
            // this.list_NewProcuts = listNewProduct;
            inflater = LayoutInflater.from(context);

            sessionmanager = new SessionManager(context);
            userDetails = sessionmanager.getSessionDetails();

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            // private final RatingBar ratingBar;
            //  private final TextView txtPrice, txtName, txtDelete,txtProductMRP;
            private final ImageView imgItem;
            //  private final CardView crdProduct;
            // private final LinearLayout myBackground;

            public MyViewHolder(View itemView) {
                super(itemView);
                //  crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
                imgItem = (ImageView) itemView.findViewById(R.id.imgItem);

            }

        }

        @Override
        public ProductImagesRecyclerViewAdapter_New.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View v = inflater.inflate(R.layout.row_single_product_images, parent, false);

            ProductImagesRecyclerViewAdapter_New.MyViewHolder viewFolder = new ProductImagesRecyclerViewAdapter_New.MyViewHolder(v);
            return viewFolder;
        }

        @Override
        public void onBindViewHolder(final ProductImagesRecyclerViewAdapter_New.MyViewHolder holder, final int position) {

            final ProductData pd = list_ProductImages.get(position);


            try {
                Glide.with(_context).load(pd.getImage_url()).crossFade().into(holder.imgItem);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.imgItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {


                        URL myFileUrl = new URL(pd.getImage_url());
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        imgItem.setImage(ImageSource.bitmap(bm));
                        //.placeholder(R.mipmap.ic_launcher)
                        // Glide.with(context).load(image_url).error(R.mipmap.ic_launcher).into(imgItem);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }


        @Override
        public int getItemCount() {
            return list_ProductImages.size();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            try {

                //Log.d(TAG,context.getPackageName() + "." + getIntent().getStringExtra("ActivityName"));

                Intent i = new Intent(context, SingleItemActivity.class);
                //i.putExtra("ActivityName",getIntent().getStringExtra("ActivityName"));
                //sessionManager.setActivityName();
                startActivity(i);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {
            //Log.d(TAG,context.getPackageName() + "." + getIntent().getStringExtra("ActivityName"));

            Intent i = new Intent(context, SingleItemActivity.class);
           // i.putExtra("ActivityName",getIntent().getStringExtra("ActivityName"));
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
