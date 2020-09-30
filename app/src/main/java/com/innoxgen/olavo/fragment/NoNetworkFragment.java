package com.innoxgen.olavo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.innoxgen.olavo.R;
import com.innoxgen.olavo.nav_item.HomeFragment;


public class NoNetworkFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_no_internet, container, false);


        TextView no_conection = view.findViewById(R.id.txt_no_connection);
        TextView check =  view.findViewById(R.id.txt_1);
        TextView txt_try = view.findViewById(R.id.txt_try);



        LinearLayout layout_try =  view.findViewById(R.id.layout_try);
        layout_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout fl = FrameLayout.class.cast(getActivity().findViewById(R.id.nav_host_fragment));
                fl.removeAllViews();
                Fragment fragment = new HomeFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, fragment);
                getFragmentManager().popBackStack();
                ft.addToBackStack(null);
                ft.commit();

            }
        });


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("No network");
    }
}
