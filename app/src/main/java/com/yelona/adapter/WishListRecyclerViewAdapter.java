package com.yelona.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.yelona.R;
import com.yelona.SingleItemActivity;
import com.yelona.WishListActivity;
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

public class WishListRecyclerViewAdapter extends RecyclerView.Adapter<WishListRecyclerViewAdapter.MyViewHolder> {


    String image_url;
    private final Context _context;
    private final ArrayList<ProductData> list_NewProcuts;
    private final LayoutInflater inflater;
    private final SessionManager sessionmanager;
    private final String layoutType;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = WishListRecyclerViewAdapter.class.getSimpleName();

    public WishListRecyclerViewAdapter(Context context, ArrayList<ProductData> listNewProduct, String layoutType) {
        this._context = context;
        this.list_NewProcuts = listNewProduct;
        inflater = LayoutInflater.from(context);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();
        this.layoutType = layoutType;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final RatingBar ratingBar;
        private final TextView txtPrice, txtName, txtDelete,txtProductMRP,txtOffer,txtStockStatus;
        private final ImageView imgItem;
        private final CardView crdProduct;

        public MyViewHolder(View itemView) {
            super(itemView);
            crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            txtName = (TextView) itemView.findViewById(R.id.txtItemName);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            txtDelete = (TextView) itemView.findViewById(R.id.txtDelete);
            txtProductMRP = (TextView) itemView.findViewById(R.id.txtProductMRP);
            txtStockStatus = (TextView) itemView.findViewById(R.id.txtStockStatus);
            txtOffer  =(TextView)itemView.findViewById(R.id.txtOffer);

          /*  if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard") || userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("category")) {
                // sets width to wrap content and height to 10 dp ->
                crdProduct.setLayoutParams(new CardView.LayoutParams(
                        CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.WRAP_CONTENT));
                crdProduct.setContentPadding(4, 4, 4, 4);
                crdProduct.setCardElevation(8);
            } else {
                crdProduct.setCardElevation(4);
            }*/


        }
    }

    @Override
    public WishListRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = inflater.inflate(R.layout.row_single_product_wishlist, parent, false);

        MyViewHolder viewFolder = new MyViewHolder(v);
        return viewFolder;
    }

    @Override
    public void onBindViewHolder(final WishListRecyclerViewAdapter.MyViewHolder holder, final int position) {

        final ProductData pd = list_NewProcuts.get(position);

        holder.txtName.setText(pd.getProductname());
       // holder.txtPrice.setText(Html.fromHtml("<b>" + pd.getMrp() + "</b>" + " <del>" + pd.getPrice() + "</del>"));
        holder.txtPrice.setText("\u20b9 "+pd.getPrice());
        holder.txtProductMRP.setText("\u20b9 "+pd.getMrp());


        //Check Product availability
        if (pd.getInventory().equals("0")) {
            //SetVisible out of stock
            holder.txtStockStatus.setVisibility(View.VISIBLE);
            holder.txtStockStatus.setText("Out Of Stock");

        } else {
            //SetInVisible out of stock
            holder.txtStockStatus.setVisibility(View.GONE);
        }


        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbhandler db= new dbhandler(_context);
                SQLiteDatabase sd= db.getReadableDatabase();
                sd = db.getWritableDatabase();

                holder.txtDelete.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.anim_item));

                sendWishListDetailsToServer(db,sd,pd.getProductid(),pd.getPrice(),"remove",position);



            }
        });



        try {

             image_url = pd.getImage_url();
            if (image_url.contains("images") && image_url.contains("__w-200"))
            {
                int pos = image_url.lastIndexOf("/");
                //pos = image_url.inde

                                    /*if (image_url.contains("600")) {
                                        image_url = image_url.substring(pos, image_url.length());
                                        image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                                    }
                                     else*/ if (image_url.contains("400")) {

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

            Glide.with(_context).load(image_url).crossFade().into(holder.imgItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Double offer = Double.parseDouble(pd.getPrice())*100/Double.parseDouble(pd.getMrp());
        int percentage = 100 - offer.intValue();
        if(percentage == 0 || percentage < 0)
        {
            holder.txtOffer.setVisibility(View.GONE);
        }
        else {
            //  holder.txtOffer.setVisibility(View.GONE);
            holder.txtOffer.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.fadein));

            if (percentage <= 9) {
                holder.txtOffer.setText(" " + percentage + " % off");
            } else {
                holder.txtOffer.setText("" + percentage + " % off");
            }
        }

        holder.imgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Toast.makeText(_context, "Name : " + pd.getProductname() + " Id : " + pd.getProductid(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(_context,
                            SingleItemActivity.class);
                    sessionmanager.setProductDetails(pd.getProductid(),"1",userDetails.get(SessionManager.KEY_PRODUCT_DESCR),image_url, userDetails.get(SessionManager.KEY_PRODUCT_RATING));

                    //intent.putExtra("ProductId", pd.getProductid());
                    intent.putExtra("ActivityName", "WishListActivity");
                    _context.startActivity(intent);
                    //finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void sendWishListDetailsToServer(final dbhandler db, final SQLiteDatabase sd, final String ProductId, final String ProductPrice, final String type, final int position) {



        String url_addtowishlist = AllKeys.WEBSITE + "manageWishlist/" + type + "/"+ userDetails.get(SessionManager.KEY_USERID) +"/"+ ProductId +"/" + db.convertEncodedString(db.getDateTime()) + "/" + db.convertEncodedString(db.getDateTime()) + "/" + ProductPrice + "";

        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
        final StringRequest str_addToWishList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response ManageWishList : " + response);

                if (response.equals("1"))
                {



                    if (type.equals("add")) {
                        Toast.makeText(_context, "Added to your wishlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(_context, "Item deleted from  wishlist", Toast.LENGTH_SHORT).show();
                    }
                    list_NewProcuts.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    if(list_NewProcuts.size() == 0)
                    {

                        Intent intent = new Intent(_context , WishListActivity.class);
                        _context.startActivity(intent);


                    }

                }
                else
                {
                    Toast.makeText(_context, "Sorry,try again...", Toast.LENGTH_SHORT).show();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null) {
                    sendWishListDetailsToServer(db, sd, ProductId, ProductPrice, type,position);

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
