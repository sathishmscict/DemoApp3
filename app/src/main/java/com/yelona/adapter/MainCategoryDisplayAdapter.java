package com.yelona.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yelona.R;
import com.yelona.SubCategories;
import com.yelona.fragments.DescriptionFragment;
import com.yelona.helper.AllKeys;
import com.yelona.pojo.MainCategory;

import java.util.ArrayList;

/**
 * Created by PC2 on 18-Mar-17.
 */

public class MainCategoryDisplayAdapter extends RecyclerView.Adapter<MainCategoryDisplayAdapter.MyViewHolder> {


    private final Context _context;
    private final ArrayList<MainCategory> listMainCategoryData;
    private String TAG = MainCategoryDisplayAdapter.class.getSimpleName();

    public MainCategoryDisplayAdapter(Context context, ArrayList<MainCategory> listMainCategory) {
        this._context = context;
        this.listMainCategoryData = listMainCategory;


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgCategory;
        private final TextView txtCategoryName;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgCategory = (ImageView)itemView.findViewById(R.id.imgCategory);
            txtCategoryName =(TextView)itemView.findViewById(R.id.txtCategoryName);

        }
    }

    @Override
    public MainCategoryDisplayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(_context).inflate(R.layout.row_single_main_category, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MainCategoryDisplayAdapter.MyViewHolder holder, int position) {


        holder.txtCategoryName.setText(listMainCategoryData.get(position).getCategoryName());
        String image_url =listMainCategoryData.get(position).getImageUrl();

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

        holder.imgCategory.setImageDrawable(listMainCategoryData.get(position).getImg());
       // Glide.with(_context).load(image_url).placeholder(R.drawable.loader_yellow_original_150).error(R.drawable.loader_yellow_original_500).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade(R.anim.fadein, 2000).into(holder.imgCategory);





    }

    @Override
    public int getItemCount() {
        return listMainCategoryData.size();
    }
}
