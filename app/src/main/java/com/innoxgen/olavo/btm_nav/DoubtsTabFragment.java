package com.innoxgen.olavo.btm_nav;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.btm_nav.DiscussionFragment;
import com.innoxgen.olavo.btm_nav.DoubtsFragment;
import com.innoxgen.olavo.fragment.NoNetworkFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fathima Shifna K on 05-09-2020.
 */
public  class DoubtsTabFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_doubts, container, false);
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.viewPager);



        if (isNetworkConnectionAvailable()) {
            // fetch data

            viewPager.setOffscreenPageLimit(0);
            // Set Tabs inside Toolbar
            TabLayout tabs =  root.findViewById(R.id.tabLayout);


            tabs.setupWithViewPager(viewPager);
            setupViewPager(viewPager);
        } else {
            // display error
           if (getActivity()!=null)
           {
               Fragment fragment = new NoNetworkFragment();
               FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
               ft.replace(R.id.nav_host_fragment, fragment);
               ft.addToBackStack(null);
               getActivity().getSupportFragmentManager().popBackStack();
               ft.commit();
           }
        }


        return root;
    }

    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new DoubtsFragment(), "DOUBTS FORUM");
        adapter.addFragment(new DiscussionFragment(), "DISCUSSION");
        viewPager.setAdapter(adapter);


    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
