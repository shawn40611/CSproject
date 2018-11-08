package com.nctucs.csproject.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;


import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

public class AddEventActivity extends Navigation_BaseActivity {
    private Spinner spnHr, spnMin;
    private int hr = -1, min = -1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        CurrentMenuItem = 1;

        toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);
        spnHr = findViewById(R.id.add_event_hr);
        spnMin = findViewById(R.id.add_event_min);

       /* ArrayAdapter<CharSequence> arrAdapHr = ArrayAdapter.createFromResource(this,
                R.array.add_event_hr,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> arrAdapMin = ArrayAdapter.createFromResource(this,
                R.array.add_event_min,
                android.R.layout.simple_spinner_item);*/

        /*arrAdapHr.setDropDownViewResource(R.layout.spinner_dropdown);
        arrAdapMin.setDropDownViewResource(R.layout.spinner_dropdown);

        spnHr.setAdapter(arrAdapHr);
        spnMin.setAdapter(arrAdapMin);

        spnHr.setOnItemSelectedListener(spnHrSelected);
        spnMin.setOnItemSelectedListener(spnMinSelected);*/

    }
    private AdapterView.OnItemSelectedListener spnHrSelected
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            hr = i;
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
    private AdapterView.OnItemSelectedListener spnMinSelected
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            min = i;
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };


    public void btn(View v){
        EditText etAddEventName = findViewById(R.id.et_add_event_name);
        String name = etAddEventName.getText().toString();
        Log.d("name", name);
        Log.d("hr", String.valueOf(hr));
        Log.d("min", String.valueOf(min * 15));
    }

}
