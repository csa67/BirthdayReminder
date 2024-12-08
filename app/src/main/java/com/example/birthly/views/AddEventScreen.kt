package com.example.birthly.views

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.birthly.viewmodel.UserViewModel
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(viewModel: UserViewModel = viewModel(), navController: NavController) {
    var person by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var isToggled by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Name TextField
        TextField(
            value = person,
            onValueChange = { person = it },
            label = { Text("Name") },
            placeholder = { Text("Enter name of the person E.g. Joe") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Date of Birth TextField
        TextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date of Birth") },
            placeholder = { Text("MM/DD/YYYY") },
            trailingIcon = {
                IconButton(onClick = {
                    showDatePickerDialog(context) { selectedDate ->
                        date = selectedDate
                    }
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Open Date Picker")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Reminder Toggle
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Add a Reminder")
            Switch(
                checked = isToggled,
                onCheckedChange = { isToggled = it },
                interactionSource = MutableInteractionSource()
            )
        }

        // Reminder Time TextField
        if (isToggled) {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = time ?: "",
                onValueChange = { time = it },
                label = { Text("Notify") },
                placeholder = { Text("Enter the time you want to get notified at") },
                trailingIcon = {
                    IconButton(onClick = {
                        showTimePickerDialog(context) { selectedTime ->
                            time = selectedTime
                        }
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Open Time Picker")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Add Event Button
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { addBirthday(person, date, time, viewModel, context, navController)},
            interactionSource = MutableInteractionSource(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add Event")


        }
    }
}
    @RequiresApi(Build.VERSION_CODES.O)
    fun addBirthday(person: String, date: String, time: String?, viewModel: UserViewModel, context: Context, navController: NavController){
        viewModel.addBirthday(person, date, time, context)
        navController.navigate(
            "home",
            navOptions = NavOptions.Builder()
                .setPopUpTo("home", inclusive = false) // Pop up to "home" without removing "home" from the stack
                .setLaunchSingleTop(true)              // Avoid multiple instances of the same destination
                .build()
        )
    }

    fun showDatePickerDialog(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                onDateSelected("$selectedMonth/${selectedDay + 1}/$selectedYear")
            },
            year, month, day
        ).show()
    }

    fun showTimePickerDialog(context: Context, onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            },
            hour,
            minute,
            true
        ).show()
    }


