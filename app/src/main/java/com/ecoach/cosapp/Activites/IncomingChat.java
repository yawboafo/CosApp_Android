package com.ecoach.cosapp.Activites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.feed.TopicDetail;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicConversationCreateTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Conversation;
import com.ecoach.cosapp.Activites.Company.CompanyDetails;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.DataBase.AppInstanceSettings;
import com.ecoach.cosapp.Http.APIRequest;
import com.ecoach.cosapp.Http.VolleySingleton;
import com.ecoach.cosapp.Models.IncomingChatModel;
import com.ecoach.cosapp.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import info.hoang8f.widget.FButton;

import static com.applozic.mobicomkit.broadcast.NotificationBroadcastReceiver.TAG;
import static com.ecoach.cosapp.R.id.customerEmail;
import static com.ecoach.cosapp.R.id.customerphone;
import static com.ecoach.cosapp.R.id.itemDetails;

public class IncomingChat extends Activity {
    Vibrator vibrator;
    Ringtone r;
    MediaPlayer mMediaPlayer;
    private AudioManager myAudioManager;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    IncomingChatModel incoming;

    private String company_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_chat);
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        //myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

       // v.vibrate(1000);
        vibrator.vibrate(pattern, 0);
        initViews();






        try {
            Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch(Exception e) {

            e.printStackTrace();
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vibrator.cancel();
                mMediaPlayer.stop();
                IncomingChat.this.finish();
            }
        }, 30000);
    }


    @Override
    public void onBackPressed() {
        vibrator.cancel();
        mMediaPlayer.stop();
        super.onBackPressed();
    }

    private void initViews() {

        TextView textview = (TextView)findViewById(R.id.userfullname);
        TextView customerphone = (TextView)findViewById(R.id.customerphone);
        TextView customerEmail= (TextView)findViewById(R.id.customerEmail);

        if(Application.appincomingChatModel != null){

             incoming = Application.appincomingChatModel;


            if(incoming.getBehalf_company_name() !=null){

                textview.setText(incoming.getBehalf_company_name());
                customerphone.setText(incoming.getCustomer_phone());
                customerEmail.setText(incoming.getCustomer_email());
            }else {
                textview.setText(incoming.getCustomer_fname() + " " + incoming.getCustomer_lname());
                customerphone.setText(incoming.getCustomer_phone());
                customerEmail.setText(incoming.getCustomer_email());
            }





        }


        FButton acceptChatButton = (FButton)findViewById(R.id.acceptChatButton);
        acceptChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancel();
                mMediaPlayer.stop();
                IncomingChatModel incoming = Application.appincomingChatModel;


                Log.d("Logged", "incoming CompID" + incoming.getCustomer_encrypted_id());

                AcceptChatInvite(incoming.getCompany_id(),incoming.getCustomer_id(),Application.appincomingChatModel);


                /**
                 *       /**  Intent intent = new Intent(CompanyDetails.this, ConversationActivity.class);
                 intent.putExtra(ConversationUIService.USER_ID,Application.getSelectedCompanyObbject().getEmail());
                 intent.putExtra(ConversationUIService.DISPLAY_NAME, Application.getSelectedCompanyObbject().getCompanyName()); //put it for displaying the title.
                 startActivity(intent);
                 **/

            }
        });


        FButton rejectIncomingchat = (FButton)findViewById(R.id.rejectIncomingchat);
        rejectIncomingchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancel();
                mMediaPlayer.stop();
                IncomingChat.this.finish();
            }
        });
    }

    private void AcceptChatInvite(String companyID, String customer_id, final IncomingChatModel incoming){

        final SweetAlertDialog pDialog;
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Connecting ..");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d("companies","loading from initiateChat");

        final HashMap<String, String> params = new HashMap<String, String>();



        params.put("is_accept_waiting_user",""+ "1");
        params.put("company_id",companyID);
        params.put("customer",customer_id);

        Log.d("Params ",params+"");

        Log.d("INCOMING","CUSTOMER ID " + customer_id);


        volleySingleton= VolleySingleton.getsInstance();
        requestQueue=VolleySingleton.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                APIRequest.BASE_URL,
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
                                pDialog.hide();
                                new SweetAlertDialog(IncomingChat.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Sorry,Try Again")
                                        .setContentText(message)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                IncomingChat.this.finish();
                                            }
                                        })
                                        .show();

                            } else if (statuscode.equals("201")) {


                                String chat_session = object.getString("chat_session");
                                company_id= object.getString("company");
                                Log.d("chat_session","chat_session : "+chat_session);


                                ApplozicUserLogin(incoming,chat_session);

                               // initiateOneOnOneChat(incoming,chat_session);



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
                new SweetAlertDialog(IncomingChat.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                IncomingChat.this.finish();
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
                headers.put("auth-key", AppInstanceSettings.load(AppInstanceSettings.class,1).getUserkey());
                return headers;
            }
        };

        requestQueue.add(request);
        Log.d("oxinbo","Server Logs"+params.toString());
    }

    private void ApplozicUserLogin(String displayName,String ID){

        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {

                ApplozicClient.getInstance(context).enableNotification();
                //ApplozicClient.getInstance(context).hideChatListOnNotification();
                ApplozicClient.getInstance(context).setContextBasedChat(false);


                Log.d("aPPLOZIC Succes",registrationResponse.toString());
                ApplozicClient.getInstance(context).setHandleDial(true).setIPCallEnabled(true);
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
        applozicUser.setUserId(ID);

        applozicUser.setDisplayName(displayName);

        List<String> featureList =  new ArrayList<>();
        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_AUDIO_CALL.getValue());// FOR AUDIO
        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_VIDEO_CALL.getValue());// FOR VIDEO
        applozicUser.setFeatures(featureList);

        applozicUser.setAuthenticationTypeId(com.applozic.mobicomkit.api.account.user.User.AuthenticationType.APPLOZIC.getValue());
        new UserLoginTask(applozicUser, listener, this).execute((Void) null);




    }

    private Conversation buildConversation(final IncomingChatModel incoming,String chat_session) {
//1111868  1111868
        //Title and subtitles are required if you are enabling the view for particular context.
//
        TopicDetail topic = new TopicDetail();
      //  topic.setTitle("Hyundai i20");//Your Topic title
      //  topic.setSubtitle("May be your car model");//Put Your Topic subtitle
      //  topic.setLink("Topic Image link if any");

        //You can set two Custom key-value pair which will appear on context view .

        topic.setKey1(Application.AppUserKey);
        topic.setKey2(incoming.getCustomer_encrypted_id());
        topic.setValue1(incoming.getCompany_id());
        topic.setValue2("");

        //Create Conversation.

        Conversation conversation = new Conversation();

        //SET UserId for which you want to launch chat or conversation

        conversation.setTopicId(chat_session);
        conversation.setUserId(incoming.getCustomer_encrypted_id());
        conversation.setTopicDetail(topic.getJson());
        return conversation;

    }


    private void initiateOneOnOneChat(final IncomingChatModel incoming,String chat_session){

        Log.d("Applozic","Initiating chat with new Account....");


        ApplozicConversationCreateTask applozicConversationCreateTask = null;
//        UserService.getInstance(IncomingChat.this).updateDisplayNameORImageLink(incoming.getCompany_name(),"",null,null);

        ApplozicConversationCreateTask.ConversationCreateListener conversationCreateListener =  new ApplozicConversationCreateTask.ConversationCreateListener() {
            @Override
            public void onSuccess(Integer conversationId, Context context) {
                Log.d("Applozic","Initiating chat with new Account success....");

                Log.d("conVid",conversationId.toString());
                //For launching the  one to one  chat

                pDialog.dismiss();

                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra("takeOrder", true);

                Log.d("Applozic","Reciever UserID "+  incoming.getCustomer_encrypted_id());

                if( incoming.getBehalf_company_name() == null){
                    intent.putExtra(ConversationUIService.USER_ID, incoming.getCustomer_encrypted_id());//RECEIVER USERID
                    // intent.putExtra(ConversationUIService.DEFAULT_TEXT, "Hello I am interested in this car, Can we chat?");

                    intent.putExtra(ConversationUIService.DISPLAY_NAME, incoming.getCustomer_fname()+  "  "  + incoming.getCustomer_lname());
                }else{

                    intent.putExtra(ConversationUIService.USER_ID, incoming.behalf_company_id+incoming.getCustomer_encrypted_id());//RECEIVER USERID
                    // intent.putExtra(ConversationUIService.DEFAULT_TEXT, "Hello I am interested in this car, Can we chat?");
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, incoming.getBehalf_company_name());

                }


                ApplozicApplication.cosappUserID = Application.AppUserKey;

              //
                //intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT,true);
                intent.putExtra(ConversationUIService.CONVERSATION_ID,conversationId);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e, Context context) {
                Log.d("Applozic","Initiating chat with new Account failed....");

            }
        };
        Conversation conversation = buildConversation(incoming,chat_session); //From Step 1
       // conversation.set
     //   UserService.getInstance(context).updateDisplayNameORImageLink(incoming.getCompany_name(),"",null,null);


        applozicConversationCreateTask = new ApplozicConversationCreateTask(IncomingChat.this,conversationCreateListener,conversation);
        applozicConversationCreateTask.execute((Void)null);

    }

     SweetAlertDialog pDialog;

    private void ApplozicUserLogin(final IncomingChatModel incoming,
                                   final String chatsessionID){

        Log.d("Applozic","Login into new Company Account ....");

        //new UserClientService(IncomingChat.this).logout();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Almost done ..");
        //pDialog.setContentText("Logging you into " + incoming.getCompany_name()+ " account ");
        pDialog.setCancelable(false);
        pDialog.show();




        UserLogoutTask.TaskListener userLogoutTaskListener = new UserLogoutTask.TaskListener() {
            @Override
            public void onSuccess(Context context) {
                UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse, Context context) {

                        Log.d("Applozic","Login into new Company Account Succesfull....");


                        ApplozicClient.getInstance(context).enableNotification();
                        ApplozicClient.getInstance(context).hideChatListOnNotification();

                        Log.d("aPPLOZIC Succes",registrationResponse.toString());
                        ApplozicClient.getInstance(context).setContextBasedChat(false).setHandleDial(true).setIPCallEnabled(true);
                        Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
                        activityCallbacks.put(ApplozicSetting.RequestCode.AUDIO_CALL, AudioCallActivityV2.class.getName());
                        activityCallbacks.put(ApplozicSetting.RequestCode.VIDEO_CALL, VideoActivity.class.getName());
                        ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks);


                        /**
                         *  String avatorPath= incoming.getBehalf_company_path()+incoming.getBehalf_company_storage()+"/"+incoming.getBehalf_company_avatar();
                         String userID = incoming.getCompany_id()+incoming.getCompany_department();
                         String displayname = incoming.getCompany_name();
                         *
                         *
                         * **/




                        PushNotificationTask.TaskListener pushNotificationTaskListener=  new PushNotificationTask.TaskListener() {
                            @Override
                            public void onSuccess(RegistrationResponse registrationResponse) {


                                initiateOneOnOneChat(incoming,chatsessionID);

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

                        Log.d("Applozic","Login into new Company Account Failed....");

                    }
                };
                String avatorPath= incoming.getBehalf_company_path()+incoming.getBehalf_company_storage()+"/"+incoming.getBehalf_company_avatar();

                com.applozic.mobicomkit.api.account.user.User applozicUser = new com.applozic.mobicomkit.api.account.user.User();
                applozicUser.setUserId(incoming.company_id+Application.AppUserKey);
                applozicUser.setDisplayName(incoming.getCompany_name());
                applozicUser.setImageLink(avatorPath);
                List<String> featureList =  new ArrayList<>();
                featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_AUDIO_CALL.getValue());// FOR AUDIO
                featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_VIDEO_CALL.getValue());// FOR VIDEO
                applozicUser.setFeatures(featureList);

                applozicUser.setAuthenticationTypeId(com.applozic.mobicomkit.api.account.user.User.AuthenticationType.APPLOZIC.getValue());
                new UserLoginTask(applozicUser, listener, IncomingChat.this).execute((Void) null);

            }
            @Override
            public void onFailure(Exception exception) {
                //Logout failure
            }
        };UserLogoutTask userLogoutTask = new UserLogoutTask(userLogoutTaskListener, IncomingChat.this);
        userLogoutTask.execute((Void) null);






    }
}
