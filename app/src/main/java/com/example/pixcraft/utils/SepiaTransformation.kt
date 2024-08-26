package com.example.pixcraft.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.charset.Charset
import java.security.MessageDigest

class SepiaTransformation : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("SepiaTransformation".toByteArray(Charset.forName("UTF-8")))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val sepiaMatrix = ColorMatrix().apply {
            set(floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(sepiaMatrix)
        val sepiaBitmap = Bitmap.createBitmap(toTransform.width, toTransform.height, toTransform.config)
        val canvas = Canvas(sepiaBitmap)
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        return sepiaBitmap
    }
}
