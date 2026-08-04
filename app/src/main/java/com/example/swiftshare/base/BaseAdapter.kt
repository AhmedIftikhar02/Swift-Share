package com.example.swiftshare.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    areItemsTheSame: (T, T) -> Boolean,
    areContentsTheSame: (T, T) -> Boolean
) : ListAdapter<T, BaseAdapter.BaseViewHolder<VB>>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = areItemsTheSame(oldItem, newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T) = areContentsTheSame(oldItem, newItem)
    }).build()
) {
    class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }
    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        bind(holder.binding, getItem(position), position)
    }
    abstract fun bind(binding: VB, item: T, position: Int)
}
