package com.zyh.itemDecoration

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zyh.swipe.R
import com.zyh.swipe.SwipeUtils

/**
 * 使用ItemDecoration实现侧滑菜单
 * */
class ItemDecorationSwipeMenuActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null

    private var adapter: BaseQuickAdapter<String, BaseViewHolder>? = null

    private var mSwipeMenuHelper: ItemDecorationSwipeMenuHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_decoration_swipe_menu)

        initRecyclerView()

        //设置侧滑
        val mySwipeMenuCallback = MySwipeMenuCallback()
        mSwipeMenuHelper = ItemDecorationSwipeMenuHelper.install(mRecyclerView, mySwipeMenuCallback)
    }

    private fun initRecyclerView() {
        mRecyclerView = findViewById(R.id.rv)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(R.layout.layout_item)
        adapter?.addChildClickViewIds(R.id.bg)
        adapter?.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.bg) {
            }
        }
        mRecyclerView?.adapter = adapter
        val list = ArrayList<String>()
        for (i in 0..100) {
            list.add("Index ---- $i")
        }
        adapter?.setNewInstance(list)
    }

    inner class MyAdapter(private val resId: Int) :
        BaseQuickAdapter<String, BaseViewHolder>(resId) {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.indexTv, item)
            //区分颜色
            if (holder.adapterPosition % 2 == 0) {
                holder.setBackgroundColor(R.id.bg, resources.getColor(R.color.teal_200))
            } else {
                holder.setBackgroundColor(R.id.bg, resources.getColor(R.color.teal_700))
            }
        }
    }

    //自定义侧滑Callback
    class MySwipeMenuCallback : ItemDecorationSwipeMenuCallback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val position = viewHolder.adapterPosition
            return if (position % 2 == 0) {
                ItemTouchHelper.LEFT
            } else {
                FLAG_NONE
            }
        }

        override fun getSwipeMenuMaxWidth(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return 200
        }

        override fun onSwipeTo(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            x: Float,
            y: Float
        ) {
            SwipeUtils.onItemSwipeTo(viewHolder, x, y)
        }
    }
}