package com.nicos.imagepickerandroidcompose

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.nicos.imagepickerandroid.image_picker.*
import com.nicos.imagepickerandroid.utils.enums.TakeImageType
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import com.nicos.imagepickerandroidcompose.ui.theme.ImagePickerAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImagePickerAndroidTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImagePickerDemo()
                }
            }
        }
    }
}

@Composable
fun ImagePickerDemo() {
    val context = LocalContext.current

    // Use Nullable states instead of allocating empty Bitmaps/Uris
    var singleImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var multiImageBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    // ==========================================
    // 1. Initialize Library Pickers
    // ==========================================

    PickSingleImage(
        scaleBitmapModel = null,
        listener = { bitmap, _ -> singleImageBitmap = bitmap }
    )

    PickMultipleImagesWithBase64Values(
        scaleBitmapModel = ScaleBitmapModel(height = 100, width = 100),
        maxNumberOfImages = 3,
        listener = { bitmapList, _, base64List ->
            if (bitmapList != null) {
                multiImageBitmaps = bitmapList
                base64List?.forEach { base64 ->
                    Log.d("ImagePicker", "Base64 value retrieved: ${base64.take(20)}...")
                }
            }
        }
    )

    TakeSingleCameraImage(
        scaleBitmapModel = null,
        takeImageType = TakeImageType.TAKE_IMAGE_PREVIEW,
        listener = { bitmap, _ ->
            singleImageBitmap = bitmap
        } // Reusing the single image state for preview
    )

    PickSingleVideo(
        listener = { uri -> videoUri = uri }
    )

    // ==========================================
    // 2. UI Layout
    // ==========================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Makes the screen scrollable
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // --- Single Image & Camera Section ---
        SectionContainer(title = "Single Image & Camera") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    pickSingleImage(onImagePickerNotAvailable = {
                        Log.d(
                            "ImagePicker",
                            "Picker Not Available"
                        )
                    })
                }) {
                    Text("Pick Image")
                }
                Button(onClick = {
                    takeSingleCameraImage(
                        context = context,
                        onPermanentCameraPermissionDeniedCallBack = {
                            Log.d(
                                "ImagePicker",
                                "Camera Permission Denied"
                            )
                        })
                }) {
                    Text("Open Camera")
                }
            }

            // Preview
            singleImageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // --- Multiple Images Section ---
        SectionContainer(title = "Multiple Images") {
            Button(onClick = { pickMultipleImagesWithBase64Values() }) {
                Text("Pick Multiple Images")
            }

            // Previews
            if (multiImageBitmaps.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(multiImageBitmaps) { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // --- Video Section ---
        SectionContainer(title = "Single Video") {
            Button(onClick = { pickSingleVideo() }) {
                Text("Pick Video")
            }

            // Preview
            videoUri?.let { uri ->
                VideoPlayerComposable(uri = uri)
            }
        }
    }
}

// ==========================================
// Helper Composables
// ==========================================

@Composable
fun SectionContainer(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

/**
 * Handles the ExoPlayer lifecycle properly to avoid memory leaks.
 */
@Composable
fun VideoPlayerComposable(uri: Uri) {
    val context = LocalContext.current

    // remember the player so it survives recompositions, but recreate it if the URI changes
    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            play()
        }
    }

    // Release the player when this composable leaves the screen to prevent memory leaks
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .size(250.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}