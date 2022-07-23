package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.IntervalSoundItem
import com.timmytruong.materialintervaltimer.utils.extensions.color
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor() :
    BaseListAdapter<ItemSoundBinding, IntervalSoundItem>(ItemSoundBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemSoundBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val sound = list[position]
        with(holder.view) {
            val textColor = if (sound.isSelected) {
                root.context.color(R.color.color_secondary_accent)
            } else {
                root.context.color(R.color.color_gray)
            }
            title.text = sound.title
            title.setTextColor(textColor)
            title.setOnClickListener { sound.clicks.invoke(position) }
        }
    }
}