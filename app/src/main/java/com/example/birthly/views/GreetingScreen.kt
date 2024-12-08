package com.example.birthly.views

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.birthly.viewmodel.UserViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreetingScreen(
    viewModel: UserViewModel
) {

    val name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val generatedContent by viewModel.greeting.collectAsState(initial = null)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Any details to include?") },
            placeholder = { Text("E.g., loves hiking, kind-hearted") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.generateGreeting(name = name, age=12, description = description)
            },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = MutableInteractionSource()
        ) {
            Text("Generate Greeting")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the generated content
        generatedContent?.let {
            Text(
                text = "Generated Greeting:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )

            Row {
                Button(onClick = { copyText(context, it) }) {
                    Text("Copy")
                }
                Button(onClick = { shareText(context,it) }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share the message")
                    Text("Share Greeting")
                }
            }


        }

    }
}

fun copyText(context: Context, textToCopy: String) {
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText("Greeting", textToCopy))
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun shareText(context: Context, textToShare: String) {
    val shareIntent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        putExtra(android.content.Intent.EXTRA_TEXT, textToShare)
        type = "text/plain"
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share via"))
}

