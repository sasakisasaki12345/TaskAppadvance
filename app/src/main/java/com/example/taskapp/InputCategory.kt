package com.example.taskapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input_2.*
import java.util.*

class InputCategory : AppCompatActivity() {

    private var mCategory:Category? =null
    private var taskId:Int?=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_category)

        val intent = intent
        taskId= intent.getIntExtra(EXTRA_TASK,-1)
        Log.d("aaa",taskId.toString())
        val realm = Realm.getDefaultInstance()
        mCategory = realm.where(Category::class.java).equalTo("id",taskId).findFirst()
        realm.close()

        button_study.setOnClickListener(addcategory("study"))
        button_housework.setOnClickListener(addcategory("housework"))
        button_shopping.setOnClickListener(addcategory("shopping"))
        button_other.setOnClickListener(addcategory("other"))

    }


    private fun addcategory(select:String) = View.OnClickListener {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()

        if (mCategory == null) {
            //インスタンスがなければ作成し、Taskと同じIDを設定
            mCategory = Category()
            mCategory!!.id = taskId!!
        } else {
            //インスタンスとIDが元々あればそのままスルー
        }

        val category = select

        mCategory!!.category = category

        val taskClass = realm.where(Task::class.java).equalTo("id",mCategory!!.id).findFirst()

        taskClass!!.category = mCategory!!

        realm.copyToRealmOrUpdate(taskClass)
        realm.commitTransaction()

        realm.close()

        finish()

    }
}
