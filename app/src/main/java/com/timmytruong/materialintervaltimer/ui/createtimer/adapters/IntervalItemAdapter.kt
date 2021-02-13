package com.timmytruong.materialintervaltimer.ui.createtimer.adapters

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseListAdapter
import com.timmytruong.materialintervaltimer.databinding.ItemIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class IntervalItemAdapter @Inject constructor():
    BaseListAdapter<ItemIntervalBinding, Interval>() {

    override val bindingLayout: Int
        get() = R.layout.item_interval

    override fun onBindViewHolder(holder: BaseViewHolder<ItemIntervalBinding>, position: Int) {
        holder.view.interval = list[position]
        holder.view.titleVisible = false
    }
}