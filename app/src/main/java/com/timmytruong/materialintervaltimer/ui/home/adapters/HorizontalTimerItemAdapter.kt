package com.timmytruong.materialintervaltimer.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ItemTimerHorizontalBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel
import com.timmytruong.materialintervaltimer.utils.DesignUtils

class HorizontalTimerItemAdapter(private val homeViewModel: HomeViewModel) :
    RecyclerView.Adapter<HorizontalTimerItemAdapter.TimerItemViewHolder>() {

    class TimerItemViewHolder(val view: ItemTimerHorizontalBinding) :
        RecyclerView.ViewHolder(view.root)

    private lateinit var binding: ItemTimerHorizontalBinding

    private val timers: ArrayList<Timer> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.item_timer_horizontal, parent, false)
        return TimerItemViewHolder(view = binding)
    }

    override fun getItemCount(): Int = timers.size

    override fun onBindViewHolder(holder: TimerItemViewHolder, position: Int) {
        holder.view.timer = timers[position]
        holder.view.homeViewModel = homeViewModel
        val time = DesignUtils.getTimeFromSeconds(timers[position].timer_total_time_ms / 1000)
        holder.view.time = DesignUtils.formatNormalizedTime(
            time,
            holder.view.root.context.getString(R.string.timerTimeFormat)
        )
    }

    fun addNewList(newTimers: List<Timer>) {
        timers.clear()
        timers.addAll(newTimers)
        notifyDataSetChanged()
    }
}