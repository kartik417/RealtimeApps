package com.example.signlanguagetranslationactivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextToASLScreen() {
    var inputText by remember { mutableStateOf("") }
    var submittedText by remember { mutableStateOf("") }
    var gifResId by remember { mutableStateOf<Int?>(null) }

    val wordToGifMap = mapOf(
        "angry" to R.drawable.angry,
        "happy" to R.drawable.happy,
        "mom" to R.drawable.mom,
        "excuse" to R.drawable.excuse,
        "help" to R.drawable.help,
        "thank you" to R.drawable.thank_you,
        "who" to R.drawable.who,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⌨️ Text to Sign",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter word or phrase") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            submittedText = inputText.lowercase().trim()
            gifResId = wordToGifMap[submittedText]
        }) {
            Text("Convert to Sign")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (submittedText.isNotEmpty()) {
            if (gifResId != null) {
                GifImage(
                    rawResId = gifResId!!,
                    modifier = Modifier
                        .size(220.dp)
                        .padding(8.dp)
                )
            } else {
                Text(
                    text = "⚠️ No sign image found for \"$submittedText\".",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

