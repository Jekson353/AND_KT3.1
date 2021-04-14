package com.samoylenko.kt12.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.databinding.FragmentPictureBinding
import com.samoylenko.kt12.view.load


class PictureFragment : Fragment() {

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? { // create ContextThemeWrapper from the original Activity Context with the custom theme
//        val contextThemeWrapper: Context = ContextThemeWrapper(
//            activity,
//            R.style.yourCustomTheme
//        ) // clone the inflater using the ContextThemeWrapper
//        val localInflater =
//            inflater.cloneInContext(contextThemeWrapper) // inflate the layout using the cloned inflater, not default inflater
//        return localInflater.inflate(R.layout.yourLayout, container, false)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Изображение"
       // val contextThemeWrapper: Context = ContextThemeWrapper(
       //     activity,
       //     R.style.
      //  ) // clone the inflater using the ContextThemeWrapper
       // val localInflater =
           // inflater.cloneInContext(contextThemeWrapper) // inflate the layout using the cloned inflater, not default inflater

       // (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //activity?.actionBar?.hide()
        activity?.actionBar?.title = "Авторизация"

        val binding = FragmentPictureBinding.inflate(inflater, container, false)
        val url: String? = arguments?.getSerializable("picture") as? String

        url?.let {
            binding.imageViewPicture.load("${PostsApi.MEDIA_URL}media/${it}")
        }

        return binding.root
    }
}