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

public class PlaceMainAdapter extends RecyclerView.Adapter<PlaceMainAdapter.MyViewHolder> {

    Context c;
    ArrayList<Place> places;
    static PlaceMainAdapterActivity mListener;
    static int tripPosition;

    public PlaceMainAdapter(Context c, ArrayList<Place> places) {
        this.c = c;
        this.places = places;
        this.mListener = (AddPlacesActivity) c;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view_place, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Picasso.get().load(places.get(position).getIcon()).into(holder.iv_place_icon);
        holder.tv_place.setText(places.get(position).getName());
        holder.clickedPosition = position;

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_place;
        ImageButton ib_add_place;
        ImageView iv_place_icon;


        int clickedPosition;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_place = itemView.findViewById(R.id.tv_place);
            ib_add_place = itemView.findViewById(R.id.ib_add_place);
            iv_place_icon = itemView.findViewById(R.id.iv_place_icon);

            ib_add_place.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mListener.addPlace(clickedPosition);
        }
    }

    public interface PlaceMainAdapterActivity {
        public void addPlace(int position);
    }
}