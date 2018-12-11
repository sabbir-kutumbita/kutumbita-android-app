package com.kutumbita.app.utility;

import android.text.TextUtils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtility {

    public static String changeDateFormat(String currentFormat, String requiredFormat, String dateString) {
        try {

            // String dateStr = "21/20/2011";

            DateFormat srcDf = new SimpleDateFormat(currentFormat);

            // parse the date string into Date object
            Date date = srcDf.parse(dateString);

            DateFormat destDf = new SimpleDateFormat(requiredFormat);

            // format the date into another format
            dateString = destDf.format(date);

            return dateString;

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
