package com.timmytruong.materialintervaltimer.ui.create.timer.adapters

import androidx.databinding.ObservableBoolean
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.base.screen.Clicks
import com.timmytruong.materialintervaltimer.ui.base.screen.ListBinding
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.utils.ObservableString
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor() :
    BaseListAdapter<ItemSoundBinding, IntervalSoundScreenBinding>() {

    override val bindingLayout: Int = R.layout.item_sound

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSoundBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.screen = list[position]
    }
}

data class IntervalSoundScreenBinding(
    val id: Int,
    val soundName: ObservableString = ObservableString(""),
    val isSelected: ObservableBoolean = ObservableBoolean(false),
    override val clicks: Clicks
): ListBinding()