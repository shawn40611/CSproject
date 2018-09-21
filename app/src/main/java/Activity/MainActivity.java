package Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nctucs.csproject.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;


public class MainActivity extends Activity {

    MaterialCalendarView materialCalendarView;//布局内的控件
    CalendarDay currentDate;//自定义的日期对象
    int mYear, mMonth,mDay;
    private GoogleSignInAccount mAccount;
    private TextView test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAccount = getIntent().getParcelableExtra("mAccount");
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);// 實例化

        //编辑日历属性
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)   //设置每周开始的第一天
                .setCalendarDisplayMode(CalendarMode.MONTHS)//设置显示模式，可以显示月的模式，也可以显示周的模式
                .commit();// 返回对象并保存
        //      设置点击日期的监听
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull com.prolificinteractive.materialcalendarview.MaterialCalendarView widget, @NonNull com.prolificinteractive.materialcalendarview.CalendarDay date, boolean selected) {
                currentDate = date;
            }
        });

        if(mAccount != null){
            System.out.println(mAccount.getEmail().toString());
        }

    }
    public void getTime(View view) {
        if (currentDate != null) {
            int year = currentDate.getYear();
            int month = currentDate.getMonth() + 1; //月份跟系统一样是从0开始的，实际获取时要加1
            int day = currentDate.getDay();
        } else {
            ;
        }


    }
}
