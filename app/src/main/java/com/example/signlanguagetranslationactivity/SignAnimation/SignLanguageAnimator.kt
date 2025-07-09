package com.example.signlanguagetranslationactivity

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
        "help" to R.drawable.help,
        "thank you" to R.drawable.thank_you,
        "who" to R.drawable.who,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9))
                )
            )
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // 🖼️ GIF Preview
            gifResId?.let {
                Card(
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    GifImage(
                        rawResId = it,
                        modifier = Modifier
                            .size(240.dp)
                            .padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📝 Recognized Text Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recognized Text",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (recognizedText.isNotEmpty()) recognizedText else "🎤 Tap the mic below to speak...",
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                }
            }

            if (recognizedText.isNotEmpty() && gifResId == null) {
                Text(
                    text = "⚠️ No sign image found for \"$recognizedText\".",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        // 🎙️ Bottom Floating Mic Button
        Box(
            modifier = Modifier.fillMaxSize(),
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = statusText,
            color = if (isListening) Color(0xFF1A237E) else Color.DarkGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Mic with glowing ring while listening
        Surface(
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
            shape = CircleShape,
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .size(90.dp)
                .border(
                    width = if (isListening) 3.dp else 1.dp,
                    color = if (isListening) Color(0xFF1E88E5) else Color.LightGray,
                    shape = CircleShape
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                    contentDescription = null,
                    tint = if (isListening) Color(0xFF1E88E5) else Color.DarkGray,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewVoiceToGifScreen() {
    MaterialTheme {
        VoiceToGifScreen()
    }
}
