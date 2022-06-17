package com.zyh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.zyh.swipe.R
import kotlinx.android.synthetic.main.activity_glide_test.*

class GlideTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_test)

        testGlide()
    }

    private fun testGlide() {
        Glide.with(this)
            .load("http://192.168.200.135:9699/minio/image-operation/4c1c53e0-9c77-422e-bd5c-e571d5ade19b.jpg?response-content-type=application%2Foctet-stream&response-content-disposition=attachment%3Bfilename%3DIMG_20220501_171829.jpg&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20220617%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220617T081006Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=c642d103edb4af1dedcf20a01a103040c715fca429770387c08e93f00184898c")
            .into(glideIv)
    }
}