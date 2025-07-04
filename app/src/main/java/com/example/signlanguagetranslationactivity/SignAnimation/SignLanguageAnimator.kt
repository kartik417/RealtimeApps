package com.example.signlanguagetranslationactivity

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val permissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        VoiceToGifScreen()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("🎤 Please grant microphone permission to use this feature.")
        }
    }
}

@Composable
fun VoiceToGifScreen() {
    var recognizedText by remember { mutableStateOf("") }
    var gifResId by remember { mutableStateOf<Int?>(null) }

    val wordToGifMap = mapOf(
        "angry" to R.drawable.angry,
        "happy" to R.drawable.happy,
        "mom" to R.drawable.mom,
        "excuse" to R.drawable.excuse,
        "happy" to R.drawable.happy,
        "help" to R.drawable.help,
        "thank you" to R.drawable.thank_you,
        "who" to R.drawable.who,

    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding() // <-- avoids overlap with status and nav bars
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // leave space for button
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            gifResId?.let {
                GifImage(
                    rawResId = it,
                    modifier = Modifier
                        .size(220.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (recognizedText.isNotEmpty()) "Recognized Text:\n$recognizedText" else "🎤 Tap the mic below to speak...",
                fontSize = 18.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (recognizedText.isNotEmpty() && gifResId == null) {
                Text(
                    text = "⚠️ No sign image found for \"$recognizedText\".",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Bottom mic button
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            SpeechToTextComposable { spokenText ->
                recognizedText = spokenText.lowercase().trim()
                gifResId = wordToGifMap[recognizedText]
            }
        }
    }
}
@Composable
fun SpeechToTextComposable(onResult: (String) -> Unit) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("🎤 Tap to speak") }

    val recognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()?.let {
                            onResult(it)
                            statusText = "✅ Recognized: $it"
                        }
                    isListening = false
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()?.let { statusText = "🗣️ Listening: $it" }
                }

                override fun onError(error: Int) {
                    isListening = false
                    statusText = "❌ Error occurred. Try again."
                }

                override fun onReadyForSpeech(params: Bundle?) {
                    statusText = "🎧 Ready to listen..."
                }

                override fun onBeginningOfSpeech() {
                    statusText = "🗣️ Listening..."
                }

                override fun onEndOfSpeech() {
                    statusText = "🔄 Processing..."
                }

                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    DisposableEffect(Unit) {
        onDispose { recognizer.destroy() }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status text for feedback
        Text(
            text = statusText,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 16.sp
        )

        // Mic Button with Glow while listening
        IconButton(
            onClick = {
                if (isListening) {
                    recognizer.stopListening()
                    statusText = "🔇 Stopped"
                } else {
                    recognizer.startListening(recognizerIntent)
                    statusText = "🎧 Listening..."
                }
                isListening = !isListening
            },
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .then(if (isListening) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) else Modifier)
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                tint = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
