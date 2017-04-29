package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.MobiComMessageService;
import com.applozic.mobicomkit.api.conversation.database.ConversationDatabaseService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.broadcast.ConnectivityReceiver;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.Clive.Models.ConversationChat;
import com.applozic.mobicomkit.uiwidgets.Clive.Models.ConversationModel;
import com.applozic.mobicomkit.uiwidgets.Clive.RatingDialog;
import com.applozic.mobicomkit.uiwidgets.Clive.VolleySingleton;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.async.AlSyncAccountStatusTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.MessageCommunicator;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.AudioMessageFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MultimediaOptionFragment;
import com.applozic.mobicomkit.uiwidgets.instruction.ApplozicPermissions;
import com.applozic.mobicomkit.uiwidgets.instruction.InstructionUtil;
import com.applozic.mobicomkit.uiwidgets.people.fragment.ProfileFragment;
import com.applozic.mobicomkit.uiwidgets.uilistener.MobicomkitUriListener;
import com.applozic.mobicommons.commons.core.utils.PermissionsUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.SearchListFragment;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.applozic.mobicomkit.uiwidgets.R.id.message;


/**
 * Created by devashish on 6/25/2015.
 */
public class ConversationActivity extends AppCompatActivity implements
        MessageCommunicator,
        MobiComKitActivityInterface,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback,MobicomkitUriListener
        ,SearchView.OnQueryTextListener,
       RatingDialog.OnFragmentInteractionListener{

    public static final int LOCATION_SERVICE_ENABLE = 1001;
    public static final String TAKE_ORDER = "takeOrder";
    public static final String CONTACT = "contact";
    public static final String CHANNEL = "channel";
    public static final String CONVERSATION_ID = "conversationId";
    public static final String GOOGLE_API_KEY_META_DATA = "com.google.android.geo.API_KEY";
    public static final String ACTIVITY_TO_OPEN_ONCLICK_OF_CALL_BUTTON_META_DATA = "activity.open.on.call.button.click";
    protected static final long UPDATE_INTERVAL = 500;
    protected static final long FASTEST_INTERVAL = 1;
    private static final String LOAD_FILE = "loadFile";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String API_KYE_STRING = "YOUR_GEO_API_KEY";
    private static final String CAPTURED_IMAGE_URI = "capturedImageUri";
    private static final String CAPTURED_VIDEO_URI = "capturedVideoUri";
    private static final String SHARE_TEXT = "share_text";
    private static Uri capturedImageUri;
    private static String inviteMessage;
    protected ConversationFragment conversation;
    protected MobiComQuickConversationFragment quickConversationFragment;
    protected MobiComKitBroadcastReceiver mobiComKitBroadcastReceiver;
    protected ActionBar mActionBar;
    protected GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public Contact contact;
    private Channel channel;
    private static int retry;
    public LinearLayout layout;
    String geoApiKey;
    String activityToOpenOnClickOfCallButton;
    int resourceId;
    RelativeLayout childFragmentLayout;
    public boolean isTakePhoto;
    public boolean isAttachment;
    private BaseContactService baseContactService;
    private ApplozicPermissions applozicPermission;
    public Integer currentConversationId;
    private Uri videoFileUri;
    private Uri imageUri;
    ProfileFragment profilefragment;
    MobiComMessageService  mobiComMessageService;
    private ConversationUIService conversationUIService;
    private SearchView searchView;
    private String searchTerm;
    private SearchListFragment searchListFragment;
    AlCustomizationSettings alCustomizationSettings;
    ConnectivityReceiver connectivityReceiver;
    private Calendar calendar;
    File mediaFile;
    File profilePhotoFile;
    Conversation conversationTopicDetails;
    String repID = "";
    String customerID = "";


    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    public ConversationActivity() {

    }

    public Snackbar snackbar;

    @Override
    public void showErrorMessageView(String message) {
        try{
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

    public static void addFragment(FragmentActivity fragmentActivity,
                                   Fragment fragmentToAdd,
                                   String fragmentTag) {
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();

        // Fragment activeFragment = UIService.getActiveFragment(fragmentActivity);
        FragmentTransaction fragmentTransaction = supportFragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.layout_child_activity, fragmentToAdd,
                fragmentTag);

        if (supportFragmentManager.getBackStackEntryCount() > 1
                && !ConversationUIService.MESSGAE_INFO_FRAGMENT.equalsIgnoreCase(fragmentTag)){
            supportFragmentManager.popBackStackImmediate();
        }
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
        supportFragmentManager.executePendingTransactions();
        //Log.i(TAG, "BackStackEntryCount: " + supportFragmentManager.getBackStackEntryCount());
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

        Log.d("conversationid","OnresumeconversationID"+BroadcastService.staticconversationID+"");
        try {
          //
            // getConversationDetailsToFile();

            conversationTopicDetails = ConversationDatabaseService.getInstance(ConversationActivity.this).getConversationByConversationId(BroadcastService.staticconversationID);
            String TopicDetails = conversationTopicDetails.getTopicDetail();

            Log.d("TopicDetails",TopicDetails);


        }catch (Exception e){

            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
        //ApplozicMqttService.getInstance(this).unSubscribe();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(CONTACT, contact);
        savedInstanceState.putSerializable(CHANNEL, channel);
        savedInstanceState.putSerializable(CONVERSATION_ID, currentConversationId);


        Log.d("conversation id",currentConversationId+"");

        if (capturedImageUri != null) {
            savedInstanceState.putString(CAPTURED_IMAGE_URI, capturedImageUri.toString());
        }
        if (videoFileUri != null) {
            savedInstanceState.putString(CAPTURED_VIDEO_URI, videoFileUri.toString());
        }
        if (mediaFile != null) {
            savedInstanceState.putSerializable(LOAD_FILE, mediaFile);
        }

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onSupportNavigateUp() {

      /**  ConversationFragment conversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentByTag(ConversationUIService.CONVERSATION_FRAGMENT);

        if(conversationFragment!=null){

            RatingDialog addRepsDialog=new RatingDialog().newInstance("","");
            showDialog(addRepsDialog,"checkout");

           // Toast.makeText(getApplicationContext(),"im not null",Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(getApplicationContext(),"im  null",Toast.LENGTH_SHORT).show();
        }
**/

    /**  Log.d("getBackEntryCount ",""+getSupportFragmentManager().getBackStackEntryCount());

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null && isTaskRoot()) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }


               // ConversationActivity.this.finish();
                return true;
            }
            Boolean takeOrder = getIntent().getBooleanExtra(TAKE_ORDER, false);
            if (takeOrder) {
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null && isTaskRoot()) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }
                ConversationActivity.this.finish();
                return true;
            } else {
                getSupportFragmentManager().popBackStack();
            }
            Utils.toggleSoftKeyBoard(this, true);
            return true;
        }
        return false;
     **/

       /**



        ConversationFragment conversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentByTag(ConversationUIService.CONVERSATION_FRAGMENT);

        if(conversationFragment!=null) {
            try {


                conversationTopicDetails = ConversationDatabaseService.getInstance(ConversationActivity.this).getConversationByConversationId(BroadcastService.staticconversationID);
            }catch (Exception e){

                e.printStackTrace();
            }
            try {

                String TopicDetails = conversationTopicDetails.getTopicDetail();

                JSONObject jObject  = new JSONObject(TopicDetails);
                repID = jObject.getString("key1");
                customerID = jObject.getString("key2");
                Log.d("USERIDS","Rep ID "+ repID + " customer ID" +ApplozicApplication.cosappUserID);

                if(customerID.equals(ApplozicApplication.cosappUserID)){

                    Log.d("checkRep","its not a rep chat");
                    RatingDialog addRepsDialog=new RatingDialog().newInstance("","");
                    showDialog(addRepsDialog,"checkout");
                }else {
                    Log.d("checkRep","its a rep chat");

                }

            }catch (Exception e){

                e.printStackTrace();
            }





            // Toast.makeText(getApplicationContext(),"im not null",Toast.LENGTH_SHORT).show();
        }**/

        //return true;

      /**  if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null && isTaskRoot()) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }
                ConversationActivity.this.finish();
                return true;
            }
            Boolean takeOrder = getIntent().getBooleanExtra(TAKE_ORDER, false);
            if (takeOrder) {
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null && isTaskRoot()) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }
                ConversationActivity.this.finish();
                return true;
            } else {
                getSupportFragmentManager().popBackStack();
            }
            Utils.toggleSoftKeyBoard(this, true);
            return true;
        }**/


        try {

            Log.d("isRated","is Conversation Rated "+ get4rmSharedPreference(BroadcastService.staticconversationID.toString()));

        }catch (Exception e){

            e.printStackTrace();
        }

            try {


                conversationTopicDetails = ConversationDatabaseService.getInstance(ConversationActivity.this).getConversationByConversationId(BroadcastService.staticconversationID);
            }catch (Exception e){

                e.printStackTrace();
            }
            try {

                String TopicDetails = conversationTopicDetails.getTopicDetail();

                JSONObject jObject  = new JSONObject(TopicDetails);
                repID = jObject.getString("key1");
                customerID = jObject.getString("key2");
                Log.d("USERIDS","Rep ID "+ repID + " customer ID" +ApplozicApplication.cosappUserID);

                if(customerID.equals(ApplozicApplication.cosappUserID) && get4rmSharedPreference(BroadcastService.staticconversationID.toString())== false){


                    Log.d("checkRep","its not a rep chat");
                    RatingDialog addRepsDialog=new RatingDialog().newInstance("","");
                    showDialog(addRepsDialog,"checkout");

                    ///onBackPressed();
                }else {
                    ConversationActivity.this.finish();
                    return true;

                }

            }catch (Exception e){

                e.printStackTrace();
            }




        //ConversationActivity.this.finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Log.d("ApplozicUserID", "AppCosapp"+ApplozicApplication.cosappUserID);


        baseContactService =  new AppContactService(this);
        conversationUIService =  new ConversationUIService(this);
        mobiComMessageService = new MobiComMessageService(this, MessageIntentService.class);
    //    quickConversationFragment = new MobiComQuickConversationFragment();
        String jsonString = FileUtils.loadSettingsJsonFile(getApplicationContext());
        if(!TextUtils.isEmpty(jsonString)){
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString,AlCustomizationSettings.class);
        }else {
            alCustomizationSettings = new AlCustomizationSettings();
        }

        if(!TextUtils.isEmpty(alCustomizationSettings.getChatBackgroundImageName())){
            resourceId = getResources().getIdentifier(alCustomizationSettings.getChatBackgroundImageName(), "drawable", getPackageName());
        }
        if(resourceId != 0){
            getWindow().setBackgroundDrawableResource(resourceId);
        }
        setContentView(R.layout.quickconversion_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        connectivityReceiver = new ConnectivityReceiver();
        geoApiKey = Utils.getMetaDataValue(getApplicationContext(), GOOGLE_API_KEY_META_DATA);
        activityToOpenOnClickOfCallButton = Utils.getMetaDataValue(getApplicationContext(), ACTIVITY_TO_OPEN_ONCLICK_OF_CALL_BUTTON_META_DATA);
        layout = (LinearLayout) findViewById(R.id.footerAd);
        applozicPermission = new ApplozicPermissions(this, layout);
        childFragmentLayout = (RelativeLayout) findViewById(R.id.layout_child_activity);
        profilefragment =  new ProfileFragment();
        profilefragment.setAlCustomizationSettings(alCustomizationSettings);






        if (Utils.hasMarshmallow()) {
            applozicPermission.checkRuntimePermissionForStorage();
        }
        mActionBar = getSupportActionBar();
        inviteMessage = Utils.getMetaDataValue(getApplicationContext(), SHARE_TEXT);
        retry = 0;
        if (savedInstanceState != null) {
            capturedImageUri = savedInstanceState.getString(CAPTURED_IMAGE_URI) != null ?
                    Uri.parse(savedInstanceState.getString(CAPTURED_IMAGE_URI)) : null;
            videoFileUri = savedInstanceState.getString(CAPTURED_VIDEO_URI) != null ?
                    Uri.parse(savedInstanceState.getString(CAPTURED_VIDEO_URI)) : null;
            mediaFile = savedInstanceState.getSerializable(LOAD_FILE) != null? (File) savedInstanceState.getSerializable(LOAD_FILE) :null;

            contact = (Contact) savedInstanceState.getSerializable(CONTACT);
            channel = (Channel) savedInstanceState.getSerializable(CHANNEL);
            currentConversationId = savedInstanceState.getInt(CONVERSATION_ID);


            Log.d("conversationid","conversationID"+BroadcastService.staticconversationID+"");

            if(contact != null || channel != null ){
                if (channel != null) {
                    conversation = new ConversationFragment(null, channel,currentConversationId);
                } else {
                    conversation = new ConversationFragment(contact, null,currentConversationId);
                }
                addFragment(this, conversation, ConversationUIService.CONVERSATION_FRAGMENT);
            }
        }/** else {
            setSearchListFragment(quickConversationFragment);
            addFragment(this, quickConversationFragment,
         ConversationUIService.QUICK_CONVERSATION_FRAGMENT);
        }**/
        mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(this);
        InstructionUtil.showInfo(this, R.string.info_message_sync, BroadcastService.INTENT_ACTIONS.INSTRUCTION.toString());

        mActionBar.setTitle(R.string.conversations);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        onNewIntent(getIntent());

        Boolean takeOrder = getIntent().getBooleanExtra(TAKE_ORDER, false);

        if(!takeOrder){
            new MobiComConversationService(getApplicationContext()).processLastSeenAtStatus();
        }

        new AlSyncAccountStatusTask(this, new AlSyncAccountStatusTask.TaskListener() {
            @Override
            public void onCompletion(Context context) {
                try {
                    if (ApplozicClient.getInstance(context).isAccountClosed() || ApplozicClient.getInstance(context).isNotAllowed()) {
                        snackbar = Snackbar.make(layout, ApplozicClient.getInstance(context).isAccountClosed() ?
                                        R.string.applozic_account_closed : R.string.applozic_free_version_not_allowed_on_release_build,
                                Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                    }
                } catch (Exception e) {
                }
            }
        }).execute((Void)null);

        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));





    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //setIntent(intent);
        if (!MobiComUserPreference.getInstance(this).isLoggedIn()) {
            //user is not logged in
            Log.i("AL", "user is not logged in yet.");
            return;
        }

        try {
            if(intent.getExtras() != null){
                BroadcastService.setContextBasedChat(intent.getExtras().getBoolean(ConversationUIService.CONTEXT_BASED_CHAT));
            }
            conversationUIService.checkForStartNewConversation(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showActionBar() {
        mActionBar.setDisplayShowTitleEnabled(true);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        showActionBar();
        //return false;
        getMenuInflater().inflate(R.menu.mobicom_basic_menu_for_normal_message, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        if (Utils.hasICS()) {
            searchItem.collapseActionView();
        }
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(true);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            conversationUIService.onActivityResult(requestCode, resultCode, data);
            handleOnActivityResult(requestCode,data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    if(data == null){
                        return;
                    }
                    if(imageUri != null){
                        imageUri = result.getUri();
                        if (imageUri != null && profilefragment != null) {
                            profilefragment.handleProfileimageUpload(false,imageUri,profilePhotoFile);
                        }
                    }else {
                        imageUri = result.getUri();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";
                        profilePhotoFile = FileClientService.getFilePath(imageFileName,this, "image/jpeg");
                        if (imageUri != null && profilefragment != null) {
                            profilefragment.handleProfileimageUpload(true,imageUri,profilePhotoFile);
                        }
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
            if (requestCode == LOCATION_SERVICE_ENABLE) {
                if (((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    googleApiClient.connect();
                } else {
                    Toast.makeText(ConversationActivity.this, R.string.unable_to_fetch_location, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void handleOnActivityResult(int requestCode, Intent intent) {

        switch (requestCode) {

            case ProfileFragment.REQUEST_CODE_ATTACH_PHOTO:
                Uri selectedFileUri = (intent == null ? null : intent.getData());
                imageUri = null;
                beginCrop(selectedFileUri);
                break;

            case ProfileFragment.REQUEST_CODE_TAKE_PHOTO:
                beginCrop(imageUri);
                break;

        }
    }

    void beginCrop(Uri imageUri){
        try{
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setMultiTouchEnabled(true)
                    .start(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionsUtils.REQUEST_STORAGE) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.storage_permission_granted);
                if(isAttachment){
                    isAttachment = false;
                    processAttachment();
                }
            } else {
                showSnackBar(R.string.storage_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_LOCATION) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.location_permission_granted);
                processingLocation();
            } else {
                showSnackBar(R.string.location_permission_not_granted);
            }

        } else if (requestCode == PermissionsUtils.REQUEST_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_state_permission_granted);
            } else {
                showSnackBar(R.string.phone_state_permission_not_granted);
            }
        }
        else if (requestCode == PermissionsUtils.REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_call_permission_granted);
                processCall(contact,currentConversationId);
            } else {
                showSnackBar(R.string.phone_call_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_AUDIO_RECORD) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.record_audio_permission_granted);
                showAudioRecordingDialog();
            } else {
                showSnackBar(R.string.record_audio_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_camera_permission_granted);
                if (isTakePhoto) {
                    processCameraAction();
                } else {
                    processVideoRecording();
                }
            } else {
                showSnackBar(R.string.phone_camera_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CONTACT) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.contact_permission_granted);
                processContact();
            } else {
                showSnackBar(R.string.contact_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA_FOR_PROFILE_PHOTO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_camera_permission_granted);
                if(profilefragment != null){
                    profilefragment.processPhotoOption();
                }
            } else {
                showSnackBar(R.string.phone_camera_permission_not_granted);
            }
        }else if (requestCode == PermissionsUtils.REQUEST_STORAGE_FOR_PROFILE_PHOTO) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.storage_permission_granted);
                if(profilefragment != null){
                    profilefragment.processPhotoOption();
                }
            } else {
                showSnackBar(R.string.storage_permission_not_granted);
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    public void processingLocation() {
        if (alCustomizationSettings.isLocationShareViaMap() && !TextUtils.isEmpty(geoApiKey) && !API_KYE_STRING.equals(geoApiKey)) {
            Intent toMapActivity = new Intent(this, MobicomLocationActivity.class);
            startActivityForResult(toMapActivity, MultimediaOptionFragment.REQUEST_CODE_SEND_LOCATION);
            Log.i("test", "Activity for result strarted");

        } else {
            //================= START GETTING LOCATION WITHOUT LOADING MAP AND SEND LOCATION AS TEXT===============

            if (!((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.location_services_disabled_title)
                        .setMessage(R.string.location_services_disabled_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.location_service_settings, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, LOCATION_SERVICE_ENABLE);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(ConversationActivity.this, R.string.location_sending_cancelled, Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                googleApiClient.disconnect();
                googleApiClient.connect();
            }

            //=================  END ===============

        }

    }

    public void processLocation() {
        if (Utils.hasMarshmallow()) {
            new ApplozicPermissions(ConversationActivity.this, layout).checkRuntimePermissionForLocation();
        } else {
            processingLocation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.start_new) {
            conversationUIService.startContactActivityForResult();
        } else if (id == R.id.conversations) {
            Intent intent = new Intent(this, ChannelCreateActivity.class);
            intent.putExtra(ChannelCreateActivity.GROUP_TYPE,Channel.GroupType.PUBLIC.getValue().intValue());
            startActivity(intent);
        }else if(id == R.id.broadcast){
            Intent intent = new Intent(this, ContactSelectionActivity.class);
            intent.putExtra(ContactSelectionActivity.GROUP_TYPE,Channel.GroupType.BROADCAST.getValue().intValue());
            startActivity(intent);
        }else if (id == R.id.refresh) {
            String message = this.getString(R.string.info_message_sync);
            mobiComMessageService.syncMessagesWithServer(message);
        } else if (id == R.id.shareOptions) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setAction(Intent.ACTION_SEND)
                    .setType("text/plain").putExtra(Intent.EXTRA_TEXT, inviteMessage);
            startActivity(Intent.createChooser(intent, "Share Via"));
            return super.onOptionsItemSelected(item);
        }else if(id == R.id.applozicUserProfile){
            profilefragment.setApplozicPermissions(applozicPermission);
            addFragment(this,profilefragment,ProfileFragment.ProfileFragmentTag);
        }
        return false;
    }

    @Override
    public void onQuickConversationFragmentItemClick(View view, Contact contact, Channel channel,Integer conversationId,String searchString) {
        conversation = new ConversationFragment(contact, channel,conversationId,searchString);
        addFragment(this, conversation, ConversationUIService.CONVERSATION_FRAGMENT);
        this.channel = channel;
        this.contact = contact;
        this.currentConversationId = conversationId;

        //channel.getKey()
    }

    @Override
    public void startContactActivityForResult() {
        conversationUIService.startContactActivityForResult();
    }

    @Override
    public void addFragment(ConversationFragment conversationFragment) {
        addFragment(this, conversationFragment, ConversationUIService.CONVERSATION_FRAGMENT);
        conversation = conversationFragment;
    }

    @Override
    public void onBackPressed() {


     /** if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            try{
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if(upIntent != null && isTaskRoot()){
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            this.finish();
            return;
        }
        Boolean takeOrder = getIntent().getBooleanExtra(TAKE_ORDER, false);
        ConversationFragment conversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentByTag(ConversationUIService.CONVERSATION_FRAGMENT);
        if (conversationFragment != null && conversationFragment.isVisible() && (conversationFragment.multimediaPopupGrid.getVisibility() == View.VISIBLE)) {
            conversationFragment.hideMultimediaOptionGrid();
            return;
        }
        if (takeOrder){
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (upIntent != null && isTaskRoot()) {
                TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
            }
            ConversationActivity.this.finish();
        }else {
            super.onBackPressed();
        }
**/

       /** Conversation conversation = ConversationDatabaseService.getInstance(ConversationActivity.this).getConversationByConversationId(BroadcastService.staticconversationID);

        Log.d("ConTopic",conversation.getTopicDetail().toString());

        RatingDialog addRepsDialog = new RatingDialog().newInstance("", "");
        showDialog(addRepsDialog, "checkout");
        **/
        try {


            conversationTopicDetails = ConversationDatabaseService.getInstance(ConversationActivity.this).getConversationByConversationId(BroadcastService.staticconversationID);
        }catch (Exception e){

            e.printStackTrace();
        }
        try {

            String TopicDetails = conversationTopicDetails.getTopicDetail();

            JSONObject jObject  = new JSONObject(TopicDetails);
            repID = jObject.getString("key1");
            customerID = jObject.getString("key2");
            Log.d("USERIDS","Rep ID "+ repID + " customer ID" +ApplozicApplication.cosappUserID);

            if(get4rmSharedPreference(BroadcastService.staticconversationID.toString())== false){


                Log.d("checkRep","its not a rep chat");
                RatingDialog addRepsDialog=new RatingDialog().newInstance("","");
                showDialog(addRepsDialog,"checkout");

                ///onBackPressed();
            }else {
                ConversationActivity.this.finish();

            }

        }catch (Exception e){

            e.printStackTrace();
        }

    }

    @Override
    public void updateLatestMessage(Message message, String formattedContactNumber) {
        conversationUIService.updateLatestMessage(message, formattedContactNumber);

    }

    @Override
    public void removeConversation(Message message, String formattedContactNumber) {
        conversationUIService.removeConversation(message, formattedContactNumber);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (mCurrentLocation == null) {
                Toast.makeText(this, R.string.waiting_for_current_location, Toast.LENGTH_SHORT).show();
                locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(UPDATE_INTERVAL);
                locationRequest.setFastestInterval(FASTEST_INTERVAL);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
            if (mCurrentLocation != null && conversation != null) {
                conversation.attachLocation(mCurrentLocation);
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(((Object) this).getClass().getSimpleName(),
                "onConnectionSuspended() called.");

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            if (conversation != null && location != null) {
                conversation.attachLocation(location);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            showErrorDialog(connectionResult.getErrorCode());
        }

    }

    public void setChildFragmentLayoutBG(){

        childFragmentLayout.setBackgroundResource(R.color.conversation_list_all_background);
    }
    public void setChildFragmentLayoutBGToTransparent(){

        childFragmentLayout.setBackgroundResource(android.R.color.transparent);
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
    }

    public Contact getContact() {
        return contact;
    }

    public Channel getChannel() {
        return channel;
    }

    public Integer getConversationId() {
        return currentConversationId;
    }

    public static Uri getCapturedImageUri() {
        return capturedImageUri;
    }

    public static void setCapturedImageUri(Uri capturedImageUri) {
        ConversationActivity.capturedImageUri = capturedImageUri;
    }

    public void showSnackBar(int resId) {
        snackbar = Snackbar.make(layout, resId,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public Uri getVideoFileUri() {
        return videoFileUri;
    }

    public void setVideoFileUri(Uri videoFileUri) {
        this.videoFileUri = videoFileUri;
    }

    public void isTakePhoto(boolean takePhoto) {
        this.isTakePhoto = takePhoto;
    }

    public void isAttachment(boolean attachment) {
        this.isAttachment = attachment;
    }

    public File getFileObject() {
        return mediaFile;
    }

    public void showAudioRecordingDialog() {

        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfPermissionForAudioRecording(this)) {
            new ApplozicPermissions(this, layout).requestAudio();
        } else if(PermissionsUtils.isAudioRecordingPermissionGranted(this)) {

            FragmentManager supportFragmentManager = getSupportFragmentManager();
            DialogFragment fragment = AudioMessageFragment.newInstance();

            FragmentTransaction fragmentTransaction = supportFragmentManager
                    .beginTransaction().add(fragment, "AudioMessageFragment");

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();

        }else{

            if(alCustomizationSettings.getAudioPermissionNotFoundMsg()==null){
                showSnackBar(R.string.applozic_audio_permission_missing);
            }else{
                snackbar = Snackbar.make(layout, alCustomizationSettings.getAudioPermissionNotFoundMsg(),
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

        }
    }


    public void processCall(Contact contactObj, Integer conversationId) {
        this.contact = baseContactService.getContactById(contactObj.getContactIds());
        this.currentConversationId = conversationId;
        try {
            if(ApplozicClient.getInstance(getApplicationContext()).isIPCallEnabled()){
                //Audio Call
                String activityName = ApplozicSetting.getInstance(this).getActivityCallback(ApplozicSetting.RequestCode.AUDIO_CALL);
                Class activityToOpen =  Class.forName(activityName);
                Intent intent = new Intent(this, activityToOpen);
                intent.putExtra("CONTACT_ID", contact.getUserId());
                startActivity(intent);
                return;
            }

            if (activityToOpenOnClickOfCallButton != null) {
                Intent callIntent = new Intent(this, Class.forName(activityToOpenOnClickOfCallButton));
                if (currentConversationId != null) {
                    Conversation conversation = ConversationService.getInstance(this).getConversationByConversationId(currentConversationId);
                    callIntent.putExtra(ConversationUIService.TOPIC_ID, conversation.getTopicId());
                }
                callIntent.putExtra(ConversationUIService.CONTACT, contact);
                startActivity(callIntent);
            } else if (alCustomizationSettings.isShowActionDialWithOutCalling()){
                if(!TextUtils.isEmpty(contact.getContactNumber())) {
                    Intent callIntent;
                    String uri = "tel:" + contact.getContactNumber().trim();
                    callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(uri));
                    startActivity(callIntent);
                }
            }else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCallPermission(this)) {
                    applozicPermission.requestCallPermission();
                } else if(PermissionsUtils.isCallPermissionGranted(this)){
                    if (!TextUtils.isEmpty(contact.getContactNumber())) {
                        Intent callIntent;
                        String uri = "tel:" + contact.getContactNumber().trim();
                        callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse(uri));
                        startActivity(callIntent);
                    }
                } else {
                    snackbar = Snackbar.make(layout,R.string.phone_call_permission_not_granted ,
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

        } catch (Exception e) {
            Log.i("ConversationActivity", "Call permission is not added in androidManifest");
        }
    }


    public void processCameraAction() {
        try {
            if (PermissionsUtils.isCameraPermissionGranted(this)) {
                imageCapture();
            } else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCameraPermission(this)) {
                    applozicPermission.requestCameraPermission();
                } else {
                    imageCapture();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processVideoRecording() {
        try{
            if(PermissionsUtils.isCameraPermissionGranted(this)){
                showVideoCapture();
            }else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCameraPermission(this)) {
                    applozicPermission.requestCameraPermission();
                } else {
                    showVideoCapture();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void processContact(){
        if(Utils.hasMarshmallow() && PermissionsUtils.checkSelfForContactPermission(this)){
            applozicPermission.requestContactPermission();
        }else {
            Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            contactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(contactIntent, MultimediaOptionFragment.REQUEST_CODE_CONTACT_SHARE);
        }
    }
    public void imageCapture() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";

            mediaFile = FileClientService.getFilePath(imageFileName, getApplicationContext(), "image/jpeg");

            capturedImageUri = FileProvider.getUriForFile(this,  Utils.getMetaDataValue(this, MobiComKitConstants.PACKAGE_NAME) + ".provider", mediaFile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip =
                        ClipData.newUri(getContentResolver(), "a Photo", capturedImageUri);

                cameraIntent.setClipData(clip);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                List<ResolveInfo> resInfoList =
                        getPackageManager()
                                .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, capturedImageUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(packageName, capturedImageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                if (mediaFile != null) {
                    startActivityForResult(cameraIntent, MultimediaOptionFragment.REQUEST_CODE_TAKE_PHOTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processAttachment(){
        if(Utils.hasMarshmallow() && PermissionsUtils.checkSelfForStoragePermission(this)){
            applozicPermission.requestStoragePermissions();
        }else {
            Intent intentPick = new Intent(this, MobiComAttachmentSelectorActivity.class);
            startActivityForResult(intentPick, MultimediaOptionFragment.REQUEST_MULTI_ATTCAHMENT);
        }
    }

    public void showVideoCapture() {

        try{
            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "VID_" + timeStamp + "_" + ".mp4";

            mediaFile = FileClientService.getFilePath(imageFileName, getApplicationContext(), "video/mp4");

            videoFileUri = FileProvider.getUriForFile(this, Utils.getMetaDataValue(this, MobiComKitConstants.PACKAGE_NAME)+".provider",mediaFile);

            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileUri);

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                videoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip=
                        ClipData.newUri(getContentResolver(), "a Video", videoFileUri);

                videoIntent.setClipData(clip);
                videoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                List<ResolveInfo> resInfoList=
                        getPackageManager()
                                .queryIntentActivities(videoIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, videoFileUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(packageName, videoFileUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

                }
            }

            if (videoIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                if (mediaFile != null) {
                    videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(videoIntent, MultimediaOptionFragment.REQUEST_CODE_CAPTURE_VIDEO_ACTIVITY);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public Uri getCurrentImageUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";
        profilePhotoFile = FileClientService.getFilePath(imageFileName, getApplicationContext(), "image/jpeg");
        imageUri = FileProvider.getUriForFile(this,  Utils.getMetaDataValue(this, MobiComKitConstants.PACKAGE_NAME) + ".provider", profilePhotoFile);
        return imageUri;
    }



    public void processGalleryPhotoSelection(){
        if(Utils.hasMarshmallow() && PermissionsUtils.checkSelfForStoragePermission(this)){
            applozicPermission.requestStoragePermissionsForProfilePhoto();
        }else {
            Intent getContentIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(getContentIntent, ProfileFragment.REQUEST_CODE_ATTACH_PHOTO);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.searchTerm = query;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        this.searchTerm = query;
        if (getSearchListFragment() != null) {
            getSearchListFragment().onQueryTextChange(query);
        }
        return true;
    }

    public SearchListFragment getSearchListFragment() {
        return searchListFragment;
    }

    public void setSearchListFragment(SearchListFragment searchListFragment) {
        this.searchListFragment = searchListFragment;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(connectivityReceiver != null){
                unregisterReceiver(connectivityReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(String rating_value ,String review_text) {


        try {

            String TopicDetails = conversationTopicDetails.getTopicDetail();

            Log.d("detail",TopicDetails);

            JSONObject jObject  = new JSONObject(TopicDetails);
            repID = jObject.getString("key1");
            customerID = jObject.getString("key2");
            String companyID = jObject.getString("value1");
            String chatsessionID = conversationTopicDetails.getTopicId();



            rateConversation(chatsessionID,companyID,rating_value ,review_text);




        }catch (Exception e){

            e.printStackTrace();
        }




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
     SweetAlertDialog pDialog = null;
    private void rateConversation(String chat_session, String company_id, String rating_value ,String review_text){


        pDialog = new SweetAlertDialog(ConversationActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Submitting Review ..");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d("companies","loading from initiateChat");

        final HashMap<String, String> params = new HashMap<String, String>();

/*
* is_rate_rep
chat_session
company_id
rating_value
review_text
*
*
*
* */

        params.put("is_rate_rep",""+ "1");
        params.put("chat_session",chat_session);
        params.put("company_id",company_id);
        params.put("rating_value",rating_value);
        params.put("review_text",review_text);





        volleySingleton= VolleySingleton.getsInstance(getApplicationContext());
        requestQueue=VolleySingleton.getRequestQueue(getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                ApplozicApplication.BASE_URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        pDialog.dismiss();
                        try {
                            Log.d("Params",response.toString()+"");
                            JSONObject object = response.optJSONObject("ecoachlabs");
                            String statuscode = object.getString("status");
                            String message = object.getString("msg");

                            if (!statuscode.equals("201")) {

                                new SweetAlertDialog(ConversationActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Sorry,Try Again")
                                        .setContentText(message)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();

                                            }
                                        })
                                        .show();

                            } else if (statuscode.equals("201")) {
                                //getConversationDetailsFromFileUpdateRating();

                                write2SharedPrefrence(BroadcastService.staticconversationID.toString(),true);

                                new SweetAlertDialog(ConversationActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Completed")
                                        .setContentText(message)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                sweetAlertDialog.dismiss();

                                            }
                                        })
                                        .show();
//

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
                new SweetAlertDialog(ConversationActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
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
                headers.put("auth-key",ApplozicApplication.cosappUserID);
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


    private void getConversationDetailsToFile(){

        Log.d("Map ","Map Key getting map key ");
        try {


            conversationTopicDetails = ConversationDatabaseService.getInstance(ConversationActivity.this).getConversationByConversationId(BroadcastService.staticconversationID);
        }catch (Exception e){

            e.printStackTrace();
        }
        try {

            String TopicDetails = conversationTopicDetails.getTopicDetail();

            JSONObject jObject  = new JSONObject(TopicDetails);
            repID = jObject.getString("key1");
            customerID = jObject.getString("key2");
            String companyID = jObject.getString("value1");
            String chatsessionID = conversationTopicDetails.getTopicId();

            ConversationModel conversationModel = new ConversationModel();
            conversationModel.setChat_session(chatsessionID);
            conversationModel.setCompanyID(companyID);
            conversationModel.setRepID(repID);
            conversationModel.setCustomerID(customerID);
            conversationModel.setConversationid(BroadcastService.staticconversationID);
           // conversationModel.setRated(false);
           // conversationModel.setMarkedAsread(false);

            ConversationChat conversationChat = ApplozicApplication.ReadConversationChat(ConversationActivity.this);


            for(Map.Entry<Integer,ConversationModel> entry : conversationChat.getMap().entrySet()) {
                Integer key = entry.getKey();
                //Integer value = entry.getValue();

                Log.d("Map ","Map Key "+key);
            }


            conversationChat.getMap().put(BroadcastService.staticconversationID,conversationModel);
            ApplozicApplication.SaveConversationChat(ConversationActivity.this,conversationChat);


            Log.d("MapSize","Map size"+conversationChat.getMap().size());



         //   ApplozicApplication.SaveConversationChat(conversationChat);



        }catch (Exception e){

            e.printStackTrace();
        }


    }

    private void getConversationDetailsFromFileUpdateRating(){

        ConversationChat conversationChat = ApplozicApplication.ReadConversationChat(ConversationActivity.this);
        ConversationModel conversationModel = null;


      //  conversationChat.getMap().get(BroadcastService.staticconversationID).setRated(true);

        Log.d("isRated","isRated " +  "setting conversation to rated ....");

        for(Map.Entry<Integer,ConversationModel> entry : conversationChat.getMap().entrySet()) {
            Integer key = entry.getKey();
            //Integer value = entry.getValue();

            if(key == BroadcastService.staticconversationID){
                conversationModel = entry.getValue();


                conversationModel.setRated(true);

            }
        }


       conversationChat.getMap().put(BroadcastService.staticconversationID,conversationModel);
        ApplozicApplication.SaveConversationChat(ConversationActivity.this,conversationChat);
        Log.d("isRated","isRated " +  "setting conversation to rated done....");

    }

    private boolean getifConversationRated(){

        boolean isRated = false;

        ConversationChat conversationChat = ApplozicApplication.ReadConversationChat(ConversationActivity.this);
       // ConversationModel conversationModel = null;

        for(Map.Entry<Integer,ConversationModel> entry : conversationChat.getMap().entrySet()) {
            Integer key = entry.getKey();
            //Integer value = entry.getValue();

            if(key == BroadcastService.staticconversationID){


                ConversationModel   conversationModel = entry.getValue();
                isRated = conversationModel.isRated();

            }
        }


        return  isRated;


    }


    private void write2SharedPrefrence(String key,boolean Value){
        SharedPreferences sharedPref = ConversationActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, Value);
        editor.commit();


    }


    private boolean get4rmSharedPreference(String key){

        SharedPreferences sharedPref =  ConversationActivity.this.getPreferences(Context.MODE_PRIVATE);


        boolean isRated = sharedPref.getBoolean(key, false);



        return  isRated;

    }
}