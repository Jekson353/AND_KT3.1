package com.samoylenko.kt12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.samoylenko.kt12.databinding.ActivityPostBinding


class PostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textPost = intent.getStringExtra("textPost")
        val textUrl = intent.getStringExtra("urlVideo")
        if (textPost != null) {
            binding.editTextPost.setText(textPost.toString())
        }
        if (textUrl !=null){
            binding.inputUrlVideo.setText(textUrl.toString())
        }

        binding.savePost.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("textPost", binding.editTextPost.text.toString())
            resultIntent.putExtra("urlVideo", binding.inputUrlVideo.text.toString())
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}