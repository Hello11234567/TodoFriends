package com.example.todofriends.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth

// ✅ HomeActivity 클래스 삭제
// ✅ BottomNavBar 삭제
// ✅ ScheduleItem, getDummySchedules는 ScheduleViewModel.kt로 이동 예정, 지금은 유지

data class ScheduleItem(
    val id: Int,
    val title: String,
    val time: String,
    val endTime: String = "",
    val isDone: Boolean = false
)

fun calculateEndTime(startTime: String, duration: String): String {
    return try {
        val parts = startTime.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val durationMin = duration.toIntOrNull() ?: 0
        val totalMinutes = hour * 60 + minute + durationMin
        String.format("%02d:%02d", totalMinutes / 60, totalMinutes % 60)
    } catch (e: Exception) {
        ""
    }
}
//ScheduleViewModel 파라미터 추가
@Composable
fun HomeScreen(scheduleViewModel: ScheduleViewModel) {
    val bgColor = Color(0xFF0F0F13)
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val showSchedule = selectedDate != null

    //ViewModel에서 일정 데이터 구독
    val scheduleMap by scheduleViewModel.scheduleMap.collectAsState()

    val calendarWeight by animateFloatAsState(
        targetValue = if (showSchedule) 0.52f else 1f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "calendarWeight"
    )

    //Scaffold, bottomBar 제거 -> NavActivity가 담당
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        CalendarSection(
            modifier = Modifier.weight(calendarWeight),
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = if (selectedDate == date) null else date
            },
            isCompact = showSchedule
        )

        if (showSchedule) {
            AnimatedVisibility(
                visible = true,
                modifier = Modifier.weight(1f - calendarWeight + 0.48f),
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400, easing = EaseInOutCubic)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut()
            ) {
                selectedDate?.let { date ->
                    //ViewModel 데이터 우선, 없으면 더미 데이터
                    val schedules = scheduleMap[date] ?:emptyList()
                    ScheduleSection(
                        selectedDate = date,
                        schedules = schedules,
                        onCheckedChange = { index, item ->
                            scheduleViewModel.toggleSchedule(date, index, item)
                        },
                        onDelete = { index, item ->
                            scheduleViewModel.removeSchedule(date, item.id)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarSection(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    isCompact: Boolean = false
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val today = remember { LocalDate.now() }

    val selectedColor = Color(0xFFBF9B72)
    val todayColor = Color(0xFF4A3828)
    val sundayColor = Color(0xFFFF6B6B)
    val saturdayColor = Color(0xFF6B9FFF)

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val visibleMonth = state.firstVisibleMonth.yearMonth

    val monthFontSize by animateFloatAsState(
        targetValue = if (isCompact) 20f else 28f,
        animationSpec = tween(400),
        label = "monthFontSize"
    )

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val cellHeight = if (isCompact) (screenHeight * 0.52f - 120.dp) / 6
    else (screenHeight - 56.dp - 48.dp - 40.dp) / 6

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (isCompact) 12.dp else 20.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${visibleMonth.year}년 ${visibleMonth.monthValue}월",
                    color = Color.White,
                    fontSize = monthFontSize.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = when (day) {
                        "일" -> sundayColor.copy(alpha = 0.7f)
                        "토" -> saturdayColor.copy(alpha = 0.7f)
                        else -> Color(0xFF666677)
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        HorizontalCalendar(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            dayContent = { day ->
                val isSelected = day.date == selectedDate
                val isToday = day.date == today
                val isCurrentMonth =
                    day.position == com.kizitonwose.calendar.core.DayPosition.MonthDate
                val isSunday = day.date.dayOfWeek.value == 7
                val isSaturday = day.date.dayOfWeek.value == 6

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cellHeight)
                        .clickable { onDateSelected(day.date) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 30.dp else 34.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> selectedColor
                                    isToday -> todayColor
                                    else -> Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            color = when {
                                isSelected -> Color(0xFF1C0E06)
                                isToday -> Color(0xFFE8C9A0)
                                !isCurrentMonth -> Color(0xFF444455)
                                isSunday -> sundayColor
                                isSaturday -> saturdayColor
                                else -> Color.White
                            },
                            fontSize = if (isCompact) 11.sp else 13.sp,
                            fontWeight = when {
                                isSelected || isToday -> FontWeight.Bold
                                else -> FontWeight.Normal
                            }
                        )
                    }

                    if (isToday && !isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 2.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(selectedColor)
                        )
                    }
                }
            }
        )
    }
}

// ✅ schedules, onCheckedChange 파라미터 추가
@Composable
fun ScheduleSection(
    selectedDate: LocalDate,
    schedules: List<ScheduleItem>,
    onCheckedChange: (Int, ScheduleItem) -> Unit,
    onDelete: (Int, ScheduleItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(0xFFBF9B72)
    val bgSection = Color(0xFF16161C)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(bgSection)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF444455))
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .background(accentColor, shape = RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일의 일정",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "등록된 일정이 없어요 🗓️",
                    color = Color(0xFF555566),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                itemsIndexed(schedules) { index, item ->
                    key(item.id) {
                        ScheduleRow(
                            item = item,
                            accentColor = accentColor,
                            onCheckedChange = { onCheckedChange(index, item) },
                            onDelete = { onDelete(index, item) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleRow(
    item: ScheduleItem,
    accentColor: Color,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit //삭제 콜백
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEF5350))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        dismissContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16161C))
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (item.isDone) accentColor else Color.Transparent)
                        .border(1.5.dp, if (item.isDone) accentColor else Color(0xFF555555), CircleShape)
                        .clickable { onCheckedChange() },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF1C0E06),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (item.time.isNotEmpty()) {
                        if (item.endTime.isNotEmpty()) "${item.title}  (${item.time} - ${item.endTime})"
                        else "${item.title}  (${item.time})"
                    } else item.title,
                    color = if (item.isDone) Color(0xFF555566) else Color.White,
                    fontSize = 14.sp,
                    fontWeight = if (item.isDone) FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                )
            }
        }
    )
}