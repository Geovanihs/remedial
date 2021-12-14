package com.ut.login.Activities.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ut.login.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Checker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Checker extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_checker, container, false);;
        return view;
    }
}