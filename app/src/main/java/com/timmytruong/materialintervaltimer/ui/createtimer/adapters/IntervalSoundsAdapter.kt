package com.timmytruong.materialintervaltimer.ui.createtimer.adapters

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.utils.constants.AppConstants
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor(
    private val createTimerViewModel: CreateTimerViewModel
) : RecyclerView.Adapter<IntervalSoundsAdapter.IntervalSoundsViewHolder>() {

    private lateinit var binding: ItemSoundBinding

    private var previousPosition: Int = 0

    private val sounds = AppConstants.SOUNDS

    private lateinit var parentContext: Context

    class IntervalSoundsViewHolder(val view: ItemSoundBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalSoundsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.item_sound, parent, false)
        parentContext = parent.context
        return IntervalSoundsViewHolder(view = binding)
    }

    override fun onBindViewHolder(holder: IntervalSoundsViewHolder, position: Int) {
        holder.view.soundTitle = sounds[position].sound_name

        holder.view.isSelected = sounds[position].sound_is_selected

        holder.view.root.setOnClickListener { onSoundClicked(position = position) }
    }

    override fun getItemCount(): Int = sounds.size

    private fun onSoundClicked(position: Int) {
        sounds[previousPosition].sound_is_selected = false
        sounds[position].sound_is_selected = true
        createTimerViewModel.setSelectedSound(sounds[position])
        notifyItemChanged(previousPosition)
        notifyItemChanged(position)
        previousPosition = position
        playSound(position = position)
    }

    private fun playSound(position: Int) {
        val id = sounds[position].sound_id
        if (id != -1) {
            id.let { MediaPlayer.create(parentContext, it).start() }
        }
    }
}