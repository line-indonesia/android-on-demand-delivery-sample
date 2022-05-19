package com.linecorp.id.ondemanddelivery

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.linecorp.id.ondemanddelivery.databinding.ActivityMainBinding

class MainActivity : BaseSplitActivity() {

    private lateinit var binding: ActivityMainBinding

    private val listener = SplitInstallStateUpdatedListener { state ->
        val multiInstall = state.moduleNames().size > 1
        val names = state.moduleNames().joinToString(" - ")

        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val totalBytes = state.totalBytesToDownload()
                val progress = state.bytesDownloaded()
                val percentage = (progress * 100 / totalBytes).toInt()
                displayLoadingState(
                    state,
                    "Downloading $names\n$progress of $totalBytes bytes ($percentage%)"
                )
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                //In some cases, Google Play may require user confirmation before satisfying a download request. For example, if your app has not been installed by Google Play
                manager.startConfirmationDialogForResult(state, this, CONFIRMATION_REQUEST_CODE)
            }
            SplitInstallSessionStatus.INSTALLED -> {
                onSuccessfulLoad(names, launch = !multiInstall)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                displayLoadingState(state, "Installing $names")
            }
            SplitInstallSessionStatus.FAILED -> {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${state.errorCode()} for module $names",
                    Toast.LENGTH_SHORT
                ).show()
            }
            SplitInstallSessionStatus.CANCELED -> {
                Toast.makeText(
                    this@MainActivity,
                    "Cancel download module $names",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private lateinit var manager: SplitInstallManager
    private val instantModuleName by lazy { getString(R.string.feature_name_instantmodule) }
    private val separateModuleName by lazy { getString(R.string.feature_name_separatemodule) }
    private val bigVideoName by lazy { getString(R.string.feature_name_bigvideo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manager = SplitInstallManagerFactory.create(this)
        binding.buttonInstantPageDetail.setOnClickListener { loadAndLaunchModule(instantModuleName) }
        binding.buttonOnDemandPageSeparate.setOnClickListener {
            loadAndLaunchModule(
                separateModuleName
            )
        }
        binding.buttonOnDemandPageVideo.setOnClickListener { loadAndLaunchModule(bigVideoName) }
        binding.buttonUninstall.setOnClickListener { requestUninstall() }
    }

    override fun onResume() {
        manager.registerListener(listener)
        super.onResume()
    }

    override fun onPause() {
        manager.unregisterListener(listener)
        super.onPause()
    }

    private fun requestUninstall() {
        Toast.makeText(
            this,
            "Requesting uninstall of all modules.\nThis will happen at some point in the future.",
            Toast.LENGTH_SHORT
        ).show()

        val installedModules = manager.installedModules.toList()
        if (installedModules.isEmpty()) {
            Toast.makeText(
                this,
                "No module to uninstall.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        manager.deferredUninstall(installedModules).addOnSuccessListener {
            Toast.makeText(
                this,
                "Uninstalling $installedModules",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Failed uninstall of $installedModules",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadAndLaunchModule(name: String) {
        updateProgressMessage("Loading module $name")

        //Log installed module for easier
        Toast.makeText(
            this, "InstalledModule: ${manager.installedModules}",
            Toast.LENGTH_SHORT
        ).show()

        //Check if module has been installed
        if (manager.installedModules.contains(name)) {
            launchActivityWithModuleName(name)
            displayButtons()
            return
        }

        //Request install request
        val request = SplitInstallRequest.newBuilder()
            .addModule(name)
            .build()

        //Start download immediately
        manager.startInstall(request)

        updateProgressMessage("Starting install for $name")
    }

    private fun displayLoadingState(state: SplitInstallSessionState, message: String) {
        displayProgress()

        binding.progressBarLoading.max = state.totalBytesToDownload().toInt()
        binding.progressBarLoading.progress = state.bytesDownloaded().toInt()

        updateProgressMessage(message)
    }

    private fun updateProgressMessage(message: String) {
        if (binding.linearLayoutLoading.visibility != View.VISIBLE) displayProgress()
        binding.textViewLoading.text = message
    }

    private fun displayProgress() {
        binding.linearLayoutLoading.visibility = View.VISIBLE
        binding.linearLayoutButton.visibility = View.GONE
    }

    private fun displayButtons() {
        binding.linearLayoutLoading.visibility = View.GONE
        binding.linearLayoutButton.visibility = View.VISIBLE
    }

    private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
        if (launch) {
            if (manager.installedModules.contains(moduleName)) {
                launchActivityWithModuleName(moduleName)
            } else {
                Toast.makeText(
                    this,
                    "This feature is only available in Downloaded App version. Please install the app from PlayStore.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        displayButtons()
    }

    private fun launchActivityWithModuleName(moduleName: String) {
        when (moduleName) {
            instantModuleName -> launchActivity(INSTANT_FEATURE_CLASSNAME)
            separateModuleName -> launchActivity(SEPARATE_FEATURE_CLASSNAME)
            bigVideoName -> launchActivity(BIG_VIDEO_CLASSNAME)
        }
    }

    private fun launchActivity(className: String) {
        val intent = Intent().setClassName(BuildConfig.APPLICATION_ID, className)
        startActivity(intent)
    }

    companion object {
        private const val INSTANT_FEATURE_CLASSNAME =
            "${BuildConfig.APPLICATION_ID}.feature.instantmodule.ui.PageInstantActivity"
        private const val SEPARATE_FEATURE_CLASSNAME =
            "${BuildConfig.APPLICATION_ID}.feature.separatemodule.ui.PageSeparateActivity"
        private const val BIG_VIDEO_CLASSNAME =
            "${BuildConfig.APPLICATION_ID}.feature.bigvideo.ui.PageVideoActivity"

        private const val CONFIRMATION_REQUEST_CODE = 101
    }
}
