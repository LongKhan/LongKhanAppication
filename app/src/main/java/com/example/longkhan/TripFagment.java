package com.example.longkhan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripFagment extends Fragment {
    private TripsAdapter tripsAdapter;
    private List<TripModel> tripList = new ArrayList<>();
    String  tripProfile ;
    String tripTitle,tripdoc ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trip_fragment, container, false);
        RecyclerView recycler_view = v.findViewById(R.id.recycleview_trip);
        tripsAdapter = new TripsAdapter(getContext(),tripList,getFragmentManager());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(tripsAdapter);
        prepareTripData();
        return v;
    }
    private void prepareTripData() {
        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                                ArrayList<Map> searchedMember = (ArrayList<Map>) document.get("cf_members");
                                for (Object s : searchedMember) {
                                    if (s.toString().contains(LogIn.userName) && s != null) {
                                        HashMap<String, String> searchedUsername = (HashMap<String, String>) s;
                                        for (Map.Entry m : searchedUsername.entrySet()) {
                                            if (m.getValue().equals(LogIn.userName)) {
                                                tripTitle = document.getString("trip_title");
                                                tripProfile = document.getString("trip_profile");
                                                tripdoc =document.getString("trip_docid");
                                                createAdater(tripProfile,tripTitle,tripdoc);
                                            }
                                        }
                                        break;
                                    }
                                }
                        }

                    }
                });

    }
    private void createAdater(String urlProfile, String tripName,String tripdoc) {
        TripModel trip = new TripModel(tripName, urlProfile,tripdoc);
        tripList.add(trip);
        tripsAdapter.notifyDataSetChanged();
    }
}
