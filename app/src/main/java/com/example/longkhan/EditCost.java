package com.example.longkhan;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditCost extends Fragment {
    EditText editText_cost_title,editText_details;
    TextView editText_cost;
    ImageView imageView_bill;
    Timestamp getDay;
    Double cost;
    String category, edit_cost_currency;
    String trip_code;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference FolderStorange = FirebaseStorage.getInstance().getReference().child("Receipt_Cost");
    Spinner edit_cost_currency_spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_cost_popup, container, false);

        final Spinner spinner_category = v.findViewById(R.id.edit_category_spinner);
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

        editText_cost = v.findViewById(R.id.editText_cost);
        editText_cost_title = v.findViewById(R.id.editText_cost_title);
        editText_details = v.findViewById(R.id.editText_details);
        imageView_bill = v.findViewById(R.id.image_bill_view);
        //ImageView btn_add_Bill = v.findViewById(R.id.btn_add_Bill);
        final Dialog dialog = new Dialog(getContext());
        Button ok_edit =v.findViewById(R.id.ok_edit);

        //Picasso.get().load(CostAdapter.url).resize(50, 50).centerInside().into(imageView_bill);

        System.out.println(CostAdapter.cost+" "+CostAdapter.detail);
        Glide.with(getContext()).load(CostAdapter.url).override(300,300).error(R.drawable.bill).into(imageView_bill);
        editText_cost_title.setText(CostAdapter.title);
        editText_cost.setText(CostAdapter.cost);
        editText_details.setText(CostAdapter.detail);


//        btn_add_Bill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent,1); } });

        ok_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());
                if (editText_cost_title.getText().length() != 0){
                    if (editText_cost.getText().toString().length() >= 1 &&
                            Double.parseDouble(editText_cost.getText().toString()) > 0){
                        db.collection("costs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                boolean check = true;
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String searchedTripCode = document.getString("cost_title");
                                        if (editText_cost_title.getText().toString().equals(searchedTripCode)) {
                                            alertdialog.setMessage("Duplicate Cost Title !!")
                                                    .create()
                                                    .show();
                                            check = false;
                                            break;
                                        }
                                    }
                                    if(getDay.compareTo(RecentFragment.start_date) >= 0) {
                                        cost = Double.parseDouble(editText_cost.getText().toString());
                                        if (check == true) {
                                            if (RecentFragment.remaining_Total-cost >= 0){
                                                uploadDatatoServer();
                                                Fragment fragment = new RecentFragment();
                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.fragmentcontainner, fragment)
                                                        .addToBackStack(null)
                                                        .commit();

                                            }else {
                                                dialog.show();
                                                alertdialog.setMessage("We cost more than remaining amount.")
                                                        .create()
                                                        .show();
                                            }
                                        }
                                    }else {
                                        dialog.show();
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
                }else{alertdialog.setMessage("Please fill in cost title.")
                        .create()
                        .show();
                }

            }
        });
        return v;
    }

    Uri filepath;
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == getActivity().RESULT_OK && reqCode == 1) {
            imageView_bill.setImageURI(data.getData());
            filepath = data.getData();
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
                    Toast.makeText(getContext(), "Uploaded image to data", Toast.LENGTH_SHORT).show();
                    Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            final Map<String, Object> cost_object = new HashMap<>();
                            cost_object.put("cost_adder", LogIn.userName);
                            cost_object.put("cost", cost);
                            cost_object.put("cost_title", editText_cost_title.getText().toString());
                            cost_object.put("trip_code", RecentFragment.trip_code);
                            cost_object.put("date", getDay);
                            cost_object.put("receipt",String.valueOf(uri));
                            cost_object.put("cost_docid",null);
                            cost_object.put("details", editText_details.getText().toString());
                            cost_object.put("category", category);
                            cost_object.put("currency", edit_cost_currency_spinner.getSelectedItem().toString());

                            db.collection("costs")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String searchedJoincode = document.getString("cost_title");
                                                    if (editText_cost_title.getText().toString().equals(searchedJoincode)) {
                                                        DocumentReference updateUsername = db.collection("costs").document(CostAdapter.cost_docid);
                                                        updateUsername
                                                                .update(cost_object);
                                                        RecentFragment.trip_cost +=cost;
                                                        DocumentReference updatetrip = db.collection("trips").document(CostAdapter.cost_docid);
                                                        updatetrip
                                                                .update("trip_cost", RecentFragment.trip_cost);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    });

                        }
                    });
                }
            }); }else if (filepath == null){
            final Map<String, Object> cost_object2 = new HashMap<>();
            cost_object2.put("cost_adder", LogIn.userName);
            cost_object2.put("cost", cost);
            cost_object2.put("cost_title", editText_cost_title.getText().toString());
            cost_object2.put("trip_code", RecentFragment.trip_code);
            cost_object2.put("date", getDay);
            cost_object2.put("receipt",null);
            cost_object2.put("cost_docid",null);
            cost_object2.put("details", editText_details.getText().toString());
            cost_object2.put("category", category);
            cost_object2.put("currency", edit_cost_currency_spinner.getSelectedItem().toString());

            db.collection("costs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String searchedJoincode = document.getString("cost_title");
                                    if (editText_cost_title.getText().toString().equals(searchedJoincode)) {
                                        DocumentReference updateUsername = db.collection("costs").document(CostAdapter.cost_docid);
                                        updateUsername
                                                .update(cost_object2);
                                        RecentFragment.trip_cost +=cost;
                                        DocumentReference updatetrip = db.collection("trips").document(CostAdapter.cost_docid);
                                        updatetrip
                                                .update("trip_cost", RecentFragment.trip_cost);
                                        break;
                                    }
                                }
                            }
                        }
                    });
        }
    }
}
