package com.ecoach.cosapp.RecycleAdapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ecoach.cosapp.DataBase.Companies;
import com.ecoach.cosapp.DataBase.Departments;
import com.ecoach.cosapp.DataBase.VerifiedCompanies;
import com.ecoach.cosapp.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by apple on 4/21/17.
 */

public class CompanyListStyleAdapter extends RecyclerView.Adapter<CompanyListStyleAdapterViewHolder>  {
    private Context context;
    private LayoutInflater inflater;
    List<VerifiedCompanies> data= Collections.emptyList();
    View view=null;
    int selected_position = -1;

    public CompanyListStyleAdapter(Context context, List<VerifiedCompanies> data) {
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.context=context;



    }

    @Override
    public CompanyListStyleAdapterViewHolder onCreateViewHolder(ViewGroup
                                                                  parent, int viewType) {
        view = inflater.inflate(R.layout.departments_view_cell, parent, false);
        // } else if(Application.BillersRecycleViewLayoutType.equalsIgnoreCase("List")){
        // view= inflater.inflate(R.layout.list_item_layout_billers,parent,false);}

        CompanyListStyleAdapterViewHolder holder = new CompanyListStyleAdapterViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(CompanyListStyleAdapterViewHolder
                                         holder, final int position) {

        final VerifiedCompanies items=data.get(position);
        if(selected_position == position){
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.primary_dark_color));
        }else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.txthiddenId.setText(items.getCompanyCuid());
        holder.departmentName.setText(items.getCompanyName().toString());

        holder.morebutton.setVisibility(View.VISIBLE);
        holder.morebutton.setText(items.getAccountType());
        if(items.getAccountType().equals("ADMIN")){

            holder.morebutton.setTextColor(context.getResources().getColor(R.color.colorGreen));

        }else if(items.getAccountType().equals("REP")){

            holder.morebutton.setTextColor(context.getResources().getColor(R.color.red_btn_bg_color));

        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                // Do your another stuff for your onClick
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
class CompanyListStyleAdapterViewHolder extends RecyclerView.ViewHolder {
    TextView txthiddenId,departmentName;
    Button morebutton;

    public CompanyListStyleAdapterViewHolder(View itemView) {
        super(itemView);


        txthiddenId=(TextView)itemView.findViewById(R.id.hiddenID);
        departmentName = (TextView) itemView.findViewById(R.id.departmentName);
        morebutton=(Button)itemView.findViewById(R.id.morebutton);





    }
}
