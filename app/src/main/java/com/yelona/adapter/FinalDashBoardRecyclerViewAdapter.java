package com.yelona.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelona.R;
import com.yelona.SingleItemActivity;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sathish Gadde on 30-Jan-17.
 */

public class FinalDashBoardRecyclerViewAdapter extends RecyclerView.Adapter<FinalDashBoardRecyclerViewAdapter.MyViewHolder> {


    private final Context _context;
    private final ArrayList<ProductData> list_NewProcuts;
    private final LayoutInflater inflater;
    private final SessionManager sessionmanager;
    private final ArrayList<String> list_wishList;


    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = FinalDashBoardRecyclerViewAdapter.class.getSimpleName();

    public FinalDashBoardRecyclerViewAdapter(Context context, ArrayList<ProductData> listNewProduct, ArrayList<String> list_wishList) {
        this._context = context;
        this.list_NewProcuts = listNewProduct;
        this.list_wishList = list_wishList;
        inflater = LayoutInflater.from(context);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private final TextView txtName;
        private final ImageView imgItem;
        private final CardView crdProduct;

        public MyViewHolder(View itemView) {
            super(itemView);
            crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            txtName = (TextView) itemView.findViewById(R.id.txtItemName);



            if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard") || userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("category")) {
                // sets width to wrap content and height to 10 dp ->

                crdProduct.setLayoutParams(new CardView.LayoutParams(
                        CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.WRAP_CONTENT));
                crdProduct.setContentPadding(4, 4, 4, 4);
                crdProduct.setCardElevation(8);

            } else {

                crdProduct.setCardElevation(4);
            }


        }
    }

    @Override
    public FinalDashBoardRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = inflater.inflate(R.layout.row_single_final_dashboard, parent, false);
        MyViewHolder viewFolder = new MyViewHolder(v);
        return viewFolder;
    }

    @Override
    public void onBindViewHolder(final FinalDashBoardRecyclerViewAdapter.MyViewHolder holder, int position) {

        final ProductData pd = list_NewProcuts.get(position);

        holder.txtName.setText(pd.getProductname());


        /*if (pd.getProductid() == "47") {

        int IsExistInWishList = list_wishList.indexOf(pd.getProductid());
        if (IsExistInWishList == -1) {
            holder.txtWishList.setBackgroundResource(R.drawable.ic_wishlist_default);
        } else {
            holder.txtWishList.setBackgroundResource(R.drawable.ic_wishlist_seleceted);

        }
        }*/







        try {

            String image_url = pd.getImage_url();
            if (image_url.contains("images") && image_url.contains("__w-200")) {
                int pos = image_url.lastIndexOf("/");
                //pos = image_url.inde

                                    /*if (image_url.contains("600")) {
                                        image_url = image_url.substring(pos, image_url.length());
                                        image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                                    }
                                     else*/

                if (image_url.contains("600")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                }
                else if (image_url.contains("400")) {

                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w400" + image_url;
                } else {
                    image_url = image_url.substring(pos, image_url.length());
                    image_url = AllKeys.RESOURSES + "uploads/images/w200" + image_url;
                }


                // Log.d(TAG, "New Url : " + image_url);
            } else {
                image_url = AllKeys.RESOURSES + image_url;
            }

            /*Transformation blurTransformation = new Transformation() {
                @Override
                public Bitmap transform(Bitmap source) {
                    Bitmap blurred = Blur.fastblur(_context, source, 10);
                    source.recycle();
                    return blurred;
                }

                @Override
                public String key() {
                    return "blur()";
                }
            };*/

            Glide.with(_context).load(image_url).crossFade().placeholder(R.drawable.loader_yellow_original_150).error(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.imgItem);
/*
            Glide.with(this).load(R.drawable.demo)
                    .bitmapTransform(new BlurTransformation(context))
                    .into((ImageView) findViewById(R.id.image));*/

            /*Glide.with(_context).load(image_url)
                    .bitmapTransform(new BlurTransformation(_context,200), new CropCircleTransformation(_context))
                    .into(holder.imgItem);
*/

           /* Glide.with(_context).load(image_url)
                    .bitmapTransform(new BlurTransformation(_context))
                    .into(holder.imgItem);*/


        /*    Glide.with(_context).load(image_url)
                    .bitmapTransform(new BlurTransformation(_context, 25), new CropCircleTransformation(_context))
                    .into(holder.imgItem);*/


        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.imgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    //Toast.makeText(_context, "Name : " + pd.getProductname() + " Id : " + pd.getProductid(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Clicked On Name : " + pd.getProductname() + " Id : " + pd.getProductid());
                    Intent intent = new Intent(_context,
                            SingleItemActivity.class);

                    int IsExistInWishList = list_wishList.indexOf(pd.getProductid());


                    sessionmanager.setProductDetails(pd.getProductid(), String.valueOf(IsExistInWishList),userDetails.get(SessionManager.KEY_PRODUCT_DESCR),pd.getImage_url(), userDetails.get(SessionManager.KEY_PRODUCT_RATING));


                    sessionmanager.setCategoryTypeAndIdDetails("category", pd.getCategory_id(),"");


                    //intent.putExtra("ProductId", pd.getProductid());
                    intent.putExtra("ActivityName", "ItemDisplayActivity");

                    //intent.putExtra("ProductId", pd.getProductid());



                    intent.putExtra("IsExistInWishList", IsExistInWishList);


                    sessionmanager.setActivityName("ItemDisplayActivity");
                    _context.startActivity(intent);
                    //finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void sendWishListDetailsToServer(final dbhandler db, final SQLiteDatabase sd, final String ProductId, final String ProductPrice, final String type) {


        String url_addtowishlist = AllKeys.WEBSITE + "manageWishlist/" + type + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId + "/" + db.convertEncodedString(db.getDateTime()) + "/" + db.convertEncodedString(db.getDateTime()) + "/" + ProductPrice + "";

        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
        StringRequest str_addToWishList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response ManageWishList : " + response);

                if (response.equals("0")) {
                    Toast.makeText(_context, "Sorry,try again...", Toast.LENGTH_SHORT).show();
                } else {
                    if (type.equals("add")) {
                        Toast.makeText(_context, "Added to your wishlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(_context, "Removed from your wishlist", Toast.LENGTH_SHORT).show();
                    }

                }

                db.close();
                sd.close();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null) {
                    sendWishListDetailsToServer(db, sd, ProductId, ProductPrice, type);

                }
                Log.d(TAG, "Error in ManageWishList : " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_addToWishList);

    }

    @Override
    public int getItemCount() {
        return list_NewProcuts.size();
    }
}
