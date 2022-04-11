package com.example.recordplayer

import android.net.Uri


data class Audio(
    val path: String?,
    val uri: Uri?,
    val time: String,
    val duration: String,
    val user: Boolean
)
