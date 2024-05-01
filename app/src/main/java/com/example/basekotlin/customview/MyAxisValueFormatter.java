package com.example.basekotlin.customview;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyAxisValueFormatter implements IAxisValueFormatter {
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d");
    @SuppressLint("SimpleDateFormat")
    private final DateFormat munchFormatter = new SimpleDateFormat("MMM");

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        List<YearMonth> listDate = getAllX();
        int firstMonth = listDate.get(0).lengthOfMonth();
        int secondMonth = listDate.get(0).lengthOfMonth() + listDate.get(1).lengthOfMonth();
        int lastMont = listDate.get(0).lengthOfMonth() + listDate.get(1).lengthOfMonth() + listDate.get(2).lengthOfMonth();
        Date date = new Date();
        if (value <= firstMonth) {
            date.setMonth(currentMonth - 2);
            String monthString = munchFormatter.format(date);
            LocalDate localTime = LocalDate.of(0, date.getMonth() + 1, (int) value);
            return dayFormatter.format(localTime) + "/" + monthString;
        } else if (value > firstMonth && value <= secondMonth) {
            date.setMonth(currentMonth - 1);
            String monthString = munchFormatter.format(date);
            LocalDate localTime = LocalDate.of(0, date.getMonth() + 1, (int) value - firstMonth);
            return dayFormatter.format(localTime) + "/" + monthString;
        } else if (value > secondMonth && value <= lastMont) {
            date.setMonth(currentMonth);
            String monthString = munchFormatter.format(date);
            LocalDate localTime = LocalDate.of(0, date.getMonth() + 1, (int) value - secondMonth);
            return dayFormatter.format(localTime) + "/" + monthString;
        } else return "";
    }

    private List<YearMonth> getAllX() {
        List<YearMonth> listMonth = new ArrayList<>();
        Calendar current = Calendar.getInstance();
        int year = current.get(Calendar.YEAR);
        int month = current.get(Calendar.MONTH) + 1;
        if (month == 1) {
            listMonth.add(getMonthLength(year - 1, 11));
            listMonth.add(getMonthLength(year - 1, 12));
            listMonth.add(getMonthLength(year, month));
        } else if (month == 2) {
            listMonth.add(getMonthLength(year - 1, 12));
            listMonth.add(getMonthLength(year, month - 1));
            listMonth.add(getMonthLength(year, month));
        } else {
            listMonth.add(getMonthLength(year, month - 2));
            listMonth.add(getMonthLength(year, month - 1));
            listMonth.add(getMonthLength(year, month));
        }
        return listMonth;
    }

    private YearMonth getMonthLength(int year, int month) {
        return YearMonth.of(year, month);
    }
}
