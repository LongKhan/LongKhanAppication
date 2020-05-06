package com.example.longkhan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SettingFragment extends Fragment {
    String Email, add_trip_currency, join_trip_currency, currency;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView trip_code, trip_title_input, amount_input;
    double aamount;
    double result;
    BigDecimal fix_result;
    Timestamp start_timestamp, end_timestamp;
    ImageView trip_profile;
    StorageReference FolderStorange = FirebaseStorage.getInstance().getReference().child("Profile_trip");
    String docTripID;
    TextView join_code, end_date_label, start_date_label;
    Spinner add_trip_currency_spinner;
    Spinner join_trip_currency_spinner;
    DatePicker start_date, end_date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.setting_fragment, container, false);

        Button sign_out_btn = v.findViewById(R.id.signout_btn);
        final TextView username = v.findViewById(R.id.username_text);
        final TextView email = v.findViewById(R.id.email_text);
        final ImageView user_profile = v.findViewById(R.id.image_user);
        final Dialog dialog = new Dialog(getContext());
        CardView create_trip = v.findViewById(R.id.create_trip_btn);
        CardView join_trip_btn = v.findViewById(R.id.join_trip_btn);


        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String searchedEmail = document.getString("gmail");
                                if (LogIn.personEmail.equals(searchedEmail)) {
                                   Glide.with(getContext()).load(document.getString("photo")).override(500, 500).error(R.drawable.user_large).into(user_profile);
                                    username.setText(document.getString("username"));
                                    email.setText(document.getString("gmail"));
                                    Email = document.getString("gmail");
                                    break;
                                }
                            }
                        }
                    }
                });

        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        create_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.create_trip_popup);
                trip_code = dialog.findViewById(R.id.trip_code);
                trip_title_input = dialog.findViewById(R.id.trip_title);
                amount_input = dialog.findViewById(R.id.user_amount);
                trip_profile = dialog.findViewById(R.id.image_trip);
                trip_code.setText(getRandomString(6));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();

                add_trip_currency_spinner = dialog.findViewById(R.id.add_trip_currency_spinner);
                ArrayAdapter<CharSequence> add_trip_currency_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Currency, android.R.layout.simple_spinner_item);
                add_trip_currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                add_trip_currency_spinner.setAdapter(add_trip_currency_adapter);
                add_trip_currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        add_trip_currency = add_trip_currency_spinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        add_trip_currency = "THB";
                    }
                });


                dialog.findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {//add_profile
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    }
                });

                final AlertDialog.Builder tripTitleDialog = new AlertDialog.Builder(getContext());
                dialog.findViewById(R.id.ok_add_trip_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        start_date = dialog.findViewById(R.id.start_date);
                        end_date = dialog.findViewById(R.id.cost_list_add);
                        final int startDay = start_date.getDayOfMonth();
                        final int startMonth = start_date.getMonth();
                        final int startYear = start_date.getYear() - 1900;
                        final int endDay = end_date.getDayOfMonth();
                        final int endMonth = end_date.getMonth();
                        final int endYear = end_date.getYear() - 1900;
                        System.out.println(startYear + " " + endYear);
                        start_timestamp = new Timestamp(new Date(startYear, startMonth, startDay));
                        end_timestamp = new Timestamp(new Date(endYear, endMonth, endDay));

                        add_trip_currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                add_trip_currency = add_trip_currency_spinner.getSelectedItem().toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                add_trip_currency = "THB";
                            }
                        });

                        if (trip_title_input.getText().length() != 0) {
                            if (amount_input.getText().toString().length() >= 1 &&
                                    Double.parseDouble(amount_input.getText().toString()) > 0) {
                                db.collection("trips")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                boolean checkDupCode = true;
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String searchedTripCode = document.getString("trip_code");
                                                        if (trip_code.getText().toString().equals(searchedTripCode)) {
                                                            AlertDialog.Builder dupTripCodeDialog = new AlertDialog.Builder(getContext(), R.color.red_indian);
                                                            dupTripCodeDialog.setMessage("Duplicate trip code !")
                                                                    .create()
                                                                    .show();
                                                            checkDupCode = false;
                                                            break;
                                                        }
                                                    }
                                                    if (end_timestamp.compareTo(start_timestamp) > 0) {
                                                        aamount = Double.parseDouble(amount_input.getText().toString());
                                                        if (checkDupCode == true) {
                                                            uploadDatatoServer();
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                    else if(end_timestamp.compareTo(start_timestamp) == 0) {
                                                        aamount = Double.parseDouble(amount_input.getText().toString());
                                                        if (checkDupCode == true) {
                                                            uploadDatatoServer();
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                    else {
                                                        dialog.show();
                                                        tripTitleDialog.setMessage("Incorrect Date.")
                                                                .create()
                                                                .show();
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                AlertDialog.Builder dialogAmount = new AlertDialog.Builder(getContext());
                                dialogAmount.setMessage("Please fill in amount.")
                                        .create()
                                        .show();
                            }
                        } else {
                            dialog.show();
                            tripTitleDialog.setMessage("Please fill in trip title.")
                                    .create()
                                    .show();
                        }
                    }
                });

                dialog.findViewById(R.id.random_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        trip_code.setText(getRandomString(6));
                    }
                });

            }


        });
        join_trip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.join_trip_popup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
                join_code = dialog.findViewById(R.id.join_code);
                amount_input = dialog.findViewById(R.id.user_amount);
                join_trip_currency_spinner = dialog.findViewById(R.id.join_trip_currency_spinner);
                ArrayAdapter<CharSequence> join_trip_currency_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Currency, android.R.layout.simple_spinner_item);
                join_trip_currency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                join_trip_currency_spinner.setAdapter(join_trip_currency_adapter);

                dialog.findViewById(R.id.confirm_join_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if (join_code.getText().toString().length() > 0) {
                            db.collection("trips")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            boolean addedCheck = false;
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String searchedTripCode = document.getString("trip_code");
                                                    if (searchedTripCode.equals(join_code.getText().toString())) {
                                                        if(username.getText().toString().equals(document.getString("collector"))){
                                                            addedCheck = true;
                                                            AlertDialog.Builder dupJointrip = new AlertDialog.Builder(getContext());
                                                            dupJointrip.setMessage("You are collector this trip, so you cannot add this trip code.")
                                                                    .create()
                                                                    .show();
                                                        }
                                                        else {
                                                        ArrayList<Map> searchedMember = (ArrayList<Map>) document.get("wt_members");
                                                        if (searchedMember != null) {
                                                            for (Object s : searchedMember) {
                                                                if (s.toString().contains(username.getText().toString())) {
                                                                    HashMap<String, String> searchedUsername = (HashMap<String, String>) s;
                                                                    for (Map.Entry m : searchedUsername.entrySet()) {
                                                                        if (m.getValue().equals(username.getText().toString())) {
                                                                            addedCheck = true;
                                                                            AlertDialog.Builder dupJointrip = new AlertDialog.Builder(getContext());
                                                                            dupJointrip.setMessage("You added this trip code.")
                                                                                    .create()
                                                                                    .show();
                                                                            break;
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }}
                                                        break;
                                                    }
                                                }
                                            }
                                            if (addedCheck == false) {
                                                if (amount_input.getText().toString().length() >= 1 &&
                                                        Double.parseDouble(amount_input.getText().toString()) > 0) {
                                                    aamount = Double.parseDouble(amount_input.getText().toString());
                                                    aamount = Math.round(aamount * 100.0) / 100.0;
                                                    join_trip_currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                            join_trip_currency = join_trip_currency_spinner.getSelectedItem().toString();
                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                                            join_trip_currency = "THB";
                                                        }
                                                    });
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
                                                            final String mMessage = response.body().string();
                                                            db.collection("trips")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            final Map<String, Object> trip = new HashMap<>();
                                                                            boolean findCode = false;
                                                                            if(task.isSuccessful()) {
                                                                                for(QueryDocumentSnapshot document : task.getResult()) {
                                                                                    String searchedTripCode = document.getString("trip_code");
                                                                                    String valuedocid = document.getString("trip_docid");
                                                                                    if(searchedTripCode.equals(join_code.getText().toString())){
                                                                                        currency = document.getString("currency");
                                                                                        try {
                                                                                            JSONObject onj = new JSONObject(mMessage);
                                                                                            JSONObject b = onj.getJSONObject("rates");
                                                                                            System.out.println("cuu " + currency);
                                                                                            result = Double.parseDouble(b.getString(currency))
                                                                                                    / Double.parseDouble(b.getString(join_trip_currency_spinner.getSelectedItem().toString()))
                                                                                                    * aamount;
                                                                                            result = Math.round(result * 100.0) / 100.0;
                                                                                            trip.put("ori_amount", result);
                                                                                            trip.put("trans_amount", result);
                                                                                            trip.put("username", LogIn.userName);
                                                                                            trip.put("currency", join_trip_currency_spinner.getSelectedItem().toString());
                                                                                            String doc_trip_id = document.getId();
                                                                                            db.collection("trips").document(doc_trip_id)
                                                                                                    .update("wt_members", FieldValue.arrayUnion(trip));
                                                                                            dialog.dismiss();
                                                                                            findCode = true;
                                                                                            createNotification(document.getString("collector"),
                                                                                                    reqTripNoti(LogIn.userName, document.getString("trip_title"), aamount, join_trip_currency_spinner.getSelectedItem().toString()));
                                                                                        } catch (JSONException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            } if (findCode == false) {
                                                                                AlertDialog.Builder dialogAmount = new AlertDialog.Builder(getContext());
                                                                                dialogAmount.setMessage("Join code invalid")
                                                                                        .create()
                                                                                        .show();
                                                                            }
                                                                        }
                                                                    });
                                                        }});}
                                                else {
                                                    AlertDialog.Builder dialogAmount = new AlertDialog.Builder(getContext());
                                                    dialogAmount.setMessage("Please fill in amount")
                                                            .create()
                                                            .show();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            AlertDialog.Builder dialogAmount = new AlertDialog.Builder(getContext());
                            dialogAmount.setMessage("Please fill in join code")
                                    .create()
                                    .show();
                        }

                    }
                });


            }
        });

        return v;
    }

    Uri filepath;

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == getActivity().RESULT_OK && reqCode == 1) {
            trip_profile.setImageURI(data.getData());
            filepath = data.getData();
        }
    }

    public void uploadDatatoServer() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = baos.toByteArray();

        if (filepath != null) {
            // Defining the child of storageReference
            final StorageReference Imagename = FolderStorange.child("image" + filepath.getLastPathSegment());
            Imagename.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Uploaded image to data", Toast.LENGTH_SHORT).show();
                    Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            if (end_timestamp.compareTo(start_timestamp) >= 0) {
                                aamount = Math.round(aamount * 100.0) / 100.0;
                                Map<String, Object> trip = new HashMap<>();
                                Map<String, Object> member = new HashMap<>();
                                member.put("username", LogIn.userName);
                                member.put("trans_amount", aamount);
                                member.put("ori_amount", aamount);
                                member.put("currency", add_trip_currency_spinner.getSelectedItem().toString());
                                trip.put("trip_title", trip_title_input.getText().toString());
                                trip.put("trip_code", trip_code.getText().toString());
                                trip.put("start_date", start_timestamp);
                                trip.put("end_date", end_timestamp);
                                trip.put("trip_profile", String.valueOf(uri));
                                trip.put("cf_members", FieldValue.arrayUnion(member));
                                trip.put("trip_docid", null);
                                trip.put("collector", LogIn.userName);
                                trip.put("wt_members", FieldValue.arrayUnion());
                                trip.put("trip_cost", 0);
                                trip.put("trip_total", aamount);
                                trip.put("currency", add_trip_currency_spinner.getSelectedItem().toString());
                                trip.put("wt_walletTrip",FieldValue.arrayUnion());

                                db.collection("trips")
                                        .add(trip)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(final DocumentReference documentReference) {
                                                docTripID = documentReference.getId();
                                                db.collection("trips")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        String searchedJoincode = document.getString("trip_code");
                                                                        if (trip_code.getText().toString().equals(searchedJoincode)) {
                                                                            DocumentReference updateUsername = db.collection("trips").document(docTripID);
                                                                            updateUsername
                                                                                    .update("trip_docid", docTripID)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Toast.makeText(getContext(), "Doc successfully updated!", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(getContext(), "Fail update doc..", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("failed");
                                            }
                                        });

                            }
                        }
                    });
                }
            });
        } else {
            if (end_timestamp.compareTo(start_timestamp) >= 0) {
                aamount = Math.round(aamount * 100.0) / 100.0;
                Map<String, Object> trip = new HashMap<>();
                Map<String, Object> member = new HashMap<>();
                member.put("username", LogIn.userName);
                member.put("trans_amount", aamount);
                member.put("ori_amount", aamount);
                member.put("currency", add_trip_currency_spinner.getSelectedItem().toString());
                trip.put("trip_title", trip_title_input.getText().toString());
                trip.put("trip_code", trip_code.getText().toString());
                trip.put("start_date", start_timestamp);
                trip.put("end_date", end_timestamp);
                trip.put("trip_profile", " ");
                trip.put("cf_members", FieldValue.arrayUnion(member));
                trip.put("trip_docid", null);
                trip.put("collector", LogIn.userName);
                trip.put("wt_members", FieldValue.arrayUnion());
                trip.put("trip_cost", 0);
                trip.put("trip_total", aamount);
                trip.put("currency", add_trip_currency_spinner.getSelectedItem().toString());
                trip.put("wt_walletTrip",FieldValue.arrayUnion());

                db.collection("trips")
                        .add(trip)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(final DocumentReference documentReference) {
                                docTripID = documentReference.getId();
                                db.collection("trips")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String searchedJoincode = document.getString("trip_code");
                                                        if (trip_code.getText().toString().equals(searchedJoincode)) {
                                                            DocumentReference updateUsername = db.collection("trips").document(docTripID);
                                                            updateUsername
                                                                    .update("trip_docid", docTripID)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                        }
                                                                    });
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        });

                            }
                        });
            }
        }
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getContext(), Email + " Signed Out Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LogIn.class);
                        startActivity(intent);
                    }
                });
    }

    public static String getRandomString(int i) {
        final String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        while (i > 0) {
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i--;
        }
        return result.toString();
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

    public static String reqTripNoti(String username, String tripTitle, double amount, String currency) {
        String txt;
        txt = username + " wants to join " + tripTitle + " with " + amount + " " + currency + ".";
        return txt;
    }

    public static String cfTripNoti(String username, String tripTitle) {
        String txt;
        txt = username + " joined " + tripTitle + ".";
        return txt;
    }

    public static String addCostNoti(String costTitle, double amount,  String currency, String tripTitle, String username) {
        String txt;
        txt = costTitle + " " + amount + " " + currency + " in " + tripTitle + " by " + username +".";
        return txt;
    }

    public static String editCostNoti(String costTitle, double amount,  String currency, String tripTitle, String username) {
        String txt;
        txt = "Edit " + costTitle + " " + amount + " " + currency + " in " + tripTitle + " by " + username +".";
        return txt;
    }

    public static String delCostNoti(String costTitle, String tripTitle, String username) {
        String txt;
        txt = "Delete " + costTitle + " " + " in " + tripTitle + " by " + username +".";
        return txt;
    }

    public static String reqAmountNoti(String username, double amount, String currency, String tripTitle) {
        String txt;
        txt = username + " add " + amount + currency + " in " + tripTitle + ".";
        return txt;
    }

    public static String cfAmountNoti(String username, double amount, String currency, String tripTitle) {
        String txt;
        txt = username + amount + currency + " in " + tripTitle + ".";
        return txt;
    }

    public static String titleNoti(String username, String oldTripTitle, String newTripTitle) {
        String txt;
        txt = username + " changed " + newTripTitle + " from " + oldTripTitle + ".";
        return txt;
    }
}
