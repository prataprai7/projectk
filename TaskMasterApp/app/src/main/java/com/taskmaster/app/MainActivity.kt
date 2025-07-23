package com.taskmaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.taskmaster.app.ui.navigation.TaskMasterNavigation
import com.taskmaster.app.ui.screens.auth.AuthScreen
import com.taskmaster.app.ui.theme.TaskMasterTheme
import com.taskmaster.app.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TaskMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentUser by authViewModel.currentUser.collectAsState()
                    
                    if (currentUser != null) {
                        TaskMasterNavigation()
                    } else {
                        AuthScreen(authViewModel = authViewModel)
                    }
                }
            }
        }
    }
}