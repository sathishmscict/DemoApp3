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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.yelona.CheckoutActivity;
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

public class CartListRecyclerViewAdapter2 extends RecyclerView.Adapter<CartListRecyclerViewAdapter2.MyViewHolder> {


    private final Context _context;
    private final ArrayList<ProductData> list_NewProcuts;
    private final LayoutInflater inflater;
    private final SessionManager sessionmanager;

    String image_url;

    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = CartListRecyclerViewAdapter2.class.getSimpleName();

    public CartListRecyclerViewAdapter2(Context context, ArrayList<ProductData> listNewProduct) {
        this._context = context;
        this.list_NewProcuts = listNewProduct;
        inflater = LayoutInflater.from(context);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final RatingBar ratingBar;
        private final TextView txtPrice, txtName, txtDelete, txtProductMRP, txtOffer, txtAdd, txtRemove, txtQuantity, txtStockStatus, txtDeliveryCharge;
        private final ImageView imgItem;
        private final CardView crdProduct;
        private final Button btnWishlist, btnRemoveFromCart;

        public MyViewHolder(View itemView) {
            super(itemView);
            crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            txtName = (TextView) itemView.findViewById(R.id.txtItemName);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            txtDelete = (TextView) itemView.findViewById(R.id.txtDelete);
            txtProductMRP = (TextView) itemView.findViewById(R.id.txtProductMRP);
            txtDeliveryCharge = (TextView) itemView.findViewById(R.id.txtDeliveryCharge);

            txtOffer = (TextView) itemView.findViewById(R.id.txtOffer);
            btnWishlist = (Button) itemView.findViewById(R.id.btnWishlist);
            btnRemoveFromCart = (Button) itemView.findViewById(R.id.btnRemove);

            txtAdd = (TextView) itemView.findViewById(R.id.txtAdd);
            txtRemove = (TextView) itemView.findViewById(R.id.txtRemove);
            txtQuantity = (TextView) itemView.findViewById(R.id.txtCounter);
            txtStockStatus = (TextView) itemView.findViewById(R.id.txtStockStatus);



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
    public CartListRecyclerViewAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = inflater.inflate(R.layout.row_single_product_cartlist, parent, false);

        MyViewHolder viewFolder = new MyViewHolder(v);
        return viewFolder;
    }

    @Override
    public void onBindViewHolder(final CartListRecyclerViewAdapter2.MyViewHolder holder, final int position) {

        final ProductData pd = list_NewProcuts.get(position);

        holder.txtName.setText(pd.getProductname());
        // holder.txtPrice.setText(Html.fromHtml("<b>" + pd.getMrp() + "</b>" + " <del>" + pd.getPrice() + "</del>"));

        int qty_prod = Integer.parseInt(pd.getQuantity());
        final int unitprice = (int) (Double.parseDouble(pd.getPrice()) * qty_prod);
        final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) * qty_prod);

        holder.txtPrice.setText("\u20b9 " + unitprice);
        holder.txtProductMRP.setText("\u20b9 " + mrpprice);

        if (pd.getShippingcost().equals("0")) {
            holder.txtDeliveryCharge.setText("Free Delivery");
        } else {
            holder.txtDeliveryCharge.setText("Delivery Charge : " + pd.getShippingcost());
        }


        //   pd.setPrice(String.valueOf(unitprice));
        //  pd.setMrp(String.valueOf(mrpprice));

        //  list_NewProcuts.set(position,pd);


        //Check Product availability
        if (pd.getInventory().equals("0")) {
            //SetVisible out of stock
            holder.txtStockStatus.setVisibility(View.VISIBLE);
            holder.txtStockStatus.setText("Out Of Stock");

        } else {
            //SetInVisible out of stock
            holder.txtStockStatus.setVisibility(View.GONE);
        }

        holder.txtQuantity.setText(pd.getQuantity());


        holder.txtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    int qty_prod = Integer.parseInt(pd.getQuantity());

                    if (qty_prod > 1) {


                        /**
                         * Update Cart Details send to server
                         */
                        final int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty_prod);
                        final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty_prod);

                        final int qty = --qty_prod;
                        String url_addtowishlist = AllKeys.WEBSITE + "manageCart/update/" + userDetails.get(SessionManager.KEY_USERID) + "/" + pd.getProductid() + "/" + qty + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "";
                        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
                        final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d(TAG, "Response ManageWishList : " + response);

                                if (response.equals("1")) {


                                    //Toast.makeText(_context, "Cart details updated", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "cart details successfully updated");


                                    //--qty;

                                    holder.txtQuantity.setText(" " + qty + " ");
                                    pd.setQuantity(String.valueOf(qty));


                                    //Double price = Double.parseDouble(pd.getPrice()) * Double.parseDouble(String.valueOf(qty));
                                    //int unitprice = (int) (Double.parseDouble(pd.getPrice())/qty);

                                    int price = unitprice * qty;
                                    holder.txtPrice.setText("\u20b9 " + String.valueOf(price));
                                    //Double mrp = Double.parseDouble(pd.getMrp()) * Double.parseDouble(String.valueOf(qty));
                                    int mrp = mrpprice * qty;
                                    holder.txtProductMRP.setText("\u20b9 " + String.valueOf(mrp));


                                    pd.setPrice(String.valueOf(price));
                                    pd.setMrp(String.valueOf(mrp));
                                    pd.setQuantity(String.valueOf(qty));
                                    list_NewProcuts.set(position, pd);


                                    Toast.makeText(_context, "You've changed  '" + pd.getProductname() + "' quantity to '" + qty + "'", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                                Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                            }
                        });
                        MyApplication.getInstance().addToRequestQueue(str_addToCartList);


                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });

        holder.txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int qty_prod = Integer.parseInt(pd.getQuantity());
                    int max_quantity = 1;
                    if (qty_prod >= 1 && qty_prod < Integer.parseInt(pd.getInventory())) {

                        /*int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty);
                        int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty);

                        ++qty;
                        holder.txtQuantity.setText(" " + qty + " ");
                                               int price = unitprice * qty;
                        holder.txtPrice.setText("\u20b9 " + String.valueOf(price));
                                               int mrp = mrpprice * qty;
                        holder.txtProductMRP.setText("\u20b9 " + String.valueOf(mrp));


                        pd.setPrice(String.valueOf(price));
                        pd.setMrp(String.valueOf(mrp));
                        pd.setQuantity(String.valueOf(qty));
                        list_NewProcuts.set(position, pd);*/

                        /**
                         * Update Cart Details send to server
                         */
                        final int unitprice = (int) (Double.parseDouble(pd.getPrice()) / qty_prod);
                        final int mrpprice = (int) (Double.parseDouble(pd.getMrp()) / qty_prod);

                        final int qty = ++qty_prod;
                        String url_addtowishlist = AllKeys.WEBSITE + "manageCart/update/" + userDetails.get(SessionManager.KEY_USERID) + "/" + pd.getProductid() + "/" + qty + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "";
                        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
                        final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d(TAG, "Response ManageWishList : " + response);

                                if (response.equals("1")) {


                                    // Toast.makeText(_context, "Cart details updated", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "cart details successfully updated");

                                    holder.txtQuantity.setText(" " + qty + " ");
                                    int price = unitprice * qty;
                                    holder.txtPrice.setText("\u20b9 " + String.valueOf(price));
                                    int mrp = mrpprice * qty;
                                    holder.txtProductMRP.setText("\u20b9 " + String.valueOf(mrp));


                                    pd.setPrice(String.valueOf(price));
                                    pd.setMrp(String.valueOf(mrp));
                                    pd.setQuantity(String.valueOf(qty));
                                    list_NewProcuts.set(position, pd);

                                    Toast.makeText(_context, "You've changed  '" + pd.getProductname() + "' quantity to '" + qty + "'", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                                Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
                            }
                        });
                        MyApplication.getInstance().addToRequestQueue(str_addToCartList);

                    } else {

                        //Toast.makeText(_context, "Sorry, you cannot cart more than " + pd.getInventory() + " quantity ", Toast.LENGTH_SHORT).show();

                        Toast.makeText(_context, "We're sorry! Only " + pd.getInventory() + " units of " + pd.getProductname() + " for each customer.", Toast.LENGTH_LONG).show();
                        //Snackbar.make(coordinatorLayout,""+getString(R.string.maxquantity),Snackbar.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });


        holder.btnRemoveFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbhandler db = new dbhandler(_context);
                SQLiteDatabase sd = db.getReadableDatabase();
                sd = db.getWritableDatabase();


                holder.btnRemoveFromCart.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.anim_item));

                sendCartDetailsToServer(pd.getProductid(), "remove", position);


            }
        });
        holder.btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //holder.btnRemove.performClick();

                holder.btnWishlist.startAnimation(AnimationUtils.loadAnimation(_context, R.anim.anim_item));
                dbhandler db = new dbhandler(_context);


                sendCartToWishlistDetailsToServcer(pd.getProductid(), dbhandler.convertEncodedString(db.getDateTime()), pd.getPrice());


            }
        });


        try {

            image_url = pd.getImage_url();
            if (image_url.contains("images") && image_url.contains("__w-200")) {
                int pos = image_url.lastIndexOf("/");
                //pos = image_url.inde

                                    /*if (image_url.contains("600")) {
                                        image_url = image_url.substring(pos, image_url.length());
                                        image_url = AllKeys.RESOURSES + "uploads/images/w600" + image_url;
                                    }
                                     else*/
                if (image_url.contains("400")) {

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

        Double offer = Double.parseDouble(pd.getPrice()) * 100 / Double.parseDouble(pd.getMrp());
        int percentage = 100 - offer.intValue();
        if (percentage == 0 || percentage < 0) {
            holder.txtOffer.setVisibility(View.GONE);
        } else {
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
                    sessionmanager.setProductDetails(pd.getProductid(), "1", userDetails.get(SessionManager.KEY_PRODUCT_DESCR), image_url, userDetails.get(SessionManager.KEY_PRODUCT_RATING));


                    //intent.putExtra("ProductId", pd.getProductid());
                    intent.putExtra("ActivityName", "CheckoutActivity");
                    _context.startActivity(intent);
                    //finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void sendCartToWishlistDetailsToServcer(final String ProductId, final String dateTime, final String ProductPrice) {

        String url_movecarttowishlist = AllKeys.WEBSITE + "moveCartToWishList/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId + "/" + dateTime + "/" + ProductPrice + "";

        Log.d(TAG, "URL  MoveCartToWishList " + url_movecarttowishlist);

        StringRequest str_moveCartToWishList = new StringRequest(Request.Method.GET, url_movecarttowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("q")) {
                    Toast.makeText(_context, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error in moveCartToWishList : " + error.getMessage());

                sendCartToWishlistDetailsToServcer(ProductId, dateTime, ProductPrice);
            }
        });

        MyApplication.getInstance().addToRequestQueue(str_moveCartToWishList);


    }


    /**
     * Remove item details from cart details
     *
     * @param ProductId
     * @param type
     * @param position
     */
    private void sendCartDetailsToServer(final String ProductId, final String type, final int position) {


        String url_addtowishlist = AllKeys.WEBSITE + "manageCart/" + type + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + ProductId;

        Log.d(TAG, "URL AddToWishList : " + url_addtowishlist);
        final StringRequest str_addToCartList = new StringRequest(Request.Method.GET, url_addtowishlist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response ManageWishList : " + response);

                if (response.equals("1")) {


                    if (type.equals("add")) {
                        Toast.makeText(_context, "Added to your wishlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(_context, "Item deleted from  wishlist", Toast.LENGTH_SHORT).show();
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                        list_NewProcuts.remove(position);
                        if (list_NewProcuts.size() == 0) {
                            Intent intent = new Intent(_context, CheckoutActivity.class);
                            _context.startActivity(intent);
                            sessionmanager.setCartItemsIdDetails("");
                        }

                    }


                } else {
                    Toast.makeText(_context, "Try again...", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null) {
                    sendCartDetailsToServer(ProductId, type, position);

                }
                Log.d(TAG, "Error in ManageCartList : " + error.getMessage());
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_addToCartList);

    }

    @Override
    public int getItemCount() {
        return list_NewProcuts.size();
    }
}
