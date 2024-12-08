package com.example.birthly.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birthly.viewmodel.UserViewModel
import com.example.birthly.model.Birthday
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, viewModel: UserViewModel) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val birthdays by viewModel.birthdaysList.collectAsState()

    // Fetch birthdays when HomeScreen is loaded
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.fetchBirthdays()
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(16.dp)) {
        Text(
            text = "Today's Events",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        showTodayEvents(birthdays)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Upcoming Birthdays",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ShowBirthdaysList(birthdays, navController)

        SmallFloatingActionButton(onClick = { navController.navigate("addNewBirthday") }) {
            Icon(Icons.Filled.Add, contentDescription = "Floating Action Button To Add More Birthdays")
        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowBirthdaysList(list: List<Birthday>, navController: NavController) {

    val newBirthdaysList = list.map{
        birthday ->
        val daysUntilNextBday = calculateDaysUntilNextBirthday(birthday.birthdate)
        birthday to daysUntilNextBday
    }

    val sortedList = newBirthdaysList.sortedBy { it.second }

    Column(modifier = Modifier.padding(16.dp)) {
        sortedList.forEach { (birthday,daysUntilNextBday) ->
            val formattedDate = formatBirthday(birthday.birthdate)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                onClick = {
                    navController.navigate("composeGreeting")
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Show the name in bold
                    Text(
                        text = birthday.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    // Show formatted birthday and days until next birthday
                    Text(
                        text = "$formattedDate · Coming in $daysUntilNextBday days",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showTodayEvents(birthdays: List<Birthday>) {
    val today = LocalDate.now()

    // Filter birthdays that match today's date
    val todayBirthdays = birthdays.filter {
        val (month, day, _) = it.birthdate.split("/").map { part -> part.toInt() }
        today.monthValue == month && today.dayOfMonth == day
    }

    if (todayBirthdays.isNotEmpty()) {
        todayBirthdays.forEach { birthday ->
            Text(
                text = "🎉 ${birthday.name}'s Birthday is Today!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    } else {
        Text(
            text = "No events for today!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Function to calculate days until the next birthday (format: MM/DD/YYYY)
@RequiresApi(Build.VERSION_CODES.O)
fun calculateDaysUntilNextBirthday(birthdate: String): Int {
    val today = LocalDate.now()
    val (month, day, _) = birthdate.split("/").map { it.toInt() } // Parse MM/DD/YYYY
    val thisYearBirthday = LocalDate.of(today.year, month, day)

    // Determine the next birthday
    val nextBirthday = if (thisYearBirthday.isBefore(today) || thisYearBirthday.isEqual(today)) {
        thisYearBirthday.plusYears(1)
    } else {
        thisYearBirthday
    }

    // Calculate days until the next birthday
    return ChronoUnit.DAYS.between(today, nextBirthday).toInt()
}

// Function to format the birthday as "Month Day" (e.g., "May 28")
@RequiresApi(Build.VERSION_CODES.O)
fun formatBirthday(birthdate: String): String {
    val (month, day, _) = birthdate.split("/").map { it.toInt() } // Parse MM/DD/YYYY
    val monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
    return "$monthName $day"
}
