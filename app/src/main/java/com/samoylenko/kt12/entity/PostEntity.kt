package com.samoylenko.kt12.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samoylenko.kt12.dto.Attachment
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.enumeration.AttachmentType

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val likes: Int,
    var countVisability: Int,
    val likedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,
){
    companion object{
        fun  fromPost(post: Post): PostEntity =  with(post){
            PostEntity(id, author, authorAvatar, content, published, sharing, likes, countVisability, likedByMe,  AttachmentEmbeddable.fromPost(post.attachment))
        }
    }
}

fun PostEntity.toPost(): Post = Post (id, author, authorAvatar, content, published, sharing, likes, countVisability, likedByMe, attachment?.toPost())

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toPost() = Attachment(url, type)

    companion object {
        fun fromPost(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}