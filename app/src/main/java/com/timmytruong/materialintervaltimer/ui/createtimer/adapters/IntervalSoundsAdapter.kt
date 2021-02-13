package com.timmytruong.materialintervaltimer.ui.createtimer.adapters

import android.media.MediaPlayer
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor(
    private val createTimerViewModel: CreateTimerViewModel,
    sounds: List<IntervalSound>
) : BaseListAdapter<ItemSoundBinding, IntervalSound>() {

    init {
        addList(sounds)
    }

    private var previousPosition: Int = 0

    override val bindingLayout: Int
        get() = R.layout.item_sound

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSoundBinding>, position: Int) {
        holder.view.soundTitle = list[position].sound_name
        holder.view.isSelected = list[position].sound_is_selected
        holder.view.root.setOnClickListener { onSoundClicked(position = position) }
    }

    private fun onSoundClicked(position: Int) {
        list[previousPosition].sound_is_selected = false
        list[position].sound_is_selected = true
        createTimerViewModel.setSelectedSound(list[position])
        notifyItemChanged(previousPosition)
        notifyItemChanged(position)
        previousPosition = position
        playSound(position = position)
    }

    private fun playSound(position: Int) {
        val id = list[position].sound_id
        if (id != -1) {
            id.let { MediaPlayer.create(context, it).start() }
        }
    }
}