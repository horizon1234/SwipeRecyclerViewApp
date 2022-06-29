package com.zyh.itemDecoration
import android.app.Application
import android.content.res.Resources
import android.graphics.*

/**
 * 侧滑按钮，
 * */
class SwipeButton(
    private val buttonText: String = "测试",
    private val buttonColor: Int = Color.parseColor("#5297FF"),
    private val textColor: Int = Color.parseColor("#FFFFFF"),
    private val clickListener: ((Int) -> Unit)? = null) {

    //当前按钮可以点击的范围，这里默认其实就是Button的范围
    var clickRegion: RectF? = null
    //当前按钮所绑定的RecyclerView的position
    private var position: Int = 0

    //点击事件是否在按钮点击范围内，这个由RecyclerView的点击事件会调用
    fun onClick(x: Float, y: Float): Boolean {
        if (clickRegion != null && clickRegion?.contains(x, y)!!) {
            clickListener?.invoke(position)
            return true
        }
        return false
    }


    //绘制，这里因为按钮比较简单，所以直接使用代码绘制
    //参数canvas和rectF都是第三方调用者传入
    fun onDraw(canvas: Canvas, rectF: RectF, position: Int) {
        val paint = Paint()
        //设置按钮背景颜色
        paint.color = buttonColor
        canvas.drawRect(rectF, paint)
        //设置按钮文本颜色
        paint.color = textColor
        //设置文字大小
        paint.textSize = 18 * Resources.getSystem().displayMetrics.density
        paint.textAlign = Paint.Align.LEFT

        //在rectF上绘制文本
        val tempRect = Rect()
        val height = rectF.height()
        val width = rectF.width()
        paint.getTextBounds(buttonText, 0, buttonText.length, tempRect)
        val x = width / 2f - tempRect.width() / 2f - tempRect.left
        val y = height / 2f + tempRect.height() / 2f - tempRect.bottom
        canvas.drawText(buttonText, rectF.left + x, rectF.top + y, paint)
        //记录点击区域，这里这样记录点击区域的话，也就是在每次RecyclerView按钮绘制时都是最新的
        clickRegion = rectF
        this.position = position
    }

}