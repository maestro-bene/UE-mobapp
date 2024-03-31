package fr.imt_atlantique.myfirstapplication.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.widget.EditText;

import java.util.Locale;

public class DatePickerUtils {

    public static void showDatePickerDialog(Context context, EditText birthDateEditText) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
            birthDateEditText.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
