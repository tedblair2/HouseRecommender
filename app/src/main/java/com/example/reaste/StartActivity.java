package com.example.reaste;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    Animation top,bottom;
    TextView title,slogan;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        title=findViewById(R.id.name);
        slogan=findViewById(R.id.slogan);
        logo=findViewById(R.id.logo);

        logo.setAnimation(top);
        title.setAnimation(bottom);
        slogan.setAnimation(bottom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null){
                    startActivity(new Intent(StartActivity.this,MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }else {
                    Intent intent=new Intent(StartActivity.this,Login.class);
                    startActivity(intent);
                }
                finish();
            }
        },4500);
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
//        if (fUser!= null){
//            startActivity(new Intent(StartActivity.this,MainActivity.class)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//            finish();
//        }
//    }
}