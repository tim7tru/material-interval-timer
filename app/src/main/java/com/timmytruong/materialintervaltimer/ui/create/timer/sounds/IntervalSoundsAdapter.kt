package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem
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

data class IntervalSoundItem(
    val id: Int,
    val title: String? = null,
    var isSelected: Boolean = false,
    override val clicks: Clicks
): ListItem()