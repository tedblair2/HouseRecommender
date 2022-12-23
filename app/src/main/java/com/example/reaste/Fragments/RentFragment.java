package com.example.reaste.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.reaste.Adapter.PostAdapter;
import com.example.reaste.EditActivity;
import com.example.reaste.Login;
import com.example.reaste.MainActivity;
import com.example.reaste.Model.Posts;
import com.example.reaste.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class RentFragment extends Fragment {
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Posts> posts;
    Toolbar toolbar;
    SearchView searchView=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_rent, container, false);

        toolbar=view.findViewById(R.id.toolbar_rent);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        recyclerView=view.findViewById(R.id.recycler_rent);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        posts=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),posts);
        recyclerView.setAdapter(postAdapter);

        getPosts();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu,menu);
        MenuItem item=menu.findItem(R.id.search_tool);
        SearchManager manager=(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (item != null){
            searchView=(SearchView) item.getActionView();
        }
        if (searchView !=null){
            searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint("Search...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!TextUtils.isEmpty(newText)){
                        searchRent(newText);
                    }else {
                        getPosts();
                    }
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_tool:
                return false;
            case R.id.logout:
                AlertDialog dialog=new AlertDialog.Builder(requireContext()).create();
                dialog.setMessage("Are you sure you want to logout?");
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(requireContext(),Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchRent(String s) {
        Query query=FirebaseDatabase.getInstance().getReference("Posts").orderByChild("location")
                .startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts post=snapshot1.getValue(Posts.class);
                    assert post != null;
                    if (post.getType().equals("Rent")){
                        posts.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts() {
        FirebaseUser fUser=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts posts1=snapshot1.getValue(Posts.class);
                    assert posts1 != null;
                    if (posts1.getType().equals("Rent")) {
                        assert fUser != null;
                        if (!posts1.getPublisherid().equals(fUser.getUid())) {
                            posts.add(posts1);
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