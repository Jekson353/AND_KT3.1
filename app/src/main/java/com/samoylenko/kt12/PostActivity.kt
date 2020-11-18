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
        if (textPost != null) {
            binding.editTextPost.setText(textPost.toString())
        }

        binding.savePost.setOnClickListener {
            setResult(
                RESULT_OK, Intent().putExtra(
                    Intent.EXTRA_TEXT,
                    binding.editTextPost.text.toString()
                )
            )
            finish()
        }
    }
}