package com.example.android.finalcountdown;

import android.content.Context;

import java.util.Calendar;

public class CustomDate
{
    private int Hours;
    private int Minutes;
    private int Seconds;
    private int Day;
    private int Month;
    private int Year;

    public int getHours(){return Hours;}
    public int getMinutes(){return Minutes;}
    public int getSeconds(){return Seconds;}
    public int getDay(){return Day;}
    public int getMonth(){return Month;}
    public int getYear(){return Year;}

    static Calendar cal1 = Calendar.getInstance();
    static Calendar cal2 = Calendar.getInstance();

    static Integer[] elapsed = new Integer[6];

    //JAN = 31, FEB = 28, MAR = 31, APR = 30, MAY = 31, JUN = 30, JUL = 31, AUG = 31, SEP = 30, OCT = 31, NOV = 30, DEC = 31

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Is the entered date correct
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isCorrectDate(int d, int m, int y)
    {
        if(d <= 31 && (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12))
        {
            return true;
        }
        else if(d <= 30 && (m == 4 || m == 6 || m == 9 || m == 11))
        {
            return true;
        }
        else if(d <= 29 && (m == 2 && isLeap(y)))
        {
            return true;
        }
        else if(d <= 28 && (m == 2 && !isLeap(y)))
        {
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Is a leap year
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isLeap(int year)
    {
        if ((year % 400) == 0)
            return true;
        if ((year % 100) == 0)
            return false;
        if ((year % 4) == 0)
            return true;

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Returns a date
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getDate()
    {
        return (Integer.toString(Year) + "." + Integer.toString(Month) + "." + Integer.toString(Day) + " / " +
                Integer.toString(Hours) + ":" + Integer.toString(Minutes) + ":" + Integer.toString(Seconds));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets a new date
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean setDate(int h, int m, int s, int d, int mnth, int y)
    {
        if(isCorrectDate(d, mnth + 1, y))
        {
            Hours = h;
            Minutes = m;
            Seconds = s;

            Day = d;
            Month = mnth;
            Year = y;

            return true;
        }

        return  false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Sets a new date an overload function
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean setDate(CustomDate obj)
    {
        if(isCorrectDate(obj.getDay(), obj.getMonth(), obj.getYear()))
        {
            Hours = obj.getHours();
            Minutes = obj.getMinutes();
            Seconds = obj.getSeconds();

            Day = obj.getDay();
            Month = obj.getMonth();
            Year = obj.getYear();

            return true;
        }

        return  false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Remaining or elapsed time in YEARS, MONTHS, DAYS .....
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static int elapsed(Calendar before, Calendar after, int field)
    {
        Calendar before_clone = (Calendar) before.clone();
        int elapsed = -1;

        while (!before_clone.after(after)) //this is not as efficient as I would like it to be.
        {
            before_clone.add(field, 1);
            elapsed++;
        }

        return elapsed;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Main method that calculates the difference between two dates;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String calculateElapsedTime(Context c, CustomDate start_obj, CustomDate end_obj)
    {
        Calendar start = Calendar.getInstance();
        start.set(start_obj.getYear(), start_obj.getMonth(), start_obj.getDay(), start_obj.getHours(), start_obj.getMinutes(), start_obj.getSeconds());

        Calendar end = Calendar.getInstance();
        end.set(end_obj.getYear(), end_obj.getMonth(), end_obj.getDay(), end_obj.getHours(), end_obj.getMinutes(), end_obj.getSeconds());

        Calendar start_clone = (Calendar) start.clone();

        elapsed[0] = elapsed(start_clone, end, Calendar.YEAR);
        start_clone.add(Calendar.YEAR, elapsed[0]);

        elapsed[1] = elapsed(start_clone, end, Calendar.MONTH);
        start_clone.add(Calendar.MONTH, elapsed[1]);

        elapsed[2] = elapsed(start_clone, end, Calendar.DAY_OF_MONTH);
        start_clone.add(Calendar.DAY_OF_MONTH, elapsed[2]);

        elapsed[3] = (int) Math.abs(end.getTimeInMillis() - start_clone.getTimeInMillis()) / 3600000;
        start_clone.add(Calendar.HOUR, elapsed[3]);

        elapsed[4] = (int) Math.abs(end.getTimeInMillis() - start_clone.getTimeInMillis()) / 60000;
        start_clone.add(Calendar.MINUTE, elapsed[4]);

        elapsed[5] = (int) Math.abs(end.getTimeInMillis() - start_clone.getTimeInMillis()) / 1000;

        return String.format(c.getResources().getString(R.string.formatting), elapsed);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Returns the MAX date among two dates
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static int whichIsBigger(CustomDate obj1, CustomDate obj2)
    {
        cal1.set(obj1.getYear(), obj1.getMonth(), obj1.getDay(), obj1.getHours(), obj1.getMinutes(), obj1.getSeconds());
        cal2.set(obj2.getYear(), obj2.getMonth(), obj2.getDay(), obj2.getHours(), obj2.getMinutes(), obj2.getSeconds());

        return cal1.compareTo(cal2);
    }
}


