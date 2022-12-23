package com.example.reaste.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.reaste.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewAdapter2 extends PagerAdapter {
    private List<String> imagelinks;

    public ViewAdapter2(List<String> imagelinks) {
        this.imagelinks = imagelinks;
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
        View view=LayoutInflater.from(container.getContext()).inflate(R.layout.view_items, container, false);
        ImageView imageView=view.findViewById(R.id.image_load);
        Glide.with(container.getContext()).load(imagelinks.get(position)).centerCrop()
                .placeholder(R.drawable.not_available).into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }
}
