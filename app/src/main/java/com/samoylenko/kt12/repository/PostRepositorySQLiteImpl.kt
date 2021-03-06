package com.samoylenko.kt12.repository

import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.dao.PostDao
import com.samoylenko.kt12.dto.Attachment
import com.samoylenko.kt12.dto.Media
import com.samoylenko.kt12.dto.MediaUpload
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.entity.PostEntity
import com.samoylenko.kt12.entity.toPost
import com.samoylenko.kt12.enumeration.AttachmentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException


class PostRepositorySQLiteImpl(private val dao: PostDao): PostRepository {

    override val posts: Flow<List<Post>>
        get() = dao.getAll()
            .map { it.map(PostEntity::toPost) }
            .flowOn(Dispatchers.Default)

    override suspend fun getAll(): List<Post> {
        val networkPosts = PostsApi.retrofitService.getAll()
            dao.insertPost(networkPosts.map(PostEntity.Companion::fromPost))
        return networkPosts
    }

    override suspend fun getPost(id: Long): Post {
        return PostsApi.retrofitService.getById(id)
    }

    override suspend fun dislikeById(id: Long): Post {
        val postDislike = PostsApi.retrofitService.dislikeById(id)

        postDislike.let {
            dao.dislikesById(id)
        }

        return postDislike
    }

    override suspend fun likeById(id: Long): Post {
        val postLike = PostsApi.retrofitService.likeById(id)

        postLike.let {
            dao.likesById(it.id)
        }
        return postLike
    }

    override suspend fun shareById(id: Long) {
        PostsApi.retrofitService.shareById(id)
        dao.shareById(id)
    }

    override suspend fun save(post: Post) {
        val localPosts = PostsApi.retrofitService.save(post)
        dao.save(PostEntity.fromPost(localPosts))
    }

    override suspend fun removeById(id: Long) {
        PostsApi.retrofitService.removeById(id)
        dao.removeById(id)
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
            while (true){
                val newer = PostsApi.retrofitService.getNewer(id)
                emit(newer.size)
            }
        }.catch {
            emit(0)
        }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
    }

    override suspend fun upload(upload: MediaUpload): Media {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            return PostsApi.retrofitService.upload(media)
    }
}