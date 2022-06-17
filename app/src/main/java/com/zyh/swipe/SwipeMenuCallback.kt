package com.zyh.swipe

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

open class SwipeMenuCallback {

    companion object {

        /**无*/
        const val FLAG_NONE = 0

        /**全方向*/
        const val FLAG_ALL = ItemTouchHelper.LEFT or
                ItemTouchHelper.RIGHT or
                ItemTouchHelper.DOWN or
                ItemTouchHelper.UP

        /**垂直方向*/
        const val FLAG_VERTICAL = ItemTouchHelper.DOWN or ItemTouchHelper.UP

        /**水平方向*/
        const val FLAG_HORIZONTAL = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

    }

    open fun getMovementFlags(recyclerView: RecyclerView,
                              viewHolder: RecyclerView.ViewHolder): Int{
        //来返回viewHolder的滑动方向
        return SwipeMenuCallback.FLAG_HORIZONTAL
    }

    open fun getSwipeThreshold(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Float {
        return 0.3f
    }

    open fun getSwipeMaxWidth(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return SwipeUtils.getSwipeMenuWidth(viewHolder)
    }

    open fun getSwipeMaxHeight(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return SwipeUtils.getSwipeMenuHeight(viewHolder)
    }

    open fun getSwipeVelocityThreshold(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        defaultValue: Float
    ): Float {
        return defaultValue
    }

    open fun onSwipeTo(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        x: Float,
        y: Float
    ) {
        SwipeUtils.onItemSwipeMenuTo(viewHolder,x,y)
    }

}