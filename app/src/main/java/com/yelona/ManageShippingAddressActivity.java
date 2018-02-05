package com.yelona;

import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import com.yelona.app.MyApplication;
import com.yelona.database.dbhandler;
import com.yelona.helper.AllKeys;
import com.yelona.pojo.Addresses;
import com.yelona.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

import static com.yelona.helper.AllKeys.back_button;

public class ManageShippingAddressActivity extends AppCompatActivity {

    private RadioGroup rdGrpAddressMode;
    private TextView txtnodata;
    private Button btnsave;
    private LinearLayout llAddData;
    private LinearLayout llDisplayData;
    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private SpotsDialog pDialog;
    private CoordinatorLayout coordinatorLayout;
    private Spinner spnState;
    private TextInputLayout input_layout_edtName;
    private EditText edtName;
    private TextInputLayout input_layout_edtEmail;
    private EditText edtEmail;
    private TextInputLayout input_layout_edtMobile;
    private EditText edtMobile;
    private TextInputLayout input_layout_edtPincode;
    private EditText edtPincode;
    private TextInputLayout input_layout_edtCity;
    private EditText edtCity;
    private TextInputLayout input_layout_edtArea;
    private EditText edtArea;
    private TextInputLayout input_layout_edtAddress1, input_layout_edtAddress2;
    private EditText edtAddress1, edtAddress2;
    private TextInputLayout input_layout_edtLandmark;
    private EditText edtLandmark;
    private RecyclerView recyclerview_address;
    private dbhandler db;
    private FloatingActionButton fab;
    private RadioButton rdHome;
    private RadioButton rdOffice;
    private String TAG = ManageShippingAddressActivity.class.getSimpleName();
    private ArrayList<Addresses> list_addresses = new ArrayList<Addresses>();
    private ManageAddresesRecyclerViewAdapter adapter;
    private ArrayList<Integer> list_stateid = new ArrayList<Integer>();
    private ArrayList<String> list_state = new ArrayList<String>();
    private boolean IsEditRecord = false;
    private boolean IsDeliverHere = false;
    private String ADDRESS_ID = "0";
    private Button btnShow;

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
    private TextView txtBillingName;
    private TextView txtBillingAddress;
    private TextView txtBillingMobile;
    private Button btnChangeBillingAddress;
    private CheckBox chkSameAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shipping_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Shipping Information");


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                IsEditRecord = false;
                fab.setVisibility(View.GONE);
                llAddData.setVisibility(View.VISIBLE);
                llDisplayData.setVisibility(View.GONE);
            }
        });


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getSessionDetails();
        db = new dbhandler(context);

        /**
         * Dialog initilizing
         */
        pDialog = new SpotsDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(back_button);
        } catch (Exception e) {
            e.printStackTrace();
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        txtBillingName = (TextView) findViewById(R.id.txtBillingName);
        txtBillingAddress = (TextView) findViewById(R.id.txtBillingAddress);
        txtBillingMobile = (TextView) findViewById(R.id.txtBillingMobile);
        btnChangeBillingAddress = (Button) findViewById(R.id.btnChangeBillingAddress);


        txtBillingName.setText(userDetails.get(SessionManager.KEY_BILLING_NAME));
        txtBillingAddress.setText(userDetails.get(SessionManager.KEY_BILLING_ADDRESS));
        txtBillingMobile.setText(userDetails.get(SessionManager.KEY_BILLING_MOBILENO));

        btnChangeBillingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ManageBillingAddressActivity.class);
                startActivity(intent);
                finish();

            }
        });


        chkSameAddress = (CheckBox) findViewById(R.id.chkSameAddress);

        chkSameAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                    // gss

                }

            }
        });

        llAddData = (LinearLayout) findViewById(R.id.llAddData);
        llDisplayData = (LinearLayout) findViewById(R.id.llDisplayData);

        rdGrpAddressMode = (RadioGroup) findViewById(R.id.rdGrpAddressMode);
        rdHome = (RadioButton) findViewById(R.id.rdHome);
        rdOffice = (RadioButton) findViewById(R.id.rdOffice);


        input_layout_edtName = (TextInputLayout) findViewById(R.id.input_layout_edtName);
        edtName = (EditText) findViewById(R.id.edtName);
        edtName.setText(userDetails.get(SessionManager.KEY_USER_FIRSTNAME));

        input_layout_edtEmail = (TextInputLayout) findViewById(R.id.input_layout_edtEmail);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtEmail.setText(userDetails.get(SessionManager.KEY_USER_EMAIL));


        input_layout_edtMobile = (TextInputLayout) findViewById(R.id.input_layout_edtMobile);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        edtMobile.setText(userDetails.get(SessionManager.KEY_USER_MOBILE));

        input_layout_edtPincode = (TextInputLayout) findViewById(R.id.input_layout_edtPincode);
        edtPincode = (EditText) findViewById(R.id.edtPincode);

        input_layout_edtCity = (TextInputLayout) findViewById(R.id.input_layout_edtCity);
        edtCity = (EditText) findViewById(R.id.edtCity);


        spnState = (Spinner) findViewById(R.id.spnState);

        input_layout_edtArea = (TextInputLayout) findViewById(R.id.input_layout_edtArea);
        edtArea = (EditText) findViewById(R.id.edtArea);

        input_layout_edtAddress1 = (TextInputLayout) findViewById(R.id.input_layout_edtAddress1);
        edtAddress1 = (EditText) findViewById(R.id.edtAddress1);

        input_layout_edtAddress2 = (TextInputLayout) findViewById(R.id.input_layout_edtAddress2);
        edtAddress2 = (EditText) findViewById(R.id.edtAddress2);

        input_layout_edtLandmark = (TextInputLayout) findViewById(R.id.input_layout_edtLandmark);
        edtLandmark = (EditText) findViewById(R.id.edtLandmark);

        btnsave = (Button) findViewById(R.id.btnsave);
        btnShow = (Button) findViewById(R.id.btnShow);

        recyclerview_address = (RecyclerView) findViewById(R.id.rv_address);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerview_address.setLayoutManager(layoutManager);
        recyclerview_address.addItemDecoration(new dbhandler.GridSpacingItemDecoration(2, db.dpToPx(2), true));
        recyclerview_address.setItemAnimator(new DefaultItemAnimator());


        recyclerview_address.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        txtnodata = (TextView) findViewById(R.id.txtnodata);

        llAddData.setVisibility(View.GONE);
        llDisplayData.setVisibility(View.VISIBLE);


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llAddData.setVisibility(View.GONE);
                llDisplayData.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
            }

        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                boolean isError = false;
                if (edtName.getText().toString().equals("")) {
                    input_layout_edtName.setErrorEnabled(true);
                    input_layout_edtName.setError("Enter Name");
                    isError = true;

                } else {
                    input_layout_edtName.setErrorEnabled(false);


                }

                if (edtEmail.getText().toString().equals("")) {
                    input_layout_edtEmail.setErrorEnabled(true);
                    input_layout_edtEmail.setError("Enter Email");
                    isError = true;

                } else {
                    input_layout_edtEmail.setErrorEnabled(false);

                    if (checkEmail(edtEmail.getText().toString()) == false) {
                        input_layout_edtEmail.setErrorEnabled(true);
                        input_layout_edtEmail.setError("Invalid email");
                        isError = true;
                    }


                }

                if (edtMobile.getText().toString().equals("")) {
                    input_layout_edtMobile.setErrorEnabled(true);
                    input_layout_edtMobile.setError("Enter Mobile");
                    isError = true;

                } else {
                    if (edtMobile.getText().toString().length() != 10) {
                        input_layout_edtMobile.setErrorEnabled(true);
                        input_layout_edtMobile.setError("Invalid mobile no.");
                        isError = true;


                    } else {
                        input_layout_edtMobile.setErrorEnabled(false);


                    }
                }

                if (edtPincode.getText().toString().equals("")) {
                    input_layout_edtPincode.setErrorEnabled(true);
                    input_layout_edtPincode.setError("Enter Pincode");
                    isError = true;

                } else {
                    if (edtPincode.getText().toString().length() != 6) {
                        input_layout_edtPincode.setErrorEnabled(true);
                        input_layout_edtPincode.setError("Invalid Pincode");
                        isError = true;


                    } else {
                        input_layout_edtPincode.setErrorEnabled(false);

                    }

                }

                if (edtCity.getText().toString().equals("")) {
                    input_layout_edtCity.setErrorEnabled(true);
                    input_layout_edtCity.setError("Enter City");
                    isError = true;

                } else {
                    input_layout_edtCity.setErrorEnabled(false);

                }

                if (spnState.getSelectedItemPosition() == 0) {
                    Toast.makeText(context, "Please select State", Toast.LENGTH_SHORT).show();
                    isError = true;
                }


               /* if (edtArea.getText().toString().equals("")) {
                    input_layout_edtArea.setErrorEnabled(true);
                    input_layout_edtArea.setError("Enter Area");
                    isError = true;

                } else {
                    input_layout_edtArea.setErrorEnabled(false);
                    isError = false;

                }*/


                if (edtAddress1.getText().toString().equals("")) {
                    input_layout_edtAddress1.setErrorEnabled(true);
                    input_layout_edtAddress1.setError("Enter Address");
                    isError = true;

                } else {
                    input_layout_edtAddress1.setErrorEnabled(false);


                }

                if (edtAddress2.getText().toString().equals("")) {
                    input_layout_edtAddress2.setErrorEnabled(true);
                    input_layout_edtAddress2.setError("Enter Address");
                    isError = true;

                } else {
                    input_layout_edtAddress2.setErrorEnabled(false);


                }


                /*if (edtLandmark.getText().toString().equals("")) {
                    input_layout_edtLandmark.setErrorEnabled(true);
                    input_layout_edtLandmark.setError("Enter Landmark");
                    isError = true;

                } else {
                    input_layout_edtLandmark.setErrorEnabled(false);
                    isError = false;

                }*/


                if (isError == false)
                {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();


                    if (IsEditRecord == true) {
                        sendmanageShippingAddressDetailsToServer();

                    } else if (IsDeliverHere == true) {

                        sessionmanager.setShippingAdress(edtName.getText().toString(), edtEmail.getText().toString(), edtMobile.getText().toString(), edtAddress1.getText().toString() + "," + edtAddress2.getText().toString() + "," + edtCity.getText().toString() + "-" + edtPincode.getText().toString() + "," + spnState.getSelectedItem().toString(), ADDRESS_ID);


                        //Intent ii = new Intent(_context, ManagePaymentInformation.class);
                        Intent ii = new Intent(context, OrderReviewActivity.class);
                        startActivity(ii);
                        finish();
                    } else {
                        sendmanageShippingAddressDetailsToServer();
                    }

                }
                else
                {
                    Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show();
                }


            }
        });


        getAllAddressDetailsFromServer();
        getAllStateDetailsFromServer();


    }

    /**
     * Check Email Validation
     *
     * @param email
     * @return
     */
    private boolean checkEmail(String email) {
        System.out.println("Email Validation:==>" + email);
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }


    private void getAllStateDetailsFromServer() {
        String url_getStates = AllKeys.WEBSITE + "getAllStatesData";
        ;
        Log.d(TAG, "url getAllStatesData : " + url_getStates);
        StringRequest str_getAllStates = new StringRequest(Request.Method.GET, url_getStates, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.contains("state")) {
                    list_state.clear();
                    list_stateid.clear();


                    response = dbhandler.convertToJsonFormat(response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        list_stateid.add(0);
                        list_state.add("Select State");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);

                            list_stateid.add(c.getInt(AllKeys.TAG_STATEID));
                            list_state.add(c.getString(AllKeys.TAG_STATE));

                        }

                        spnState.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list_state));


                        // adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_addresses);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getAllStates);
    }

    private void sendmanageShippingAddressDetailsToServer() {

        showDialog();
        String type;
        if (rdHome.isChecked() == true) {
            type = "1";
        } else {
            type = "2";

        }


        String url_manageAddress;
        if (IsEditRecord == true) {


            url_manageAddress = AllKeys.WEBSITE + "updateUserAddress/" + ADDRESS_ID + "/" + userDetails.get(SessionManager.KEY_USERID) + "/" + dbhandler.convertEncodedString(edtCity.getText().toString()) + "/" + type + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "/" + dbhandler.convertEncodedString(list_state.get(spnState.getSelectedItemPosition())) + "/" + dbhandler.convertEncodedString(edtAddress1.getText().toString()) + "/" + dbhandler.convertEncodedString(edtAddress2.getText().toString()) + "/" + dbhandler.convertEncodedString(edtPincode.getText().toString()) + "/" + dbhandler.convertEncodedString(edtLandmark.getText().toString()) + "";
            Log.d(TAG, "editAddress URL : " + url_manageAddress);
        } else {
            //$state,$address1,$address2,$pin,$landmark="",$area="",$street="")
            url_manageAddress = AllKeys.WEBSITE + "addUserAddress/" + userDetails.get(SessionManager.KEY_USERID) + "/" + dbhandler.convertEncodedString(edtCity.getText().toString()) + "/" + type + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "/" + dbhandler.convertEncodedString(dbhandler.getDateTime()) + "/" + dbhandler.convertEncodedString(list_state.get(spnState.getSelectedItemPosition())) + "/" + dbhandler.convertEncodedString(edtAddress1.getText().toString()) + "/" + dbhandler.convertEncodedString(edtAddress2.getText().toString()) + "/" + dbhandler.convertEncodedString(edtPincode.getText().toString()) + "/" + dbhandler.convertEncodedString(edtLandmark.getText().toString()) + "";
            Log.d(TAG, "addAddress URL : " + url_manageAddress);
        }

        StringRequest str_manageAddress = new StringRequest(Request.Method.GET, url_manageAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.equals("1")) {
                    edtCity.setText("");
                    edtLandmark.setText("");
                    edtPincode.setText("");
                    edtName.setText("");
                    edtArea.setText("");
                    edtAddress1.setText("");
                    edtAddress2.setText("");
                    spnState.setSelection(0);
                    edtEmail.setText("");
                    edtLandmark.setText("");


                    getAllAddressDetailsFromServer();

                    if (IsEditRecord == true) {
                        IsEditRecord = false;
                        btnsave.setText("Save Address");
                        Snackbar.make(coordinatorLayout, "Address has been updated", Snackbar.LENGTH_SHORT).show();
                    } else {
                        btnsave.setText("Save Address");
                        Snackbar.make(coordinatorLayout, "Address has been added", Snackbar.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError) {
                    hideDialog();
                } else {
                    sendmanageShippingAddressDetailsToServer();
                }


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_manageAddress);


    }

    private void getAllAddressDetailsFromServer() {
        showDialog();
        String url_getAddresses = AllKeys.WEBSITE + "getAdderssesByUserid/" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetAllAddresses :" + url_getAddresses);

        StringRequest str_getAllAddresses = new StringRequest(Request.Method.GET, url_getAddresses, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                list_addresses.clear();
                if (response.contains("user_id"))
                {
                    try {
                        response = response.replace("null", "\"\"");
                        response = dbhandler.convertToJsonFormat(response);
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            Addresses ad = new Addresses(c.getString(AllKeys.TAG_ADDRESS_ID), c.getString(AllKeys.TAG_ADDRESS_AREA), c.getString(AllKeys.TAG_ADDRESS_STREET), c.getString(AllKeys.TAG_ADDRESS_LANDMARK), c.getString(AllKeys.TAG_ADDRESS_CITY), c.getString(AllKeys.TAG_ADDRESS_STATE), c.getString(AllKeys.TAG_ADDRESS_ADDRESS1), c.getString(AllKeys.TAG_ADDRESS_ADDRESS2), c.getString(AllKeys.TAG_ADDRESS_PINCODE), userDetails.get(SessionManager.KEY_USERNAME), userDetails.get(SessionManager.KEY_USER_EMAIL), userDetails.get(SessionManager.KEY_USER_MOBILE), c.getString(AllKeys.TAG_ADDRESS_TYPE), c.getString(AllKeys.TAG_ADDRESS_CREATED_AT), c.getString(AllKeys.TAG_ADDRESS_UPDATED_AT));
                            list_addresses.add(ad);


                        }

                        adapter = new ManageAddresesRecyclerViewAdapter(context, list_addresses, "billing");
                        recyclerview_address.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideDialog();
                    }


                    txtnodata.setVisibility(View.GONE);
                    llDisplayData.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    llAddData.setVisibility(View.GONE);


                } else
                    {

                    txtnodata.setVisibility(View.GONE);

                        llDisplayData.setVisibility(View.GONE);
                        fab.setVisibility(View.GONE);
                        llAddData.setVisibility(View.VISIBLE);


                }



                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof ServerError || error instanceof NetworkError) {
                    hideDialog();
                } else {
                    getAllAddressDetailsFromServer();

                }
                Log.d(TAG, "Error getAllAddresses : " + error.getMessage());
            }
        });

        MyApplication.getInstance().addToRequestQueue(str_getAllAddresses);

    }
    //onCreate Completed


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(context, OrderReviewActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = null;
        try {
            i = new Intent(context, OrderReviewActivity.class);
            //i.putExtra("ActivityName",ACTIVITYNAME);
            startActivity(i);
            finish();
        } catch (Exception e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error in Converting Class : " + e.getMessage());
            e.printStackTrace();
        }


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


    /**
     * ManageAddress Adapter
     */

    public class ManageAddresesRecyclerViewAdapter extends RecyclerView.Adapter<ManageAddresesRecyclerViewAdapter.MyViewHolder> {


        private final Context _context;
        private final ArrayList<Addresses> list_addresses;
        private final LayoutInflater inflater;
        private final SessionManager sessionManager;
        private String address_type = "";
        private HashMap<String, String> userDetails = new HashMap<String, String>();


        public ManageAddresesRecyclerViewAdapter(Context context, ArrayList<Addresses> list_addresses, String type) {
            this._context = context;
            this.list_addresses = list_addresses;
            this.address_type = type;
            inflater = LayoutInflater.from(context);
            sessionManager = new SessionManager(context);
            userDetails = sessionManager.getSessionDetails();


        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtName;
            private final TextView txtAddress;
            private final Button btnDeliverHere, btnEdit;
            private final TextView txtMobile;

            public MyViewHolder(View itemView) {
                super(itemView);
                txtName = (TextView) itemView.findViewById(R.id.txtname);
                txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
                txtMobile = (TextView) itemView.findViewById(R.id.txtMobile);
                btnDeliverHere = (Button) itemView.findViewById(R.id.btnDeleverHere);
                btnEdit = (Button) itemView.findViewById(R.id.btnEdit);


            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.row_single_address, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {


            final Addresses ad = list_addresses.get(position);

            holder.txtName.setText(ad.getName());
            holder.txtAddress.setText(ad.getAddress1() + "," + ad.getAddress2() + ",\n" + ad.getCity() + "," + ad.getState() + "-" + ad.getPincode());

            holder.txtMobile.setText(ad.getMobileno());
            holder.btnDeliverHere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnsave.setText("Deliver Here");

                    IsDeliverHere = true;
                    IsEditRecord = false;
                    Toast.makeText(_context, "Deliever Here", Toast.LENGTH_SHORT).show();

                    ADDRESS_ID = ad.getId();

                    sessionManager.setShippingAdress(userDetails.get(SessionManager.KEY_USER_FIRSTNAME), userDetails.get(SessionManager.KEY_USER_EMAIL), userDetails.get(SessionManager.KEY_USER_MOBILE), ad.getAddress1() + "," + ad.getAddress2() + "," + ad.getCity() + "-" + ad.getPincode() + "," + ad.getState(), ad.getId());


                    //Intent ii = new Intent(_context, ManagePaymentInformation.class);
                    // Intent ii = new Intent(_context, OrderReviewActivity.class);
                    // _context.startActivity(ii);

                    edtName.setText(userDetails.get(SessionManager.KEY_USER_FIRSTNAME));
                    edtEmail.setText(userDetails.get(SessionManager.KEY_USER_EMAIL));
                    edtMobile.setText(userDetails.get(SessionManager.KEY_USER_MOBILE));
                    edtPincode.setText(ad.getPincode());
                    edtCity.setText(ad.getCity());
                    edtAddress1.setText(ad.getAddress1());
                    edtAddress2.setText(ad.getAddress2());
                    edtArea.setText(ad.getAres());
                    edtLandmark.setText(ad.getLandmark());


                    int pos = list_state.indexOf(ad.getState());
                    try {
                        spnState.setSelection(pos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (ad.getType().equals("1")) {

                        rdHome.setChecked(true);


                    } else {

                        rdOffice.setChecked(true);
                    }

                    llAddData.setVisibility(View.VISIBLE);
                    llDisplayData.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);


                }
            });
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnsave.setText("Update Address");
                    IsDeliverHere = false;
                    IsEditRecord = true;
                    ADDRESS_ID = ad.getId();
                    Toast.makeText(_context, "Edit", Toast.LENGTH_SHORT).show();

                    sessionManager.setShippingAdress(userDetails.get(SessionManager.KEY_USERNAME), userDetails.get(SessionManager.KEY_USER_EMAIL), userDetails.get(SessionManager.KEY_USER_MOBILE), ad.getAddress1() + ad.getAddress2() + ad.getCity() + ad.getPincode() + ad.getState(), ad.getId());


                    // Intent ii = new Intent(_context, ManageShippingAddressActivity.class);
                    //  _context.startActivity(ii);


                    edtName.setText(userDetails.get(SessionManager.KEY_USER_FIRSTNAME));
                    edtEmail.setText(userDetails.get(SessionManager.KEY_USER_EMAIL));
                    edtMobile.setText(userDetails.get(SessionManager.KEY_USER_MOBILE));
                    edtPincode.setText(ad.getPincode());
                    edtCity.setText(ad.getCity());
                    edtAddress1.setText(ad.getAddress1());
                    edtAddress2.setText(ad.getAddress2());
                    edtArea.setText(ad.getAres());
                    edtLandmark.setText(ad.getLandmark());


                    int pos = list_state.indexOf(ad.getState());
                    try {
                        spnState.setSelection(pos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (ad.getType().equals("1")) {

                        rdHome.setChecked(true);


                    } else {

                        rdOffice.setChecked(true);
                    }

                    llAddData.setVisibility(View.VISIBLE);
                    llDisplayData.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);


                }
            });

        }

        @Override
        public int getItemCount() {
            return list_addresses.size();
        }
    }


    //Complete ManageShippingAddress Activity

}
