
package com.example.longkhan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostAdapter extends RecyclerView.Adapter<CostAdapter.MyViewHolder> {
    ArrayList<String> note;
    double result,cost_trip,wallet;
    private List<CostModel> costList;
    private Context context;
    static String title;
    static String cost_docid;
    static String url,cost,detail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragmentManager;
    String pattern = "dd/MM/yyyy", category;
    DateFormat df = new SimpleDateFormat(pattern);
    TextView text_title,text_cost,text_category,text_date,text_detail ,text_note ,text_currency ;
    EditText edit_cost_title, edit_cost;
    TextView edit_cost_currency, edit_date, edit_detail, edit_equal;
    ImageView image_deatail_bill;
    static ImageView edit_image_bill;
    Spinner edit_category_spinner ;
    StorageReference FolderStorange = FirebaseStorage.getInstance().getReference().child("Receipt_Cost");
    Button edit_ok;
    String currencyTrip;
    String trip_code,trip_docid;
    double this_cost,cost_edit,incal, edit_trans_cost, edit_ori_cost, this_ori_cost;

    int checkmore = 2;
    String equalvalue;
    Cost_ShowMember cost_memberAdapter;
    private List<MemberModel> memberList = new ArrayList<>();
    ArrayList<String> equal_list;



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cost_name;
        ImageView bill_image;
        TextView cost,adder_view;
        ImageButton edit_btn, remove_btn;
        TextView cost_currency;
        MyViewHolder(View view) {
            super(view);
            cost_name = view.findViewById(R.id.cost_title);
            bill_image = view.findViewById(R.id.receipt_image);
            cost = view.findViewById(R.id.cost_trip);
            edit_btn = view.findViewById(R.id.edit_btn);
            remove_btn = view.findViewById(R.id.delete_btn);
            adder_view= view.findViewById(R.id.adder_view);
            cost_currency = view.findViewById(R.id.cost_list_currency);
        }
    }



    public CostAdapter(Context context, List<CostModel> costList, FragmentManager fragmentManager) {
        this.costList = costList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cost_list_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CostModel value = costList.get(position);
        System.out.println("cost in "+value.getCost_adder());
        holder.cost_name.setText(value.getCost_name());
        holder.cost.setText(value.getThis_cost());
        holder.adder_view.setText(value.getCost_adder());
        holder.cost_currency.setText(value.getTrip_currency());
        Glide.with(context).load(value.getReceipt()).override(50,50).error(R.drawable.bill).into(holder.bill_image);
        trip_code =value.getTrip_code();
        trip_docid = value.getTrip_docid();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialogPopup = new Dialog(context);
                dialogPopup.setContentView(R.layout.cost_detail_popup);
                dialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogPopup.show();

                text_title = dialogPopup.findViewById(R.id.text_title);
                text_cost = dialogPopup.findViewById(R.id.text_cost);
                text_category = dialogPopup.findViewById(R.id.text_category);
                text_date = dialogPopup.findViewById(R.id.text_date);
                text_detail = dialogPopup.findViewById(R.id.text_detail);
                text_currency = dialogPopup.findViewById(R.id.details_cuurency);
                image_deatail_bill = dialogPopup.findViewById(R.id.image_deatail_bill);
                text_note = dialogPopup.findViewById(R.id.detail_equal_result);

                db.collection("costs")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            String member_name = null , amout_peruser = null,checkmember = null;
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String valuecode = document.getString("cost_docid");
                                        String thisdoc = value.getCostDoc();
                                        if ( thisdoc.equals(valuecode) ) {
                                            System.out.println(thisdoc+" == "+valuecode);
                                            Timestamp date = document.getTimestamp("date");
                                            Date datecon = date.toDate();
                                            String todate = df.format(datecon);
                                            text_title.setText(document.getString("cost_title"));
                                            text_cost.setText(document.getDouble("ori_cost").toString());
                                            text_category.setText(document.getString("category"));
                                            text_currency.setText(document.getString("currency"));
                                            text_date.setText(todate);
                                            Glide.with(context).load(value.getReceipt()).override(300, 300).error(R.drawable.bill).into(image_deatail_bill);

                                            if (document.getString("details").equals(" ")){
                                                text_detail.setText(document.getString("-"));

                                            }else text_detail.setText(document.getString("details"));

                                            ArrayList<String> cf_items = (ArrayList<String>) document.get("note");
                                            if (cf_items.size()!=0){
                                                text_note.setText("");
                                                for (int j = 0; j < cf_items.size(); j++){
                                                    if(j == cf_items.size()-1) {
                                                        text_note.append(cf_items.get(j));
                                                    }
                                                    else {
                                                        text_note.append(cf_items.get(j) + ",");
                                                    }
                                                }
                                            }else
                                                text_note.setText("Yes");

                                        }
                                    }
                                }
                            }
                        });


            }
        });


        if (LogIn.userName.equals(value.getCost_adder())){
            holder.remove_btn.setVisibility(View.VISIBLE);
            holder.edit_btn.setVisibility(View.VISIBLE);

            holder.remove_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    AlertDialog.Builder dialogDeleteTrip=new AlertDialog.Builder(context);
                    dialogDeleteTrip.setMessage("Confirm delete this cost ?");
                    dialogDeleteTrip.setNeutralButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            costList.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            note = new ArrayList<>() ;
                            note = value.getNote();
                            result = Double.parseDouble(value.getThis_cost());
                            final Map<String, Object> object = new HashMap<>();
                            final Map<String,Object> objectold = new HashMap<>();
                            System.out.println(note.toString());
                            if (note.size() > 1){
                                Double perresult = result/note.size();
                                perresult = Math.round(perresult * 100.0) / 100.0;
                                System.out.println(perresult);
                                final Double finalPerresult = perresult;
                                db.collection("trips")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            String member_name = null ,checkmember = null,currency=null;
                                            Double cal,ori_amount,trans_amount,amout_peruser = null;
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String valuecode = document.getString("trip_code");
                                                        if (value.trip_code .equals(valuecode) ) {
                                                            System.out.println("3");
                                                            ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                                            for (Object s : cf_items) {
                                                                HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                                                for(Map.Entry m : searchedUsername.entrySet()) {
                                                                    for (String v  : note){
                                                                        if (v.equals(m.getValue()) && !m.getValue().equals(checkmember) ){
                                                                            member_name = m.getValue().toString();
                                                                            checkmember = m.getValue().toString();
                                                                        }
                                                                    }
                                                                    if (m.getValue().equals(member_name)){
                                                                        member_name = m.getValue().toString();
                                                                    }
                                                                    if (m.getKey().equals("trans_amount")){
                                                                        trans_amount = Double.parseDouble(m.getValue().toString());
                                                                        cal = Double.parseDouble(m.getValue().toString());
                                                                        cal += finalPerresult;
                                                                        amout_peruser = cal;
                                                                        amout_peruser = Math.round(amout_peruser * 100.0) / 100.0;
                                                                    }
                                                                    if (m.getKey().equals("ori_amount")){
                                                                        ori_amount = Double.parseDouble(m.getValue().toString());
                                                                    }
                                                                    if (m.getKey().equals("currency")){
                                                                        currency = m.getValue().toString();
                                                                    }}
                                                                if (member_name != null && amout_peruser != null && ori_amount!=null && currency!=null){
                                                                    System.out.println("3.5");
                                                                    objectold.put("currency",currency);
                                                                    objectold.put("ori_amount",ori_amount );
                                                                    objectold.put("trans_amount", trans_amount);
                                                                    objectold.put("username", member_name);
                                                                    trans_amount = null;
                                                                    if (trans_amount==null){
                                                                        DocumentReference deleteRequest = db.collection("trips").document(value.trip_docid);
                                                                        deleteRequest.update("cf_members", FieldValue.arrayRemove(objectold));
                                                                        object.put("ori_amount",amout_peruser );
                                                                        object.put("currency",currency);
                                                                        object.put("trans_amount", amout_peruser);
                                                                        object.put("username", member_name);
                                                                        System.out.println("1.5  "+member_name);
                                                                        savetoData(object,value.trip_docid,value.trip_code,value.getCostDoc());
                                                                        member_name = null;
                                                                        amout_peruser = null;
                                                                        ori_amount = null;
                                                                        currency = null;}

                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            else {
                                db.collection("trips")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            Double finalperesult2;
                                            String member_name = null ,checkmember = null,currency=null;
                                            Double cal,ori_amount,trans_amount,amout_peruser = null;
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String valuecode = document.getString("trip_code");
                                                        if ( value.trip_code.equals(valuecode) ) {
                                                            System.out.println("445");
                                                            ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                                            finalperesult2 = Math.round(( result/cf_items.size()) * 100.0) / 100.0;
                                                            for (Object s : cf_items) {
                                                                HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                                                for(Map.Entry m : searchedUsername.entrySet()) {
                                                                    if (m.getKey().equals("username")  && !m.getValue().equals(checkmember)){
                                                                        member_name = m.getValue().toString();
                                                                        checkmember = m.getValue().toString();
                                                                    }
                                                                    if (m.getKey().equals("trans_amount")){
                                                                        trans_amount = Double.parseDouble(m.getValue().toString());
                                                                        cal = Double.parseDouble(m.getValue().toString());
                                                                        cal += finalperesult2;
                                                                        amout_peruser = cal;
                                                                    }
                                                                    if (m.getKey().equals("ori_amount")){
                                                                        ori_amount = Double.parseDouble(m.getValue().toString());
                                                                    }
                                                                    if (m.getKey().equals("currency")){
                                                                        currency = m.getValue().toString();
                                                                    }
                                                                    if (member_name != null && amout_peruser != null && ori_amount!=null && currency!=null){
                                                                        objectold.put("currency",currency);
                                                                        objectold.put("ori_amount",ori_amount );
                                                                        objectold.put("trans_amount", trans_amount);
                                                                        objectold.put("username", member_name);

                                                                        DocumentReference deleteRequest = db.collection("trips").document(value.trip_docid);
                                                                        deleteRequest.update("cf_members", FieldValue.arrayRemove(objectold));
                                                                        trans_amount = null;
                                                                        if (trans_amount==null){
                                                                            amout_peruser = Math.round(amout_peruser * 100.0) / 100.0;
                                                                            object.put("ori_amount",amout_peruser );
                                                                            object.put("currency",currency);
                                                                            object.put("trans_amount", amout_peruser);
                                                                            object.put("username", member_name);
                                                                            System.out.println("1.5  "+member_name);
                                                                            savetoData(object,value.trip_docid,value.trip_code,value.getCostDoc());
                                                                            member_name = null;
                                                                            amout_peruser = null;
                                                                            ori_amount = null;
                                                                            currency = null;

                                                                        }
                                                                        System.out.println("old object " + objectold);
                                                                        System.out.println("new object " + object);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });}

                            /** noti **/
                            db.collection("trips")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot document : task.getResult()) {
                                                    String searchedTripCode = document.getString("trip_code");
                                                    if(searchedTripCode.equals(value.getTrip_code())){
                                                        ArrayList<Map> searched_cf_members = (ArrayList<Map>) document.get("cf_members");
                                                        if(searched_cf_members != null) {
                                                            for(Object cf : searched_cf_members) {
                                                                HashMap<String, String> searchedUsername = (HashMap<String, String>) cf;
                                                                for(Map.Entry m : searchedUsername.entrySet()){
                                                                    if(m.getKey().equals("username")){
                                                                        createNotification((String) m.getValue()
                                                                                , SettingFragment.delCostNoti(value.getCost_name()
                                                                                        ,document.getString("trip_title")
                                                                                        ,LogIn.userName));
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
                    });dialogDeleteTrip.show();

                }
            });



            holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //fragmentManager.beginTransaction().replace(R.id.fragmentcontainner, new EditCost()).commit();


                    final Dialog dialogPopup = new Dialog(context);
                    dialogPopup.setContentView(R.layout.edit_cost_popup);
                    dialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialogPopup.show();

                    edit_image_bill = dialogPopup.findViewById(R.id.image_bill_view);
                    edit_cost_title = dialogPopup.findViewById(R.id.editText_cost_title);
                    edit_cost = dialogPopup.findViewById(R.id.editText_cost);
                    edit_cost_currency = dialogPopup.findViewById(R.id.edit_cost_currency);
                    edit_date = dialogPopup.findViewById(R.id.edit_date);
                    edit_category_spinner = dialogPopup.findViewById(R.id.edit_category_spinner);
                    edit_detail = dialogPopup.findViewById(R.id.editText_details);
                    edit_ok = dialogPopup.findViewById(R.id.ok_edit);
                    edit_equal = dialogPopup.findViewById(R.id.edit_detail_equal_result);


//                    edit_add_bill = dialogPopup.findViewById(R.id.btn_add_Bill);


                    db.collection("costs")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                String member_name = null , amout_peruser = null,checkmember = null;
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String valuecode = document.getString("cost_docid");
                                            String thisdoc = value.getCostDoc();
                                            if ( thisdoc.equals(valuecode) ) {
                                                edit_cost_title.setText(document.getString("cost_title"));
                                                edit_cost.setText(document.getDouble("ori_cost").toString());
                                                edit_cost_currency.setText(document.getString("currency"));
                                                currencyTrip = document.getString("currency");
                                                category = document.getString("category");
                                                this_cost= document.getDouble("trans_cost");
                                                this_ori_cost = document.getDouble("ori_cost");

                                                Glide.with(context).load(value.getReceipt()).override(300, 300).error(R.drawable.bill).into(edit_image_bill);


                                                if (document.getString("details").equals(" ")){
                                                    edit_detail.setText(document.getString("-"));

                                                }else edit_detail.setText(document.getString("details"));



                                                equal_list = (ArrayList<String>) document.get("note");


                                                ArrayList<String> cf_items = (ArrayList<String>) document.get("note");

                                                if (equal_list.size()!=0){
                                                    for (int j = 0; j < equal_list.size(); j++){
                                                        equalvalue = "No";
                                                        edit_equal.setText(equalvalue);

                                                    }
                                                }else {
                                                    equalvalue = "Yes";
                                                    edit_equal.setText(equalvalue);
                                                }

                                                Timestamp date = document.getTimestamp("date");
                                                Date ts_to_date = date.toDate();
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                String new_format_date = sdf.format(ts_to_date);
                                                int edit_year = Integer.parseInt(new_format_date.substring(0,4));
                                                int edit_month = Integer.parseInt(new_format_date.substring(5,7));
                                                int edit_date_sub = Integer.parseInt(new_format_date.substring(8,10));
                                                String [] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                                                String str = month[edit_month-1] + " " + edit_date_sub + ",  " + edit_year;
                                                edit_date.setText(str);

                                                final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.Category,android.R.layout.simple_spinner_item);
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                edit_category_spinner.setAdapter(adapter);
                                                int selectionPosition= adapter.getPosition(category);
                                                edit_category_spinner.setSelection(selectionPosition);
                                                System.out.println("yy " + selectionPosition);

                                                final RecyclerView recycler_view_cost = dialogPopup.findViewById(R.id.edit_recycler_view_member);
                                                final LinearLayoutManager layoutManage = new LinearLayoutManager(context);
                                                cost_memberAdapter = new Cost_ShowMember(context,memberList);
                                                layoutManage.setOrientation(LinearLayoutManager.HORIZONTAL);
                                                recycler_view_cost.setLayoutManager(layoutManage);
                                                recycler_view_cost.setItemAnimator(new DefaultItemAnimator());
                                                recycler_view_cost.setAdapter(cost_memberAdapter);
                                                prepareCostsMembertData2( value.getTrip_code());
                                                memberList.clear();

                                            }
                                        }
                                    }
                                }
                            });
                    edit_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            category = edit_category_spinner.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    edit_ok.setOnClickListener(new View.OnClickListener() {
                        Double cost_total;
                        boolean check = false;
                        @Override
                        public void onClick(View view) {
                            final AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
                            if (edit_cost_title.getText().toString().length() != 0){
                                cost_edit =   Double.parseDouble(edit_cost.getText().toString());
                                cost_edit = Math.round(cost_edit * 100.0) / 100.0;
                                System.out.print(cost_edit + " xxx " + this_ori_cost);
                                if(cost_edit != this_ori_cost){
                                    /** currency **/
                                    String url = "http://data.fixer.io/api/latest?access_key=75eebbc51210e28b820dd55e3ca714b3";
                                    OkHttpClient client = new OkHttpClient();
                                    final Request request = new Request.Builder()
                                            .url(url)
                                            .build();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {
                                        }
                                        @Override
                                        public void onResponse(Response response) throws IOException {
                                            String mMessage = response.body().string();
                                            try {
                                                JSONObject onj = new JSONObject(mMessage);
                                                final JSONObject b = onj.getJSONObject("rates");

                                                edit_trans_cost = Double.parseDouble(b.getString(value.trip_currency))
                                                        / Double.parseDouble(b.getString(edit_cost_currency.getText().toString()))
                                                        * cost_edit ;
                                                edit_trans_cost = Math.round(edit_trans_cost * 100.0) / 100.0;
                                                edit_ori_cost = Math.round(Double.parseDouble(edit_cost.getText().toString()) * 100.0) / 100.0;

                                                System.out.println(edit_trans_cost);

                                                DocumentReference updateCost = db.collection("costs").document(value.getCostDoc());
                                                updateCost.update("cost_title", edit_cost_title.getText().toString());
                                                updateCost.update("ori_cost", edit_ori_cost);
                                                updateCost.update("trans_cost", edit_trans_cost);
                                                updateCost.update("category", edit_category_spinner.getSelectedItem().toString());
                                                updateCost.update("details", edit_detail.getText().toString());

                                                db.collection("trips").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String valuee = document.getString("trip_code");
                                                                if ( value.getTrip_code().equals(valuee)){
                                                                    result = document.getDouble("trip_cost");
                                                                    if ( cost_edit > this_ori_cost){
                                                                        System.out.println("AA "+result+" thi"+ edit_trans_cost);
                                                                        incal = cost_edit - this_ori_cost;
                                                                        try {
                                                                            incal = Double.parseDouble(b.getString(value.trip_currency))
                                                                                    / Double.parseDouble(b.getString(edit_cost_currency.getText().toString()))
                                                                                    * incal ;
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        result += incal;
                                                                        checkmore = 0;
                                                                        calpercost(incal,0);
                                                                        result = Math.round(result * 100.0)/ 100.0;
                                                                        DocumentReference updateCosttrip = db.collection("trips").document(value.getTrip_docid());
                                                                        updateCosttrip.update("trip_cost",result);
                                                                    }else if (cost_edit < this_ori_cost){
                                                                        System.out.println("BB "+result+" thi"+edit_trans_cost);
                                                                        incal = this_ori_cost - cost_edit;
                                                                        try {
                                                                            incal = Double.parseDouble(b.getString(value.trip_currency))
                                                                                    / Double.parseDouble(b.getString(edit_cost_currency.getText().toString()))
                                                                                    * incal ;
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        result -= incal;
                                                                        checkmore =1;
                                                                        calpercost(incal,1);
                                                                        result = Math.round(result * 100.0)/ 100.0;
                                                                        DocumentReference updateCosttrip = db.collection("trips").document(value.getTrip_docid());
                                                                        updateCosttrip.update("trip_cost",result);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                else {
                                    System.out.print("qqq ");
                                    DocumentReference updateCostTrip = db.collection("costs").document(value.getCostDoc());
                                    updateCostTrip.update("cost_title", edit_cost_title.getText().toString());
                                    updateCostTrip.update("category", edit_category_spinner.getSelectedItem().toString());
                                    updateCostTrip.update("details", edit_detail.getText().toString());
                                }

                            }
                            else{alertdialog.setMessage("Please fill in cost title.")
                                    .create()
                                    .show();
                            }
                            dialogPopup.dismiss();
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
                                            if(searchedTripCode.equals(value.getTrip_code())){
                                                ArrayList<Map> searched_cf_members = (ArrayList<Map>) document.get("cf_members");
                                                if(searched_cf_members != null) {
                                                    for(Object cf : searched_cf_members) {
                                                        HashMap<String, String> searchedUsername = (HashMap<String, String>) cf;
                                                        for(Map.Entry m : searchedUsername.entrySet()){
                                                            if(m.getKey().equals("username")){
                                                                createNotification((String) m.getValue()
                                                                        , SettingFragment.editCostNoti
                                                                                (document.getString("cost_title"), edit_ori_cost
                                                                                        , edit_cost_currency.getText().toString()
                                                                                        , document.getString("trip_title")
                                                                                        , value.cost_adder));
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
            });

        }else {
            holder.remove_btn.setVisibility(View.INVISIBLE);
            holder.edit_btn.setVisibility(View.INVISIBLE);
        }


    }


    @Override
    public int getItemCount() {
        return costList.size();
    }

    private void savetoData(Map object, final String tripdocid, final String trip_code, final String cost_docid) {
        System.out.println(tripdocid+"C_doc_id "+cost_docid);
        DocumentReference removeCost = db.collection("costs").document(cost_docid);
        removeCost.delete();

        DocumentReference updateUsername = db.collection("trips").document(tripdocid);
        updateUsername
                .update("cf_members", FieldValue.arrayUnion(object))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("DocumentSnapshot successfully updated!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("ERROR updating document");
                    }
                });

        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String searchedTripCode = document.getString("trip_code");
                                if (searchedTripCode.equals(trip_code)) {
                                    cost_trip =document.getDouble("trip_cost");
                                    wallet =document.getDouble("trip_total");
                                    DocumentReference updatetrip = db.collection("trips").document(tripdocid);
                                    updatetrip.update("trip_cost",cost_trip-=result);
                                break;
                                }
                            }
                        }
                    }
                });
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

    private void calpercost(final double result,final int checkmoree) {
        System.out.println("1");
        List<String> check = new ArrayList<>() ;
        final Map<String, Object> object = new HashMap<>();
        final Map<String,Object> objectold = new HashMap<>();
        if (equalvalue.equals("No")){
                final Double perresult = Math.round(result/equal_list.size()* 100.0) / 100.0;
            db.collection("trips")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        String member_name = null ,checkmember = null,currency=null;
                        Double cal,ori_amount,trans_amount,amout_peruser = null;
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String valuecode = document.getString("trip_code");
                                    if (  trip_code.equals(valuecode) ) {
                                        ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                        for (Object s : cf_items) {
                                            HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                            for(Map.Entry m : searchedUsername.entrySet()) {
                                                if (Cost_MemberAdapter.selecmemberList.size()<=0){
                                                    for (String v  : equal_list){
                                                        if (v.equals(m.getValue()) && !m.getValue().equals(checkmember) ){
                                                            member_name = m.getValue().toString();
                                                            checkmember = m.getValue().toString();
                                                        }
                                                    }
                                                }else{
                                                    for (String v  : Cost_MemberAdapter.selecmemberList){
                                                        if (v.equals(m.getValue()) && !m.getValue().equals(checkmember) ){
                                                            member_name = m.getValue().toString();
                                                            checkmember = m.getValue().toString();
                                                        }
                                                    }
                                                }
                                                if (m.getValue().equals(member_name)){
                                                    member_name = m.getValue().toString();
                                                }
                                                if (m.getKey().equals("trans_amount")){
                                                    trans_amount = Double.parseDouble(m.getValue().toString());
                                                    cal = Double.parseDouble(m.getValue().toString());
                                                    if ( checkmoree == 1){
                                                        cal += perresult;
                                                    }else if (checkmoree == 0){
                                                        cal -= perresult;
                                                    }
                                                    amout_peruser = cal;
                                                }
                                                if (m.getKey().equals("ori_amount")){
                                                    ori_amount = Double.parseDouble(m.getValue().toString());
                                                }
                                                if (m.getKey().equals("currency")){
                                                    currency = m.getValue().toString();
                                                }}
                                            if (member_name != null && amout_peruser != null && ori_amount!=null && currency!=null){
                                                objectold.put("currency",currency);
                                                objectold.put("ori_amount",ori_amount );
                                                objectold.put("trans_amount", trans_amount);
                                                objectold.put("username", member_name);

                                                DocumentReference deleteRequest = db.collection("trips").document(trip_docid);
                                                deleteRequest.update("cf_members", FieldValue.arrayRemove(objectold));
                                                trans_amount = null;
                                                if (trans_amount==null){
                                                    amout_peruser = Math.round(amout_peruser * 100.0)/100.0;
                                                    object.put("ori_amount",amout_peruser );
                                                    object.put("currency",currency);
                                                    object.put("trans_amount", amout_peruser);
                                                    object.put("username", member_name);
                                                    System.out.println("1.5  "+member_name);
                                                    savetoData(object,member_name);
                                                    member_name = null;
                                                    amout_peruser = null;
                                                    ori_amount = null;
                                                    currency = null;

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
        }else if (equalvalue.equals("Yes")){
            db.collection("trips")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        String member_name = null ,checkmember = null,currency=null;
                        Double cal,ori_amount,trans_amount,amout_peruser = null;
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String valuecode = document.getString("trip_code");
                                    if (trip_code.equals(valuecode) ) {
                                        ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                        final Double peresult2 = Math.round(result/cf_items.size()* 100.0) / 100.0;
                                        for (Object s : cf_items) {
                                            HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                            for(Map.Entry m : searchedUsername.entrySet()) {
                                                if (m.getKey().equals("username")  && !m.getValue().equals(checkmember)){
                                                    member_name = m.getValue().toString();
                                                    checkmember = m.getValue().toString();
                                                }
                                                if (m.getKey().equals("trans_amount")){
                                                    trans_amount = Double.parseDouble(m.getValue().toString());
                                                    cal = Double.parseDouble(m.getValue().toString());
                                                    if ( checkmoree == 1){
                                                        cal += peresult2;
                                                    }else if (checkmoree == 0){
                                                        cal -= peresult2;
                                                    }
                                                    amout_peruser = cal;
                                                }
                                                if (m.getKey().equals("ori_amount")){
                                                    ori_amount = Double.parseDouble(m.getValue().toString());
                                                }
                                                if (m.getKey().equals("currency")){
                                                    currency = m.getValue().toString();
                                                }
                                                if (member_name != null && amout_peruser != null && ori_amount!=null && currency!=null){

                                                    objectold.put("currency",currency);
                                                    objectold.put("ori_amount",ori_amount );
                                                    objectold.put("trans_amount", trans_amount);
                                                    objectold.put("username", member_name);

                                                    DocumentReference deleteRequest = db.collection("trips").document(trip_docid);
                                                    deleteRequest.update("cf_members", FieldValue.arrayRemove(objectold));
                                                    trans_amount = null;
                                                    if (trans_amount==null){
                                                        amout_peruser = Math.round(amout_peruser * 100.0) / 100.0;
                                                        object.put("ori_amount",amout_peruser );
                                                        object.put("currency",currency);
                                                        object.put("trans_amount", amout_peruser);
                                                        object.put("username", member_name);
                                                        System.out.println("1.5  "+member_name);
                                                        savetoData(object,member_name);
                                                        member_name = null;
                                                        amout_peruser = null;
                                                        ori_amount = null;
                                                        currency = null;

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });}
    }



    private void savetoData(Map object,String username) {
        DocumentReference updateUsername = db.collection("trips").document(trip_docid);
        updateUsername
                .update("cf_members",FieldValue.arrayUnion(object))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("DocumentSnapshot successfully updated!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("ERROR updating document");
                        Toast.makeText(context, "Fail update doc..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void prepareCostsMembertData2 (final String trip_join_code) {
        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    String memberName = null , amountPerUser = null, checkMember = null;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String valueCode = document.getString("trip_code");
                                if (trip_join_code.equals(valueCode) ) {
                                    ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                    for (Object s : cf_items) {
                                        HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                        for(Map.Entry m : searchedUsername.entrySet()) {
                                            if (m.getKey().equals("trans_amount")){
                                                amountPerUser = m.getValue().toString();
                                            }
                                            for (String v  : equal_list){
                                                if (v.equals(m.getValue()) && !m.getValue().equals(checkMember) ){
                                                    memberName = m.getValue().toString();
                                                    checkMember = m.getValue().toString();
                                                }
                                            }
                                            if (memberName !=null && amountPerUser !=null ){
                                                findUser2(memberName, amountPerUser);
                                                amountPerUser =null;
                                                memberName =null;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void findUser2(final String usr, final String amountUser){
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String url_profile;
                String amount_user = amountUser;
                for (QueryDocumentSnapshot doc : task.getResult()){
                    String value = doc.getString("username");
                    if (value.equals(usr)){
                        url_profile =doc.getString("photo");
                        createMAdapter(url_profile,usr, amount_user);
                    }
                }
            }
        });

    }
    private void createMAdapter(String urlProfile, String usrName,String amout_peruser) {

        MemberModel trip = new MemberModel(usrName, urlProfile,amout_peruser);
        memberList.add(trip);
        cost_memberAdapter.notifyDataSetChanged();
    }



}

