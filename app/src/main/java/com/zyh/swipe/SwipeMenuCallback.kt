package com.zyh.swipe

import androidx.recyclerview.widget.RecyclerView

open class SwipeMenuCallback {

    companion object {
        //上
        const val UP = 1
        //下
        const val DOWN = 1 shl 1
        //左
        const val LEFT = 1 shl 2
        //右
        const val RIGHT = 1 shl 3
        //无侧滑
        const val FLAG_NONE = 0
        //全方向
        const val FLAG_ALL = LEFT or
                RIGHT or
                DOWN or
                UP
        //竖直方向
        const val FLAG_VERTICAL = DOWN or UP
        //水平方向
        const val FLAG_HORIZONTAL = LEFT or RIGHT
    }

    open fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        //默认可以向左侧滑
        return LEFT
    }

    open fun getSwipeThreshold(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Float {
        //侧滑阈值，默认0.5F
        return 0.3F
    }

    open fun getSwipeMenuMaxWidth(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return SwipeUtils.getSwipeMenuWidth(viewHolder)
    }

    open fun getSwipeMenuMaxHeight(
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
        SwipeUtils.onItemSwipeMenuTo(viewHolder, x, y)
    }

}