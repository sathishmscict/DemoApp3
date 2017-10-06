package com.yelona.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.yelona.R;
import com.yelona.helper.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class dbhandler extends SQLiteOpenHelper {


    public static final String databasename = "Yelona.db";
    public static final int dbversion = 1;
    private final Context context;

    String query = "";


    //Category Related Table
    public static final String TABLE_CATEGORY = "CategoryMaster";
    public static final String CATEGORY_ID = "Id";
    public static final String CATEGORY_NAME = "Name";
    public static final String CATEGORY_PARENTID = "ParentId";
    public static final String CATEGORY_TYPE = "Type";
    public static final String CATEGORY_DELETED_AT = "DeletedAt";
    public static final String CATEGORY_CREATED_AT = "CreatedAt";
    public static final String CATEGORY_UPDATED_AT = "UpdatedAt";
    public static final String CATEGORY_MLM_DISCOUNT = "MLMDiscount";
    public static final String CATEGORY_SEQUENCE_NO = "SequenceNo";
    public static final String CATEGORY_SHIPPING_COST = "ShippingCost";
    public static final String CATEGORY_SHIPING_COST_SELLER = "ShippingCostSeller";
    public static final String CATEGORY_SHIPPING_COST_BUYER = "ShippingCosBuyer";


    // OrderItem Related Keys

    public static final String TABLE_ORDER = "OrderMst";
    public static final String ORDER_ID = "Id";
    public static final String ORDER_SELLERID = "seller_id";
    public static final String ORDER_PRODUCTID = "product_id";
    public static final String ORDER_QUANTITY = "qty";
    public static final String ORDER_UNIT_PRICE = "unit_price";
    public static final String ORDER_SHIPPING_CHARGE = "shipping_charge";
    public static final String ORDER_PROMO_VALUE = "promo_value";
    public static final String ORDER_PAYABLE_USER = "payable_user";
    public static final String ORDER_PAYABLE_SELLER = "payable_seller";
    public static final String ORDER_STATUS = "status";
    public static final String ORDER_PAYMENT_TYPE = "payment_type";
    public static final String ORDER_IS_REVIEWED = "is_reviewed";
    public static final String ORDER_IS_PAY_SELLER = "is_pay_seller";
    public static final String ORDER_IS_PAY_USER = "is_pay_user";
    public static final String ORDER_CREATED_AT = "created_at";
    public static final String ORDER_IS_MLM = "is_mlm";
    public static final String ORDER_MLM_TRANSACTION_ID = "mlm_transaction_id";
    public static final String ORDER_MLM_VALUE = "mlm_value";
    public static final String ORDER_ORDER_ID = "order_id";
    public static final String ORDER_SINLGE_ORDER_ID = "single_order_id";
    public static final String ORDER_SHIPPING_ADDRESS_ID = "shipping_address_id";


    // ClientMAster Related Keys
    public static final String TABLE_CLIENTMASTER = "ClientMst";
    public static final String CLIENTMASTER_CLIENTID = "ClientId";
    public static final String CLIENTMASTER_CLIENT_NAME = "ClientName";
    public static final String CLIENTMASTER_CLIENT_MOBILE = "Mobile";
    public static final String CLIENTMASTER_CLIENT_ADDRESS = "Address";
    public static final String CLIENTMASTER_CLIENT_EMAIL = "Email";
    public static final String CLIENTMASTER_COMPANYNAME = "CompanyName";


    private static String TAG = dbhandler.class.getSimpleName();


    public dbhandler(Context context) {
        super(context, databasename, null, dbversion);
        this.context = context;

        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        try {

            db.execSQL("create table IF NOT EXISTS Notification_Mst(id INTEGER PRIMARY KEY AUTOINCREMENT,header TEXT,notification TEXT,ndate text)");


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER + "("
                    + ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ORDER_SELLERID + " TEXT,"
                    + ORDER_PRODUCTID + " TEXT," + ORDER_QUANTITY + " TEXT,"
                    + ORDER_UNIT_PRICE + " TEXT," + ORDER_SHIPPING_CHARGE + " TEXT," +
                    "" + ORDER_PROMO_VALUE + " TEXT," + ORDER_PAYABLE_USER + " TEXT," + ORDER_PAYABLE_SELLER + "" +
                    " TEXT," + ORDER_STATUS + " TEXT," + ORDER_PAYMENT_TYPE + " TEXT," + ORDER_IS_REVIEWED + " TEXT," +
                    "" + ORDER_IS_PAY_SELLER + " TEXT," + ORDER_IS_PAY_USER + " TEXT," + ORDER_CREATED_AT + " TEXT," +
                    "" + ORDER_IS_MLM + " TEXT," + ORDER_MLM_TRANSACTION_ID + " TEXT," + ORDER_MLM_VALUE + " TEXT," +
                    ORDER_ORDER_ID + " TEXT," + ORDER_SINLGE_ORDER_ID + " TEXT," + ORDER_SHIPPING_ADDRESS_ID + " TEXT)";
            Log.d(TAG, "Query " + TABLE_ORDER + " Create :" + query);
            db.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + "("
                    + CATEGORY_ID + " INTEGER PRIMARY KEY,"
                    + CATEGORY_NAME + " TEXT,"
                    + CATEGORY_PARENTID + " INTEGER," + CATEGORY_TYPE + " INTEGER,"
                    + CATEGORY_DELETED_AT + " TEXT," + CATEGORY_CREATED_AT + " TEXT," + CATEGORY_UPDATED_AT + " TEXT," + CATEGORY_MLM_DISCOUNT + " INTEGER," + CATEGORY_SEQUENCE_NO + " INTEGER," + CATEGORY_SHIPPING_COST + " INTEGER," + CATEGORY_SHIPING_COST_SELLER + " INTEGER," + CATEGORY_SHIPPING_COST_BUYER + " INTEGER)";
            Log.d(TAG, "Query " + TABLE_CATEGORY + " Create :" + query);
            db.execSQL(query);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("dbhandler onCreate : ", e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        try {

			/*		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBTASK);*/
           /* db.execSQL("DROP TABLE IF EXISTS Notification_Mst");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTMASTER);
*/
            db.setVersion(newVersion);
            onCreate(db);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    public static final String convertedDoubleAmount(String amt)
    {
        Log.d(TAG,"Value : "+amt);
        NumberFormat formatter = null;
        try {
            formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
           amt = formatter.format(amt);


        } catch (Exception e) {
            e.printStackTrace();

        }
        return amt;
    }

    public static final String getOrderStatusNameByStatusId(int id) {
        String status = "";
        if (id == 0)
            status = "ORDER_PLACED";
        else if (id == 1)
            status="FULFILLED_TODAY";
        else if (id == 2)
            status="DELAYED";
        else if (id == 3)
            status="RETURNS";
        else if (id == 4)
            status="CANCEL";
        else if (id == 5)
            status="MANIFESTED";
        else if (id == 6)
            status="SHIPPED";
        else if (id == 7)
            status="DELIVERED";
        else if (id == 8)
            status="COMPLETED";
        else if (id == 9)
            status="PENDING_FROM_ADMIN";
        else if (id == 10)
            status="DISPATCED";
        else if (id == 11)
            status="CANCLE_FROM_SELLER";



            return status;
    }

    public static final String convertToJsonDateFormat(String cur_date) {

        Log.d("Passed Date : ", cur_date);
        SimpleDateFormat dateFormat = null;
        Date date = null;
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault());

//String string = "January 2, 2010";
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            date = format.parse(cur_date);
            System.out.println(date);
        } catch (Exception e) {
            Log.d("Convert DataFormat :: ", e.getMessage());
        }


        //Date date = new Date();

        return dateFormat.format(date);


    }


    public static final String convertToAppDateFormat(String cur_date) {

        Log.d("Passed Date : ", cur_date);
        SimpleDateFormat dateFormat = null;
        Date date = null;
        try {
            dateFormat = new SimpleDateFormat("dd-MM-yyyy",
                    Locale.getDefault());

//String string = "January 2, 2010";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            date = format.parse(cur_date);
            System.out.println(date);
        } catch (Exception e) {
            Log.d("Convert DataFormat :", e.getMessage());
        }


        //Date date = new Date();

        return dateFormat.format(date);


    }

    //Methods
    public static String convertToJsonFormat(String json_data)
    {

        String response = "{\"data\":" + json_data + "}";
        return response;

    }

    public static final String convertEncodedString(String str) {
        String enoded_string = null;
        try {
            enoded_string = URLEncoder.encode(str, "utf-8").replace(".", "%2E");
            enoded_string = URLEncoder.encode(str, "utf-8").replace("+", "%20");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return enoded_string;
    }

    public static String getMonth(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }

    public static final String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());


        Date date = new Date();

        Log.d(TAG, "Current Date and Time :" + dateFormat.format(date).toString());

        return dateFormat.format(date);
    }


    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("Encoded String : ", encodedImage);
        return encodedImage;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        Log.d("Decoded String   : ", "" + BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    public int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    public static final String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String originalPassword = "password";
        String generatedSecuredPasswordHash = BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));
        System.out.println(generatedSecuredPasswordHash);

        boolean matched = BCrypt.checkpw(originalPassword, generatedSecuredPasswordHash);
        System.out.println(matched);
    }

    /*public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/
}
