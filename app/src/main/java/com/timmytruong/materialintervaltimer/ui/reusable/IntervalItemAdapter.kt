package com.timmytruong.materialintervaltimer.ui.create.timer.adapters

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.base.screen.Clicks
import com.timmytruong.materialintervaltimer.base.screen.EmptyClicks
import com.timmytruong.materialintervaltimer.base.screen.ListBinding
import com.timmytruong.materialintervaltimer.databinding.ItemIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalItemAdapter @Inject constructor(): BaseListAdapter<ItemIntervalBinding, IntervalItemScreenBinding>() {

    override val bindingLayout: Int
        get() = R.layout.item_interval

    override fun onBindViewHolder(holder: BaseViewHolder<ItemIntervalBinding>, position: Int) {
        holder.view.screen = list[position]
    }
}

data class IntervalItemScreenBinding(
    val hasHeader: ObservableBoolean = ObservableBoolean(false),
    val header: ObservableField<String> = ObservableField(""),
    val iconId: ObservableInt = ObservableInt(0),
    val title: ObservableField<String> = ObservableField(""),
    val description: ObservableField<String> = ObservableField(""),
    override val clicks: Clicks<IntervalItemScreenBinding> = EmptyClicks
): ListBinding<IntervalItemScreenBinding>()