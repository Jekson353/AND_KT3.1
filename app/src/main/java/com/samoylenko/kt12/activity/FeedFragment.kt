package com.samoylenko.kt12.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.samoylenko.kt12.R
import com.samoylenko.kt12.adapter.OnInteractionListener
import com.samoylenko.kt12.adapter.PostAdapter
import com.samoylenko.kt12.databinding.FragmentFeedBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.uimodel.getErrorMessage
import com.samoylenko.kt12.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    private val binding by lazy {
        FragmentFeedBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            override fun onClickPost(post: Post) {
                val bundle = Bundle()
                bundle.putLong("idPost", post.id)
                bundle.putString("author", post.author)
                bundle.putString("authorAvatar", post.authorAvatar)
                bundle.putString("txtDate", post.published)
                bundle.putString("content", post.content)
                bundle.putInt("visability", post.countVisability)
                bundle.putInt("likes", post.likes)
                bundle.putInt("share", post.sharing)
                bundle.putString("video", post.video)
                bundle.putBoolean("likedByMe", post.likedByMe)
                findNavController().navigate(R.id.action_feedFragment_to_onePostFragment, bundle)
            }

            override fun playVideo(post: Post) {
                Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    .also {
                        if (it.resolveActivity(requireActivity().packageManager) == null) {
                            Toast.makeText(
                                requireActivity(),
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
                        if (it.resolveActivity(requireActivity().packageManager) == null) {
                            Toast.makeText(
                                requireActivity(),
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

        viewModel.data.observe(viewLifecycleOwner, { uimodel ->
            binding.errorGroup.isVisible = uimodel.errorVisible
            binding.emptyText.isVisible = uimodel.empty
            binding.progress.isVisible = uimodel.loading
            binding.errorText.text = uimodel.error.getErrorMessage(resources)
            binding.ProgressBarLoading.isVisible = uimodel.progressBar

        })

        viewModel.posts.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            binding.listItem.smoothScrollToPosition(0)
        })

        binding.addPostButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_postFragment)
        }

        binding.errorButton.setOnClickListener {
            viewModel.getPosts()
        }

        viewModel.edited.observe(viewLifecycleOwner, { post ->
            if (post.id == 0L) {
                return@observe
            }
            val bundle = Bundle()
            bundle.putString("textPost", post.content)
            bundle.putString("urlVideo", post.video)
            findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
        })

        viewModel.newPosts.observe(viewLifecycleOwner){count ->
            if (count>0){
                view?.let {
                    Snackbar.make(it, "Доступно новых записей: $count", Snackbar.LENGTH_LONG)
                        .setAction("ПОКАЗАТЬ") {
                            viewModel.getPosts()
                        }
                        .show()
                }
            }
        }

        return binding.root
    }
}
