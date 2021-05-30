package com.cashback.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;

public class PhoneObject {


    int uiState;

    FirebaseUser user;

    PhoneAuthCredential credentials;

    public PhoneObject(int uiState, FirebaseUser user, PhoneAuthCredential credentials) {
        this.uiState = uiState;
        this.user = user;
        this.credentials = credentials;
    }

    public int getUiState() {
        return uiState;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public PhoneAuthCredential getCredentials() {
        return credentials;
    }
}


