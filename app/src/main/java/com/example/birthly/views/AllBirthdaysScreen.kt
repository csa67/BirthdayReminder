package com.example.birthly.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.birthly.viewmodel.BirthlyViewModel

@Composable
fun AllBirthdaysScreen(viewModel: BirthlyViewModel) {
    val searchText by viewModel.searchText.collectAsState() // Observes search text
    val filteredBirthdays by viewModel.filteredList.collectAsState() // Observes filtered list

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchText ?: "",
            onValueChange = { query ->
                viewModel.onSearchTextChange(query) // Update search text
            },
            placeholder = { Text("Search Birthdays...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )


        // LazyColumn for Filtered Birthdays List
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 72.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics { traversalIndex = 1f },
        ) {
            if (filteredBirthdays.isEmpty()) {
                // Show message when no results are found
                item {
                    Text(
                        text = "No results found",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = Color.Gray
                    )
                }
            } else {
                // Show the filtered list of birthdays
                items(filteredBirthdays) { birthday ->
                    Text(
                        text = "${birthday.name} - ${birthday.birthdate}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}