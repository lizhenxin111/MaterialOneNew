package com.lzx.materialone.manager;

import java.util.Calendar;

/**
 * 日期管理类
 */

public class DateManager {
    public static String getDate(){
        Calendar today = Calendar.getInstance();
        StringBuilder date = new StringBuilder();
        date.append(today.get(Calendar.YEAR)).append("-")
                .append(today.get(Calendar.MONTH) + 1).append("-")
                .append(today.get(Calendar.DAY_OF_MONTH));
        return date.toString();
    }

    public static int getMonth(){
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.MONTH) + 1;
    }
    public static int getDayOfMonth(){
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.DAY_OF_MONTH);
    }
    public static int getYear(){
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR);
    }

}
