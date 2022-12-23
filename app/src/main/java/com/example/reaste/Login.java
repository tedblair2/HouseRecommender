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
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    TextInputLayout email,password;
    Button login;
    TextView regpage;
    ProgressDialog prog;
    String Emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        prog=new ProgressDialog(this);
        prog.setTitle("Log In");
        prog.setMessage("Loading...");
        auth=FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        regpage=findViewById(R.id.reg_page);

        regpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt=email.getEditText().getText().toString();
                String password_txt=password.getEditText().getText().toString();

                if(TextUtils.isEmpty(email_txt)){
                    email.setError("Email cannot be empty");
                }else if (!email_txt.matches(Emailpattern)){
                    email.setError("Email is not correct format");
                }else if (password_txt.length() < 8){
                    password.setError("Password too short");
                }else if (TextUtils.isEmpty(password_txt)){
                    password.setError("Password cannot be empty");
                }
                else{
                    signIn(email_txt,password_txt);
                }
            }
        });
    }

    private void signIn(String email_txt, String password_txt) {
        prog.show();
        auth.signInWithEmailAndPassword(email_txt,password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    prog.dismiss();
                    Intent intent=new Intent(Login.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                prog.dismiss();
            }
        });
    }
}