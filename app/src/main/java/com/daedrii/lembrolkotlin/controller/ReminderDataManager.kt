package com.daedrii.lembrolkotlin.controller

import android.util.Log
import com.daedrii.lembrolkotlin.model.Reminder
import com.daedrii.lembrolkotlin.model.ReminderGroup
import com.daedrii.lembrolkotlin.model.exceptions.EmptyFieldException
import com.daedrii.lembrolkotlin.model.exceptions.InvalidDateException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderDataManager {

    private var reminderGroups: ArrayList<ReminderGroup> = ArrayList()
    private var reminders: ArrayList<Reminder> = ArrayList()
    private var dateList: HashMap<String, ArrayList<Reminder>> = HashMap()


    //Encontra um Agrupamento de Lembretes a partir de uma data, retorna nulo caso não encontre.
    fun findReminderGroupByDate(date: String): ReminderGroup?{
        for(group in reminderGroups){
            if (group.date == date){
                return group
            }
        }
        return null
    }

    //Cria um novo dado nas 3 estruturas de dados
    fun addList(newReminder: Reminder) {
        if(newReminder.date.isEmpty() || newReminder.name.isEmpty()){
            throw EmptyFieldException("Os campos não podem estar vazios para a inserção.")
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try{
            val date = sdf.parse(newReminder.date)
            val timestamp = date?.time ?: 0

            if (Date(timestamp).time < Date().time - 97200000) { //Permite criar lembretes para hoje e em diante, mas não para dias anteriores. TODO criar metodo mais eficaz de verificação de datas
                throw InvalidDateException("A data informada precisa estar no futuro para ser inserida")
            }
        }catch (e: ParseException) {
            Log.d("SimpleDateFormat", e.message ?: "")
        }

        reminders.add(newReminder)

        //ReminderGroup agrupa lembretes de uma data específica (date)
        val date = newReminder.date
        var group = findReminderGroupByDate(date)
        if (group == null) {
            // Cria uma nova lista para a data específica
            val remindersList = ArrayList<Reminder>()
            dateList[date] = remindersList // Adiciona a nova lista à dateList
            group = ReminderGroup(date, remindersList)
            reminderGroups.add(group)
        }

        // Adiciona o novo lembrete à lista de lembretes específica da data

        dateList[date]?.add(newReminder)
    }

    //Seed para uma nova Lista para testes de implementação
    fun loadList(){
        reminders = ArrayList()
        reminderGroups = ArrayList()
        dateList = HashMap()

        try {
            addList(Reminder("Aniversario da Taís", "07/06/2024"))
            addList(Reminder("Limpar caixa de areia", "30/05/2024"))
            addList(Reminder("Colocar ração", "29/05/2024"))
        } catch (e: EmptyFieldException) {
            Log.w("EmptyFieldException", e.message ?: "")
        } catch (e: InvalidDateException) {
            Log.w("InvalidDateException", e.message ?: "")
        }
    }


    fun getReminderGroups(): ArrayList<ReminderGroup> {
        return reminderGroups
    }

    fun getReminders(): ArrayList<Reminder> {
        return reminders
    }

    fun getDateList(): HashMap<String, ArrayList<Reminder>> {
        return dateList
    }

    fun setReminderGroups(reminderGroups: ArrayList<ReminderGroup>) {
        this.reminderGroups = reminderGroups
    }

    fun setReminders(reminders: ArrayList<Reminder>) {
        this.reminders = reminders
    }

    fun setDateList(dateList: HashMap<String, ArrayList<Reminder>>) {
        this.dateList = dateList
    }

}