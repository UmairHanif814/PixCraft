package com.example.pixcraft.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class BrightnessTransformation(private val brightness: Float) : BitmapTransformation() {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val paint = Paint()
        val colorMatrix = ColorMatrix().apply { setScale(brightness, brightness, brightness, 1f) }
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        val result = Bitmap.createBitmap(toTransform.width, toTransform.height, toTransform.config)
        Canvas(result).drawBitmap(toTransform, 0f, 0f, paint)

        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("brightness_$brightness".toByteArray())
    }
}
