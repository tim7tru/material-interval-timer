package com.timmytruong.materialintervaltimer.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.timmytruong.materialintervaltimer.model.Time

class TimeConverter {
    @TypeConverter
    fun toList(json: String?): Time {
        return try {
            Gson().fromJson(json, Time::class.java)
        } catch (err: Exception) {
            Time()
        }
    }

    @TypeConverter
    fun toJson(sound: Time?): String {
        return try {
            Gson().toJson(sound)
        } catch (err: Exception) {
            ""
        }
    }
}