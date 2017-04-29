package com.ecoach.cosapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
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
import com.ecoach.cosapp.Activites.Company.CompaniesActivity;
import com.ecoach.cosapp.Activites.Company.CompanyDetails;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.DataBase.Categories;
import com.ecoach.cosapp.DataBase.GalleryStorage;
import com.ecoach.cosapp.DataBase.Recommendation;
import com.ecoach.cosapp.DataBase.VerifiedCompanies;
import com.ecoach.cosapp.Http.APIRequest;
import com.ecoach.cosapp.Http.VolleySingleton;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.RecycleAdapters.CategoriesAdapter;
import com.ecoach.cosapp.RecycleAdapters.RecommendationAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView toprecyler, bottomrecycler;
    ImageView centeradvert;
    RecyclerView.LayoutManager layoutManager,verticalManager;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private AVLoadingIndicatorView avLoadingIndicatorView;
   // SwipeRefreshLayout swiperRefresh;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toprecyler = (RecyclerView)view.findViewById(R.id.topRecycleView);




        setSliderLayout(view);



        if(VerifiedCompanies.getRecomendedCompanies().size() == 0){

            getRecomendations(view);
        }else{

            try{


                RecommendationAdapter recommendationAdapter = new RecommendationAdapter(getContext(), VerifiedCompanies.getRecomendedCompanies());
                toprecyler.setAdapter(recommendationAdapter);
                toprecyler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


            }catch (Exception e){
                e.printStackTrace();
            }
        }


        if(Categories.getAllCategories().size() == 0){

            getCategories(view);
        }else{

            SetRecycleView( view);

        }

try{
    toprecyler.addOnItemTouchListener(new RecyclerTouchListener(getContext(), toprecyler, new ClickListener() {
        @Override
        public void onClick(View view, int position) {
            TextView tv=(TextView)view.findViewById(R.id.labelTxt);
            String selectedCompanyName=tv.getText().toString();

            Application.setSelectedCompanyName(selectedCompanyName);


            TextView id=(TextView)view.findViewById(R.id.texthidden);
            String selectedid=id.getText().toString();

            Application.setSelectedCompanyID(selectedid);
            Application.setSelectedCompanyObbject(VerifiedCompanies.getCompanyByIDByName(selectedCompanyName));

            Intent intent = new Intent(getActivity(),CompanyDetails.class);
            startActivity(intent);

        }

        @Override
        public void onLongClick(View view, int position) {

        }
    }));
}catch (Exception e){

    e.printStackTrace();
}


    }

    public void setSliderLayout(View view){



        centeradvert = (ImageView) view.findViewById(R.id.adView);
      //  centeradvert.addSlider();

    }


    @Override
    public void onStop() {


        super.onStop();
    }

    public void SetRecycleView(View view){


        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(),Categories.getAllCategories());
        layoutManager = new GridLayoutManager(getActivity(), 2);

        bottomrecycler = (RecyclerView) view.findViewById(R.id.bottomRecycleView);
        bottomrecycler.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), bottomrecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                TextView tv=(TextView)view.findViewById(R.id.hiddenID);
                String id=tv.getText().toString();


                Log.d("category","catgory item "+tv.getText().toString());
                Application.setSelectedCategoryID(id);



                TextView name=(TextView)view.findViewById(R.id.labelTxt);
                String catname=name.getText().toString();

                Application.setSelectedCategoryName(catname);

                Intent intent = new Intent(getActivity(), CompaniesActivity.class);
                startActivity(intent);



            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        bottomrecycler.setAdapter(categoriesAdapter);
        bottomrecycler.setLayoutManager(layoutManager);
        bottomrecycler.setNestedScrollingEnabled(false);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//recomendations
    private void getRecomendations(final View view){
        final SweetAlertDialog pDialog;
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Setting you up ..");
        pDialog.setCancelable(false);
        pDialog.show();

    Log.d("recomendation","loading from background");

    final HashMap<String, String> params = new HashMap<String, String>();

     Recommendation.truncate(Recommendation.class);
        params.put("fetch_public_info",""+ "1");
    params.put("scope","recommendation");
    params.put("rec_lat","0.0");
    params.put("rec_long","0.0");


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



                        Log.d("recomendation logs",response.toString());

                        getRecomendationsformatJSONLOCAL(response,view);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    //  Message.messageShort(MyApplication.getAppContext(),""+tokenValue+"\n"+response.toString()+"\n"+booleaner);
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error)   {

            pDialog.dismiss();

            //  dialogs.SimpleWarningAlertDialog("Transmission Error", "Connection Failed").show();
            Log.d("volley.Response", error.toString());





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
    private void getRecomendationsformatJSONLOCAL(JSONObject response,View view){

        List<VerifiedCompanies> companiesArrayList = new ArrayList<VerifiedCompanies>();
        List<GalleryStorage> showcaseList = new ArrayList<GalleryStorage>();
        VerifiedCompanies companies;
        GalleryStorage galleryStorage;
        try {

            JSONObject  object= response.optJSONObject("ecoachlabs");
            JSONArray info = object.getJSONArray("info");

            for (int i = 0 ; i < info.length(); i++) {

                JSONObject obj = info.getJSONObject(i);


                companies = VerifiedCompanies.getCompaniesByID(obj.getString("companyCuid"),obj.getString("companyCategoryId"));

                if(companies == null){


                    Log.d("companies","companies was null");

                    companies =   new VerifiedCompanies();
                }
                Log.d("companies","companies was not null");

                companies.setCategory_id(obj.getString("companyCategoryId"));

                String company_id = obj.getString("companyCuid");
                companies.setCompanyCuid(company_id);

                String company_name = obj.getString("companyName");
                companies.setCompanyName(company_name);

                companies.setIsRepOnline("false");

                String companyCategory = obj.getString("companyCategory");
                companies.setCompanyCategory(companyCategory);



                String companyCategoryID = obj.getString("companyCategoryId");
                companies.setCompanyCategoryid(companyCategoryID);


                String company_path = obj.getString("Path");
                companies.setPath(company_path);


                String company_avator = obj.getString("avatarLocation");
                companies.setAvatarLocation(company_avator);


                String active = obj.getString("companyStatus");
                companies.setCompanyStatus(active);


                String company_rating = obj.getString("companyRating");
                companies.setCompanyRating(company_rating);

                String address = obj.getString("Address");
                companies.setAddress(address);

                String bio = obj.getString("Bio");
                companies.setBio(bio);


                String Phone1 = obj.getString("Phone1");
                companies.setPhone1(Phone1);


                String Phone2 = obj.getString("Phone2");
                companies.setPhone2(Phone2);

                String email = obj.getString("Email");
                companies.setEmail(email);


                String Website = obj.getString("Website");
                companies.setWebsite(Website);


                String companyLat = obj.getString("companyLat");
                companies.setCompanyLat(companyLat);


                String companyLong = obj.getString("companyLong");
                companies.setCompanyLong(companyLong);


                String coverLocation = obj.getString("coverLocation");
                companies.setCoverLocation(coverLocation);


                String companyStorageName = obj.getString("companyStorageName");
                companies.setCompanyStorageName(companyStorageName);


                companies.setIsRecommendation("recommendation");

                companies.setForUser(false);



                JSONArray showcase = obj.getJSONArray("showcase");

                for (int A = 0 ; A < showcase.length(); A++) {

                    JSONObject showcaseobj = showcase.getJSONObject(i);
                    String storageID = showcaseobj.getString("showcaseId");
                    galleryStorage = GalleryStorage.getStorageSingleByLocation(showcaseobj.getString("showcaseLocation"));
                    if(galleryStorage == null){

                        galleryStorage = new GalleryStorage();

                    }

                    Log.d("showCase Loc",showcaseobj.getString("showcaseLocation"));
                    galleryStorage.setCompanyCuid(companies.getCompanyCuid());

                    String showcaseLocation = showcaseobj.getString("showcaseLocation");
                    galleryStorage.setShowcaseLocation(showcaseLocation);



                    String showType = showcaseobj.getString("showcaseType");
                    galleryStorage.setShowcaseType(showType);




                    showcaseList.add(galleryStorage);


                }


                companiesArrayList.add(companies);


            }

            ActiveAndroid.beginTransaction();
            try
            {

                for(VerifiedCompanies verifiedCompanies : companiesArrayList){



                    Long id =   verifiedCompanies.save();


                    Log.d("verified comps", "id"+id);

                }



                for(GalleryStorage galleryStorage1 : showcaseList){



                    Long id =   galleryStorage1.save();


                    Log.d("galleryStorage1", "id"+id);

                }

                ActiveAndroid.setTransactionSuccessful();
            }
            finally {
                ActiveAndroid.endTransaction();


                try{

                    RecommendationAdapter recommendationAdapter = new RecommendationAdapter(getContext(), VerifiedCompanies.getRecomendedCompanies());
                    toprecyler.setAdapter(recommendationAdapter);
                    toprecyler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

                }catch (Exception e){
                    e.printStackTrace();
                }

                //SetRecycleView(view);
            }





        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    //


    private void getCategories(final View view){





        final HashMap<String, String> params = new HashMap<String, String>();



        params.put("fetch_public_info",""+ "1");
        params.put("scope","company_categories");


        volleySingleton= VolleySingleton.getsInstance();
        requestQueue=VolleySingleton.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                APIRequest.BASE_URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    //Log.d("Params",params+"");
                    @Override
                    public void onResponse(JSONObject response) {

                        try {



                            Log.d("logs",response.toString());

                            formatJSON(response,view);


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

    private void formatJSON(JSONObject response,View view){

        List<Categories> categoriesArrayList = new ArrayList<Categories>();

        try {

            JSONObject  object= response.optJSONObject("ecoachlabs");
            JSONArray info = object.getJSONArray("info");

            for (int i = 0 ; i < info.length(); i++) {

                JSONObject obj = info.getJSONObject(i);
                String category_id = obj.getString("category_id");
                Categories categories = Categories.getCategoryByID(category_id);

                if(categories == null)
                    categories = new Categories();


                //String category_id = obj.getString("category_id");
                categories.setCategoryID(category_id);

                String category_name = obj.getString("category_name");
                categories.setCategoryNames(category_name);


                String category_pic = obj.getString("category_pic");



                categories.setCategoryBackgroundImage(category_pic);



                String path = obj.getString("path");
                categories.setPath(path);
                //categories.setCategoryIcons(category_pic);


                // String category_pic = obj.getString("category_pic");
               // categories.setCategoryIcons("www.android.com");


                categoriesArrayList.add(categories);


            }


            ActiveAndroid.beginTransaction();
            try
            {

                for(Categories center : categoriesArrayList){


                    Long id =   center.save();


                    Log.d("Categories ID", "id"+id);

                }



                ActiveAndroid.setTransactionSuccessful();
            }
            finally {
                ActiveAndroid.endTransaction();

                SetRecycleView(view);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            this.clickListener=clickListener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child= recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clickListener!=null){


                        clickListener.onLongClick(child,recyclerView.getChildPosition(child));
                    }

                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child= rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clickListener!=null && gestureDetector.onTouchEvent(e)){


                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener{

        public void onClick(View view,int position);
        public void onLongClick(View view,int position);

    }
}
