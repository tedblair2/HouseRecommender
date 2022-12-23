package com.example.reaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.reaste.Fragments.AddFragment;
import com.example.reaste.Fragments.BuyFragment;
import com.example.reaste.Fragments.LibraryFragment;
import com.example.reaste.Fragments.ProfileFragment;
import com.example.reaste.Fragments.RentFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom=findViewById(R.id.bottom_nav);

        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_rent:
                        fragment= new RentFragment();
                        break;
                    case R.id.nav_buy:
                        fragment=new BuyFragment();
                        break;
                    case R.id.nav_add:
                        fragment=new AddFragment();
                        break;
                    case R.id.nav_library:
                        fragment=new LibraryFragment();
                        break;
                    case R.id.nav_profile:
                        fragment=new ProfileFragment();
                        break;
                }
                if (fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

                }
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RentFragment()).commit();
    }
}