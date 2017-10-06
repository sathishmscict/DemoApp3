package studios.codelight.smartloginlibrary.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import studios.codelight.smartloginlibrary.R;
import studios.codelight.smartloginlibrary.SmartCustomLogoutListener;
import studios.codelight.smartloginlibrary.SmartLoginConfig;
import studios.codelight.smartloginlibrary.users.SmartFacebookUser;
import studios.codelight.smartloginlibrary.users.SmartGoogleUser;
import studios.codelight.smartloginlibrary.users.SmartUser;
import studios.codelight.smartloginlibrary.util.DialogUtil;

/**
 * Copyright (c) 2016 Codelight Studios
 * Created by Kalyan on 9/29/2015.
 */
public class UserSessionManager {

    static final String USER_SESSION = "user_session_key";
    static final String USER_PREFS = "codelight_studios_user_prefs";
    static final String DEFAULT_SESSION_VALUE = "No logged in user";

    /**
        This static method can be called to get the logged in user.
        It reads from the shared preferences and builds a SmartUser object and returns it.
        If no user is logged in it returns null
    */
    public static SmartUser getCurrentUser(Context context){
        SmartUser smartUser = null;
        SharedPreferences preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String sessionUser = preferences.getString(USER_SESSION, DEFAULT_SESSION_VALUE);
        String user_type = preferences.getString(SmartLoginConfig.USER_TYPE, SmartLoginConfig.CUSTOMUSERFLAG);
        if(!sessionUser.equals(DEFAULT_SESSION_VALUE)){
            try {
                switch (user_type) {
                    case SmartLoginConfig.FACEBOOKFLAG:
                        smartUser = gson.fromJson(sessionUser, SmartFacebookUser.class);
                        break;
                    case SmartLoginConfig.GOOGLEFLAG:
                        smartUser = gson.fromJson(sessionUser, SmartGoogleUser.class);
                        break;
                    default:
                        smartUser = gson.fromJson(sessionUser, SmartUser.class);
                        break;
                }
            }catch (Exception e){
                Log.e("GSON", e.getMessage());
            }
        }
        return smartUser;
    }

    /**
        This method sets the session object for the current logged in user.
        This is called from inside the SmartLoginActivity to save the
        current logged in user to the shared preferences.
    */
    public boolean setUserSession(Context context, SmartUser smartUser){
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        if(smartUser != null) {
            try {
                preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                editor = preferences.edit();

                if (smartUser instanceof SmartFacebookUser) {
                    editor.putString(SmartLoginConfig.USER_TYPE, SmartLoginConfig.FACEBOOKFLAG);
                } else if (smartUser instanceof SmartGoogleUser) {
                    editor.putString(SmartLoginConfig.USER_TYPE, SmartLoginConfig.GOOGLEFLAG);
                } else {
                    editor.putString(SmartLoginConfig.USER_TYPE, SmartLoginConfig.CUSTOMUSERFLAG);
                }

                Gson gson = new Gson();
                smartUser.setPassword(null);
                String sessionUser = gson.toJson(smartUser);
                Log.d("GSON", sessionUser);
                editor.putString(USER_SESSION, sessionUser);
                editor.apply();
                return true;
            } catch (Exception e) {
                Log.e("Session Error", e.getMessage());
                return false;
            }
        } else {
            Log.e("Session Error", "User is null");
            return false;
        }
    }

    /**
        This static method logs out the user that is logged in.
        This implements facebook and google logout.
        Custom user logout is left to the user.
        It also removes the preference entries.
    */
    static SharedPreferences.Editor editor;
    public static boolean logout(Activity context, SmartUser user, SmartCustomLogoutListener smartCustomLogoutListener, GoogleApiClient googleApiClient){
        SharedPreferences preferences;
        try {
            preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            editor = preferences.edit();

            try {
                String user_type = preferences.getString(SmartLoginConfig.USER_TYPE, SmartLoginConfig.CUSTOMUSERFLAG);
                switch (user_type) {
                    case SmartLoginConfig.FACEBOOKFLAG:
                        LoginManager.getInstance().logOut();
                        editor.remove(SmartLoginConfig.USER_TYPE);
                        editor.remove(USER_SESSION);
                        editor.apply();
                        return true;
                    case SmartLoginConfig.GOOGLEFLAG:
                        if(googleApiClient == null || !googleApiClient.isConnected()) {
                            Toast.makeText(context, "GoogleApiClient should be connected.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        final boolean[] logoutStatus = {false};
                        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if(status.isSuccess()) {
                                    editor.remove(SmartLoginConfig.USER_TYPE);
                                    editor.remove(USER_SESSION);
                                    editor.apply();
                                    logoutStatus[0] = true;
                                }
                            }
                        });
                        return logoutStatus[0];
                    case SmartLoginConfig.CUSTOMUSERFLAG:
                        if(!smartCustomLogoutListener.customUserSignout(user)){
                            throw new Exception("User not logged out");
                        }
                        return true;
                    default:
                        return false;
                }
            } catch (Exception e){
                Log.e("User Logout Error", e.getMessage());
                DialogUtil.getErrorDialog(R.string.network_error, context).show();
                return false;
            }

        } catch (Exception e){
            Log.e("User Logout Error", e.getMessage());
            return false;
        }
    }
}
