package com.example.birthly.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.birthly.AppNavigation
import com.example.birthly.isFirstLaunch
import com.example.birthly.markFirstLaunchDone
import com.example.birthly.repositories.AuthRepository
import com.example.birthly.repositories.BirthdayRepository
import com.example.birthly.room.DatabaseProvider
import com.example.birthly.ui.theme.BirthlyTheme
import com.example.birthly.viewmodel.BirthlyViewModel
import com.example.birthly.viewmodel.BirthlyViewModelFactory
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Setup DB and ViewModel
        val db = DatabaseProvider.getDatabase(applicationContext)
        val birthdayDao = db.birthdayDao()
        val repo = BirthdayRepository(birthdayDao)
        val viewModel = ViewModelProvider(
            this,
            BirthlyViewModelFactory(AuthRepository(), repo)
        )[BirthlyViewModel::class.java]

        // First-launch logic for contact birthdays
        if (isFirstLaunch(this)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                lifecycleScope.launch {
                    viewModel.getContactBirthdays(this@MainActivity)
                    markFirstLaunchDone(this@MainActivity)
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    1002
                )
            }
        }

        // UI
        setContent {
            BirthlyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
