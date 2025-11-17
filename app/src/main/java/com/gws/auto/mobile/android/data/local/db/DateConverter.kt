package com.gws.auto.mobile.android.data.local.db

import androidx.room.TypeConverter
import com.gws.auto.mobile.android.domain.model.ScheduleType
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

class ScheduleTypeConverter {
    @TypeConverter
    fun fromScheduleType(value: ScheduleType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toScheduleType(value: String?): ScheduleType? {
        return value?.let { ScheduleType.valueOf(it) }
    }
}
