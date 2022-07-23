package com.timmytruong.data.util.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.timmytruong.data.model.IntervalSound

class IntervalSoundConverter {
    @TypeConverter
    fun toList(json: String?): IntervalSound {
        return try {
            Gson().fromJson(json, IntervalSound::class.java)
        } catch (err: Exception) {
            IntervalSound()
        }
    }

    @TypeConverter
    fun toJson(sound: IntervalSound?): String {
        return try {
            Gson().toJson(sound)
        } catch (err: Exception) {
            ""
        }
    }
}