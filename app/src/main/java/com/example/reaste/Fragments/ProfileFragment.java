package com.example.reaste.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.reaste.Adapter.ProfileAdapter;
import com.example.reaste.EditActivity;
import com.example.reaste.Model.Posts;
import com.example.reaste.Model.Users;
import com.example.reaste.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    RecyclerView recyclerView;
    TextView owner,posts;
    Button edit;
    CircleImageView profile;

    List<Posts> postsList;
    ProfileAdapter profileAdapter;

    FirebaseUser fUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        Toolbar toolbar=view.findViewById(R.id.toolbar_profile);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=view.findViewById(R.id.recycler_profile);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        postsList=new ArrayList<>();
        profileAdapter=new ProfileAdapter(getContext(),postsList);
        recyclerView.setAdapter(profileAdapter);
        owner=view.findViewById(R.id.owner1_house);
        posts=view.findViewById(R.id.no_of_posts);
        profile=view.findViewById(R.id.profile_image);
        edit=view.findViewById(R.id.edit_btn);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),EditActivity.class));
            }
        });

        userInfo();
        postCount();
        userPosts();
        return view;
    }

    private void userPosts() {
        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts posts1=snapshot1.getValue(Posts.class);
                    if (posts1.getPublisherid().equals(fUser.getUid())){
                        postsList.add(posts1);
                    }
                }
                profileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void postCount() {
        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter=0;
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts posts1=snapshot1.getValue(Posts.class);
                    if (posts1.getPublisherid().equals(fUser.getUid())){
                        counter++;
                    }
                }
                posts.setText(new StringBuilder().append(String.valueOf(counter)).append(" posts").toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        owner.setText(users.getName());
                        if (users.getProfileimage().equals("default")){
                            profile.setImageResource(R.drawable.profileplaceholder);
                        }else{
                            Picasso.get().load(users.getProfileimage()).into(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}