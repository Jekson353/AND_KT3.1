package com.samoylenko.kt12.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.samoylenko.kt12.R
import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.databinding.FragmentNewPostBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.error.getErrorMessage
import com.samoylenko.kt12.util.AndroidUtils
import com.samoylenko.kt12.view.load
import com.samoylenko.kt12.viewmodel.PostViewModel
import java.io.File


class NewPostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer =  ::requireParentFragment )
    private val photoRequestCode = 1
    private val cameraRequestCode = 2
    var editedPost: Post? = null

    private var fragmentBinding: FragmentNewPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.savePost -> {
                fragmentBinding?.let {
                    it.ProgressBarLoading.isVisible = true
                    val content = it.editTextPost.text.toString()
                    if (content.isEmpty()){
                        Toast.makeText(
                            requireActivity(),
                            "Текст не может быть пустым",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@let
                    }
                    viewModel.changeContent(content)
                    viewModel.save()
                    AndroidUtils.hideSoftKeyBoard(requireView())
                }
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
        (activity as AppCompatActivity).supportActionBar?.title = "Создание/редактирование"

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        fragmentBinding = binding
        val onePost: Post? = arguments?.getSerializable("post") as? Post
        editedPost = onePost

        editedPost?.content.let {editedText ->
            binding.editTextPost.setText(editedText)
        }

        editedPost?.attachment?.url?.let {urlImage->
            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.load("${PostsApi.BASE_URL}/media/${urlImage}")
        }

        binding.editTextPost.requestFocus()

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .galleryMimeTypes(arrayOf(
                    "image/png",
                    "image/jpeg",
                ))
                .start(photoRequestCode)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .start(cameraRequestCode)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }

        viewModel.postCreated.observe(viewLifecycleOwner){
            viewModel.getPosts()
            findNavController().navigateUp()
        }

        viewModel.postCreateError.observe(viewLifecycleOwner){
            binding.ProgressBarLoading.isVisible = false
            Toast.makeText(requireContext(), it.getErrorMessage(resources), Toast.LENGTH_SHORT).show()
        }

        viewModel.photo.observe(viewLifecycleOwner){
            if (it.uri == null){
                binding.photoContainer.visibility = View.GONE
                return@observe
            }
            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}