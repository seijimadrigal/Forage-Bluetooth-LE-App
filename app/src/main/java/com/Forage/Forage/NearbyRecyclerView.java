package com.Forage.Forage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NearbyRecyclerView extends RecyclerView.Adapter<NearbyRecyclerView.ViewHolder> {

    private List<Object> nearbyPeople;

    public NearbyRecyclerView(List<Object> nearbyPeople){
            this.nearbyPeople = nearbyPeople;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView personName;
        ImageView personImage;

         ViewHolder(@NonNull View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.person_image);
            personName = itemView.findViewById(R.id.person_name);
        }
    }



    @NonNull
    @Override
    public NearbyRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_people_template,parent,false);
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyRecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return nearbyPeople.size();
    }


}
