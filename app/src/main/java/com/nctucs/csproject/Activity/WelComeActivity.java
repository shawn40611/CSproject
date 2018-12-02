package com.nctucs.csproject.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.nctucs.csproject.JSONGenerator;
import com.nctucs.csproject.MyService;
import com.nctucs.csproject.R;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class WelComeActivity extends FragmentActivity {


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
    private Boolean Connected = false;
    private GoogleAccountCredential mCredential;
    private Bitmap user_photo;
    public static final String ADDRESS = "178.128.90.63";
    public static final int PORT = 8888;
    private static final String SCOPES ="https://www.googleapis.com/auth/calendar";



    private static final int NUM_PAGES = 3;


    private ViewPager mPager;


    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.view_slide);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new WelComeActivity.ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(2);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                ImageView img = (ImageView) findViewById(R.id.dot) ;
                if(position == 0) img.setImageResource(R.drawable.page_1);
                else if(position == 1) img.setImageResource(R.drawable.page_2);
                else {
                    img.setImageResource(R.drawable.page_3);
                    pageThree();
                }
            }
        });


    }



    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SlideFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


    private void pageThree() {
        btn_signin = findViewById(R.id.btn_signin);
        mProgressBar = findViewById(R.id.progressbar);
        setSignInButtonText(btn_signin, "Sign In With GOOGLE");

        // Configure sign-in to request the user's ID, email address, and basic
        // Configure Google Sign In



        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void signIn() {
        String serverClientId = getString(R.string.server_client_id_1);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SCOPES))
                .requestServerAuthCode(serverClientId,true)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
                Intent socketIntent = new Intent();
                socketIntent.setClass(WelComeActivity.this, MyService.class);
                System.out.println("StartService");
                JSONGenerator generator = new JSONGenerator();
                JSONArray data = generator.setUp(mAccount.getServerAuthCode(),mAccount.getEmail());
                socketIntent.putExtra("Set_up",data.toString());
                startService(socketIntent);
                InformationHandler.setAccount(mAccount);
                mCredential =  GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                        .setSelectedAccount(mAccount.getAccount());
                InformationHandler.setCredential(mCredential);
                InformationHandler.setClient(mGoogleSignInClient);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(mAccount.getPhotoUrl() != null) {
                                HttpURLConnection connection = (HttpURLConnection) new URL(mAccount.getPhotoUrl().toString()).openConnection();
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
