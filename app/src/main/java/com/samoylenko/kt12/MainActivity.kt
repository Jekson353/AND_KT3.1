package com.samoylenko.kt12

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.samoylenko.kt12.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostAdapter(object : OnInteractionListener{
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
            }
        }
        )

        binding.listItem.adapter = adapter
        viewModel.data.observe(this, { posts ->
            adapter.submitList(posts)
        })

        viewModel.edited.observe(this, {post ->
            if (post.id ==0L){
                return@observe
            }
            with(binding.editTextContent){
                requestFocus()
                binding.layoutCancel.visibility = View.VISIBLE
                setText(post.content)
            }
        })

        binding.saveButton.setOnClickListener {
            with(binding.editTextContent){
                if (TextUtils.isEmpty(text)){
                    Toast.makeText(
                        this@MainActivity,
                        "Текст не может быть пустым",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(text.toString())
                viewModel.save()

                binding.layoutCancel.visibility = View.GONE
                setText("")
                clearFocus()
                AndroidUtils.hideSoftKeyBoard(this)
            }
        }

        binding.imageBtnCancel.setOnClickListener {
            with(binding.editTextContent){
                setText("")
                clearFocus()
                binding.layoutCancel.visibility = View.GONE
                AndroidUtils.hideSoftKeyBoard(this)
            }
        }

    }

}
