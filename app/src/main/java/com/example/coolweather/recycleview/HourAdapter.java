
package com.example.coolweather.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolweather.R;

import java.util.List;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.ViewHolder> {
    private List<HourData> mHourList;

    public HourAdapter(List<HourData> hourDataList){
        mHourList = hourDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourData hourData = mHourList.get(position);
        holder.temperature.setText(hourData.getTemperature());
        holder.weatherPhenomena.setText(hourData.getWeatherPhenomena());
        holder.time.setText(hourData.getTime());
    }

    @Override
    public int getItemCount() {
        return mHourList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        //ImageView hourView;
        TextView time;//时间
        TextView weatherPhenomena;//天气现象文字
        TextView temperature;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = (TextView)itemView.findViewById(R.id.time_text);
            weatherPhenomena = (TextView)itemView.findViewById(R.id.wea_text);
            temperature = (TextView)itemView.findViewById(R.id.tem_text);
            //hourView = itemView;

        }
    }
}

