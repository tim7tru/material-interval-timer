package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemIntervalBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.IntervalItem
import com.timmytruong.materialintervaltimer.utils.extensions.set
import com.timmytruong.materialintervaltimer.utils.extensions.showIf
import com.timmytruong.materialintervaltimer.utils.extensions.toDisplayTime
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalItemAdapter @Inject constructor() : BaseListAdapter<ItemIntervalBinding, IntervalItem>(ItemIntervalBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemIntervalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val interval = list[position]

        with(holder.view) {
            interval.time?.toDisplayTime()?.run {
                val displayTime = if (first == null) {
                    root.context.getString(R.string.no_hour_time_format, second, third)
                } else {
                    root.context.getString(R.string.full_time_format, first, second, third)
                }

                time.text = displayTime
            }

            header.showIf(interval.hasHeaders)

            when (position) {
                0 -> header.text = root.context.getString(R.string.current_interval_title)
                1 -> header.text = root.context.getString(R.string.up_next_interval_title)
                else -> { /** No op **/ }
            }

            icon.set(interval.icon)
            title.text = interval.title
        }
    }
}