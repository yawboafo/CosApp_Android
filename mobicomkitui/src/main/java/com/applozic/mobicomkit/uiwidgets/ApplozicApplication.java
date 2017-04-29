package com.applozic.mobicomkit.uiwidgets;

import android.app.Application;
import android.content.Context;

import com.applozic.mobicomkit.uiwidgets.Clive.Models.ConversationChat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by devashish on 28/4/14.
 */
public class ApplozicApplication extends Application {

    public static Context context;
    public static final String TITLE = "Chats";
    public static  String cosappUserID = "";
    public static boolean reviewSent = false;

    public static String BASE_URL = "http://api.ecoachlabs.com/v1/cosapp/api.php";
    @Override
    public void onCreate() {
        // workaround for http://code.google.com/p/android/issues/detail?id=20915
        try {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());
            }*/

            context = getApplicationContext();
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
       super.attachBaseContext(base);
       // MultiDex.install(this);
    }



    public static void SaveConversationChat(ConversationChat conversationChat){


        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("conversation.srl", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(context);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ConversationChat ReadConversationChat( ){

        ConversationChat simpleClass = new ConversationChat();
        try {

            FileInputStream fis = context.openFileInput("conversation.srl");
            ObjectInputStream is = new ObjectInputStream(fis);
             simpleClass = (ConversationChat) is.readObject();
            is.close();
            fis.close();

        }catch (Exception e){


        }



        return  simpleClass;
    }
}