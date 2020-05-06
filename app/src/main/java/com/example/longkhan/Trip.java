package com.example.longkhan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Trip extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<MemberModel> memberList = new ArrayList<>();
    List<CostModel> costlist =new ArrayList<>();
    private MemberAdapter memberAdapter;
    CostAdapter costAdapter;
    EditText cost_title,cost_input, detail, edit_trip_title;
    String category,cost_docid,username,trip_docid, currency;
    static String trip_code;
    static double remaining_Total ;
    double cost,trip_total, result ;
    static double trip_cost ;
    TextView text_total, edit_trip_code ;
    ImageView image_bill, edit_image_trip, edit_image;
    static Timestamp start_date,end_date;
    StorageReference FolderStorange = FirebaseStorage.getInstance().getReference().child("Receipt_Cost");
    Timestamp localTime = Timestamp.now();
    Date localDate = localTime.toDate();
    Timestamp getdate ;
    String tripdocid, add_cost_currency, currency_trip;
    double amoutperuser=0.0,wallet, user_amount, aamount;
    ImageButton remove_btn;
    boolean check =false;
    //BarChart barChart;
    PieChart piechart;
    ArrayList<DataestModel> dataset = new ArrayList<>();
    ArrayList<String> labelNames;
    PieDataSet pieDataSet;
    ArrayList<PieEntry> pieEntryArrayList;
    PieData pieData;
    Spinner add_cost_currency_spinner;
    Button ok_edit_trip_btn;
    TextView edit_start_date, edit_end_date, edit_user_amount , edit_trip_curency;
    String edit_start_date_string, edit_end_date_string, old_trip_title, start_date_string, end_date_string;
    int checkimage = 0;
    String equalvalue;
    Cost_MemberAdapter cost_memberAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trip_deatail_fragment, container, false);

        username=LogIn.userName;

        final ImageView image_trip = v.findViewById(R.id.imageView_trip);
        final TextView Viewtrip_title = v.findViewById(R.id.textView_titlet);
        final TextView Viewtrip_code = v.findViewById(R.id.textView_code);
        final TextView Viewtrip_wallet = v.findViewById(R.id.remaining_cost);
        final TextView Viewtrip_cost = v.findViewById(R.id.cost_total);
        final Dialog dialogPopup = new Dialog(getContext());
        final TextView cost_currency = v.findViewById(R.id.textView_currency2);
        final TextView remaining_cost_currency = v.findViewById(R.id.textView_currency1);
        final TextView Viewtrip_start_date = v.findViewById(R.id.start_date);
        final TextView Viewtrip_end_date = v.findViewById(R.id.end_date);
        remove_btn =v.findViewById(R.id.cancel_request_btn);
        final ImageButton edit_trip = v.findViewById(R.id.editT_btn);
        ImageButton csv_btn = v.findViewById(R.id.csv_btn);
        piechart = v.findViewById(R.id.pie_chart);
        CardView cost_history_btn =v.findViewById(R.id.cost_history_btn);
        ImageButton add_cost_btn =v.findViewById(R.id.add_cost_btn2);
        CardView member_btn = v.findViewById(R.id.member_btn2);


        //prepareData
        RecentFragment.trip_code=null;
        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tripdocid =document.getString("trip_docid");
                                start_date = document.getTimestamp("start_date");
                                end_date = document.getTimestamp("end_date");
                                String valueuser = document.getString("collector");
                                Date str = start_date.toDate();
                                Date end = end_date.toDate();
                                    ArrayList<Map> wt_items = (ArrayList<Map>) document.get("wt_members");
                                    ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                    if (TripsAdapter.doctripid.equals(tripdocid)){
                                    if(wt_items.size() == 0 && cf_items.size() == 1){
                                        remove_btn.setVisibility(View.VISIBLE);
                                        edit_trip.setVisibility(View.VISIBLE);
                                    }
                                    for (Object s : cf_items) {
                                        HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                        for(Map.Entry m : searchedUsername.entrySet()) {
                                            if (m.getValue().equals(LogIn.userName)){
                                                check =true;
                                                String url = document.getString("trip_profile");
                                                //Glide.with(getContext()).load(url).override(300,300).error(R.drawable.user).into(image_trip);
                                                trip_docid = document.getString("trip_docid");
                                                trip_total = document.getDouble("trip_total");
                                                trip_cost = document.getDouble("trip_cost");
                                                currency_trip = document.getString("currency");

                                                remaining_Total = trip_total - trip_cost;
                                                remaining_Total = Math.round(remaining_Total * 100.0) / 100.0;
                                                Viewtrip_wallet.setText(String.valueOf(remaining_Total));
                                                trip_cost = Math.round(trip_cost * 100.0) / 100.0;
                                                Viewtrip_cost.setText(String.valueOf(trip_cost));
                                                Glide.with(getContext()).load(document.getString("trip_profile")).override(300,300).error(R.drawable.team_image).into(image_trip);
                                                Viewtrip_title.setText(document.getString("trip_title"));
                                                trip_code = document.getString("trip_code");
                                                cost_currency.setText(document.getString("currency"));
                                                remaining_cost_currency.setText(document.getString("currency"));
                                                setdatachart(trip_code);
                                                Viewtrip_code.setText(trip_code);


                                                start_date = document.getTimestamp("start_date");
                                                end_date = document.getTimestamp("end_date");

                                                Date ts_to_start_date = start_date.toDate();
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                                String new_format_start_date = sdf.format(ts_to_start_date);
                                                int start_year = Integer.parseInt(new_format_start_date.substring(0,4));
                                                int start_month = Integer.parseInt(new_format_start_date.substring(5,7));
                                                int start_date_sub = Integer.parseInt(new_format_start_date.substring(8,10));
                                                String [] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                                                String ss = month[start_month-1] + " " + start_date_sub + ",  " + start_year;
                                                Viewtrip_start_date.setText(ss);
                                                System.out.println("aa " + start_year + " " + start_month + " " + start_date_sub);

                                                Date ts_to_end_date = end_date.toDate();
                                                String new_format_end_date = sdf.format(ts_to_end_date);
                                                int end_year = Integer.parseInt(new_format_end_date.substring(0,4));
                                                int end_month = Integer.parseInt(new_format_end_date.substring(5,7));
                                                int end_date_sub = Integer.parseInt(new_format_end_date.substring(8,10));
                                                ss = month[end_month-1] + " " + end_date_sub + ",  " + end_year;
                                                Viewtrip_end_date.setText(ss);
                                                System.out.println("aa " + end_year + " " + end_month + " " + end_date_sub);

                                                if(LogIn.userName.equals(document.getString("collector"))) {
                                                    edit_trip.setVisibility(View.VISIBLE);
                                                }
                                                break;}

                                        }
                                    }
                            }}
                        }
                    }
                });

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogDeleteTrip=new AlertDialog.Builder(getContext());
                dialogDeleteTrip.setMessage("Confirm delete trip");
                dialogDeleteTrip.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("trips").document(trip_docid)
                                .delete();
                        getActivity().recreate();
                    }
                });
                dialogDeleteTrip.show();
            }
        });

        edit_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPopup.setContentView(R.layout.edit_trip_popup);
                dialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialogPopup.show();

                edit_trip_title = dialogPopup.findViewById(R.id.edit_trip_title);
                edit_image_trip = dialogPopup.findViewById(R.id.edit_image_trip);
                edit_image = dialogPopup.findViewById(R.id.edit_image);
                edit_user_amount = dialogPopup.findViewById(R.id.edit_user_amount);
                ok_edit_trip_btn = dialogPopup.findViewById(R.id.ok_edit_trip_btn);
                edit_trip_code = dialogPopup.findViewById(R.id.edit_trip_code);
                edit_start_date = dialogPopup.findViewById(R.id.edit_start_date);
                edit_end_date = dialogPopup.findViewById(R.id.edit_end_date);
                edit_trip_curency = dialogPopup.findViewById(R.id.edit_trip_currency);

                //find_collector_amount
                DocumentReference find_user_amount = db.collection("trips").document(trip_docid);
                find_user_amount.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    ArrayList find_cf_members = (ArrayList) document.get("cf_members");
                                    for(Object item : find_cf_members){
                                        Map<String, Object> find_user_amount = (Map<String, Object>) item;
                                        if(find_user_amount.containsValue(document.getString("collector"))) {
                                            user_amount = Double.parseDouble(find_user_amount.get("ori_amount").toString());
                                            edit_user_amount.setText(user_amount+"");
                                            edit_trip_curency.setText(document.getString("currency"));
                                            edit_start_date_string = document.get("start_date").toString();
                                            edit_end_date_string = document.get("end_date").toString();
                                            start_date = document.getTimestamp("start_date");
                                            end_date = document.getTimestamp("end_date");

                                            Date ts_to_start_date = start_date.toDate();
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            String new_format_start_date = sdf.format(ts_to_start_date);
                                            int edit_start_year = Integer.parseInt(new_format_start_date.substring(0,4));
                                            int edit_start_month = Integer.parseInt(new_format_start_date.substring(5,7));
                                            int edit_start_date_sub = Integer.parseInt(new_format_start_date.substring(8,10));
                                            String [] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                                            String str = month[edit_start_month-1] + " " + edit_start_date_sub + ",  " + edit_start_year;
                                            edit_start_date.setText(str);
                                            System.out.println("aa " + edit_start_year + " " + edit_start_month + " " + edit_start_date_sub);

                                            Date ts_to_end_date = end_date.toDate();
                                            String new_format_end_date = sdf.format(ts_to_end_date);
                                            int edit_end_year = Integer.parseInt(new_format_end_date.substring(0,4));
                                            int edit_end_month = Integer.parseInt(new_format_end_date.substring(5,7));
                                            int edit_end_date_sub = Integer.parseInt(new_format_end_date.substring(8,10));
                                            str = month[edit_end_month-1] + " " + edit_end_date_sub + ",  " + edit_end_year;
                                            edit_end_date.setText(str);
                                            System.out.println("aa " + edit_end_year + " " + edit_end_month + " " + edit_end_date_sub);

                                            Glide.with(getContext()).load(document.getString("trip_profile")).override(300,300).error(R.drawable.team_image).into(edit_image_trip);

                                            break;
                                        }
                                    }
                                }
                            }
                        });

                edit_trip_title.setText(Viewtrip_title.getText().toString());
                edit_trip_code.setText(Viewtrip_code.getText().toString());

                dialogPopup.findViewById(R.id.edit_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        checkimage =2;
                        startActivityForResult(intent, 1);
                    }
                });


                ok_edit_trip_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateTrip();
                        dialogPopup.dismiss();
                        getActivity().recreate();
                    }
                });
            }});

        cost_history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPopup.setContentView(R.layout.cost_history_popup);
                dialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialogPopup.show();

                final LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                RecyclerView recycler_view_cost = dialogPopup.findViewById(R.id.recycler_view_historycosts);
                costAdapter = new CostAdapter(getContext(),costlist,getFragmentManager());
                layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                recycler_view_cost.setLayoutManager(layoutManager1);
                recycler_view_cost.setItemAnimator(new DefaultItemAnimator());
                recycler_view_cost.setAdapter(costAdapter);
                prepareCoststData(trip_code);
                costlist.clear();
            }
        });

        add_cost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPopup.setContentView(R.layout.add_cost_popup);
                dialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialogPopup.show();

                cost_title =dialogPopup.findViewById(R.id.cost_title_input);
                cost_input = dialogPopup.findViewById(R.id.cost_text);
                image_bill = dialogPopup.findViewById(R.id.receipt_image);
                image_bill.setImageResource(R.drawable.bill);
                detail =dialogPopup.findViewById(R.id.cost_details);

                final RecyclerView recycler_view_cost = dialogPopup.findViewById(R.id.recycler_view_member);
                cost_memberAdapter = new Cost_MemberAdapter(getContext(),memberList);
                final LinearLayoutManager layoutManage = new LinearLayoutManager(getActivity());
                recycler_view_cost.setVisibility(View.INVISIBLE);

                final Spinner spinner_equal = dialogPopup.findViewById(R.id.spinner_equal);
                ArrayAdapter<CharSequence> adap = ArrayAdapter.createFromResource(getContext(),R.array.Equal,android.R.layout.simple_spinner_item);
                adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_equal.setAdapter(adap);
                spinner_equal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        equalvalue = spinner_equal.getSelectedItem().toString();
                        if (equalvalue.equals("No")){
                            recycler_view_cost.setVisibility(View.VISIBLE);
                            layoutManage.setOrientation(LinearLayoutManager.HORIZONTAL);
                            recycler_view_cost.setLayoutManager(layoutManage);
                            recycler_view_cost.setItemAnimator(new DefaultItemAnimator());
                            recycler_view_cost.setAdapter(cost_memberAdapter);
                            prepareCostsMembertData2(trip_code);
                            memberList.clear();
                        }
                        else if (equalvalue.equals("Yes")){
                            recycler_view_cost.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


                final Spinner spinner_category = dialogPopup.findViewById(R.id.category_spinner);
                final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.Category,android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_category.setAdapter(adapter);
                spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        category = spinner_category.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                add_cost_currency_spinner = dialogPopup.findViewById(R.id.add_cost_currency_spinner);
                ArrayAdapter<CharSequence> add_cost_currency_adapter = ArrayAdapter.createFromResource(getContext(), R.array.Currency, android.R.layout.simple_spinner_item);
                add_cost_currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                add_cost_currency_spinner.setAdapter(add_cost_currency_adapter);
                add_cost_currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        add_cost_currency = add_cost_currency_spinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        add_cost_currency ="THB";
                    }
                });
                dialogPopup.findViewById(R.id.btn_add_Image_Bill).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        checkimage = 1;
                        startActivityForResult(intent,1);
                    } });
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());

                dialogPopup.findViewById(R.id.ok_add_trip_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DatePicker date = dialogPopup.findViewById(R.id.date_cost);
                        final int Day = date.getDayOfMonth();
                        final int Month = date.getMonth();
                        final int Year = date.getYear()-1900;
                        getdate = new Timestamp(new Date(Year,Month,Day));
                        if (cost_title.getText().length() != 0){
                            if (cost_input.getText().toString().length() >= 1 &&
                                    Double.parseDouble(cost_input.getText().toString()) > 0){
                                db.collection("costs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        boolean check = true;
                                        if(task.isSuccessful()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String searchedTripCode = document.getString("cost_title");
                                                String value = document.getString("trip_code");
                                                if (cost_title.getText().toString().equals(searchedTripCode) && trip_code.equals(value)) {
                                                    alertdialog.setMessage("Duplicate Cost Title !!")
                                                            .create()
                                                            .show();
                                                    check = false;
                                                    break;
                                                }
                                            }
                                            if(getdate.compareTo(start_date) >= 0 && getdate.toDate().before(end_date.toDate()) ) {
                                                /** currency **/
                                                String url = "http://data.fixer.io/api/latest?access_key=75eebbc51210e28b820dd55e3ca714b3";
                                                OkHttpClient client = new OkHttpClient();
                                                final Request request = new Request.Builder()
                                                        .url(url)
                                                        .build();
                                                final boolean finalCheck = check;
                                                client.newCall(request).enqueue(new Callback() {
                                                    @Override
                                                    public void onFailure(Request request, IOException e) {
                                                    }
                                                    @Override
                                                    public void onResponse(Response response) throws IOException {
                                                        String mMessage = response.body().string();
                                                        try {
                                                            JSONObject onj = new JSONObject(mMessage);
                                                            JSONObject b = onj.getJSONObject("rates");
                                                            System.out.println("cuu " + currency_trip);
                                                            result = Double.parseDouble(b.getString(currency_trip))
                                                                    / Double.parseDouble(b.getString(add_cost_currency_spinner.getSelectedItem().toString()))
                                                                    * Double.parseDouble(cost_input.getText().toString());
                                                            result = Math.round(result * 100.0) / 100.0;
                                                            if (finalCheck == true) {
                                                                if (remaining_Total-result >= 0){
                                                                    uploadDatatoServer();
                                                                    db.collection("trips")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                                                                            String searchedTripCode = document.getString("trip_code");
                                                                                            if(searchedTripCode.equals(trip_code)){
                                                                                                ArrayList<Map> searched_cf_members = (ArrayList<Map>) document.get("cf_members");
                                                                                                if(searched_cf_members != null) {
                                                                                                    for(Object cf : searched_cf_members) {
                                                                                                        HashMap<String, String> searchedUsername = (HashMap<String, String>) cf;
                                                                                                        for(Map.Entry m : searchedUsername.entrySet()){
                                                                                                            if(m.getKey().equals("username")){
                                                                                                                createNotification((String) m.getValue(), SettingFragment.addCostNoti(cost_title.getText().toString()
                                                                                                                        , Double.parseDouble(cost_input.getText().toString()), add_cost_currency_spinner.getSelectedItem().toString()
                                                                                                                        , document.getString("trip_title"), LogIn.userName));
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
                                                                    dialogPopup.dismiss();
                                                                }else {
                                                                    dialogPopup.show();
                                                                    alertdialog.setMessage("We cost more than remaining amount.")
                                                                            .create()
                                                                            .show();
                                                                }
                                                            }


                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                            }else {
                                                dialogPopup.show();
                                                alertdialog.setMessage("Date Invalid.")
                                                        .create()
                                                        .show();
                                            }

                                        }//task.isSuccessful


                                    }
                                });

                            }else {alertdialog.setMessage("Please fill in cost.")
                                    .create()
                                    .show();
                            }
                        }else{alertdialog.setMessage("Please fill in in cost title.")
                                .create()
                                .show();
                        }
                    }
                });
            }
        });

        member_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPopup.setContentView(R.layout.cost_history_popup);
                dialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialogPopup.show();

                RecyclerView recycler_view = dialogPopup.findViewById(R.id.recycler_view_historycosts);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                memberAdapter = new MemberAdapter(getContext(),memberList);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycler_view.setLayoutManager(layoutManager);
                recycler_view.setItemAnimator(new DefaultItemAnimator());
                recycler_view.setAdapter(memberAdapter);
                prepareMemberData();
                memberList.clear();

            }
        });

        csv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.AlertDialog.Builder dialogSendEmail=new AlertDialog.Builder(getContext());
                dialogSendEmail.setMessage("Do you want to export trip csv file ?");
                dialogSendEmail.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getActivity(), SendEmail.class);
                        startActivity(intent);
                    }
                });
                dialogSendEmail.show();
            }
        });


        return v;
    }

    String pattern = "dd/MM/yyyy";
    DateFormat df = new SimpleDateFormat(pattern);
    int Food =0;
    int Transport=0;
    int Accommodation=0;
    int Appliances=0;
    int Etc=0;

    private void setdatachart(final String codejoin) {
        db.collection("costs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean check = true;
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String value = document.getString("trip_code");
                        Timestamp date = document.getTimestamp("date");
                        Date datecon = date.toDate();
                        String todate = df.format(datecon);
                        if (codejoin.equals(value)) {
                            String valuecategory = document.getString("category");
                            Double valuecost = document.getDouble("ori_cost");
                            int intcost = (int) Math.round(valuecost);
                            switch (valuecategory){
                                case "Food":
                                    Food += intcost;
                                    break;
                                case "Transport":
                                    Transport += intcost;
                                    break;
                                case "Accommodation":
                                    Accommodation += intcost;
                                    break;
                                case "Appliances":
                                    Appliances += intcost;
                                    break;
                                case "Etc":
                                    Etc += intcost;
                                    break;
                            }
                            makeChart();
                        }
                    }

                }//task.isSuccessful
            }
        });
    }


    @SuppressLint("WrongConstant")
    private void makeChart() {
        pieEntryArrayList = new ArrayList<>();
        labelNames = new ArrayList<>();
//        labelNames.add("Food");
//        labelNames.add("Transport");
//        labelNames.add("Accommodation");
//        labelNames.add("Appliances");
//        labelNames.add("Etc");
        //makebar
        if (Food>0){
            pieEntryArrayList.add(new PieEntry(Food, "Food"));
        }
        if (Transport>0){
            pieEntryArrayList.add(new PieEntry(Transport, "Transport"));
        }
        if (Accommodation>0){
            pieEntryArrayList.add(new PieEntry(Accommodation, "Accommodation"));
        }
        if(Appliances>0){
            pieEntryArrayList.add(new PieEntry(Appliances, "Appliances"));
        }
        if (Etc>0){
            pieEntryArrayList.add(new PieEntry(Etc, "Etc"));
        }

        pieDataSet = new PieDataSet(pieEntryArrayList,"");
// pieDataSet.setSelectionShift(10);
        pieDataSet.setValueTextColors(Collections.singletonList(Color.parseColor("#253135")));
        piechart.setUsePercentValues(true);
        piechart.getCenterTextRadiusPercent();
        pieDataSet.setColors(Color.parseColor("#CAA87B"),Color.parseColor("#847B5C"),Color.parseColor("#EDA924"),Color.parseColor("#58774B"),Color.parseColor("#803A38"));
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        pieDataSet.setValueFormatter(new DefaultValueFormatter(2));
        pieData =new PieData(pieDataSet);
        Typeface b = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);
        pieDataSet.setValueTypeface(b);
        pieData.setValueTypeface(b);
        pieData.setValueTextSize(15);
        piechart.setCenterTextTypeface(b);
        piechart.setData(pieData);
        piechart.setHoleRadius(25);
        piechart.setTransparentCircleRadius(25);
        piechart.setCenterText("Total Cost");
        piechart.setCenterTextColor(R.color.Black);
        piechart.setCenterTextSize(20);
//piechart.animateY(3000, Easing.EaseOutBounce);
        piechart.invalidate();
        piechart.setEntryLabelTextSize(15);
        piechart.setEntryLabelColor(R.color.dark_grey);


    }


    Uri filepath;
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == getActivity().RESULT_OK && reqCode == 1) {
            if (checkimage == 1){
                image_bill.setImageURI(data.getData());
                filepath = data.getData();
            }else if (checkimage == 2){
                Glide.with(getContext()).load(data.getData()).override(300,300).error(R.drawable.team_image).into(edit_image_trip);
                //edit_image_trip.setImageURI(data.getData());
                filepath = data.getData();
            }
        }

        else {
            Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void uploadDatatoServer() {
        if (filepath != null){
            // Defining the child of storageReference
            final StorageReference Imagename = FolderStorange.child("image"+filepath.getLastPathSegment());
            Imagename.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
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
                                JSONObject b = onj.getJSONObject("rates");
                                System.out.println("cuu " + currency_trip);
                                result = Double.parseDouble(b.getString(currency_trip))
                                        / Double.parseDouble(b.getString(add_cost_currency_spinner.getSelectedItem().toString()))
                                        * Double.parseDouble(cost_input.getText().toString());
                                result = Math.round(result * 100.0) / 100.0;
                                cost = Math.round(Double.parseDouble(cost_input.getText().toString()) * 100.0) / 100.0;
                                Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String, Object> cost_object = new HashMap<>();
                                        cost_object.put("cost_adder", username);
                                        cost_object.put("ori_cost", cost);
                                        cost_object.put("trans_cost", result);
                                        cost_object.put("cost_title", cost_title.getText().toString());
                                        cost_object.put("trip_code", trip_code);
                                        cost_object.put("date", getdate);
                                        cost_object.put("receipt",String.valueOf(uri));
                                        cost_object.put("cost_docid",null);
                                        cost_object.put("details", detail.getText().toString());
                                        cost_object.put("category", category);
                                        cost_object.put("currency", add_cost_currency_spinner.getSelectedItem().toString());
                                        cost_object.put("note",Cost_MemberAdapter.selecmemberList);

                                        db.collection("costs")
                                                .add(cost_object)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(final DocumentReference documentReference) {
                                                        cost_docid = documentReference.getId();
                                                        updatedoc(trip_cost+=result,cost_docid);
                                                        calpercost(result);
                                                    }
                                                });
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }); }else if (filepath == null){
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
                public void onResponse(final Response response) throws IOException {
                    final String mMessage = response.body().string();
                    db.collection("trips")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String searchedTripCode = document.getString("trip_code");
                                            if (searchedTripCode.equals(trip_code)) {
                                                currency = document.getString("currency");
                                                try {
                                                    JSONObject onj = new JSONObject(mMessage);
                                                    JSONObject b = onj.getJSONObject("rates");
                                                    System.out.println("cuu " + currency_trip);
                                                    result = Double.parseDouble(b.getString(currency_trip))
                                                            / Double.parseDouble(b.getString(add_cost_currency_spinner.getSelectedItem().toString()))
                                                            * Double.parseDouble(cost_input.getText().toString());
                                                    result = Math.round(result * 100.0) / 100.0;
                                                    cost = Math.round(Double.parseDouble(cost_input.getText().toString()) * 100.0) / 100.0;
                                                    Map<String, Object> cost_object2 = new HashMap<>();
                                                    cost_object2.put("cost_adder", LogIn.userName);
                                                    cost_object2.put("ori_cost", cost);
                                                    cost_object2.put("trans_cost", result);
                                                    cost_object2.put("cost_title", cost_title.getText().toString());
                                                    cost_object2.put("trip_code", trip_code);
                                                    cost_object2.put("date", getdate);
                                                    cost_object2.put("receipt", null);
                                                    cost_object2.put("cost_docid", null);
                                                    cost_object2.put("details", detail.getText().toString());
                                                    cost_object2.put("category", category);
                                                    cost_object2.put("currency", add_cost_currency_spinner.getSelectedItem().toString());
                                                    cost_object2.put("note",Cost_MemberAdapter.selecmemberList);

                                                    db.collection("costs")
                                                            .add(cost_object2)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(final DocumentReference documentReference) {
                                                                    cost_docid = documentReference.getId();
                                                                    updatedoc(trip_cost+=result,cost_docid);
                                                                    calpercost(result);
                                                                }
                                                            });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                }
            });}
    }


    private void updatedoc(final Double trip_cost, final String cost_docid) {
        db.collection("costs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String searchedJoincode = document.getString("cost_title");
                                if (cost_title.getText().toString().equals(searchedJoincode)) {
                                    DocumentReference updateUsername = db.collection("costs").document(cost_docid);
                                    updateUsername
                                            .update("cost_docid", cost_docid);
                                    DocumentReference updatetrip = db.collection("trips").document(trip_docid);
                                    updatetrip
                                            .update("trip_cost", trip_cost);
                                    break;
                                }
                            }
                        }
                    }
                });

    }
    private void calpercost(final double result) {
        System.out.println("1");
        List<String> check = new ArrayList<>() ;
        final Map<String, Object> object = new HashMap<>();
        final Map<String,Object> objectold = new HashMap<>();
        if (equalvalue.equals("No")){
            final Double perresult = result/Cost_MemberAdapter.selecmemberList.size();
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
                                    if ( trip_code.equals(valuecode) ) {
                                        ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                        for (Object s : cf_items) {
                                            HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                            for(Map.Entry m : searchedUsername.entrySet()) {
                                                for (String v  : Cost_MemberAdapter.selecmemberList){
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
                                                    cal -= perresult;
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
                                    if ( trip_code.equals(valuecode) ) {
                                        ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                        final Double peresult2 = result/cf_items.size();
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
                                                    cal -= peresult2;
                                                    amout_peruser = cal;
                                                    amout_peruser = Math.round(amout_peruser * 100.0) / 100.0;
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
                        startActivity(getActivity().getIntent());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("ERROR updating document");
                        Toast.makeText(getContext(), "Fail update doc..", Toast.LENGTH_SHORT).show();
                        startActivity(getActivity().getIntent());
                    }
                });
    }


    private void prepareCoststData(final String joincode) {
        db.collection("costs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String cost_name_Data = null;
                        String cost_Data = null;
                        String costDoc_Data = null;
                        String cost_adder = null;
                        String receipt =null;
                        String detail = null;
                        ArrayList <String> note ;
                        for (QueryDocumentSnapshot document : task.getResult()){
                            String value = document.getString("trip_code");
                            System.out.println(joincode +" Cost"+ value);
                            if (value.equals(joincode)) {
                                cost_name_Data = document.getString("cost_title");
                                cost_Data = String.valueOf(document.getDouble("trans_cost"));
                                costDoc_Data =document.getString("cost_docid");
                                cost_adder = document.getString("cost_adder");
                                receipt =document.getString("receipt");
                                detail = document.getString("details");
                                ArrayList<String> cf_items = (ArrayList<String>) document.get("note");
                                note = new ArrayList<>();
                                if (cf_items.size()!=0){
                                    for (int j = 0; j < cf_items.size(); j++){
                                        note.add(cf_items.get(j));
                                    }
                                }else{   note.add(" ");}
                                createCostAdater(cost_name_Data,cost_Data,costDoc_Data,cost_adder,receipt,detail,note,trip_code,trip_docid, currency_trip);
                            }
                        }

                    }
                });
    }
    private void createCostAdater(String cost_name, String this_cost, String costDoc, String cost_adder,String receipt,String detail,ArrayList note,String trip_code,String trip_docid, String trip_currency) {
        CostModel request = new CostModel(cost_name, this_cost, costDoc,cost_adder,receipt,detail,note,trip_code,trip_docid, trip_currency);
        costlist.add(request);
        costAdapter.notifyDataSetChanged();
    }


    private void prepareMemberData() {
        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    String member_name = null , amout_peruser = null,checkmember = null, usr_currency = null;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String value =document.getString("trip_docid");
                                if ( TripsAdapter.doctripid.equals(value) ) {
                                    ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                    for (Object s : cf_items) {
                                        HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                        for(Map.Entry m : searchedUsername.entrySet()) {
                                            if (m.getKey().equals("trans_amount")){
                                                amout_peruser = m.getValue().toString();
                                            }
                                            if(m.getKey().equals("username") && !m.getValue().equals(checkmember)) {
                                                member_name = m.getValue().toString();
                                                checkmember = m.getValue().toString();
                                            }
                                            if (member_name!=null && amout_peruser!=null ){
                                                usr_currency = document.getString("currency");
                                                finduser(member_name,amout_peruser, usr_currency);
                                                amout_peruser=null;
                                                member_name =null;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void finduser(final String usr, final String amountusr, final String usr_currency){
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String url_profile;
                String amount_usr = amountusr;
                for (QueryDocumentSnapshot doc : task.getResult()){
                    String value = doc.getString("username");
                    if (value.equals(usr)){
                        System.out.println("amount "+amount_usr);
                        url_profile =doc.getString("photo");
                        createAdapter(url_profile,usr, amount_usr, usr_currency);
                    }
                }
            }
        });

    }
    private void createAdapter(String urlProfile, String tripName,String amout_peruser, String usr_currency) {

        MemberModel trip = new MemberModel(tripName, urlProfile,amout_peruser, usr_currency);
        memberList.add(trip);
        memberAdapter.notifyDataSetChanged();
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

    public void updateTrip() {
        db.collection("trips").document(trip_docid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            old_trip_title = task.getResult().getString("trip_title");
                            if(filepath != null) {
                                final StorageReference Imagename = FolderStorange.child("image" + filepath.getLastPathSegment());
                                Imagename.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                            @Override
                                            public void onSuccess(Uri uri) {
                                                if (edit_trip_title != null) {
                                                    DocumentReference updateTrip = db.collection("trips").document(trip_docid);
                                                    updateTrip.update("trip_title", edit_trip_title.getText().toString());
                                                    updateTrip.update("trip_profile", String.valueOf(uri));

                                                    db.collection("trips")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if(task.isSuccessful()){
                                                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                                                            String searchedTripCode = document.getString("trip_code");
                                                                            if(searchedTripCode.equals(trip_code)){
                                                                                ArrayList<Map> searched_cf_members = (ArrayList<Map>) document.get("cf_members");
                                                                                if(searched_cf_members != null) {
                                                                                    for(Object cf : searched_cf_members) {
                                                                                        HashMap<String, String> searchedUsername = (HashMap<String, String>) cf;
                                                                                        for(Map.Entry m : searchedUsername.entrySet()){
                                                                                            if(m.getKey().equals("username")){
                                                                                                createNotification(m.getValue().toString(), SettingFragment.titleNoti(LogIn.userName, old_trip_title, edit_trip_title.getText().toString()));
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
                                                    DocumentReference updateTrip = db.collection("trips").document(trip_docid);
                                                    updateTrip.update("trip_profile", String.valueOf(uri));
                                                }
                                                System.out.println("tt " + trip_docid);
                                            }
                                        });

                                    }
                                });
                            }
                            else {
                                if (edit_trip_title != null) {
                                    DocumentReference updateTrip = db.collection("trips").document(trip_docid);
                                    updateTrip.update("trip_title", edit_trip_title.getText().toString());

                                    db.collection("trips")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                                            String searchedTripCode = document.getString("trip_code");
                                                            if(searchedTripCode.equals(trip_code)){
                                                                ArrayList<Map> searched_cf_members = (ArrayList<Map>) document.get("cf_members");
                                                                if(searched_cf_members != null) {
                                                                    for(Object cf : searched_cf_members) {
                                                                        HashMap<String, String> searchedUsername = (HashMap<String, String>) cf;
                                                                        for(Map.Entry m : searchedUsername.entrySet()){
                                                                            if(m.getKey().equals("username")){
                                                                                createNotification(m.getValue().toString(), SettingFragment.titleNoti(LogIn.userName, old_trip_title, edit_trip_title.getText().toString()));
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
                                System.out.println("tt " + trip_docid);
                            }
                        }
                    }
                });
    }

    private void prepareCostsMembertData2(final String trip_join_code) {
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
                                            if(m.getKey().equals("username") && !m.getValue().equals(checkMember)) {
                                                memberName = m.getValue().toString();
                                                checkMember = m.getValue().toString();
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
                        createMAdapter(url_profile,usr, amount_user, currency);
                    }
                }
            }
        });

    }
    private void createMAdapter(String urlProfile, String usrName,String amout_peruser, String usr_currency) {

        MemberModel trip = new MemberModel(usrName, urlProfile,amout_peruser, usr_currency);
        memberList.add(trip);
        cost_memberAdapter.notifyDataSetChanged();
    }
}
