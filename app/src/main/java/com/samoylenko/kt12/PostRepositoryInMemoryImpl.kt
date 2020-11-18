package com.samoylenko.kt12

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 1L
    private var posts: List<Post> = listOf(
        Post(
            id = nextId++,
            author = "Пушкин А.С.",
            content = "У Лукоморья дуб зеленый, Златая цепь на дубе том...",
            published = "01 апреля 1985 года",
            sharing = 0,
            like = 995,
            countVisability = 10,
            video = "",
            likedByMe = false
        ),
        Post(
            id = nextId++,
            author = "Лермонтов Н.Ю.",
            content = "У Лукоморья дуб зеленый, Златая цепь на дубе том...",
            published = "01 апреля 1985 года",
            sharing = 0,
            like = 2,
            countVisability = 230,
            video = "https://www.youtube.com/watch?v=8riWu4xkJ3M",
            likedByMe = false
        ),
        Post(
            id = nextId++,
            author = " Неизвестный автор",
            content = "И знать не знаю, как вас звали...",
            published = "01 апреля 2020 года",
            sharing = 0,
            like = 15,
            countVisability = 6,
            video = "",
            likedByMe = false
        ),
        Post(
            id = nextId++,
            author = "Грамилов С.В.",
            content = "У Лукоморья дуб зеленый, Златая цепь на дубе том...",
            published = "01 апреля 1944 года",
            sharing = 0,
            like = 995,
            countVisability = 10,
            video = "",
            likedByMe = false
        )
    )
    private val data: MutableLiveData<List<Post>> = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                if (it.likedByMe) {
                    it.copy(likedByMe = !it.likedByMe, like = it.like - 1)
                } else {
                    it.copy(likedByMe = !it.likedByMe, like = it.like + 1)
                }
            }
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                it.copy(sharing = it.sharing + 1)
            }
        }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Локальное сохранение",
                    //content = "",
                    published = "Только что",
                    sharing = 0,
                    like = 0,
                    countVisability = 0,
                    video = "",
                    likedByMe = false

                )
            ) + posts
            data.value = posts
            return
        }
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content, video = post.video)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id !=id }
        data.value = posts
    }
}