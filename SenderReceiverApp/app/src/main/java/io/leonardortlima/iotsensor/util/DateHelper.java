package io.leonardortlima.iotsensor.util;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    public static String getCurrentDateAsISOString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ",
                Locale.getDefault());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return sdf.format(new Date());
        } else {
            return sdf.format(new Date()).replaceAll("(\\d\\d)(\\d\\d)$", "$1:$2");
        }
    }
}
