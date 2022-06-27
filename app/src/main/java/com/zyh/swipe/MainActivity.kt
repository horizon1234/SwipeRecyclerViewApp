package com.zyh.swipe

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class MainActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null

    private var mSwipeMenuHelper: SwipeMenuHelper? = null

    companion object{
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)

        val adapter = DemoAdapter(R.layout.layout_item)

        adapter.addChildClickViewIds(R.id.bg)
        adapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.bg) {
                mSwipeMenuHelper?.closeSwipeByPosition(position)
            }
        }

        mRecyclerView?.adapter = adapter

        val list = ArrayList<String>()
        for (i in 0..100) {
            list.add("Index ---- $i")
        }

        adapter.setNewInstance(list)

        //设置侧滑
        val mySwipeMenuCallback = MySwipeMenuCallback()
        mSwipeMenuHelper = SwipeMenuHelper.install(mRecyclerView, mySwipeMenuCallback)
    }

    //自定义侧滑Callback
    class MySwipeMenuCallback : SwipeMenuCallback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val position = viewHolder.adapterPosition
            return if (position % 2 == 0){
                ItemTouchHelper.LEFT
            }else{
                FLAG_NONE
            }
        }

        override fun getSwipeMenuMaxWidth(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return SwipeUtils.getSwipeMenuWidth(viewHolder)
        }

        override fun onSwipeTo(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            x: Float,
            y: Float
        ) {
            Log.i(TAG, "onSwipeTo: $x  $y")
            super.onSwipeTo(recyclerView, viewHolder, x, y)
        }
    }

    //Adapter
    inner class DemoAdapter(val resId: Int) : BaseQuickAdapter<String,BaseViewHolder>(resId){
        @RequiresApi(Build.VERSION_CODES.M)
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.indexTv, item)
            if (holder.adapterPosition % 2 == 0) {
                holder.setBackgroundColor(R.id.bg, resources.getColor(R.color.teal_200))
            } else {
                holder.setBackgroundColor(R.id.bg, resources.getColor(R.color.teal_700))
            }
            when (holder.adapterPosition % 4) {
                0 -> {
                    holder.setText(R.id.testTv,"删除+签收")
                    //2个按钮
                    val deleteSwipe = SwipeBtn(this@MainActivity).apply {
                        text = "删除"
                        setOnClickListener {
                            Toast.makeText(this@MainActivity, "删除", Toast.LENGTH_SHORT).show()
                        }
                        setTextColor(getColor(R.color.white))
                        setBackgroundColor(getColor(R.color.red))
                        setPadding(100,0,100,0)
                    }
                    val signSwipe = SwipeBtn(this@MainActivity).apply {
                        text = "签收"
                        setOnClickListener {
                            Toast.makeText(this@MainActivity, "签收", Toast.LENGTH_SHORT).show()
                        }
                        setTextColor(getColor(R.color.purple_700))
                        setBackgroundColor(getColor(R.color.white))
                        setPadding(100,0,100,0)
                    }

                    holder.getView<SwipeBtnContainer>(R.id.swipeContainer)
                        .addViews(arrayListOf(deleteSwipe, signSwipe))
                }
                2 -> {
                    holder.setText(R.id.testTv,"详情")
                    //1个按钮
                    val detailSwipe = SwipeBtn(this@MainActivity).apply {
                        text = "详情"
                        setOnClickListener {
                            Toast.makeText(this@MainActivity, "详情", Toast.LENGTH_SHORT).show()
                        }
                        setTextColor(getColor(R.color.white))
                        setBackgroundColor(getColor(R.color.red))
                    }
                    holder.getView<SwipeBtnContainer>(R.id.swipeContainer)
                        .addViews(arrayListOf(detailSwipe))
                }
                else -> {
                    holder.setText(R.id.testTv,"无侧滑菜单")
                }
            }
        }

    }
}