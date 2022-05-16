package com.timmytruong.materialintervaltimer.ui.home

import com.timmytruong.materialintervaltimer.databinding.ItemPresetCardsBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.PresetGridItem
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class PresetsGridAdapter: BaseListAdapter<ItemPresetCardsBinding, PresetGridItem>(ItemPresetCardsBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemPresetCardsBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = list[position]
        with(holder.view) {
            title.text = item.timer.title

            preset.setOnClickListener { item.clicks(position) }
        }
    }
}