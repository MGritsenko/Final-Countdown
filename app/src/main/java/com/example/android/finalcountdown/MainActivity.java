package com.example.android.finalcountdown;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.leakcanary.LeakCanary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

////////////////////////////////////////////////////////////////////////////////////////////////////
//MainActivity
////////////////////////////////////////////////////////////////////////////////////////////////////

public class MainActivity extends AppCompatActivity
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ScrollView scrollView;

    Button setNewCountDownBtn;
    Button amPm;

    ListView scrollableTextView;
    TextView resultTime;
    TextView resultDate;

    CheckBox timeBox;
    CheckBox dateBox;

    EditText hoursEdit;
    EditText minutesEdit;
    EditText secondsEdit;

    EditText daysEdit;
    EditText monthsEdit;
    EditText yearsEdit;

    CustomDate targetDate = new CustomDate();
    CustomDate timeRemaining = new CustomDate();
    Date date = new Date();
    CustomDate tmp = new CustomDate();
    List<String> list = new ArrayList<>();
    ListAdapter listAdapter = new ListAdapter();

    Handler myHandler = new Handler();
    Runnable updateTask;

    String day = "18";
    String month = "4";
    String year = "2016";

    String hours = "19";
    String minutes = "0";
    String seconds = "0";

    DataBaseHelperClass myDb;

    boolean ampm = false;
    boolean english = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //This method saves a state of the app before a screen orientation changed.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean("hours", hoursEdit.isEnabled());
        outState.putBoolean("minutes", minutesEdit.isEnabled());
        outState.putBoolean("seconds", secondsEdit.isEnabled());

        outState.putBoolean("days", daysEdit.isEnabled());
        outState.putBoolean("months", monthsEdit.isEnabled());
        outState.putBoolean("years", yearsEdit.isEnabled());

        outState.putBoolean("button", setNewCountDownBtn.isEnabled());
        outState.putBoolean("buttonAmPm", ampm);
        outState.putBoolean("amPmButtonIsActivated", amPm.isEnabled());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //On destroy method
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        myHandler.removeCallbacks(updateTask);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //LeakCanary
    ////////////////////////////////////////////////////////////////////////////////////////////////
//    public static RefWatcher getRefWatcher(Context context)
//    {
//        MainActivity application = (MainActivity) context.getApplicationContext();
//        return application.refWatcher;
//    }

    //private RefWatcher refWatcher;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Main method that runs after the app was launched.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //refWatcher = LeakCanary.install(this.getApplication());
        //LeakCanary.install(this.getApplication());

        myDb = new DataBaseHelperClass(this);

        setNewCountDownBtn = (Button) findViewById(R.id.setNewCountDownButton);
        amPm = (Button) findViewById(R.id.ampm);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        scrollableTextView = (ListView) findViewById(R.id.scrollableTextView);
        resultTime = (TextView) findViewById(R.id.resultTime);
        resultDate = (TextView) findViewById(R.id.resultDate);

        timeBox = (CheckBox) findViewById(R.id.timeBox);
        dateBox = (CheckBox) findViewById(R.id.dateBox);

        hoursEdit = (EditText) findViewById(R.id.hoursEdit);
        minutesEdit = (EditText) findViewById(R.id.minutesEdit);
        secondsEdit = (EditText) findViewById(R.id.secondsEdit);

        daysEdit = (EditText) findViewById(R.id.daysEdit);
        monthsEdit = (EditText) findViewById(R.id.monthsEdit);
        yearsEdit = (EditText) findViewById(R.id.yearsEdit);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //setOnTouchListener called when a touch event is dispatched to a view. This allows
        //listeners to get a chance to respond before the target view.
        //
        //When a user touches the scrollableTextView, scrollView.setOnTouchListener emits and
        //prevents to scroll all the main view. After that emits scrollableTextView.setOnTouchListener
        //and takes scrolling handling on his own.
        ////////////////////////////////////////////////////////////////////////////////////////////
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
               view.findViewById(R.id.scrollableTextView).getParent()
                        .requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        scrollableTextView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent arg1)
            {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Loads up previously saved info about the data was entered.
        ////////////////////////////////////////////////////////////////////////////////////////////
        String locale = Locale.getDefault().getDisplayLanguage();   //getResources().getConfiguration().locale.toString();

        if(!locale.equals("English")) //en_US
        {
            english = false; //not a russian locale

            View b = amPm;
            b.setVisibility(View.GONE);
        }
        else
            english = true;

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Loads up previously saved info about the data was entered.
        ////////////////////////////////////////////////////////////////////////////////////////////
        ReadMessage();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Sets new target date.
        ////////////////////////////////////////////////////////////////////////////////////////////
        DisplayNewDate();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Sets time field enabled.
        ////////////////////////////////////////////////////////////////////////////////////////////
        SetTimeEnabled();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Sets date field enabled.
        ////////////////////////////////////////////////////////////////////////////////////////////
        SetDateEnabled();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Sets button "SET NEW DATE"  enabled.
        ////////////////////////////////////////////////////////////////////////////////////////////
        SetButtonEnabled();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Restores saved settings if savedInstanceState != null.
        ////////////////////////////////////////////////////////////////////////////////////////////
        if(savedInstanceState != null)
        {
            hoursEdit.setEnabled(savedInstanceState.getBoolean("hours"));
            minutesEdit.setEnabled(savedInstanceState.getBoolean("minutes"));
            secondsEdit.setEnabled(savedInstanceState.getBoolean("seconds"));

            daysEdit.setEnabled(savedInstanceState.getBoolean("days"));
            monthsEdit.setEnabled(savedInstanceState.getBoolean("months"));
            yearsEdit.setEnabled(savedInstanceState.getBoolean("years"));

            setNewCountDownBtn.setEnabled(savedInstanceState.getBoolean("button"));

            ampm = savedInstanceState.getBoolean("buttonAmPm");
            amPm.setEnabled(savedInstanceState.getBoolean("amPmButtonIsActivated"));
            amPm();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Main loop that updates every second and shows remaining time to a target date.
        ////////////////////////////////////////////////////////////////////////////////////////////
        UpdateCountDown();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets new target date on to the screen.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void DisplayNewDate()
    {
        targetDate.setDate(Integer.parseInt(hours), Integer.parseInt(minutes), Integer.parseInt(seconds),
                Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year));

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dd_mm_yy), Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        cal.set(targetDate.getYear(), targetDate.getMonth() , targetDate.getDay(), targetDate.getHours(), targetDate.getMinutes(), targetDate.getSeconds());

        date.setTime(cal.getTimeInMillis());

        resultTime.setText(dateFormat.format(date));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Saves the date was entered.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void WriteMessage()
    {
        if(!targetDate.setDate(Integer.parseInt(hours), Integer.parseInt(minutes), Integer.parseInt(seconds),
                Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year)))
        {
            DisplayError(R.string.incorrect_date);

            return;
        }

        boolean isInserted =  myDb.InsertData(targetDate);

        if(isInserted)
            DisplayError(R.string.inserted);
        else
            DisplayError(R.string.not_inserted);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Loads up previously saved info about the data was entered.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void ReadMessage()
    {
        Cursor res = myDb.GetAllData();

        if(res.getCount() == 0)
        {
            return;//show mess
        }

        list.clear();

        while (res.moveToNext())
        {
            tmp.setDate(res.getInt(1), res.getInt(2), res.getInt(3), res.getInt(4), res.getInt(5), res.getInt(6));

            DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dd_mm_yy), Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.set(tmp.getYear(), tmp.getMonth() , tmp.getDay(), tmp.getHours(), tmp.getMinutes(), tmp.getSeconds());
            date.setTime(cal.getTimeInMillis());
            resultTime.setText(dateFormat.format(date));

            list.add(dateFormat.format(date));
        }

        hours = Integer.toString(tmp.getHours());
        minutes = Integer.toString(tmp.getMinutes());
        seconds = Integer.toString(tmp.getSeconds());
        day = Integer.toString(tmp.getDay());
        month = Integer.toString(tmp.getMonth());
        year = Integer.toString(tmp.getYear());

        listAdapter.setListAdapter(this, list);
        scrollableTextView.setAdapter(listAdapter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Main method that calculates the time to go.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void GetDateTime()
    {
        timeRemaining.setDate(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),
                              Calendar.getInstance().get(Calendar.SECOND), Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                              Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR));

        int res = CustomDate.whichIsBigger(timeRemaining, targetDate);
        String str;

        if(res == -1)
            str = CustomDate.calculateElapsedTime(this.getApplicationContext(), timeRemaining, targetDate);
        else if(res == 1)
            str = CustomDate.calculateElapsedTime(this.getApplicationContext(), targetDate, timeRemaining);
        else
            str = CustomDate.calculateElapsedTime(this.getApplicationContext(), timeRemaining, targetDate);

        resultDate.setText(str);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Main loop that updates every second and shows remaining time.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void UpdateCountDown()
    {
        updateTask = new Runnable()
        {
            @Override
            public void run()
            {
                GetDateTime();
                myHandler.postDelayed(this,1000);
            }
        };

        myHandler.postDelayed(updateTask,1000);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets time field enabled.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void SetTimeEnabled()
    {
        if(timeBox.isChecked())
        {
            hoursEdit.setEnabled(true);
            minutesEdit.setEnabled(true);
            secondsEdit.setEnabled(true);

            amPm.setEnabled(true);
        }
        else
        {
            hoursEdit.setEnabled(false);
            minutesEdit.setEnabled(false);
            secondsEdit.setEnabled(false);

            amPm.setEnabled(false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets date field enabled.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void SetDateEnabled()
    {
        if(dateBox.isChecked())
        {
            daysEdit.setEnabled(true);
            monthsEdit.setEnabled(true);
            yearsEdit.setEnabled(true);
        }
        else
        {
            daysEdit.setEnabled(false);
            monthsEdit.setEnabled(false);
            yearsEdit.setEnabled(false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets the "SET NEW COUNTDOWN" button enabled.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void SetButtonEnabled()
    {
        if(!timeBox.isChecked() && !dateBox.isChecked())
            setNewCountDownBtn.setEnabled(false);
        else
            setNewCountDownBtn.setEnabled(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets checkbox Time enabled
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetTimeEnabled(View view)
    {
        SetTimeEnabled();
        SetButtonEnabled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets checkbox Date enabled
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetDateEnabled(View view)
    {
        SetDateEnabled();
        SetButtonEnabled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Gets a data from "Set date" fields
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean SetNewDate()
    {
        String strDays = daysEdit.getText().toString();
        String strMonths = monthsEdit.getText().toString();
        String strYears = yearsEdit.getText().toString();

        if(TextUtils.isEmpty(strDays))
        {
            DisplayError(R.string.error);

            return false;
        }

        if(Long.parseLong(strDays) < 1)
        {
            DisplayError(R.string.error_d_min);

            return false;
        }

        if(Long.parseLong(strDays) > 31)
        {
            DisplayError(R.string.error_d_max);

            return false;
        }

        if(TextUtils.isEmpty(strMonths))
        {
            DisplayError(R.string.error);

            return false;
        }

        if(Long.parseLong(strMonths) < 1)
        {
            DisplayError(R.string.error_mnth_min);

            return false;
        }

        if(Long.parseLong(strMonths) > 12)
        {
            DisplayError(R.string.error_mnth_max);

            return false;
        }

        if(TextUtils.isEmpty(strYears))
        {
            DisplayError(R.string.error);

            return false;
        }

        if(Long.parseLong(strYears) < 1)
        {
            DisplayError(R.string.error_ymin);

            return false;
        }

        if(Long.parseLong(strYears) > 6000)
        {
            DisplayError(R.string.error_ymax);

            return false;
        }

        day = daysEdit.getText().toString();

        int m = Integer.parseInt(monthsEdit.getText().toString()) - 1;
        month = String.valueOf(m);

        year = yearsEdit.getText().toString();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Gets a data from "Set time" fields
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean SetNewTime()
    {
        String strHours = hoursEdit.getText().toString();
        String strMinutes = minutesEdit.getText().toString();
        String strSeconds = secondsEdit.getText().toString();

        if(TextUtils.isEmpty(strHours))
        {
            DisplayError(R.string.error);

            return false;
        }

        if(Long.parseLong(strHours) > 12 && english)
        {
            DisplayError(R.string.error_h_max);

            return false;
        }

        if(Long.parseLong(strHours) < 1 && english)
        {
            DisplayError(R.string.error_h_min);

            return false;
        }

        if(Long.parseLong(strHours) > 23)
        {
            DisplayError(R.string.error_h_max);

            return false;
        }

        if(TextUtils.isEmpty(strMinutes))
        {
            DisplayError(R.string.error);

            return false;
        }

        if(Long.parseLong(strMinutes) > 59)
        {
            DisplayError(R.string.error_m);

            return false;
        }

        if(TextUtils.isEmpty(strSeconds))
        {
            DisplayError(R.string.error);

            return false;
        }

        if(Long.parseLong(strSeconds) > 59)
        {
            DisplayError(R.string.error_s);

            return false;
        }

        if(english && ampm)         //if ampm == PM
            hours = convertToPm(Integer.parseInt(hoursEdit.getText().toString()));
        else if(english && !ampm)   //if ampm == AM
            hours = convertToAm(Integer.parseInt(hoursEdit.getText().toString()));
        else
            hours = hoursEdit.getText().toString();

        minutes = minutesEdit.getText().toString();
        seconds = secondsEdit.getText().toString();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Dislplay an error
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void DisplayError(int str)
    {
        Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //On click method "SET NEW COUNTDOWN"
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetNewCountDown(View view)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(this.getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(setNewCountDownBtn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        if(timeBox.isChecked())
        {
            if(!SetNewTime())
                return;
        }

        if(dateBox.isChecked())
        {
            if(!SetNewDate())
                return;
        }

        WriteMessage();  //saves the date was entered
        ReadMessage();
        DisplayNewDate();  //shows a new date on the screen
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //On click method "EM@IL ME"
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SendEmail(View view)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.parse("mailto:"));
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mihail.gritsenko@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
        intent.putExtra(Intent.EXTRA_TEXT, resultDate.getText());

        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //On click method amPm, chnges the label of the amPm button from AM to PM
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void amPm(View view)
    {
        if(!ampm)
        {
            ampm = true;

            amPm.setText(getString(R.string.pm));
        }
        else
        {
            ampm = false;

            amPm.setText(getString(R.string.am));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //onSaveInstance helper method to restore amPm button settings
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void amPm()
    {
        if(!ampm)
            amPm.setText(getString(R.string.am));
        else
            amPm.setText(getString(R.string.pm));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //converts from 24h to 12h format
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String convertToPm(int hours)
    {
        switch (hours)
        {
            case 1:
                return "13";
            case 2:
                return "14";
            case 3:
                return "15";
            case 4:
                return "16";
            case 5:
                return "17";
            case 6:
                return "18";
            case 7:
                return "19";
            case 8:
                return "20";
            case 9:
                return "21";
            case 10:
                return "22";
            case 11:
                return "23";
            case 12:
                return "12";
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //converts from 24h to 12h format
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String convertToAm(int hours)
    {
        switch (hours)
        {
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "10";
            case 11:
                return "11";
            case 12:
                return "0";
        }

        return null;
    }
}
