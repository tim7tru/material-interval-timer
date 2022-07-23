package com.timmytruong.data.util.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.timmytruong.data.model.Interval

class IntervalConverter {
    @TypeConverter
    fun toList(json: String?): ArrayList<Interval> = try {
        Gson().fromJson(json, Array<Interval>::class.java).toCollection(ArrayList())
    } catch (err: Exception) {
        ArrayList()
    }

    @TypeConverter
    fun toJson(list: ArrayList<Interval>?): String = try {
        Gson().toJson(list)
    } catch (err: Exception) {
        ""
    }
}