package com.example.hw02;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context c;
    ArrayList<Track> trackList;

    public MyAdapter(Context c, ArrayList<Track> trackList) {
        this.c = c;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.tv_title.setText("Track: "+track.title);
        holder.tv_artist.setText("Artist: "+track.artist);
        holder.tv_track_price.setText("Price: "+track.trackPrice);
        holder.tv_releaseDate.setText("Date: "+track.releaseDate);
        holder.track = track;
        holder.context = c;
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_title,tv_artist,tv_track_price,tv_releaseDate;
        Track track;
        Context context;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_track_price = itemView.findViewById(R.id.tv_track_price);
            tv_releaseDate = itemView.findViewById(R.id.tv_releaseDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,DisplayTrack.class);
            intent.putExtra(MainActivity.TRACK_KEY, track);
            context.startActivity(intent);
        }
    }
}

