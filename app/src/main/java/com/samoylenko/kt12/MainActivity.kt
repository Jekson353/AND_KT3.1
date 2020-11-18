package com.samoylenko.kt12

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.samoylenko.kt12.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object {
        const val POST_REQUEST_CODE = 10
    }

    private val viewModel: PostViewModel by viewModels()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun playVideo(post: Post) {
                Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    .also {
                        if (it.resolveActivity(packageManager) == null) {
                            Toast.makeText(
                                this@MainActivity,
                                "Нет приложений для просмотра видео",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            startActivity(it)
                        }
                    }
            }

            override fun onShare(post: Post) {
                Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, post.content)
                    .setType("text/plain")
                    .also {
                        if (it.resolveActivity(packageManager) == null) {
                            Toast.makeText(
                                this@MainActivity,
                                "Нет приложений для отправки сообщений",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.shareById(post.id)
                            startActivity(it)
                        }
                    }
            }
        })

        binding.listItem.adapter = adapter
        viewModel.data.observe(this, { posts ->
            adapter.submitList(posts)
        })

        binding.addPostButton.setOnClickListener {
            startActivityForResult(Intent(this, PostActivity::class.java), POST_REQUEST_CODE)
        }

        viewModel.edited.observe(this, { post ->
            if (post.id == 0L) {
                return@observe
            }

            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra("textPost", post.content)
            intent.putExtra("urlVideo", post.video)
            startActivityForResult(intent, POST_REQUEST_CODE)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POST_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val textPost = data.getStringExtra("textPost")
            var textUrl = data.getStringExtra("urlVideo")

            if (TextUtils.isEmpty(textPost)) {
                Toast.makeText(
                    this@MainActivity,
                    "Текст не может быть пустым",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            viewModel.changeContent(textPost!!.toString(), textUrl.toString())
            viewModel.save()
        }
    }
}
