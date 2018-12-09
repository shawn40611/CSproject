package com.nctucs.csproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.JSONGenerator;
import com.nctucs.csproject.MyService;
import com.nctucs.csproject.R;
import com.nctucs.csproject.SilentLogin;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PreActivity extends Activity {
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount mAccount;
    private static final String SCOPES ="https://www.googleapis.com/auth/calendar";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.holo_Launcher);
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent();
        mAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(mAccount != null) {
            final SilentLogin login = new SilentLogin(getApplicationContext(), mAccount.getAccount());
            login.signIn();
            login.setLoginListener(new SilentLogin.isLoginCompleteListener() {
                @Override
                public void loginComplete() {
                    if (login.isLoginSuccess()) {
                        Intent socketintent = new Intent();
                        Intent socketIntent = new Intent();
                        socketIntent.setClass(PreActivity.this, MyService.class);
                        JSONGenerator generator = new JSONGenerator();
                        final GoogleSignInAccount account = InformationHandler.getAccount();
                        JSONArray data = generator.setUp(account.getServerAuthCode(),account.getEmail());
                        socketIntent.putExtra("Set_up",data.toString());
                        startService(socketIntent);
                        intent.setClass(PreActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        intent.setClass(PreActivity.this, WelComeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }else{
            intent.setClass(PreActivity.this, WelComeActivity.class);
            startActivity(intent);
        }
    }



}
