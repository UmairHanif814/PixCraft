package com.example.pixcraft.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.charset.Charset
import java.security.MessageDigest

class BlurTransformation(private val radius: Int,private val context: Context) : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("BlurTransformation$radius".toByteArray(Charset.forName("UTF-8")))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return Bitmap.createScaledBitmap(toTransform, outWidth, outHeight, false).apply {
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(rs, this)
            val output = Allocation.createTyped(rs, input.type)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setRadius(radius.toFloat())
            blurScript.setInput(input)
            blurScript.forEach(output)
            output.copyTo(this)
        }
    }
}
