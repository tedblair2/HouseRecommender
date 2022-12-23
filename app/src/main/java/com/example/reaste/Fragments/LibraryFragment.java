package com.example.reaste.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reaste.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_library, container, false);

        Toolbar toolbar=view.findViewById(R.id.toolbar_library);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Library");

        tabLayout=view.findViewById(R.id.tab_layout);
        viewPager=view.findViewById(R.id.fragments_slide);

        TitleAdapter titleAdapter=new TitleAdapter(getChildFragmentManager());
        titleAdapter.addFragments(new RecommendationsFragment(),"For you");
        titleAdapter.addFragments(new HistoryFragment() ,"History");

        viewPager.setAdapter(titleAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
    public class TitleAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public TitleAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}