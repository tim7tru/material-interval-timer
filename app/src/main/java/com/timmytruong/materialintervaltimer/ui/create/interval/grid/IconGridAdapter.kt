package com.timmytruong.materialintervaltimer.ui.create.interval.grid

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemIconBinding
import com.timmytruong.materialintervaltimer.ui.base.adapter.BaseListAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.item.IconGridItem
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class IconGridAdapter @Inject constructor(private val resources: ResourceProvider): BaseListAdapter<ItemIconBinding, IconGridItem>(
    ItemIconBinding::inflate
) {

    override fun onBindViewHolder(holder: ViewHolder<ItemIconBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = list[position]
        with(holder.view) {
            icon.setBackgroundColor(
                if (item.isSelected) resources.color(R.color.colorSecondaryAccent)
                else resources.color(R.color.colorBackgroundDark)
            )
            icon.setImageResource(item.id)
            icon.setOnClickListener { item.clicks.invoke(position) }
        }
    }
}
