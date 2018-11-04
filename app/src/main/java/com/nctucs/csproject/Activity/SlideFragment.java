package com.nctucs.csproject.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nctucs.csproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlideFragment extends Fragment {

    int pagenum;

    public SlideFragment() {

    }

    public static SlideFragment newInstance(int position) {
        SlideFragment fragment = new SlideFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagenum = getArguments() != null ? getArguments().getInt("position") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView;
        if(pagenum == 0) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.view_tutorial_1, container, false);
        }
        else if(pagenum == 1){
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.view_tutorial_2, container, false);
        }
        else {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.view_tutorial_3, container, false);
        }
        return rootView;
    }
}
