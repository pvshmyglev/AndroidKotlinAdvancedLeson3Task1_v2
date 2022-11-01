package ru.netology.nmedia.model

import android.net.Uri
import java.io.File

data class MediaModel (
    val uri: Uri? = null,
    val file: File? = null,
)