package com.example.inclass14;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder>{

    Context c;
    ArrayList<Place> places;
    static PlaceAdapterActivity mListener;
    int tripPosition;

    public PlaceAdapter(Context c, ArrayList<Place> places,int tripPosition) {
        this.c = c;
        this.places = places;
        this.mListener = (MainActivity)c;
        this.tripPosition = tripPosition;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view_sub,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Picasso.get().load(places.get(position).getIcon()).into(holder.iv_place_icon);
        holder.tv_place.setText(places.get(position).getName());
        holder.tripPositionHolder = tripPosition;
        holder.clickedPosition = position;

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_place;
        ImageButton ib_delete;
        ImageView iv_place_icon;
        int tripPositionHolder;

        int clickedPosition;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_place = itemView.findViewById(R.id.tv_place);
            ib_delete = itemView.findViewById(R.id.ib_delete);
            iv_place_icon = itemView.findViewById(R.id.iv_place_icon);

            ib_delete.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mListener.deletePlace(tripPositionHolder, clickedPosition);
        }
    }

    public interface PlaceAdapterActivity{
        public void deletePlace(int tripPosition, int position);
    }

}