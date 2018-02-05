package com.yelona;

import android.*;
import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;



public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView custom_signup_button;
    private TextInputLayout usernameWrapper;
    private EditText userNameEditText;
    private TextInputLayout passwordWrapper;
    private EditText passwordEditText;
    private Button custom_signin_button;
    private TextView forgot_password;
    private String TAG = LoginActivity.class.getSimpleName();
    private Context context = this;
    private SpotsDialog pDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private TextView errorTextView;
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
    /*ProgressDialog progress_dialog;*/

    SpotsDialog progress_dialog;
    public static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS=100;

    public String LOGIN_TYPE="normal",USER_EMAIL="";
    private String PROVIDER_USERID="0";
    private Button custom_create_new_account;

    private static final int WRITE_EXTERNAL_STORAGE = 4;
    private static final int EXT_STORAGE_PERMISSION_REQ_CODE = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setTitle("Yelona Login");

        pDialog = new SpotsDialog(context);
        pDialog.setCancelable(false);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();

        progress_dialog = new SpotsDialog(context);
        progress_dialog.setCancelable(true);







        FacebookSdk.sdkInitialize(this.getApplicationContext());




        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);

        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
              //  userNameEditText.setText("zealtech9teen@gmail.com");
       // passwordEditText.setText("zeal1234");

        forgot_password = (TextView) findViewById(R.id.forgot_password);

        custom_signin_button = (Button) findViewById(R.id.custom_signin_button);

        custom_create_new_account = (Button)findViewById(R.id.custom_create_new_account);


        custom_create_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        errorTextView = (TextView) findViewById(R.id.errorTextView);
        errorTextView.setVisibility(View.GONE);




        login_fb_button = (AppCompatButton) findViewById(R.id.login_fb_button);
        login_google_button = (AppCompatButton) findViewById(R.id.login_google_button);







        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context , ForgotPassword.class);
                startActivity(intent);
                finish();

            }
        });

        login_fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGIN_TYPE="facebook";

                login.performClick();
            }
        });

        login_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGIN_TYPE="gmail";
                passwordEditText.setText("");
                userNameEditText.setText("");


                int currentAPIVersion = Build.VERSION.SDK_INT;
                if (currentAPIVersion >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.GET_ACCOUNTS},
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
      //  permissions.add("user_birthday");
      // permissions.add("user_friends");



        login.setReadPermissions(permissions);

        custom_signup_button = (TextView) findViewById(R.id.custom_signup_button);

        custom_signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });


        custom_signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGIN_TYPE="normal";

                errorTextView.setVisibility(View.GONE);

                boolean IsError = false;
                if (userNameEditText.getText().toString().equals("")) {
                    IsError = true;
                    usernameWrapper.setErrorEnabled(true);
                    usernameWrapper.setError("Enter Usermobile Or Email");
                } else {
                    usernameWrapper.setErrorEnabled(false);

                }

                if (passwordEditText.getText().toString().equals("")) {
                    IsError = true;
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError("Enter Password");
                } else {
                    passwordWrapper.setErrorEnabled(false);

                }
                if (IsError == false) {

                    sendLoginDetailsToServer();

                }

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() != null) {
                   /* share.setVisibility(View.INVISIBLE);
                    details.setVisibility(View.INVISIBLE);
                    profile.setProfileId(null);*/
                }
            }
        });

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

                passwordEditText.setText("");
                userNameEditText.setText("");


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
                            PROVIDER_USERID = fbUserId;

                            try {
                                USER_EMAIL=object.optString("email");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            String name=object.optString("name");
                            String fbUserProfilePics = "http://graph.facebook.com/" + fbUserId + "/picture?type=large";
                            Log.e(TAG,"Json Object Data "+object+" Email id "+ USER_EMAIL+" Name :"+name);




                            //http://graph.facebook.com//picture?type=large

                            //http://graph.facebook.com/800089893478559/picture?type=large

                            //http://graph.facebook.com/799197813567767/picture?type=large

                            sendLoginDetailsToServer();

                            sessionmanager.setUserImageUrl(fbUserProfilePics);
                            LoginManager.getInstance().logOut();


                        }


                    }
                });
                Bundle bundle=new Bundle();
                bundle.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();




                Intent in = new Intent(LoginActivity.this, NewDashBoardActivity.class);
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

     //   gPlusSignOut();
      //  gPlusRevokeAccess();

    }

    private void sendLoginDetailsToServer() {

        showDialog();

        String url_check_credentials = null;
        String fcm_tokenid="";
        try {
            MyFirebaseInstanceIDService mid= new MyFirebaseInstanceIDService();
            fcm_tokenid =String.valueOf(mid.onTokenRefreshNew(context));
        } catch (Exception e) {
            fcm_tokenid="";
            e.printStackTrace();
        }


        if(LOGIN_TYPE.equals("normal"))
        {
            url_check_credentials = AllKeys.WEBSITE + "LoginUser/get/"+ LOGIN_TYPE +"/" + dbhandler.convertEncodedString(userNameEditText.getText().toString()) + "/" + dbhandler.convertEncodedString(passwordEditText.getText().toString()) + "/"+ fcm_tokenid +"/"+ fcm_tokenid +"/android";
           url_check_credentials = AllKeys.WEBSITE + "LoginUser";
        }
        else
        {
            url_check_credentials = AllKeys.WEBSITE + "LoginUser/get/"+ LOGIN_TYPE +"/" + PROVIDER_USERID+"/"+ PROVIDER_USERID +"/"+ PROVIDER_USERID +"/"+ fcm_tokenid +"/android" ;
           url_check_credentials = AllKeys.WEBSITE + "LoginUser";
        }



        Log.d(TAG, "URL userLogin : " + url_check_credentials);

        StringRequest str_checkUser = new StringRequest(Request.Method.POST, url_check_credentials, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response : " + response.toString());

                if (response.contains("first_name"))
                {
                    try
                    {
                        response = response.replace("null","\"\"");
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
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

                            sessionmanager.setUserDetails(str_userid, str_firstname, str_lastname, str_avatar, str_username, str_email, str_password, str_mobile, str_birthdate, str_gender, str_bio, str_last_login, str_verified_mobile, str_verified_email,LOGIN_TYPE);



                            Answers.getInstance().logLogin(new LoginEvent()
                                    .putMethod("User Login")
                                    .putCustomAttribute("UserName : " , str_firstname)
                                    .putCustomAttribute("UserMobile : " , str_mobile)
                                    .putCustomAttribute("UserId : " , str_userid)
                                    .putCustomAttribute("UserEmail : " , str_email)
                                    .putCustomAttribute("LoginType : " , LOGIN_TYPE)
                                    .putSuccess(true));



                            hideDialog();
                            if(str_verified_mobile.equals("0"))
                            {

                                Intent ii = new Intent(context, VerificationActivity.class);
                                startActivity(ii);
                                finish();
                            }
                            else
                            {
                                sessionmanager.CheckSMSVerificationActivity("",
                                        "1");

                                Intent ii = new Intent(context, NewDashBoardActivity.class);
                                startActivity(ii);
                                finish();

                            }




                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("Error Info :",e.getMessage());
                        hideDialog();


                    }


                } else {
                    Toast.makeText(context, "" + response.toString(), Toast.LENGTH_SHORT).show();
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(response);
                    hideDialog();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(error instanceof ServerError || error instanceof NetworkError){
                    hideDialog();
                } else {
                    sendLoginDetailsToServer();

                }

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String,String>();

                params.put("call_type","post");
                params.put("login_type",LOGIN_TYPE);
                if(LOGIN_TYPE.equals("gmail"))
                {
                    params.put("username",dbhandler.convertEncodedString(USER_EMAIL));
                }
                else
                {
                    params.put("username",dbhandler.convertEncodedString(userNameEditText.getText().toString()));
                }

                //params.put("password", dbhandler.convertEncodedString(BCrypt.hashpw(passwordEditText.getText().toString(), BCrypt.gensalt())));
                params.put("password", dbhandler.convertEncodedString(passwordEditText.getText().toString()));
                params.put("user_provider_id",PROVIDER_USERID);
                params.put("device_type","android");

                try {
                    MyFirebaseInstanceIDService mid= new MyFirebaseInstanceIDService();
                    params.put("fcm_tokenid", dbhandler.convertEncodedString(String.valueOf(mid.onTokenRefreshNew(context))));
                   // params.put("fcm_tokenid", "saaa");
                } catch (Exception e) {
                    params.put("fcm_tokenid", "");
                    e.printStackTrace();
                }

                Log.d(TAG,"Login  Params : "+params.toString());


                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(str_checkUser);


    }

    public void showDialog() {

        if (!pDialog.isShowing()) {

            pDialog.show();
        }


    }

    public void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();

        }
    }


    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    void deleteFacebookApplication() {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                boolean isSuccess = false;
                try {
                    isSuccess = response.getJSONObject().getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess && response.getError() == null) {
                    // Application deleted from Facebook account
                }

            }
        }).executeAsync();
    }



    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
//64206,-1,dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.yelona-2/base.apk", zip file "/data/app/com.yelona-2/split_lib_dependencies_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_0_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_1_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_2_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_3_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_4_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_5_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_6_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_7_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_8_apk.apk", zip file "/data/app/com.yelona-2/split_lib_slice_9_apk.apk"],nativeLibraryDirectories=[/data/app/com.yelona-2/lib/arm, /system/lib, /vendor/lib]]]
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
        Intent in = new Intent(LoginActivity.this, NewDashBoardActivity.class);
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
            String personPhotoUrl = currentPerson.getImage().getUrl();
            PROVIDER_USERID=currentPerson.getId();

            sessionmanager.setUserImageUrl(personPhotoUrl);
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

            sendLoginDetailsToServer();
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
