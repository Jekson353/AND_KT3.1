package com.samoylenko.kt12.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samoylenko.kt12.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val author: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val likes: Int,
    var countVisability: Int,
    val video: String,
    val likedByMe: Boolean
){
    companion object{
        fun  fromPost(post: Post): PostEntity =  with(post){
            PostEntity(id, author, content, published, sharing, likes, countVisability, video, likedByMe)
        }
    }
}

fun PostEntity.toPost(): Post = Post (id, author, content, published, sharing, likes, countVisability, video, likedByMe)