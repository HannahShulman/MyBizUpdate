package com.mybiz.Activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mybiz.R
import kotlinx.android.synthetic.main.activity_about_mybizz.*

//from 43 lines
//missing displaying the app version and back navigation
class AboutMybizzActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_mybizz)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        about_info_tv.text = resources.getString(R.string.about_mybizz_text)
        val version = packageManager.getPackageInfo(packageName, 0).versionName
        version_tv.append(version.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}