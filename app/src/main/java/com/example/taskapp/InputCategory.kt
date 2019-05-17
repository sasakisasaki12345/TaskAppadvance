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

    var mCategoryClass:Category?= Category()
    var getCount :Int = -1


    val mOnAddCategoryListener = View.OnClickListener {

        Realm.init(this)



        var realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val mCategory = realm.where(Category::class.java).findAll()

        getCount =
            if (mCategory.max("id") != null) {
                mCategory.max("id")!!.toInt() + 1
            } else {
                0
            }

        mCategoryClass!!.id=getCount
        mCategoryClass!!.name=add_category_name.text.toString()

        Log.d("ID",getCount.toString())
        Log.d("名前",mCategoryClass!!.name.toString())

        realm.copyToRealmOrUpdate(mCategoryClass)
        realm.commitTransaction()

        realm.close()

        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_category)

        add_button.setOnClickListener(mOnAddCategoryListener)
    }
}
