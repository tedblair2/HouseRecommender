package com.example.reaste.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reaste.Adapter.AddSlider;
import com.example.reaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class AddFragment extends Fragment {
    private TextView post;
    ImageView close,notavailable;
    private TextInputLayout title,name,location,bedrooms,price,units,amenities,type_txt;
    private AutoCompleteTextView type;
    public static final int PICK_IMAGE=2;
    ArrayList<Uri> images;
    ProgressDialog progress;
    ViewPager imageselected;
    AddSlider slider;
    LinearLayout sliderbar,layout;
    private int dotscount;
    private ImageView[] dots;

    DatabaseReference ref;
    FirebaseUser fUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add, container, false);

        ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                , PackageManager.PERMISSION_GRANTED);
        chooseImages();

        Toolbar toolbar=view.findViewById(R.id.toolbar_add);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Post a House");

        ref= FirebaseDatabase.getInstance().getReference();
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        progress=new ProgressDialog(getContext());
        progress.setMessage("Uploading...");

        type_txt=view.findViewById(R.id.type_house);
        post=view.findViewById(R.id.post);
        title=view.findViewById(R.id.house_title);
        name=view.findViewById(R.id.house_agency);
        location=view.findViewById(R.id.house_location);
        bedrooms=view.findViewById(R.id.house_bedrooms);
        price=view.findViewById(R.id.house_price);
        units=view.findViewById(R.id.house_units);
        type=view.findViewById(R.id.house_type);
        amenities=view.findViewById(R.id.house_amenities);
        close=view.findViewById(R.id.close);
        notavailable=view.findViewById(R.id.notavailable);
        layout=view.findViewById(R.id.layout_linear);
        imageselected=view.findViewById(R.id.imageselected);
        sliderbar=view.findViewById(R.id.slidebar_add);
        images=new ArrayList<>();

        ArrayList<String> choice=getChoice();
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,choice);
        type.setAdapter(adapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.getEditText().setText("");
                name.getEditText().setText("");
                bedrooms.getEditText().setText("");
                price.getEditText().setText("");
                units.getEditText().setText("");
                location.getEditText().setText("");
                amenities.getEditText().setText("");
                layout.setVisibility(View.GONE);
                notavailable.setVisibility(View.VISIBLE);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String house_name=title.getEditText().getText().toString();
                String house_owner=name.getEditText().getText().toString();
                String house_bedrooms=bedrooms.getEditText().getText().toString();
                String house_price=price.getEditText().getText().toString();
                String house_units=units.getEditText().getText().toString();
                String house_location=location.getEditText().getText().toString();
                String house_type=type.getText().toString();
                String house_amenities=amenities.getEditText().getText().toString();
                if (TextUtils.isEmpty(house_name)){
                    title.setError("Please provide a name for the property");
                }else if (TextUtils.isEmpty(house_owner)){
                    name.setError("Please provide a name for the owner or agency");
                }else if (TextUtils.isEmpty(house_bedrooms)){
                    bedrooms.setError("Please provide the number of bedrooms");
                }else if (TextUtils.isEmpty(house_price)){
                    price.setError("Please provide the price for the property");
                }else if (TextUtils.isEmpty(house_units)){
                    units.setError("Please provide number of units available");
                }else if (TextUtils.isEmpty(house_location)){
                    location.setError("Please provide a location for the property");
                }else if (TextUtils.isEmpty(house_type)) {
                    type_txt.setError("Field cannot be empty");
                }else if (TextUtils.isEmpty(house_amenities)){
                    amenities.setError("Field cannot be empty");
                }else {
                    upload(images,house_name,house_owner,house_bedrooms,house_location,house_price,house_type,house_units,house_amenities);
                }
            }
        });

        return view;
    }

    private void chooseImages() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    private ArrayList<String> getChoice() {
        ArrayList<String> choice1=new ArrayList<>();
        choice1.add("Rent");
        choice1.add("Sale");
        return choice1;
    }

    private void upload(ArrayList<Uri> images, String house_name, String house_owner, String house_bedrooms, String house_location, String house_price, String house_type, String house_units, String house_amenities) {
        progress.show();
        String postid=ref.push().getKey();
        HashMap<String,Object> map=new HashMap<>();
        map.put("title",house_name);
        map.put("agency",house_owner);
        map.put("location",house_location);
        map.put("price",house_price);
        map.put("type",house_type);
        map.put("bedrooms",house_bedrooms);
        map.put("units",house_units);
        map.put("postid",postid);
        map.put("publisherid",fUser.getUid());
        map.put("amenities",house_amenities);

        assert postid != null;
        ref.child("Posts").child(postid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                title.getEditText().setText("");
                name.getEditText().setText("");
                bedrooms.getEditText().setText("");
                price.getEditText().setText("");
                units.getEditText().setText("");
                location.getEditText().setText("");
                amenities.getEditText().setText("");
                type.setText("");
                StorageReference storageReference= FirebaseStorage.getInstance().getReference("Images");
                int size= images.size();
                ArrayList<String> imagelinks=new ArrayList<>(size);
                for (int i=0;i<images.size();i++){
                    Uri individualimage=images.get(i);
                    final StorageReference refName=storageReference.child("images"+System.currentTimeMillis());

                    refName.putFile(individualimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            refName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url=String.valueOf(uri);
                                    imagelinks.add(url);
                                    storelink(imagelinks,postid);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void storelink(ArrayList<String> imagelinks, String postid) {
        DocumentReference documentReference=FirebaseFirestore.getInstance().collection("Images")
                .document(postid);
        HashMap<String,Object> map2=new HashMap<>();
        map2.put("imagelinks",imagelinks);
        documentReference.set(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progress.dismiss();
                    layout.setVisibility(View.GONE);
                    notavailable.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getClipData() != null){
            ClipData clipData=data.getClipData();

            for (int i=0;i<clipData.getItemCount();i++){
                ClipData.Item item=clipData.getItemAt(i);
                Uri uri=item.getUri();
                images.add(uri);
            }
            showImages(images);
        }else if (data.getData() != null){
            Toast.makeText(getContext(), "Please choose multiple images", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "No pictures were selected", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showImages(ArrayList<Uri> images) {
        slider=new AddSlider(images,getContext());
        imageselected.setAdapter(slider);
        dotscount= slider.getCount();
        dots=new ImageView[dotscount];
        for (int i=0;i<dotscount;i++){
            dots[i]=new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.nonactive_dot));
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5,0,5,0);

            sliderbar.addView(dots[i],params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.active_dot));
        imageselected.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i=0;i<dotscount;i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}