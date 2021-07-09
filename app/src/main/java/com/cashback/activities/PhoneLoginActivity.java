package com.cashback.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.adapters.DeviceListAdapter;
import com.cashback.databinding.ActivityPhoneLoginBinding;
import com.cashback.models.Device;
import com.cashback.models.PhoneObject;
import com.cashback.models.response.MobileDeviceResponse;
import com.cashback.models.response.ProceedDeviceResponse;
import com.cashback.models.viewmodel.PhoneLoginViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.custom.otpview.OTPListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;

import java.util.ArrayList;

import static com.cashback.utils.Constants.IntentKey.SCREEN_TITLE;

@SuppressWarnings("All")
public class PhoneLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = PhoneLoginActivity.class.getSimpleName();
    ActivityPhoneLoginBinding moBinding;
    PhoneLoginViewModel moPhoneLoginViewModel;
    CountDownTimer timer;
    private static final long TIMER_MINUTE = 1000 * 60 * 1;
    private int count = 60;
    String stTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityPhoneLoginBinding.inflate(getLayoutInflater());

        if (getIntent() != null && getIntent().hasExtra(SCREEN_TITLE)) {
            stTitle = getIntent().getStringExtra(SCREEN_TITLE);
        }

        if (stTitle != null && !stTitle.isEmpty()) {
            moBinding.tvLoginTitle.setText(stTitle);
        }

        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        initViewModel();

        moBinding.btnSendOTP.setOnClickListener(this);
        moBinding.btnVerifyOTP.setOnClickListener(this);
        moBinding.tvResendCode.setOnClickListener(this);
        moBinding.imageLoginClose.setOnClickListener(this);
        otpListener();
    }

    private void initViewModel() {
        moPhoneLoginViewModel = new ViewModelProvider(this).get(PhoneLoginViewModel.class);
        moPhoneLoginViewModel.phoneVerificationStatus.observe(this, uploadBillObserver);
        moPhoneLoginViewModel.fetchConnectedDeviceStatus.observe(this, fetchConnectedDeviceObserver);
        moPhoneLoginViewModel.proceedDeviceStatus.observe(this, proceedDeviceObserver);

        moPhoneLoginViewModel.initPhoneVerification(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendOTP:
                sendOTP();
                break;
            case R.id.btnVerifyOTP:
                verifyOTP();
                break;
            case R.id.tvResendCode:
                resendOTP();
                break;
            case R.id.imageLoginClose:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void startTime() {
        moBinding.tvResendCode.setEnabled(false);
        timer = new CountDownTimer(TIMER_MINUTE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = "";
                if (("" + count).length() > 1) {
                    time = count + "";
                } else {
                    time = "0" + time;
                }
                moBinding.countTime.setText("00:" + time);
                count--;
            }

            @Override
            public void onFinish() {
                moBinding.countTime.setText("00:00");
                count--;
                moBinding.tvResendCode.setEnabled(true);
            }
        }.start();
    }


    Observer<PhoneObject> uploadBillObserver = new Observer<PhoneObject>() {
        @Override
        public void onChanged(PhoneObject loPhoneObject) {
            updateUI(loPhoneObject.getUiState(), loPhoneObject.getUser(), loPhoneObject.getCredentials());
        }
    };

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean moVerificationInProgress = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, moVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        moVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_CODE_SENT = 2;
    public static final int STATE_VERIFY_FAILED = 3;
    public static final int STATE_VERIFY_SUCCESS = 4;
    public static final int STATE_SIGNIN_FAILED = 5;
    public static final int STATE_SIGNIN_SUCCESS = 6;


    private void otpListener() {
        moBinding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            @Override
            public void onOTPComplete(String otp) {
                Common.hideKeyboard(PhoneLoginActivity.this);
            }
        });
    }

    private void sendOTP() {
        Common.hideKeyboard(this);
        String lsMessage = Common.validatePhoneNumber(moBinding.etPhoneNo.getText().toString().trim());
        if (!lsMessage.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), lsMessage, Snackbar.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog();
        String lsPhoneNumber = moBinding.tvCountryCode.getText() + moBinding.etPhoneNo.getText().toString().trim();
        moPhoneLoginViewModel.startPhoneNumberVerification(this, lsPhoneNumber);
        moVerificationInProgress = true;
    }

    private void verifyOTP() {
        String lsOtpCode = moBinding.otpView.getOtp().toString();
        if (TextUtils.isEmpty(lsOtpCode)) {
            Snackbar.make(findViewById(android.R.id.content), "Cannot be empty.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        moPhoneLoginViewModel.verifyPhoneNumberWithCode(this, lsOtpCode);
    }

    private void resendOTP() {
        String lsMessage = Common.validatePhoneNumber(moBinding.etPhoneNo.getText().toString().trim());
        if (!lsMessage.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), lsMessage, Snackbar.LENGTH_SHORT).show();
            return;
        }
        String lsPhoneNumber = moBinding.tvCountryCode.getText() + moBinding.etPhoneNo.getText().toString().trim();
        moPhoneLoginViewModel.resendVerificationCode(this, lsPhoneNumber);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        if (moBinding.etPhoneNo.length() > 0){
            return;
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = moPhoneLoginViewModel.getCurrentUser();
        moPhoneLoginViewModel.updateUI(currentUser);

        String lsMessage = Common.validatePhoneNumber(moBinding.etPhoneNo.getText().toString().trim());
        if (moVerificationInProgress && !lsMessage.isEmpty()) {
            String lsPhoneNumber = moBinding.tvCountryCode.getText() + moBinding.etPhoneNo.getText().toString().trim();
            moPhoneLoginViewModel.startPhoneNumberVerification(this, lsPhoneNumber);
            moVerificationInProgress = true;
        }
    }
    // [END on_start_check_user]


    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                moBinding.etPhoneNo.setText("");
                moBinding.etPhoneNo.setEnabled(true);
                moBinding.llOTPView.setVisibility(View.GONE);
                moBinding.flSendOTP.setVisibility(View.VISIBLE);
                break;
            case STATE_CODE_SENT:
                dismissProgressDialog();
                // Code sent state, show the verification field, the
                moBinding.etPhoneNo.setEnabled(false);

                moBinding.flSendOTP.setVisibility(View.GONE);
                moBinding.llOTPView.setVisibility(View.VISIBLE);

                startTime();

                break;
            case STATE_VERIFY_FAILED:
                dismissProgressDialog();
                // Verification has failed, show all options
                Snackbar.make(findViewById(android.R.id.content), R.string.status_verification_failed,
                        Snackbar.LENGTH_SHORT).show();
                moVerificationInProgress = false;
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                // Set the verification text based on the credential
                moVerificationInProgress = false;
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        moBinding.otpView.setOTP(cred.getSmsCode());
                    }
                }
                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                moBinding.otpView.setOTP("");
                timer.cancel();

                moBinding.tvResendCode.setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(android.R.id.content), R.string.status_sign_in_failed,
                        Snackbar.LENGTH_SHORT).show();

                break;
            case STATE_SIGNIN_SUCCESS:
                checkConnectedDevices();
                break;
        }
    }

    private void checkConnectedDevices() {
        showProgressDialog();
        moPhoneLoginViewModel.getConnectedDeviceList(getContext());
    }

    Observer<MobileDeviceResponse> fetchConnectedDeviceObserver = new Observer<MobileDeviceResponse>() {

        @Override
        public void onChanged(MobileDeviceResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getDevicesList() != null) {
                    if (loJsonObject.getDevicesList().size() > 1) {
                        showDeviceListDialog(loJsonObject.getDevicesList());
                    }
                }
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };


    Observer<ProceedDeviceResponse> proceedDeviceObserver = new Observer<ProceedDeviceResponse>() {

        @Override
        public void onChanged(ProceedDeviceResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                //All Done
                Intent intent = new Intent();
                setResult(1, intent);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void showDeviceListDialog(ArrayList<Device> foDeviceList) {

        try {
            Dialog moBankCardDialog = new Dialog(this);
            moBankCardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            moBankCardDialog.setContentView(R.layout.dialog_multiple_devices);
            moBankCardDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            moBankCardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            moBankCardDialog.getWindow().setGravity(Gravity.CENTER);
            moBankCardDialog.setCancelable(false);

            if (!this.isFinishing())
                moBankCardDialog.show();

            TextView loTvTitle = moBankCardDialog.findViewById(R.id.tvTitle);
            loTvTitle.setText(Common.getDynamicText(getContext(), "multiple_device_title"));

            TextView loTvMessage = moBankCardDialog.findViewById(R.id.tvMessage);
            loTvMessage.setText(Common.getDynamicText(getContext(), "multiple_device_msg"));

            Button loBtnUpdateCard = moBankCardDialog.findViewById(R.id.btnUpdateCard);

            RecyclerView loRvBankCard = moBankCardDialog.findViewById(R.id.rvBankCard);
            int liOrientation = Common.getLayoutManagerOrientation(getResources().getConfiguration().orientation);
            final LinearLayoutManager loLayoutManager = new LinearLayoutManager(getContext(), liOrientation, false);
            loRvBankCard.setLayoutManager(loLayoutManager);
            DeviceListAdapter loDeviceListAdapter = new DeviceListAdapter(getContext(), foDeviceList);
            loRvBankCard.setAdapter(loDeviceListAdapter);

            loBtnUpdateCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (moBankCardDialog != null) moBankCardDialog.dismiss();
                    long llUserId = foDeviceList.get(loDeviceListAdapter.getSelectedPosition()).getUserId();
                    proceedDevice(llUserId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedDevice(long flUserId) {
        showProgressDialog();
        moPhoneLoginViewModel.proceedDevice(getContext(), flUserId);
    }
}
