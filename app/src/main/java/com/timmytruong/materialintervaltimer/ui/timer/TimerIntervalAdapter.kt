package com.timmytruong.materialintervaltimer.ui.timer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.IntervalItemBinding
import com.timmytruong.materialintervaltimer.model.Interval
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class TimerIntervalAdapter @Inject constructor() :
    RecyclerView.Adapter<TimerIntervalAdapter.TimerIntervalViewHolder>() {

    companion object {
        const val CURRENT_TITLE = 0
        const val INTERVAL = 1
        const val UP_NEXT_TITLE = 2
    }

    private var intervalIterator = 0

    private lateinit var currentTitle: String

    private lateinit var upNextTitle: String

    private var intervals: ArrayList<Interval> = arrayListOf()

    private lateinit var binding: IntervalItemBinding

    class TimerIntervalViewHolder(val view: IntervalItemBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerIntervalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        currentTitle = parent.context.getString(R.string.currentIntervalTitle)
        upNextTitle = parent.context.getString(R.string.upNextIntervalTitle)
        binding = DataBindingUtil.inflate(inflater, R.layout.interval_item, parent, false)
        return TimerIntervalViewHolder(view = binding)
    }

    override fun onBindViewHolder(holder: TimerIntervalViewHolder, position: Int) {
        when (holder.itemViewType) {
            CURRENT_TITLE -> {
                holder.view.itemIntervalTextTitle.visibility = View.VISIBLE
                holder.view.title = currentTitle
            }
            UP_NEXT_TITLE -> {
                holder.view.itemIntervalTextTitle.visibility = View.VISIBLE
                holder.view.title = upNextTitle
            }
            INTERVAL -> {
                holder.view.itemIntervalTextTitle.visibility = View.GONE
            }
        }
        holder.view.interval = intervals[position]
        holder.view.intervalDragIcon.visibility = View.GONE
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> CURRENT_TITLE
            1 -> UP_NEXT_TITLE
            else -> INTERVAL
        }

    override fun getItemCount(): Int = intervals.size

    fun newList(list: ArrayList<Interval>) {
        intervalIterator = 0
        intervals.clear()
        intervals.addAll(list)
        notifyDataSetChanged()
    }

    fun popFirst() {
        intervals.removeAt(0)
        notifyItemRemoved(1)
    }
}