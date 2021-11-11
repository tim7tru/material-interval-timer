package com.timmytruong.materialintervaltimer.ui.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.timmytruong.materialintervaltimer.ui.base.screen.Inflater
import com.timmytruong.materialintervaltimer.ui.base.screen.ListItem

abstract class BaseListAdapter<View: ViewBinding, Entity : ListItem>(
    private val inflater: Inflater<View>
) : RecyclerView.Adapter<BaseListAdapter.ViewHolder<View>>() {

    class ViewHolder<View: ViewBinding>(val view: View) : RecyclerView.ViewHolder(view.root)

    protected val list: MutableList<Entity> = mutableListOf()

    override fun onBindViewHolder(holder: ViewHolder<View>, position: Int) {
        with(list[position]) {
            this.position = position
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<View> {
        return ViewHolder(inflater.invoke(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newList: List<Entity>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}