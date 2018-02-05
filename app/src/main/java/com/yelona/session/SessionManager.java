package com.yelona.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncAdapterType;

import com.android.volley.toolbox.StringRequest;

import com.yelona.LoginActivity;
import com.yelona.NewDashBoardActivity;

import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by Satish Gadde on 13-03-2016.
 */
public class SessionManager {


    // Check For Activation
    public static final String KEY_CODE = "code", KEY_SMSURL = "smsurl",
            KEY_RECEIVECODE = "reccode",
            KEY_VERSTATUS = "verification_status";

    public static final String KEY_NOTIFICATION_STATUS="NotificationStatus";

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;


    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "YelonaPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_CATEGORY_TYPE = "CategoryType";
    public static final String KEY_CATEGORY_ID = "CategoryID";
    public static final String KEY_CATEGORY_NAME = "CategoryName";
    public static final String KEY_PRODUCT_ID = "ProductId";
    public static final String KEY_WISHLIST_STATUS = "WishListStatus";
    public static final String KEY_CARTITEMS_ID = "CartedProductIds";
    public static final String KEY_PRODUCT_DESCR = "ProductDescr";
    public static final String KEY_PRODUCT_IMAGE_URL = "ProductImageURL", KEY_PRODUCT_RATING = "ProductRating";


    public static final String KEY_USER_PINCODE = "userPincode", KEY_USER_CITY = "userCity";

    public static final String KEY_USERID = "UserId", KEY_ENODEDED_STRING = "Encoded_string";
    public static final String KEY_USERNAME = "UserName";
    public static final String KEY_USER_MOBILE = "UserMobile";

    public static final String KEY_USER_FIRSTNAME = "FirstName", KEY_USER_LASTNAME = "LastName", KEY_USER_AVATAR = "Avatar", KEY_USER_NAME = "Username", KEY_USER_PASSWORD = "Password", KEY_USER_DOB = "DOB", KEY_USER_GENDER = "Gender", KEY_USER_BIO = "Bio", KEY_USER_LASTLOGIN = "LastLogin", KEY_USER_VERIFIED_MOBILE = "IsVerifiedMobile", KEY_USER_VERIFIED_EMAIL = "IsVerifiedEmail";

    public static final String KEY_USER_EMAIL = "UserEmail";
    public static final String KEY_ACTIVITY_NAME = "ActivityName";

    public static final String KEY_SHIPPING_NAME = "ShippingName", KEY_SHIPPING_EMAIL = "ShippingEmail", KEY_SHIPPING_MOBILENO = "ShippingMobileNo", KEY_SHIPPING_ADDRESS = "ShippingAddress", KEY_SHIPPING_ADDRESS_ID = "ShippingAddressId";
    public static final String KEY_BILLING_NAME = "BillingName", KEY_BILLING_EMAIL = "BillingEmail", KEY_BILLING_MOBILENO = "BillingMobileNo", KEY_BILLING_ADDRESS = "BillingAddress", KEY_BILLING_ADDRESS_ID = "billingAddressId";

    public static final String KEY_PRICE_TOTAL = "PriceTotal", KEY_GRAND_TOTAL = "GrandTotal", KEY_DELIVERY_CHARGES_TOTAL = "TotalDeliveryCharges", KEY_TOTAL_ITEMS = "totalItems", KEY_MLM_DISCOUNT = "totalMLMDiscount";

    public static final String KEY_SELLER_IS_ACTIVE = "SellerIsActive";
    public static final String KEY_SELLER_COMPANY_NAME = "SellerCompanyName";
    public static final String KEY_SELLER_NAME = "SellerName";
    public static final String KEY_SELLER_EMAIL = "SellerEmail";
    public static final String KEY_SELLER_ADDRESS = "SellerAddress";
    public static final String KEY_SELLER_CITY = "SellerCity";
    public static final String KEY_SELLER_STATE = "SellerState";
    public static final String KEY_SELLER_ID = "SellerId";
    public static final String KEY_SELLER_CODE = "SellerCode";
    public static final String KEY_SELLER_RATING = "SellerRating";
    public static final String KEY_SELLER_AVATAR = "SelllerAvatar";
    public static final String KEY_SELLER_MOBILE = "SellerMobile";
    public static final String KEY_DELIVERY_CHARGES = "Deliverycharges", KEY_NEWACTIVITY_NAME = "newActivityName", KEY_WALLET_BALANCE = "WalletAmount";
    public static final String KEY_SHIPPED_DAYS = "ShippedDaya", KEY_CHECKOUT_TYPE = "CheckoutType";


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public int checkLogin() {
        // Check login status
        int ans = 0;
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, NewDashBoardActivity.class);
            ans = 1;
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

        return ans;

    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public void setNotificationStatus(String  notificationStatus,String productid,String categoryid)
    {
        editor.putString(KEY_NOTIFICATION_STATUS,notificationStatus);
        editor.putString(KEY_PRODUCT_ID, productid);
        editor.putString(KEY_CATEGORY_ID , categoryid);
        editor.commit();

    }

    public void createUserSendSmsUrl(String code, String websiteurl) {

        editor.putString(KEY_CODE, code);
        editor.putString(KEY_SMSURL, websiteurl);// http://radiant.dnsitexperts.com/JSON_Data.aspx?type=otp&mobile=9825681802&code=7692
        editor.commit();

    }

    public void CheckSMSVerificationActivity(String reccode, String actstatus) {

        editor.putString(KEY_RECEIVECODE, reccode);
        editor.putString(KEY_VERSTATUS, actstatus);
        editor.commit();

    }

    public void setCartItemsIdDetails(String cartItemIds) {

        editor.putString(KEY_CARTITEMS_ID, cartItemIds);
        editor.commit();

    }


    public void setActivityName(String activityName) {
        editor.putString(KEY_ACTIVITY_NAME, activityName);
        editor.commit();

    }


    public void setUserImageUrl(String imgURL) {

        editor.putString(KEY_USER_AVATAR, imgURL);
    }

    public void setUserDetails(String str_userid, String str_firstname, String str_lastname, String str_avatar, String str_username, String str_email, String str_password, String str_mobile, String str_birthdate, String str_gender, String str_bio, String str_last_login, String str_verified_mobile, String str_verified_email, String type) {


        editor.putString(KEY_USERID, str_userid);
        editor.putString(KEY_USER_FIRSTNAME, str_firstname);
        editor.putString(KEY_USER_LASTNAME, str_lastname);
        if (type.equals("normal")) {
            editor.putString(KEY_USER_AVATAR, str_avatar);


        }
        editor.putString(KEY_USER_AVATAR, str_avatar);


        editor.putString(KEY_USER_NAME, str_username);
        editor.putString(KEY_USER_EMAIL, str_email);
        editor.putString(KEY_USER_PASSWORD, str_password);
        editor.putString(KEY_USER_MOBILE, str_mobile);

        editor.putString(KEY_USER_DOB, str_birthdate);
        editor.putString(KEY_USER_GENDER, str_gender);
        editor.putString(KEY_USER_BIO, str_bio);
        editor.putString(KEY_USER_LASTLOGIN, str_last_login);
        editor.putString(KEY_USER_VERIFIED_MOBILE, str_verified_mobile);
        editor.putString(KEY_USER_VERIFIED_EMAIL, str_verified_email);


        editor.commit();

    }

    public void setProductDetails(String productId, String wishListStatus, String descr, String productImageURL, String productRating) {
        editor.putString(KEY_PRODUCT_ID, productId);
        editor.putString(KEY_WISHLIST_STATUS, wishListStatus);
        editor.putString(KEY_PRODUCT_DESCR, descr);
        editor.putString(KEY_PRODUCT_IMAGE_URL, productImageURL);
        editor.putString(KEY_PRODUCT_RATING, productRating);
        editor.commit();

    }


    public void setOrderTotalDetails(String total_price, String grand_total, String total_delivery_charges, String total_items, String totalMLMdicsount) {


        editor.putString(KEY_PRICE_TOTAL, total_price);
        editor.putString(KEY_GRAND_TOTAL, grand_total);
        editor.putString(KEY_DELIVERY_CHARGES_TOTAL, total_delivery_charges);
        editor.putString(KEY_TOTAL_ITEMS, total_items);
        editor.putString(KEY_MLM_DISCOUNT, totalMLMdicsount);
        editor.commit();

    }


    public void setShippingAdress(String shippingName, String shippingEmail, String shippingMobileno, String shipingAddress, String shippingAddressId) {

        editor.putString(KEY_SHIPPING_NAME, shippingName);
        editor.putString(KEY_SHIPPING_EMAIL, shippingEmail);
        editor.putString(KEY_SHIPPING_MOBILENO, shippingMobileno);
        editor.putString(KEY_SHIPPING_ADDRESS, shipingAddress);
        editor.putString(KEY_SHIPPING_ADDRESS_ID, shippingAddressId);
        editor.commit();


    }


    public void setUserMobile(String mobile) {

        editor.putString(KEY_USER_MOBILE, mobile);
        editor.commit();

    }

    public void setEncodedImage(String encodeo_image) {


        editor.putString(KEY_ENODEDED_STRING, encodeo_image);

        editor.commit();
    }

    public void setUserPinocdeAndCity(String strPinocde, String strCity) {

        editor.putString(KEY_USER_PINCODE, strPinocde);
        editor.putString(KEY_USER_CITY, strCity);

        editor.commit();
    }

    public void setBillingAddress(String billingName, String billingEmail, String billingMobileNo, String billingAddress, String billingaddressId) {

        editor.putString(KEY_BILLING_NAME, billingName);
        editor.putString(KEY_BILLING_EMAIL, billingEmail);
        editor.putString(KEY_BILLING_MOBILENO, billingMobileNo);
        editor.putString(KEY_BILLING_ADDRESS, billingAddress);
        editor.putString(KEY_BILLING_ADDRESS_ID, billingaddressId);


        editor.commit();

    }


    public void setCategoryTypeAndIdDetails(String categorytype, String categoryid, String categotyname) {
        editor.putString(KEY_CATEGORY_TYPE, categorytype);
        editor.putString(KEY_CATEGORY_ID, categoryid);
        editor.putString(KEY_CATEGORY_NAME, categotyname);
        editor.commit();

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getSessionDetails() {

        //KEY_FULLNAME="uerfullname",KEY_USERNAME="username",KEY_EMAIL="email",KEY_MOBILE="mobileno",KEY_PHONENO="phoneno",KEY_PASSWORD="password";
        HashMap<String, String> user = new HashMap<String, String>();

        // user.put(KEY_ORDERID, pref.getString(KEY_ORDERID, "0"));

        user.put(KEY_CATEGORY_TYPE, pref.getString(KEY_CATEGORY_TYPE, ""));
        user.put(KEY_CATEGORY_ID, pref.getString(KEY_CATEGORY_ID, "0"));
        user.put(KEY_CATEGORY_NAME, pref.getString(KEY_CATEGORY_NAME, ""));
        user.put(KEY_PRODUCT_ID, pref.getString(KEY_PRODUCT_ID, ""));
        user.put(KEY_WISHLIST_STATUS, pref.getString(KEY_WISHLIST_STATUS, "-1"));
        user.put(KEY_CARTITEMS_ID, pref.getString(KEY_CARTITEMS_ID, ""));


        user.put(KEY_USERID, pref.getString(KEY_USERID, "0"));
        user.put(KEY_USER_FIRSTNAME, pref.getString(KEY_USER_FIRSTNAME, ""));
        user.put(KEY_USER_LASTNAME, pref.getString(KEY_USER_LASTNAME, ""));
        user.put(KEY_USER_AVATAR, pref.getString(KEY_USER_AVATAR, ""));
        user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, ""));
        user.put(KEY_USER_PASSWORD, pref.getString(KEY_USER_PASSWORD, ""));
        user.put(KEY_USER_DOB, pref.getString(KEY_USER_DOB, ""));
        user.put(KEY_USER_GENDER, pref.getString(KEY_USER_GENDER, ""));
        user.put(KEY_USER_BIO, pref.getString(KEY_USER_BIO, ""));
        user.put(KEY_USER_LASTLOGIN, pref.getString(KEY_USER_LASTLOGIN, ""));
        user.put(KEY_USER_VERIFIED_MOBILE, pref.getString(KEY_USER_VERIFIED_MOBILE, "0"));
        user.put(KEY_USER_VERIFIED_EMAIL, pref.getString(KEY_USER_VERIFIED_EMAIL, "0"));


        user.put(KEY_NOTIFICATION_STATUS , pref.getString(KEY_NOTIFICATION_STATUS,"false"));

        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));
        user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, ""));
        user.put(KEY_USER_MOBILE, pref.getString(KEY_USER_MOBILE, ""));
        user.put(KEY_ACTIVITY_NAME, pref.getString(KEY_ACTIVITY_NAME, ""));

        user.put(KEY_SHIPPING_EMAIL, pref.getString(KEY_SHIPPING_EMAIL, ""));
        user.put(KEY_SHIPPING_NAME, pref.getString(KEY_SHIPPING_NAME, ""));
        user.put(KEY_SHIPPING_MOBILENO, pref.getString(KEY_SHIPPING_MOBILENO, ""));
        user.put(KEY_SHIPPING_ADDRESS, pref.getString(KEY_SHIPPING_ADDRESS, ""));
        user.put(KEY_SHIPPING_ADDRESS_ID, pref.getString(KEY_SHIPPING_ADDRESS_ID, "0"));

        user.put(KEY_BILLING_NAME, pref.getString(KEY_BILLING_NAME, ""));
        user.put(KEY_BILLING_EMAIL, pref.getString(KEY_BILLING_EMAIL, ""));
        user.put(KEY_BILLING_MOBILENO, pref.getString(KEY_BILLING_MOBILENO, ""));
        user.put(KEY_BILLING_ADDRESS, pref.getString(KEY_BILLING_ADDRESS, ""));
        user.put(KEY_BILLING_ADDRESS_ID, pref.getString(KEY_BILLING_ADDRESS_ID, "0"));


        user.put(KEY_PRICE_TOTAL, pref.getString(KEY_PRICE_TOTAL, "0"));
        user.put(KEY_GRAND_TOTAL, pref.getString(KEY_GRAND_TOTAL, "0"));
        user.put(KEY_DELIVERY_CHARGES_TOTAL, pref.getString(KEY_DELIVERY_CHARGES_TOTAL, "0"));


        user.put(KEY_DELIVERY_CHARGES, pref.getString(KEY_DELIVERY_CHARGES, "0"));

        user.put(KEY_TOTAL_ITEMS, pref.getString(KEY_TOTAL_ITEMS, "0"));
        user.put(KEY_MLM_DISCOUNT, pref.getString(KEY_MLM_DISCOUNT, "0"));

        user.put(KEY_RECEIVECODE, pref.getString(KEY_RECEIVECODE, "0"));
        user.put(KEY_CODE, pref.getString(KEY_CODE, "0"));

        user.put(KEY_SMSURL, pref.getString(KEY_SMSURL, ""));
        user.put(KEY_VERSTATUS, pref.getString(KEY_VERSTATUS, "0"));

        user.put(KEY_USER_PINCODE, pref.getString(KEY_USER_PINCODE, ""));
        user.put(KEY_USER_CITY, pref.getString(KEY_USER_CITY, ""));

        user.put(KEY_PRODUCT_DESCR, pref.getString(KEY_PRODUCT_DESCR, ""));
        user.put(KEY_PRODUCT_IMAGE_URL, pref.getString(KEY_PRODUCT_IMAGE_URL, ""));


        user.put(KEY_SELLER_IS_ACTIVE, pref.getString(KEY_SELLER_IS_ACTIVE, ""));

        user.put(KEY_SELLER_COMPANY_NAME, pref.getString(KEY_SELLER_COMPANY_NAME, ""));

        user.put(KEY_SELLER_NAME, pref.getString(KEY_SELLER_NAME, ""));

        user.put(KEY_SELLER_EMAIL, pref.getString(KEY_SELLER_EMAIL, ""));

        user.put(KEY_SELLER_ADDRESS, pref.getString(KEY_SELLER_ADDRESS, ""));

        user.put(KEY_SELLER_CITY, pref.getString(KEY_SELLER_CITY, ""));

        user.put(KEY_SELLER_STATE, pref.getString(KEY_SELLER_STATE, ""));

        user.put(KEY_SELLER_ID, pref.getString(KEY_SELLER_ID, ""));

        user.put(KEY_SELLER_CODE, pref.getString(KEY_SELLER_CODE, ""));

        user.put(KEY_SELLER_RATING, pref.getString(KEY_SELLER_RATING, "0"));

        user.put(KEY_SELLER_AVATAR, pref.getString(KEY_SELLER_AVATAR, ""));

        user.put(KEY_SELLER_MOBILE, pref.getString(KEY_SELLER_MOBILE, ""));

        user.put(KEY_SHIPPED_DAYS, pref.getString(KEY_SHIPPED_DAYS, "7"));

        user.put(KEY_PRODUCT_RATING, pref.getString(KEY_PRODUCT_RATING, ""));

        user.put(KEY_CHECKOUT_TYPE, pref.getString(KEY_CHECKOUT_TYPE, ""));


        user.put(KEY_ENODEDED_STRING, pref.getString(KEY_ENODEDED_STRING, ""));


        user.put(KEY_NEWACTIVITY_NAME, pref.getString(KEY_NEWACTIVITY_NAME, ""));


        user.put(KEY_WALLET_BALANCE, pref.getString(KEY_WALLET_BALANCE, "0"));


        return user;
    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    //sessionmanager.setSellerInformation(c.getString(AllKeys.TAG_SELLER_IS_ACTIVE),c.getString(AllKeys.TAG_SELLER_COMPANY_NAME),c.getString(AllKeys.TAG_SELLER_NAME),c.getString(AllKeys.TAG_SELLER_EMAIL),c.getString(AllKeys.TAG_SELLER_ADDRESS),c.getString(AllKeys.TAG_SELLER_CITY),c.getString(AllKeys.TAG_SELLER_STATE),SELLER_ID,c.getString(AllKeys.TAG_SELLER_CODE),c.getString(AllKeys.TAG_SELLER_RATING),c.getString(AllKeys.TAG_SELLER_AVATAR),c.getString(AllKeys.TAG_SELLER_MOBILE));

    public void setSellerInformation(String sellerIsActive, String sellerCompanyName, String sellerName, String sellerEmail, String sellerAddress, String sellerCity, String sellerState, String seller_id, String sellerCode, String sellerRating, String sellerAvatar, String selllerMobile, String deliveryCharges, String shippedDays) {


        editor.putString(KEY_SELLER_IS_ACTIVE, sellerIsActive);
        editor.putString(KEY_SELLER_COMPANY_NAME, sellerCompanyName);
        editor.putString(KEY_SELLER_NAME, sellerName);
        editor.putString(KEY_SELLER_EMAIL, sellerEmail);
        editor.putString(KEY_SELLER_ADDRESS, sellerAddress);
        editor.putString(KEY_SELLER_CITY, sellerCity);
        editor.putString(KEY_SELLER_STATE, sellerState);
        editor.putString(KEY_SELLER_ID, seller_id);
        editor.putString(KEY_SELLER_CODE, sellerCode);
        editor.putString(KEY_SELLER_RATING, sellerRating);
        editor.putString(KEY_SELLER_AVATAR, sellerAvatar);
        editor.putString(KEY_SELLER_MOBILE, selllerMobile);

        editor.putString(KEY_DELIVERY_CHARGES, deliveryCharges);

        try {
            Double dd = Double.parseDouble(shippedDays);
            editor.putString(KEY_SHIPPED_DAYS, String.valueOf(dd.intValue()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        editor.commit();
    }

    public void setCheckoutType(String checkoutType, String productId) {

        editor.putString(KEY_CHECKOUT_TYPE, checkoutType);
        editor.putString(KEY_PRODUCT_ID, productId);
        editor.commit();
    }

    public void setNewUserSession(String newActivityName) {

        editor.putString(KEY_NEWACTIVITY_NAME, newActivityName);
        editor.commit();
    }

    public void setWalletAmount(String walletAmount) {
        editor.putString(KEY_WALLET_BALANCE, walletAmount);
        editor.commit();

    }
}
