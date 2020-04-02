package com.palmtreesoftware.experimentandroid51

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        set_dark_button.setOnClickListener {
            color_frame.setBackgroundColor(0xff000000.toInt())
        }

        set_light_button.setOnClickListener {
            color_frame.setBackgroundColor(0xffffffff.toInt())
        }

        ok_button.setOnClickListener {
            finish()
        }


        cancel_button.setOnClickListener {
            finish()
        }
    }
}
