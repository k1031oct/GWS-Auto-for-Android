package com.gws.auto.mobile.android.ui.schedule

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.ScheduleType
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    viewModel: ScheduleViewModel
) {
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val schedulesForSelectedDate by viewModel.schedulesForSelectedDate.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()

    Scaffold {
 paddingValues ->
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 32.dp, // Provide a peek height for the handle area
            sheetContent = { DayTimelineSheet(date = currentDate, schedules = schedulesForSelectedDate) },
            sheetContainerColor = MaterialTheme.colorScheme.surfaceContainer, // Use a lighter surface color
            containerColor = MaterialTheme.colorScheme.background // Ensure calendar has the correct background
        ) {
            CalendarContent(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            ) {
                viewModel.setCurrentDate(it)
                scope.launch { scaffoldState.bottomSheetState.expand() }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarContent(
    viewModel: ScheduleViewModel, 
    modifier: Modifier = Modifier,
    onDateClick: (LocalDate) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val holidays by viewModel.holidays.collectAsState()
    val allSchedules by viewModel.allSchedules.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val firstDayOfWeekSetting by viewModel.firstDayOfWeek.collectAsState()

    val daysOfWeek = remember(firstDayOfWeekSetting) {
        val week = DayOfWeek.values()
        if (firstDayOfWeekSetting.equals("Monday", ignoreCase = true)) {
            listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        } else {
            week.toList()
        }
    }

    val currentVisibleMonth by remember {
        derivedStateOf {
            YearMonth.now().plusMonths((pagerState.currentPage - (Int.MAX_VALUE / 2)).toLong())
        }
    }

    LaunchedEffect(currentDate) {
        val targetPage = (Int.MAX_VALUE / 2) + ChronoUnit.MONTHS.between(YearMonth.now(), YearMonth.from(currentDate))
        if (pagerState.currentPage != targetPage.toInt()) {
            pagerState.animateScrollToPage(targetPage.toInt())
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(onClick = { viewModel.moveToPreviousMonth() }) { Text(stringResource(id = R.string.calendar_previous_month_button)) }
            Text(
                text = currentVisibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                style = MaterialTheme.typography.headlineSmall
            )
            FilledTonalButton(onClick = { viewModel.moveToNextMonth() }) { Text(stringResource(id = R.string.calendar_next_month_button)) }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        VerticalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val month = YearMonth.now().plusMonths((page - (Int.MAX_VALUE / 2)).toLong())
            MonthView(
                yearMonth = month,
                holidays = holidays,
                schedules = allSchedules,
                onDateClick = onDateClick,
                daysOfWeek = daysOfWeek
            )
        }
    }
}

@Composable
fun DayTimelineSheet(date: LocalDate, schedules: List<Schedule>) {
    val holidays = emptyList<Holiday>()
    val schedulesWithTime = schedules.mapNotNull { schedule ->
        schedule.time?.let { timeStr ->
            try {
                LocalTime.parse(timeStr) to (schedule.workflowId)
            } catch (e: Exception) {
                null
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp, top = 16.dp)
        )

        LazyColumn {
            items(holidays) { holiday ->
                Text(holiday.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            }

            item {
                HourTimeline(schedules = schedulesWithTime, timelineHourHeight = 64.dp, hourTextWidth = 60.dp, eventColor = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun HourTimeline(schedules: List<Pair<LocalTime, String>>, timelineHourHeight: androidx.compose.ui.unit.Dp, hourTextWidth: androidx.compose.ui.unit.Dp, eventColor: Color) {
    val timelineColor = MaterialTheme.colorScheme.outlineVariant

    BoxWithConstraints(modifier = Modifier
        .fillMaxWidth()
        .height(timelineHourHeight * 24)) {
        for (hour in 0..23) {
            Row(modifier = Modifier
                .height(timelineHourHeight)
                .offset(y = (hour * timelineHourHeight.value).dp)) {
                Text(
                    text = String.format("%02d:00", hour),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .width(hourTextWidth)
                        .padding(end = 8.dp),
                    textAlign = TextAlign.End
                )
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(timelineColor))
            }
        }

        schedules.forEach { (time, name) ->
            val yOffset = with(LocalDensity.current) {
                (time.hour * timelineHourHeight.toPx()) + (time.minute / 60f * timelineHourHeight.toPx())
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = yOffset.dp, x = hourTextWidth),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .size(8.dp)
                    .background(eventColor, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun MonthView(
    yearMonth: YearMonth,
    holidays: List<Holiday>,
    schedules: List<Schedule>,
    onDateClick: (LocalDate) -> Unit,
    daysOfWeek: List<DayOfWeek>
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = daysOfWeek.indexOf(firstDayOfMonth.dayOfWeek)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxHeight(), // Ensure no background is drawn by the grid
        userScrollEnabled = false
    ) {
        if (startOffset >= 0) {
            items(startOffset) { }
        }

        items(yearMonth.lengthOfMonth()) { dayIndex ->
            val dayOfMonth = dayIndex + 1
            val date = yearMonth.atDay(dayOfMonth)

            DayCell(
                date = date,
                schedules = schedules.filter { schedule ->
                    when (schedule.scheduleType.name) {
                        ScheduleType.DAILY.name -> true
                        ScheduleType.WEEKLY.name -> schedule.weeklyDays?.any { day -> day.equals(date.dayOfWeek.name, ignoreCase = true) } == true
                        ScheduleType.MONTHLY.name -> schedule.monthlyDays?.contains(date.dayOfMonth) == true
                        ScheduleType.YEARLY.name -> schedule.yearlyMonth == date.monthValue && schedule.yearlyDayOfMonth == date.dayOfMonth
                        else -> false
                    }
                },
                holidays = holidays.filter { it.date == date },
                modifier = Modifier.clickable { onDateClick(date) }
            )
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    schedules: List<Schedule>,
    holidays: List<Holiday>,
    modifier: Modifier = Modifier
) {
    val isToday = date == LocalDate.now()

    Column(
        modifier = modifier
            .height(120.dp)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = if (isToday) Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            else Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                textAlign = TextAlign.Center,
                color = if (holidays.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        holidays.forEach { ScheduleItemText(it.name) }
        schedules.forEach { ScheduleItemText(it.workflowId) }
    }
}

@Composable
fun ScheduleItemText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
