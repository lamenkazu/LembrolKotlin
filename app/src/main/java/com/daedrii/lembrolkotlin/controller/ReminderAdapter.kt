package com.daedrii.lembrolkotlin.controller

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.daedrii.lembrolkotlin.R
import com.daedrii.lembrolkotlin.databinding.DateListBinding
import com.daedrii.lembrolkotlin.model.Reminder
import com.daedrii.lembrolkotlin.model.ReminderGroup
import com.daedrii.lembrolkotlin.view.ReminderViewHolder

class ReminderAdapter(private val context: Context,
                      private val dataManager: ReminderDataManager): BaseExpandableListAdapter() {

    private var reminderGroups: ArrayList<ReminderGroup> = ArrayList()
    private var dateList: HashMap<String, ArrayList<Reminder>> = HashMap()
    var reminders: ArrayList<Reminder> = ArrayList()

    // Define os dados do lembrete
    fun setData(data: ArrayList<Reminder>){
        // Limpa as listas existentes
        reminderGroups.clear()
        dateList.clear()
        reminders.clear()


        reminders.addAll(data)

        // Agrupa os lembretes por data
        for (reminder in data) {
            val date = reminder.date
            val reminders = dateList[date] ?: ArrayList()
            reminders.add(reminder)
            dateList[date] = reminders
        }

        // Cria os grupos a partir dos dados da lista dateList
        for ((date, reminders) in dateList) {
            val group = ReminderGroup(date, reminders)
            reminderGroups.add(group)
        }
    }
    fun orderData() {
        ReminderSorter.sortReminderGroups(reminderGroups)
    }

    fun updateViewHolder(viewHolder: ReminderViewHolder, groupPosition: Int, childPosition: Int) {
        val reminder = getChild(groupPosition, childPosition) as Reminder
        viewHolder.bind(reminder)
    }

    override fun getGroupCount(): Int {
        return dateList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val group = reminderGroups[groupPosition]
        return group.reminders.size
    }

    override fun getGroup(groupPosition: Int): Any {
        val group = reminderGroups[groupPosition]
        return group.date
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val group = reminderGroups[groupPosition]
        return group.reminders[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val binding: DateListBinding

        if(convertView == null){
            val inflater = LayoutInflater.from(context)
            binding = DateListBinding.inflate(inflater, parent, false)
            convertView = binding.root
        }else{
            binding = convertView.tag as DateListBinding
        }


        val listTitle = getGroup(groupPosition) as String
        binding.listTitle.text = listTitle

        // Verificação da lista de reminders para uma determinada data estar vazia, caso esteja, não a mostra mais.
        val reminders = dateList[listTitle]
        if(reminders?.isEmpty() == true){
            binding.listTitle.visibility = View.GONE
        }else{
            binding.listTitle.visibility = View.VISIBLE
        }

        //Seta o binding como tag da convertView para reutilizar
        convertView.tag = binding

        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val viewHolder: ReminderViewHolder

        if (convertView == null || convertView.tag == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_list_reminder, null)
            viewHolder =
                ReminderViewHolder(convertView!!, this, groupPosition, childPosition)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ReminderViewHolder
            viewHolder.updatePosition(groupPosition, childPosition)
        }

        val actualReminder = getChild(groupPosition, childPosition) as Reminder
        viewHolder.bind(actualReminder)

        return convertView
    }



    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    fun getRemindersInAGroup(groupPosition: Int): ArrayList<Reminder> {
        val group = reminderGroups[groupPosition]
        return group.reminders
    }

    fun removeReminderAt(position: Int) {
        if (position < 0 || position >= reminders.size) {
            return  // Saia do método se a posição do lembrete for inválida
        }

        val reminder = reminders[position]
        val group = getGroupForReminder(reminder)

        if (group != null) {
            group.reminders.remove(reminder)
            if (group.reminders.isEmpty()) {
                reminderGroups.remove(group)
            }
        }

        reminders.removeAt(position)
    }


    private fun getGroupPositionForReminder(position: Int): Int {
        var count = 0
        for (group in reminderGroups) {
            val size = group.reminders.size
            if (position < count + size) {
                return reminderGroups.indexOf(group)
            }
            count += size
        }
        return -1
    }

    private fun getChildPositionForReminder(groupPosition: Int, position: Int): Int {
        var count = 0
        for (i in 0 until groupPosition) {
            count += reminderGroups[i].reminders.size
        }
        return position - count
    }

    fun getReminderAt(position: Int): Reminder{
        val reminder = reminders[position]
        return reminder
    }

    fun getGroupForReminder(reminder: Reminder): ReminderGroup? {
        for (group in reminderGroups) {
            if (group.reminders.contains(reminder)) {
                return group
            }
        }
        return null
    }

    fun getDataManager(): ReminderDataManager{
        return dataManager
    }
}