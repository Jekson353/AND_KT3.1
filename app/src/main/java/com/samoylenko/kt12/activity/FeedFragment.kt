package com.samoylenko.kt12.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.samoylenko.kt12.R
import com.samoylenko.kt12.adapter.OnInteractionListener
import com.samoylenko.kt12.adapter.PostAdapter
import com.samoylenko.kt12.auth.AppAuth
import com.samoylenko.kt12.databinding.FragmentFeedBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.error.getErrorMessage
import com.samoylenko.kt12.viewmodel.AuthViewModel
import com.samoylenko.kt12.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    private val viewModelMenu: AuthViewModel by viewModels()


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        menu.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModelMenu.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModelMenu.authenticated)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when (item.itemId) {
            R.id.signin -> {
                findNavController().navigate(R.id.loginFragment)
                true
            }
            R.id.signup -> {
                // TODO: реализация регистрации
                true
            }
            R.id.signout -> {
                AppAuth.getInstance().removeAuth()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "NMedia"

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
                bundle.putSerializable("post", post)
                findNavController().navigate(R.id.action_feedFragment_to_onePostFragment, bundle)
            }

            override fun playVideo(post: Post) {
                post.attachment?.url.let {
                    val bundle = Bundle()
                    bundle.putSerializable("picture", it)
                    findNavController().navigate(
                        R.id.action_feedFragment_to_pictureFragment,
                        bundle
                    )
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
            bundle.putSerializable("post", post)
            findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
        })

//        binding.swiperefresh.setOnRefreshListener {
//            viewModel.refreshPosts()
//        }

        viewModel.newPosts.observe(viewLifecycleOwner){ count ->
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
