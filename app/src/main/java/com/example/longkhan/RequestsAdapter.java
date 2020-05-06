package com.example.longkhan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> {
    private List<RequestModel> requestList;
    private Context context;
    String url;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragmentManager;
    double trans_amountPerUser,wallet_total, ori_amountPerUser;
    double trans_cal, ori_cal;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView user_image;
        TextView user_amount;
        TextView trip_title;
        TextView request_title;
        TextView user_currency;
        ImageButton confirm_request_btn, cancel_request_btn;
        MyViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            user_image = view.findViewById(R.id.user_image);
            user_amount = view.findViewById(R.id.user_amount);
            confirm_request_btn = view.findViewById(R.id.confirm_request_btn);
            cancel_request_btn = view.findViewById(R.id.cancel_request_btn);
            trip_title =view.findViewById(R.id.trip_title);
            request_title =view.findViewById(R.id.request_title);
            user_currency = view.findViewById(R.id.user_currency);
        }
    }

    public RequestsAdapter(Context context, List<RequestModel> requestList, FragmentManager fragmentManager) {
        this.requestList = requestList;
        this.context = context;
        this.fragmentManager =fragmentManager;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final RequestModel request = requestList.get(position);
        //1 = wt_meber ,2 = wt_addwallet
        if (request.getPosition()==1){
            holder.request_title.setText(" wants to join ");
        }
        else {
            holder.request_title.setText(" wants to add money in ");
        }
        holder.username.setText(request.getUsername());
        holder.user_amount.setText(request.getOri_amount()+"");
        holder.trip_title.setText(request.getTriptitle());
        holder.user_currency.setText(request.getCurrency());
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String url_profile;
                for (QueryDocumentSnapshot doc : task.getResult()){
                    String value = doc.getString("username");
                    if (value.equals(request.getUsername())){
                        url=doc.getString("photo");
                        Glide.with(context).load(url).override(70,70).error(R.drawable.user).into(holder.user_image);
                    }
                }
            }
        });

        holder.cancel_request_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                final Map<String, Object> cancelReq = new HashMap<>();
                cancelReq.put("username", request.getUsername());
                cancelReq.put("ori_amount", request.getOri_amount());
                cancelReq.put("trans_amount", request.getTrans_amount());
                cancelReq.put("currency", request.getCurrency());

                System.out.println(cancelReq.toString());
                DocumentReference deleteRequest = db.collection("trips").document(request.getTripDoc());
                deleteRequest.update("wt_members", FieldValue.arrayRemove(cancelReq));
            }
        });

        trans_cal =request.getTrans_amount();
        ori_cal = request.getOri_amount();

        holder.confirm_request_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (request.getPosition()==1){

                    //wants to join
                    final Map<String, Object> confirmReq = new HashMap<>();
                    confirmReq.put("username", request.getUsername());
                    confirmReq.put("ori_amount", request.getOri_amount());
                    confirmReq.put("trans_amount", request.getTrans_amount());
                    confirmReq.put("currency", request.getCurrency());

                    DocumentReference deleteRequest = db.collection("trips").document(request.getTripDoc());
                    deleteRequest.update("wt_members", FieldValue.arrayRemove(confirmReq));
                    DocumentReference addRequest = db.collection("trips").document(request.getTripDoc());
                    addRequest.update("cf_members", FieldValue.arrayUnion(confirmReq));
                    db.collection("trips")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String value_start = document.getString("trip_docid");
                                            if ( value_start.equals(request.getTripDoc())) {
                                                wallet_total = document.getDouble("trip_total");
                                                wallet_total+=request.getTrans_amount();
                                                wallet_total = Math.round(wallet_total * 100.0)/100.0;
                                                db.collection("trips").document(request.getTripDoc()).update("trip_total", wallet_total);
                                                break;
                                            }
                                        }
                                    }
                                }
                            });
                    /** noti **/
                    db.collection("trips")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                            String searchedTripCode = document.getString("trip_code");
                                            if(searchedTripCode.equals(request.getTripCode())){
                                                ArrayList<Map> searched_cf_members = (ArrayList<Map>) document.get("cf_members");
                                                if(searched_cf_members != null) {
                                                    for(Object cf : searched_cf_members) {
                                                        HashMap<String, String> searchedUsername = (HashMap<String, String>) cf;
                                                        for(Map.Entry m : searchedUsername.entrySet()){
                                                            if(m.getKey().equals("username")){
                                                                createNotification((String) m.getValue()
                                                                        , SettingFragment.cfTripNoti((String) m.getValue(), request.getTriptitle()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            });
                }
                else {
                    //wants to add money in
                    final Map<String, Object> confirmReq = new HashMap<>();
                    final Map<String, Object> delReq = new HashMap<>();
                    db.collection("trips")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String value_start = document.getString("trip_docid");
                                            if ( value_start.equals(request.getTripDoc())) {
                                                ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                                for (Object s : cf_items) {
                                                    Map<String, Object> searchedUsername = (Map<String, Object>) s;
                                                    if (searchedUsername.get("username").equals(request.getUsername()) ){
                                                        trans_amountPerUser = Double.parseDouble(searchedUsername.get("trans_amount").toString()) + trans_cal;
                                                        ori_amountPerUser = Double.parseDouble(searchedUsername.get("ori_amount").toString()) + trans_cal;
                                                        confirmReq.put("username", request.getUsername());
                                                        confirmReq.put("ori_amount", ori_amountPerUser);
                                                        confirmReq.put("trans_amount", trans_amountPerUser);
                                                        confirmReq.put("currency", request.getCurrency());
                                                        System.out.println("aaa " + searchedUsername.toString());
                                                        DocumentReference delReq = db.collection("trips").document(request.getTripDoc());
                                                        delReq.update("cf_members", FieldValue.arrayRemove(searchedUsername));
                                                        DocumentReference addRequest = db.collection("trips").document(request.getTripDoc());
                                                        addRequest.update("cf_members", FieldValue.arrayUnion(confirmReq));
                                                        break;
                                                    }
                                                }break;
                                            }
                                        }
                                    }
                                }
                            });

                    final Map<String, Object> confirmReq2 = new HashMap<>();
                    confirmReq2.put("username", request.getUsername());
                    confirmReq2.put("ori_amount", ori_cal);
                    confirmReq2.put("trans_amount", trans_cal);
                    confirmReq2.put("currency", request.getCurrency());
                    DocumentReference deleteRequest = db.collection("trips").document(request.getTripDoc());
                    deleteRequest.update("wt_walletTrip", FieldValue.arrayRemove(confirmReq2));


                    db.collection("trips")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String value_start = document.getString("trip_docid");
                                            if ( value_start.equals(request.getTripDoc())) {
                                                wallet_total = document.getDouble("trip_total");
                                                wallet_total+= trans_cal;
                                                wallet_total = Math.round(wallet_total * 100.0) /100.0;
                                                db.collection("trips").document(request.getTripDoc()).update("trip_total", wallet_total);
                                                break;
                                            }
                                        }
                                    }
                                }
                            });

                    /** noti **/
                    db.collection("trips")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                            String searchedTripCode = document.getString("trip_code");
                                            if(searchedTripCode.equals(request.getTripCode())){
                                                ArrayList<Map> searched_cf_members = (ArrayList<Map>) document.get("cf_members");
                                                if(searched_cf_members != null) {
                                                    for(Object cf : searched_cf_members) {
                                                        HashMap<String, String> searchedUsername = (HashMap<String, String>) cf;
                                                        for(Map.Entry m : searchedUsername.entrySet()){
                                                            if(m.getKey().equals("username")){
                                                                createNotification((String) m.getValue()
                                                                        , SettingFragment.cfAmountNoti(request.getUsername()
                                                                                , request.getOri_amount(), request.getCurrency()
                                                                                , request.getTriptitle()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            });
                }
                requestList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });
    }
    @Override
    public int getItemCount() {
        return requestList.size();
    }

    /** notification **/

    public void createNotification(final String target, final String txt) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                String searchedUser = document.getString("username");
                                if(searchedUser.equals(target)) {
                                    String searchedToken = document.getString("token");
                                    Map<String, String> noti = new HashMap<>();
                                    noti.put("target", searchedToken);
                                    noti.put("txt", txt);
                                    db.collection("notification")
                                            .add(noti);
                                    break;
                                }
                            }
                        }
                    }
                });
    }
}
