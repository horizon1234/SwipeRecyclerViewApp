package com.zyh.swipeViewHolder

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


fun View.getChildOrNull(index: Int): View? {
    return if (this is ViewGroup) {
        this.getChildOrNull(index)
    } else {
        this
    }
}

/**获取指定位置[index]的[child], 如果有.*/
fun ViewGroup.getChildOrNull(index: Int): View? {
    return if (index in 0 until childCount) {
        getChildAt(index)
    } else {
        null
    }
}

fun View?.mW(def: Int = 0): Int {
    return this?.measuredWidth ?: def
}

fun View?.mH(def: Int = 0): Int {
    return this?.measuredHeight ?: def
}

fun Int.have(value: Int): Boolean = if (this == 0 || value == 0) false
else if (this == 0 && value == 0) true
else {
    ((this > 0 && value > 0) || (this < 0 && value < 0)) &&
            this and value == value
}


/**[androidx/core/animation/Animator.kt:82]*/
inline fun Animator.addListener(
    crossinline onEnd: (animator: Animator) -> Unit = {},
    crossinline onStart: (animator: Animator) -> Unit = {},
    crossinline onCancel: (animator: Animator) -> Unit = {},
    crossinline onRepeat: (animator: Animator) -> Unit = {}
): Animator.AnimatorListener {
    val listener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animator: Animator) = onRepeat(animator)
        override fun onAnimationEnd(animator: Animator) = onEnd(animator)
        override fun onAnimationCancel(animator: Animator) = onCancel(animator)
        override fun onAnimationStart(animator: Animator) = onStart(animator)
    }
    addListener(listener)
    return listener
}

fun ViewGroup.forEach(recursively: Boolean = false, map: (index: Int, child: View) -> Unit) {
    eachChild(recursively, map)
}

/**枚举所有child view
 * [recursively] 递归所有子view*/
fun ViewGroup.eachChild(recursively: Boolean = false, map: (index: Int, child: View) -> Unit) {
    for (index in 0 until childCount) {
        val childAt = getChildAt(index)
        map.invoke(index, childAt)
        if (recursively && childAt is ViewGroup) {
            childAt.eachChild(recursively, map)
        }
    }
}

fun Context.dip2px(dip: Float): Float{
    return this.resources.displayMetrics.density * dip + 0.5F
}