package com.samoylenko.kt12.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.databinding.FragmentNewPostBinding
import com.samoylenko.kt12.uimodel.getErrorMessage
import com.samoylenko.kt12.util.AndroidUtils
import com.samoylenko.kt12.viewmodel.PostViewModel


class NewPostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        val textPost = arguments?.getString("textPost")
        val urlVideo = arguments?.getString("urlVideo")

        binding.editTextPost.setText(textPost)
        binding.inputUrlVideo.setText(urlVideo)

        binding.editTextPost.requestFocus()

        binding.savePost.setOnClickListener {
            val content = binding.editTextPost.text.toString()
            val urlPost = binding.inputUrlVideo.text.toString()
            if (content.isEmpty()){
                Toast.makeText(
                    requireActivity(),
                    "Текст не может быть пустым",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.changeContent(content, urlPost)
            viewModel.save()
            binding.ProgressBarLoading.isVisible = true
            AndroidUtils.hideSoftKeyBoard(requireView())
        }
        viewModel.postCreated.observe(viewLifecycleOwner){
            viewModel.getPosts()
            findNavController().navigateUp()
        }

        viewModel.postCreateError.observe(viewLifecycleOwner){
            binding.ProgressBarLoading.isVisible = false
            Toast.makeText(requireContext(), it.getErrorMessage(resources), Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}