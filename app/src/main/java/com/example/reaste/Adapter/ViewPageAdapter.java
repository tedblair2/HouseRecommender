package com.example.reaste.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.reaste.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPageAdapter extends PagerAdapter {
    private List<String> imagelinks;
    private LayoutInflater layoutInflater;
    private Context context;

    public ViewPageAdapter(List<String> imagelinks, Context context) {
        this.imagelinks = imagelinks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imagelinks.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.view_items,container,false);
        ImageView imageView=view.findViewById(R.id.image_load);
        Glide.with(layoutInflater.getContext()).load(imagelinks.get(position)).centerCrop()
                .placeholder(R.drawable.not_available).into(imageView);

        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }
}
