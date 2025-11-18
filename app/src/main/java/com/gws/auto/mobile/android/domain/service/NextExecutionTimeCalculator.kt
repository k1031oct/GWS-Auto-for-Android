package com.gws.auto.mobile.android.domain.service

import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.ScheduleType
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

object NextExecutionTimeCalculator {

    fun calculateNextExecutionTime(schedule: Schedule, now: ZonedDateTime = ZonedDateTime.now()): ZonedDateTime {
        val scheduleTime = schedule.time?.let { LocalTime.parse(it) } ?: LocalTime.MIDNIGHT

        return when (schedule.scheduleType) {
            ScheduleType.HOURLY -> now.plusHours(schedule.hourlyInterval?.toLong() ?: 1L)
            ScheduleType.DAILY -> {
                var next = now.with(scheduleTime)
                if (next.isBefore(now)) {
                    next = next.plusDays(1)
                }
                next
            }
            ScheduleType.WEEKLY -> {
                val scheduleDays = schedule.weeklyDays?.mapNotNull(::mapJapaneseDayToDayOfWeek)?.toSet() ?: emptySet()
                if (scheduleDays.isEmpty()) return now.plusYears(100) // Should not be scheduled

                var next = now.with(scheduleTime)
                for (i in 0..7) {
                    val checkingDate = next.plusDays(i.toLong())
                    if (checkingDate.dayOfWeek in scheduleDays) {
                        if (checkingDate.isAfter(now)) {
                            return checkingDate
                        }
                    }
                }
                return now.plusYears(100) // Should not happen with valid data
            }
            ScheduleType.MONTHLY -> {
                val scheduleDays = schedule.monthlyDays?.sorted()?.toSet() ?: emptySet()
                if (scheduleDays.isEmpty()) return now.plusYears(100) // Should not be scheduled

                var next = now.with(scheduleTime)

                // Attempt to find a valid day in the current month
                for (day in scheduleDays) {
                    if (day >= next.dayOfMonth) {
                        // Check if day is valid for the current month
                        if (day <= next.toLocalDate().lengthOfMonth()) {
                            val candidate = next.withDayOfMonth(day)
                            if (candidate.isAfter(now)) {
                                return candidate
                            }
                        }
                    }
                }

                // If not found, move to the next month and find the first valid day
                val nextMonth = now.plusMonths(1)
                for (day in scheduleDays) {
                    if (day <= nextMonth.toLocalDate().lengthOfMonth()) {
                        return nextMonth.withDayOfMonth(day).with(scheduleTime)
                    }
                }
                // If still not found (e.g. only day 31 is selected, and next month is February)
                // look in subsequent months.
                for (i in 2..12) {
                     val futureMonth = now.plusMonths(i.toLong())
                     for (day in scheduleDays) {
                        if (day <= futureMonth.toLocalDate().lengthOfMonth()) {
                            return futureMonth.withDayOfMonth(day).with(scheduleTime)
                        }
                    }
                }
                return now.plusYears(100) // Should not happen
            }
            ScheduleType.YEARLY -> {
                val month = schedule.yearlyMonth ?: now.monthValue
                val day = schedule.yearlyDayOfMonth ?: now.dayOfMonth
                var next = now.with(scheduleTime).withMonth(month).withDayOfMonth(day)
                if (next.isBefore(now)) {
                    next = next.plusYears(1)
                }
                next
            }
        }
    }

    private fun mapJapaneseDayToDayOfWeek(japaneseDay: String): DayOfWeek? {
        return when (japaneseDay) {
            "月" -> DayOfWeek.MONDAY
            "火" -> DayOfWeek.TUESDAY
            "水" -> DayOfWeek.WEDNESDAY
            "木" -> DayOfWeek.THURSDAY
            "金" -> DayOfWeek.FRIDAY
            "土" -> DayOfWeek.SATURDAY
            "日" -> DayOfWeek.SUNDAY
            else -> null
        }
    }
}
