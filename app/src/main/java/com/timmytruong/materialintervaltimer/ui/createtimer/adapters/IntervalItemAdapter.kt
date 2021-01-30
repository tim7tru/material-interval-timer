package com.timmytruong.materialintervaltimer.ui.createtimer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.IntervalItemBinding
import com.timmytruong.materialintervaltimer.model.Interval
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class IntervalItemAdapter @Inject constructor(): RecyclerView.Adapter<IntervalItemAdapter.IntervalItemViewHolder>() {

    private var intervals: ArrayList<Interval> = arrayListOf()

    private lateinit var binding: IntervalItemBinding

    class IntervalItemViewHolder(val view: IntervalItemBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.interval_item, parent, false)
        return IntervalItemViewHolder(view = binding)
    }

    override fun onBindViewHolder(holder: IntervalItemViewHolder, position: Int) {
        holder.view.interval = intervals[position]
        holder.view.titleVisible = false
    }

    override fun getItemCount(): Int = intervals.size

    fun newList(list: ArrayList<Interval>) {
        intervals.clear()
        intervals.addAll(list)
        notifyDataSetChanged()
    }
}