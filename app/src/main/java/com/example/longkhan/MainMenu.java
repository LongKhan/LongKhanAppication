package com.example.longkhan;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainMenu extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    BottomNavigationView bottomNavigationView;
    Timestamp localTime = Timestamp.now();
    Date localDate = localTime.toDate();
    boolean isEmpty = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bottomNavigationView = findViewById(R.id.bottomNav);

        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()){
                                ArrayList<Map> find_cf_members = (ArrayList<Map>) document.get("cf_members");
                                for(Object cf : find_cf_members) {
                                    Map<String, Object> item = (Map<String, Object>) cf;
                                    System.out.println("jjj " + item.get("username").equals(LogIn.userName));
                                    if(item.get("username").equals(LogIn.userName)) {
                                        Timestamp find_start_date = (Timestamp) document.get("start_date");
                                        Timestamp find_end_date = (Timestamp) document.get("end_date");
                                        Date fsd_date = find_start_date.toDate();
                                        Date fed_date = find_end_date.toDate();
                                        SimpleDateFormat sdft = new SimpleDateFormat("yyyy:MM:dd");
                                        String str_start_date = sdft.format(fsd_date);
                                        String str_end_date = sdft.format(fed_date);
                                        String str_current_date = sdft.format(localDate);
                                        System.out.println("iii " + str_current_date + " "+ str_start_date + " " + str_end_date);
                                        if(str_current_date.equals(str_start_date)
                                                || str_current_date.equals(str_end_date)
                                                ||localDate.after(fsd_date) && localDate.before(fed_date)){
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainner, new RecentFragment()).commit();
                                            isEmpty = false;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
        if (savedInstanceState == null) {
            if(isEmpty == true) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainner, new SettingFragment()).commit();
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.trips:
                        fragment = new TripFagment();
                       break;
                    case R.id.recent:
                        fragment = new RecentFragment();
                        break;
                    case R.id.notification:
                        fragment = new NotificationFragment();
                        break;
                    case R.id.setting:
                        fragment = new SettingFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainner,fragment).commit();

                return true;
            }
        });
    }
}
