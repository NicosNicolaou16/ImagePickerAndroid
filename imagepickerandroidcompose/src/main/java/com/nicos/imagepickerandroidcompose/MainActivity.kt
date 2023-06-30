package com.nicos.imagepickerandroidcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nicos.imagepickerandroid.image_picker.pickSingleImage
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
        pickSingleImage(scaleBitmapModel = null, listener = { bitmap, uri ->
            Log.d("rwerrwerwerwer", bitmap.toString() + " " + uri.toString())
        })

    Box {
        Button(modifier = Modifier.size(70.dp, 40.dp), onClick = { pickSingleImage() }) {

        }
    }
}