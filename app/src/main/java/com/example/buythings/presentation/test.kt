package com.example.buythings.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.buythings.data.cloudinary.CloudinaryManager
import kotlinx.coroutines.launch

/**
 * TEMPORARY test screen — only to confirm the Cloudinary upload flow works end-to-end.
 * Delete this file once you've built the real "Add Product" screen.
 *
 * This mirrors the real flow your app will use: pick an image from the gallery,
 * then upload that local Uri to Cloudinary.
 */
@Composable
fun TestUploadScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var statusText by remember { mutableStateOf("Pick an image to test upload.") }
    var isUploading by remember { mutableStateOf(false) }
    var optimizedUrl by remember { mutableStateOf<String?>(null) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            isUploading = true
            statusText = "Uploading..."
            optimizedUrl = null

            scope.launch {
                try {
                    val result = CloudinaryManager.uploadImage(context, uri.toString())

                    statusText = buildString {
                        appendLine("Upload successful!")
                        appendLine("Public ID: ${result.publicId}")
                        appendLine("Size: ${result.width}x${result.height}")
                        appendLine("Format: ${result.format}")
                        appendLine("Bytes: ${result.bytes}")
                    }

                    optimizedUrl = CloudinaryManager.getOptimizedUrl(result.publicId)
                } catch (e: Exception) {
                    statusText = "Upload failed: ${e.message}"
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { pickImageLauncher.launch("image/*") },
            enabled = !isUploading
        ) {
            Text(if (isUploading) "Uploading..." else "Pick Image & Test Upload")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = statusText)

        optimizedUrl?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Optimized URL:")
            Text(text = it)
        }
    }
}