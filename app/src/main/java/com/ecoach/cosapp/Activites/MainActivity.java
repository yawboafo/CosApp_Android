package com.ecoach.cosapp.Activites;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

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
import com.applozic.audiovideo.activity.*;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.Clive.RatingDialog;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.MessageCommunicator;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComKitActivityInterface;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.ecoach.cosapp.Activites.Company.CompanyDetails;
import com.ecoach.cosapp.Activites.Company.ManageReps.ManageReps;
import com.ecoach.cosapp.Activites.Company.ManangeMyCompanies;
import com.ecoach.cosapp.Activites.UserAccounts.LoginActivity;
import com.ecoach.cosapp.Activites.UserAccounts.ProfileEditActivity;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.DataBase.AppInstanceSettings;
import com.ecoach.cosapp.DataBase.Categories;
import com.ecoach.cosapp.DataBase.CompanyRepInvite;
import com.ecoach.cosapp.DataBase.User;
import com.ecoach.cosapp.Http.APIRequest;
import com.ecoach.cosapp.Http.Terminator2;
import com.ecoach.cosapp.Http.VolleySingleton;
import com.ecoach.cosapp.R;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import com.ecoach.cosapp.Fragments.CategoriesFragment;
import com.ecoach.cosapp.Fragments.HomeFragment;
import com.ecoach.cosapp.Fragments.RecentFragment;
import com.ecoach.cosapp.RecycleAdapters.MainCategoryAdapter;
import com.ecoach.cosapp.Utilities.CircularTextView;
import com.ecoach.cosapp.Utilities.ViewUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.joanzapata.iconify.widget.IconButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MaterialTabListener,HomeFragment.OnFragmentInteractionListener,
        CategoriesFragment.OnFragmentInteractionListener,
        RecentFragment.OnFragmentInteractionListener,MessageCommunicator, MobiComKitActivityInterface,RatingDialog.OnFragmentInteractionListener {

    private FirebaseAuth auth;

    private static int retry;
    public LinearLayout layout;
    public Snackbar snackbar;
    MobiComKitBroadcastReceiver mobiComKitBroadcastReceiver;
    ConversationUIService conversationUIService;
  MobiComQuickConversationFragment mobiComQuickConversationFragment;


    ViewPagerAdapter pagerAdapter;
    MaterialTabHost tabHost;
    ViewPager pager;
    AppInstanceSettings appInstanceSettings;
    User user;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private boolean isLoggedIn = false;
    Timer timer = new Timer();
    int count = 0;
    TextView userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();


        // find MenuItem you want to change
        //MenuItem loginOLogout = menu.findItem(R.id.logout);
       // loginOLogout.setTitle("Login");
        //loginOLogout.se

        try {
            ApplozicApplication.cosappUserID = Application.AppUserKey;
            mobiComQuickConversationFragment = new MobiComQuickConversationFragment();
        }catch (Exception w){

            w.printStackTrace();
        }
        navigationView.setNavigationItemSelectedListener(this);
        setTabHost();



        setNavHeaderItems(navigationView);


        try{

            count = CompanyRepInvite.getCompanyRepInvitations(Application.AppUserKey).size();

            Log.d("Notifications", count + "<<count   +   Key>>" + Application.AppUserKey+ " user lname : "+User.getUserByKey(Application.AppUserKey).getLname()+ " user id" +User.getUserByKey(Application.AppUserKey).getId() );
            invalidateOptionsMenu();

        }catch (Exception e){

         e.printStackTrace();
        }



        try{



            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {

                   // startService(new Intent(MainActivity.this, Terminator2.class));
                    invalidateOptionsMenu();
                }
            }, 0, 3000);


        }catch (Exception e){e.printStackTrace();}




        try{

            conversationUIService = new ConversationUIService(this, mobiComQuickConversationFragment);
            mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(this, mobiComQuickConversationFragment);
            new MobiComConversationService(this).processLastSeenAtStatus();

        }catch (Exception e){

            e.printStackTrace();
        }





    }



    private void getFireBaseUserToken(){


        try {


            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            mUser.getToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();

                                Log.d("firebase","idToken  : "+idToken);
                                // Send token to your backend via HTTPS
                                // ...
                            } else {
                                // Handle error -> task.getException();
                            }
                        }
                    });

        }catch (Exception e){

            e.printStackTrace();
        }
    }



    private void setNavHeaderItems(NavigationView navigationView){

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.userName);
        TextView nav_email = (TextView)hView.findViewById(R.id.userEmail);

        Switch switch1 = (Switch)hView.findViewById(R.id.switch1);


        CircleImageView imageView = (CircleImageView)hView.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try{

                    if(appInstanceSettings.isloggedIn() == false){
                        new SweetAlertDialog(MainActivity.this)
                                .setTitleText("You need to login !")
                                .show();

                    }else{
                        Intent intent = new Intent(MainActivity.this,ProfileEditActivity.class);
                        startActivity(intent);

                    }

                }catch (Exception e){

                    new SweetAlertDialog(MainActivity.this)
                            .setTitleText("You need to login !")
                            .show();

                }





            }
        });


        try{
             appInstanceSettings = AppInstanceSettings.load(AppInstanceSettings.class,1);
             user = User.getUserByKey(appInstanceSettings.getUserkey());

            isLoggedIn = appInstanceSettings.isloggedIn();



            if(isLoggedIn ){

                nav_user.setText(user.getFname() + " " + user.getLname());
                nav_email.setText(user.getEmail());


                navigationView.getMenu().findItem(R.id.comp).setVisible(true);
                navigationView.getMenu().findItem(R.id.login).setVisible(false);
                navigationView.getMenu().findItem(R.id.logout).setVisible(true);
                navigationView.getMenu().findItem(R.id.manage).setVisible(true);
               // navigationView.getMenu().findItem(R.id.recent).setVisible(true);


                try {

                    String avatorPath= user.getPath()+user.getStorage()+"/"+user.getAvatar();

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRoundRect(user.getFname().toString().substring(0,1), MainActivity.this.getResources().getColor(R.color.colorPrimary), 10); // radius in px

                    Drawable d = new BitmapDrawable(ViewUtils.drawableToBitmap(drawable));

                    Picasso.with(MainActivity.this)
                            .load(avatorPath)
                            .placeholder(d)
                            .into(imageView);

                }catch (Exception e){


                }




                ApplozicUserLogin();
              //  fireBaseLogin();
            }else{

                nav_user.setText("You are not logged in");
                navigationView.getMenu().findItem(R.id.comp).setVisible(false);
                navigationView.getMenu().findItem(R.id.login).setVisible(true);
                navigationView.getMenu().findItem(R.id.logout).setVisible(false);
               // navigationView.getMenu().findItem(R.id.recent).setVisible(false);

                //check if user is a rep

                navigationView.getMenu().findItem(R.id.manage).setVisible(false);
                navigationView.getMenu().findItem(R.id.stats).setVisible(false);
            }

        }catch (Exception e){

            navigationView.getMenu().findItem(R.id.login).setVisible(true);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);

            e.printStackTrace();
        }




    }

    private void repIsOnline(String s) {


    }
    private void initiateChat(){


    }
    //initialise the android maetial design tab host
    private void setTabHost() {

        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);

        pager = (ViewPager) this.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        // init view pager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);

            }


        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(pagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }



    }

    private void logout(){

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Logout!")
                .setContentText("Are you sure you want to logout ?")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        AppInstanceSettings appInstanceSettings = AppInstanceSettings.load(AppInstanceSettings.class,1);
                        Application.AppUserKey = "";
                         appInstanceSettings.setIsloggedIn(false);
                         appInstanceSettings.setUserkey("");
                         appInstanceSettings.save();

                        new UserClientService(MainActivity.this).logout();
                        ProcessPhoenix.triggerRebirth(MainActivity.this);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }

                })
                .show();




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.cat) {
            Intent intent = new Intent(MainActivity.this,MainCategories.class);
            startActivity(intent);
        } else if (id == R.id.recent) {
          //  Intent intent = new Intent(MainActivity.this,MainRecentChats.class);
            //startActivity(intent);

           // Intent intent = new Intent(MainActivity.this,ConversationActivity.class);
            //startActivity(intent);
        } else if (id == R.id.comp) {

            Intent intent = new Intent(MainActivity.this,ManangeMyCompanies.class);
            startActivity(intent);

        } else if (id == R.id.manage) {
            Intent intent = new Intent(MainActivity.this,ManageReps.class);
            startActivity(intent);

        } else if (id == R.id.stats) {

        } else if (id == R.id.share) {

        } else if (id == R.id.logout){

            logout();

        }else if(id == R.id.login){

            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        MenuItem item = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(item, R.layout.badge_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

       TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);

        ImageButton imageButton=(ImageButton)notifCount.findViewById(R.id.imagebutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try{

                    if(AppInstanceSettings.load(AppInstanceSettings.class,1).isloggedIn() == false){

                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Sorry,")
                                .setContentText("You need to Login")
                                .setConfirmText("Login")
                                .setCancelText("Cancel")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismiss();
                                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .show();




                    }else{

                        Intent intent = new Intent(MainActivity.this,NotificationCenter.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                        startActivity(intent);

                    }


                }catch (Exception e){

                  e.printStackTrace();

                }










            }
        });
      //  tv.setStrokeWidth(1);

        try{

            count = CompanyRepInvite.getCompanyRepInvitations(Application.AppUserKey).size();
            if(count == 0){
                tv.setVisibility(View.INVISIBLE);


            }else{

                tv.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){


        }



        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public void onTabSelected(MaterialTab tab) {

        pager.setCurrentItem(tab.getPosition());

        Log.d("tab",tab.getPosition()+"");

    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private String[] titles = {
                "Home", "Categories","Recent Chats"
        };

        Fragment fragment=null;


        //create a viewpager method to handle fragments call

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {

/*
*  switch (index) {
           case 0:
               // Top Rated fragment activity
               return new Categories().newInstance("","");
           case 1:
               // Games fragment activity
               return new RecentSearch().newInstance("","");
           case 2:
               // Movies fragment activity
               return new RecentSearch().newInstance("","");
       }
*
*
* */

            if(num==0)
            {



                fragment = new HomeFragment().newInstance("", "");
            } if(num==1)
            {

                fragment=new CategoriesFragment().newInstance("","");



            }if(num==2){

                try{
                    appInstanceSettings = AppInstanceSettings.load(AppInstanceSettings.class,1);
                    user = User.getUserByKey(appInstanceSettings.getUserkey());

                    isLoggedIn = appInstanceSettings.isloggedIn();





                }catch (Exception e){



                    e.printStackTrace();
                }




                try {

                    if(isLoggedIn ){



                       // mobiComQuickConversationFragment.onResume();
                        fragment = mobiComQuickConversationFragment;

                    }else{

                        fragment=  new RecentFragment().newInstance("","");
                    }

                }catch (Exception e){


                }
              // fragment=  new RecentFragment().newInstance("","");

            }
            return fragment;




        }



        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

    private void ApplozicUserLogin(){

      UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
          @Override
          public void onSuccess(RegistrationResponse registrationResponse, Context context) {

              ApplozicClient.getInstance(context).enableNotification();
              //ApplozicClient.getInstance(context).hideChatListOnNotification();
             // ApplozicClient.getInstance(context).setContextBasedChat(true);


              Log.d("aPPLOZIC Succes",registrationResponse.toString());
              ApplozicClient.getInstance(context).setContextBasedChat(true).setHandleDial(true).setIPCallEnabled(true);
              Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
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
        applozicUser.setEmail(user.getEmail());
        applozicUser.setContactNumber(user.getPhone());
       List<String> featureList =  new ArrayList<>();
       featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_AUDIO_CALL.getValue());// FOR AUDIO
       featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_VIDEO_CALL.getValue());// FOR VIDEO
       applozicUser.setFeatures(featureList);

      applozicUser.setAuthenticationTypeId(com.applozic.mobicomkit.api.account.user.User.AuthenticationType.APPLOZIC.getValue());
      new UserLoginTask(applozicUser, listener, this).execute((Void) null);




  }

    @Override
    public void showErrorMessageView(String message) {

        try {
            layout.setVisibility(View.VISIBLE);
            snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setDuration(Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snackbar.getView();
            TextView textView = (TextView) group.findViewById(R.id.snackbar_action);
            textView.setTextColor(Color.YELLOW);
            group.setBackgroundColor(getResources().getColor(R.color.error_background_color));
            TextView txtView = (TextView) group.findViewById(R.id.snackbar_text);
            txtView.setMaxLines(5);
            snackbar.show();
        }catch (Exception e){

            e.printStackTrace();
        }

    }

    @Override
    public void retry() {
        retry++;
    }

    @Override
    public int getRetryCount() {
        return retry;
    }

    public void dismissErrorMessage() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        final String deviceKeyString = MobiComUserPreference.getInstance(this).getDeviceKeyString();
        final String userKeyString = MobiComUserPreference.getInstance(this).getSuUserKeyString();
        Intent intent = new Intent(this, ApplozicMqttIntentService.class);
        intent.putExtra(ApplozicMqttIntentService.USER_KEY_STRING, userKeyString);
        intent.putExtra(ApplozicMqttIntentService.DEVICE_KEY_STRING, deviceKeyString);
        startService(intent);
    }











    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mobiComKitBroadcastReceiver, BroadcastService.getIntentFilter());
        Intent subscribeIntent = new Intent(this, ApplozicMqttIntentService.class);
        subscribeIntent.putExtra(ApplozicMqttIntentService.SUBSCRIBE, true);
        startService(subscribeIntent);

        if (!Utils.isInternetAvailable(getApplicationContext())) {
            String errorMessage = getResources().getString(R.string.internet_connection_not_available);
            showErrorMessageView(errorMessage);
        }
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onQuickConversationFragmentItemClick(View view, Contact contact, Channel channel, Integer conversationId, String searchString) {
        ApplozicApplication.cosappUserID = Application.AppUserKey;
        Intent intent = new Intent(this, ConversationActivity.class);
        if(ApplozicClient.getInstance(this).isContextBasedChat()){
            intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT,true);
        }
        intent.putExtra(ConversationUIService.TAKE_ORDER, true);
        intent.putExtra(ConversationUIService.SEARCH_STRING, searchString);
        intent.putExtra(ConversationUIService.CONVERSATION_ID, conversationId);
        if (contact != null) {
            intent.putExtra(ConversationUIService.USER_ID, contact.getUserId());
            intent.putExtra(ConversationUIService.DISPLAY_NAME, contact.getDisplayName());
        } else if (channel != null) {
            intent.putExtra(ConversationUIService.GROUP_ID, channel.getKey());
            intent.putExtra(ConversationUIService.GROUP_NAME, channel.getName());
        }
        startActivity(intent);
    }

    @Override
    public void startContactActivityForResult() {
        ApplozicApplication.cosappUserID = Application.AppUserKey;
        conversationUIService.startContactActivityForResult();
    }
    @Override
    public void addFragment(ConversationFragment conversationFragment) {
        ApplozicApplication.cosappUserID = Application.AppUserKey;
    }



    @Override
    public void updateLatestMessage(final Message message, final String formattedContactNumber) {
        ApplozicApplication.cosappUserID = Application.AppUserKey;
        conversationUIService.updateLatestMessage(message, formattedContactNumber);
    }

    @Override
    public void removeConversation(Message message, String formattedContactNumber) {
        ApplozicApplication.cosappUserID = Application.AppUserKey;
        conversationUIService.removeConversation(message, formattedContactNumber);
    }




}
