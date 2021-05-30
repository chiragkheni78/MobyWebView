package com.cashback.models.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.activities.PhoneLoginActivity;
import com.cashback.models.PhoneObject;
import com.cashback.models.request.MobileDeviceRequest;
import com.cashback.models.request.ProceedDeviceRequest;
import com.cashback.models.response.ActivityListResponse;
import com.cashback.models.response.MobileDeviceResponse;
import com.cashback.models.response.ProceedDeviceResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cashback.activities.PhoneLoginActivity.STATE_CODE_SENT;
import static com.cashback.activities.PhoneLoginActivity.STATE_INITIALIZED;
import static com.cashback.activities.PhoneLoginActivity.STATE_SIGNIN_FAILED;
import static com.cashback.activities.PhoneLoginActivity.STATE_SIGNIN_SUCCESS;
import static com.cashback.activities.PhoneLoginActivity.STATE_VERIFY_SUCCESS;


public class PhoneLoginViewModel extends ViewModel {

    private static final String TAG = PhoneLoginViewModel.class.getSimpleName();
    public MutableLiveData<PhoneObject> phoneVerificationStatus = new MutableLiveData<>();
    public MutableLiveData<MobileDeviceResponse> fetchConnectedDeviceStatus = new MutableLiveData<>();
    public MutableLiveData<ProceedDeviceResponse> proceedDeviceStatus = new MutableLiveData<>();

    FirebaseAuth moAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public void initPhoneVerification(Activity foActivity) {
        // [START auth_test_phone_verify]
//        String phoneNum = "+16505554567";
//        String testVerificationCode = "123456";

        moAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);

                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(foActivity, credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later

                mVerificationId = verificationId;
                mResendToken = token;

                updateUI(STATE_CODE_SENT);
            }
        };
    }

    // [START sign_in_with_phone]
    public void signInWithPhoneAuthCredential(Activity foActivity, PhoneAuthCredential credential) {
        moAuth.signInWithCredential(credential)
                .addOnCompleteListener(foActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            LogV2.i(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Snackbar.make(foActivity.findViewById(android.R.id.content), task.getException().getMessage(),
                                        Snackbar.LENGTH_SHORT).show();
                            }

                            updateUI(STATE_SIGNIN_FAILED);
                        }
                    }
                });
    }

    public void startPhoneNumberVerification(Activity foActivity, String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(moAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(foActivity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    public void verifyPhoneNumberWithCode(Activity foActivity, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(foActivity, credential);
    }

    // [START resend_verification]
    public void resendVerificationCode(Activity foActivity, String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(moAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(foActivity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(mResendToken)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    public FirebaseUser getCurrentUser(){
        if (moAuth != null) {
            FirebaseUser currentUser = moAuth.getCurrentUser();
            return currentUser;
        } return null;
    }

    public void signOut() {
        moAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState) {
        //updateUI(uiState, getCurrentUser(), null);
        phoneVerificationStatus.postValue(new PhoneObject(uiState, getCurrentUser(), null));
    }

    private void updateUI(int uiState, FirebaseUser user) {
        //updateUI(uiState, user, null);
        phoneVerificationStatus.postValue(new PhoneObject(uiState, user, null));
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
//        updateUI(uiState, null, cred);
        phoneVerificationStatus.postValue(new PhoneObject(uiState, null, cred));
    }


    public void getConnectedDeviceList(Context foContext) {
        MobileDeviceRequest loMobileDeviceRequest = new MobileDeviceRequest();
        loMobileDeviceRequest.setAction(Constants.API.CHECK_CONNECTED_DEVICE.getValue());
        loMobileDeviceRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loMobileDeviceRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loMobileDeviceRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchConnectedDeviceStatus.postValue(new MobileDeviceResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<MobileDeviceResponse> loRequest = APIClient.getInterface().getConnectedDeviceList(loMobileDeviceRequest);
        Common.printReqRes(loRequest, "getConnectedDeviceList", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<MobileDeviceResponse>() {
            @Override
            public void onResponse(Call<MobileDeviceResponse> call, Response<MobileDeviceResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getConnectedDeviceList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    MobileDeviceResponse loJsonObject = foResponse.body();
                    if (loJsonObject.getDevicesList() != null) {
                        if (loJsonObject.getDevicesList().size() == 1) {
                            //call another API
                            proceedDevice(foContext, loJsonObject.getDevicesList().get(0).getUserId());
                        }
                    }
                    fetchConnectedDeviceStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchConnectedDeviceStatus.postValue(new MobileDeviceResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<MobileDeviceResponse> call, Throwable t) {
                Common.printReqRes(t, "getConnectedDeviceList", Common.LogType.ERROR);
                fetchConnectedDeviceStatus.postValue(new MobileDeviceResponse(true, t.getMessage()));
            }
        });
    }

    public void proceedDevice(Context foContext, long flUserID) {
        ProceedDeviceRequest loProceedDeviceRequest = new ProceedDeviceRequest();
        loProceedDeviceRequest.setAction(Constants.API.PROCEED_DEVICE.getValue());
        loProceedDeviceRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loProceedDeviceRequest.setMobileNumber(AppGlobal.getPhoneNumber());
        loProceedDeviceRequest.setUserID(flUserID);

        String lsMessage = loProceedDeviceRequest.validateData(foContext);
        if (lsMessage != null) {
            proceedDeviceStatus.postValue(new ProceedDeviceResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<ProceedDeviceResponse> loRequest = APIClient.getInterface().proceedDevice(loProceedDeviceRequest);
        Common.printReqRes(loRequest, "proceedDevice", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<ProceedDeviceResponse>() {
            @Override
            public void onResponse(Call<ProceedDeviceResponse> call, Response<ProceedDeviceResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "proceedDevice", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    ProceedDeviceResponse loJsonObject = foResponse.body();
                    proceedDeviceStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    proceedDeviceStatus.postValue(new ProceedDeviceResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<ProceedDeviceResponse> call, Throwable t) {
                Common.printReqRes(t, "proceedDevice", Common.LogType.ERROR);
                proceedDeviceStatus.postValue(new ProceedDeviceResponse(true, t.getMessage()));
            }
        });
    }
}
