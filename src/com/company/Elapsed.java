package com.company;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Elapsed {

    public static final long MS_DAY = 24 * 60 * 60 * 1000;
    private final DateFormat dateFormat = new SimpleDateFormat("HH : mm : ss : S");

    public Elapsed() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public String format(long elapsed) {
        long day = elapsed / MS_DAY;
        return day + " : " + dateFormat.format(new Date(elapsed));
    }

//    public static void main(String[] args) {
//        Elapsed e = new Elapsed();
//        for (long t = 0; t < 3 * MS_DAY; t += MS_DAY / 2) {
//            System.out.println(e.format(t));
//        }
//    }
}
