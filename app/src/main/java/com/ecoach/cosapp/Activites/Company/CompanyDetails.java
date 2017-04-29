package com.ecoach.cosapp.Activites.Company;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
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
import com.applozic.audiovideo.activity.AudioCallActivityV2;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.ecoach.cosapp.Activites.CompanyDetailsTabbedActivities.Details;
import com.ecoach.cosapp.Activites.CompanyDetailsTabbedActivities.Map;
import com.ecoach.cosapp.Activites.CompanyDetailsTabbedActivities.Profile;
import com.ecoach.cosapp.Activites.IncomingChat;
import com.ecoach.cosapp.Activites.MainActivity;
import com.ecoach.cosapp.Activites.UserAccounts.LoginActivity;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.DataBase.AppInstanceSettings;
import com.ecoach.cosapp.DataBase.User;
import com.ecoach.cosapp.DataBase.VerifiedCompanies;
import com.ecoach.cosapp.Fragments.SelectDepartmentDialog;
import com.ecoach.cosapp.Http.APIRequest;
import com.ecoach.cosapp.Http.VolleySingleton;
import com.ecoach.cosapp.Models.Company;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.Utilities.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CompanyDetails extends AppCompatActivity  implements SelectDepartmentDialog.SelectDepartmentDialogOnFragmentInteractionListener {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
   // private AVLoadingIndicatorView avi;
    private TextView companyName,companycategory;
    private MaterialRatingBar ratingBar;
    private RelativeLayout imageView2;
    AppInstanceSettings appInstanceSettings;
    User user;
    private boolean isLoggedIn = false;
    CircleImageView companyAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);


        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(Application.getSelectedCompanyName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        try{
            appInstanceSettings = AppInstanceSettings.load(AppInstanceSettings.class,1);
            user = User.getUserByKey(appInstanceSettings.getUserkey());
        }catch (Exception e){

            e.printStackTrace();
        }

        getCompanyDetails(savedInstanceState);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.chatfloatButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try{


                    isLoggedIn = appInstanceSettings.isloggedIn();





                }catch (Exception e){


                    e.printStackTrace();
                }


                try {

                    if(isLoggedIn ){

                        SelectDepartmentDialog addRepsDialog=new SelectDepartmentDialog().newInstance("","");
                        showDialog(addRepsDialog,"checkout");

                        /**  Intent intent = new Intent(CompanyDetails.this, ConversationActivity.class);
                         intent.putExtra(ConversationUIService.USER_ID,Application.getSelectedCompanyObbject().getEmail());
                         intent.putExtra(ConversationUIService.DISPLAY_NAME, Application.getSelectedCompanyObbject().getCompanyName()); //put it for displaying the title.
                         startActivity(intent);
                         **/
                    }else{
                        new SweetAlertDialog(CompanyDetails.this)
                                .setTitleText("You need to login !")
                                .show();

                    }

                }catch (Exception e){


                }


            }
        });

    }


    private void setUpCompanyDetails(Bundle savedInstanceState){
        companyAvatar=(CircleImageView)findViewById(R.id.companyAvatar);
        TextDrawable drawable = TextDrawable.builder()
                .buildRoundRect(Application.getSelectedCompanyObbject().getCompanyName().substring(0, 1), CompanyDetails.this.getResources().getColor(R.color.colorPrimary), 10);

        Drawable d = new BitmapDrawable(ViewUtils.drawableToBitmap(drawable));


        String avatorPath= Application.getSelectedCompanyObbject().getPath()+Application.getSelectedCompanyObbject().getCompanyStorageName()+"/"+Application.getSelectedCompanyObbject().getAvatarLocation();
        String CoverPath= Application.getSelectedCompanyObbject().getPath()+Application.getSelectedCompanyObbject().getCompanyStorageName()+"/"+Application.getSelectedCompanyObbject().getCoverLocation();

        Picasso.with(CompanyDetails.this)
                .load(avatorPath)
                .placeholder(d)
                .into(companyAvatar);

        imageView2=(RelativeLayout)findViewById(R.id.imageView2);

        Picasso.with(this)
                .load(CoverPath)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        BitmapDrawable background = new BitmapDrawable(bitmap);
                        imageView2.setBackgroundDrawable(background);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                        Drawable drawable = CompanyDetails.this.getResources().getDrawable(R.drawable.ic_no_image);
                       imageView2.setBackground(drawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Drawable drawable = CompanyDetails.this.getResources().getDrawable(R.drawable.ic_no_image);
                        imageView2.setBackground(drawable);
                    }
                });

        companyName=(TextView)findViewById(R.id.rep_companyName);
        companyName.setText(Application.getSelectedCompanyObbject().getCompanyName());

        companycategory=(TextView)findViewById(R.id.companycategory);
        companycategory.setText(Application.getSelectedCategoryName());


        ratingBar=(MaterialRatingBar)findViewById(R.id.companyRating);
        ratingBar.setRating(Float.parseFloat(Application.getSelectedCompanyObbject().getCompanyRating()));


        setTabHost(savedInstanceState);

    }

    private void setTabHost(Bundle savedInstanceState){

    // create the TabHost that will contain the Tabs
    TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);
    // tabHost.setup();

    TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
    TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
    TabHost.TabSpec tab3 = tabHost.newTabSpec("Third tab");

    tab1.setIndicator("Details");
    tab1.setContent(new Intent(this,Details.class));

    tab2.setIndicator("Profile");
    tab2.setContent(new Intent(this,Profile.class));

    tab3.setIndicator("Map");
    tab3.setContent(new Intent(this,Map.class));

    /** Add the tabs  to the TabHost to display. */
    tabHost.addTab(tab1);
    tabHost.addTab(tab2);
    tabHost.addTab(tab3);


}
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void showDialog(DialogFragment dialogFragment, String tag) {


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {

            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, tag);
    }


    private void getCompanyDetails(final Bundle savedInstanceState){
     try{
    VerifiedCompanies verifiedCompanies = VerifiedCompanies.getCompanyByID(Application.getSelectedCompanyID());
    if(verifiedCompanies == null)
        Log.d("yes","its null");
    Application.setSelectedCompanyObbject(verifiedCompanies);
    setUpCompanyDetails(savedInstanceState);

}catch (Exception e){


    e.printStackTrace();


}



    }

    @Override
    public void SelectDepartmentDialogonFragmentInteraction(String selectedDepartment,
                                                            boolean isPersonalAccount,
                                                            String selectedCompanyID,
                                                            String selectedCompanyName) {




        initiateChat(selectedCompanyID,selectedDepartment,selectedCompanyName,isPersonalAccount);

    }


    private void initiateChat(final String companyID, final String department, String Company_behalf, final boolean isPersonalAccount ){

        final SweetAlertDialog pDialog;
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Initiating Chat ..");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d("companies","loading from initiateChat");

        final HashMap<String, String> params = new HashMap<String, String>();



        params.put("is_join_chat_queue",""+ "1");
        if(isPersonalAccount){
           // params.put("Company_behalf",Company_behalf);
        }else{
            params.put("company_behalf",Company_behalf);
        }
        params.put("company_id",companyID);
        params.put("department",department);




        volleySingleton= VolleySingleton.getsInstance();
        requestQueue=VolleySingleton.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                APIRequest.BASE_URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    //Log.d("Params",params+"");
                    @Override
                    public void onResponse(JSONObject response) {

                        pDialog.dismiss();
                        try {
                            Log.d("Params",response.toString()+"");
                            JSONObject object = response.optJSONObject("ecoachlabs");
                            String statuscode = object.getString("status");
                            String message = object.getString("msg");

                            if (!statuscode.equals("201")) {
                                pDialog.hide();
                                new SweetAlertDialog(CompanyDetails.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Sorry,Try Again")
                                        .setContentText(message)
                                        .show();

                            } else if (statuscode.equals("201")) {

                                //persistUserData(object);
                                new SweetAlertDialog(CompanyDetails.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText(message)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {



                                                sweetAlertDialog.dismiss();

                                                try {

                                                   // ApplozicUserLogin();




                                                 //  ApplozicUserLogin(user.getUserkey(),"",user.getFname() +  "" + user.getLname(),false, Application.getSelectedCompanyObbject().getCompanyName(),companyID,department);

                                                }catch (Exception e){

                                                    e.printStackTrace();
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
            public void onErrorResponse(VolleyError error)   {



                //  dialogs.SimpleWarningAlertDialog("Transmission Error", "Connection Failed").show();
                Log.d("volley.Response", error.toString());

                pDialog.hide();
                new SweetAlertDialog(CompanyDetails.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();



                if (error instanceof TimeoutError) {
                    // dialogs.SimpleWarningAlertDialog("Network Slacking", "Time Out Error").show();
                    Log.d("volley", "NoConnectionError.....TimeoutError..");


                    //     dialogs.SimpleWarningAlertDialog("Network Slacking", "Time Out Error");



                } else if(error instanceof NoConnectionError){

                    // dialogs.SimpleWarningAlertDialog("No Internet Connections Detected", "No Internet Connection").show();

                }


                else if (error instanceof AuthFailureError) {
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
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("auth-key", AppInstanceSettings.load(AppInstanceSettings.class,1).getUserkey());
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
        Log.d("oxinbo","Server Logs"+params.toString());
    }

    private void ApplozicUserLogin(final String UserID,
                                   final String userDisplayName,
                                   String UserAvator,
                                   boolean isPersonalAccount,
                                   final String companyName,
                                   final String companyID,
                                   final String department){

        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {

                ApplozicClient.getInstance(context).enableNotification();
                ApplozicClient.getInstance(context).hideChatListOnNotification();

                Log.d("aPPLOZIC Succes",registrationResponse.toString());
                ApplozicClient.getInstance(context).setContextBasedChat(true).setHandleDial(true).setIPCallEnabled(true);
                java.util.Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
                activityCallbacks.put(ApplozicSetting.RequestCode.AUDIO_CALL, AudioCallActivityV2.class.getName());
                activityCallbacks.put(ApplozicSetting.RequestCode.VIDEO_CALL, VideoActivity.class.getName());
                ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks);


                PushNotificationTask.TaskListener pushNotificationTaskListener=  new PushNotificationTask.TaskListener() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse) {


                      /**  Intent intent = new Intent(CompanyDetails.this, ConversationActivity.class);
                        intent.putExtra(ConversationUIService.USER_ID,companyID);
                        intent.putExtra(ConversationUIService.DISPLAY_NAME, companyName +  department); //put it for displaying the title.
                        startActivity(intent);
***/
                    }

                    @Override
                    public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                    }
                };
                PushNotificationTask pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(),pushNotificationTaskListener,context);
                pushNotificationTask.execute((Void)null);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                // Log.d("aPPLOZIC Failed",registrationResponse.toString());
            }
        };


        com.applozic.mobicomkit.api.account.user.User applozicUser = new com.applozic.mobicomkit.api.account.user.User();
        applozicUser.setUserId(UserID);

        applozicUser.setDisplayName(userDisplayName);
        applozicUser.setImageLink(UserAvator);
        List<String> featureList =  new ArrayList<>();
        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_AUDIO_CALL.getValue());// FOR AUDIO
        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_VIDEO_CALL.getValue());// FOR VIDEO
        applozicUser.setFeatures(featureList);

        applozicUser.setAuthenticationTypeId(com.applozic.mobicomkit.api.account.user.User.AuthenticationType.APPLOZIC.getValue());
        new UserLoginTask(applozicUser, listener, this).execute((Void) null);




    }
    private void ApplozicUserLogin(){

        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {

                ApplozicClient.getInstance(context).enableNotification();
                ApplozicClient.getInstance(context).hideChatListOnNotification();

                Log.d("aPPLOZIC Succes",registrationResponse.toString());
                ApplozicClient.getInstance(context).setContextBasedChat(true).setHandleDial(true).setIPCallEnabled(true);
                java.util.Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
                activityCallbacks.put(ApplozicSetting.RequestCode.AUDIO_CALL, AudioCallActivityV2.class.getName());
                activityCallbacks.put(ApplozicSetting.RequestCode.VIDEO_CALL, VideoActivity.class.getName());
                ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks);


                PushNotificationTask.TaskListener pushNotificationTaskListener=  new PushNotificationTask.TaskListener() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse) {

                        Log.d("ApplozicSuccess",registrationResponse.getMessage().toString());
                    }

                    @Override
                    public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                        Log.d("ApplozicFailed",registrationResponse.getMessage().toString());

                    }
                };
                PushNotificationTask pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(),pushNotificationTaskListener,context);
                pushNotificationTask.execute((Void)null);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                // Log.d("aPPLOZIC Failed",registrationResponse.toString());
            }
        };


        com.applozic.mobicomkit.api.account.user.User applozicUser = new com.applozic.mobicomkit.api.account.user.User();
        applozicUser.setUserId(user.getUserkey());

        applozicUser.setDisplayName(user.getFname() + "  " + user.getLname());
       // applozicUser.setEmail(user.getEmail());
        //applozicUser.setContactNumber(user.getPhone());
        List<String> featureList =  new ArrayList<>();
        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_AUDIO_CALL.getValue());// FOR AUDIO
        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_VIDEO_CALL.getValue());// FOR VIDEO
        applozicUser.setFeatures(featureList);

        applozicUser.setAuthenticationTypeId(com.applozic.mobicomkit.api.account.user.User.AuthenticationType.APPLOZIC.getValue());
        new UserLoginTask(applozicUser, listener, this).execute((Void) null);




    }

}
