package com.example.reaste;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reaste.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {
    TextInputLayout name,phone;
    MaterialTextView email;
    TextView change,save;
    CircleImageView profile;
    ImageView close;
    FirebaseUser fUser;
    public static final int PICK_IMAGE=2;

    StorageTask upload;
    StorageReference ref;
    Uri uri;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        ref=FirebaseStorage.getInstance().getReference("Profile");
        dialog=new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCanceledOnTouchOutside(false);

        name=findViewById(R.id.name_edit);
        phone=findViewById(R.id.phone_edit);
        email=findViewById(R.id.email_edit);
        change=findViewById(R.id.change_photo);
        save=findViewById(R.id.save_edit);
        profile=findViewById(R.id.profile_edit);
        close=findViewById(R.id.close_edit);

        FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                name.getEditText().setText(users.getName());
                phone.getEditText().setText(users.getPhone());
                email.setText(new StringBuilder().append("Email: ").append(users.getEmail()));

                if (users.getProfileimage().equals("default")){
                    profile.setImageResource(R.drawable.profileplaceholder);
                }else {
                    Picasso.get().load(users.getProfileimage()).into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_txt=name.getEditText().getText().toString();
                String phone_txt=phone.getEditText().getText().toString();
                if (TextUtils.isEmpty(name_txt)){
                    name.setError("Please provide name");
                }else if (TextUtils.isEmpty(phone_txt)){
                    phone.setError("Please provide contact information");
                }else{
                    updateProfile(name_txt,phone_txt);
                }
            }
        });
    }

    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    private void updateProfile(String name_txt, String phone_txt) {
        dialog.show();
        HashMap<String,Object> map=new HashMap<>();
        map.put("name",name_txt);
        map.put("phone",phone_txt);

        FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.dismiss();
                Toast.makeText(EditActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri=data.getData();

            if (upload != null && upload.isInProgress()){
                Toast.makeText(EditActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }

        }
    }
    private String getFileExtension(Uri uri1){
        ContentResolver contentResolver=this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri1));
    }

    private void uploadImage() {
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("uploading image...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (uri != null){
            StorageReference reference= ref.child("images"+uri.getLastPathSegment());
            upload=reference.putFile(uri);
            upload.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri imageuri= (Uri) task.getResult();
                        String url=imageuri.toString();

                        HashMap<String,Object> profilemap=new HashMap<>();
                        profilemap.put("profileimage",url);

                        FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid()).updateChildren(profilemap);
                        dialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(EditActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}