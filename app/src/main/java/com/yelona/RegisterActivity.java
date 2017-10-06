package com.yelona;

import android.*;
import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.BCrypt;
import com.yelona.helper.Utility;
import com.yelona.helper.Utils;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;



public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context = this;
    private EditText edtUsername;
    private TextInputLayout edtUsernameWrapper;
    private EditText edtEmail;
    private TextInputLayout edtEmailWrapper;
    private TextInputLayout edtmobileWrapper;
    private EditText edtMobile;
    private TextInputLayout edtpasswordWrapper;
    private EditText edtPassword;
    private RadioGroup rdGrpGender;
    private Button btnSignup;
    private String TAG = RegisterActivity.class.getSimpleName();
    private String GENDER_ID="1";
    private SpotsDialog progress_dialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails= new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;


    private AppCompatButton login_fb_button;
    private AppCompatButton login_google_button;
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;

    private String facebook = "";
    LoginButton login;
    CallbackManager callbackManager;
    public static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS=100;

    public String SIGNUP_TYPE="normal",USER_EMAIL=null,USER_PROFILE_URL="",FIRST_NAME=null,LAST_NAME=null;
    private String PROVIDER_USERID="0";
    private TextView txtError;
    private Button btnSignin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

       // String pass = BCrypt.hashpw("ze@123", BCrypt.gensalt());


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();

        setTitle("Yelona Registration");
        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();
        Utility.checkPermission_ReadSMS(context);
        progress_dialog = new SpotsDialog(context);
        progress_dialog.setCancelable(false);

        edtUsernameWrapper = (TextInputLayout) findViewById(R.id.edtUsernameWrapper);
        edtUsername = (EditText) findViewById(R.id.edtUsername);

        edtEmailWrapper = (TextInputLayout) findViewById(R.id.edtEmailWrapper);
        edtEmail = (EditText) findViewById(R.id.edtEmail);

        edtmobileWrapper = (TextInputLayout) findViewById(R.id.edtmobileWrapper);
        edtMobile = (EditText) findViewById(R.id.edtMobile);

        edtpasswordWrapper = (TextInputLayout) findViewById(R.id.edtpasswordWrapper);
        edtPassword = (EditText) findViewById(R.id.edtpassword);

        rdGrpGender = (RadioGroup) findViewById(R.id.rdGrpGender);

        btnSignup = (Button) findViewById(R.id.btnSignup);

        btnSignin =(Button)findViewById(R.id.btnSignin);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        callbackManager = CallbackManager.Factory.create();

        txtError = (TextView)findViewById(R.id.txterror);
        txtError.setVisibility(View.GONE);

        login_fb_button = (AppCompatButton) findViewById(R.id.login_fb_button);
        login_google_button = (AppCompatButton) findViewById(R.id.login_google_button);

        login_fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SIGNUP_TYPE="facebook";

                login.performClick();


            }
        });

        //login_google_button
        login_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SIGNUP_TYPE="gmail";

                int currentAPIVersion = Build.VERSION.SDK_INT;
                if (currentAPIVersion >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(RegisterActivity.this,
                                new String[]{android.Manifest.permission.GET_ACCOUNTS},
                                MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);

                    }
                    else
                    {
                        gPlusSignIn();
                    }


                }
                else
                {
                    gPlusSignIn();

                }
            }
        });

        login = (LoginButton) findViewById(R.id.login_button);
        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);


        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
       // permissions.add("user_birthday");
      //  permissions.add("user_friends");



        login.setReadPermissions(permissions);

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

            }
        };

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                // AccessToken.getCurrentAccessToken().getToken();
                // App code
                GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (response.getError()!=null)
                        {
                            Log.e(TAG,"Error in Response "+ response);
                        }
                        else
                        {
                            String fbUserId = object.optString("id");
                            PROVIDER_USERID=fbUserId;
                            try {
                                USER_EMAIL=object.optString("email");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String name= null;
                            try {
                                name = object.optString("name");
                                FIRST_NAME = object.optString("name");;
                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                            String fbUserProfilePics = "http://graph.facebook.com/" + fbUserId + "/picture?type=large";
                            USER_PROFILE_URL = fbUserProfilePics;
                            Log.e(TAG,"Json Object Data "+object+" Email id "+ USER_EMAIL+" Name :"+name);

                            //http://graph.facebook.com//picture?type=large

                            //http://graph.facebook.com/800089893478559/picture?type=large

                            //http://graph.facebook.com/799197813567767/picture?type=large

                            sendUserDetailsToServer();

                            sessionmanager.setUserImageUrl(fbUserProfilePics);
                            LoginManager.getInstance().logOut();


                        }


                    }
                });
                Bundle bundle=new Bundle();
                bundle.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();



                Intent in = new Intent(RegisterActivity.this, NewDashBoardActivity.class);
                /*sessionmanager.createstatusKEy("1");*/
                   /* in.putExtra("Name",text);
                    in.putExtra("Email",email);
                    in.putExtra("profile",prifle);*/
                // startActivity(in);
                // finish();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //gmail here

        buidNewGoogleApiClient();




        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SIGNUP_TYPE="normal";
                boolean IsError = false;
                if (edtUsername.getText().toString().equals("")) {
                    IsError = true;
                    edtUsernameWrapper.setErrorEnabled(true);
                    edtUsernameWrapper.setError("Enter Username");
                } else {
                    edtUsernameWrapper.setErrorEnabled(false);

                }

                if (edtEmail.getText().toString().equals("")) {
                    IsError = true;
                    edtEmailWrapper.setErrorEnabled(true);
                    edtEmailWrapper.setError("Enter Email");
                } else {
                    if(AllKeys.checkEmail(edtEmail.getText().toString()))
                    {
                        edtEmailWrapper.setErrorEnabled(false);
                    }
                    else
                    {
                        IsError = true;
                        edtEmailWrapper.setErrorEnabled(true);
                        edtEmailWrapper.setError("Invalid Email");

                    }


                }

                if (edtMobile.getText().toString().equals("")) {
                    IsError = true;
                    edtmobileWrapper.setErrorEnabled(true);
                    edtmobileWrapper.setError("Enter Mobile");
                } else {

                    if(edtMobile.getText().toString().length() != 10)
                    {
                        edtmobileWrapper.setErrorEnabled(true);
                        edtmobileWrapper.setError("Invalid mobileno");
                        IsError = true;
                    }
                    else
                    {
                        edtmobileWrapper.setErrorEnabled(false);

                    }


                }

                if (edtPassword.getText().toString().equals("")) {
                    IsError = true;
                    edtpasswordWrapper.setErrorEnabled(true);
                    edtpasswordWrapper.setError("Enter Password");
                } else {
                    edtpasswordWrapper.setErrorEnabled(false);

                }
                if(rdGrpGender.getCheckedRadioButtonId() == R.id.rdMale)
                {
                    GENDER_ID ="1";
                }
                else if(rdGrpGender.getCheckedRadioButtonId() == R.id.rdFemale)
                {
                    GENDER_ID ="0";
                }
                else
                {
                    Toast.makeText(context, "select gander", Toast.LENGTH_SHORT).show();
                    IsError=true;
                }

                if (IsError == false) {

                   // Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();


                    if(Utility.checkPermission_ReadSMS(context) == true)
                    {
                        edtEmailWrapper.setEnabled(false);
                        edtmobileWrapper.setEnabled(false);
                        sendUserDetailsToServer();
                    }




                }


            }
        });




        /*Drawable drawable = edtUsername.getBackground(); // get current EditText drawable
        drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if(Build.VERSION.SDK_INT > 16) {
            edtUsername.setBackground(drawable); // set the new drawable to EditText

        }else{
            edtUsername.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16

        }
*/


    }
    //onCreate Completed

    private void sendUserDetailsToServer() {



        txtError.setVisibility(View.GONE);
        String  url_signupuser = AllKeys.WEBSITE + "SignupUser_Normal/get/normal/email/password/mobile/gender/created_at/updated_at/device_type/fcm_tokenid/firstname/lastname";

        url_signupuser = AllKeys.WEBSITE + "SignupUser_Normal/get/normal/"+ dbhandler.convertEncodedString(edtEmail.getText().toString()) +"/"+ dbhandler.convertEncodedString(edtPassword.getText().toString()) +"/"+ edtMobile.getText().toString() +"/1/"+ dbhandler.convertEncodedString(dbhandler.getDateTime()) +"/"+ dbhandler.convertEncodedString(dbhandler.getDateTime()) +"/android/fcm_tokenid/"+ dbhandler.convertEncodedString(edtUsername.getText().toString()) +"/";

        if(SIGNUP_TYPE.equals("normal"))
        {
            url_signupuser = AllKeys.WEBSITE + "SignupUser_Normal";
        }
        else
        {
            //($calltype="",$login_type="",$email="", $gender="1", $created_at="", $updated_at="",$devicetype="", $fcmtokenid="",$first_name="", $last_name="", $avatar="",$provider_user_id="", $birthday=null, $verified_mobile="0", $verified_email="0")
            if(USER_EMAIL.equals(""))
            {
                USER_EMAIL="null";
            }
           url_signupuser = AllKeys.WEBSITE + "SignupUser_Social/get/"+ SIGNUP_TYPE +"/"+ USER_EMAIL +"/1/"+ dbhandler.convertEncodedString(dbhandler.getDateTime()) +"/"+ dbhandler.convertEncodedString(dbhandler.getDateTime()) +"/android/12345/"+ FIRST_NAME +"/"+ LAST_NAME +"/"+  dbhandler.convertEncodedString(USER_PROFILE_URL) +"/"+ PROVIDER_USERID +"///";
            url_signupuser = AllKeys.WEBSITE + "SignupUser_Social";
        }


        Log.d(TAG, "URL SignupUser  :" + url_signupuser);
        StringRequest str_request_signup = new StringRequest(Request.Method.POST, url_signupuser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Singup Response : " + response);
                if (response.contains("mobile no already exist"))
                {
                    if(SIGNUP_TYPE.equals("normal"))
                    {
                        edtmobileWrapper.setEnabled(true);
                        edtmobileWrapper.setError(response.toString());
                    }
                    else
                    {
                        txtError.setText(response.toString());
                        txtError.setVisibility(View.VISIBLE);
                    }



                }
                else if(response.contains("Email id already exist"))
                {
                    if(SIGNUP_TYPE.equals("normal"))
                    {
                        edtEmailWrapper.setEnabled(true);
                        edtEmailWrapper.setError(response.toString());
                    }
                    else
                    {
                        txtError.setText(response.toString());
                        txtError.setVisibility(View.VISIBLE);
                    }

                }
                else if (response.contains("first_name"))
                {
                    try {
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++)
                        {
                            JSONObject c = arr.getJSONObject(i);


                            String str_userid = (c.getString(AllKeys.TAG_USER_ID));
                            String str_firstname = (c.getString(AllKeys.TAG_USER_FIRSTNAME));
                            String str_lastname = (c.getString(AllKeys.TAG_USER_LASTNAME));
                            String str_avatar = (c.getString(AllKeys.TAG_USER_AVATAR));
                            String str_username = (c.getString(AllKeys.TAG_USER_USERNAME));
                            String str_email = (c.getString(AllKeys.TAG_USER_EMAIL));
                            String str_password = (c.getString(AllKeys.TAG_USER_PASSWORD));
                            String str_mobile = (c.getString(AllKeys.TAG_USER_MOBILE));
                            String str_birthdate = (c.getString(AllKeys.TAG_USER_BIRTHDATE));
                            String str_gender = (c.getString(AllKeys.TAG_USER_GENDER));
                            String str_bio = (c.getString(AllKeys.TAG_USER_BIO));
                            String str_last_login = (c.getString(AllKeys.TAG_USER_LASTLOGIN));
                            String str_verified_mobile = (c.getString(AllKeys.TAG_USER_VERIFIED_MOBILE));
                            String str_verified_email = (c.getString(AllKeys.TAG_USER_VERIFIED_EMAIL));

                            sessionmanager.setUserDetails(str_userid, str_firstname, str_lastname, str_avatar, str_username, str_email, str_password, str_mobile, str_birthdate, str_gender, str_bio, str_last_login, str_verified_mobile, str_verified_email,SIGNUP_TYPE);


                            Answers.getInstance().logSignUp(new SignUpEvent()
                                    .putMethod("Signup User")
                                    .putCustomAttribute("UserName : " , str_firstname)
                                    .putCustomAttribute("UserMobile : " , str_mobile)
                                    .putCustomAttribute("UserId : " , str_userid)
                                    .putCustomAttribute("SignUpType : " , SIGNUP_TYPE)
                                    .putCustomAttribute("UserEmail : " , str_email)

                                    .putSuccess(true));


                        }




                        Intent ii = new Intent(context, VerificationActivity.class);
                        startActivity(ii);
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                else
                {
                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof ServerError || error instanceof NetworkError)
                {
                                        hideDialog();
                }
                else {
                    sendUserDetailsToServer();
                }

              //  sendUserDetailsToServer();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("call_type","post");
                params.put("signup_type", SIGNUP_TYPE);

                //params.put("username", dbhandler.convertEncodedString(edtUsername.getText().toString()));

                if(SIGNUP_TYPE.equals("normal"))
                {

                    params.put("email", dbhandler.convertEncodedString(edtEmail.getText().toString()));
                    params.put("password", dbhandler.convertEncodedString(edtPassword.getText().toString()));
                    params.put("mobileno", edtMobile.getText().toString());
                    params.put("firstname", dbhandler.convertEncodedString(edtUsername.getText().toString()));
                    params.put("lastname", "");
                }
                else
                {
                    params.put("email", USER_EMAIL);
                    params.put("password", dbhandler.convertEncodedString(edtPassword.getText().toString()));//not using this parameter
                    params.put("mobileno", edtMobile.getText().toString());//not using this parameter
                    params.put("firstname", dbhandler.convertEncodedString(FIRST_NAME));
                    try {
                        params.put("lastname", dbhandler.convertEncodedString(LAST_NAME));
                    } catch (Exception e) {
                        params.put("lastname", "");
                        e.printStackTrace();
                    }
                    params.put("profile_url", dbhandler.convertEncodedString(USER_PROFILE_URL));
                    params.put("provider_userid", dbhandler.convertEncodedString(PROVIDER_USERID));


                }

               // params.put("password", dbhandler.convertEncodedString(BCrypt.hashpw(edtPassword.getText().toString(), BCrypt.gensalt())));
                //BCrypt.hashpw("ze@123", BCrypt.gensalt());

                params.put("gender", GENDER_ID);
                params.put("created_at", dbhandler.convertEncodedString(dbhandler.getDateTime()));
                params.put("updated_at", dbhandler.convertEncodedString(dbhandler.getDateTime()));
                params.put("device_type", "android");
                try {
                    MyFirebaseInstanceIDService mid= new MyFirebaseInstanceIDService();
                    params.put("fcm_tokenid", dbhandler.convertEncodedString(String.valueOf(mid.onTokenRefreshNew(context))));
                } catch (Exception e) {
                    params.put("fcm_tokenid", "");
                    e.printStackTrace();
                }



Log.d(TAG,"Params :"+params.toString());

                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(str_request_signup);


    }


    public void CallLoginIntent(View view) {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void showDialog() {
        if (!progress_dialog.isShowing()) {
            progress_dialog.show();

        }
    }

    private void hideDialog() {
        if (progress_dialog.isShowing()) {
            progress_dialog.dismiss();


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (login_fb_button.getText().toString().toLowerCase().equals("facebook")) {
            callbackManager.onActivityResult(requestCode, responseCode, data);
        }
        if (requestCode == SIGN_IN_CODE)
        {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                progress_dialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Log.d("GOOGLE SIGN IN", "handleSignInResult:" + result.isSuccess());
            // Signed in successfully, show authenticated UI.
           /* GoogleSignInAccount acct = result.getSignInAccount();
            UserUtil util = new UserUtil();
            SmartGoogleUser googleUser = util.populateGoogleUser(acct);*/


        }

    }


  /*  public void onClick(View v) {
        if (v == login_fb_button) {

            fackbook = login_fb_button.getText().toString();
            login.performClick();
        }
        if (v == login_google_button) {
            //signIn_btn.performClick();

            gPlusSignIn();
        }

    }*/

    //gmail here
    private void buidNewGoogleApiClient()
    {

        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

    }

    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    protected void onResume() {
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        try {
            if (!result.hasResolution())
            {
                google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!is_intent_inprogress) {

            connection_result = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();
        Intent in = new Intent(RegisterActivity.this, NewDashBoardActivity.class);
        /*sessionmanager.createstatusKEy("0");*/
        //startActivity(in);
        // finish();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();
    }

    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            Log.d("user connected", "connected");
            is_signInBtn_clicked = true;
            progress_dialog.show();
            resolveSignInError();

        }
    }

    private void getProfileInfo() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                setPersonalInfo(currentPerson);
                currentPerson.getDisplayName();
                String email = currentPerson.getId();
                Log.d("Emailid", email);


            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPersonalInfo(Person currentPerson) {

        try {
            String personName = currentPerson.getDisplayName();

            PROVIDER_USERID=currentPerson.getId();
            //FIRST_NAME = currentPerson.getDisplayName();
            //LAST_NAME = currentPerson.getNickname();
            String nbirthdate= currentPerson.getBirthday();
            FIRST_NAME = currentPerson.getName().getFamilyName();
            LAST_NAME= currentPerson.getName().getGivenName();
            String middlename = currentPerson.getName().getMiddleName();
            String bio = currentPerson.getAboutMe();

            try {
                USER_PROFILE_URL = String.valueOf(currentPerson.getImage().getUrl());
                if(USER_PROFILE_URL.contains("50"))
                {
                    USER_PROFILE_URL = USER_PROFILE_URL.replace("50","300");


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String genderValue = String.valueOf(currentPerson.getGender());
                if(genderValue.equals("male"))
                {
                    GENDER_ID="1";

                }
                else if(genderValue.equals("female"))
                {
                    GENDER_ID="0";

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            sessionmanager.setUserImageUrl(USER_PROFILE_URL);
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {


                }
            }
            USER_EMAIL = Plus.AccountApi.getAccountName(google_api_client);

            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            android.accounts.Account[] accounts = AccountManager.get(context).getAccountsByType(
                    "com.google");
            for (android.accounts.Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    String email12 = account.name;
                    Log.i("MY_EMAIL_count", "" + email12);
                }
            }

            //email12 = currentPerson.getId();

            Log.d("nameuser", personName);
            Log.d("emailuser", USER_EMAIL);
            ///gss

            sendUserDetailsToServer();
            progress_dialog.dismiss();

            // sessionmanager.createsavegmaildetails(personName,personPhotoUrl,email12);
            gPlusSignOut();
            gPlusRevokeAccess();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();
            google_api_client.connect();
        }
    }

    private void gPlusRevokeAccess() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            Plus.AccountApi.revokeAccessAndDisconnect(google_api_client)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.d("MainActivity", "User access revoked!");
                            buidNewGoogleApiClient();
                            google_api_client.connect();
                        }

                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    gPlusSignIn();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    //Complete Signup  with gmail and facebbok


}
