package com.example.hw03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {

    Context c;
    ArrayList<Forecast> forecasts;
    //static AdapterActivity mListener;

    public WeatherAdapter(Context c, ArrayList<Forecast> forecasts) {
        this.c = c;
        this.forecasts = forecasts;
        //this.mListener = (MainActivity)c;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_weather,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        int icon = forecasts.get(position).getDayIcon();
        String iconStr = String.valueOf(icon);
        if(icon < 10){
            iconStr = "0"+iconStr;
        }
        Picasso.get().load(MainActivity.image_url+iconStr+"-s.png").into(holder.iv_image_rv);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM''yy");
        holder.tv_date_rv.setText(simpleDateFormat.format(forecasts.get(position).getForecastDate()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ((CityWeather) c).setUI(forecasts.get(position));
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date_rv;
        ImageView iv_image_rv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date_rv = itemView.findViewById(R.id.tv_date_rv);
            iv_image_rv = itemView.findViewById(R.id.iv_image_rv);

        }

    }

}