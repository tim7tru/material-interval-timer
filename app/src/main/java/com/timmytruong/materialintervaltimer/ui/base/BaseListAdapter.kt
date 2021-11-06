package com.timmytruong.materialintervaltimer.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.timmytruong.materialintervaltimer.ui.base.screen.ListBinding

abstract class BaseListAdapter<View : ViewDataBinding, Entity : ListBinding> :
    RecyclerView.Adapter<BaseListAdapter.BaseViewHolder<View>>() {

    class BaseViewHolder<View : ViewDataBinding>(val view: View) :
        RecyclerView.ViewHolder(view.root)

    abstract val bindingLayout: Int

    protected val list: MutableList<Entity> = mutableListOf()

    private lateinit var binding: View

    override fun onBindViewHolder(holder: BaseViewHolder<View>, position: Int) {
        list[position].position = position
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<View> {
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