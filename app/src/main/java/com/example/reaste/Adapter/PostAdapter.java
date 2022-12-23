package com.example.reaste.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.reaste.Model.History;
import com.example.reaste.Model.Images;
import com.example.reaste.Model.Posts;
import com.example.reaste.PostActivity;
import com.example.reaste.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private Context context;
    private List<Posts> postsList;

    public PostAdapter(Context context, List<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.house_items,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Posts post= postsList.get(position);
        holder.name.setText(post.getTitle());
        holder.location.setText(post.getLocation());
        holder.price.setText(post.getPrice());
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();

        history(post.getPostid(),fUser.getUid(),holder.more);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PostActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("authorid",post.getPublisherid());
                context.startActivity(intent);

                if (holder.more.getTag().equals("Not")){
                    HashMap<String,Object> history=new HashMap<>();
                    history.put("postid",post.getPostid());
                    history.put("userid",fUser.getUid());
                    reference.child("History").push().setValue(history);
                }
            }
        });
        ArrayList<SlideModel> list=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Images").document(post.getPostid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Images images=documentSnapshot.toObject(Images.class);
                for (String id:images.getImagelinks()){
                    list.add(new SlideModel(id, ScaleTypes.FIT));
                }
                holder.imageSlider.setImageList(list,ScaleTypes.FIT);
                holder.imageSlider.stopSliding();
            }
        });
    }

//    private void initializeViews(List<String> links, ViewHolder holder, int position) {
//        ViewAdapter2 viewAdapter=new ViewAdapter2(links);
//        ((ViewHolder)holder).images.setAdapter(viewAdapter);
//        ((ViewHolder)holder).images.setClipToPadding(false);
//        ((ViewHolder)holder).images.setPadding(10,0,10,0);
//
//    }
    private void history(String postid, String uid, Button more) {
        FirebaseDatabase.getInstance().getReference("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    History history=snapshot1.getValue(History.class);
                    if (history.getPostid().equals(postid) && history.getUserid().equals(uid)){
                        more.setTag("Seen");
                    }else{
                        more.setTag("Not");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
//        @BindView(R.id.home_images)
//        ViewPager images;
//
//        @BindView(R.id.slidebarhome)
//        LinearLayout slidebar;

        TextView name,location,price;
        Button more;
        ImageSlider imageSlider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.house_name);
            location=itemView.findViewById(R.id.location_house);
            price=itemView.findViewById(R.id.price1_house);
            more=itemView.findViewById(R.id.next_page);
            imageSlider=itemView.findViewById(R.id.imageslider_home);

        }
    }
}
