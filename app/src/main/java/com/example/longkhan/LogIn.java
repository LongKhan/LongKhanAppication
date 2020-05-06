package com.example.longkhan;

import java.util.Map;
import android.util.Log;
import java.util.HashMap;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.iid.FirebaseInstanceId;

public class LogIn extends AppCompatActivity {
    static String docID,userName,personEmail,personPhoto;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    private SignInButton sign_in_btn;
    private  String TAG = "MainActivity";
    static GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        mAuth = FirebaseAuth.getInstance();

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.sign_in_btn:
                        signIn();
                        break;
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(LogIn.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(LogIn.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LogIn.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);

                    } else {
                        Toast.makeText(LogIn.this, "Failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                }
                }
            });
        } else {
            Toast.makeText(LogIn.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUI(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            personEmail = account.getEmail();
            personPhoto = account.getPhotoUrl().toString();
            searchUser();
        }
    }
    public void searchUser( ) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean isFind = false;
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String value = document.getString("gmail");
                                    if(personEmail.equals(value)){
                                        userName = document.getString("username");
                                        isFind = true;
                                        break;
                                    }
                                }
                                if(isFind == true){
                                    db.collection("users")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                                            String searchedUser = document.getString("username");
                                                            if(searchedUser.equals(LogIn.userName)) {
                                                                String path = document.getId();
                                                                userName = searchedUser;
                                                                db.collection("users")
                                                                        .document(path)
                                                                        .update("token", FirebaseInstanceId.getInstance().getToken());
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                    startActivity(new Intent(LogIn.this, MainMenu.class));
                                }
                                else{
                                    // Create a new user with email photo username
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("gmail", personEmail);
                                    user.put("photo", personPhoto);
                                    user.put("username",null);
                                    user.put("token", FirebaseInstanceId.getInstance().getToken());
                                    // Add a new document with a generated ID
                                    db.collection("users")
                                            .add(user)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                    Toast.makeText(LogIn.this, "Yeah  ", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(LogIn.this, Profile.class);
                                                    startActivity(intent);
                                                    docID = documentReference.getId();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                    Toast.makeText(LogIn.this, "Cry", Toast.LENGTH_SHORT).show();
                                                }});} } }
                });

    }
}
