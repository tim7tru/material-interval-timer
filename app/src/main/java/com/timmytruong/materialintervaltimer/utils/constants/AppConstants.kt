package com.timmytruong.materialintervaltimer.utils.constants

import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.model.IntervalSound

object AppConstants {
    val SOUNDS = arrayListOf(
        IntervalSound(-1, "None", true),
        IntervalSound(R.raw.beep, "Beep", false),
        IntervalSound(R.raw.another_beep, "Another beep", false),
        IntervalSound(R.raw.censor, "Censor", false),
        IntervalSound(R.raw.ding, "Ding", false),
        IntervalSound(R.raw.elevator, "Elevator", false),
        IntervalSound(R.raw.error, "Error", false),
        IntervalSound(R.raw.robot, "Robot", false),
        IntervalSound(R.raw.synth, "Synth", false),
    )

    const val TIME_FORMAT = "%sh %sm %ss"
}