package com.example.birthly.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birthly.model.Birthday
import com.example.birthly.viewmodel.BirthlyViewModel
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, viewModel: BirthlyViewModel) {
    val birthdays by viewModel.birthdaysList.collectAsState()

    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(onClick = { navController.navigate("addNewBirthday") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Birthday")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Today's Events",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                ShowTodayEvents(birthdays)
            }

            item {
                Text(
                    text = "Upcoming Birthdays",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(birthdays.sortedBy {
                calculateDaysUntilNextBirthday(it.birthdate)
            }) { birthday ->
                val formattedDate = formatBirthday(birthday.birthdate)
                val daysUntilNextBday = calculateDaysUntilNextBirthday(birthday.birthdate)

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    onClick = {
                        navController.navigate("composeGreeting")
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = birthday.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "$formattedDate · Coming in $daysUntilNextBday days",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
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

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(sortedList){ (birthday,daysUntilNextBday) ->
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
fun ShowTodayEvents(birthdays: List<Birthday>) {
    val today = LocalDate.now()

    // Filter birthdays that match today's date
    val todayBirthdays = birthdays.filter {
        val (month, day, _) = it.birthdate.split("/").map { part -> part.toInt() }
        today.monthValue == month && today.dayOfMonth == day
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (todayBirthdays.isNotEmpty()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🎉 Today's Birthdays",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todayBirthdays) { birthday ->
                        AssistChip(
                            onClick = {//No operation
                                 },
                            label = { Text(birthday.name) }
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events for today!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
