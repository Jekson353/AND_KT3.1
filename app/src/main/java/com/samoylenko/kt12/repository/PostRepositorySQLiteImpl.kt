package com.samoylenko.kt12.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.samoylenko.kt12.dao.PostDao
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.entity.PostEntity
import com.samoylenko.kt12.entity.toPost

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
//    private var posts = emptyList<Post>()
//    private val data: MutableLiveData<List<Post>> = MutableLiveData(posts)
//    init {
//        posts = dao.getAll()
//        data.value = posts
//    }


    //override fun getAll(): LiveData<List<Post>> = data
    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { it.map(PostEntity::toPost) }

    override fun likeById(id: Long) {
        dao.likesById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromPost(post))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}