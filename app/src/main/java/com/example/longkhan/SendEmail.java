package com.example.longkhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendEmail extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String pattern = "dd/MM/yyyy";
    DateFormat df = new SimpleDateFormat(pattern);
    String trip_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_transparent);
        // final Button export = findViewById(R.id.csv_btn);
//        Button btn_home = findViewById(R.id.btn_home);

        if (RecentFragment.trip_code!=null){
            trip_code = RecentFragment.trip_code;
            System.out.println("Recent");
        }else if (Trip.trip_code!=null){
            trip_code = Trip.trip_code;
            System.out.println("Trip");
        }


        final StringBuilder data = new StringBuilder();
        data.append("Username,Remaining_Amount");
        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    String member_name = null, checkmember = null, currency = null;
                    Double trans_amount = null;
                    final Map<String, Object> object = new HashMap<>();

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String valuecode = document.getString("trip_code");
                                if (trip_code.equals(valuecode)) {
                                    ArrayList<Map> cf_items = (ArrayList<Map>) document.get("cf_members");
                                    for (Object s : cf_items) {
                                        HashMap<String, Object> searchedUsername = (HashMap<String, Object>) s;
                                        for (Map.Entry m : searchedUsername.entrySet()) {
                                            if (m.getKey().equals("username") && !m.getValue().equals(checkmember)) {
                                                member_name = m.getValue().toString();
                                                checkmember = m.getValue().toString();
                                            }
                                            if (m.getKey().equals("trans_amount")) {
                                                trans_amount = Double.parseDouble(m.getValue().toString());
                                            }
                                        }
                                        if (member_name != null && trans_amount != null) {
                                            data.append("\n" + member_name + "," + trans_amount);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
        db.collection("costs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        data.append("\n" + "Date,CostTitle,Cost_Adder,Cost,Currency,Category,Details,Equal");
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String valuecode = document.getString("trip_code");
                                if (trip_code.equals(valuecode)) {
                                    Timestamp date = document.getTimestamp("date");
                                    Date datecon = date.toDate();
                                    String todate = df.format(datecon);
                                    String cost_title = document.getString("cost_title");
                                    String cost_adder = document.getString("cost_adder");
                                    String trans_cost = document.getDouble("trans_cost").toString();
                                    String category = document.getString("category");
                                    String currency = document.getString("currency");
                                    String details = document.getString("details");
                                    ArrayList<String> cf_items = (ArrayList<String>) document.get("note");
                                    data.append("\n" + todate + "," + cost_title + "," + cost_adder + "," + trans_cost + "," + currency + "," + category + "," + details + "," + cf_items);

                                    try {
                                        //saving the file into device
                                        FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
                                        out.write((data.toString()).getBytes());
                                        out.close();
                                        //exporting
                                        Context context = getApplicationContext();
                                        File filelocation = new File(getFilesDir(), "data.csv");
                                        Uri path = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", filelocation);
                                        Intent fileIntent = new Intent(Intent.ACTION_SEND);
                                        fileIntent.setPackage("com.google.android.apps.docs");
                                        fileIntent.setType("text/csv");
                                        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
                                        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                                        startActivity(Intent.createChooser(fileIntent, "Send mail"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
    }
}