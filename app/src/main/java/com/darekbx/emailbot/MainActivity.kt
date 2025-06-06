package com.darekbx.emailbot

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.darekbx.emailbot.ui.theme.EmailBotTheme
import com.darekbx.emailbot.navigation.AppNavHost
import com.darekbx.emailbot.navigation.FiltersDestination
import com.darekbx.emailbot.ui.MainActivityViewModel
import com.darekbx.emailbot.ui.MainUiState
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                val navController = rememberNavController()
                EmailBotTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            AppBar(openFilters = { navController.navigate(FiltersDestination.route) })
                        }
                    ) { innerPadding ->
                        AppNavHost(navController, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun AppBar(
        viewModel: MainActivityViewModel = koinViewModel(),
        openFilters: () -> Unit = {}
    ) {
        val uiState by viewModel.uiState.collectAsState()
        Surface(shadowElevation = 4.dp) {
            TopAppBar(
                title = { Text("Email Bot") },
                colors = TopAppBarDefaults.topAppBarColors(),
                actions = {
                    Row {
                        IconButton(onClick = openFilters) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Filters"
                            )
                        }
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                        Box {
                            when (val state = uiState) {
                                MainUiState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(24.dp)
                                    )
                                }

                                is MainUiState.Error -> {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = state.e.message,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                else -> {
                                    IconButton(onClick = { viewModel.cleanUp() }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_clean),
                                            contentDescription = "Clean up"
                                        )
                                    }
                                }
                            }
                        }
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationaleDialog()
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission")
            .setMessage("This app needs notification permission to show clean up results.")
            .setPositiveButton("OK") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
