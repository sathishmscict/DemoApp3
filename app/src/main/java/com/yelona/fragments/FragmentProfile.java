package com.yelona.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wang.avi.AVLoadingIndicatorView;
import com.yelona.DisplayOrdersActivity;
import com.yelona.ManageShippingAddressActivity;
import com.yelona.R;
import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.helper.ImageUtils;
import com.yelona.helper.NetConnectivity;
import com.yelona.helper.Utility;
import com.yelona.session.SessionManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;


public class FragmentProfile extends Fragment {


    private Context context = getActivity();
    private String gender;
    private String maritalstatus;
    private String name;
    private String email;
    private String dob;
    private String pincode;


    private TextView txtname;
    private TextView txtmobile;
    private TextView txtemail;

    ArrayAdapter<String> occuptionadapter;

    //private EditText txtpincode;
    private static EditText edtdob;
    //private Spinner spnEducation;
    //private Spinner spnOccupation;
    //private Spinner spnCountry;
    private Spinner spnState;
    private Spinner spnCity;
    //private Spinner spnIncome;
    //private Spinner spnIndustry;
    private String DEFAULTPOINTS = "0";
    private String REFERALPOINTS = "0";
    private String REFERALCAPPING = "0";
    private String REFERALLIMIT = "0";

    private String PAYABLEAMOUNT = "0";

    String NAME, MOBILE, EMAIL, GENDER, PINCODE, DOB, EDUCATION, OCCUPATION,
            COUNTRY, STATE, CITY, INCOME, INDUSTRY, MARITALSTATUS;

    List<String> countryname = new ArrayList<String>();
    List<String> countryid = new ArrayList<String>();
    List<String> stateid = new ArrayList<String>();
    List<String> statename = new ArrayList<String>();
    List<String> cityid = new ArrayList<String>();
    List<String> cityname = new ArrayList<String>();
    List<String> educationid = new ArrayList<String>();
    List<String> educationname = new ArrayList<String>();
    List<String> occupationid = new ArrayList<String>();
    List<String> occupationname = new ArrayList<String>();
    List<String> incomeid = new ArrayList<String>();
    List<String> incomename = new ArrayList<String>();
    List<String> industryid = new ArrayList<String>();
    List<String> industryname = new ArrayList<String>();

    private dbhandler db;
    private SQLiteDatabase sd;
    private TextView txtnext;
    private String query;
    private Cursor c;
    private String SELECTEDCOUNTRY = "0";
    private String SELECTEDCITY = "0";
    private String SELECTEDSTATE = "0";
    private String SELECTEDEDUCATION = "0";
    private String SELECTEDOCCUPATION = "0";
    private String SELECTEDINCOME = "0";
    private String SELECTEDINDUSTRY = "0";

    ArrayAdapter<String> educationadapter;

    private String deviceid;
    private SessionManager sessionManager;
    private HashMap<String, String> userdetails;
    private static int y;
    private static int m;
    private static int d;
    private int spm;
    private static String startDate;
    private String showsdate;
    static final int DATE_PICKER_ID = 1111;
    private int uid;
    private String vresval;
    private int ID = 0;
    private TextView txterror;


    private String COUNTRYFLAG = "";
    private String STATEFLAG = "";
    private EditText txtgender;
    //private TextView txtlogout;
    //private TextView txtupdate;
    private TextView txtslider;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    private TextView txttitle;


    private ArrayList<String> lstStateid = new ArrayList<String>();
    private ArrayList<String> lstStatename = new ArrayList<String>();

    private ArrayList<String> lstCityid = new ArrayList<String>();
    private ArrayList<String> lstCityname = new ArrayList<String>();
    private String SELECTEDSTATEID = "0";


    private static boolean DATE_DOB = false;
    private ImageView imgProfilePic;
    private String userChoosenTask;


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap bitmap;
    private MenuItem profile_edit, profile_update;
    private LinearLayout llname, llemail;
    private EditText txtname_edit;
    private EditText txtemail_edit;
    private String TAG = FragmentProfile.class.getSimpleName();

    private ArrayList<Integer> listOccupationId = new ArrayList<Integer>();
    private ArrayList<String> listOccupation = new ArrayList<String>();

    private ArrayList<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();
    private SpotsDialog pDialog;
    private EditText edtBio;
    private LinearLayout llDOB;
    private LinearLayout llBio;
    private RadioGroup rdGrpGender;
    private CardView cvOrders;
    private TextView txtOrders;
    private String GENDER_ID;
    private TextInputLayout edtDobWrapper;
    private TextInputLayout txtname_editWrapper;
    private TextInputLayout txtemail_editWrapper;
    private RadioButton rdMale, rdFemale;
    private LinearLayout mRoot;
    private CardView cvAddresses;
    private TextView txtAddresses;
    private CardView cvUpdatePasswprd;
    private TextInputLayout edtOldPasswordWrapper;
    private EditText edtConfirmPassword;
    public TextInputLayout edtConfirmPasswordWrapper;
    public EditText edtNewPassword;
    public TextInputLayout edtNewPasswordWrapper;
    public EditText edtOldPassword;
    public Button btnUpdate;
    public LinearLayout llProcess;
    private AVLoadingIndicatorView progress;

    public TextView txtStatus;
    private LinearLayout llUpdateUI;
    private String BASE64STRING="";


    //private boolean IsUpdatePassword = false;

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        sessionManager = new SessionManager(getActivity());
        userdetails = new HashMap<String, String>();

        userdetails = sessionManager.getSessionDetails();

        db = new dbhandler(getActivity());
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        pDialog = new SpotsDialog(getActivity());
        pDialog.setCancelable(false);

        //Getting IMEI No. from  current device

        //TelephonyManager mngr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //deviceid = mngr.getDeviceId();

        mRoot = (LinearLayout) rootView.findViewById(R.id.llmain);

        txtname = (TextView) rootView.findViewById(R.id.txtname);
        txtemail = (TextView) rootView.findViewById(R.id.txtemail);


        llname = (LinearLayout) rootView.findViewById(R.id.llname);
        llemail = (LinearLayout) rootView.findViewById(R.id.llemail);
        llDOB = (LinearLayout) rootView.findViewById(R.id.llDOB);
        llBio = (LinearLayout) rootView.findViewById(R.id.llBio);
        rdGrpGender = (RadioGroup) rootView.findViewById(R.id.rdGrpGender);
        rdMale = (RadioButton) rootView.findViewById(R.id.rdMale);
        rdFemale = (RadioButton) rootView.findViewById(R.id.rdFemale);

        cvUpdatePasswprd = (CardView) rootView.findViewById(R.id.cvUpdatePasswprd);

        btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();


                boolean IsError = false;
                if (edtOldPassword.getText().toString().equals("")) {
                    IsError = true;
                    edtOldPasswordWrapper.setErrorEnabled(true);
                    edtOldPasswordWrapper.setError("Enter Old Password");
                } else {
                    edtOldPasswordWrapper.setErrorEnabled(false);

                }

                if (edtNewPassword.getText().toString().equals("")) {
                    IsError = true;
                    edtNewPasswordWrapper.setErrorEnabled(true);
                    edtNewPasswordWrapper.setError("Enter New Password");
                } else {
                    edtNewPasswordWrapper.setErrorEnabled(false);

                }


                if (edtConfirmPassword.getText().toString().equals("")) {
                    IsError = true;
                    edtConfirmPasswordWrapper.setErrorEnabled(true);
                    edtConfirmPasswordWrapper.setError("Enter Confirm Password");
                } else {
                    edtConfirmPasswordWrapper.setErrorEnabled(false);

                }


                if (IsError == false)
                {

                    if (!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                        txtStatus.setText("New Password and Confirm Password Mismatched");
                        txtStatus.setVisibility(View.VISIBLE);

                    } else {


                        //Send Details To Server
                        progress.setVisibility(View.VISIBLE);
                        txtStatus.setText("Please wait...");
                        String url_updatePassword = AllKeys.WEBSITE + "udpatePassword/" + userdetails.get(SessionManager.KEY_USERID) + "/" + dbhandler.convertEncodedString(edtOldPassword.getText().toString()) + "/" + dbhandler.convertEncodedString(edtNewPassword.getText().toString()) + "";
                        Log.d(TAG, "URL Update Password  : " + url_updatePassword);
                        StringRequest request = new StringRequest(Request.Method.GET, url_updatePassword, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d(TAG, "UpdatePassword Response : " + response);
                                if (response.contains("success")) {

                                    llProcess.setVisibility(View.GONE);
                                    txtStatus.setText("");
                                    llUpdateUI.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();


                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                    alertDialogBuilder.setTitle("Password Info ");
                                    alertDialogBuilder.setMessage("Password has been updated successfully");
                                    alertDialogBuilder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int arg1) {

                                                    dialog.cancel();
                                                    dialog.dismiss();
                                                    //txtStatus.setVisibility(View.GONE);
                                                    btnUpdate.setVisibility(View.GONE);

                                                }
                                            });


                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();


                                } else {

                                    txtStatus.setText(response);
                                    progress.setVisibility(View.GONE);
                                    txtStatus.setVisibility(View.VISIBLE);

                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.d(TAG, "ERror in Update Password : " + error.getMessage());

                            }
                        });
                        MyApplication.getInstance().addToRequestQueue(request);


                    }


                } else {
                    progress.setVisibility(View.GONE);
                    //txtStatus.setVisibility(View.VISIBLE);
                   //txtStatus.setText("Sorry,Try again...");

                }


            }
        });


        llUpdateUI = (LinearLayout) rootView.findViewById(R.id.llupdateUI);
        edtOldPasswordWrapper = (TextInputLayout) rootView.findViewById(R.id.edtOldPasswordWrapper);
        edtOldPassword = (EditText) rootView.findViewById(R.id.edtOldPassword);

        edtNewPasswordWrapper = (TextInputLayout) rootView.findViewById(R.id.edtNewPasswordWrapper);
        edtNewPassword = (EditText) rootView.findViewById(R.id.edtNewPassword);

        edtConfirmPasswordWrapper = (TextInputLayout) rootView.findViewById(R.id.edtConfirmPasswordWrapper);
        edtConfirmPassword = (EditText) rootView.findViewById(R.id.edtConfirmPassword);


        llProcess = (LinearLayout) rootView.findViewById(R.id.llProcess);
        txtStatus = (TextView) rootView.findViewById(R.id.txtStatus);

        progress = (AVLoadingIndicatorView) rootView.findViewById(R.id.progress);

        llUpdateUI.setVisibility(View.GONE);

        btnUpdate.setVisibility(View.GONE);
        //  cvUpdatePasswprd.setVisibility(View.GONE);

        cvUpdatePasswprd.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {


                btnUpdate.setVisibility(View.VISIBLE);

                llUpdateUI.setVisibility(View.VISIBLE);

                txtStatus.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);


               /* profile_update.setVisible(true);
                profile_edit.setVisible(false);*/



               /* final Dialog dialog = new Dialog(getActivity());
               // dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                dialog.setContentView(R.layout.update_update_password);

                dialog.setTitle("Update Password");
*/

                try {
                    LinearLayout llmain = (LinearLayout) rootView.findViewById(R.id.llupdateUI);
                    llmain.setVisibility(LinearLayout.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                    animation.setDuration(1000);
                    llmain.setAnimation(animation);
                    llmain.animate();
                    animation.start();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }




              /*  progress.setVisibility(View.GONE);
                txtStatus.setVisibility(View.GONE);*/


//                txtgoaltype.setText("GOAL TYPE : " + GOAL_TYPE);


                // dialog.setCancelable(false);


//                dialog.getWindow().setLayout(width, height);
                // dialog.getWindow().setLayout(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                //  dialog.show();


            }
        });


        if (userdetails.get(SessionManager.KEY_USER_GENDER).equals("1")) {


            GENDER_ID = "1";
            rdMale.setChecked(true);
        } else if (userdetails.get(SessionManager.KEY_USER_GENDER).equals("0")) {
            rdFemale.setChecked(true);
            GENDER_ID = "2";
        }


        rdGrpGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.rdMale) {
                    GENDER_ID = "1";

                } else if (checkedId == R.id.rdFemale) {

                    GENDER_ID = "2";
                }
            }
        });

        cvOrders = (CardView) rootView.findViewById(R.id.cvOrders);
        cvAddresses = (CardView) rootView.findViewById(R.id.cvAddresses);


        txtOrders = (TextView) rootView.findViewById(R.id.txtOrders);
        txtAddresses = (TextView) rootView.findViewById(R.id.txtAddresses);


        txtOrders.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), DisplayOrdersActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();


            }
        });

        txtAddresses.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ManageShippingAddressActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();


            }
        });


        txtname_edit = (EditText) rootView.findViewById(R.id.txtname_edit);
        txtemail_edit = (EditText) rootView.findViewById(R.id.txtemail_edit);


        txtmobile = (TextView) rootView.findViewById(R.id.txtmobile);


        txtgender = (EditText) rootView.findViewById(R.id.txtgender);

        //RdMarital = (RadioGroup)rootView.findViewById(R.id.radioGroup2);
        edtdob = (EditText) rootView.findViewById(R.id.edtdob);
        edtDobWrapper = (TextInputLayout) rootView.findViewById(R.id.edtDobWrapper);

        edtBio = (EditText) rootView.findViewById(R.id.edtbio);

        txtname_editWrapper = (TextInputLayout) rootView.findViewById(R.id.txtname_editWrapper);
        txtemail_editWrapper = (TextInputLayout) rootView.findViewById(R.id.txtemail_editWrapper);

        if (!userdetails.get(SessionManager.KEY_USER_BIO).equals("") && !userdetails.get(SessionManager.KEY_USER_BIO).equals("null")) {
            edtBio.setText(userdetails.get(SessionManager.KEY_USER_BIO));
        }






        /*txtcoursestartdate.setText("" + userdetails.get(SessionManager.KEY_COURSE_START_DATE));
        txtcourseenddate.setText("" + userdetails.get(SessionManager.KEY_COURSE_END_DATE));*/


        if (!userdetails.get(SessionManager.KEY_USER_DOB).equals("null")) {
            edtdob.setText("" + userdetails.get(SessionManager.KEY_USER_DOB));
        }

        //txtdoa.setText("" + userdetails.get(SessionManager.KEY_US));


        txtslider = (TextView) rootView.findViewById(R.id.txtslider);

        txttitle = (TextView) rootView.findViewById(R.id.textView3);

        imgProfilePic = (ImageView) rootView.findViewById(R.id.imgProfilePic);

        HideControls();

        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SetUserProfilePictireFromBase64EnodedString();

        //	txtlogout = (TextView)rootView.findViewById(R.id.txtlogout);

        //txtupdate = (TextView)rootView.findViewById(R.id.txtnext);
        //rdsingle = (RadioButton)rootView.findViewById(R.id.rdsingle);
        //rdmarried = (RadioButton)rootView.findViewById(R.id.rdmarried);


        //spnEducation = (Spinner)rootView.findViewById(R.id.spnEducation);

        //spnOccupation = (Spinner)rootView.findViewById(R.id.spnOccupation);
        //spnCountry = (Spinner)rootView.findViewById(R.id.spnContry);
        //spnState = (Spinner)rootView.findViewById(R.id.spnState);
        //spnCity = (Spinner)rootView.findViewById(R.id.spnCity);
        //spnIncome = (Spinner)rootView.findViewById(R.id.spnIncome);
        //spnIndustry = (Spinner)rootView.findViewById(R.id.spnIndustry);

        txterror = (TextView) rootView.findViewById(R.id.txterror);
        //txtpincode = (EditText)rootView.findViewById(R.id.txtpostalcode);

        txtname.setText("" + userdetails.get(SessionManager.KEY_USER_FIRSTNAME));
        txtmobile.setText("+91 " + userdetails.get(SessionManager.KEY_USER_MOBILE));
        txtemail.setText("" + userdetails.get(SessionManager.KEY_USER_EMAIL));
        /*txtphoneno.setText("" + userdetails.get(SessionManager.KEY_PHONENO));*/


        //	txtpincode.setText("" + PINCODE);

        //deviceid = Settings.Secure.getString(context.getContentResolver(),
        //Settings.Secure.ANDROID_ID);

        // txtnext = (TextView)findViewById(R.id.txtnext);

        final Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);

        spm = m + 1;
        if (spm <= 9) {
            String mm = "0" + spm;
            startDate = y + "-" + mm + "-" + d;
            showsdate = y + "-" + mm + "-" + d;
            if (y == cal.get(Calendar.YEAR)) {
                y = 1995;
            }
        } else {
            startDate = y + "-" + spm + "-" + d;
            showsdate = y + "-" + "d" + "-" + spm;
            if (y == cal.get(Calendar.YEAR)) {
                y = 1995;
            }


        }

        edtdob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //your code
                        DATE_DOB = true;
                        //  getActivity().showDialog(DATE_PICKER_ID);

                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getFragmentManager(), "datePicker");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return false;
            }
        });
      /*  txtdob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                DATE_DOB = true;

                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");


				getActivity().showDialog(DATE_PICKER_ID);


            }
        });
*/







/*
        txtupdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				*/
/*
                 * int selectedid = RdGender.getCheckedRadioButtonId();
				 * RadioButton rd = (RadioButton)findViewById(selectedid);
				 *//*


				if (!txtgender.getText().toString().equals("")
						&& !txtmobile.getText().toString().equals("")
						&& !txtemail.getText().toString().equals("")
						&& !txtpincode.getText().toString().equals("")) {

					if (
							 !SELECTEDSTATE.equals("0")
							) {

						gender = "" + txtgender.getText().toString();

						*/
/*int selectedid = RdMarital.getCheckedRadioButtonId();
                        RadioButton marital = (RadioButton)rootView.findViewById(selectedid);
						
						maritalstatus = marital.getText().toString();*//*


						name = "" + txtname.getText().toString();
						if (MOBILE.length() != 10) {
							MOBILE = "" + txtmobile.getText().toString();
						}

						email = "" + txtemail.getText().toString();
						dob = "" + txtdob.getText().toString();
						pincode = "" + txtpincode.getText().toString();

						new UserProfileUpdate().execute();

					} else {

						if (SELECTEDSTATE.equals("0")) {
							Toast.makeText(getActivity(), "Please Select State",
									Toast.LENGTH_SHORT).show();
						}


					}

				} else {
					Toast.makeText(getActivity(), "All Fields Compulsary",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
*/






/*


		spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				SELECTEDSTATE = stateid.get(position);
				// Toast.makeText(context, "STATE ID :" + SELECTEDSTATE,
				// Toast.LENGTH_SHORT).show();

				STATEFLAG = "true";

				if (SELECTEDSTATE != "0") {
					//new GetAllCityDetails().execute();

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				SELECTEDCITY = cityid.get(position);
				// Toast.makeText(context, "CITY ID :" + SELECTEDCITY,
				// Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});*/


        /**
         * Set Profile Picture
         */
        imgProfilePic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                selectImage();

            }
        });


        /**
         * Get Occupation master details from server
         *
         */
        if (NetConnectivity.isOnline(getActivity())) {

            //GetOccupationMasterDetailsFromServer();
        } else {
            //FillOccupationspinnerData();
        }


        // Inflate the layout for this fragment
        return rootView;
    }


    private void HideControls() {

        edtdob.setEnabled(false);

        edtBio.setEnabled(false);


        llname.setVisibility(View.GONE);
        llemail.setVisibility(View.GONE);
        llBio.setVisibility(View.GONE);
        llDOB.setVisibility(View.GONE);
        rdGrpGender.setVisibility(View.GONE);
        cvOrders.setVisibility(View.VISIBLE);
        cvAddresses.setVisibility(View.VISIBLE);
        cvUpdatePasswprd.setVisibility(View.VISIBLE);


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null)

            result += line;

        inputStream.close();
        return result;
    }

    //Get State and City Master Details From Server

   /* private class GetAllStateDetails extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;

        private JSONArray allstates;


        String statejsonStr = "";


        private String state_url = AllKeys.TAG_YOURCARE_WEBSITE
                + "GetStatesData?action=state&countryid=15";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response


            statejsonStr = sh.makeServiceCall(state_url, ServiceHandler.GET);
            if (statejsonStr.contains("stateid")) {
                statejsonStr = dbhandler.convertToJsonFormat(statejsonStr);


            }

            Log.d("State Response: ", "> " + statejsonStr);

            if (statejsonStr != null && statejsonStr != "") {
                try {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(statejsonStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Getting JSON Array node
                    allstates = jsonObj.getJSONArray("data");

                    Log.d("State Count : ", "" + allstates.length());

                    lstStateid.clear();
                    lstStatename.clear();
                    // looping through All Contacts
                    for (int i = 0; i < allstates.length(); i++) {
                        JSONObject c = allstates.getJSONObject(i);

                        // Insert Data into the table
                        try {

                            ContentValues cv = new ContentValues();
                            int stateid = c.getInt("stateid");
                            String statename = c.getString("name");
                            lstStateid.add("" + stateid);
                            lstStatename.add(statename);

                            Log.d("STATEID : " + stateid, "STATE NAME : " + statename);


                        } catch (Exception e) {

                            Log.d("Error in statejson", e.getMessage());
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            // Complete Getting State details

            ArrayAdapter<String> stateadapter = new ArrayAdapter<String>(
                    context, android.R.layout.simple_spinner_dropdown_item,
                    lstStatename);

            spnState.setAdapter(stateadapter);

            try {

                if (SELECTEDSTATEID.equals("0")) {
                    SELECTEDSTATEID = "5";
                }
                int stateidpos = lstStateid.indexOf(SELECTEDSTATEID);
                String statenamepos = lstStatename.get(stateidpos);
                if (!statenamepos.equals(null)) {
                    int spinnerPositionn = stateadapter
                            .getPosition(statenamepos);
                    // String spinnerPosition = countryname.get(countrypos);
                    spnState.setSelection(spinnerPositionn);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("Error :" + e.getMessage());
            }


        }
    }

*/
    //Complete Getting State and city master details from server




/*
    protected Dialog onCreateDialog(int id) {
        switch (id) {
			case DATE_PICKER_ID:

				// open datepicker dialog.
				// set date picker for current date
				// add pickerListener listner to date picker
				return new DatePickerDialog(getActivity(), pickerListener, y, m, d);
		}
		return null;
	}

*/
    /*private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

		public String showdate;

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
							  int selectedMonth, int selectedDay) {

			y = selectedYear;
			m = selectedMonth;
			d = selectedDay;

			// Show selected date

			String dd="";
			if(d<=9)
			{
				dd="0"+d;
			}
			else
			{
				dd=""+d;

			}


			//   String startDate;
			int spm = m + 1;
			if (spm <= 9) {
				String mm = "0" + spm;
				startDate = dd + "-" + mm + "-" + y;
				//startDate = AllKeys.convertToJsonDateFormat(startDate);
				showdate = dd + "-" + mm + "-" + y;
			} else {
				startDate = y + "-" + spm + "-" + dd;
				//startDate = AllKeys.convertToJsonDateFormat(startDate);
				showdate = dd + "-" + spm + "-" + y;
			}
			txtdob.setText(showdate);

		}
	};*/

    @SuppressLint("ValidFragment")
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        private String showdate = "";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

// Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {

            y = year;
            m = month;
            d = day;

            // Show selected date

            String dd = "";
            if (d <= 9) {
                dd = "0" + d;
            } else {
                dd = "" + d;

            }


            //   String startDate;
            int spm = m + 1;
            if (spm <= 9) {
                String mm = "0" + spm;
                startDate = dd + "-" + mm + "-" + y;
                //startDate = AllKeys.convertToJsonDateFormat(startDate);
                showdate = dd + "-" + mm + "-" + y;
            } else {
                startDate = y + "-" + spm + "-" + dd;
                //startDate = AllKeys.convertToJsonDateFormat(startDate);
                showdate = dd + "-" + spm + "-" + y;
            }
            if (DATE_DOB == true) {

                edtdob.setText(showdate);
            }

        }
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        //inflater.inflate(R.menu.menu_profile, menu);


        super.onCreateOptionsMenu(menu, inflater);


        try {
            getActivity().getMenuInflater().inflate(R.menu.menu_profile, menu);


            profile_edit = (MenuItem) menu.findItem(R.id.action_edit);

            profile_update = (MenuItem) menu.findItem(R.id.action_update);

            profile_update.setVisible(false);


            if (userdetails.get(SessionManager.KEY_USER_FIRSTNAME).equals("")) {
                profile_update.setVisible(true);
                profile_edit.setVisible(false);

                profile_update.setVisible(true);
                profile_edit.setVisible(false);
                ShowAllControlsEditableAndVisible();

            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement


        if (id == R.id.action_edit) {
            {


                //  IsUpdatePassword = false;
                profile_update.setVisible(true);
                profile_edit.setVisible(false);

                ShowAllControlsEditableAndVisible();

            }

        }

        if (id == R.id.action_update) {

            if (NetConnectivity.isOnline(getActivity())) {
                try {


                  /*  if (IsUpdatePassword == false)
                    {
*/
                    Boolean isError = false;

                    if (edtdob.getText().toString().equals("")) {

                        isError = true;
                        edtDobWrapper.setErrorEnabled(true);
                        edtDobWrapper.setError("Enter DateOfBirth");
                    } else {
                        edtDobWrapper.setErrorEnabled(false);

                    }

                    if (txtname_edit.getText().toString().equals("")) {
                        isError = true;
                        txtname_editWrapper.setErrorEnabled(true);
                        txtname_editWrapper.setError("Enter Name");
                    } else {

                        txtname_editWrapper.setErrorEnabled(false);
                    }


                    if (txtemail_edit.getText().toString().equals("")) {

                        isError = true;
                        txtemail_editWrapper.setErrorEnabled(true);
                        txtemail_editWrapper.setError("Please Enter Email");

                    } else {
                        txtemail_editWrapper.setErrorEnabled(false);

                        if (AllKeys.checkEmail(txtemail_edit.getText().toString())) {
                            txtemail_editWrapper.setErrorEnabled(false);
                        } else {
                            isError = true;
                            txtemail_editWrapper.setErrorEnabled(true);
                            txtemail_editWrapper.setError("Invalid Email");

                        }
                    }


                    //new SendProfileDetailsToServer().execute();


                    if (isError == false) {
                        sendProfileUpdateDetialsToServer();
                    }

                  /*  } else {*/

                    //Update Password
/*

                        boolean IsError = false;
                        if (edtOldPassword.getText().toString().equals("")) {
                            IsError = true;
                            edtOldPasswordWrapper.setErrorEnabled(true);
                            edtOldPasswordWrapper.setError("Enter Old Password");
                        } else {
                            edtOldPasswordWrapper.setErrorEnabled(false);

                        }

                        if (edtNewPassword.getText().toString().equals("")) {
                            IsError = true;
                            edtNewPasswordWrapper.setErrorEnabled(true);
                            edtNewPasswordWrapper.setError("Enter New Password");
                        } else {
                            edtNewPasswordWrapper.setErrorEnabled(false);

                        }


                        if (edtConfirmPassword.getText().toString().equals("")) {
                            IsError = true;
                            edtConfirmPasswordWrapper.setErrorEnabled(true);
                            edtConfirmPasswordWrapper.setError("Enter Confirm Password");
                        } else {
                            edtConfirmPasswordWrapper.setErrorEnabled(false);

                        }


                        if (IsError == false) {

                            if (!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                                txtStatus.setText("New Password and Confirm Password Mismatched");
                                txtStatus.setVisibility(View.VISIBLE);

                            } else {


                                //Send Details To Server
                                progress.setVisibility(View.VISIBLE);
                                txtStatus.setText("Please wait...");
                                String url_updatePassword = AllKeys.WEBSITE + "udpatePassword/" + userdetails.get(SessionManager.KEY_USERID) + "/" + dbhandler.convertEncodedString(edtOldPassword.getText().toString()) + "/" + dbhandler.convertEncodedString(edtNewPassword.getText().toString()) + "";
                                Log.d(TAG, "URL Update Password  : " + url_updatePassword);
                                StringRequest request = new StringRequest(Request.Method.GET, url_updatePassword, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.d(TAG, "UpdatePassword Response : " + response);
                                        if (response.contains("success")) {

                                            llProcess.setVisibility(View.GONE);
                                            txtStatus.setText("");
                                            llUpdateUI.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();


                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                            alertDialogBuilder.setTitle("Password Info ");
                                            alertDialogBuilder.setMessage("Password has been updated successfully");
                                            alertDialogBuilder.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int arg1) {

                                                            dialog.cancel();
                                                            dialog.dismiss();

                                                        }
                                                    });


                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();


                                        } else {

                                            txtStatus.setText(response);
                                            progress.setVisibility(View.GONE);

                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.d(TAG, "ERror in Update Password : " + error.getMessage());

                                    }
                                });
                                MyApplication.getInstance().addToRequestQueue(request);


                            }


                        } else {
                            progress.setVisibility(View.GONE);
                            txtStatus.setVisibility(View.VISIBLE);
                            txtStatus.setText("Sorry,Try again...");

                        }
*/


                    //Complete Update Password Process

               /*     }*/


                } catch (Exception e) {
                    System.out.print("Errorr :" + e.getMessage());
                    e.printStackTrace();
                }
            } else {

                Toast.makeText(getActivity(), "Please enable wifi or mobile data", Toast.LENGTH_SHORT).show();


            }


        }

        return super.onOptionsItemSelected(item);
    }

    private void sendProfileUpdateDetialsToServer() {




        /*DateFormat originalFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = originalFormat.parse("August 21, 2012");
        String formattedDate = targetFormat.format(date);  // 20120821*/

        String formattedDate;

        try {
            DateFormat originalFormat = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = originalFormat.parse(edtdob.getText().toString());
            formattedDate = targetFormat.format(date);  // 20120821

        } catch (ParseException e) {
            formattedDate = "";  // 20120821
            e.printStackTrace();
        }


        String url_profileUpdate = AllKeys.WEBSITE + "updateUserDetails/" + userdetails.get(SessionManager.KEY_USERID) + "/" + dbhandler.convertEncodedString(txtname_edit.getText().toString()) + "/" + dbhandler.convertEncodedString(txtemail_edit.getText().toString()) + "/" + formattedDate + "/" + GENDER_ID + "/" + dbhandler.convertEncodedString(edtBio.getText().toString()) + "";
        http:
//192.168.0.21/yelona/index.php/welcome/updateUserDetails/775/Gadde/sathishmicit2012%40gmail.com/11-03-1993/1/
        Log.d(TAG, "URL Profile Update  :" + url_profileUpdate);

        StringRequest str_profileUpdate = new StringRequest(Request.Method.GET, url_profileUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response Update Profile :" + response);

                if (response.contains("success")) {
                    sessionManager.setUserDetails(userdetails.get(SessionManager.KEY_USERID), txtname_edit.getText().toString(), userdetails.get(SessionManager.KEY_USER_LASTNAME), userdetails.get(SessionManager.KEY_USER_AVATAR), userdetails.get(SessionManager.KEY_USER_NAME), txtemail_edit.getText().toString(), userdetails.get(SessionManager.KEY_USER_PASSWORD), userdetails.get(SessionManager.KEY_USER_MOBILE), edtdob.getText().toString(), GENDER_ID, edtBio.getText().toString(), userdetails.get(SessionManager.KEY_USER_LASTLOGIN), userdetails.get(SessionManager.KEY_USER_VERIFIED_MOBILE), userdetails.get(SessionManager.KEY_USER_VERIFIED_EMAIL), "");


                    profile_edit.setVisible(true);
                    profile_update.setVisible(false);
                    txtname.setText(txtname_edit.getText().toString());
                    txtemail.setText(txtemail_edit.getText().toString());

                    HideControls();

                    Snackbar.make(mRoot, "Profile details updated", Snackbar.LENGTH_LONG).show();



                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Profile Info ");
                    alertDialogBuilder.setMessage("Profile details has been updated successfully");
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {

                                    dialog.cancel();
                                    dialog.dismiss();
                                    //txtStatus.setVisibility(View.GONE);
                                   // btnUpdate.setVisibility(View.GONE);

                                }
                            });


                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();



                } else {
                    Snackbar.make(mRoot, "Sorry, try again...", Snackbar.LENGTH_LONG).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {

                    hideDialog();
                } else {
                    sendProfileUpdateDetialsToServer();
                }

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_profileUpdate);

    }

    private void ShowAllControlsEditableAndVisible() {

        edtdob.setEnabled(true);
        edtBio.setEnabled(true);


        llemail.setVisibility(View.VISIBLE);
        llname.setVisibility(View.VISIBLE);
        llBio.setVisibility(View.VISIBLE);
        llDOB.setVisibility(View.VISIBLE);
        rdGrpGender.setVisibility(View.VISIBLE);
        cvOrders.setVisibility(View.GONE);
        cvAddresses.setVisibility(View.GONE);

        cvUpdatePasswprd.setVisibility(View.GONE);

        txtname_edit.setVisibility(View.VISIBLE);

        txtname_edit.setText("" + txtname.getText().toString());
        txtemail.setVisibility(View.VISIBLE);
        txtemail_edit.setText("" + txtemail.getText().toString());


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


    public class SendProfileDetailsToServer extends AsyncTask<Void, Void, Void> {

        //private Dialog dialog;
        private ProgressBar progressBar;
        private String response_signup = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //  dialog = new Dialog(getActivity());

            //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // dialog.setContentView(R.layout.dialog_progress);


            // progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar2);
            // progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

         /*   Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();  // deprecated
            int height = display.getHeight();
            height = height / 3;// - 100;
            width = width - 20;

            dialog.getWindow().setLayout(width, height);
            dialog.setCancelable(false);
            dialog.show();*/

            showDialog();


        }

        @Override
        protected Void doInBackground(Void... paramssss) {


           /* ServiceHandler sh = new ServiceHandler();


            //String URL_CREATE_NEW_USER = AllKeys.TAG_WEBSITE+"/GetClientRegitration?type=signin&name="+ txtfullname.getText().toString() +"&username="+ txtusername.getText().toString() +"&password="+ txtpassword.getText().toString() +"&mobile="+ txtmobile.getText().toString() +"&email="+ txtemail.getText().toString() +"&deviceid="+ DEVICEID+"sa" +"&phno="+ txtphoneno.getText().toString() +"&ver_code="+ code +"";
            String URL_CREATE_NEW_USER = AllKeys.WEBSITE + "UpdateCustomerDetail";


            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("type", "updatecustomer"));
            params.add(new BasicNameValuePair("custid", userdetails.get(SessionManager.KEY_USERID)));
            params.add(new BasicNameValuePair("name", dbhandler.convertEncodedString(txtname_edit.getText().toString())));
            params.add(new BasicNameValuePair("phoneno", dbhandler.convertEncodedString(txtphoneno.getText().toString())));
            params.add(new BasicNameValuePair("address", dbhandler.convertEncodedString(txtaddress.getText().toString())));
            params.add(new BasicNameValuePair("city", dbhandler.convertEncodedString(txtcity.getText().toString())));
            params.add(new BasicNameValuePair("state", dbhandler.convertEncodedString(txtstate.getText().toString())));
            params.add(new BasicNameValuePair("mobile", dbhandler.convertEncodedString(userdetails.get(SessionManager.KEY_MOBILE))));
            params.add(new BasicNameValuePair("email", dbhandler.convertEncodedString(txtemail_edit.getText().toString())));


            if(txtdob.getText().toString().equals(""))
            {
                params.add(new BasicNameValuePair("dob", ""));

            }
            else
            {
            params.add(new BasicNameValuePair("dob", dbhandler.convertToJsonDateFormat(txtdob.getText().toString())));

            }




            if(txtdoa.getText().toString().equals(""))
            {
            params.add(new BasicNameValuePair("doa", ""));

            }
            else
            {

                params.add(new BasicNameValuePair("doa", dbhandler.convertToJsonDateFormat(txtdoa.getText().toString())));
            }
            params.add(new BasicNameValuePair("OccupationId", String.valueOf(listOccupationId.get(spnoccupation.getSelectedItemPosition()))));


            session.setUserDetails(txtname_edit.getText().toString(), userdetails.get(SessionManager.KEY_MOBILE), txtemail_edit.getText().toString(), txtcity.getText().toString(), txtstate.getText().toString(), userdetails.get(SessionManager.KEY_USERID), txtphoneno.getText().toString(), txtaddress.getText().toString(), userdetails.get(SessionManager.KEY_PROGRAMMEID), txtprogrammename.getText().toString(), txtcoursestartdate.getText().toString(), txtcourseenddate.getText().toString(), txtdoa.getText().toString(), txtdob.getText().toString(), "" + listOccupationId.get(spnoccupation.getSelectedItemPosition()));

            Log.d("URL GetClientReg :", URL_CREATE_NEW_USER + params.toString());


            response_signup = sh.makeServiceCall(URL_CREATE_NEW_USER, ServiceHandler.POST, params);

            Log.d("Response  ", "ProfileUpdate : " + response_signup);*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            hideDialog();

       /*     if (response_signup.equals("1")) {
                //Toast.makeText(getActivity() , "Details has been update",Toast.LENGTH_SHORT).show();

                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Profile details has been update successfully");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(getActivity(),
                                NewDashBoardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("PROFILEUPDATE","TRUE");
                        startActivity(intent);
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);




                    }
                });


                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } else
                {

                //Toast.makeText(getActivity() , "Details not updated",Toast.LENGTH_SHORT).show();

                final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Profile details not update successfully");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        *//*Intent intent = new Intent(getActivity(),
                                NewDashBoardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*//*


                    }
                });


                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }*/


        }
    }


    /**
     * User Profile Pic Selection Related Code
     */


    private void SetUserProfilePictireFromBase64EnodedString() {
        try {


            String myBase64Image = userdetails.get(SessionManager.KEY_ENODEDED_STRING);//Here get image and convert into base64
            if (!myBase64Image.equals("")) {

                Bitmap myBitmapAgain = dbhandler.decodeBase64(myBase64Image);

                imgProfilePic.setImageBitmap(myBitmapAgain);


            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Decode Img Exception : ", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Profile Picture!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermission_ExternalStorage(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/


    private void onCaptureImageResult(Intent data) {

        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);


        int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
        Log.d("C:Before Bitmap Size : ", "" + bitmapByteCount);


        //String realPath=getRealPathFromURI(data.getData());

        Uri tempUri = getImageUri(bitmap);


        String realPath = null;
        try {
            Log.d("C: Realpath URI : ", "" + tempUri.toString());
            realPath = getRealPathFromURI(tempUri);
            Log.d("C: Realpath : ", realPath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        bitmap = ImageUtils.getInstant().getCompressedBitmap(realPath);
        //imageView.setImageBitmap(bitmap);

        bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
        Log.d("C:After Bitmap Size : ", "" + bitmapByteCount);


        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        imgProfilePic.setImageBitmap(bitmap);


        if (NetConnectivity.isOnline(getActivity())) {

            BASE64STRING= dbhandler.getStringImage(bitmap);
            sessionManager.setEncodedImage(BASE64STRING);


            //  new SendUserProfilePictureToServer().execute();
        } else {

            //   checkInternet();
            Toast.makeText(getActivity(), "Please enable internet", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {


                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
                Log.d("Before Bitmap Size : ", "" + bitmapByteCount);


                Uri tempUri = getImageUri(bitmap);


                String realPath = null;
                try {
                    Log.d("CC: Realpath URI : ", "" + tempUri.toString());
                    realPath = getRealPathFromURI(tempUri);
                    Log.d("CC: Realpath : ", realPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*Uri uri = data.getData();

                realPath=""+getRealPathFromURI(data.getData());
                Log.d("RealPath : " , ""+realPath);
                realPath = uri.getEncodedPath();
                Log.d("RealPath URI : " , ""+realPath);
                realPath=""+getRealPathFromURI_NEW(data.getData());
                Log.d("RealPath New : " , ""+realPath);*/


                bitmap = ImageUtils.getInstant().getCompressedBitmap(realPath);
                //imageView.setImageBitmap(bitmap);

                bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
                Log.d("After Bitmap Size : ", "" + bitmapByteCount);


                // getStringImage(bm);


            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        try {
            if (NetConnectivity.isOnline(getActivity())) {


                BASE64STRING = dbhandler.getStringImage(bitmap);
                sessionManager.setEncodedImage(BASE64STRING);
                imgProfilePic.setImageBitmap(bitmap);

                //   new SendUserProfilePictureToServer().execute();
            } else {
//                checkInternet();
                Toast.makeText(getActivity(), "Please enable internet", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


/*    private class SendUserProfilePictureToServer extends AsyncTask<Void, Void, Void>
    {

        private Dialog dialog;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(getActivity());

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_progress);


            progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar2);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();  // deprecated
            int height = display.getHeight();
            height = height / 3;// - 100;
            width = width - 20;

            dialog.getWindow().setLayout(width, height);
            dialog.show();
            dialog.setCancelable(false);


        }


        @Override
        protected Void doInBackground(Void... voids) {
            final Bitmap finalBm = bitmap;

            *//*getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {*//*

            //String enc = encodeToBase64(finalBm, Bitmap.CompressFormat.JPEG, 50);


            String enc = dbhandler.getStringImage(finalBm);
            session.setEncodedImage(enc);

            if (NetConnectivity.isOnline(getActivity())) {
                ServiceHandler sh = new ServiceHandler();

                String URL_PROFILEPICUPDATE = AllKeys.WEBSITE + "UpdateStudentProfile";
//                        String URL_PROFILEPICUPDATE = AllKeys.TAG_WEBSITE_SERVICET + "InsertGyanCapsule";
                Log.d("URL ProfileUpdate : ", URL_PROFILEPICUPDATE);
                //String res = sh.makeServiceCall(URL_PROFILEPICUPDATE, ServiceHandler.GET);
                //Log.d("Response : ", res);


                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("type", "updateprofile"));
                params.add(new BasicNameValuePair("custid", "" + userdetails.get(session.KEY_USERID)));

                params.add(new BasicNameValuePair("imagecode", enc));
                String response = sh.makeServiceCall(URL_PROFILEPICUPDATE, ServiceHandler.POST, params);

                Log.d("Profile Url :", URL_PROFILEPICUPDATE + params.toString());
                Log.d("Response : ", response);

            } else {
                //checkInternet();

            }


              *//*  }
            });*//*

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                imgProfilePic.setImageBitmap(bitmap);
                dialog.dismiss();
                dialog.cancel();

            }

        }


    }*/

    /**
     * Complete User select profile picture from device
     */


    /*public String getRealPathFromURI( Uri contentUri) {//content://media/external/images/media/4288
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/
    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String dd = cursor.getString(column_index);
            return cursor.getString(column_index);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return "sa";

    }


    private String getRealPathFromURI_NEW(Uri contentURI) {
        String result = null;

        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }


    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    /**
     * BottomSheetAdapter
     */


}