package com.daedrii.lembrolkotlin.view

import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.daedrii.lembrolkotlin.R
import com.daedrii.lembrolkotlin.controller.ReminderAdapter
import com.daedrii.lembrolkotlin.model.Reminder
import com.google.android.material.textview.MaterialTextView

class ReminderViewHolder(itemView: View, private val adapter: ReminderAdapter, private var groupPosition: Int, private var childPosition: Int) : RecyclerView.ViewHolder(itemView) {

    private val lblReminder: MaterialTextView = itemView.findViewById(R.id.lbl_reminder)
    private val lblDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

    // Vincula cada Reminder com sua View na tela, alinhando-o com seus componentes
    fun bind(reminder: Reminder) {
        lblReminder.text = reminder.name // Define o campo de texto do componente

        lblDelete.setOnClickListener {
            // Define a ação do botão de remover do componente
            removeReminder(groupPosition, childPosition)
        }
    }

    // Remove um lembrete
    private fun removeReminder(groupPosition: Int, childPosition: Int): Reminder {
        val remindersInAGroup = adapter.getRemindersInAGroup(groupPosition)
        val removedReminder = remindersInAGroup.removeAt(childPosition) // Remove reminder do grupo
        adapter.getDataManager().getReminders().remove(removedReminder) // Remove Reminder da lista de lembretes
        adapter.notifyDataSetChanged()
        return removedReminder
    }

    fun updatePosition(groupPosition: Int, childPosition: Int) {
        this.groupPosition = groupPosition
        this.childPosition = childPosition
    }

    fun getGroupPosition(): Int {
        return groupPosition
    }

    fun getChildPosition(): Int {
        return childPosition
    }

    fun getLblReminder(): MaterialTextView {
        return lblReminder
    }

    fun getLblDelete(): ImageButton {
        return lblDelete
    }

    fun getAdapter(): ReminderAdapter {
        return adapter
    }
}