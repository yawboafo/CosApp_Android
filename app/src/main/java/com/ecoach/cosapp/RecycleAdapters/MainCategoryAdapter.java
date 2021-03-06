package com.ecoach.cosapp.RecycleAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ecoach.cosapp.DataBase.Categories;
import com.ecoach.cosapp.DataBase.Companies;
import com.ecoach.cosapp.R;
import com.ecoach.cosapp.Utilities.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by apple on 3/28/17.
 */

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapterViewHolder>  {
    private Context context;
    private LayoutInflater inflater;
    List<Categories> data= Collections.emptyList();
    View view=null;

    public MainCategoryAdapter(Context context, List<Categories> data) {
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.context=context;



    }

    @Override
    public MainCategoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.maincategorycell, parent, false);
        // } else if(Application.BillersRecycleViewLayoutType.equalsIgnoreCase("List")){
        // view= inflater.inflate(R.layout.list_item_layout_billers,parent,false);}

        MainCategoryAdapterViewHolder holder = new MainCategoryAdapterViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MainCategoryAdapterViewHolder holder, int position) {

        final Categories items=data.get(position);


        holder.txthiddenId.setText(items.getCategoryID().toString());
        holder.txtLabel.setText(items.getCategoryNames().toString());


        TextDrawable drawable = TextDrawable.builder()
                .buildRoundRect(items.getCategoryNames().toString().substring(0, 1), context.getResources().getColor(R.color.colorPrimary), 10);
        Drawable d = new BitmapDrawable(ViewUtils.drawableToBitmap(drawable));
        Picasso.with(context)
                .load(items.getCategoryBackgroundImage())
                .placeholder(d)
                .fit()
                .centerCrop()
                .into(holder.iconView);






    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
class MainCategoryAdapterViewHolder extends RecyclerView.ViewHolder {
    TextView txthiddenId,txtLabel;

    CircleImageView iconView;

    public MainCategoryAdapterViewHolder(View itemView) {
        super(itemView);

        txthiddenId=(TextView)itemView.findViewById(R.id.catID);
        iconView = (CircleImageView)itemView.findViewById(R.id.iconview);
        txtLabel=(TextView)itemView.findViewById(R.id.labelview);






    }
}
