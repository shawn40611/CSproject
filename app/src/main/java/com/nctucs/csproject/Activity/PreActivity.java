package com.nctucs.csproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.signin.SignIn;
import com.google.api.services.calendar.Calendar;
import com.nctucs.csproject.InformationHandler;

public class PreActivity extends Activity{
    GoogleSignInAccount mAccount;
    GoogleSignInClient mSignInClient;
    GoogleApiClient mApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccount = GoogleSignIn.getLastSignedInAccount(this);
        Intent intent = new Intent();
        if(mAccount == null){
            intent.setClass(PreActivity.this,WelComeActivity.class);
            startActivity(intent);
        }else{
            InformationHandler.setAccount(mAccount);
            intent.setClass(PreActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}
