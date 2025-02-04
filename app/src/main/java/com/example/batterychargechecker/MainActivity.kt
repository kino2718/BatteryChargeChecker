package com.example.batterychargechecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.batterychargechecker.ui.theme.BatteryChargeCheckerTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatteryChargeCheckerTheme {
                MainScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.bindMyService()
    }

    override fun onStop() {
        viewModel.unbindMyService()
        super.onStop()
    }
}
