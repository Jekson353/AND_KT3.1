package com.samoylenko.kt12.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.BuildConfig
import com.samoylenko.kt12.R
import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.databinding.CardPostBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.view.load
import com.samoylenko.kt12.viewmodel.PostViewModel


class OnePostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    var post: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = CardPostBinding.inflate(inflater, container, false)
        val onePost: Post? = arguments?.getSerializable("post") as? Post
        post = onePost

        binding.like.setOnClickListener {
            onePost?.id?.let {
                viewModel.likeById(it)
            }
        }

        binding.menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { itemView ->
                    when (itemView.itemId) {
                        R.id.deleteView -> {
                            onePost?.id?.let{
                                viewModel.removeById(it)
                                findNavController().navigateUp()
                            }
                            true
                        }
                        R.id.editView -> {
                            if (onePost != null) {
                                viewModel.edit(onePost)
                                val bundle = Bundle()
                                bundle.putSerializable("post", post)
                                findNavController().navigate(
                                    R.id.action_onePostFragment_to_postFragment,
                                    bundle
                                )
                            }
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        binding.share.setOnClickListener {
            Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, onePost?.content)
                .setType("text/plain")
                .also {
                    if (it.resolveActivity(requireActivity().packageManager) == null) {
                        Toast.makeText(
                            requireActivity(),
                            "Нет приложений для отправки сообщений",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onePost?.id?.let {
                            viewModel.shareById(it)
                        }
                        startActivity(it)
                    }
                }
        }

        view?.isVisible = false
        binding.imgAvatar.load("${PostsApi.BASE_URL}/avatars/${onePost?.authorAvatar}")
        binding.author.text = onePost!!.author
        binding.txtDate.text = onePost.published
        binding.textData.text = onePost.content
        binding.visability.text = onePost.countVisability.toString()
        binding.like.text = onePost.likes.toString()
        binding.share.text = onePost.sharing.toString()
        binding.like.isChecked = onePost.likedByMe
        onePost.attachment?.url.let {
            if (!it.isNullOrEmpty()){
                binding.imageViewPlay.load("${BuildConfig.BASE_URL}/media/${onePost.attachment?.url}").let {
                    binding.layoutPhoto.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }

}