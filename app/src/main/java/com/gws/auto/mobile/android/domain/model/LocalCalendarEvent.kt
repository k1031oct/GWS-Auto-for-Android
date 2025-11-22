package com.gws.auto.mobile.android.domain.model

/**
 * Represents a single event fetched from the device's local calendar provider.
 *
 * @param title The title or name of the event.
 * @param startTime The start time of the event in epoch milliseconds.
 * @param endTime The end time of the event in epoch milliseconds.
 * @param allDay Indicates whether the event is an all-day event.
 */
data class LocalCalendarEvent(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val allDay: Boolean
)
