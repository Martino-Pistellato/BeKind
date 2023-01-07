package com.example.bekind_v2.Utilities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.TextView;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ScheduleBar {

    public static class ScheduleDate {
        private static Date scheduleDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        public void ScheduleBar() {}

        public static void setScheduleDate(Date newDate) { scheduleDate = newDate; }

        public static Date getScheduleDate() {
            return scheduleDate;
        }
        
        public static LocalDate getScheduleLocalDate() {
            return scheduleDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        public static DatePickerDialog showDatePickerDialog(Context context) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context);
            Date date = ScheduleDate.getScheduleDate();


            Utilities.BetterCalendar calendar = new Utilities.BetterCalendar(scheduleDate);

            datePickerDialog.updateDate( calendar.getYear(),
                    calendar.getMonth(),
                    calendar.getDay());

            datePickerDialog.show();
            return datePickerDialog;
        }

        public static void setTextDate(TextView textScheduleDate) {
            Utilities.BetterCalendar calendar = new Utilities.BetterCalendar(scheduleDate);
            textScheduleDate.setText("Programma del giorno " +
                    calendar.getDay() + "/" +
                    (calendar.getMonth() + 1) + "/" +
                    calendar.getYear()); //changing the TextView
        }
    }

}
