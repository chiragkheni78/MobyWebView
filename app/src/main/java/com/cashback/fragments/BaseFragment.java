package com.cashback.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.cashback.utils.Common;
import com.cashback.utils.SharedPreferenceManager;

public class BaseFragment extends Fragment {

    ViewBinding binding;
    private SharedPreferenceManager moSharedPreferenceManager;

    ProgressDialog loProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moSharedPreferenceManager = new SharedPreferenceManager(getActivity());
    }

    protected SharedPreferenceManager getPreferenceManager(){
        return  moSharedPreferenceManager;
    }

    protected void showProgressDialog(){
        loProgressDialog = Common.showProgressDialog(getActivity());
    }

    protected void dismissProgressDialog(){
        Common.dismissProgressDialog(loProgressDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected View getContentView(ViewBinding binding){
        this.binding = binding;
        View view = binding.getRoot();
        return view;
    }
}
