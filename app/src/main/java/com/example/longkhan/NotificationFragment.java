package com.example.longkhan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class NotificationFragment extends Fragment {
    private List<RequestModel> requestList = new ArrayList<>();
    private RequestsAdapter requestsAdapter;
    String  tripProfile ;
    String tripTitle,tripdoc ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notification_fragment, container, false);

        RecyclerView recycler_view = v.findViewById(R.id.recycleview_noti);
        requestsAdapter = new RequestsAdapter(getContext(),requestList,getFragmentManager());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(requestsAdapter);
        prepareRequestData();
        requestList.clear();
        return v;
    }
    private void prepareRequestData() {
        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String usernameRequestData = null;
                        double ori_amountRequestData = 0;
                        double trans_amountRequestData = 0;
                        String tripCodeRequestData = null;
                        String tripDocRequestData = null;

                        String usrRequest_wallet = null;
                        double ori_amountRequest_wallet = 0;
                        double trans_amountRequest_wallet = 0;
                        String tripCodeRequest_wallet = null;
                        String tripDocRequest_wallet = null;
                        String currency = null;
                        for (QueryDocumentSnapshot document : task.getResult()){
                            String value = document.getString("collector");
                            if (value.equals(LogIn.userName)) {
                                String title = document.getString("trip_title");
                                ArrayList<Map> searchedMember = (ArrayList<Map>) document.get("wt_members");
                                ArrayList<Map> searchedMemer_Addwallet = (ArrayList<Map>) document.get("wt_walletTrip");
                                if (searchedMember!=null){
                                    for (Object s : searchedMember) {
                                        HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                        for(Map.Entry m : searchedUsername.entrySet()) {
                                            if(m.getKey().equals("username")) {
                                                usernameRequestData = m.getValue().toString();
                                                tripCodeRequestData = document.getString("trip_code");
                                                tripDocRequestData = document.getString("trip_docid");
                                            }
                                            if(m.getKey().equals("ori_amount")) {
                                                ori_amountRequestData = Double.parseDouble(m.getValue().toString());
                                                tripCodeRequestData = document.getString("trip_code");
                                            }
                                            if(m.getKey().equals("trans_amount")) {
                                                trans_amountRequestData = Double.parseDouble(m.getValue().toString());
                                                tripCodeRequestData = document.getString("trip_code");
                                            }
                                            if(m.getKey().equals("currency")) {
                                                currency = m.getValue().toString();
                                            }
                                            if(usernameRequestData != null && ori_amountRequestData != 0 && trans_amountRequestData != 0
                                                    && tripCodeRequestData != null && tripDocRequestData != null && currency != null) {
                                                createRequestAdater(usernameRequestData, ori_amountRequestData, trans_amountRequestData,
                                                        tripCodeRequestData, tripDocRequestData,1,title,currency);
                                                usernameRequestData = null;
                                                ori_amountRequestData = 0;
                                                trans_amountRequestData = 0;
                                                tripCodeRequestData = null;
                                                tripDocRequestData = null;
                                            }
                                        }
                                    }
                                }
                                if (searchedMemer_Addwallet!=null){
                                    for (Object k :searchedMemer_Addwallet){
                                        HashMap<String, Object> searchedUsr_Addwallet = (HashMap<String, Object>) k;
                                        for(Map.Entry m : searchedUsr_Addwallet.entrySet()){
                                            if(m.getKey().equals("username")) {
                                                usrRequest_wallet = m.getValue().toString();
                                                tripCodeRequest_wallet = document.getString("trip_code");
                                                tripDocRequest_wallet = document.getString("trip_docid");
                                                System.out.println("eee " +  usrRequest_wallet);
                                            }
                                            if(m.getKey().equals("ori_amount")) {
                                                ori_amountRequest_wallet = Double.parseDouble(m.getValue().toString());
                                                System.out.println("xxx " +  ori_amountRequest_wallet);
                                            }
                                            if(m.getKey().equals("trans_amount")) {
                                                trans_amountRequest_wallet = Double.parseDouble(m.getValue().toString());
                                                tripCodeRequestData = document.getString("trip_code");
                                            }
                                            if(m.getKey().equals("currency")) {
                                                currency = m.getValue().toString();
                                            }
                                            if(usrRequest_wallet != null && ori_amountRequest_wallet != 0 && tripCodeRequest_wallet != null && tripDocRequest_wallet != null) {
                                                createRequestAdater(usrRequest_wallet, ori_amountRequest_wallet, trans_amountRequest_wallet, tripCodeRequest_wallet, tripDocRequest_wallet,2,title,currency);
                                                usrRequest_wallet = null;
                                                ori_amountRequest_wallet = 0;
                                                tripCodeRequest_wallet = null;
                                                tripDocRequest_wallet = null;
                                            }
                                            System.out.println("yyyy " + usrRequest_wallet +" "+ori_amountRequest_wallet +" "+tripCodeRequest_wallet +" "+tripDocRequest_wallet +"2 "+currency+title);
                                        }
                                    }
                                }
                        }}

                    }
                });

    }

    private void createRequestAdater(String username, double ori_amount, double trans_amount,  String tripCode, String tripDoc,int position,String trip_title,String currency) {
        RequestModel request = new RequestModel(username, ori_amount, trans_amount, tripCode, tripDoc,position,trip_title,currency);
        requestList.add(request);
        requestsAdapter.notifyDataSetChanged();
        //1 = wt_meber ,2 = wt_addwallet
    }
}
