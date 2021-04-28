package com.timmytruong.materialintervaltimer.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<Binding : ViewDataBinding, Entity> :
    RecyclerView.Adapter<BaseListAdapter.BaseViewHolder<Binding>>() {

    class BaseViewHolder<Binding : ViewDataBinding>(val view: Binding) :
        RecyclerView.ViewHolder(view.root)

    abstract val bindingLayout: Int

    protected val context: Context by lazy { binding.root.context }

    protected val view: View by lazy { binding.root }

    protected val list: MutableList<Entity> = mutableListOf()

    private lateinit var binding: Binding

    abstract override fun onBindViewHolder(holder: BaseViewHolder<Binding>, position: Int)

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Binding> {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            bindingLayout,
            parent,
            false
        )
        return BaseViewHolder(view = binding)
    }

    fun addList(newList: List<Entity>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}