package com.timmytruong.materialintervaltimer

import com.timmytruong.materialintervaltimer.utils.DesignUtils
import org.junit.Before
import org.junit.Test

class DesignUtilsTest {

    private lateinit var design: DesignUtils

    @Before
    fun init() {
        design = DesignUtils
    }

    @Test
    fun validSecondsFromTimeSecondsOnly() {
        val time = "45"
        val ans = 45
        assert(design.getSecondsFromTime(time) == ans)
    }

    @Test
    fun validSecondsFromTimeMinutesAndSeconds() {
        val time = "245"
        val ans = 165
        assert(design.getSecondsFromTime(time) == ans)
    }

    @Test
    fun validSecondsFromTimeHoursAndMinutesAndSeconds() {
        val time = "142020"
        val ans = 51620
        assert(design.getSecondsFromTime(time) == ans)
    }

    @Test
    fun validSecondsFromTimeHoursAndSeconds() {
        val time = "140020"
        val ans = 50420
        assert(design.getSecondsFromTime(time) == ans)
    }

    @Test
    fun validTimeFromSecondsSecondsOnly() {
        val seconds = 45
        val time = "000045"
        assert(design.getTimeFromSeconds(seconds) == time)
    }

    @Test
    fun validTimeFromSecondsMinutesAndSeconds() {
        val seconds = 165
        val time = "000245"
        assert(design.getTimeFromSeconds(seconds) == time)
    }

    @Test
    fun validTimeFromSecondsHoursMinutesAndSeconds() {
        val seconds = 51620
        val time = "142020"
        assert(design.getTimeFromSeconds(seconds) == time)
    }

    @Test
    fun validTimeFromSecondsHoursAndSeconds() {
        val seconds = 50420
        val time = "140020"
        assert(design.getTimeFromSeconds(seconds) == time)
    }
}