package com.daedrii.lembrolkotlin.controller

import com.daedrii.lembrolkotlin.model.ReminderGroup
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object ReminderSorter {

    fun sortReminderGroups(reminderGroups: ArrayList<ReminderGroup>){
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        reminderGroups.sortWith(Comparator{ group1, group2 ->
            try {
                val date1 = dateFormat.parse(group1.date)
                val date2 = dateFormat.parse(group2.date)
                return@Comparator date1?.compareTo(date2) ?: 0
            }catch (e: ParseException){
                e.printStackTrace()
            }
            return@Comparator 0

        })
    }

}