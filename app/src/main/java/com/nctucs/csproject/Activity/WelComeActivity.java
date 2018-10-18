package com.nctucs.csproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class WelComeActivity extends Activity {


    private GoogleSignInAccount mAccount;
    private int RC_SIGN_IN = 1;
    private SignInButton btn_signin;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btn_tutorial_1_right;
    private Button btn_tutorial_2_left;
    private Button btn_tutorial_2_right;
    private Button btn_tutorial_3_left;
    private ProgressBar mProgressBar;
    private Socket mSocket;
    private int ServerPort = 6666;
    private Boolean Connected = false;
    private GoogleAccountCredential mCredential;
    private Bitmap user_photo;
    public static final String ADDRESS = "178.128.90.63";
    public static final int PORT = 8888;

    private static final String SCOPES ="https://www.googleapis.com/auth/calendar";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switchto1();


    }

    private void switchto1()
    {
        setContentView(R.layout.view_tutorial_1);
        btn_tutorial_1_right = findViewById(R.id.btn_tutorial_1_right);
        btn_tutorial_1_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchto2();
            }
        });


    }

    private void switchto2()
    {
        setContentView(R.layout.view_tutorial_2);
        btn_tutorial_2_right = findViewById(R.id.btn_tutorial_2_right);
        btn_tutorial_2_left = findViewById(R.id.btn_tutorial_2_left);
        btn_tutorial_2_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchto3();
            }
        });
        btn_tutorial_2_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchto1();
            }
        });

    }
    private void switchto3()
    {
        setContentView(R.layout.view_tutorial_3);
        btn_tutorial_3_left = findViewById(R.id.btn_tutorial_3_left);
        btn_tutorial_3_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchto2();
            }
        });
        btn_signin = findViewById(R.id.btn_signin);
        mProgressBar = findViewById(R.id.progressbar);
        setSignInButtonText(btn_signin, "Sign In With GOOGLE");

        // Configure sign-in to request the user's ID, email address, and basic
        // Configure Google Sign In
        String serverClientId = getString(R.string.server_client_id_1);
        System.out.println(serverClientId);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SCOPES))
                .requestServerAuthCode(serverClientId,false)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
           updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    private void updateUI(GoogleSignInAccount account)
    {
        if(account != null)
        {
            //setSignInButtonText(btn_signin,account.getEmail().toString());
            mAccount = account;
            Intent intent = new Intent();
            intent.setClass(WelComeActivity.this,MainActivity.class);
            if(mAccount != null) {
                InformationHandler.setAccount(mAccount);
                mCredential =  GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                        .setSelectedAccount(mAccount.getAccount());
                InformationHandler.setCredential(mCredential);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection connection = (HttpURLConnection) new URL(mAccount.getPhotoUrl().toString()).openConnection();
                            connection.connect();
                            InputStream input = connection.getInputStream();

                            user_photo = BitmapFactory.decodeStream(input);

                        }catch (IOException e){
                            System.out.println(e.getMessage());
                        }
                    }
                });
                Thread connect = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSocket = new Socket(ADDRESS, PORT);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                connect.start();
                t.start();
                while(t.isAlive()||connect.isAlive())
                    ;
                InformationHandler.setBitmap(user_photo);
                InformationHandler.setSocket(mSocket);
            }
            startActivity(intent);
            finish();
        }
        else{
            setSignInButtonText(btn_signin,"Login Fail");
            mProgressBar.setVisibility(View.GONE);
        }
    }


    void setSignInButtonText(SignInButton btn,String button_text){
        for(int i = 0 ; i < btn.getChildCount() ; i++){
            View v = btn.getChildAt(i);
            if(v instanceof TextView){
                TextView tv = (TextView) v;
                tv.setText(button_text);
                return;
            }
        }
    }
}
