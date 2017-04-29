package com.ecoach.cosapp.Application;

import android.content.Context;


import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Configuration;
import com.android.volley.RequestQueue;
import com.applozic.mobicomkit.ApplozicClient;
import com.ecoach.cosapp.Activites.MainActivity;
import com.ecoach.cosapp.DataBase.AppInstanceSettings;
import com.ecoach.cosapp.DataBase.Categories;
import com.ecoach.cosapp.DataBase.Companies;
import com.ecoach.cosapp.DataBase.CompanyRepInvite;
import com.ecoach.cosapp.DataBase.Departments;
import com.ecoach.cosapp.DataBase.GalleryStorage;
import com.ecoach.cosapp.DataBase.Recommendation;
import com.ecoach.cosapp.DataBase.RepAvailablity;
import com.ecoach.cosapp.DataBase.RepInvites;
import com.ecoach.cosapp.DataBase.RepsReview;
import com.ecoach.cosapp.DataBase.User;
import com.ecoach.cosapp.DataBase.VerifiedCompanies;
import com.ecoach.cosapp.Http.Terminator2;
import com.ecoach.cosapp.Http.VolleySingleton;
import com.ecoach.cosapp.Models.IncomingChatModel;
import com.ecoach.cosapp.Models.RepInvite;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.RecycleAdapters.RecommendationAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by apple on 3/25/17.
 */

public class Application extends android.app.Application {



    public static IncomingChatModel appincomingChatModel;
    public static String getFireBaseToken;
    public static String selectedCategoryID;
    public static String selectedCategoryName;


    public static String selectedCompanyID;
    public static String selectedCompanyName;

    public static boolean alreadyDisplayedNotification = false;

    public static VerifiedCompanies selectedCompanyObbject;
    public static RepInvite activeRepInvite;
    public static RepInvites activeRepInvites;
    private static Context context;

    private static String companyCover = "";


    private static String profilePic = "";
    private static String companyLogo = "";
    private static String companyCert = "";
    private static String companyChatBack = "";
    private static String last_company_id = "";

    public static String AppUserKey = "";

    public static VolleySingleton volleySingleton;
    public static RequestQueue requestQueue;

        @Override
        public void onCreate() {
            super.onCreate();

            context = getApplicationContext();




            try{


                setFontsMaster();
                initializeDB();

                volleySingleton= VolleySingleton.getsInstance();
                requestQueue=VolleySingleton.getRequestQueue();





            }catch (Exception e){

                e.printStackTrace();
            }




            try{
                this.AppUserKey = AppInstanceSettings.load(AppInstanceSettings.class,1).getUserkey();
            }catch (Exception e){}

            try {



                startService(new Intent(this, Terminator2.class));

            }catch (Exception e){

                e.printStackTrace();
            }




        }


    public static Context getAppContext() {
        return context;
    }


    protected void initializeDB() {
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);


        configurationBuilder.addModelClasses(Categories.class);
        configurationBuilder.addModelClass(Companies.class);
        configurationBuilder.addModelClass(GalleryStorage.class);
        configurationBuilder.addModelClass(VerifiedCompanies.class);
        configurationBuilder.addModelClass(User.class);
        configurationBuilder.addModelClass(AppInstanceSettings.class);
        configurationBuilder.addModelClass(Departments.class);
        configurationBuilder.addModelClass(CompanyRepInvite.class);
        configurationBuilder.addModelClass(RepInvites.class);
        configurationBuilder.addModelClass(Recommendation.class);
        configurationBuilder.addModelClass(RepsReview.class);
        configurationBuilder.addModelClass(RepAvailablity.class);

        if (Cache.isInitialized() && Cache.getTableInfos().isEmpty()) {
            ActiveAndroid.dispose();
        }
        ActiveAndroid.initialize(configurationBuilder.create());
    }


    public static VerifiedCompanies getSelectedCompanyObbject() {
        return selectedCompanyObbject;
    }

    public static void setSelectedCompanyObbject(VerifiedCompanies selectedCompanyObbject) {
        Application.selectedCompanyObbject = selectedCompanyObbject;
    }

    public static String getSelectedCompanyID() {
        return selectedCompanyID;
    }

    public static void setSelectedCompanyID(String selectedCompanyID) {
        Application.selectedCompanyID = selectedCompanyID;
    }

    public static String getSelectedCompanyName() {
        return selectedCompanyName;
    }

    public static void setSelectedCompanyName(String selectedCompanyName) {
        Application.selectedCompanyName = selectedCompanyName;
    }

    public static String getSelectedCategoryID() {
        return selectedCategoryID;
    }

    public static void setSelectedCategoryID(String selectedCategoryID) {
        Application.selectedCategoryID = selectedCategoryID;
    }

    public static String getSelectedCategoryName() {
        return selectedCategoryName;
    }

    public static void setSelectedCategoryName(String selectedCategoryName) {
        Application.selectedCategoryName = selectedCategoryName;
    }


    public static String getCompanyCover() {
        return companyCover;
    }

    public static void setCompanyCover(String companyCover) {
        Application.companyCover = companyCover;
    }

    public static String getCompanyLogo() {
        return companyLogo;
    }

    public static void setCompanyLogo(String companyLogo) {
        Application.companyLogo = companyLogo;
    }

    public static String getCompanyCert() {
        return companyCert;
    }

    public static void setCompanyCert(String companyCert) {
        Application.companyCert = companyCert;
    }

    public static String getCompanyChatBack() {
        return companyChatBack;
    }

    public static void setCompanyChatBack(String companyChatBack) {
        Application.companyChatBack = companyChatBack;
    }

    public static String getProfilePic() {
        return profilePic;
    }

    public static void setProfilePic(String profilePic) {
        Application.profilePic = profilePic;
    }

    public static String getLast_company_id() {
        return last_company_id;
    }

    public static void setLast_company_id(String last_company_id) {
        Application.last_company_id = last_company_id;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Application.context = context;
    }

    private void setFontsMaster(){

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Assistant-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
}
