package com.zyh.swipe

import android.view.ViewGroup
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.RecyclerView
import com.zyh.swipe.SwipeMenuHelper.Companion.SWIPE_MENU_TYPE_FLOWING

//侧滑菜单相关的
object SwipeUtils {

    /**
     * 获取ViewHolder的侧滑菜单宽度，这里必须要求是
     * 侧滑菜单在第一个
     * */
    fun getSwipeMenuWidth(itemHolder: RecyclerView.ViewHolder): Int{
        return itemHolder.itemView.getChildOrNull(0).mW()
    }

    fun getSwipeMenuHeight(itemHolder: RecyclerView.ViewHolder): Int{
        return itemHolder.itemView.getChildOrNull(0).mH()
    }

    /**请将menu, 布局在第1个child的位置, 并且布局的[left]和[top]都是0
     * 默认的UI效果就是, TranslationX.
     * 默认实现暂时只支持左/右滑动的菜单, 上/下滑动菜单不支持
     * */
    open fun onItemSwipeMenuTo(itemHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,itemSwipeMenuType: Int = SWIPE_MENU_TYPE_FLOWING) {
        val parent = itemHolder.itemView
        if (parent is ViewGroup && parent.childCount > 1) {
            //菜单最大的宽度, 用于限制滑动的边界
            val menuWidth = getSwipeMenuWidth(itemHolder)
            val tX = MathUtils.clamp(dX, -menuWidth.toFloat(), menuWidth.toFloat())
            parent.forEach { index, child ->
                if (index == 0) {
                    if (itemSwipeMenuType == SWIPE_MENU_TYPE_FLOWING) {
                        if (dX > 0) {
                            child.translationX = -menuWidth + tX
                        } else {
                            child.translationX = parent.mW() + tX
                        }
                    } else {
                        if (dX > 0) {
                            child.translationX = 0f
                        } else {
                            child.translationX = (parent.mW() - menuWidth).toFloat()
                        }
                    }
                } else {
                    child.translationX = tX
                }
            }
        }
    }

}