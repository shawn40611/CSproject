package com.nctucs.csproject.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.nctucs.csproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class TestActivity extends Activity implements View.OnFocusChangeListener {

    private static  String APPLICATION_NAME="";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    //private static final String TOKENS_DIRECTORY_PATH = "tokens";


    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "com.credentials.json";
    private GoogleSignInAccount mAccount;
    private Dialog test_dialog;
    private EditText et1, et2, et3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Button btn = findViewById(R.id.test_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEvent();
            }
        });

        mAccount = getIntent().getParcelableExtra("mAccount");
        APPLICATION_NAME = getString(R.string.app_name);
        /*try {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            //Calendar service = new Calendar.Builder(transport, JSON_FACTORY, getCredentials(transport,mAccount,TestActivity.this))
                    //.setApplicationName(APPLICATION_NAME)
                    //.build();

            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            //Events events = service.events().list("primary")
              //      .setMaxResults(10)
                //    .setTimeMin(now)
                  //  .setOrderBy("startTime")
                    //.setSingleEvents(true)
                    //.execute();
            //List<Event> items = events.getItems();
            if (items.isEmpty()) {
                System.out.println("No upcoming events found.");
            } else {
                System.out.println("Upcoming events");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    System.out.printf("%s (%s)\n", event.getSummary(), start);
                    TextView tv = findViewById(R.id.tv_test);
                    tv.setText(event.getSummary());
                }
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }*/

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }





    public void showAddEvent(){

        test_dialog = new Dialog(this);
        test_dialog.setContentView(R.layout.dialog_test);
        test_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        test_dialog.show();
        et1 = test_dialog.findViewById(R.id.test_et_add_name);
        et2 = test_dialog.findViewById(R.id.test_et_add_description);
        et3 = test_dialog.findViewById(R.id.test_et_add_location);

        et1.setOnFocusChangeListener(this);
        et2.setOnFocusChangeListener(this);
        et3.setOnFocusChangeListener(this);


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v.getId() != R.id.test_et_add_name) Log.d("test!", "not et1");
        else Log.d("testtt", "et1");
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }

    /*private static Credential getCredentials(HttpTransport transport, GoogleSignInAccount mAccount, Context mcontext) throws IOException {
        // Load client secrets.
       // InputStream in = mcontext.getResources().openRawResource(R.raw.credentials);
        System.out.println(in != null);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, JSON_FACTORY, clientSecrets, SCOPES)
                //.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(mAccount.getId());
    }
*/
}
