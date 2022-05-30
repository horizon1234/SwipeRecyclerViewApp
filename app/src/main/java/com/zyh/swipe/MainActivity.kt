package com.zyh.swipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class MainActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)

        val adapter = object : BaseQuickAdapter<String,BaseViewHolder>(R.layout.layout_item){
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.tv,item)
                if (holder.adapterPosition % 2 == 0){
                    holder.setBackgroundColor(R.id.bg,resources.getColor(R.color.white))
                }else{
                    holder.setBackgroundColor(R.id.bg,resources.getColor(R.color.teal_700))
                }
            }
        }

        mRecyclerView?.adapter = adapter

        val list = ArrayList<String>()
        for (i in 0 .. 1000){
            list.add(System.currentTimeMillis().toString() + " ---- $i")
        }

        adapter.setNewInstance(list)
    }
}