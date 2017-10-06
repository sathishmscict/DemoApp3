package com.yelona.helper;


import android.graphics.drawable.Drawable;

import com.yelona.R;

import java.util.regex.Pattern;

public class AllKeys {
	public static  final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

	public static final String URL_PINCODE_CHECK = "http://s.evahan.in/Traking/check_service";


	public static final String URL_PINCODE_TRACKING = "http://s.evahan.in/Traking/Check_service/tracking?awbno=";




	public static final String URL_CITYNAME_BY_PINCODE_CHECK = "https://www.whizapi.com/api/v2/util/ui/in/indian-city-by-postal-code?project-app-key=p75dteec5dvdymhkis18c5lk";

	public static final String URL_CITYNAME_BY_PINCODE_CHECK2 = "http://postalpincode.in/api/pincode/";
	public static String TAG_PAYMENT_HASH_GENERATION="http://19designs.org/yelona/get_hash.php";
	public static final String TAG_MOBILE_SLIDER_1="Mobile Slider One";
	public static final String TAG_MOBILE_SLIDER_2="Mobile Slider Two";

    public static final boolean checkEmail(String email) {
		System.out.println("Email Validation:==>" + email);
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}


	public static final String WEBSITE = "http://19designs.org/yelona/index.php/welcome/";
	//public static final String WEBSITE = "http://arham.dnsitexperts.com/yelona/index.php/welcome/";//http://demo1.dnsitexperts.com/
	//public static final String WEBSITE = "http://19designs.org/yelona/index.php/welcome/";//http://demo1.dnsitexperts.com/


	public static final String RESOURSES="https://www.yelona.com/";
	public static final Integer MY_SOCKET_TIMEOUT_MS=30000;

	public static final String PAYBIZ_KEY="46Hobg";
	public static final String PAYBIZ_SALT="46Hobg";
	public static final String PAYBIZ_ENVIRONMENT = "Production";

	//public static final String TAG_WEBSITE_SERVICET="http://panel.dnsitexperts.com/serviceT.asmx/";

	//public static final String TAG_YOURCARE_WEBSITE="http://yourcarepharmacy.com/MedicalService.asmx/";

	//public static final String WEBSITE2 = "http://panel.dnsitexperts.com/";//
	//public static final String CLIENT_ID = "27";//
	//public static final String MAIN_WEB = "radiant.dnsitexperts.com";//

	//public static final String WEBSITE_SOCIALLINK = "http://radiant.dnsitexperts.com/sociallinks.aspx";

	//public static final String WEBSITE_EVENTS = "http://radiant.dnsitexperts.com/events.aspx";




	//userRelated Keys
	public static final String TAG_USER_ID="id";
	public static final String TAG_USER_FIRSTNAME="first_name";
	public static final String TAG_USER_LASTNAME="last_name";
	public static final String TAG_USER_AVATAR="avatar";
	public static final String TAG_USER_USERNAME="username";
	public static final String TAG_USER_EMAIL="email";
	public static final String TAG_USER_PASSWORD="password";
	public static final String TAG_USER_MOBILE="mobile";
	public static final String TAG_USER_BIRTHDATE="birthday";
	public static final String TAG_USER_GENDER="gender";
	public static final String TAG_USER_BIO="bio";
	public static final String TAG_USER_LASTLOGIN="last_login";
	public static final String TAG_USER_VERIFIED_MOBILE="verified_mobile";
	public static final String TAG_USER_VERIFIED_EMAIL="verified_email";


//Order Master Related Keys
//public static final String TAG_ORDER = "id";

	public static final String TAG_ORDER_PRODUCT_IMAGE = "ProductImage";
	public static final String TAG_ORDER_SELLERID = "seller_id";
	public static final String TAG_ORDER_PRODUCTID = "product_id";
	public static final String TAG_ORDER_QUANTITY = "qty";
	public static final String TAG_ORDER_UNIT_PRICE = "unit_price";
	public static final String TAG_ORDER_SHIPPING_CHARGE = "shipping_charge";
	public static final String TAG_ORDER_PROMO_VALUE = "promo_value";
	public static final String TAG_ORDER_PAYABLE_USER = "payable_user";
	public static final String TAG_ORDERPAYABLE_SELLER = "payable_seller";
	public static final String TAG_ORDER_STATUS = "status";
	public static final String TAG_ORDER_PAYMENT_TYPE = "payment_type";
	public static final String TAG_ORDER_IS_REVIEWED = "is_reviewed";
	public static final String TAG_ORDER_IS_PAY_SELLER = "is_pay_seller";
	public static final String TAG_ORDER_IS_PAY_USER = "is_pay_user";
	public static final String TAG_ORDER_CREATED_AT = "created_at";
	public static final String TAG_ORDER_UPDATED_AT = "updated_at";
	public static final String TAG_ORDER_DELETED_AT = "deleted_at";
	public static final String TAG_ORDER_IS_MLM = "is_mlm";
	public static final String TAG_ORDER_MLM_TRANSACTION_ID = "mlm_transactions_id";
	public static final String TAG_ORDER_MLM_VALUE = "mlm_value";
	public static final String TAG_ORDER_ORDER_ID = "order_id";
	public static final String TAG_ORDER_SELLER_DATE = "seller_date";
	public static final String TAG_ORDER_SHIPPING_ADDRESS = "shipping_address";
	public static final String TAG_ORDER_BILLING_ADDRESS = "billing_address";
	public static final String TAG_ORDER_SINGLE_ORDERID = "single_order_id";
	public static final String TAG_ORDER_PAYMENT_ID = "payment_id";
	public static final String TAG_ORDER_PAYMENT_RESPONSE = "payment_response";
	public static final String TAG_ORDER_SHIPPING_PROVIDER = "shipping_provider";
	public static final String TAG_ORDER_DELIVERY_DATE = "delivered_date";
	public static final String TAG_ORDER_TRACKING_ID = "tracking_id";
	public static final String TAG_ORDER_SHIPPING_ADDRESSID = "shipping_address_id";
	public static final String TAG_ORDER_BUYER_ADDRESSID = "buyer_address_id";
	public static final String TAG_ORDER_DISPATCH_DATE = "dispatch_date";
	public static final String TAG_ORDER_COMPLETE_DATE = "complete_date";
	public static final String TAG_ORDER_VARIANT_ID = "size_variant";
	public static final String TAG_ORDER_PENALTY = "penalty";
	public static final String TAG_ORDER_IS_PENALTY_COVER = "is_penalty_cover";







	//getAllSliderType Related Keys
	public static final String TAG_BANNER_TYPEID = "id";
	public static final String TAG_BANNER_NAME = "typeName";
	public static final String TAG_BANNER_CATEGORYLINK = "link";
	public static final String TAG_BANNER_SQQUENCE = "sequence_no";

	public static final String TAG_BANNER_IMAGE="image";


	//CategoryMaster Related Keys

	public static final String TAG_CATEGORY_ID = "id";
	public static final String TAG_CATEGORY_NAME = "name";
	public static final String TAG_CATEGORY_PARENTID = "parent_id";
	public static final String TAG_CATEGORY_TYPE = "type";
	public static final String TAG_CATEGORY_DELETED_AT = "deleted_at";
	public static final String TAG_CATEGORY_CREATED_AT= "created_at";
	public static final String TAG_CATEGORY_UPDATED_AT = "updated_at";
	public static final String TAG_CATEGORY_MLM_DISCOUNT = "mlm_discount";
	public static final String TAG_CATEGORY_SEQUENCE_NO = "sequence_no";
	public static final String TAG_CATEGORY_SHIPING_COST = "shipping_cost";
	public static final String TAG_CATEGORY_SHIPPING_COST_SELLER = "shipping_cost_seller";
	public static final String TAG_CATEGORY_SHIPPING_COST_BUYER = "shipping_cost_buyer";





	//Contact Us Related Keys
	public static final String TAG_CONTACT_ADDRESS="Address";
	public static final String TAG_CONTACT_LONGTITUDE="Longitude";
	public static final String TAG_CONTACT_LATTITUDE="Latitude";
	public static final String TAG_CONTACT_EMAIL="Email";
	public static final String TAG_CONTACT_PHONE="PhoneNo";
	public static final String TAG_CONTACT_MOBILE="MobileNo";
	public static final String TAG_CONTACT_PERSON="ContactPerson";

	//dashboard_product_category_type , categorytype and WishList Related Keys
	public static final String TAG_CHECKOUT_PRODUCTID="product_id";
	public static final String TAG_SHIPPINFCOST="shipping_cost";
	public static final String TAG_PRODUCTID="id";
	public static final String TAG_CHECKOUT_PRODUCT_NAME="ProductName";
	public static final String TAG_PRODUCT_CATEGORYID="category_id";
	public static final String TAG_IMAGE_URL="image";

	public static final String TAG_SELLER_PINCODE="SellerPincode";

	public static final String TAG_PRICE="price";
	public static final String TAG_OFFER_VALUE="offer_value";
	public static final String TAG_OFFER_TAG="offer_tag";
	public static final String TAG_COLOR="color";
	public static final String TAG_WEIGHT="weight";
	public static final String TAG_HEIGHT="height";
	public static final String TAG_LENGTH="length";
	public static final String TAG_WIDTH="width";
	public static final String TAG_SIZE="size";
	public static final String TAG_IS_SIZE="is_size";
	public static final String TAG_MRP="mrp";
	public static final String TAG_SKU="sku";
	public static final String TAG_SHIP_DATE="ship_date";
	public static final String TAG_BRAND="brand";
	public static final String TAG_INVENTORY="inventory";
	public static final String TAG_COMITTED_BY="committed_qty";
	public static final String TAG_PRODUCTNAME="name";
	public static final String TAG_DESCRIPTION="description";
	public static final String TAG_CREATED_AT="created_at";
	public static final String TAG_UPDATE_AT="updated_at";
	public static final String TAG_STAR_COUNT="star_count";
	public static final String TAG_MORE_IMAGES="path";
	public static final String TAG_MLM_DISCOUNT="mlm_discount";
	public static final String TAG_SELLERID="seller_id";

	public static final String TAG_SELLER_COMPANY_NAME="company_name";
	public static final String TAG_SELLER_NAME="first_name";
	public static final String TAG_SELLER_EMAIL="email";
	public static final String TAG_SELLER_ADDRESS="address";
	public static final String TAG_SELLER_CITY="city";
	public static final String TAG_SELLER_STATE="state";
	public static final String TAG_SELLER_CODE="seller_code";
	public static final String TAG_SELLER_RATING="star";
	public static final String TAG_SELLER_MOBILE="mobile";
	public static final String TAG_SELLER_AVATAR="avatar";
	public static final String TAG_SELLER_IS_ACTIVE="is_active";



	//Review Related keys
	public static final String TAG_REVIEW_CREATED_AT="created_at";
	public static final String TAG_REVIEW_UPDATED_AT="updated_at";
	public static final String TAG_REVIEW="review";
	public static final String TAG_REVIEW_STARS="star";
	public static final String TAG_REVIEW_USER_NAME="username";









	public static final String TAG_WISHLIST_PRODUCTID="product_id";

	//Cart Data Related Keys
	public static final String TAG_CART_ID="id";
	public static final String TAG_CART_PRODUCT_ID="product_id";
	//public static final String TAG_CART_QUANTITY="qty";
	public static final String TAG_CART_CREATED_AT="created_at";
	public static final String TAG_CART_UPDATED_AT="updated_at";
	public static final String TAG_QUANTITY = "qty";

	public static final String TAG_COMMITTED_QTY = "committed_qty";
	public static final String TAG_UNIT_PRICE = "unitprice";



	//Addresses Related Keys
	public static final String TAG_ADDRESS_ID="id";
	public static final String TAG_ADDRESS_AREA="area";
	public static final String TAG_ADDRESS_STREET="street";
	public static final String TAG_ADDRESS_LANDMARK="landmark";
	public static final String TAG_ADDRESS_CITY="city";
	public static final String TAG_ADDRESS_TYPE="type";
	public static final String TAG_ADDRESS_CREATED_AT="created_at";
	public static final String TAG_ADDRESS_UPDATED_AT="updated_at";
	public static final String TAG_ADDRESS_STATE="state";
	public static final String TAG_ADDRESS_ADDRESS1="address1";
	public static final String TAG_ADDRESS_ADDRESS2="address2";
	public static final String TAG_ADDRESS_PINCODE="pin";


	public static final int back_button = R.drawable.icon_back6;

	//State Master Related Data
	public static final String TAG_STATEID="id";
	public static final String TAG_STATE="state";

	//Searching Related Keys
	public static final String TAG_SEARCH_ARRAY="data";
	public static final String TAG_SEARCH_FLAG="flag";
	public static final String TAG_SEARCH_MESSAGE="message";
	public static final String TAG_SEARCH_ID="id";
	public static final String TAG_SEARCH_PRODUCTID="product_id";
	public static final String TAG_SEARCH_NAME="name";
	public static final String TAG_SEARCH_CATEGORYID="category_id";
	public static final String TAG_SEARCH_CATEGORYNAME="category_name";
	public static final String TAG_SEARCH_CATEGORY_TYPE="category_type";
	public static final String TAG_SEARCH_CATEGORY_NAME="category_type_name";
	public static final String TAG_SEARCH_IS_ACTIVE_SELLER="is_active_seller";
	public static final String TAG_SEARCH_IS_ADMIN_ACTIVE="is_active_admin";
	public static final String TAG_SEARCH_SKU="sku";
	public static final String TAG_SEARCH_PRICE="price";
	public static final String TAG_SEARCH_IS_PARENT="is_parent";










}
