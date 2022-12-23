package com.example.reaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reaste.Adapter.EditAdapter;
import com.example.reaste.Model.Images;
import com.example.reaste.Model.Posts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PosteditActivity extends AppCompatActivity {
    private TextInputLayout title,owner,location,amenities,price,bedrooms,units;
    private AutoCompleteTextView type;
    private RecyclerView recyclerView;
    private TextView save;
    private ImageView back;

    List<String> imagelinks;
    EditAdapter editAdapter;

    ProgressDialog dialog;

    String postid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postedit);
        dialog=new ProgressDialog(this);
        dialog.setMessage("Updating house details...");
        dialog.setCanceledOnTouchOutside(false);

        title=findViewById(R.id.edit_title);
        owner=findViewById(R.id.edit_agency);
        location=findViewById(R.id.edit_location);
        amenities=findViewById(R.id.edit_amenities);
        price=findViewById(R.id.edit_price);
        bedrooms=findViewById(R.id.edit_bedrooms);
        units=findViewById(R.id.edit_units);
        type=findViewById(R.id.edit_type);
        back=findViewById(R.id.close_editpost);
        save=findViewById(R.id.save_editpost);
        recyclerView=findViewById(R.id.post_images);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(PosteditActivity.this,2));
        imagelinks=new ArrayList<>();

        ArrayList<String> choice=getChoice();
        ArrayAdapter<String> adapter=new ArrayAdapter<>(PosteditActivity.this,R.layout.support_simple_spinner_dropdown_item,choice);
        type.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        postid=intent.getStringExtra("postid");

        FirebaseDatabase.getInstance().getReference("Posts").child(postid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Posts posts=snapshot.getValue(Posts.class);
                        title.getEditText().setText(posts.getTitle());
                        owner.getEditText().setText(posts.getAgency());
                        location.getEditText().setText(posts.getLocation());
                        price.getEditText().setText(posts.getPrice());
                        bedrooms.getEditText().setText(posts.getBedrooms());
                        units.getEditText().setText(posts.getUnits());
                        amenities.getEditText().setText(posts.getAmenities());
                        type.setText(posts.getType());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        DocumentReference reference= FirebaseFirestore.getInstance().collection("Images")
                .document(postid);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Images images=documentSnapshot.toObject(Images.class);
                for (String links:images.getImagelinks()){
                    imagelinks.add(links);
                }
                editAdapter=new EditAdapter(PosteditActivity.this,imagelinks);
                recyclerView.setAdapter(editAdapter);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String house_name=title.getEditText().getText().toString();
                String house_owner=owner.getEditText().getText().toString();
                String house_bedrooms=bedrooms.getEditText().getText().toString();
                String house_price=price.getEditText().getText().toString();
                String house_units=units.getEditText().getText().toString();
                String house_location=location.getEditText().getText().toString();
                String house_type=type.getText().toString();
                String house_amenities=amenities.getEditText().getText().toString();
                if (TextUtils.isEmpty(house_name)){
                    title.setError("Please provide a name for the property");
                }else if (TextUtils.isEmpty(house_owner)){
                    owner.setError("Please provide a name for the owner or agency");
                }else if (TextUtils.isEmpty(house_bedrooms)){
                    bedrooms.setError("Please provide the number of bedrooms");
                }else if (TextUtils.isEmpty(house_price)){
                    price.setError("Please provide the price for the property");
                }else if (TextUtils.isEmpty(house_units)){
                    units.setError("Please provide number of units available");
                }else if (TextUtils.isEmpty(house_location)){
                    location.setError("Please provide a location for the property");
                }else if (TextUtils.isEmpty(house_type)) {
                    type.setError("Field cannot be empty");
                }else if (TextUtils.isEmpty(house_amenities)){
                    amenities.setError("Field cannot be empty");
                }else {
                    updateDetails(house_name,house_owner,house_bedrooms,house_location,house_price,house_type,house_units,house_amenities);
                }

            }
        });
    }

    private void updateDetails(String house_name, String house_owner, String house_bedrooms, String house_location, String house_price, String house_type, String house_units, String house_amenities) {
        dialog.show();
        HashMap<String,Object> map=new HashMap<>();
        map.put("title",house_name);
        map.put("agency",house_owner);
        map.put("location",house_location);
        map.put("price",house_price);
        map.put("type",house_type);
        map.put("bedrooms",house_bedrooms);
        map.put("units",house_units);
        map.put("amenities",house_amenities);

        FirebaseDatabase.getInstance().getReference("Posts").child(postid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(PosteditActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(PosteditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<String> getChoice() {
        ArrayList<String> choices=new ArrayList<>();
        choices.add("Rent");
        choices.add("Sale");
        return choices;
    }
}