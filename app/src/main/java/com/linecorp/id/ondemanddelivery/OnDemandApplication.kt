package com.linecorp.id.ondemanddelivery

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat

class OnDemandApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}
