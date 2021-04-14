package com.samoylenko.kt12.dto

import com.samoylenko.kt12.enumeration.AttachmentType
import java.io.Serializable

data class Post(
    var id:Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val likes: Int,
    var countVisability: Int,
    val likedByMe: Boolean = false,
    var attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
): Serializable

data class Attachment(
    val url: String,
    val type: AttachmentType,
)