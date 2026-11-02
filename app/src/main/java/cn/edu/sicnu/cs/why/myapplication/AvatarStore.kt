package cn.edu.sicnu.cs.why.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

object AvatarStore {
    private const val FILE_NAME = "avatar.png"
    private const val MAX_DIMENSION = 1024

    fun avatarFile(context: Context): File = File(context.filesDir, FILE_NAME)

    fun hasAvatar(context: Context): Boolean = avatarFile(context).exists()

    fun copyFromUri(context: Context, uri: Uri, target: File): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            } != null
        } catch (e: Exception) {
            false
        }
    }

    fun load(context: Context): Bitmap? = decodeFile(avatarFile(context))

    fun decodeFile(file: File): Bitmap? {
        if (!file.exists()) return null
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            var sampleSize = 1
            while (bounds.outWidth / sampleSize > MAX_DIMENSION ||
                bounds.outHeight / sampleSize > MAX_DIMENSION
            ) {
                sampleSize *= 2
            }
            val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            null
        }
    }
}
