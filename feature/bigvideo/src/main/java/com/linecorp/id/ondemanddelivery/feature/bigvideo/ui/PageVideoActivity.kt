package com.linecorp.id.ondemanddelivery.feature.bigvideo.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.linecorp.id.ondemanddelivery.BaseSplitActivity
import com.linecorp.id.ondemanddelivery.feature.bigvideo.R
import com.linecorp.id.ondemanddelivery.feature.bigvideo.databinding.ActivityPageVideoBinding

class PageVideoActivity : BaseSplitActivity() {

    private lateinit var binding: ActivityPageVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPlayVideo.setOnClickListener {
            Toast.makeText(
                this@PageVideoActivity, "Play Video Clicked",
                Toast.LENGTH_SHORT
            ).show()

            binding.buttonPlayVideo.visibility = View.GONE

            val path = "android.resource://" + packageName + "/" + R.raw.big_buck_bunny
            binding.videoViewPlayer.setVideoURI(Uri.parse(path))
            binding.videoViewPlayer.start()
        }
    }
}
