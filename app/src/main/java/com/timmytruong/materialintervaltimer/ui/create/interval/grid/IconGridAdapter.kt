package com.timmytruong.materialintervaltimer.ui.create.interval.grid

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemIconBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.IconGridItem
import com.timmytruong.materialintervaltimer.utils.extensions.color
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IconGridAdapter @Inject constructor(): BaseListAdapter<ItemIconBinding, IconGridItem>(
    ItemIconBinding::inflate
) {

    override fun onBindViewHolder(holder: ViewHolder<ItemIconBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = list[position]
        with(holder.view) {
            icon.setBackgroundColor(
                if (item.isSelected) root.context.color(R.color.color_secondary_accent)
                else root.context.color(R.color.color_background_dark)
            )
            icon.setImageResource(item.id)
            icon.setOnClickListener { item.clicks.invoke(position) }
        }
    }
}
