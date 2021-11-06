package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import android.util.Log
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseListAdapter2
import com.timmytruong.materialintervaltimer.ui.base.screen.Clicks
import com.timmytruong.materialintervaltimer.ui.base.screen.ListItem
import com.timmytruong.materialintervaltimer.utils.set
import com.timmytruong.materialintervaltimer.utils.textColor
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor() :
    BaseListAdapter2<ItemSoundBinding, IntervalSoundItem>(ItemSoundBinding::inflate) {

    override fun onBindViewHolder(holder: ViewHolder<ItemSoundBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val sound = list[position]
        with(holder.view) {
            title.set(sound.title)
            title.textColor(if (sound.isSelected) R.color.colorSecondaryAccent else R.color.colorGray)
            title.setOnClickListener { sound.clicks.invoke(position) }
        }
    }
}

data class IntervalSoundItem(
    val id: Int,
    val title: String? = null,
    var isSelected: Boolean = false,
    override val clicks: Clicks
): ListItem()