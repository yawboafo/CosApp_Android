package com.ecoach.cosapp.Activites;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.ecoach.cosapp.Activites.Company.CompaniesActivity;
import com.ecoach.cosapp.Activites.Company.CompanyDetails;
import com.ecoach.cosapp.Application.Application;
import com.ecoach.cosapp.DataBase.VerifiedCompanies;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.RecycleAdapters.CompaniesViewAdapter;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

RecyclerView searchedItems;
    private SearchView searchView;
    LinearLayoutManager linearLayoutManager;
    CompaniesViewAdapter companiesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // add back arrow to toolbar
        if (getSupportActionBar() != null){

            getSupportActionBar().setTitle("Search ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        setviews();
    }

    private void setviews() {

        searchView=(SearchView)findViewById(R.id.search_bar);
        searchView.setFocusable(true);
        searchView.setIconified(false);


        searchedItems= (RecyclerView)findViewById(R.id.searchedItems);
        searchedItems.addOnItemTouchListener(new RecyclerTouchListener(SearchActivity.this, searchedItems, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView tv=(TextView)view.findViewById(R.id.labelTxt);
                String selectedCompanyName=tv.getText().toString();

                Application.setSelectedCompanyName(selectedCompanyName);


                TextView id=(TextView)view.findViewById(R.id.companyid);
                String selectedid=id.getText().toString();

                Application.setSelectedCompanyID(selectedid);


                Intent intent = new Intent(SearchActivity.this,CompanyDetails.class);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        //        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(),Categories.getAllCategories());



        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                SetupRecycleview(VerifiedCompanies.getCompanyByLikeName(newText));
                return false;
            }
        });

    }

    private void SetupRecycleview(  List<VerifiedCompanies> companiesArrayList){


        companiesAdapter = new CompaniesViewAdapter(SearchActivity.this, companiesArrayList);

        linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        searchedItems.setAdapter(companiesAdapter);
        searchedItems.setLayoutManager(linearLayoutManager);


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
