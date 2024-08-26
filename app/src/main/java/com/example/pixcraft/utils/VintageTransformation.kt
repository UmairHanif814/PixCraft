package com.example.pixcraft.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class VintageTransformation : BitmapTransformation() {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f)
            postConcat(ColorMatrix(floatArrayOf(
                1.5f, 0.5f, 0.5f, 0f, 0f,
                0.5f, 1.5f, 0.5f, 0f, 0f,
                0.5f, 0.5f, 1.5f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )))
        }

        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        val result = Bitmap.createBitmap(toTransform.width, toTransform.height, toTransform.config)
        Canvas(result).drawBitmap(toTransform, 0f, 0f, paint)

        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("vintage".toByteArray())
    }
}
