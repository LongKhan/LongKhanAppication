package com.example.longkhan;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.squareup.picasso.Picasso;
import com.google.android.gms.tasks.Task;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class Profile extends AppCompatActivity {
    static String username;
    String TAG = "ProfileActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageButton add_btn = findViewById(R.id.add_btn);
        final EditText username_txt = findViewById(R.id.username_txt);
        final ImageView user_profile = findViewById(R.id.image_trip);
        final TextView textView = findViewById(R.id.textView);
        textView.setTextColor(getColor(R.color.light_cream));
        username_txt.setTextColor(getColor(R.color.dark_grey));

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String searchedEmail = document.getString("gmail");
                                if(LogIn.personEmail.equals(searchedEmail)){
                                    Picasso.get().load(document.getString("photo"))
                                            .resize(500,500)
                                            .centerInside().into(user_profile);
                                    break;
                                }
                            }
                        }
                    }
                });

        add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = username_txt.getText().toString();
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                boolean checkUsername = true;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String searchedUsername = document.getString("username");
                                        if (username.equals(searchedUsername)) {
                                            AlertDialog.Builder dupUsernameDialog = new AlertDialog.Builder(Profile.this);
                                            dupUsernameDialog.setMessage("Duplicate username !")
                                                    .create()
                                                    .show();
                                            checkUsername = false;
                                            break;
                                        }

                                    }
                                    if(checkUsername == true){
                                        if (username.length() < 6 || username.length() > 8 ){
                                            AlertDialog.Builder lenUsernameDialog = new AlertDialog.Builder(Profile.this);
                                            lenUsernameDialog.setMessage("Username cannot be less than 6 characters and more than 8 characters.")
                                                    .create()
                                                    .show();
                                        }
                                        else {
                                            DocumentReference updateUsername = db.collection("users").document(LogIn.docID);
                                            updateUsername
                                                    .update("username", username)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                            Intent intent = new Intent(Profile.this, MainMenu.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "ERROR updating document", e);
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }
}