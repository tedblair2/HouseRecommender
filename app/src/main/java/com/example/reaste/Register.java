package com.example.reaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    TextInputLayout name,email,phone,password,confirm;
    Button register;
    TextView login;
    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog prog;

    FirebaseAuth auth;
    DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        prog=new ProgressDialog(this);
        prog.setTitle("Registration");
        prog.setMessage("Loading...");
        auth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance().getReference();
        name=findViewById(R.id.fullname);
        email=findViewById(R.id.email_reg);
        phone=findViewById(R.id.phonenumber);
        password=findViewById(R.id.password_reg);
        confirm=findViewById(R.id.password_confirm);
        register=findViewById(R.id.register);
        login=findViewById(R.id.login_page);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname_txt=name.getEditText().getText().toString();
                String email_txt=email.getEditText().getText().toString();
                String phone_txt=phone.getEditText().getText().toString();
                String password_txt=password.getEditText().getText().toString();
                String confirm_txt=confirm.getEditText().getText().toString();

                if (TextUtils.isEmpty(fullname_txt)){
                    name.setError("Name cannot be empty");
                }else if (TextUtils.isEmpty(email_txt)){
                    email.setError("Username cannot be empty");
                }else if (TextUtils.isEmpty(phone_txt)){
                    phone.setError("Username cannot be empty");
                }else if (TextUtils.isEmpty(password_txt)){
                    password.setError("Username cannot be empty");
                }else if (!password_txt.matches(confirm_txt)){
                    confirm.setError("Passwords do not match");
                }else if (password_txt.length() < 8){
                    password.setError("Password has to be at least 8 characters");
                }else if (!email_txt.matches(emailpattern)){
                    email.setError("Use correct email pattern");
                }else {
                    registerUser(fullname_txt,email_txt,phone_txt,password_txt);
                }
            }
        });

    }

    private void registerUser(String fullname_txt, String email_txt, String phone_txt, String password_txt) {
        prog.show();
        auth.createUserWithEmailAndPassword(email_txt,password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("name",fullname_txt);
                    map.put("email",email_txt);
                    map.put("phone",phone_txt);
                    map.put("userid",auth.getCurrentUser().getUid());
                    map.put("profileimage","default");

                    firebaseDatabase.child("Users").child(auth.getCurrentUser().getUid()).setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                prog.dismiss();
                                Intent intent=new Intent(Register.this,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                prog.dismiss();
            }
        });

    }
}