package com.example.taskapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import kotlinx.android.synthetic.main.content_input_2.*
import java.util.*

class InputCategory : AppCompatActivity() {

    var addCategoryName :String =""


    val mOnAddCategoryListener = View.OnClickListener {

        val intent = Intent(this,InputActivity::class.java)
        intent.putExtra("name",addCategoryName)
        startActivity(intent)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_category)

        addCategoryName = add_category_name.text.toString()

        add_button.setOnClickListener(mOnAddCategoryListener)
    }
}
