package com.timmytruong.materialintervaltimer.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.timmytruong.materialintervaltimer.ui.base.screen.Inflater
import com.timmytruong.materialintervaltimer.ui.base.screen.ListItem

abstract class BaseListAdapter<View : ViewDataBinding, Entity : ListItem> :
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

abstract class BaseListAdapter2<View: ViewBinding, Entity : ListItem>(
    private val inflater: Inflater<View>
) : RecyclerView.Adapter<BaseListAdapter2.ViewHolder<View>>() {

    class ViewHolder<View: ViewBinding>(val view: View) : RecyclerView.ViewHolder(view.root)

    protected val list: MutableList<Entity> = mutableListOf()

    private lateinit var binding: View

    private lateinit var context: Context

    override fun onBindViewHolder(holder: ViewHolder<View>, position: Int) {
        with(list[position]) {
            this.position = position
            holder.view.root.setOnClickListener { clicks.invoke(position) }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<View> {
        context = parent.context
        binding = inflater.invoke(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view = binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newList: List<Entity>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    protected fun string(@StringRes id: Int?) = if (id == null) null else context.getString(id)
}