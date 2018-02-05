package com.yelona.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lid.lib.LabelImageView;
import com.squareup.picasso.Picasso;
import com.yelona.LoginActivity;
import com.yelona.R;
import com.yelona.SingleItemActivity;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.CustomFonts;
import com.yelona.pojo.ProductData;
import com.yelona.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sathish Gadde on 30-Jan-17.
 */

public class NewProductsRecyclerViewAdapter extends RecyclerView.Adapter<NewProductsRecyclerViewAdapter.MyViewHolder> {


    private final Context _context;
    private final ArrayList<ProductData> list_NewProcuts;
    private final LayoutInflater inflater;
    private final SessionManager sessionmanager;
    private final String layoutType;
    private final ArrayList<String> list_wishList;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = NewProductsRecyclerViewAdapter.class.getSimpleName();

    String image_url;

    public NewProductsRecyclerViewAdapter(Context context, ArrayList<ProductData> listNewProduct, String layoutType, ArrayList<String> list_wishList) {
        this._context = context;
        this.list_NewProcuts = listNewProduct;
        inflater = LayoutInflater.from(context);
        this.list_wishList = list_wishList;

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();
        this.layoutType = layoutType;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final RatingBar ratingBar;
        private final TextView txtPrice, txtName, txtWishList, txtProductMRP, txtOffer;
        private final ImageView imgItem;
        private final CardView crdProduct;
        private final TextView txtStockStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            txtName = (TextView) itemView.findViewById(R.id.txtItemName);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtProductMRP = (TextView) itemView.findViewById(R.id.txtProductMRP);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            txtWishList = (TextView) itemView.findViewById(R.id.txtWishLiast);
            txtStockStatus = (TextView) itemView.findViewById(R.id.txtStockStatus);

            txtOffer = (TextView) itemView.findViewById(R.id.txtOffer);
          //  imgItem.setScaleType(ImageView.ScaleType.FIT_XY);

            if (userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("dashboard") || userDetails.get(SessionManager.KEY_CATEGORY_TYPE).equals("category")) {
                // sets width to wrap content and height to 10 dp ->
                crdProduct.setLayoutParams(new CardView.LayoutParams(
                        CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.WRAP_CONTENT));
                crdProduct.setContentPadding(4, 4, 4, 4);
                crdProduct.setCardElevation(8);
                //  imgItem.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.FILL_PARENT ,CardView.LayoutParams.FILL_PARENT ));
            } else {
                crdProduct.setCardElevation(4);
            }


        }
    }

    @Override
    public NewProductsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (layoutType.equals("list")) {
            v = inflater.inflate(R.layout.row_single_product_listview, parent, false);
        } else {
            v = inflater.inflate(R.layout.row_single_product_gridview, parent, false);

        }
        MyViewHolder viewFolder = new MyViewHolder(v);
        return viewFolder;
    }

    @Override
    public void onBindViewHolder(final NewProductsRecyclerViewAdapter.MyViewHolder holder, final int position) {

        final ProductData pd = list_NewProcuts.get(position);

        holder.txtName.setTypeface(CustomFonts.typefaceCondensed(_context));
        holder.txtPrice.setTypeface(CustomFonts.typefaceCondensed(_context));
        holder.txtProductMRP.setTypeface(CustomFonts.typefaceCondensed(_context));
        holder.txtOffer.setTypeface(CustomFonts.typefaceCondensed(_context));


        holder.txtName.setText(pd.getProductname());
        //holder.txtPrice.setText(Html.fromHtml("<b>₹" + pd.getPrice() + "</b>" + "\n ₹<del>" + pd.getMrp() + "</del>"));
        holder.txtPrice.setText("\u20b9" + pd.getPrice());
        holder.txtProductMRP.setText("\u20b9" + pd.getMrp());


        Double offer = Double.parseDouble(pd.getPrice()) * 100 / Double.parseDouble(pd.getMrp());
        int percentage = 100 - offer.intValue();
        if (percentage == 0 || percentage < 0) {
            holder.txtOffer.setVisibility(View.GONE);
        } else {
            //  holder.txtOffer.setVisibility(View.GONE);
            holder.txtOffer.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.fadein));

            if (percentage <= 9) {
                holder.txtOffer.setText(" " + percentage + "% off");
                //holder.imgItem.setLabelText(" " + percentage + "% off");
            } else {
                holder.txtOffer.setText("" + percentage + "% off");
               // holder.imgItem.setLabelText("" + percentage + "% off");
            }
        }


        final int IsExistInWishList = list_wishList.indexOf(pd.getProductid());
        if (IsExistInWishList == -1) {
            holder.txtWishList.setBackgroundResource(R.drawable.ic_wishlist_default);
        } else {
            holder.txtWishList.setBackgroundResource(R.drawable.ic_wishlist_seleceted2);

        }

        //Check Product availability
        if (pd.getInventory().equals("0")) {
            //SetVisible out of stock
            holder.txtStockStatus.setVisibility(View.VISIBLE);
            holder.txtStockStatus.setText("Out Of Stock");

        } else {
            //SetInVisible out of stock
            holder.txtStockStatus.setVisibility(View.GONE);
        }


        holder.txtWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(userDetails.get(SessionManager.KEY_USERID).equals("0"))
                {
                    sessionmanager.setNewUserSession("ItemDisplayActivity");
                    Intent intent = new Intent(_context , LoginActivity.class);
                    _context.startActivity(intent);




                }
                else
                {
                    holder.txtWishList.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.clockwise_refresh));



                    dbhandler db = new dbhandler(_context);
                    SQLiteDatabase sd = db.getReadableDatabase();
                    sd = db.getWritableDatabase();

                    if (IsExistInWishList == -1) {
                        sendWishListDetailsToServer(db, pd.getProductid(), pd.getPrice(), "add", holder, pd,position);
                    } else {
                        sendWishListDetailsToServer(db, pd.getProductid(), pd.getPrice(), "remove", holder, pd,position);
                    }
                }





            }
        });


        try {

            if(layoutType.equals("grid") || layoutType.equals("list"))
            {

                /*Glide.with(_context).load(pd.getImage_url()).placeholder(R.drawable.loader_yellow_original_150).error(R.drawable.loader_yellow_original_500).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.imgItem);


            }
            else
            {*/

                 image_url =pd.getImage_url();
                if (image_url.contains("images") && image_url.contains("__w-200"))
                {
                    int pos = image_url.lastIndexOf("/");
                    //pos = image_url.inde


                                   /* if (image_url.contains("600")) {
                                        image_url = image_url.substring(pos, image_url.length());
                                        image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                                    }
                                    else*/
                    if (image_url.contains("600")) {

                        image_url = image_url.substring(pos, image_url.length());
                        image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                    }
                    else if (image_url.contains("400")) {

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

                Glide.with(_context).load(image_url).placeholder(R.drawable.loader_yellow_original_150).error(R.drawable.loader_yellow_original_500).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade(R.anim.fadein, 2000).into(holder.imgItem);


            }
            Log.d(TAG, "Final Image Path : " + pd.getImage_url());
            //.placeholder(R.mipmap.ic_launcher)






            // Picasso.with(_context).load(pd.getImage_url()).resize(1000, 400).centerInside().into(holder.imgItem);
            //  Picasso.with(_context).load(pd.getImage_url()).fit().into(holder.imgItem);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_context, "Error in Image Loading  : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        holder.imgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    //Toast.makeText(_context, "Name : " + pd.getProductname() + " Id : " + pd.getProductid(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Clicked On Name : " + pd.getProductname() + " Id : " + pd.getProductid());
                    Intent intent = new Intent(_context,
                            SingleItemActivity.class);
                    sessionmanager.setProductDetails(pd.getProductid(), String.valueOf(IsExistInWishList),userDetails.get(SessionManager.KEY_PRODUCT_DESCR), image_url, userDetails.get(SessionManager.KEY_PRODUCT_RATING));

                    //intent.putExtra("ProductId", pd.getProductid());
                    intent.putExtra("ActivityName", "ItemDisplayActivity");
                    sessionmanager.setActivityName("ItemDisplayActivity");
                    _context.startActivity(intent);
                    //finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return list_NewProcuts.size();
    }

    private void sendWishListDetailsToServer(final dbhandler db, final String ProductId, final String ProductPrice, final String type, final NewProductsRecyclerViewAdapter.MyViewHolder holder, final ProductData pd,final int pos) {


        String url_addtowishlist = AllKeys.WEBSITE + "manageWishlist/" + type + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId + "/" + db.convertEncodedString(db.getDateTime()) + "/" + db.convertEncodedString(db.getDateTime()) + "/" + ProductPrice + "";


        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
        StringRequest str_addToWishList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response ManageWishList : " + response);

                if (response.equals("1")) {


                    if (type.equals("add")) {
                        Log.d(TAG, "Wishlist Data : Add " + list_wishList.toString());
                        list_wishList.add(ProductId);
                        holder.txtWishList.setBackgroundResource(R.drawable.ic_wishlist_seleceted2);


                        // pd.setSelectionStatus(true);
                        //  int indexof_item = list_NewProcuts.indexOf(pd);
                        //  list_NewProcuts.set(indexof_item,pd);




                        Toast.makeText(_context, "Added to your wishlist", Toast.LENGTH_SHORT).show();
                    } else {

                        //pd.setSelectionStatus(false);
                        // int indexof_item = list_NewProcuts.indexOf(pd);
                        // list_NewProcuts.set(indexof_item,pd);


                        Log.d(TAG, "Wishlist Data : Remove " + list_wishList.toString());
                        final int index = list_wishList.indexOf(ProductId);
                        list_wishList.remove(index);
                        holder.txtWishList.setBackgroundResource(R.drawable.ic_wishlist_default);
                        Toast.makeText(_context, "Removed from your wishlist", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(_context, "Sorry,try again...", Toast.LENGTH_SHORT).show();
                }

                db.close();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null) {

                }
                sendWishListDetailsToServer(db, ProductId, ProductPrice, type, holder, pd,pos);
                Log.d(TAG, "Error in ManageWishList : " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_addToWishList);

    }
}
