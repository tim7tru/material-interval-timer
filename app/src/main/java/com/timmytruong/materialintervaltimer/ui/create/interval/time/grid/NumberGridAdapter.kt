package com.timmytruong.materialintervaltimer.ui.create.interval.time.grid

import com.timmytruong.materialintervaltimer.databinding.ItemNumberBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.base.adapter.Clicks
import com.timmytruong.materialintervaltimer.ui.base.adapter.ListItem
import javax.inject.Inject

class NumberGridAdapter @Inject constructor(): BaseListAdapter<ItemNumberBinding, NumberItem>(
    ItemNumberBinding::inflate
) {

    override fun onBindViewHolder(holder: ViewHolder<ItemNumberBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = list[position]
        with (holder.view) {
            if (item.number == null) {
                number.isClickable = false
            } else {
                number.text = item.number.toString()
                number.setOnClickListener { item.clicks.invoke(position) }
            }
        }
    }
}

data class NumberItem(
    val number: Int? = null,
    override val clicks: Clicks
): ListItem()