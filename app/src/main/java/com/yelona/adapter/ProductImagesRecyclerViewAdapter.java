package com.yelona.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.yelona.R;
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

public class ProductImagesRecyclerViewAdapter extends RecyclerView.Adapter<ProductImagesRecyclerViewAdapter.MyViewHolder> {


    private final Context _context;
    private final ArrayList<ProductData> list_NewProcuts;
    private final LayoutInflater inflater;
    private final SessionManager sessionmanager;


    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = ProductImagesRecyclerViewAdapter.class.getSimpleName();


 // Start with first item selected
  //  private int focusedItem = 0;
    private int selectedItem=0;

    public ProductImagesRecyclerViewAdapter(Context context, ArrayList<ProductData> listNewProduct) {
        this._context = context;
        this.list_NewProcuts = listNewProduct;
        inflater = LayoutInflater.from(context);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();


    }
   /*    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Handle key up and key down and attempt to move selection
        recyclerView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

                // Return false if scrolled to the bounds and allow focus to move off the list
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return tryMoveSelection(lm, 1);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return tryMoveSelection(lm, -1);
                    }
                }

                return false;
            }
        });
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
        int tryFocusItem = focusedItem + direction;

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
            notifyItemChanged(focusedItem);
            focusedItem = tryFocusItem;
            notifyItemChanged(focusedItem);
            lm.scrollToPosition(focusedItem);
            return true;
        }

        return false;
    }*/


    public class MyViewHolder extends RecyclerView.ViewHolder  {

       // private final RatingBar ratingBar;
      //  private final TextView txtPrice, txtName, txtDelete,txtProductMRP;
        private final ImageView imgItem;
      //  private final CardView crdProduct;
    //  private final LinearLayout myBackground;

        public MyViewHolder(View itemView)
        {
            super(itemView);
          //  crdProduct = (CardView) itemView.findViewById(R.id.crdProduct);
            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            //myBackground=(LinearLayout)itemView.findViewById(R.id.llimages);
           // txtName = (TextView) itemView.findViewById(R.id.txtItemName);
          //  txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
          //  ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
          //  txtDelete = (TextView) itemView.findViewById(R.id.txtDelete);
           // txtProductMRP = (TextView) itemView.findViewById(R.id.txtProductMRP);

            // Handle item click and set the selection
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redraw the old selection and the new
                    notifyItemChanged(focusedItem);
                    focusedItem = getLayoutPosition();
                    notifyItemChanged(focusedItem);
                }
            });*/

        }

    }

    @Override
    public ProductImagesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = inflater.inflate(R.layout.row_single_product_images, parent, false);

        MyViewHolder viewFolder = new MyViewHolder(v);
        return viewFolder;
    }

    @Override
    public void onBindViewHolder(final ProductImagesRecyclerViewAdapter.MyViewHolder holder, final int position) {

        final ProductData pd = list_NewProcuts.get(position);



        try {
            Glide.with(_context).load(pd.getImage_url()).crossFade().into(holder.imgItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  if(selectedItem == 0)
        {
        // Set selected state; use a state list drawable to style the view
        holder.itemView.setSelected(selectedItem == position);

        }*/


      /*  if(position == selectedPosition){
            holder.itemView.setSelected(true);
        } else {
           holder.itemView.setSelected(false);
        }*/
        // Actual selection / deselection logic
      /*  holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
               *//* int currentPosition = holder.getLayoutPosition();
                if(selectedPosition != currentPosition){
                    // Temporarily save the last selected position
                    int lastSelectedPosition = selectedPosition;
                    // Save the new selected position
                    selectedPosition = currentPosition;
                    // update the previous selected row
                    notifyItemChanged(lastSelectedPosition);
                    // select the clicked row
                    holder.itemView.setSelected(true);
                }
                else
                { notifyItemChanged(holder.getLayoutPosition());
                    holder.itemView.setSelected(false);
                }*//*
                //notifyItemChanged(selectedItem);

              //  selectedItem = mRecyclerView.getChildPosition(v);
                //selectedItem = holder.getLayoutPosition();

              //  notifyItemChanged(selectedItem);
                if(position != 0)
                {
              //  holder.itemView.setSelected(selectedItem == position);
                    holder.itemView.setSelected(true);
                    notifyItemChanged(position);


                }


            }
        });

*/

      /*  holder.myBackground.setOnTouchListener(new View.OnTouchListener()
        {
            @Override public boolean onTouch(View v, MotionEvent event) {



                if (event.getAction()==MotionEvent.ACTION_DOWN){

                    //holder.myBackground.setBackgroundColor(Color.parseColor("#f8f8f8"));
                    holder.myBackground.setSelected(true);

                }if (event.getAction()==MotionEvent.ACTION_UP){
                  //  holder.myBackground.setBackgroundColor(Color.WHITE);
                    holder.myBackground.setSelected(false);
                }
                notifyItemChanged(position);
               // notifyDataSetChanged();

                return false;
            }
        });*/


       /* holder.itemView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
               // notifyItemChanged(selectedItem);
                selectedItem = position;
                holder.itemView.setSelected(position == selectedItem);
               // notifyItemChanged(selectedItem);
            }
        });
        holder.itemView.setSelected(position == selectedItem);
*/

        if(pd.isSelectionStatus() == true)
        {
            holder.itemView.setSelected(true);
        }
        else
        {
            holder.itemView.setSelected(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
               // notifyItemChanged(position);
                notifyDataSetChanged();
                // TODO Auto-generated method stub
                if(pd.isSelectionStatus() == true)
                {
                    for(int i=0;i<list_NewProcuts.size();i++)
                    {
                        ProductData pdd =new ProductData(pd.getImage_url() ,false);
                        list_NewProcuts.set(position,pdd);
                    }
                    ProductData pdd =new ProductData(pd.getImage_url() ,false);
                    list_NewProcuts.set(position,pdd);
                    list_NewProcuts.set(0,pdd);
                    holder.itemView.setSelected(false);

                }
                else
                {
                    for(int i=0;i<list_NewProcuts.size();i++)
                    {
                        ProductData pdd =new ProductData(pd.getImage_url() ,false);
                        list_NewProcuts.set(position,pdd);
                    }
                    ProductData pdd =new ProductData(pd.getImage_url() ,true);
                    list_NewProcuts.set(position,pdd);
                    holder.itemView.setSelected(true);
                }


                // notifyItemChanged(selectedItem);
//                selectedItem = position;
  //              holder.itemView.setSelected(position == selectedItem);
                // notifyItemChanged(selectedItem);
            }
        });


    }



    @Override
    public int getItemCount() {
        return list_NewProcuts.size();
    }
}
