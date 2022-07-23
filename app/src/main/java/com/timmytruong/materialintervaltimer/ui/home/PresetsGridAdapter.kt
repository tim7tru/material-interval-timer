package com.timmytruong.materialintervaltimer.ui.home

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemPresetCardsBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.TimerItem
import com.timmytruong.materialintervaltimer.utils.extensions.toDisplayTime
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class PresetsGridAdapter @Inject constructor():
    BaseListAdapter<ItemPresetCardsBinding, TimerItem>(ItemPresetCardsBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemPresetCardsBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val timer = list[position]
        with(holder.view) {
            title.text = timer.getDisplayTitle(root.context.getString(R.string.untitled))
            timer.intervalCount?.let {
                count.text = root.context.getString(R.string.number_of_intervals_format, it)
            }
            timer.time?.toDisplayTime()?.run {
                val displayTime = if (first == null) {
                    root.context.getString(R.string.no_hour_time_format, second, third)
                } else {
                    root.context.getString(R.string.full_time_format, first, second, third)
                }

                time.text = displayTime
            }
//            preset.setOnClickListener { timer.clicks(position) }
        }
    }
}