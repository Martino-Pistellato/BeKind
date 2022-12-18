package com.example.bekind_v2.Utilities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utilities {
    public static class SharedViewModel{
        public static ProposalsViewModel proposalsViewModel;
        public static LocalDate day;
        public static ArrayList<String> filters;
    }
    public static class BetterCalendar {
        public Calendar c;

        public BetterCalendar() {
            this.c = java.util.Calendar.getInstance();
        }
        public BetterCalendar(Date date) {
            this.c = java.util.Calendar.getInstance();
            setDate(date);
        }

        public void setDate(Date date) {
            this.c.setTime(date);
        }

        public int getMonth() {
            return this.c.get(Calendar.MONTH);
        }
        public int getDay() {
            return this.c.get(Calendar.DAY_OF_MONTH);
        }
        public int getYear() {
            return this.c.get(Calendar.YEAR);
        }
    }

    public static void manageFilter(String filter, ArrayList<String> filters){
        if(filters.contains(filter))
            filters.remove(filter);
        else
            filters.add(filter);
    }
}
