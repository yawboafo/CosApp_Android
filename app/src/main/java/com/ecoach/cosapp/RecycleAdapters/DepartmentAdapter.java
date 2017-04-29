package com.ecoach.cosapp.RecycleAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ecoach.cosapp.DataBase.Departments;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.Utilities.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by apple on 4/8/17.
 */

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapterViewHolder>  {
    private Context context;
    private LayoutInflater inflater;
    List<Departments> data= Collections.emptyList();
    View view=null;
    int selected_position = -1;
    public DepartmentAdapter(Context context, List<Departments> data) {
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.context=context;



    }

    @Override
    public DepartmentAdapterViewHolder onCreateViewHolder(ViewGroup
                                                                     parent, int viewType) {
        view = inflater.inflate(R.layout.departments_view_cell, parent, false);
        // } else if(Application.BillersRecycleViewLayoutType.equalsIgnoreCase("List")){
        // view= inflater.inflate(R.layout.list_item_layout_billers,parent,false);}

        DepartmentAdapterViewHolder holder = new DepartmentAdapterViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(DepartmentAdapterViewHolder
                                         holder, final int position) {

        final Departments items=data.get(position);
        if(selected_position == position){
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.primary_dark_color));
        }else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.txthiddenId.setText(items.getDepartmentid());
        holder.departmentName.setText(items.getDepartmentname().toString());


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
class DepartmentAdapterViewHolder extends RecyclerView.ViewHolder {
    TextView txthiddenId,departmentName;
    Button morebutton;

    public DepartmentAdapterViewHolder(View itemView) {
        super(itemView);


        txthiddenId=(TextView)itemView.findViewById(R.id.hiddenID);
        departmentName = (TextView) itemView.findViewById(R.id.departmentName);
        morebutton=(Button)itemView.findViewById(R.id.morebutton);





    }
}

