package com.samoylenko.kt12.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samoylenko.kt12.BuildConfig
import com.samoylenko.kt12.R
import com.samoylenko.kt12.databinding.CardPostBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.util.Calc
import com.samoylenko.kt12.view.load
import com.samoylenko.kt12.view.loadCircleCrop

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun playVideo(post: Post) {}
    fun onClickPost(post: Post) {}
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {

        binding.apply {
            imgAvatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            author.text = post.author
            txtDate.text = post.published
            textData.text = post.content
            visability.text = post.countVisability.toString()
            like.text = Calc.intToText(post.likes)
            share.text = post.sharing.toString()
            like.isChecked = post.likedByMe

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            post.attachment?.url.let {
                if (!it.isNullOrEmpty()){
                    imageViewPlay.load("${BuildConfig.BASE_URL}/media/${post.attachment?.url}").let {
                        layoutPhoto.visibility = View.VISIBLE
                    }
                }
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { itemView ->
                        when (itemView.itemId) {
                            R.id.deleteView -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.editView -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            imageViewPlay.setOnClickListener {
                onInteractionListener.playVideo(post)
            }

            binding.root.setOnClickListener {
                onInteractionListener.onClickPost(post)
            }
        }
    }
}