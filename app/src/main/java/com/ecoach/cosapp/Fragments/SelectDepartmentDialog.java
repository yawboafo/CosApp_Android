package com.ecoach.cosapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
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
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.DataBase.AppInstanceSettings;
import com.ecoach.cosapp.DataBase.Departments;
import com.ecoach.cosapp.DataBase.GalleryStorage;
import com.ecoach.cosapp.DataBase.VerifiedCompanies;
import com.ecoach.cosapp.Http.APIRequest;
import com.ecoach.cosapp.Http.VolleySingleton;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.RecycleAdapters.CompanyListStyleAdapter;
import com.ecoach.cosapp.RecycleAdapters.DepartmentAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectDepartmentDialog.SelectDepartmentDialogOnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectDepartmentDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectDepartmentDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    LinearLayoutManager linearLayoutManager;
    DepartmentAdapter departmentAdapter;
    CompanyListStyleAdapter companyListStyleAdapter;
    RecyclerView departmentsRecyleview,companyDepartmentRecyleview;
    ViewFlipper accountSelectViewflipper;

    private AVLoadingIndicatorView avi;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    String selectedDepartment =  "" ;
    boolean isPersonalAccount = false;
    String selectedCompanyID = "";
    String selectedCompanyName = "";
    private SelectDepartmentDialogOnFragmentInteractionListener mListener;

    public SelectDepartmentDialog() {
        // Required empty public constructor
    }


    public static SelectDepartmentDialog newInstance(String param1, String param2) {
        SelectDepartmentDialog fragment = new SelectDepartmentDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_select_department_dialog, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try{


            companyDepartmentRecyleview=(RecyclerView)view.findViewById(R.id.companiesList);
            companyDepartmentRecyleview.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), companyDepartmentRecyleview, new ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    TextView textView = (TextView)view.findViewById(R.id.departmentName);

                    selectedCompanyName = textView.getText().toString();
                    selectedCompanyID =   VerifiedCompanies.getCompanyByIDByName(selectedCompanyName).getCompanyCuid();
                    //accountSelectViewflipper.showNext();
Log.d("COSAPP","selected company ID :  "+selectedCompanyName + selectedCompanyID);

                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            departmentsRecyleview =(RecyclerView)view.findViewById(R.id.departments);
            departmentsRecyleview.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), departmentsRecyleview, new ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    TextView textView = (TextView)view.findViewById(R.id.departmentName);

                    selectedDepartment = textView.getText().toString();
                    accountSelectViewflipper.showNext();


                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            accountSelectViewflipper = (ViewFlipper)view.findViewById(R.id.accountSelectViewflipper);

            FButton backButton = (FButton)view.findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountSelectViewflipper.showPrevious();
                }
            });



            FButton continueButton = (FButton)view.findViewById(R.id.progressButton);
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    accountSelectViewflipper.showNext();
                    summaryScreen(view);
                }
            });



            TextView textView10 = (TextView)view.findViewById(R.id.textView10);
            textView10.setText(Application.getSelectedCompanyObbject().getCompanyName());


            Switch switch2 = (Switch)view.findViewById(R.id.switch2);
          //  switch2.setChecked(true);
            switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        isPersonalAccount = true;
                        companyDepartmentRecyleview.setVisibility(View.INVISIBLE);
                    }else{
                        isPersonalAccount =  false;
                        companyDepartmentRecyleview.setVisibility(View.VISIBLE);
                    }
                }
            });


           FButton closebutton=(FButton)view.findViewById(R.id.closebut);
            closebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
            setdepartmentsRecyleview(view);

            if(Departments.getDepartmentsByCompanyID(Application.getSelectedCompanyObbject().getCompanyCuid()).size() == 0){

                loadcompanyDepartment(view);
            }  else{


                departmentAdapter.notifyDataSetChanged();


            }



            if (VerifiedCompanies.getAllCompaniesBy4User(true, Application.AppUserKey).size() == 0) {

                getCategoriesLocal();
            } else {

               companyListStyleAdapter.notifyDataSetChanged();

            }

        }catch (Exception e)
        {e.printStackTrace();}
    }
    void setdepartmentsRecyleview(View view){








        departmentAdapter = new DepartmentAdapter(getActivity(), Departments.getDepartmentsByCompanyID(Application.getSelectedCompanyObbject().getCompanyCuid()));

        linearLayoutManager = new LinearLayoutManager(getActivity());
        departmentsRecyleview.setAdapter(departmentAdapter);
        departmentsRecyleview.setLayoutManager(linearLayoutManager);






        companyListStyleAdapter = new CompanyListStyleAdapter(getActivity(), VerifiedCompanies.getAllCompaniesBy4User(true, Application.AppUserKey));


        linearLayoutManager = new LinearLayoutManager(getActivity());
        companyDepartmentRecyleview.setAdapter(companyListStyleAdapter);
        companyDepartmentRecyleview.setLayoutManager(linearLayoutManager);
    }

    private void summaryScreen(View view){

        TextView companyToContact = (TextView)view.findViewById(R.id.companyToContact);
        companyToContact.setText(Application.getSelectedCompanyObbject().getCompanyName());

        TextView departmentToContact = (TextView)view.findViewById(R.id.department);
        departmentToContact.setText("Department : "+ selectedDepartment);


        TextView usingAccount = (TextView)view.findViewById(R.id.usingAccount);

        if(isPersonalAccount) {
            usingAccount.setText("Using : " + "My Personal Account");
        }else{

            usingAccount.setText("Using : " + selectedCompanyName);

        }



        FButton backButt = (FButton)view.findViewById(R.id.backButt);
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountSelectViewflipper.showPrevious();
            }
        });

        FButton startChat = (FButton)view.findViewById(R.id.startChat);
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /**
                 *  String selectedDepartment =  "" ;
                 boolean isPersonalAccount = false;
                 String selectedCompanyID = "";
                 String selectedCompanyName = "";
                 * */



                if(!selectedDepartment.isEmpty()  ){



                    mListener.SelectDepartmentDialogonFragmentInteraction(
                            selectedDepartment,
                            isPersonalAccount,
                            Application.getSelectedCompanyObbject().getCompanyCuid(),
                            selectedCompanyID);

                    getDialog().dismiss();
                }



            }
        });
    }

    private void loadcompanyDepartment(final View view){

        avi=(AVLoadingIndicatorView)view.findViewById(R.id.avi);
        avi.setVisibility(View.VISIBLE);
        avi.show();

        final HashMap<String, String> params = new HashMap<String, String>();



        params.put("fetch_public_info", "1");
        params.put("scope","wide_company_departments");
        params.put("company_id",Application.getSelectedCompanyObbject().getCompanyCuid());

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

                            avi.hide();

                            Log.d("logs",response.toString());




                            JSONObject  object= response.optJSONObject("ecoachlabs");
                            String statuscode = object.getString("status");
                            String message = object.getString("msg");

                            Departments departments;
                            List<Departments> departmentsList = new ArrayList<>();

                            if(statuscode.equals("201")){

                                JSONArray info = object.getJSONArray("info");
                                //   JSONArray info = object.getJSONArray("info");

                                for (int i = 0 ; i < info.length(); i++) {

                                    JSONObject obj = info.getJSONObject(i);


                                    Log.d("selectedCUD",Application.getSelectedCompanyObbject().getCompanyCuid());

                                    departments = Departments.getDepartmentsByIDByName(obj.getString("department"),Application.getSelectedCompanyObbject().getCompanyCuid());

                                    if(departments == null){

                                        departments= new Departments();
                                    }


                                    //departments.setDepartmentid(obj.getString("department_id"));
                                    departments.setCompany_id(Application.getSelectedCompanyObbject().getCompanyCuid());
                                    departments.setDepartmentname(WordUtils.capitalizeFully(obj.getString("department")));

                                    departmentsList.add(departments);
                                }


                                ActiveAndroid.beginTransaction();
                                try
                                {

                                    for(Departments departments1 : departmentsList){



                                        Long id =   departments1.save();


                                        Log.d("department ID", "id"+id);

                                    }




                                    ActiveAndroid.setTransactionSuccessful();
                                }
                                finally {
                                    ActiveAndroid.endTransaction();




                                    setdepartmentsRecyleview(view);
                                }

                            }else{

                                getDialog().dismiss();
                            }






                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                        //  Message.messageShort(MyApplication.getAppContext(),""+tokenValue+"\n"+response.toString()+"\n"+booleaner);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)   {

                avi.hide();
                getDialog().dismiss();

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



    private void getCategoriesLocal(){



        Log.d("companies","loading from background");

        final HashMap<String, String> params = new HashMap<String, String>();



        params.put("fetch_admin_info",""+ "1");
        params.put("scope","my_companies");
        //  params.put("category_id", Application.getSelectedCategoryID());

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

                           // refreshLayout.setRefreshing(false);

                            Log.d("logs",response.toString());


                            try{

                                new Delete().from(VerifiedCompanies.class).where("forUser = ?",true).and("userID = ?",
                                        AppInstanceSettings.load(AppInstanceSettings.class,1).getUserkey()).execute();


                            }catch (Exception e){


                            }
                            formatJSONLOCAL(response);


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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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
    private void formatJSONLOCAL(JSONObject response){

        List<VerifiedCompanies> companiesArrayList = new ArrayList<VerifiedCompanies>();
        List<GalleryStorage> showcaseList = new ArrayList<GalleryStorage>();
        VerifiedCompanies companies;
        GalleryStorage galleryStorage;
        try {

            JSONObject  object= response.optJSONObject("ecoachlabs");
            JSONArray info = object.getJSONArray("info");

            for (int i = 0 ; i < info.length(); i++) {

                JSONObject obj = info.getJSONObject(i);


                companies = VerifiedCompanies.getCompaniesByIDand4User(obj.getString("companyCuid"),true);

                if(companies == null){


                    Log.d("companies","companies was null");

                    companies =   new VerifiedCompanies();
                }
                Log.d("companies","companies was not null");


                String companyCategoryId = obj.getString("companyCategoryId");
                companies.setCategory_id(companyCategoryId);

                String company_id = obj.getString("companyCuid");
                companies.setCompanyCuid(company_id);

                String company_name = obj.getString("companyName");
                companies.setCompanyName(company_name);


                String company_path = obj.getString("Path");
                companies.setPath(company_path);


                String company_avator = obj.getString("avatarLocation");
                companies.setAvatarLocation(company_avator);


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


                String accountType = obj.getString("accountType");
                companies.setAccountType(accountType);


                String compCatName = obj.getString("companyCategory");
                companies.setCompanyCategory(compCatName);


                companies.setForUser(true);


                companies.setUserID(AppInstanceSettings.load(AppInstanceSettings.class,1).getUserkey());

                //TODO Create the department schema


                JSONArray showcase = obj.getJSONArray("showcase");

                for (int A = 0 ; A < showcase.length(); A++) {

                    JSONObject showcaseobj = showcase.getJSONObject(i);
                    String storageID = showcaseobj.getString("showcaseId");
                    galleryStorage = GalleryStorage.getStorageSingle(companies.getCompanyCuid(),storageID);
                    if(galleryStorage == null){

                        galleryStorage = new GalleryStorage();

                    }
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


                    Log.d("Company ID", "id"+id);

                }



                for(GalleryStorage galleryStorage1 : showcaseList){



                    Long id =   galleryStorage1.save();


                    Log.d("galleryStorage1", "id"+id);

                }

                ActiveAndroid.setTransactionSuccessful();
            }
            finally {
                ActiveAndroid.endTransaction();

                //setdepartmentsRecyleview(view);
                companyListStyleAdapter = new CompanyListStyleAdapter(getActivity(), VerifiedCompanies.getAllCompaniesBy4User(true, Application.AppUserKey));


                linearLayoutManager = new LinearLayoutManager(getActivity());
                companyDepartmentRecyleview.setAdapter(companyListStyleAdapter);
                companyDepartmentRecyleview.setLayoutManager(linearLayoutManager);

                //SetRecycleView(view);
            }


           // setRecycleView();
            // companiesAdapter.notifyDataSetChanged();
            //recyclerView.setAdapter(companiesAdapter);
            //recyclerView.setLayoutManager(linearLayoutManager);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String selectedDepartment,
                                boolean isPersonalAccount,
                                String selectedCompanyID,
                                String selectedCompanyName) {
        if (mListener != null) {
            mListener.SelectDepartmentDialogonFragmentInteraction(selectedDepartment,
                    isPersonalAccount,
                    selectedCompanyID,
                    selectedCompanyName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectDepartmentDialogOnFragmentInteractionListener) {
            mListener = (SelectDepartmentDialogOnFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SelectDepartmentDialogOnFragmentInteractionListener {
        // TODO: Update argument type and name

        /**
         *  String selectedDepartment =  "" ;
         boolean isPersonalAccount = false;
         String selectedCompanyID = "";
         String selectedCompanyName = "";
         * */
        void SelectDepartmentDialogonFragmentInteraction(String selectedDepartment,
                                                         boolean isPersonalAccount,
                                                         String selectedCompanyID,
                                                         String selectedCompanyName);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private  ClickListener clickListener;

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
