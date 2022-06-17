package com.zyh.cache

import android.app.Application
import android.content.Context
import android.os.Environment
import com.jakewharton.disklrucache.DiskLruCache

class DiskLruCacheTest(context: Context) {

    val diskLruCache = DiskLruCache.open(context.filesDir,1,1,50*1000*1000)

}