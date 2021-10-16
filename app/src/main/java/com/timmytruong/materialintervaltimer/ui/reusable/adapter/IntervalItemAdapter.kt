package com.timmytruong.materialintervaltimer.ui.reusable.adapter

import androidx.databinding.ObservableInt
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.base.screen.Clicks
import com.timmytruong.materialintervaltimer.base.screen.EmptyClicks
import com.timmytruong.materialintervaltimer.base.screen.ListBinding
import com.timmytruong.materialintervaltimer.databinding.ItemIntervalBinding
import com.timmytruong.materialintervaltimer.utils.ObservableString
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IntervalItemAdapter @Inject constructor():
    BaseListAdapter<ItemIntervalBinding, IntervalItemScreenBinding>() {

    override val bindingLayout: Int
        get() = R.layout.item_interval

    override fun onBindViewHolder(holder: BaseViewHolder<ItemIntervalBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.screen = list[position]
    }
}

data class IntervalItemScreenBinding(
    val header: ObservableString = ObservableString(""),
    val iconId: ObservableInt = ObservableInt(0),
    val title: ObservableString = ObservableString(""),
    val description: ObservableString = ObservableString(""),
    override val clicks: Clicks = EmptyClicks
): ListBinding()