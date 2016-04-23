package com.example.android.finalcountdown;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //MainActivity
    ////////////////////////////////////////////////////////////////////////////////////////////////

public class MainActivity extends AppCompatActivity {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ScrollView scrollView;

    Button setNewCountDownBtn;

    TextView scrollableTextView;
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

    Calendar timeRemaining;
    Calendar targetDate;

    long millis = 0;

    boolean runHandler = true;
    Handler myHandler = new Handler();

    String day = "24";
    String month = "4";
    String year = "2016";

    String hours = "23";
    String minutes = "59";
    String seconds = "59";

    String fileName = "data_time";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //This method saves a state of the app before a screen orientation changed.
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("hours", hoursEdit.isEnabled());
        outState.putBoolean("minutes", minutesEdit.isEnabled());
        outState.putBoolean("seconds", secondsEdit.isEnabled());

        outState.putBoolean("days", daysEdit.isEnabled());
        outState.putBoolean("months", monthsEdit.isEnabled());
        outState.putBoolean("years", yearsEdit.isEnabled());

        outState.putBoolean("button", setNewCountDownBtn.isEnabled());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Main method that runs after the app was launched.
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNewCountDownBtn = (Button) findViewById(R.id.setNewCountDownButton);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        scrollableTextView = (TextView) findViewById(R.id.scrollableTextView);
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

        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
               view.findViewById(R.id.scrollableTextView).getParent()
                        .requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        scrollableTextView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent arg1) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Sets the movement method that interprets movement keys by scrolling the text buffer.
        ////////////////////////////////////////////////////////////////////////////////////////////
        scrollableTextView.setMovementMethod(new ScrollingMovementMethod());

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Sets new target date.
        ////////////////////////////////////////////////////////////////////////////////////////////
        DisplayNewDate();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Loads up previously saved info about the data was entered.
        ////////////////////////////////////////////////////////////////////////////////////////////
        ReadMessage();

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
        if(savedInstanceState != null){
            hoursEdit.setEnabled(savedInstanceState.getBoolean("hours"));
            minutesEdit.setEnabled(savedInstanceState.getBoolean("minutes"));
            secondsEdit.setEnabled(savedInstanceState.getBoolean("seconds"));

            daysEdit.setEnabled(savedInstanceState.getBoolean("days"));
            monthsEdit.setEnabled(savedInstanceState.getBoolean("months"));
            yearsEdit.setEnabled(savedInstanceState.getBoolean("years"));

            setNewCountDownBtn.setEnabled(savedInstanceState.getBoolean("button"));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Main loop that updates every second and shows remaining time to a target date.
        ////////////////////////////////////////////////////////////////////////////////////////////
        UpdateCountDown();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets new target date on to the screen.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void DisplayNewDate(){

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dd_mm_yy), Locale.getDefault());
        Date date = new Date();

        targetDate = Calendar.getInstance();
        targetDate.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day),
                Integer.parseInt(hours), Integer.parseInt(minutes), Integer.parseInt(seconds));

        date.setTime(targetDate.getTimeInMillis());
        resultTime.setText(dateFormat.format(date));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Saves the date was entered.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void WriteMessage(){
        String message = Long.toString(targetDate.getTimeInMillis()) + "\n";

        try {
            FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_APPEND);

            fileOutputStream.write(message.getBytes());
            fileOutputStream.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Loads up previously saved info about the data was entered.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void ReadMessage(){
        try {
            FileInputStream fileInputStream = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String message;
            String buf = "";
            String tmp = null;

            DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dd_mm_yy), Locale.getDefault());
            Date date = new Date();

            while((message = bufferedReader.readLine()) != null){
                tmp = message;

                long time = Long.parseLong(message.replaceAll("[\\D]",""));

                date.setTime(time);
                buf += dateFormat.format(date) + "\n";
            }

            if(tmp != null) {
                long time;
                time = Long.parseLong(tmp.replaceAll("[\\D]",""));

                date.setTime(time);
                resultTime.setText(dateFormat.format(date));

                targetDate = Calendar.getInstance();
                targetDate.setTime(date);

                scrollableTextView.setText(buf);

                return;
            }

            DisplayNewDate(); //this one calls only the first time app is launched

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Main method that calculates the time to go.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void GetDateTime() {

        Date date = new Date();

        timeRemaining = Calendar.getInstance();
        date.setTime(timeRemaining.getTimeInMillis());

        millis = Math.abs(timeRemaining.getTimeInMillis() - targetDate.getTimeInMillis());

        int scnds = (int) (millis / 1000) % 60 ;
        int mnts = (int) ((millis / (1000 * 60)) % 60);
        int hrs = (int) ((millis / (1000 * 60 * 60)) % 24);
        int dys = (int) ((millis / (1000 * 60 * 60 * 24)));

        resultDate.setText(getString(R.string.formating, dys, hrs, mnts, scnds));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Main loop that updates every second and shows remaining time.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void UpdateCountDown(){

        new Thread(new Runnable(){  //making a new thread

            @Override
            public void run(){
                while (runHandler){
                    try{
                        Thread.sleep(500);
                        myHandler.post(new Runnable(){  //myHandler manages tasks in UI thread

                            @Override
                            public void run(){
                                GetDateTime();
                            }
                        });
                    }
                    catch (Exception exception){
                        exception.printStackTrace();
                    }
                }
            }
        }).start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets time field enabled.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void SetTimeEnabled(){
        if(timeBox.isChecked()){
            hoursEdit.setEnabled(true);
            minutesEdit.setEnabled(true);
            secondsEdit.setEnabled(true);
        }
        else{
            hoursEdit.setEnabled(false);
            minutesEdit.setEnabled(false);
            secondsEdit.setEnabled(false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets date field enabled.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void SetDateEnabled(){
        if(dateBox.isChecked()){
            daysEdit.setEnabled(true);
            monthsEdit.setEnabled(true);
            yearsEdit.setEnabled(true);
        }
        else{
            daysEdit.setEnabled(false);
            monthsEdit.setEnabled(false);
            yearsEdit.setEnabled(false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets the "SET NEW COUNTDOWN" button enabled.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void SetButtonEnabled(){
        if(!timeBox.isChecked() && !dateBox.isChecked()){
            setNewCountDownBtn.setEnabled(false);
        }
        else{
            setNewCountDownBtn.setEnabled(true);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets checkbox Time enabled
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetTimeEnabled(View view){
        SetTimeEnabled();
        SetButtonEnabled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets checkbox Date enabled
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetDateEnabled(View view){
        SetDateEnabled();
        SetButtonEnabled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Gets a data from "Set date" fields
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean SetNewDate(){
        String strDays = daysEdit.getText().toString();
        String strMonths = monthsEdit.getText().toString();
        String strYears = yearsEdit.getText().toString();

        if(TextUtils.isEmpty(strDays)) {
            daysEdit.setError(getString(R.string.error));
            return false;
        }

        if(TextUtils.isEmpty(strMonths)) {
            monthsEdit.setError(getString(R.string.error));
            return false;
        }

        if(TextUtils.isEmpty(strYears)) {
            yearsEdit.setError(getString(R.string.error));
            return false;
        }

        day = daysEdit.getText().toString();
        month = monthsEdit.getText().toString();
        year = yearsEdit.getText().toString();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Gets a data from "Set time" fields
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean SetNewTime(){
        String strHours = hoursEdit.getText().toString();
        String strMinutes = minutesEdit.getText().toString();
        String strSeconds = secondsEdit.getText().toString();

        if(TextUtils.isEmpty(strHours)) {
            hoursEdit.setError(getString(R.string.error));
            return false;
        }

        if(TextUtils.isEmpty(strMinutes)) {
            minutesEdit.setError(getString(R.string.error));
            return false;
        }

        if(TextUtils.isEmpty(strSeconds)) {
            secondsEdit.setError(getString(R.string.error));
            return false;
        }

        hours = hoursEdit.getText().toString();
        minutes = minutesEdit.getText().toString();
        seconds = secondsEdit.getText().toString();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //On click method "SET NEW COUNTDOWN"
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetNewCountDown(View view){

        if(timeBox.isChecked()) {
            if(!SetNewTime())
                return;
        }

        if(dateBox.isChecked()) {
            if(!SetNewDate())
                return;
        }

        DisplayNewDate();  //shows on the a new date screen

        WriteMessage();  //saves the date was entered
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //On click method "EM@IL ME"
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SendEmail(View view){

        Intent intent = new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.parse("mailto:"));
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mihail.gritsenko@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
        intent.putExtra(Intent.EXTRA_TEXT, resultDate.getText());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
