package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.IntervalSoundItem
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor(resources: ResourceProvider) :
    BaseListAdapter<ItemSoundBinding, IntervalSoundItem>(ItemSoundBinding::inflate) {

    private val selectedColor = resources.color(R.color.colorSecondaryAccent)

    private val unselectedColor = resources.color(R.color.colorGray)

    override fun onBindViewHolder(holder: ViewHolder<ItemSoundBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val sound = list[position]
        with(holder.view) {
            title.text = sound.title
            title.setTextColor(if (sound.isSelected) selectedColor else unselectedColor)
            title.setOnClickListener { sound.clicks.invoke(position) }
        }
    }
}