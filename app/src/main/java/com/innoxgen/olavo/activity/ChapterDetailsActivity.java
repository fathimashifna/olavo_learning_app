package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.innoxgen.olavo.fragment.ExamZoneFragment;
import com.innoxgen.olavo.fragment.MaterialsFragment;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.fragment.StudyFragment;

import java.util.ArrayList;
import java.util.List;

public class ChapterDetailsActivity extends AppCompatActivity {

    String chap_id,chap_name;
    TextView title;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_details);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        title=findViewById(R.id.tv_header_comment);
        viewPager = findViewById(R.id.viewPager_det);
        iv_back=findViewById(R.id.iv_back);
        tabLayout = findViewById(R.id.tabLayout_det);

        if (getIntent().getExtras() != null) {
            chap_id= getIntent().getStringExtra("chap_id");
            chap_name= getIntent().getStringExtra("Chapname");
        }
        title.setText(chap_name);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        setupViewPager(viewPager,bundle);
        tabLayout.setupWithViewPager(viewPager);

    }

    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
    public String getMyData() {
        return chap_id;
    }
    private void setupViewPager(ViewPager viewPager,Bundle bundle) {
       ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StudyFragment(), "STUDY ZONE",chap_id);
        adapter.addFragment(new MaterialsFragment(), "MATERIALS",chap_id);
        adapter.addFragment(new ExamZoneFragment(), "EXAM ZONE",chap_id);
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title,String chap_id) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}