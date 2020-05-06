package com.example.longkhan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.MyViewHolder> {
    private List<TripModel> tripList;
    private Context context;
    FragmentManager fragmentManager;
    static String doctripid;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView trip_title;
        ImageView imageProfile;
        MyViewHolder(View view) {
            super(view);
            trip_title = view.findViewById(R.id.trip_title);
            imageProfile = view.findViewById(R.id.trip_profile);
        }
    }

    public TripsAdapter(Context context,List<TripModel> tripList, FragmentManager fragmentManager) {
        this.tripList = tripList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_list_item, parent,false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final TripModel trip = tripList.get(position);
        holder.trip_title.setText(trip.getTripTitle());
        Glide.with(context).load(trip.getUrlProfile()).override(70,70).error(R.drawable.team_image).into(holder.imageProfile);
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 doctripid=trip.getDoctripid();
                 fragmentManager.beginTransaction()
                         .replace(R.id.fragmentcontainner, new Trip())
                         .commit();
             }
         });
    }
    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
