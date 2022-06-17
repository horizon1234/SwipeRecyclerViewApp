package com.zyh.cache

import android.graphics.Bitmap
import android.util.LruCache

class LruCacheTest {

    //当前进程的最大内存
    val maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
    //获取内存的1/8
    val cacheMaxSize = maxMemory / 8

    val bitmapLruCache = object : LruCache<String, Bitmap>(cacheMaxSize.toInt()){

        override fun sizeOf(key: String?, value: Bitmap?): Int {
            return value!!.byteCount / 1024 / 1024
        }

        override fun entryRemoved(
            evicted: Boolean,
            key: String?,
            oldValue: Bitmap?,
            newValue: Bitmap?
        ) {
            //移除旧缓存时会调用

        }
    }



    //添加缓存
    fun putBitmap(key: String,bitmap: Bitmap){
        bitmapLruCache.put(key, bitmap)
    }

    //获取缓存
    fun getBitmap(key: String){
        bitmapLruCache.get(key)
    }
}