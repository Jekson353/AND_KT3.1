package com.samoylenko.kt12.dao

import androidx.room.*
import com.samoylenko.kt12.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("""
                UPDATE PostEntity SET
                    likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                    likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = :id;
            """)
    suspend fun likesById(id: Long)

    @Query("""
                UPDATE PostEntity SET
                    likes = likes - CASE WHEN likedByMe THEN -1 ELSE 1 END,
                    likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = :id;
            """)
    suspend fun dislikesById(id: Long)

    @Query("UPDATE PostEntity SET sharing = sharing + 1 WHERE id = :id")
    suspend fun shareById(id: Long)

    @Insert
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(posts: List<PostEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePost(post: PostEntity)

    @Query("SELECT id FROM PostEntity WHERE id = :id LIMIT 1")
    suspend fun getPostId(id: Long): Long?

    suspend fun save(post: PostEntity){
        val id = getPostId(post.id)
        if (id == null){
            insertPost(post)
        }else{
            updatePost(post)
        }
    }

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}