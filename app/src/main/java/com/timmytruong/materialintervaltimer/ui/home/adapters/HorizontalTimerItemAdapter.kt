package com.timmytruong.materialintervaltimer.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.HorizontalCardBinding
import com.timmytruong.materialintervaltimer.model.MockData
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel

class HorizontalTimerItemAdapter(private val homeViewModel: HomeViewModel) :
    RecyclerView.Adapter<HorizontalTimerItemAdapter.TimerItemViewHolder>() {

    class TimerItemViewHolder(val view: HorizontalCardBinding) : RecyclerView.ViewHolder(view.root)

    private lateinit var binding: HorizontalCardBinding

    private val timers: ArrayList<Timer> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.horizontal_card, parent, false)
        return TimerItemViewHolder(view = binding)
    }

    override fun getItemCount(): Int = timers.size

    override fun onBindViewHolder(holder: TimerItemViewHolder, position: Int) {
        holder.view.timer = timers[position]
        holder.view.homeViewModel = homeViewModel
    }

    fun addNewList(newTimers: List<Timer>) {
        timers.clear()
        timers.addAll(newTimers)
        notifyDataSetChanged()
    }
}