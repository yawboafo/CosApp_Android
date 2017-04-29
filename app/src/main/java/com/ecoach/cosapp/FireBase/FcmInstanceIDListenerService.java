package com.ecoach.cosapp.FireBase;

import android.content.Intent;
import android.util.Log;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.ecoach.cosapp.Activites.UserAccounts.LoginActivity;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.Http.FireBaseRegister;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by sunil on 9/4/16.
 */
public class FcmInstanceIDListenerService extends FirebaseInstanceIdService {

    final private static String TAG = "FcmInstanceIDListener";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String registrationId = FirebaseInstanceId.getInstance().getToken();
        Application.getFireBaseToken  = registrationId;
        startService(new Intent(getApplicationContext(), FireBaseRegister.class));
        Log.i(TAG, "Found Registration Id:" + Application.getFireBaseToken );
        Applozic.getInstance(this).setDeviceRegistrationId(registrationId);
        if (MobiComUserPreference.getInstance(this).isRegistered()) {
            try {
                RegistrationResponse registrationResponse = new RegisterUserClientService(this).updatePushNotificationId(registrationId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}