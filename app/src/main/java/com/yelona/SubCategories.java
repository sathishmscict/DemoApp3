package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yelona.database.dbhandler;
import com.yelona.expandableData.BaseItem;
import com.yelona.expandableData.GroupItem;
import com.yelona.expandableData.Item;
import com.yelona.helper.CustomFonts;
import com.yelona.session.SessionManager;
import com.yelona.views.LevelBeamView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.OnItemClickListener;

public class SubCategories extends AppCompatActivity {

    private MultiLevelListView multiLevelListView;
    private Context context = this;
    private dbhandler db;
    private SQLiteDatabase sd;

    /*Expandable ListView*/
    private static final int MAX_LEVELS = 3;

    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;

    private ArrayList<String> firstLevelCategoryName = new ArrayList<String>();
    private ArrayList<Integer> firstLEvelCategoryId = new ArrayList<Integer>();

    private ArrayList<String> secondLevelCategoryName = new ArrayList<String>();
    private ArrayList<Integer> secondLEvelCategoryId = new ArrayList<Integer>();

    private ArrayList<String> thirdtLevelCategoryName = new ArrayList<String>();
    private ArrayList<Integer> thirdLEvelCategoryId = new ArrayList<Integer>();

    private String TAG = SubCategories.class.getSimpleName();
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories);
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


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();
        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        setTitle(userDetails.get(SessionManager.KEY_CATEGORY_NAME));


        confMenu();

    }

    private void confMenu() {
        multiLevelListView = (MultiLevelListView) findViewById(R.id.multiLevelMenu);

        // custom ListAdapter
        ListAdapter listAdapter = new ListAdapter();

        multiLevelListView.setAdapter(listAdapter);
        multiLevelListView.setOnItemClickListener(mOnItemClickListener);

        List<BaseItem> rootMenu = new ArrayList<>();

        //Set Super Category Data

        String query_categories = "select * from Categorymaster where parentid=" + userDetails.get(SessionManager.KEY_CATEGORY_ID) + " order by "+ dbhandler.CATEGORY_SEQUENCE_NO +" asc";
        Log.d(TAG, "Query Select subcategories" + query_categories);
        Cursor cc = sd.rawQuery(query_categories, null);
        if (cc.getCount() > 0) {
            rootMenu.clear();
            firstLEvelCategoryId.clear();
            firstLevelCategoryName.clear();
            while (cc.moveToNext()) {
                int getCategoryId = cc.getInt(cc.getColumnIndex(dbhandler.CATEGORY_ID));

                String upperString = cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME));
                upperString = upperString.substring(0, 1).toUpperCase() + upperString.substring(1);

                firstLevelCategoryName.add(upperString);
                firstLEvelCategoryId.add(getCategoryId);

                //Check SubCategory Exist or Not..If exist then add as GroupItem()
                String query_supercategory = "select * from Categorymaster where parentid=" + getCategoryId +" order by "+ dbhandler.CATEGORY_SEQUENCE_NO +" asc";
                Cursor cursor_supercategory = sd.rawQuery(query_supercategory, null);
                if (cursor_supercategory.getCount() <= 0) {

                    //rootMenu.add(new Item(cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME))));
                    rootMenu.add(new Item(upperString));

                } else {
                    //rootMenu.add(new GroupItem(cc.getString(cc.getColumnIndex(dbhandler.CATEGORY_NAME))));
                    rootMenu.add(new GroupItem(upperString));
                }

            }

        }

        listAdapter.setDataItems(rootMenu);

        //Complete Super Category Data
    }


    /**
     * Hanlde ItemClickLitener of Expandable ListView
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        private void showItemDescription(Object object, ItemInfo itemInfo) {
            StringBuilder builder = new StringBuilder("\"");
            builder.append(((BaseItem) object).getName());
            builder.append("\" clicked!\n");

            //StringBuilder builder = new StringBuilder();

            builder.append(String.format("level[%d], idx in level[%d/%d]",
                    itemInfo.getLevel() + 1, /*Indexing starts from 0*/
                    itemInfo.getIdxInLevel() + 1 /*Indexing starts from 0*/,
                    itemInfo.getLevelSize()));

            if (itemInfo.isExpandable()) {
                builder.append(String.format(", expanded[%b]", itemInfo.isExpanded()));
            } else {
                // Toast.makeText(NewDashBoardActivity.this, ((BaseItem) object).getName(), Toast.LENGTH_SHORT).show();

                try {
                    if ((itemInfo.getLevel() + 1) == 2)
                    {
                       SetCategoryDetails("category", String.valueOf(secondLEvelCategoryId.get(secondLevelCategoryName.indexOf(((BaseItem) object).getName()))), ((BaseItem) object).getName());
                   } else if ((itemInfo.getLevel() + 1) == 3) {
                       SetCategoryDetails("category", String.valueOf(thirdLEvelCategoryId.get(thirdtLevelCategoryName.indexOf(((BaseItem) object).getName()))), ((BaseItem) object).getName());
                   }
                   else
                   {
                       SetCategoryDetails("category", String.valueOf(firstLEvelCategoryId.get(firstLevelCategoryName.indexOf(((BaseItem) object).getName()))), ((BaseItem) object).getName());
                   }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            // builder.append(getItemInfoDsc(itemInfo));
//"Beauty" clicked!level[2], idx in level[1/9], expanded[true]

            Log.d(TAG, "Clicked Item Info : " + builder.toString());

        }

        @Override
        public void onItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }

        @Override
        public void onGroupItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }
    };
    //Compelte Handle onItem click Litener

    /**
     * Handle Custom ListAdpater
     */
    private class ListAdapter extends MultiLevelListAdapter {

        private class ViewHolder {
            TextView nameView;
            TextView infoView;
            ImageView arrowView;
            LevelBeamView levelBeamView;
        }

        @Override
        public List<?> getSubObjects(Object object) {
            // DIEKSEKUSI SAAT KLIK PADA GROUP-ITEM

            return getSubItems((BaseItem) object);
        }

        @Override
        public boolean isExpandable(Object object) {


            return ((BaseItem) object) instanceof GroupItem;
        }

        @Override
        public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
            ListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ListAdapter.ViewHolder();
                convertView = LayoutInflater.from(SubCategories.this).inflate(R.layout.data_item, null);
                //viewHolder.infoView = (TextView) convertView.findViewById(R.id.dataItemInfo);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.dataItemName);
                viewHolder.arrowView = (ImageView) convertView.findViewById(R.id.dataItemArrow);
                viewHolder.levelBeamView = (LevelBeamView) convertView.findViewById(R.id.dataItemLevelBeam);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SubCategories.ListAdapter.ViewHolder) convertView.getTag();
            }


            //String upperString = ((BaseItem) object).getName();
            //upperString = upperString.substring(0,1).toUpperCase() + upperString.substring(1);


            //viewHolder.nameView.setText(upperString);

            viewHolder.nameView.setText(((BaseItem) object).getName());

            if (itemInfo.getLevel() == 0) {

                viewHolder.nameView.setTypeface(CustomFonts.typefaceCondensed(context));
            } else if (itemInfo.getLevel() == 1) {
                viewHolder.nameView.setTypeface(CustomFonts.typefaceCondensed(context));
            } else {
                viewHolder.nameView.setTypeface(CustomFonts.typefaceCondensed(context));

            }


            //viewHolder.infoView.setText(getItemInfoDsc(itemInfo));

            if (itemInfo.isExpandable()) {
                viewHolder.arrowView.setVisibility(View.VISIBLE);
                viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ?
                        R.drawable.ic_expand_less_minus_circle : R.drawable.ic_expand_more_plus_circle);
                        /*R.drawable.ic_expand_less : R.drawable.ic_expand_more);*/
            } else {
                viewHolder.arrowView.setVisibility(View.GONE);
            }

            viewHolder.levelBeamView.setLevel(itemInfo.getLevel());

            return convertView;
        }
    }
    /**
     * Complete ListAdapter
     */


    /**
     * Fill Data on Expandalbe listview based on category click
     *
     * @param baseItem
     * @return
     */

    public List<BaseItem> getSubItems(BaseItem baseItem) {

        List<BaseItem> result = new ArrayList<>();
        int level = ((GroupItem) baseItem).getLevel() + 1;

        String menuItem = baseItem.getName();

        if (!(baseItem instanceof GroupItem)) {
            throw new IllegalArgumentException("GroupItem required");
        }

        GroupItem groupItem1 = (GroupItem) baseItem;
        if (groupItem1.getLevel() >= MAX_LEVELS) {
            return null;
        }


        switch (level) {
            case LEVEL_1:

                String query = "select * from Categorymaster where parentid=" + firstLEvelCategoryId.get(firstLevelCategoryName.indexOf(menuItem)) +" order by "+ dbhandler.CATEGORY_SEQUENCE_NO +" asc";
                Cursor cursor_category = sd.rawQuery(query, null);
                if (cursor_category.getCount() > 0) {
                    List<BaseItem> list = new ArrayList<>();
                    //secondLEvelCategoryId.clear();
                   //secondLevelCategoryName.clear();

                    while (cursor_category.moveToNext()) {

                        secondLevelCategoryName.add(cursor_category.getString(cursor_category.getColumnIndex(dbhandler.CATEGORY_NAME)));
                        secondLEvelCategoryId.add(cursor_category.getInt(cursor_category.getColumnIndex(dbhandler.CATEGORY_ID)));


                        // Setiap membuat groupItem harus di set levelnya
                        // GroupItem groupItem = new GroupItem("GROUP 1");
                        //  groupItem.setLevel(groupItem.getLevel() + 1);

                        // list.add(new Item("ITEM 1"));
                        //list.add(new Item("ITEM 2"));
                        //list.add(groupItem);


                        //Set Category and SubCategort Related DAta
                        String query_categories = "select * from Categorymaster where parentid=" + cursor_category.getInt(cursor_category.getColumnIndex(dbhandler.CATEGORY_ID)) +" order by "+ dbhandler.CATEGORY_SEQUENCE_NO +" asc";
                        Cursor cc = sd.rawQuery(query_categories, null);
                        if (cc.getCount() <= 0) {

                            list.add(new Item(cursor_category.getString(cursor_category.getColumnIndex(dbhandler.CATEGORY_NAME))));
                        } else {

                            GroupItem groupItemCategory = new GroupItem(cursor_category.getString(cursor_category.getColumnIndex(dbhandler.CATEGORY_NAME)));
                            groupItemCategory.setLevel(groupItemCategory.getLevel() + 1);
                            list.add(groupItemCategory);


                        }


                        //Complete Set CAtegory and subcategory data


                    }
                    result = list;

                }

                break;

            case LEVEL_2:
                String query_subcategory = "select * from Categorymaster where parentid=" + secondLEvelCategoryId.get(secondLevelCategoryName.indexOf(menuItem)) + "";
                Cursor cursor_subcategory = sd.rawQuery(query_subcategory, null);
                if (cursor_subcategory.getCount() > 0) {
                    List<BaseItem> list = new ArrayList<>();
                    thirdLEvelCategoryId.clear();
                    thirdtLevelCategoryName.clear();

                    while (cursor_subcategory.moveToNext()) {

                        thirdtLevelCategoryName.add(cursor_subcategory.getString(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_NAME)));
                        thirdLEvelCategoryId.add(cursor_subcategory.getInt(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_ID)));


                        // Setiap membuat groupItem harus di set levelnya
                        GroupItem groupItem = new GroupItem("GROUP 1");
                        groupItem.setLevel(groupItem.getLevel() + 1);

                        // list.add(new Item("ITEM 1"));
                        //list.add(new Item("ITEM 2"));
                        //list.add(groupItem);


                        //Set Category and SubCategort Related DAta
                        String query_categories = "select * from Categorymaster where parentid=" + cursor_subcategory.getInt(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_ID)) + " order by "+ dbhandler.CATEGORY_SEQUENCE_NO +" asc";
                        Cursor cc = sd.rawQuery(query_categories, null);
                        if (cc.getCount() <= 0) {

                            list.add(new Item(cursor_subcategory.getString(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_NAME))));
                        } else {

                            GroupItem groupItemCategory = new GroupItem(cursor_subcategory.getString(cursor_subcategory.getColumnIndex(dbhandler.CATEGORY_NAME)));
                            groupItemCategory.setLevel(groupItemCategory.getLevel() + 1);
                            list.add(groupItemCategory);


                        }


                        //Complete Set CAtegory and subcategory data


                    }
                    result = list;

                }

                break;
        }

        return result;
    }
    //Complet Fill DAta On Category Or Subcategory


    private void SetCategoryDetails(String categoryType, String categoryId, String categoryName) {

        Log.d(TAG, "Item Click Data : CategoryType : " + categoryType + " CategoryId : " + categoryId + " CategoryName : " + categoryName);

        // sessionmanager.setCategoryTypeAndIdDetails("dashboard",String.valueOf(list_bannerTypeId.get(list_bannerTypeName.indexOf("Hot Deals"))),"Hot Deals");
        sessionmanager.setCategoryTypeAndIdDetails(categoryType, categoryId, categoryName);

        Intent intent = new Intent(context, ItemDisplayActivity.class);
       startActivity(intent);
        finish();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            try {
                Intent i = new Intent(context, NewDashBoardActivity.class);
                //i.putExtra("ActivityName",ACTIVITYNAME);
                startActivity(i);
                finish();
            } catch (Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error in Converting Class : " + e.getMessage());
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = null;
        try {
            i = new Intent(context, NewDashBoardActivity.class);
            //i.putExtra("ActivityName",ACTIVITYNAME);
            startActivity(i);
            finish();
        } catch (Exception e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error in Converting Class : " + e.getMessage());
            e.printStackTrace();
        }


    }
}
