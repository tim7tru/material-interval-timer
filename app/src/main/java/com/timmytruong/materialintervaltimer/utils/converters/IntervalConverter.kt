package com.timmytruong.materialintervaltimer.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.timmytruong.materialintervaltimer.model.Interval

class IntervalConverter {
    @TypeConverter
    fun toList(json: String?): ArrayList<Interval> {
        return try {
            Gson().fromJson(json, Array<Interval>::class.java).toCollection(ArrayList())
        } catch (err: Exception) {
            ArrayList()
        }
    }

    @TypeConverter
    fun toJson(list: ArrayList<Interval>?): String {
        return try {
            Gson().toJson(list)
        } catch (err: Exception) {
            ""
        }
    }
}