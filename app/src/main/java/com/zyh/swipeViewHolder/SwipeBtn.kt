package com.zyh.swipeViewHolder

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import java.util.jar.Attributes

class SwipeBtn @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    style: Int = 0
) :
    AppCompatTextView(context, attributes, style) {

    init {
        setPadding(20)
        gravity = Gravity.CENTER
    }
}

class SwipeBtnContainer @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    style: Int = 0
) :
    LinearLayout(context, attributes, style) {

    fun addViews(views: ArrayList<View>) {
        super.removeAllViews()
        for (view in views) {
            addView(view)
        }
    }

    override fun addView(child: View?) {
        val param = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        super.addView(child, param)
    }

}