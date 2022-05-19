package com.linecorp.id.ondemanddelivery

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat

abstract class BaseSplitActivity : AppCompatActivity() {

    override fun attachBaseContext(ctx: Context?) {
        super.attachBaseContext(ctx)
        SplitCompat.install(this)
    }
}
