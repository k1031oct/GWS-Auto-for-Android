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
            ScheduleType.HOURLY -> {
                now.plusHours(schedule.hourlyInterval?.toLong() ?: 1)
            }
            ScheduleType.DAILY -> {
                val todayExecution = now.toLocalDate().atTime(time)
                if (todayExecution.isAfter(now)) todayExecution else todayExecution.plusDays(1)
            }
            ScheduleType.WEEKLY -> {
                val scheduleDays = schedule.weeklyDays?.mapNotNull(::mapDayToDayOfWeek)?.toSet()
                if (scheduleDays.isNullOrEmpty()) {
                    now.plusYears(100) // Should not happen with valid data
                } else {
                    var potentialExecution = now.toLocalDate().atTime(time)
                    if (!potentialExecution.isAfter(now)) {
                        potentialExecution = potentialExecution.plusDays(1)
                    }
                    while (potentialExecution.dayOfWeek !in scheduleDays) {
                        potentialExecution = potentialExecution.plusDays(1)
                    }
                    potentialExecution
                }
            }
            ScheduleType.MONTHLY -> {
                val scheduleDaysOfMonth = schedule.monthlyDays?.toSet()
                if (scheduleDaysOfMonth.isNullOrEmpty()) {
                    now.plusYears(100) // Should not happen
                } else {
                    var potentialExecution = now.toLocalDate().atTime(time)
                    if (!potentialExecution.isAfter(now)) {
                        potentialExecution = potentialExecution.plusDays(1)
                    }
                    while (potentialExecution.dayOfMonth !in scheduleDaysOfMonth) {
                        potentialExecution = potentialExecution.plusDays(1)
                    }
                    potentialExecution
                }
            }
            ScheduleType.YEARLY -> {
                val thisYearExecution = now.toLocalDate()
                    .withMonth(schedule.yearlyMonth ?: 1)
                    .withDayOfMonth(schedule.yearlyDayOfMonth ?: 1)
                    .atTime(time)
                if (thisYearExecution.isAfter(now)) {
                    thisYearExecution
                } else {
                    thisYearExecution.plusYears(1)
                }
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
