package com.nctucs.csproject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.nctucs.csproject.Activity.AddEventActivity;
import com.nctucs.csproject.Activity.EventsStatusActivity;
import com.nctucs.csproject.Activity.GroupsActivity;
import com.nctucs.csproject.Activity.MainActivity;
import com.nctucs.csproject.Activity.NotificationActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Navigation_BaseActivity extends AppCompatActivity{

    private DrawerLayout DL;
    private FrameLayout FL;
    protected NavigationView NV;
    protected Toolbar toolbar;
    protected int CurrentMenuItem = 0;//紀錄目前User位於哪一個項目
    private DateTime start,end;
    private GoogleSignInAccount mAccount;
    private GoogleAccountCredential mCredential;
    private Boolean done = false;
    private MakeRequestTask requestTask;
    private isLoadDataListener loadLisneter;
    //private final String ACCOUNT = "myaccount";
    public static final String SOCKER_RCV = "ReceiveStr";
    private String data;

    public MyService mService;
    public Boolean mBound;



    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private List<Event> items;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private ImageView iv_user_photo;
    private TextView tv_user_email;
    public Dialog dialog_log_out;
    private GoogleSignInClient mClient;
    Button confirm,cancel;
    private SocketReceiver socketReceiver;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        DL = (DrawerLayout) getLayoutInflater().inflate(R.layout.drawer_layout, null);
        FL = (FrameLayout) DL.findViewById(R.id.content_frame);
        NV = (NavigationView)DL.findViewById(R.id.view_nav);
        toolbar = findViewById(R.id.toolbar);
        getLayoutInflater().inflate(layoutResID, FL, true);
        super.setContentView(DL);
        setUpNavigation();
        View header = NV.getHeaderView(0);
        tv_user_email = header.findViewById(R.id.tv_usr_email);
        iv_user_photo = header.findViewById(R.id.iv_usr_photo);
        tv_user_email.setText(mAccount.getEmail());
        if(InformationHandler.getBitmap() != null)
            iv_user_photo.setImageBitmap(InformationHandler.getBitmap());
        iv_user_photo.setBackgroundResource(R.drawable.usr_photo);
        dialog_log_out = new Dialog(this);
        dialog_log_out.setContentView(R.layout.dialog_log_out);
        confirm = dialog_log_out.findViewById(R.id.btn_confirm);
        cancel = dialog_log_out.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = InformationHandler.getClient();
        mAccount = InformationHandler.getAccount();
        mCredential = InformationHandler.getCredential(this);

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayShowTitleEnabled(false);
    }
    private void setUpNavigation() {
        // Set navigation item selected listener
        NV.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(!(menuItem == NV.getMenu().getItem(CurrentMenuItem))) {//判斷使者者是否點擊當前畫面的項目，若不是，根據所按的項目做出分別的動作
                    switch (menuItem.getItemId()) {
                        case R.id.nav_main:
                            System.out.println("calendar");
                            Intent intent = new Intent();
                            intent.setClass(Navigation_BaseActivity.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                            break;
                        case R.id.nav_events:
                            Intent intent2 = new Intent();
                            intent2.setClass(Navigation_BaseActivity.this, EventsStatusActivity.class);
                            startActivity(intent2);
                            overridePendingTransition(0, 0);
                            finish();
                            break;
                        case R.id.nav_groups:
                            Intent intent3 = new Intent();
                            intent3.setClass(Navigation_BaseActivity.this, GroupsActivity.class);
                            startActivity(intent3);
                            overridePendingTransition(0,0);
                            finish();
                            break;
                        case R.id.nav_notification:
                            Intent intent4 = new Intent();
                            intent4.setClass(Navigation_BaseActivity.this, NotificationActivity.class);
                            startActivity(intent4);
                            overridePendingTransition(0,0);
                            finish();
                            break;
                        case R.id.nav_log_out:
                            dialog_log_out.show();
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mClient.signOut();
                                    dialog_log_out.dismiss();
                                    mService.stopSelf();
                                    finish();
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_log_out.dismiss();
                                }
                            });
                            break;
                    }
                }
                else {//點擊當前項目時，收起Navigation
                    DL.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });

    }

    public class SocketReceiver extends BroadcastReceiver{
        public Boolean notify;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(SOCKER_RCV)){
                 data  = intent.getExtras().getString("Data");
            }

        }
    }





    /****************Get Google Calendar Data*********************/
    public void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        }  else if (! isDeviceOnline()) {
            System.out.println("No network connection available.");
        } else {
            requestTask = new MakeRequestTask(mCredential);
            requestTask.execute();

        }
    }
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                Navigation_BaseActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private com.google.api.services.calendar.Calendar mService = null;
        private Exception
                mLastError = null;
        private HttpTransport transport;


        MakeRequestTask(GoogleAccountCredential credential) {
            transport = AndroidHttp.newCompatibleTransport();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            System.out.println("start = " +  start + " end = " + end);
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(start)
                    .setTimeMax(end)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    System.out.println(start );
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            // mOutputText.setText("");
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            //  mProgress.hide();
            if (loadLisneter != null) {
                loadLisneter.loadComplete();
            }
            if (output == null || output.size() == 0) {
                System.out.println("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Calendar API:");
                System.out.println(TextUtils.join("\n", output));

            }
        }

        @Override
        protected void onCancelled() {
            // mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Navigation_BaseActivity.REQUEST_AUTHORIZATION);
                } else {
                    System.out.println("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                System.out.println("Request cancelled.");
            }
        }




    }
    public void setStartDatetime(Date date){
        start = new DateTime(date);
    }
    public void setEndDatetime(Date date){
        end = new DateTime(date);
    }
    public   List<Event>  getData(){
        return  items.size() == 0 ? null : items;
    }
    public interface isLoadDataListener {
        public void loadComplete();
    }
    public void setLoadDataComplete(isLoadDataListener dataComplete) {
        this.loadLisneter = dataComplete;
    }

    public void clearLoadDataComplete(){
        this.loadLisneter = null;
    }


}
