package com.gws.auto.mobile.android.domain.service

import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.ScheduleType
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object NextExecutionTimeCalculator {

    fun calculateDelay(schedule: Schedule, now: LocalDateTime = LocalDateTime.now()): Duration {
        val nextExecutionTime = calculateNextExecution(schedule, now)
        return Duration.between(now, nextExecutionTime)
    }

    fun calculateNextExecution(schedule: Schedule, now: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        val time = schedule.time?.let { LocalTime.parse(it) } ?: LocalTime.MIDNIGHT

        return when (schedule.scheduleType) {
            ScheduleType.HOURLY -> now.plusHours(schedule.hourlyInterval?.toLong() ?: 1)

            ScheduleType.DAILY -> {
                val todayExecution = now.with(time)
                if (todayExecution.isAfter(now)) todayExecution else todayExecution.plusDays(1)
            }

            ScheduleType.WEEKLY -> {
                val scheduleDays = schedule.weeklyDays
                    ?.mapNotNull { dayString ->
                        mapDayToDayOfWeek(dayString)
                    }
                    ?.sorted() ?: return now.plusYears(100) // No days, schedule far in future

                var next = now.with(time)
                if (next.isBefore(now)) {
                    next = next.plusDays(1)
                }

                while (next.dayOfWeek !in scheduleDays) {
                    next = next.plusDays(1)
                }
                next
            }

            ScheduleType.MONTHLY -> {
                val scheduleDaysOfMonth = schedule.monthlyDays?.sorted() ?: return now.plusYears(100)
                
                var tempDate = now
                while (true) {
                    for (day in scheduleDaysOfMonth) {
                        // Check if the day is valid for the current month
                        if (day <= tempDate.toLocalDate().lengthOfMonth()) {
                            val nextExecution = tempDate.withDayOfMonth(day).with(time)
                            if (nextExecution.isAfter(now)) {
                                return nextExecution
                            }
                        }
                    }
                    // Move to the next month
                    tempDate = tempDate.plusMonths(1).withDayOfMonth(1)
                }
            }
            
            ScheduleType.YEARLY -> {
                val next = now.with(time)
                    .withMonth(schedule.yearlyMonth ?: 1)
                    .withDayOfMonth(schedule.yearlyDayOfMonth ?: 1)
                if (next.isAfter(now)) next else next.plusYears(1)
            }
        }
    }

    private fun mapDayToDayOfWeek(day: String): DayOfWeek? {
        return when (day.trim().uppercase()) {
            "月", "MONDAY" -> DayOfWeek.MONDAY
            "火", "TUESDAY" -> DayOfWeek.TUESDAY
            "水", "WEDNESDAY" -> DayOfWeek.WEDNESDAY
            "木", "THURSDAY" -> DayOfWeek.THURSDAY
            "金", "FRIDAY" -> DayOfWeek.FRIDAY
            "土", "SATURDAY" -> DayOfWeek.SATURDAY
            "日", "SUNDAY" -> DayOfWeek.SUNDAY
            else -> null
        }
    }
}
