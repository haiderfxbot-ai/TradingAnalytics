package com.tradinganalytics.data.database

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class Converters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        if (list == null) return null
        val jsonArray = JSONArray()
        for (item in list) {
            jsonArray.put(item)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json == null) return null
        val jsonArray = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    @TypeConverter
    fun fromStringMap(map: Map<String, String>?): String? {
        if (map == null) return null
        val jsonObject = JSONObject()
        for ((key, value) in map) {
            jsonObject.put(key, value)
        }
        return jsonObject.toString()
    }

    @TypeConverter
    fun toStringMap(json: String?): Map<String, String>? {
        if (json == null) return null
        val jsonObject = JSONObject(json)
        val map = mutableMapOf<String, String>()
        for (key in jsonObject.keys()) {
            map[key] = jsonObject.getString(key)
        }
        return map
    }
}
