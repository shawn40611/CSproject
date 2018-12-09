package com.nctucs.csproject;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Use this class to login with google account using the OpenId oauth method.
 */
public class SilentLogin {

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private Account mAccount;
    private static final String SCOPES ="https://www.googleapis.com/auth/calendar";
    private isLoginCompleteListener listener;
    private Boolean loginSuccess = false;
    private Bitmap user_photo;


    public SilentLogin(Context appContext,Account account) {
        this.mContext = appContext;
        mAccount = account;
        createGoogleClient();
    }



    private void createGoogleClient() {
        GoogleSignInOptions gso;
        if(mAccount != null) {
            String serverClientId = mContext.getResources().getString(R.string.server_client_id_1);
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .setAccount(mAccount)
                    .requestScopes(new Scope(SCOPES))
                    .requestServerAuthCode(serverClientId, false)
                    .requestEmail()
                    .build();
        }else{
            String serverClientId = mContext.getResources().getString(R.string.server_client_id_1);
           gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(SCOPES))
                    .requestServerAuthCode(serverClientId, false)
                    .requestEmail()
                    .build();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signOut(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionResult result  = mGoogleApiClient.blockingConnect();
                if(result.isSuccess())
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }
        }).start();

    }

    public void signIn(){
            new Thread(new Runnable() { public void run() {
                String serverClientId = mContext.getResources().getString(R.string.server_client_id_1);
                ConnectionResult result = mGoogleApiClient.blockingConnect();
                if(result.isSuccess()) {

                    OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn
                            (mGoogleApiClient);
                    GoogleSignInResult googleSignInResult = opr.await();
                    System.out.println("1 ,"+googleSignInResult.getStatus());
                   onSilentLoginFinished(googleSignInResult);
                }}}).start();
        }

    private void onSilentLoginFinished(GoogleSignInResult signInResult) {
        if (signInResult != null) {
            System.out.println(signInResult.getStatus());
            final GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
            if (signInAccount != null) {
                InformationHandler.setAccount(signInAccount);
                String emailAddress = signInAccount.getEmail();
                String auth = signInAccount.getServerAuthCode();
                System.out.println("auth = " + auth);
                System.out.println("emailAddress = " + emailAddress);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(signInAccount.getPhotoUrl() != null) {
                                HttpURLConnection connection = (HttpURLConnection) new URL(signInAccount.getPhotoUrl().toString()).openConnection();
                                connection.connect();
                                InputStream input = connection.getInputStream();

                                user_photo = BitmapFactory.decodeStream(input);
                            }

                        }catch (IOException e){
                            System.out.println(e.getMessage());
                        }
                    }
                });

                t.start();
                while(t.isAlive())
                    ;
                InformationHandler.setBitmap(user_photo);
                loginSuccess = true;
            }
        }
        if(listener != null){
            listener.loginComplete();
        }
    }

    public Boolean isLoginSuccess() {
        return loginSuccess;
    }

    public interface  isLoginCompleteListener {
        public void loginComplete();
    }
    public void setLoginListener(isLoginCompleteListener loginListener){
        this.listener = loginListener;
    }

}