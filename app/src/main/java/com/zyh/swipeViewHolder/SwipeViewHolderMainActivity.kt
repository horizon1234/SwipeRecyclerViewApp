package com.zyh.swipeViewHolder

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zyh.swipe.R
import com.zyh.swipe.SwipeUtils

/**
 * 侧滑菜单在ViewHolder中实现方式
 * */
class SwipeViewHolderMainActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null

    private var mSwipeMenuHelper: SwipeMenuHelper? = null

    private var adapter: BaseQuickAdapter<String,BaseViewHolder>? = null

    companion object{
        const val TAG = "SwipeViewHolderMain"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        //设置侧滑
        val mySwipeMenuCallback = MySwipeMenuCallback()
        mSwipeMenuHelper = SwipeMenuHelper.install(mRecyclerView, mySwipeMenuCallback)
    }

    private fun initRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = DemoAdapter(R.layout.layout_item)
        adapter?.addChildClickViewIds(R.id.bg)
        adapter?.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.bg) {
                //点击先关闭侧滑菜单
                mSwipeMenuHelper?.closeSwipeByPosition(position)
            }
        }
        mRecyclerView?.adapter = adapter
        val list = ArrayList<String>()
        for (i in 0..100) {
            list.add("Index ---- $i")
        }
        adapter?.setNewInstance(list)
    }

    //自定义侧滑Callback
    class MySwipeMenuCallback : SwipeMenuCallback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val position = viewHolder.adapterPosition
            return if (position % 2 == 0){
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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

    //Adapter，因为这种侧滑菜单在ViewHolder中，我们按需加载侧滑菜单
    inner class DemoAdapter(private val resId: Int) : BaseQuickAdapter<String,BaseViewHolder>(resId){
        @RequiresApi(Build.VERSION_CODES.M)
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.indexTv, item)
            //区分颜色
            if (holder.adapterPosition % 2 == 0) {
                holder.setBackgroundColor(R.id.bg, resources.getColor(R.color.teal_200))
            } else {
                holder.setBackgroundColor(R.id.bg, resources.getColor(R.color.teal_700))
            }
            when (holder.adapterPosition % 4) {
                0 -> {
                    //2个按钮
                    holder.setText(R.id.testTv,"删除+签收")
                    val deleteSwipe = SwipeBtn(this@SwipeViewHolderMainActivity).apply {
                        text = "删除"
                        setOnClickListener {
                            Toast.makeText(this@SwipeViewHolderMainActivity, "删除", Toast.LENGTH_SHORT).show()
                        }
                        setTextColor(getColor(R.color.white))
                        setBackgroundColor(getColor(R.color.red))
                        setPadding(100,0,100,0)
                    }
                    val signSwipe = SwipeBtn(this@SwipeViewHolderMainActivity).apply {
                        text = "签收"
                        setOnClickListener {
                            Toast.makeText(this@SwipeViewHolderMainActivity, "签收", Toast.LENGTH_SHORT).show()
                        }
                        setTextColor(getColor(R.color.purple_700))
                        setBackgroundColor(getColor(R.color.white))
                        setPadding(100,0,100,0)
                    }

                    holder.getView<SwipeBtnContainer>(R.id.swipeContainer)
                        .addViews(arrayListOf(deleteSwipe, signSwipe))
                }
                2 -> {
                    //1个按钮
                    holder.setText(R.id.testTv,"详情")
                    val detailSwipe = SwipeBtn(this@SwipeViewHolderMainActivity).apply {
                        text = "详情"
                        setOnClickListener {
                            Toast.makeText(this@SwipeViewHolderMainActivity, "详情", Toast.LENGTH_SHORT).show()
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