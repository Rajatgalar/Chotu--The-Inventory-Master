package com.itechnowizard.chotu.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_FORMAT = "dd-MM-yyyy"

    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val formattedDate = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
                onDateSelected(formattedDate)
            }, year, month, day
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    fun showDatePickerWithAllDate(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val formattedDate = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
                onDateSelected(formattedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }


}