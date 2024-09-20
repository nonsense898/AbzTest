package com.non.abztest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.non.abztest.bind.Base
import com.non.abztest.databinding.ItemUsersBinding
import com.non.abztest.model.UserGet

class UserAdapter : PagingDataAdapter<UserGet, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }

    class UserViewHolder(private val binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userGet: UserGet) {
            binding.user = userGet
            Base.drawableUrl(binding.avatar, userGet.photo)
            binding.executePendingBindings()
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserGet>() {
        override fun areItemsTheSame(oldItem: UserGet, newItem: UserGet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserGet, newItem: UserGet): Boolean {
            return oldItem == newItem
        }
    }
}