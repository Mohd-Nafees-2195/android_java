package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

public class CalendarPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_picker);

        Date today=new Date();
        Calendar nextYear=Calendar.getInstance();
        nextYear.add(Calendar.YEAR,1);
        CalendarPickerView datePicker=findViewById(R.id.calendar);

        //To Select the list of dates
        datePicker.init(today,nextYear.getTime()).withSelectedDate(today);

        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Calendar calSelected=Calendar.getInstance();
                calSelected.setTime(date);

                String selectedDate=""+calSelected.get(Calendar.DAY_OF_MONTH)
                        +"/"+(calSelected.get(Calendar.MONTH)+1)
                        +"/"+calSelected.get(Calendar.YEAR);
                Toast.makeText(CalendarPickerActivity.this, selectedDate, Toast.LENGTH_SHORT).show();


                int day=calSelected.get(Calendar.DAY_OF_MONTH);
                int month=calSelected.get(Calendar.MONTH);
                int year=calSelected.get(Calendar.YEAR);
                Intent intent=new Intent(CalendarPickerActivity.this,SetDueDateActivity.class);
                intent.putExtra("date",selectedDate);
                startActivity(intent);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }
}