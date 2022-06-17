package com.zyh

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.zyh.swipe.R
import kotlinx.android.synthetic.main.activity_image_test.*

class ImageTestActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ImageTest"
    }

    val images = arrayOf(R.mipmap.bg_splash,R.mipmap.test)
    var number = 0
    lateinit var bitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_test)

        testBitmapSize()
        testBitmapSize1()
        testBitmapSize2()

        val option = BitmapFactory.Options()
        option.inMutable = true
        bitmap = BitmapFactory.decodeResource(resources, images[1], option)

        btn.setOnClickListener {
            number ++
//            setImageView()
            //复用Bitmap
            setImageView1()
        }
    }

    private fun setImageView(){
        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeResource(resources,images[number % 2], options)
        val height = bitmap.height
        val width = bitmap.width
        val allocationByteCount = bitmap.allocationByteCount
        val byteCount = bitmap.byteCount
        val density = bitmap.density
        val mutable = bitmap.isMutable
        //这里可以看出一个像素占4个字节  图片内存34M
        Log.i(
            TAG, "setImageView: height ：$height " +
                    "width ：$width " +
                    "allocationByteCount ：$allocationByteCount " +
                    "byteCount ：$byteCount " +
                    "density ：$density " +
                    "mutable ：$mutable "
        )
        //设置图片
        image.setImageBitmap(bitmap)
    }

    //复用Bitmap
    private fun setImageView1(){
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inMutable = true
        options.inBitmap = bitmap
        options.inJustDecodeBounds = false
        bitmap = BitmapFactory.decodeResource(resources,images[number % 2], options)
        val height = bitmap.height
        val width = bitmap.width
        val allocationByteCount = bitmap.allocationByteCount
        val byteCount = bitmap.byteCount
        val density = bitmap.density
        val mutable = bitmap.isMutable
        //这里可以看出一个像素占4个字节  图片内存34M
        Log.i(
            TAG, "setImageView: height ：$height " +
                    "width ：$width " +
                    "allocationByteCount ：$allocationByteCount " +
                    "byteCount ：$byteCount " +
                    "density ：$density " +
                    "mutable ：$mutable "
        )
        //设置图片
        image.setImageBitmap(bitmap)
    }

    /**
     * 在mipmap文件夹中的图片，被加载时，Bitmap占用内存巨大
     * */
    private fun testBitmapSize() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.test)
        val height = bitmap.height
        val width = bitmap.width
        val allocationByteCount = bitmap.allocationByteCount
        val byteCount = bitmap.byteCount
        val density = bitmap.density
        val mutable = bitmap.isMutable
        //这里可以看出一个像素占4个字节  图片内存34M
        Log.i(
            TAG, "testBitmapSize: height ：$height " +
                    "width ：$width " +
                    "allocationByteCount ：$allocationByteCount " +
                    "byteCount ：$byteCount " +
                    "density ：$density " +
                    "mutable ：$mutable "
        )
    }

    /**
     * 图片在assert中的大小，不会进行缩放，就是该Bitmap该再用的大小
     * 即宽 * 高 * 像素点内存(4)
     *
     * */
    private fun testBitmapSize1() {
        val stream = assets.open("test.jpg")
        val bitmap = BitmapFactory.decodeStream(stream)
        val height = bitmap.height
        val width = bitmap.width
        val allocationByteCount = bitmap.allocationByteCount
        val byteCount = bitmap.byteCount
        val density = bitmap.density
        val mutable = bitmap.isMutable
        //这里可以看出一个像素占4个字节  图片内存34M
        Log.i(
            TAG, "testBitmapSize1: height ：$height " +
                    "width ：$width " +
                    "allocationByteCount ：$allocationByteCount " +
                    "byteCount ：$byteCount " +
                    "density ：$density " +
                    "mutable ：$mutable "
        )
    }

    /**
     * 图片还在assert中的大小，
     * 即宽 * 高 * 像素点内存(4)，
     * 这里改变每个像素点的内存占用
     * 从ARGB_8888改成ARGB_565，即修改Config
     *
     * */
    private fun testBitmapSize2() {
        val stream = assets.open("test.jpg")
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inSampleSize = 2
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.test, options)
//        val bitmap = BitmapFactory.decodeStream(stream,null,options)!!
        val height = bitmap.height
        val width = bitmap.width
        val allocationByteCount = bitmap.allocationByteCount
        val byteCount = bitmap.byteCount
        val density = bitmap.density
        val mutable = bitmap.isMutable
        //这里可以看出一个像素占4个字节  图片内存34M
        Log.i(
            TAG, "testBitmapSize2: height ：$height " +
                    "width ：$width " +
                    "allocationByteCount ：$allocationByteCount " +
                    "byteCount ：$byteCount " +
                    "density ：$density " +
                    "mutable ：$mutable "
        )
    }

}