package com.mrwhoknows.findmynoti.util

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val deviceName: String
        get() = Build.MANUFACTURER + " " + Build.MODEL.ifBlank { Build.PRODUCT }
}

actual fun getPlatform(): Platform = AndroidPlatform()
