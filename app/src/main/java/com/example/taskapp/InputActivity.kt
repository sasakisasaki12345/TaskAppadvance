package com.example.taskapp

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import java.util.*
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

class InputActivity : AppCompatActivity() {

    private var mYear = 0
    private var mMouth = 0
    private var mDay = 0
    private var mHour=0
    private var mMinute=0
    private var mTask :Task? = null
    var spinnerItems = arrayListOf<String>("勉強","家事","買物","その他")
    var selectItem:String? =null


    private val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog(this,DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth->
            mYear = year
            mMouth = month
            mDay = dayOfMonth
        val dateString = mYear.toString() + "/" + String.format("%02d", mMouth + 1) + "/" + String.format("%02d", mDay)
        date_button.text = dateString
    },mYear,mMouth,mDay)
        datePickerDialog.show()
        }

    private val mOnTimeClickListener = View.OnClickListener {
        val timePickerDialog = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener{ _, hour,minute->
            mHour = hour
            mMinute = minute
            val timeString = String.format("%02d",mHour)+ ":" + String.format("%02d", mMinute)
            times_button.text = timeString
        },mHour,mMinute,false)
        timePickerDialog.show()
    }

    private val mOmDoneClickListener = View.OnClickListener {
        addTask()
        finish()
    }

    private  val mOnCategoryAddOnClickListener = View.OnClickListener {

        addCategory()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        date_button.setOnClickListener(mOnDateClickListener)
        times_button.setOnClickListener(mOnTimeClickListener)
        done_button.setOnClickListener(mOmDoneClickListener)
        category_add_button.setOnClickListener(mOnCategoryAddOnClickListener)

        val intent = intent
        val taskId = intent.getIntExtra(EXTRA_TASK,-1)
        val realm=Realm.getDefaultInstance()
        mTask = realm.where(Task::class.java).equalTo("id",taskId).findFirst()
        realm.close()

        if(mTask==null) {
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMouth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

        }else {

            title_edit_text.setText(mTask!!.title)
            content_edit_text.setText(mTask!!.contents)

            val calendar = Calendar.getInstance()
            calendar.time = mTask!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMouth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

            val dateString =
                mYear.toString() + "/" + String.format("%02d", mMouth + 1) + "/" + String.format("%02d", mDay)
            val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)

            date_button.text = dateString
            times_button.text = timeString
        }
     }

    override fun onResume() {
        super.onResume()
        makeSpiner()
    }




    private fun addTask() {
        val realm = Realm.getDefaultInstance()
        val identifier: Int

        realm.beginTransaction()

        if(mTask == null) {
            mTask = Task()

            val taskRealmResults = realm.where(Task::class.java).findAll()

            identifier =
                if (taskRealmResults.max("id") != null) {
                    taskRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mTask!!.id = identifier
        }

        val title = title_edit_text.text.toString()
        val content = content_edit_text.text.toString()
        val mCategory:Category = Category()

        mCategory.id = mTask!!.id
        mCategory.name = selectItem

        //textviewからじゃくなくスピナーからとる
        mTask!!.category = mCategory
        mTask!!.title=title
        mTask!!.contents=content
        val calendar = GregorianCalendar(mYear,mMouth,mDay,mHour,mMinute)
        val date = calendar.time
        mTask!!.date =date

        realm.copyToRealmOrUpdate(mTask!!)
        realm.commitTransaction()

        realm.close()

        val resultIntent = Intent(applicationContext,TaskAlarmReceiver::class.java)
        resultIntent.putExtra(EXTRA_TASK,mTask!!.id)
        val resultPendingIntent = PendingIntent.getBroadcast(
            this,
            mTask!!.id,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,resultPendingIntent)

        }

    private fun addCategory(){
        //intentにidいれてinputcategoryに行く
        val intent = Intent(this,InputCategory::class.java)
        startActivity(intent)
    }

    private fun makeSpiner(){

        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            spinnerItems
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                selectItem = spinnerParent.selectedItem as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }
    }