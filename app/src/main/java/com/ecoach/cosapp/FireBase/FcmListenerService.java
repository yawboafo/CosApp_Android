package com.ecoach.cosapp.FireBase;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.ecoach.cosapp.Activites.IncomingChat;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.Models.IncomingChatModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;


public class FcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "ApplozicGcmListener";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i(TAG,"Message data:"+remoteMessage.getData());
//        Log.d("cosappFirebase",remoteMessage.getNotification().toString());

        if(remoteMessage.getData().size()>0){

            if(remoteMessage.getData().containsKey("customer_info")){

                Log.d(TAG,"customer_email : IT CONTAINS KEY");


                try {

                   // Map<String, String> params = remoteMessage.getData();
                    String object = remoteMessage.getData().get("customer_info");
                    JSONObject jsonObject = new JSONObject(object);
                    IncomingChatModel incomingChatModel = new IncomingChatModel();

                    incomingChatModel.setCustomer_avatar(jsonObject.getString("customer_avatar"));

                    incomingChatModel.setCustomer_storage(jsonObject.getString("customer_storage"));

                    incomingChatModel.setCustomer_email(jsonObject.getString("customer_email"));

                    incomingChatModel.setAvatar_path(jsonObject.getString("avatar_path"));

                    incomingChatModel.setCustomer_phone(jsonObject.getString("customer_phone"));


                    incomingChatModel.setCustomer_lname(jsonObject.getString("customer_lname"));

                    incomingChatModel.setCustomer_fname(jsonObject.getString("customer_fname"));

                    incomingChatModel.setCustomer_id(jsonObject.getString("customer_id"));
                    incomingChatModel.setCustomer_encrypted_id(jsonObject.getString("customer_encrypted_id"));
                    incomingChatModel.setCompany_id(remoteMessage.getData().get("company_id"));




                    incomingChatModel.setCompany_name(remoteMessage.getData().get("company_name"));
                    incomingChatModel.setCompany_department(remoteMessage.getData().get("company_department"));


try{
    String onbehalf = remoteMessage.getData().get("behalf_info");
    JSONObject onbehalfbject = new JSONObject(onbehalf);
    incomingChatModel.setBehalf_company_avatar(onbehalfbject.getString("company_avatar"));

    incomingChatModel.setBehalf_company_path(onbehalfbject.getString("company_path"));

    incomingChatModel.setBehalf_company_storage(onbehalfbject.getString("company_storage"));

    incomingChatModel.setBehalf_company_name(onbehalfbject.getString("company_name"));

    incomingChatModel.setBehalf_company_id(onbehalfbject.getString("company_id"));
}catch (Exception e){
    e.printStackTrace();
}


                     Application.appincomingChatModel = incomingChatModel;


                    //String customer_email = object1.getString("customer_email");
                    Log.d(TAG,"paramObject : "+jsonObject.getString("customer_email"));

                    Log.d(TAG,"customer_email"+remoteMessage.getData().get("customer_email"));

                    Log.d(TAG,"customer_encrypted_id"+jsonObject.getString("customer_encrypted_id"));


                    Log.d("UserID","CURRENT USER ID"+Application.AppUserKey  +  "CALLING UERS  " + incomingChatModel.getCustomer_encrypted_id());
                    Log.d("UserID","I found the same IDS");

                    if(Application.AppUserKey.toString().equals(incomingChatModel.getCustomer_encrypted_id().toString())){
                        Log.d("UserID","hmm the same  IDs");
                        Toast.makeText(getApplicationContext(),"Ugly error",Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("UserID","Not same IDs");
                        Intent intent = new Intent(Application.getContext(), IncomingChat.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }






            }else{

                if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
                    Log.i(TAG, "Applozic notification processing...");
                    MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
                    return;
                }
            }

        }

    }

}