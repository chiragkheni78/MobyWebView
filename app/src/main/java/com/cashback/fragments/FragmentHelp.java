package com.cashback.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.adapters.HelpListAdapter;
import com.cashback.databinding.ActivityHelpBinding;
import com.cashback.models.response.HelpResponse;
import com.cashback.models.viewmodel.HelpViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

public class FragmentHelp extends BaseFragment {

    public FragmentHelp() {

    }

    ActivityHelpBinding moBinding;
    HelpViewModel moProfileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        moBinding = ActivityHelpBinding.inflate(inflater, container, false);
        return getContentView(moBinding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeContent();
    }


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityImageSlidderBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }*/

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeContent() {
        Common.hideKeyboard(getActivity());

       initViewModel();

       if (getArguments()!= null) {
           String msScreenType = getArguments().getString(Constants.IntentKey.ADVERT_SCREEN_TYPE);
           String lsTitle = getArguments().getString(Constants.IntentKey.SCREEN_TITLE);

           //showProgressDialog();
           //moProfileViewModel.fetAdvertList(getContext(), msScreenType);

           setToolbar(lsTitle);
       }

       moBinding.webViewHelp.getSettings().setJavaScriptEnabled(true);
       moBinding.webViewHelp.loadUrl("https://mobyads.in/moby/v2-apis/?fsAction=loadWebViewHtml&fsPage=faqs");
    }

    private void setToolbar(String fsTitle) {

        moBinding.toolbar.toolbar.setVisibility(View.GONE);
    }

    private void initViewModel() {
        moProfileViewModel = new ViewModelProvider(this).get(HelpViewModel.class);
        moProfileViewModel.advertListStatus.observe(getActivity(), advertImageObserver);
    }

    Observer<HelpResponse> advertImageObserver = loJsonObject -> {
        if (!loJsonObject.isError()) {
            setImageSlider(loJsonObject.getFoFAQLists());
        } else {
            Common.showErrorDialog(getActivity(), loJsonObject.getMessage(), false);
        }
        Common.dismissProgressDialog(loProgressDialog);
    };

    private void setImageSlider(ArrayList<HelpResponse.HelpModel> foAdvertisementList) {
        moBinding.reyclerViewHelp.setLayoutManager(new LinearLayoutManager(getActivity()));
        moBinding.reyclerViewHelp.setAdapter(new HelpListAdapter(getActivity(), foAdvertisementList));
    }



}
