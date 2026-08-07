package com.example.buythings.data.cloudinary

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

// ---- Cloudinary configuration ----
// Client-side config: cloud name + unsigned upload preset only.
// No api_key / api_secret here — safe to ship in a real app.
private const val CLOUD_NAME = "m7acawqw"          // ← replace this if reusing
private const val UPLOAD_PRESET = "ml__BuyThings"   // ← replace with your unsigned preset name

private val client = OkHttpClient()

/**
 * Result returned after a successful upload.
 */
data class CloudinaryUploadResult(
    val secureUrl: String,
    val publicId: String,
    val width: Int?,
    val height: Int?,
    val format: String?,
    val bytes: Long?
)

object CloudinaryManager {

    /**
     * Uploads an image (content:// or file:// Uri, as a String) to Cloudinary
     * using a direct HTTP multipart request — bypassing the Cloudinary Android
     * SDK's chunked "large upload" path, which does not support unsigned
     * uploads with a preset.
     */
    suspend fun uploadImage(context: Context, imageUriOrUrl: String): CloudinaryUploadResult {
        return withContext(Dispatchers.IO) {
            val file = copyUriToTempFile(context, imageUriOrUrl)

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    val errorMessage = try {
                        JSONObject(bodyString).getJSONObject("error").getString("message")
                    } catch (e: Exception) {
                        bodyString
                    }
                    throw Exception(errorMessage)
                }

                val json = JSONObject(bodyString)
                CloudinaryUploadResult(
                    secureUrl = json.getString("secure_url"),
                    publicId = json.getString("public_id"),
                    width = if (json.has("width")) json.getInt("width") else null,
                    height = if (json.has("height")) json.getInt("height") else null,
                    format = if (json.has("format")) json.getString("format") else null,
                    bytes = if (json.has("bytes")) json.getLong("bytes") else null
                )
            }
        }
    }

    /**
     * Copies a content:// (or file://) Uri into a real temp file on disk
     * so it can be attached to the multipart request.
     */
    private fun copyUriToTempFile(context: Context, uriString: String): File {
        val uri = Uri.parse(uriString)
        val tempFile = File.createTempFile("cloudinary_upload_", ".tmp", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    /**
     * Builds a transformed (optimized) URL for a given public ID.
     * f_auto -> Cloudinary picks the best format for the requesting device/browser
     * q_auto -> Cloudinary picks the best quality/compression tradeoff automatically
     */
    fun getOptimizedUrl(publicId: String): String {
        return "https://res.cloudinary.com/$CLOUD_NAME/image/upload/f_auto,q_auto/$publicId"
    }
}