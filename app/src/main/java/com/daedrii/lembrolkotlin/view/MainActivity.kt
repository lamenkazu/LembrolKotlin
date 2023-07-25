package com.daedrii.lembrolkotlin.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.Toast
import com.daedrii.lembrolkotlin.controller.ReminderAdapter
import com.daedrii.lembrolkotlin.controller.ReminderDataManager
import com.daedrii.lembrolkotlin.databinding.ActivityMainBinding
import com.daedrii.lembrolkotlin.model.Reminder
import com.daedrii.lembrolkotlin.model.exceptions.EmptyFieldException
import com.daedrii.lembrolkotlin.model.exceptions.InvalidDateException
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var materialDatePicker: MaterialDatePicker<Long>
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var dataManager: ReminderDataManager
    private lateinit var binding: ActivityMainBinding

    private fun initComponents(){

        dataManager = ReminderDataManager()
        reminderAdapter = ReminderAdapter(this, dataManager)
        binding.reminderList.setAdapter(reminderAdapter)

        materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds() - 10800000)
            .build()

    }

    private fun setComponentActions(){
        binding.txtDate.setOnFocusChangeListener{_, hasFocus ->
            if(hasFocus && !materialDatePicker.isAdded){
                materialDatePicker.show(supportFragmentManager, "tag")
            }
        }

        binding.txtDate.setOnTouchListener{_, event ->
            if(!materialDatePicker.isAdded){
                materialDatePicker.show(supportFragmentManager, "tag")

            }
            false
        }

        materialDatePicker.addOnPositiveButtonClickListener { selection->
            val data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selection))
            binding.txtDate.setText(data)
        }

        binding.btnCreate.setOnClickListener{

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.txtReminder.windowToken, 0)

            try {
                val textReminder = binding.txtReminder.text.toString()
                val textDate = binding.txtDate.text.toString()

                dataManager.addList(Reminder(textReminder, textDate))

                reminderAdapter.setData(dataManager.getReminders())

                reminderAdapter.orderData()
                reminderAdapter.notifyDataSetChanged()

                binding.txtReminder.setText("")
                binding.txtDate.setText("")
            } catch (e: EmptyFieldException) {
                Log.w("EmptyFieldException", e.message.toString())
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            } catch (e: InvalidDateException) {
                Log.w("InvalidDateException", e.message.toString())
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        setComponentActions()

        dataManager.loadList()
    }

    override fun onResume() {
        super.onResume()

        if(!dataManager.getReminders().isEmpty()){
            reminderAdapter.setData(dataManager.getReminders())
            reminderAdapter.orderData()
            reminderAdapter.notifyDataSetChanged()
        }
    }
}