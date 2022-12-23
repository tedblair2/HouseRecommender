package com.example.reaste.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reaste.Adapter.PostAdapter;
import com.example.reaste.Model.History;
import com.example.reaste.Model.Posts;
import com.example.reaste.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    SearchView searchView;
    RecyclerView recyclerView;

    List<Posts> postsList;
    PostAdapter postAdapter;
    List<String> historylist;
    List<String> noduplicates;
    FirebaseUser fUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history, container, false);
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        searchView=view.findViewById(R.id.search_hist);
        recyclerView=view.findViewById(R.id.recycler_history);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        postsList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postsList);
        recyclerView.setAdapter(postAdapter);
        historylist=new ArrayList<>();
        noduplicates=new ArrayList<>();

        userHistory();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchHistory(newText);
                }else {
                    getHistory();
                }
                return true;
            }
        });

        return view;
    }

    private void searchHistory(String newText) {
        Query query=FirebaseDatabase.getInstance().getReference("Posts").orderByChild("location")
                .startAt(newText).endAt(newText + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts posts=snapshot1.getValue(Posts.class);
                    for (String id:noduplicates){
                        if (posts.getPostid().equals(id)){
                            postsList.add(posts);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userHistory() {
        FirebaseDatabase.getInstance().getReference("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historylist.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    History history=snapshot1.getValue(History.class);
                    if (history.getUserid().equals(fUser.getUid())){
                        historylist.add(history.getPostid());
                    }
                }
                noduplicates.clear();
                for (String hist:historylist){
                    if (!noduplicates.contains(hist)){
                        noduplicates.add(hist);
                    }
                }
                getHistory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getHistory() {
        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts posts=snapshot1.getValue(Posts.class);
                    for (String id:noduplicates){
                        if (posts.getPostid().equals(id)){
                            postsList.add(posts);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}