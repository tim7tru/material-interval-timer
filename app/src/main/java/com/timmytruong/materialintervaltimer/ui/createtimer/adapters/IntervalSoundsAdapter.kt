package com.timmytruong.materialintervaltimer.ui.createtimer.adapters

import android.content.Context
import android.media.MediaPlayer
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.databinding.ItemSoundBinding
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Qualifier

@FragmentScoped
class IntervalSoundsAdapter @Inject constructor(
    private val createTimerViewModel: CreateTimerViewModel,
    @Close private val closeString: String,
    sounds: List<IntervalSound>
) : BaseListAdapter<ItemSoundBinding, IntervalSound>() {

    init {
        addList(sounds)
    }

    private var previousPosition: Int = 0

    override val bindingLayout: Int = R.layout.item_sound

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemSoundBinding>,
        position: Int
    ) {
        holder.apply {
            view.name = list[position].sound_name
            view.isSelected = list[position].sound_is_selected
            view.itemSoundButton.setOnClickListener {
                onSoundClicked(position = position)
            }
        }
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

@Qualifier
private annotation class Close

@InstallIn(FragmentComponent::class)
@Module
class IntervalSoundAdapterModule {

    @Provides
    @Close
    fun provideCloseString(@ApplicationContext context: Context) =
        context.getString(R.string.close)
}