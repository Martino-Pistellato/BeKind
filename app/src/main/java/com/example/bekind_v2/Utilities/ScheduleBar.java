package com.example.bekind_v2.Utilities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;


import java.util.Calendar;
import java.util.Date;

public class ScheduleBar {

    public static class ScheduleDate {
        public static Date scheduleDate;

        public void ScheduleBar(Date scheduleDate) {
            this.scheduleDate = scheduleDate;
        }

        public void setScheduleDate(Date scheduleDate) {
            this.scheduleDate = scheduleDate;
        }

        public Date getScheduleDate() {
            return scheduleDate;
        }

        public static void showDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener dateSetListener) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(scheduleDate);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }

    }

}
