package com.example.reaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.reaste.Adapter.PostAdapter;
import com.example.reaste.Adapter.RetrofitInstance;
import com.example.reaste.Adapter.ViewPageAdapter;
import com.example.reaste.Model.Api;
import com.example.reaste.Model.Images;
import com.example.reaste.Model.Posts;
import com.example.reaste.Model.RetrofitApi;
import com.example.reaste.Model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    TextView title,owner,location,price,bedrooms,amenities,units;
    MaterialButton call;
    ViewPager viewPager;
    ViewPageAdapter viewPageAdapter;
    LinearLayout sliderbar;
    private int dotscount;
    private ImageView[] dots;
    private RecyclerView recyclerView;
    List<Posts> postsList;
    PostAdapter postAdapter;

    String postid;
    String authorid;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar=findViewById(R.id.toolbar_more);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        reference= FirebaseDatabase.getInstance().getReference();
        title=findViewById(R.id.title_house);
        owner=findViewById(R.id.owner_house);
        location=findViewById(R.id.location1_house);
        price=findViewById(R.id.price_house);
        bedrooms=findViewById(R.id.bedrooms_house);
        amenities=findViewById(R.id.amenities_house);
        units=findViewById(R.id.units_house);
        call=findViewById(R.id.call);
        sliderbar=findViewById(R.id.slidebar);
        viewPager=findViewById(R.id.imageslider);
        recyclerView=findViewById(R.id.recycler_content);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        postsList=new ArrayList<>();
        postAdapter=new PostAdapter(PostActivity.this,postsList);
        recyclerView.setAdapter(postAdapter);

        Intent intent=getIntent();
        postid=intent.getStringExtra("postid");
        authorid=intent.getStringExtra("authorid");

        createRecommendtations();

        FirebaseDatabase.getInstance().getReference("Posts").child(postid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Posts posts=snapshot.getValue(Posts.class);
                        title.setText(posts.getTitle());
                        owner.setText(new StringBuilder().append("Provided by ").append(posts.getAgency()).toString());
                        location.setText(new StringBuilder().append("Located in ").append(posts.getLocation()).toString());
                        price.setText(new StringBuilder().append("Price:  ").append(posts.getPrice()).toString());
                        bedrooms.setText(new StringBuilder().append("Bedrooms: ").append(posts.getBedrooms()).toString());
                        if (posts.getAmenities().equals("null")){
                            amenities.setText("Various amenities are available such as wifi,hot shower and borehole");
                        }else{
                            amenities.setText(new StringBuilder().append("Amenities: ").append(posts.getAmenities()).toString());
                        }

                        units.setText(new StringBuilder().append(posts.getUnits()).append(" units").toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        DocumentReference ref= FirebaseFirestore.getInstance().collection("Images").document(postid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Images images=documentSnapshot.toObject(Images.class);
                List<String> imagelinks=new ArrayList<>();
                assert images != null;
                for (String links:images.getImagelinks()){
                    imagelinks.add(links);
                }
                viewPageAdapter=new ViewPageAdapter(imagelinks,PostActivity.this);
                viewPager.setAdapter(viewPageAdapter);
                dotscount= viewPageAdapter.getCount();
                dots=new ImageView[dotscount];
                for (int i=0;i<dotscount;i++){
                    dots[i]=new ImageView(PostActivity.this);
                    dots[i].setImageDrawable(ContextCompat.getDrawable(PostActivity.this,R.drawable.nonactive_dot));
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(5,0,5,0);

                    sliderbar.addView(dots[i],params);
                }
                dots[0].setImageDrawable(ContextCompat.getDrawable(PostActivity.this,R.drawable.active_dot));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for (int i=0;i<dotscount;i++){
                            dots[i].setImageDrawable(ContextCompat.getDrawable(PostActivity.this,R.drawable.nonactive_dot));
                        }
                        dots[position].setImageDrawable(ContextCompat.getDrawable(PostActivity.this,R.drawable.active_dot));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });
        FirebaseDatabase.getInstance().getReference("Users").child(authorid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        String phoneno=users.getPhone();

                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(Intent.ACTION_DIAL,
                                        Uri.fromParts("tel",phoneno,null));
                                startActivity(intent1);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void createRecommendtations() {
        Call<Api> call= RetrofitInstance.getInstance().api().createpost(postid);

        call.enqueue(new Callback<Api>() {
            @Override
            public void onResponse(Call<Api> call, Response<Api> response) {
                if (!response.isSuccessful()){
                    Log.d("api error","Code "+response.code());
                    return;
                }
                Api apiresponse=response.body();
                assert apiresponse != null;
                getReccomendations(apiresponse.getPostlist());
                Log.d("response code","response code is "+response.code());
            }

            @Override
            public void onFailure(Call<Api> call, Throwable t) {
                Log.d("throw error","error is "+t.getMessage());
                Snackbar.make(recyclerView,"Cannot get Recommendations at this time",Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchRecommendations(postid);
                            }
                        }).show();

            }
        });
    }

    private void fetchRecommendations(String postid) {
        Call<Api> call= RetrofitInstance.getInstance().api().createpost(postid);

        call.enqueue(new Callback<Api>() {
            @Override
            public void onResponse(Call<Api> call, Response<Api> response) {
                if (!response.isSuccessful()){
                    Log.d("api error","Code "+response.code());
                    return;
                }
                Api apiresponse=response.body();
                assert apiresponse != null;
                getReccomendations(apiresponse.getPostlist());
                Log.d("response code","response code is "+response.code());
            }

            @Override
            public void onFailure(Call<Api> call, Throwable t) {
                Log.d("throw error","error is "+t.getMessage());
                Snackbar.make(recyclerView,"Cannot get Recommendations at this time",Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchRecommendations(postid);
                            }
                        }).show();

            }
        });
    }

    private void getReccomendations(List<String> postlist1) {
        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Posts post=snapshot1.getValue(Posts.class);
                    for (String id:postlist1){
                        if (post.getPostid().equals(id)){
                            postsList.add(post);
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