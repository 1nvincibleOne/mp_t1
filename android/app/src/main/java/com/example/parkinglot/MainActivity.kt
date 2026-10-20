package com.example.parkinglot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.parkinglot.ui.ParkingScreen
import com.example.parkinglot.ui.theme.ParkingLotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParkingLotTheme {
                ParkingScreen()
            }
        }
    }
}
