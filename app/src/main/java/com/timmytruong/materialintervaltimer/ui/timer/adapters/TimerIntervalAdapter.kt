package com.timmytruong.materialintervaltimer.ui.timer.adapters

import android.view.View
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.databinding.ItemIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

private const val CURRENT_TITLE = 0
private const val INTERVAL = 1
private const val UP_NEXT_TITLE = 2

@ActivityScoped
class TimerIntervalAdapter @Inject constructor() :
    BaseListAdapter<ItemIntervalBinding, Interval>() {

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> CURRENT_TITLE
            1 -> UP_NEXT_TITLE
            else -> INTERVAL
        }

    override val bindingLayout: Int
        get() = R.layout.item_interval

    override fun onBindViewHolder(holder: BaseViewHolder<ItemIntervalBinding>, position: Int) {
        when (holder.itemViewType) {
            CURRENT_TITLE -> {
                holder.view.title = context.getString(R.string.currentIntervalTitle)
                holder.view.titleVisible = true
            }
            UP_NEXT_TITLE -> {
                holder.view.title = context.getString(R.string.upNextIntervalTitle)
                holder.view.titleVisible = true
            }
            INTERVAL -> {
                holder.view.titleVisible = false
            }
        }
        holder.view.interval = list[position]
        holder.view.intervalDragIcon.visibility = View.GONE
    }
}