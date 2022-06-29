package com.zyh

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zyh.itemDecoration.ItemDecorationSwipeMenuActivity
import com.zyh.swipe.R
import com.zyh.swipeViewHolder.SwipeViewHolderMainActivity
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        swipeViewHolder.setOnClickListener {
            startActivity(Intent(this,SwipeViewHolderMainActivity::class.java))
        }

        itemDecoration.setOnClickListener {
            startActivity(Intent(this,ItemDecorationSwipeMenuActivity::class.java))
        }
    }
}