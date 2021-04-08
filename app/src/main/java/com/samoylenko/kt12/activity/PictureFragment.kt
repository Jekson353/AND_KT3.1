package com.samoylenko.kt12.activity

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.samoylenko.kt12.R
import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.databinding.FragmentPictureBinding
import com.samoylenko.kt12.view.load

class PictureFragment : Fragment() {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_post, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.actionBar?.hide()

        val binding = FragmentPictureBinding.inflate(inflater, container, false)
        val url: String? = arguments?.getSerializable("picture") as? String

        url?.let {
            binding.imageViewPicture.load("${PostsApi.MEDIA_URL}media/${it}")
        }

        return binding.root
    }
}