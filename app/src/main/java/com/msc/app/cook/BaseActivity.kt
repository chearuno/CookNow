package com.msc.app.cook

import android.graphics.Color
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

open class BaseActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
//    }

    fun setupToolbar(
        toolbarId: Int,
        title: String?,
        @ColorRes titleColor: Int,
        @ColorRes colorBg: Int,
        @DrawableRes burger: Int
    ) {
        toolbar = findViewById(toolbarId)
        toolbar!!.setBackgroundColor(
            ContextCompat.getColor(this, colorBg)
        )
        setSupportActionBar(toolbar)
        val pageTitle = toolbar!!.findViewById(R.id.tv_title) as TextView
        pageTitle.text = title
        pageTitle.setTextColor(ContextCompat.getColor(this, titleColor))
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(burger)
    }

    fun changeStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

}