package com.timmytruong.materialintervaltimer.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<B : ViewDataBinding, T> :
    RecyclerView.Adapter<BaseListAdapter.BaseViewHolder<B>>() {

    class BaseViewHolder<B : ViewDataBinding>(val view: B) : RecyclerView.ViewHolder(view.root)

    abstract val bindingLayout: Int

    protected val context: Context by lazy { binding.root.context }

    protected val list: MutableList<T> = mutableListOf()

    private lateinit var binding: B

    abstract override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int)

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            bindingLayout,
            parent,
            false
        )
        return BaseViewHolder(view = binding)
    }

    open fun addList(newList: List<T>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}