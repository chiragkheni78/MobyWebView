package com.cashback.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.activities.HomeActivity;
import com.cashback.activities.PhoneLoginActivity;
import com.cashback.adapters.AdvertAdapter;
import com.cashback.adapters.ShareBannerAdapter;
import com.cashback.databinding.ActivityReferEarnBinding;
import com.cashback.models.Advertisement;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;
import com.cashback.utils.HttpDownloadUtility;
import com.cashback.utils.LogV2;
import com.cashback.utils.SharedPreferenceManager;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.cashback.fragments.MapViewFragment.REQUEST_PHONE_LOGIN;
import static com.cashback.utils.Constants.IntentKey.SCREEN_TITLE;

public class ShareFragment extends BaseFragment implements View.OnClickListener {

    public ShareFragment() {

    }

    ActivityReferEarnBinding moBinding;
    SharedPreferenceManager moSharedPreferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        moBinding = ActivityReferEarnBinding.inflate(inflater, container, false);
        return getContentView(moBinding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeContent();
    }

    private void initializeContent() {
        moSharedPreferenceManager = new SharedPreferenceManager(getContext());
        moBinding.tvWhatsapp.setOnClickListener(this);
        moBinding.tvMessenger.setOnClickListener(this);
        moBinding.tvSMS.setOnClickListener(this);
        moBinding.tvEmail.setOnClickListener(this);
        moBinding.tvFacebook.setOnClickListener(this);
        moBinding.tvTwitter.setOnClickListener(this);
        moBinding.tvTelegram.setOnClickListener(this);
        moBinding.tvCopy.setOnClickListener(this);

//        String[] loURL = moSharedPreferenceManager.getShareBannerUrl();
//        if (loURL != null) {
        setImageSlider();
//        }
    }

    private String getMessage() {
        if (!getPreferenceManager().getReferralLink().isEmpty()) {
            String lsMessage = Common.getDynamicText(getContext(), "share_message");
            if (getPreferenceManager().getReferralCode() != null)
                lsMessage = lsMessage.replace("CCCCC", getPreferenceManager().getReferralCode());
            lsMessage = lsMessage.replace("XXXXXX", getPreferenceManager().getReferralLink());
            return lsMessage;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin();
            return;
        }

        switch (v.getId()) {
            case R.id.tvWhatsapp:
                Common.openWhatsapp(getContext(), getMessage());
                break;
            case R.id.tvMessenger:
                Common.openMessenger(getContext(), getMessage());
                break;
            case R.id.tvSMS:
                Common.openSMS(getContext(), getMessage());
                break;
            case R.id.tvEmail:
                Common.openEmail(getContext(), getMessage());
                break;
            case R.id.tvFacebook:
                Common.openFacebook(getContext(), getMessage());
                break;
            case R.id.tvTwitter:
//                if (AppGlobal.getSharePageImages() != null && AppGlobal.getSharePageImages().size()>0){
//                    downloadImage(AppGlobal.getSharePageImages().get(0).getImageUrl());
//                }
                Common.openTwitter(getContext(), getMessage());
                break;
            case R.id.tvTelegram:
                Common.openTelegram(getContext(), getMessage());
                break;
            case R.id.tvCopy:
                AdGydeEvents.shareApp(getContext(), FirebaseEvents.OPEN_COPY_TEXT_SHARE);
                Common.setClipboard(getContext(), getMessage());
                Toast.makeText(getActivity(), "Copied", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void setImageSlider() {
        if (AppGlobal.moSharePageImages != null && AppGlobal.moSharePageImages.size() > 0) {
            moBinding.imageSlider.setSliderAdapter(new ShareBannerAdapter(getContext(), AppGlobal.moSharePageImages, new AdvertAdapter.OnItemClick() {
                @Override
                public void onItemClick(Advertisement advertisement) {
                    Intent loIntent = new Intent(getActivity(), HomeActivity.class);
                    loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    loIntent.putExtra(Constants.IntentKey.OFFER_ID, advertisement.getAdID());
                    loIntent.putExtra(Constants.IntentKey.CATEGORY_ID, advertisement.getCategoryID());
                    loIntent.putExtra(Constants.IntentKey.BANNER_ID, advertisement.getBannerID());
                    startActivity(loIntent);
                    getActivity().finish();
                }
            }));

            if (AppGlobal.moSharePageImages.size() < 2) {
                moBinding.imageSlider.setInfiniteAdapterEnabled(false);
            }
        }
    }

    private void openPhoneLogin() {
        Intent loIntent = new Intent(getContext(), PhoneLoginActivity.class);
        loIntent.putExtra(SCREEN_TITLE, this.getResources().getString(R.string.msg_verify_phone_number));
        startActivityForResult(loIntent, REQUEST_PHONE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHONE_LOGIN) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = new Bundle();
                bundle.putString("mobile", AppGlobal.getPhoneNumber());
                FirebaseEvents.trigger(getActivity(), bundle, FirebaseEvents.SHARE_APP_OTP_VERIFIED);
                Toast.makeText(getContext(), "Phone Verified", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void downloadImage(String fsUrl) {
        if (isStoragePermissionGranted(getActivity())) {
            new DownloadFileAsync(fsUrl).execute();
        }
    }

    private class DownloadFileAsync extends AsyncTask<String, Void, String> {

        String lsSaveDir = LogV2.PATH + "/Images/";
        String lsImageUrl;

        public DownloadFileAsync(String fsUrl) {
            lsImageUrl = fsUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return HttpDownloadUtility.downloadFile(lsImageUrl, lsSaveDir, "share-image");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String fsDownloadedFile) {
            super.onPostExecute(fsDownloadedFile);
            dismissProgressDialog();
            if (!TextUtils.isEmpty(fsDownloadedFile)) {
                Common.openInstagram(getContext(), getMessage(), fsDownloadedFile);
            }
        }
    }


    public static final int REQUEST_FILE_ACCESS = 910;

    public boolean isStoragePermissionGranted(Activity foContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (foContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TTT", "Permission is granted2");
                return true;
            } else {
                Log.v("TTT", "Permission is revoked2");
                ActivityCompat.requestPermissions(foContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FILE_ACCESS);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TTT", "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FILE_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moBinding.tvTwitter.performClick();
                } else {
                    Common.showErrorDialog(getContext(), "", false);
                }
                break;
        }
    }
}
