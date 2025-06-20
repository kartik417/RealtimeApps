package com.example.signlanguagetranslationactivity

import android.graphics.Insets.add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest

@Composable
fun GifImage(rawResId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
        }
        .build()

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("android.resource://${context.packageName}/raw/$rawResId")
            .build(),
        contentDescription = "ASL GIF",
        imageLoader = imageLoader,
        modifier = modifier
    )
}
