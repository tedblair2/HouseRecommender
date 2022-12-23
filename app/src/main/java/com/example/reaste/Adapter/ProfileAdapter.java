package com.example.reaste.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.reaste.Model.Images;
import com.example.reaste.Model.Posts;
import com.example.reaste.PostActivity;
import com.example.reaste.PosteditActivity;
import com.example.reaste.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{
    private Context context;
    private List<Posts> posts;

    public ProfileAdapter(Context context, List<Posts> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.profile_items,parent,false);
        return new ProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Posts post=posts.get(position);
        holder.name.setText(post.getTitle());
        holder.location.setText(post.getLocation());
        holder.price.setText(post.getPrice());
        holder.units.setText(new StringBuilder().append(post.getUnits()).append(" units").toString());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PosteditActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("authorid",post.getPublisherid());
                context.startActivity(intent);
            }
        });
        ArrayList<SlideModel> links=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Images").document(post.getPostid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Images images=documentSnapshot.toObject(Images.class);
                for (String id:images.getImagelinks()){
                    links.add(new SlideModel(id, ScaleTypes.FIT));
                }
                holder.imageSlider.setImageList(links,ScaleTypes.FIT);
                holder.imageSlider.stopSliding();
            }
        });

    }

//    private void initializeViews(List<String> links, ViewHolder holder, int position) {
//        ViewAdapter2 viewAdapter=new ViewAdapter2(links);
//        ((ViewHolder)holder).viewPager.setAdapter(viewAdapter);
//        ((ViewHolder)holder).viewPager.setClipToPadding(false);
//        ((ViewHolder)holder).viewPager.setPadding(10,0,10,0);
//        int dotscount= links.size();
//        ImageView[] dots=new ImageView[dotscount];
//        for (int i=0;i<dotscount;i++){
//            dots[i]=new ImageView(context);
//            dots[i].setImageDrawable(ContextCompat.getDrawable(context,R.drawable.nonactive_dot));
//            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(8,3,8,3);
//
//            ((ViewHolder)holder).slidebar.addView(dots[i],params);
//            dots[0].setImageDrawable(ContextCompat.getDrawable(context,R.drawable.active_dot));
//            ((ViewHolder)holder).viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//                    for (int i=0;i<dotscount;i++){
//                        dots[i].setImageDrawable(ContextCompat.getDrawable(context,R.drawable.nonactive_dot));
//                    }
//                    dots[position].setImageDrawable(ContextCompat.getDrawable(context,R.drawable.active_dot));
//
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//        }
    //}

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
//        @BindView(R.id.images_profile)
//        ViewPager viewPager;
//
//        @BindView(R.id.slidebar1)
//        LinearLayout slidebar;

        TextView name,location,price,units;
        Button edit;
        ImageSlider imageSlider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_profile);
            location=itemView.findViewById(R.id.location_profile);
            price=itemView.findViewById(R.id.price_profile);
            units=itemView.findViewById(R.id.units_profile);
            edit=itemView.findViewById(R.id.edit_page);
            imageSlider=itemView.findViewById(R.id.image_autoslider);

        }
    }
}
