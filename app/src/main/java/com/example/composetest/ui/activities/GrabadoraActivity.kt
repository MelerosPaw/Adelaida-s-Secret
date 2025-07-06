package com.example.composetest.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.composetest.ui.compose.screen.ScreenGrabadora
import com.example.composetest.ui.viewmodel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GrabadoraActivity: ComponentActivity() {

    val vm by viewModels<MediaViewModel>()

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GrabadoraActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent { ScreenGrabadora() }

        onBackPressedDispatcher.addCallback(callback)
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            vm.onRelease()
            isEnabled = false
            remove()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.onRelease()
    }
}