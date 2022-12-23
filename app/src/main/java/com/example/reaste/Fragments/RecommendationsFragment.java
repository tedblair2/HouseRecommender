package com.example.reaste.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reaste.Adapter.PostAdapter;
import com.example.reaste.Adapter.RetrofitInstance;
import com.example.reaste.Model.Api;
import com.example.reaste.Model.Posts;
import com.example.reaste.Model.RetrofitApi;
import com.example.reaste.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsFragment extends Fragment {
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Posts> posts;
    ProgressDialog dialog;

    FirebaseUser fUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_recommendations, container, false);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();

        createRecommendations();

        recyclerView=view.findViewById(R.id.recycler_rec);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        posts=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),posts);
        recyclerView.setAdapter(postAdapter);
        return view;
    }

    private void createRecommendations() {
        Call<Api> call= RetrofitInstance.getInstance().api().getPosts(fUser.getUid());

        call.enqueue(new Callback<Api>() {
            @Override
            public void onResponse(Call<Api> call, Response<Api> response) {
                if (!response.isSuccessful()){
                    Log.d("api error","response code is "+response.code());
                    dialog.dismiss();
                    return;
                }

                Api api= response.body();
                getRecommendations(api.getPostlist());
            }

            @Override
            public void onFailure(Call<Api> call, Throwable t) {
                Log.d("throw error","error is "+t.getMessage());
                Snackbar.make(recyclerView,"Cannot get recommendations at this time",Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createRecommendations();
                            }
                        }).show();
                dialog.dismiss();

            }
        });
    }

    private void getRecommendations(List<String> postlist) {
        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts post=snapshot1.getValue(Posts.class);
                    for (String id:postlist){
                        if (post.getPostid().equals(id)){
                            posts.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}