package com.zyh.swipe

import android.animation.ValueAnimator
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.math.MathUtils
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zyh.swipe.SwipeMenuCallback.Companion.DOWN
import com.zyh.swipe.SwipeMenuCallback.Companion.LEFT
import com.zyh.swipe.SwipeMenuCallback.Companion.RIGHT
import com.zyh.swipe.SwipeMenuCallback.Companion.UP
import kotlin.math.abs
import kotlin.math.absoluteValue

class SwipeMenuHelper(var swipeMenuCallback: SwipeMenuCallback) :
    RecyclerView.OnChildAttachStateChangeListener {

    private var _recyclerView: RecyclerView? = null

    /**当前打开的菜单ViewHolder*/
    var _swipeMenuViewHolder: RecyclerView.ViewHolder? = null

    //按下的ViewHolder
    var _downViewHolder: RecyclerView.ViewHolder? = null

    //是否需要处理事件
    var _needHandleTouch = true

    //当前正在进行左右滑or上下滑
    var _swipeFlags: Int = 0

    var _slop = 0

    var _lastDistanceX = 0f
    var _lastDistanceY = 0f

    var _lastVelocityX = 0f
    var _lastVelocityY = 0f

    var _lastValueAnimator: ValueAnimator? = null

    /**当前滚动的距离*/
    var _scrollX = 0f
    var _scrollY = 0f

    val mOnItemTouchListener = object : RecyclerView.OnItemTouchListener {

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            if (_needHandleTouch) {
                gestureDetectorCompat?.onTouchEvent(e)
            }
            val actionMasked = e.actionMasked
            if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
                touchFinish()
            }
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return when (val actionMasked = e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i(TAG, "onInterceptTouchEvent: ACTION_DOWN事件")
                    _resetScrollValue()
                    gestureDetectorCompat?.onTouchEvent(e)
                }
                else -> {
                    Log.i(TAG, "onInterceptTouchEvent: 是否拦截 $e")
                    if (_needHandleTouch) {
                        Log.i(TAG, "onInterceptTouchEvent: 需要继续处理")
                        gestureDetectorCompat?.onTouchEvent(e)
                    } else {
                        Log.i(TAG, "onInterceptTouchEvent: 不需要继续处理")
                        if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
                            Log.i(TAG, "onInterceptTouchEvent: 处理UP事件")
                            touchFinish()
                        }
                        false
                    }
                }
            } ?: false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            if (!disallowIntercept) {
                return
            }
        }

        fun touchFinish() {
            if (_needHandleTouch) {
                val downViewHolder = _downViewHolder
                val recyclerView = _recyclerView
                if (recyclerView != null && downViewHolder != null) {

                    val swipeFlag =
                        swipeMenuCallback.getMovementFlags(recyclerView, downViewHolder)

                    val swipeThreshold =
                        swipeMenuCallback.getSwipeThreshold(recyclerView, downViewHolder)

                    //左右滑动菜单
                    val swipeMaxWidth =
                        swipeMenuCallback.getSwipeMenuMaxWidth(recyclerView, downViewHolder)
                            .toFloat()

                    val swipeMaxHeight =
                        swipeMenuCallback.getSwipeMenuMaxHeight(recyclerView, downViewHolder)
                            .toFloat()

                    //宽度阈值
                    val swipeWidthThreshold = swipeMaxWidth * swipeThreshold
                    val swipeHeightThreshold = swipeMaxHeight * swipeThreshold

                    Log.i(TAG, "touchFinish: 侧滑菜单宽度 $swipeMaxWidth  宽度阈值$swipeWidthThreshold")

                    //速率阈值
                    val swipeVelocityXThreshold = swipeMenuCallback.getSwipeVelocityThreshold(
                        recyclerView,
                        downViewHolder,
                        _lastVelocityX
                    )
                    val swipeVelocityYThreshold = swipeMenuCallback.getSwipeVelocityThreshold(
                        recyclerView,
                        downViewHolder,
                        _lastVelocityY
                    )

                    if (_swipeFlags == SwipeMenuCallback.FLAG_HORIZONTAL) {
                        if (_lastVelocityX != 0f && _lastVelocityX.absoluteValue >= swipeVelocityXThreshold) {
                            Log.i(TAG, "touchFinish: 左右最后是Fling状态")
                            if (_scrollX < 0 && _lastVelocityX < 0 && swipeFlag.have(LEFT)) {
                                Log.i(TAG, "touchFinish: 向左快速Fling")
                                scrollSwipeMenuTo(downViewHolder, -swipeMaxWidth, 0f)
                            } else if (_scrollX > 0 &&
                                _lastVelocityX > 0 &&
                                swipeFlag.have(RIGHT)
                            ) {
                                Log.i(TAG, "touchFinish: 向右快速Fling")
                                scrollSwipeMenuTo(downViewHolder, swipeMaxWidth, 0f)
                            } else {
                                Log.i(TAG, "touchFinish: 关闭")
                                closeSwipeMenu(downViewHolder)
                            }
                        } else {
                            Log.i(TAG, "touchFinish: 左右最后是Scroll")
                            if (_scrollX < 0) {
                                Log.i(TAG, "touchFinish: ViewHolder已经左滑 _scrollX = $_scrollX _lastDistance = $_lastDistanceX")
                                Log.i(TAG, "touchFinish: ViewHolder已经左滑 阈值是$swipeWidthThreshold  反向阈值是${swipeWidthThreshold - swipeMaxWidth}")
                                if ((_lastDistanceX > 0 && _scrollX.absoluteValue >= swipeWidthThreshold) ||
                                    (_lastDistanceX < 0 && (swipeMaxWidth + _scrollX) < swipeWidthThreshold)
                                ) {
                                    //意图打开右边的菜单
                                    scrollSwipeMenuTo(downViewHolder, -swipeMaxWidth, 0f)
                                } else {
                                    //关闭菜单
                                    closeSwipeMenu(downViewHolder)
                                }
                            } else if (_scrollX > 0) {
                                if ((_lastDistanceX < 0 && _scrollX.absoluteValue >= swipeWidthThreshold) ||
                                    (_lastDistanceX > 0 && (swipeMaxWidth - _scrollX) < swipeWidthThreshold)
                                ) {
                                    //意图打开左边的菜单
                                    scrollSwipeMenuTo(downViewHolder, swipeMaxWidth, 0f)
                                } else {
                                    //关闭菜单
                                    closeSwipeMenu(downViewHolder)
                                }
                            }
                        }
                    } else if (_swipeFlags == SwipeMenuCallback.FLAG_VERTICAL) {
                        //上下滑动菜单
                        if (_lastVelocityY != 0f && _lastVelocityY.absoluteValue >= swipeVelocityYThreshold) {
                            //fling
                            if (_scrollY < 0 && _lastVelocityY < 0 && swipeFlag.have(DOWN)) {
                                //向下快速fling
                                scrollSwipeMenuTo(downViewHolder, 0f, swipeMaxHeight)
                            } else if (_scrollY > 0 &&
                                _lastVelocityY > 0 &&
                                swipeFlag.have(UP)
                            ) {
                                scrollSwipeMenuTo(downViewHolder, 0f, -swipeMaxHeight)
                            } else {
                                closeSwipeMenu(downViewHolder)
                            }
                        } else {
                            //scroll
                            if (_scrollY < 0) {
                                if ((_lastDistanceY > 0 && _scrollY.absoluteValue >= swipeHeightThreshold) ||
                                    (_lastDistanceY < 0 && (swipeMaxHeight + _scrollY) < swipeHeightThreshold)
                                ) {
                                    //意图打开下边的菜单
                                    scrollSwipeMenuTo(downViewHolder, 0f, -swipeMaxHeight)
                                } else {
                                    //关闭菜单
                                    closeSwipeMenu(downViewHolder)
                                }
                            } else if (_scrollY > 0) {
                                if ((_lastDistanceY < 0 && _scrollY.absoluteValue >= swipeHeightThreshold) ||
                                    (_lastDistanceY > 0 && (swipeMaxHeight - _scrollY) < swipeHeightThreshold)
                                ) {
                                    //意图打开上边的菜单
                                    scrollSwipeMenuTo(downViewHolder, 0f, swipeMaxHeight)
                                } else {
                                    //关闭菜单
                                    closeSwipeMenu(downViewHolder)
                                }
                            }
                        }
                    }
                }
            }

            _downViewHolder = null
            _needHandleTouch = true
            _swipeFlags = 0
            _recyclerView?.parent?.requestDisallowInterceptTouchEvent(false)
        }
    }

    private val itemTouchHelperGestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            Log.i(TAG, "onDown: 触摸到屏幕，开始处理")
            if (e != null) {
                val findSwipedView = findSwipedView(e)
                if (findSwipedView == null) {
                    Log.i(TAG, "onDown: 没有点击到ViewHolder上")
                    _needHandleTouch = false
                    closeSwipeMenu(_swipeMenuViewHolder)
                } else {
                    findSwipedView.apply {
                        if (_lastValueAnimator?.isRunning == true ||
                            (_downViewHolder != null && _downViewHolder != this)
                        ) {
                            Log.i(TAG, "onDown: 动画还没有完成")
                            _needHandleTouch = false
                        } else {
                            _downViewHolder = this
                            if (_swipeMenuViewHolder != null && _downViewHolder != _swipeMenuViewHolder) {
                                Log.i(TAG, "onDown: 收起其他已展开的ViewHolder")
                                _needHandleTouch = false
                                closeSwipeMenu(_swipeMenuViewHolder)
                            }
                        }
                    }
                }
            } else {
                _needHandleTouch = false
            }
            return super.onDown(e)
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            Log.i(TAG, "onScroll: $e1  $e2  $distanceX  $distanceY")
            val absDx: Float = abs(distanceX)
            val absDy: Float = abs(distanceY)
            if (absDx >= _slop || absDy >= _slop) {
                if (absDx > absDy) {
                    _lastDistanceX = distanceX
                } else {
                    _lastDistanceY = distanceY
                }
            }

            _lastVelocityX = 0f
            _lastVelocityY = 0f

            val downViewHolder = _downViewHolder
            val recyclerView = _recyclerView

            if (recyclerView != null && downViewHolder != null) {
                val swipeFlag =
                    swipeMenuCallback.getMovementFlags(recyclerView, downViewHolder)
                Log.i(TAG, "onScroll: 被按下ViewHolder的swipeFlag = $swipeFlag")
                if (swipeFlag <= 0) {
                    Log.i(TAG, "onScroll: 不需要继续处理")
                    _needHandleTouch = false
                } else {
                    //本次滑动的意图方向
                    val flag: Int = if (absDx > absDy) {
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    } else {
                        ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    }
                    Log.i(TAG, "onScroll: 本次意图的方向 $flag")
                    if (_swipeFlags == 0) {
                        _swipeFlags = flag
                    }
                    val swipeMaxWidth =
                        swipeMenuCallback.getSwipeMenuMaxWidth(recyclerView, downViewHolder)
                            .toFloat()
                    val swipeMaxHeight =
                        swipeMenuCallback.getSwipeMenuMaxHeight(recyclerView, downViewHolder)
                            .toFloat()

                    _scrollX -= distanceX
                    _scrollX = MathUtils.clamp(_scrollX, -swipeMaxWidth, swipeMaxWidth)
                    _scrollY -= distanceY
                    _scrollY = MathUtils.clamp(_scrollY, -swipeMaxHeight, swipeMaxHeight)
                    Log.i(TAG, "onScroll: _scrollX = $_scrollX  _scrollY = $_scrollY")
                    if (_swipeFlags == SwipeMenuCallback.FLAG_HORIZONTAL) {
                        Log.i(TAG, "onScroll: 本次滑动是左右")
                        if (swipeFlag.have(ItemTouchHelper.LEFT) ||
                            swipeFlag.have(ItemTouchHelper.RIGHT)
                        ) {
                            _scrollY = 0f
                            if (_scrollX < 0 && swipeFlag and ItemTouchHelper.LEFT == 0) {
                                Log.i(TAG, "onScroll: 手势左滑，但是不具备左侧滑")
                                _scrollX = 0f
                            } else if (_scrollX > 0 && swipeFlag and ItemTouchHelper.RIGHT == 0) {
                                Log.i(TAG, "onScroll: 手势右滑，但是不具备右滑")
                                _scrollX = 0f
                            } else {
                                _recyclerView?.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                        } else {
                            Log.i(TAG, "onScroll: 手势左右滑动，但是该viewHolder不具备")
                            _swipeFlags = 0
                            _needHandleTouch = false
                            _scrollX = 0f
                            if (_swipeMenuViewHolder == _downViewHolder) {
                                //已经打开了按下的菜单, 但是菜单缺没有此方向的滑动flag
                                //则关闭菜单
                                Log.i(TAG, "onScroll: 左右滑动 flag是上下的")
                                closeSwipeMenu(_swipeMenuViewHolder)
                                return _needHandleTouch
                            } else {
                                _scrollY = 0f
                            }
                        }
                    } else {
                        Log.i(TAG, "onScroll: 本次手势滑动是上下方向")
                        if (swipeFlag.have(ItemTouchHelper.UP) || swipeFlag.have(ItemTouchHelper.DOWN)) {
                            _scrollX = 0f
                            if (_scrollY < 0 && swipeFlag and ItemTouchHelper.DOWN == 0) {
                                //不具备向下滑动
                                _scrollY = 0f
                            } else if (_scrollY > 0 && swipeFlag and ItemTouchHelper.UP == 0) {
                                //不具备向上滑动
                                _scrollY = 0f
                            } else {
                                _recyclerView?.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                        } else {
                            Log.i(TAG, "onScroll: 手势上下，但是没有对应swipeFlag，白搭")
                            _swipeFlags = 0
                            _needHandleTouch = false
                            _scrollY = 0f
                            if (_swipeMenuViewHolder == _downViewHolder) {
                                //已经打开了按下的菜单, 但是菜单缺没有此方向的滑动flag
                                //则关闭菜单
                                Log.i(TAG, "onScroll: 手势上下，flag是左右的")
                                closeSwipeMenu(_swipeMenuViewHolder)
                                return _needHandleTouch
                            } else {
                                _scrollX = 0f
                            }
                        }
                    }

                    swipeMenuCallback.onSwipeTo(
                        recyclerView,
                        downViewHolder,
                        _scrollX,
                        _scrollY
                    )
                }
            } else {
                _needHandleTouch = false
            }

            return _needHandleTouch
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            //L.i("velocityX:$velocityX velocityY:$velocityY")
            _lastVelocityX = velocityX
            _lastVelocityY = velocityY
            return super.onFling(e1, e2, velocityX, velocityY)
        }

    }

    var gestureDetectorCompat: GestureDetectorCompat? = null

    companion object {

        const val TAG = "SwipeMenuHelper"

        /**滑动菜单滑动方式, 默认. 固定在底部*/
        const val SWIPE_MENU_TYPE_DEFAULT = 0x1

        /**滑动菜单滑动方式, 跟随在内容后面*/
        const val SWIPE_MENU_TYPE_FLOWING = 0x2

        /**安装*/
        fun install(recyclerView: RecyclerView?): SwipeMenuHelper {
            val slideMenuHelper = SwipeMenuHelper(SwipeMenuCallback())
            slideMenuHelper.attachToRecyclerView(recyclerView)
            return slideMenuHelper
        }

        fun install(
            recyclerView: RecyclerView?,
            swipeMenuCallback: SwipeMenuCallback
        ): SwipeMenuHelper {
            val slideMenuHelper = SwipeMenuHelper(swipeMenuCallback)
            slideMenuHelper.attachToRecyclerView(recyclerView)
            return slideMenuHelper
        }
    }

    fun closeSwipeByPosition(position: Int){
        val viewHolder = _recyclerView?.findViewHolderForAdapterPosition(position)
        closeSwipeMenu(viewHolder)
    }

    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (_recyclerView === recyclerView) {
            return  // nothing to do
        }
        if (_recyclerView != null) {
            destroyCallbacks()
        }
        _recyclerView = recyclerView
        if (recyclerView != null) {
            setupCallbacks()
        }
    }

    private fun setupCallbacks() {
        _recyclerView?.apply {
            val vc = ViewConfiguration.get(context)
            _slop = vc.scaledTouchSlop
//            addItemDecoration(this@SwipeMenuHelper)
            addOnItemTouchListener(mOnItemTouchListener)
            addOnChildAttachStateChangeListener(this@SwipeMenuHelper)
            startGestureDetection()
        }
    }

    private fun destroyCallbacks() {
        _recyclerView?.apply {
//            removeItemDecoration(this@SwipeMenuHelper)
            removeOnItemTouchListener(mOnItemTouchListener)
            removeOnChildAttachStateChangeListener(this@SwipeMenuHelper)
            stopGestureDetection()
        }
    }

    private fun startGestureDetection() {
        gestureDetectorCompat =
            GestureDetectorCompat(_recyclerView!!.context, itemTouchHelperGestureListener)
        gestureDetectorCompat?.setIsLongpressEnabled(false)
    }

    private fun stopGestureDetection() {
        gestureDetectorCompat = null
    }

    fun _resetScrollValue() {
        _lastVelocityX = 0f
        _lastVelocityY = 0f
        _lastDistanceX = 0f
        _lastDistanceY = 0f
        _swipeFlags = 0
    }

    private fun findSwipedView(motionEvent: MotionEvent): RecyclerView.ViewHolder? {
        val child: View = findChildView(motionEvent) ?: return null
        return _recyclerView?.getChildViewHolder(child)
    }

    fun findChildView(event: MotionEvent): View? {
        val x = event.x
        val y = event.y
        return _recyclerView?.findChildViewUnder(x, y)
    }

    fun closeSwipeMenu(viewHolder: RecyclerView.ViewHolder? = _swipeMenuViewHolder) {
        viewHolder?.apply {
            scrollSwipeMenuTo(this, 0f, 0f)
        }
    }

    fun scrollSwipeMenuTo(viewHolder: RecyclerView.ViewHolder, x: Float = 0f, y: Float = 0f) {
        if (_lastValueAnimator?.isRunning == true) {
            return
        }
        _recyclerView?.apply {
            val startX = _scrollX
            val startY = _scrollY

            if (x != 0f || y == 0f) {
                //将要打开的菜单
                _swipeMenuViewHolder = viewHolder
            }

            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.addUpdateListener {
                val fraction: Float = it.animatedValue as Float
                val currentX = startX + (x - startX) * fraction
                val currentY = startY + (y - startY) * fraction

                _scrollX = currentX
                _scrollY = currentY

                swipeMenuCallback.onSwipeTo(_recyclerView!!, viewHolder, currentX, currentY)
            }
            valueAnimator.addListener(onEnd = {
                if (x == 0f && y == 0f) {
                    //关闭菜单
                    _swipeMenuViewHolder = null
                } else {
                    _swipeMenuViewHolder = viewHolder
                }
                _lastValueAnimator = null
            }, onCancel = {
                _lastValueAnimator = null
            })
            valueAnimator.duration =
                ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION.toLong()
            valueAnimator.start()
            _lastValueAnimator = valueAnimator
        }
    }

    override fun onChildViewAttachedToWindow(view: View) {
        _recyclerView?.apply {
            getChildViewHolder(view)?.let { block ->
                swipeMenuCallback.onSwipeTo(_recyclerView!!, block, 0F, 0F)
            }
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        _recyclerView?.apply {
            getChildViewHolder(view)?.apply {
                if (this == _swipeMenuViewHolder) {
                    _resetScrollValue()
                    _scrollX = 0f
                    _scrollY = 0f
                    swipeMenuCallback.onSwipeTo(_recyclerView!!, this, 0f, 0f)
                    _swipeMenuViewHolder = null
                }
                if (this == _downViewHolder) {
                    _downViewHolder = null
                }
            }
        }
    }


}