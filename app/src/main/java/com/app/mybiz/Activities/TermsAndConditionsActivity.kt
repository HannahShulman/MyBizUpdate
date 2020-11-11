package com.app.mybiz.Activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.app.mybiz.R
import kotlinx.android.synthetic.main.activity_terms_and_policy.*

//frorm 43 lines
class TermsAndConditionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_policy)
        val toolbarTitle = intent?.getStringExtra(TITLE_KEY)
        toolbar.title = toolbarTitle
        Log.d("TAG", "onCreate: ${intent?.getStringExtra(TITLE_KEY)}")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        content_view.text = intent?.getStringExtra(CONTENT_KEY)
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

    companion object {
        const val TITLE_KEY = "title"
        const val CONTENT_KEY = "Content"
    }
}