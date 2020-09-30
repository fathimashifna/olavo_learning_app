package com.innoxgen.olavo.nav_item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.innoxgen.olavo.R;


public class MyCourceFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mycource, container, false);
        getActivity().setTitle("My Course");

        return root;
    }
}