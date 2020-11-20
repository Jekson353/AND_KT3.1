package com.samoylenko.kt12.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.R
import com.samoylenko.kt12.databinding.FragmentPostBinding
import com.samoylenko.kt12.util.AndroidUtils
import com.samoylenko.kt12.viewmodel.PostViewModel


class PostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val textPost = arguments?.getString("textPost")
        val urlVideo = arguments?.getString("urlVideo")
        val owner = arguments?.getString("owner")

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

            AndroidUtils.hideSoftKeyBoard(requireView())
            if(owner.equals("onePost")){
                findNavController().navigate(R.id.action_postFragment_to_feedFragment)
            }else{
                findNavController().navigateUp()
            }

        }

        return binding.root
    }
}