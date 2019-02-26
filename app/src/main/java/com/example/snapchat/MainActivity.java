package com.example.snapchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
FragmentPagerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
final View background=findViewById(R.id.background_view);
        ViewPager viewPager=findViewById(R.id.viewPager);
adapterViewPager=new MyPagerAdapter(getSupportFragmentManager());
viewPager.setAdapter(adapterViewPager);

final int colorBlue= ContextCompat.getColor(this,R.color.lightblue);
final int colorPurple=ContextCompat.getColor(this,R.color.purple);

viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position==0){
            background.setBackgroundColor(colorBlue);
            background.setAlpha(1-positionOffset);
        }
        else if(position==1){
            background.setBackgroundColor(colorPurple);
            background.setAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
});
viewPager.setCurrentItem(1);
    }
    public static class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ChatFragment.newInstance();
                case 1:
                    return MusicFragment.newInstance();
                case 2:
                    return StoryFragment.newInstance();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    }

