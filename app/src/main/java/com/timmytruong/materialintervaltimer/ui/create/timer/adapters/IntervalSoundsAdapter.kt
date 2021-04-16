package com.timmytruong.materialintervaltimer.ui.create.timer.adapters

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.base.screen.Clicks
import com.timmytruong.materialintervaltimer.base.screen.ListBinding
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor() :
    BaseListAdapter<ItemSoundBinding, IntervalSoundScreenBinding>() {

    override val bindingLayout: Int = R.layout.item_sound

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSoundBinding>, position: Int) {
        holder.view.screen = list[position]
    }
}

data class IntervalSoundScreenBinding(
    val soundName: ObservableField<String> = ObservableField(""),
    val isSelected: ObservableBoolean = ObservableBoolean(false),
    val position: Int = -1,
    override val clicks: Clicks<IntervalSoundScreenBinding>
): ListBinding<IntervalSoundScreenBinding>()