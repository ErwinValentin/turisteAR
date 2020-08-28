package com.valentingonzalez.turistear.includes

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.valentingonzalez.turistear.R

object BasicToolbar {
    @JvmStatic
    fun show(activity: AppCompatActivity, title: String?, upEnabled: Boolean) {
        val mToolbar = activity.findViewById<Toolbar>(R.id.register_toolbar)
        activity.setSupportActionBar(mToolbar)
        activity.supportActionBar!!.title = title
        assert(activity.supportActionBar != null)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(upEnabled)
    }
}