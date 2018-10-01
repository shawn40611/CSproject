package Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nctucs.csproject.R;

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
        String serverClientId = getString(R.string.server_client_id);
        System.out.println(serverClientId);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId)
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
                System.out.println("have account" + mAccount.getEmail().toString());
                Bundle bundle = new Bundle();
                bundle.putParcelable("mAccount", mAccount);
                intent.putExtras(bundle);
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
