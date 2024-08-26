package com.example.pixcraft.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder

enum class GlideTransformation() {
    Default,
    GrayScale,
    Sepia,
    InvertColors,
    BlackWhite,
    Brightness,
    Contrast,
    Cool,
    Negative,
    Saturation,
    Vintage,
    Warmth;
}
