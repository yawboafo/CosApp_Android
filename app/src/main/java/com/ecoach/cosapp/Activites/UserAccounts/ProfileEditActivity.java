package com.ecoach.cosapp.Activites.UserAccounts;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ecoach.cosapp.Activites.Company.Addcompany;
import com.ecoach.cosapp.Activites.MainActivity;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.DataBase.AppInstanceSettings;
import com.ecoach.cosapp.DataBase.Categories;
import com.ecoach.cosapp.DataBase.User;
import com.ecoach.cosapp.Http.APIRequest;
import com.ecoach.cosapp.Http.UploadBase64;
import com.ecoach.cosapp.Http.VolleySingleton;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.Utilities.GPSTracker;
import com.ecoach.cosapp.Utilities.Utility;
import com.ecoach.cosapp.Utilities.ViewUtils;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

public class ProfileEditActivity extends AppCompatActivity implements IPickResult {
    EditText emailedt,phoneTxt,passwordTxt,confirmpassword,surname,firstname,locationDiscirption;
    TextView fullname;
    ImageButton LOCATIONPICKER;
    FButton changePassword,backbutton,submitchanges;
    ViewFlipper viewFlipper;
    CircleImageView imageView;
    ImageView imageButton;
    GPSTracker gpsTracker;
    String company_lat = "0.0";
    String company_long = "0.0";
    User user = null;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        gpsTracker = new GPSTracker(ProfileEditActivity.this);

        if (getSupportActionBar() != null){

            getSupportActionBar().setTitle("Your Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        viewFlipper=(ViewFlipper)findViewById(R.id.viewFlipper);

        changePassword=(FButton) findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewFlipper.showNext();
            }
        });

        submitchanges =(FButton)findViewById(R.id.backButton);
        submitchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginVolley();
            }
        });

        backbutton=(FButton) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        });

        imageButton =(ImageView)findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).show(ProfileEditActivity.this);
            }
        });

        imageView=(CircleImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).show(ProfileEditActivity.this);
            }
        });
        editTextViews();

    }

    private void editTextViews(){

        emailedt = (EditText)findViewById(R.id.emailedt);
        phoneTxt = (EditText)findViewById(R.id.phoneEdt);
        //passwordTxt =(EditText)findViewById(R.id.passwordEdt);
        //confirmpassword=(EditText)findViewById(R.id.passwordEdt2);
        surname=(EditText)findViewById(R.id.surnameEdt);
        LOCATIONPICKER=(ImageButton)findViewById(R.id.firstEdt);
        LOCATIONPICKER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gpsTracker.canGetLocation()){


                    gpsTracker.showSettingsAlert();
                }else if(gpsTracker.getLocation() == null){
                    company_lat = String.valueOf(gpsTracker.getLatitude()) ;
                    company_long = String.valueOf(gpsTracker.getLongitude());
                    LOCATIONPICKER.setImageResource(R.drawable.mapshot);
                    LOCATIONPICKER.setScaleType(ImageView.ScaleType.FIT_XY);

                   // ViewUtils.multipleDialog(Addcompany.this,"Could not get your location","Try moving around for a few minutes");
                }else {

                    company_lat = String.valueOf(gpsTracker.getLatitude()) ;
                    company_long = String.valueOf(gpsTracker.getLongitude());
                    LOCATIONPICKER.setImageResource(R.drawable.mapshot);
                    LOCATIONPICKER.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });
        fullname = (TextView)findViewById(R.id.fullname);



        //set Values


         user = User.getUserByKey(AppInstanceSettings.load(AppInstanceSettings.class,1).getUserkey());

        emailedt.setText(user.getEmail());
        phoneTxt.setText(user.getPhone());
        surname.setText(user.getLoc_desc());
        fullname.setText(user.getFname() +  "  " + user.getLname());

        try {

            String avatorPath= user.getPath()+user.getStorage()+"/"+user.getAvatar();

            TextDrawable drawable = TextDrawable.builder()
                    .buildRoundRect(user.getFname().toString().substring(0,1), ProfileEditActivity.this.getResources().getColor(R.color.colorPrimary), 10); // radius in px

            Drawable d = new BitmapDrawable(ViewUtils.drawableToBitmap(drawable));

            Picasso.with(ProfileEditActivity.this)
                    .load(avatorPath)
                    .placeholder(d)
                    .into(imageView);

        }catch (Exception e){


        }
        //firstname.setText(user.getFname());


    }
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        imageView.setImageBitmap(pickResult.getBitmap());
        Application.setProfilePic(Utility.Base64String(pickResult.getBitmap()));
    }

    SweetAlertDialog pDialog;
    private void loginVolley() {

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Updating  ..");
        pDialog.setCancelable(false);
        pDialog.show();


        final HashMap<String, String> params = new HashMap<String, String>();


        params.put("is_edit_user_profile", "" + "1");
        params.put("email", emailedt.getText().toString());
        params.put("phone", phoneTxt.getText().toString());
        params.put("location_lat", company_lat);
        params.put("location_long",company_long);
        params.put("location_desc", surname.getText().toString());

        Log.d("loginD", params.toString());


        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = VolleySingleton.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                APIRequest.BASE_URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    //Log.d("Params",params+"");
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            Log.d("logs", response.toString());


                            Log.d("logs", response.toString());


                            JSONObject object = response.optJSONObject("ecoachlabs");
                            String statuscode = object.getString("status");
                            String message = object.getString("msg");

                            if (!statuscode.equals("201")) {

                                new SweetAlertDialog(ProfileEditActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Sorry,Try Again")
                                        .setContentText(message)
                                        .show();

                            } else if (statuscode.equals("201")) {

                                try{persistUserData(object);}catch (Exception e){e.printStackTrace();}

                                new SweetAlertDialog(ProfileEditActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Completed")
                                        .setContentText(message)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                sweetAlertDialog.dismiss();

                                                if(     Application.getProfilePic().length() != 0){


                                                    Intent intent = new Intent(ProfileEditActivity.this, UploadBase64.class);
                                                    startService(intent);



                                                }
                                            }
                                        })
                                        .show();





                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //  Message.messageShort(MyApplication.getAppContext(),""+tokenValue+"\n"+response.toString()+"\n"+booleaner);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                new SweetAlertDialog(ProfileEditActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();


                //  dialogs.SimpleWarningAlertDialog("Transmission Error", "Connection Failed").show();
                Log.d("volley.Response", error.toString());


                if (error instanceof TimeoutError) {
                    // dialogs.SimpleWarningAlertDialog("Network Slacking", "Time Out Error").show();
                    Log.d("volley", "NoConnectionError.....TimeoutError..");


                    //     dialogs.SimpleWarningAlertDialog("Network Slacking", "Time Out Error");


                } else if (error instanceof NoConnectionError) {

                    // dialogs.SimpleWarningAlertDialog("No Internet Connections Detected", "No Internet Connection").show();

                } else if (error instanceof AuthFailureError) {
                    //  Log.d("volley", "AuthFailureError..");
                    // dialogs.SimpleWarningAlertDialog("Authentication Failure","AuthFailureError").show();


                } else if (error instanceof ServerError) {
                    // dialogs.SimpleWarningAlertDialog("Server Malfunction", "Server Error").show();

                } else if (error instanceof NetworkError) {
                    // dialogs.SimpleWarningAlertDialog("Network Error", "Network Error").show();

                } else if (error instanceof ParseError) {
                    // dialogs.SimpleWarningAlertDialog("Parse Error","Parse Error").show();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("auth-key", user.getUserkey());
                return headers;
            }
        };
        int socketTimeout = 480000000;//8 minutes - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
        Log.d("oxinbo", "Server Logs" + params.toString());
    }

    private void persistUserData(JSONObject jsonObject) {
        String ukey = "";
        String email = "";
        try {

            Log.d("loginD", jsonObject.toString());
            ukey = jsonObject.getString("ukey");
            email = jsonObject.getString("email");

            String path = jsonObject.getString("path");
            String storage = jsonObject.getString("storage");


            String fname = jsonObject.getString("fname");
            String lname = jsonObject.getString("lname");

            String phone = jsonObject.getString("phone");
            String avatar = jsonObject.getString("avatar");


            User user = User.getUserByKey(ukey);
            if (user == null)
                user = new User();

            user.setUserkey(ukey);
            user.setEmail(email);
            user.setPath(path);
            user.setStorage(storage);
            user.setFname(fname);
            user.setLname(lname);
            user.setPhone(phone);
            user.setAvatar(avatar);

            Long id = user.save();

            Log.d("Saved User ", id.toString());


            AppInstanceSettings appInstanceSettings = AppInstanceSettings.load(AppInstanceSettings.class, 1);
            if (appInstanceSettings == null)
                appInstanceSettings = new AppInstanceSettings();

            appInstanceSettings.setIsloggedIn(true);
            appInstanceSettings.setUserkey(ukey);
            appInstanceSettings.save();

            // startService(new Intent(LoginActivity.this, FireBaseRegister.class));

           // fireBaseLogin(email, ukey);

            // ProcessPhoenix.triggerRebirth(LoginActivity.this);


        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
          //  fireBaseLogin(email, ukey);
        } catch (Exception e) {

            e.printStackTrace();
        }


    }
}
