package com.timmytruong.materialintervaltimer.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.HorizontalCardBinding
import com.timmytruong.materialintervaltimer.model.MockData
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners

class HorizontalTimerItemAdapter(
    private val homeFrag: OnClickListeners.HomeFrag,
    private val timers: ArrayList<Timer> = MockData.timerList
): RecyclerView.Adapter<HorizontalTimerItemAdapter.TimerItemViewHolder>() {

    private lateinit var binding: HorizontalCardBinding

    class TimerItemViewHolder(val view: HorizontalCardBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.horizontal_card, parent, false)
        return TimerItemViewHolder(view = binding)
    }

    override fun getItemCount(): Int = timers.size

    override fun onBindViewHolder(holder: TimerItemViewHolder, position: Int) {
        holder.view.timer = MockData.timer
        holder.view.onClick = homeFrag
    }
}