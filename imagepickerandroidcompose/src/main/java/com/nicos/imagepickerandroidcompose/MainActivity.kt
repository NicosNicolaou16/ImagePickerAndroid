package com.nicos.imagepickerandroidcompose

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.nicos.imagepickerandroid.image_picker.PickMultipleImagesWithBase64Values
import com.nicos.imagepickerandroid.image_picker.PickSingleImage
import com.nicos.imagepickerandroid.image_picker.PickVideo
import com.nicos.imagepickerandroid.image_picker.TakeCameraImage
import com.nicos.imagepickerandroid.image_picker.pickMultipleImagesWithBase64Value
import com.nicos.imagepickerandroid.image_picker.pickSingleImage
import com.nicos.imagepickerandroid.image_picker.pickVideo
import com.nicos.imagepickerandroid.image_picker.takeCameraImage
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import com.nicos.imagepickerandroidcompose.ui.theme.ImagePickerAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImagePickerAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImagePicker()
                }
            }
        }
    }
}

@Composable
fun ImagePicker() {
    val context = LocalContext.current
    val bitmapValue = remember {
        mutableStateOf(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
    }
    val bitmapListValue = remember {
        mutableStateOf(listOf<Bitmap>())
    }
    val uriValue = remember {
        mutableStateOf(Uri.EMPTY)
    }
    PickSingleImage(scaleBitmapModel = null, listener = { bitmap, uri ->
        if (bitmap != null) {
            bitmapValue.value = bitmap
        }
    })
    PickMultipleImagesWithBase64Values(
        scaleBitmapModel = ScaleBitmapModel(
            height = 100,
            width = 100
        ),
        listener = { bitmapList, uriList, base64List ->
            if (bitmapList != null) {
                bitmapListValue.value = bitmapList
                base64List?.forEach { base64 ->
                    Log.d("base64Value", base64)
                }
            }
        })
    TakeCameraImage(scaleBitmapModel = null, listener = { bitmap, uri ->
        if (bitmap != null) {
            bitmapValue.value = bitmap
        }
    })
    val exoPlayer = remember {
        mutableStateOf(ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uriValue.value))
            prepare()
            play()
        })
    }
    PickVideo(listener = { uri ->
        if (uri != null) {
            uriValue.value = uri
            exoPlayer.value = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(uriValue.value))
                prepare()
                play()
            }
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            Image(
                bitmap = bitmapValue.value.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(200.dp, 200.dp)
            )
            Box(modifier = Modifier.size(200.dp, 200.dp)) {
                AndroidView(
                    factory = { context ->
                        StyledPlayerView(context).apply {
                            player = exoPlayer.value
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = {
                        it.player = exoPlayer.value
                    }
                )
            }
        }
        LazyRow {
            items(bitmapListValue.value) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp, 200.dp)
                )
            }
        }
        Button(modifier = Modifier.size(150.dp, 50.dp), onClick = { pickSingleImage() }) {
            Text(text = "Pick Single Image", style = TextStyle(textAlign = TextAlign.Center))
        }
        Button(
            modifier = Modifier.size(150.dp, 50.dp),
            onClick = { pickMultipleImagesWithBase64Value() }) {
            Text(text = "Pick Multiple Images", style = TextStyle(textAlign = TextAlign.Center))
        }
        Button(modifier = Modifier.size(150.dp, 50.dp), onClick = { takeCameraImage() }) {
            Text(text = "Take Camera Images", style = TextStyle(textAlign = TextAlign.Center))
        }
        Button(modifier = Modifier.size(150.dp, 50.dp), onClick = { pickVideo() }) {
            Text(text = "Pick Single Video", style = TextStyle(textAlign = TextAlign.Center))
        }

    }
}