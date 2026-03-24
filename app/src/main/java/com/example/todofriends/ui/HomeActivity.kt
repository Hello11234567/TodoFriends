package com.example.todofriends.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todofriends.R
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HomeScreen() }
    }
}

data class ScheduleItem(
    val id: Int,
    val title: String,
    val time: String,
    val isDone: Boolean = false
)

fun getDummySchedules(date: LocalDate): List<ScheduleItem> {
    return when (date.dayOfMonth) {
        24 -> listOf(
            ScheduleItem(1, "목욕하기", "10:00 - 11:00", true),
            ScheduleItem(2, "친구와 점심", "13:00 - 14:00", true),
            ScheduleItem(3, "공부하기", "15:00 - 17:00"),
            ScheduleItem(4, "운동", "18:00 - 19:00")
        )
        else -> emptyList()
    }
}

@Composable
fun HomeScreen() {
    val bgColor = Color(0xFF0F0F13)
    var selectedTab by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val showSchedule = selectedDate != null

    // 캘린더 비율 애니메이션: 선택 전 1f, 선택 후 0.55f
    val calendarWeight by animateFloatAsState(
        targetValue = if (showSchedule) 0.52f else 1f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "calendarWeight"
    )

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {
            // 캘린더: weight가 줄어들며 위로 올라감
            CalendarSection(
                modifier = Modifier.weight(calendarWeight),
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = if (selectedDate == date) null else date
                },
                isCompact = showSchedule
            )

            //weight를 AnimatedVisibility 바깥으로 빼기
            if(showSchedule) {
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
                        ScheduleSection(
                            selectedDate = date,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
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

    // 헤더 폰트 크기 애니메이션
    val monthFontSize by animateFloatAsState(
        targetValue = if (isCompact) 20f else 28f,
        animationSpec = tween(400),
        label = "monthFontSize"
    )
    val yearFontSize by animateFloatAsState(
        targetValue = if (isCompact) 11f else 14f,
        animationSpec = tween(400),
        label = "yearFontSize"
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

@Composable
fun ScheduleSection(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(0xFFBF9B72)
    val bgSection = Color(0xFF16161C)
    var schedules by remember(selectedDate) {
        mutableStateOf(getDummySchedules(selectedDate))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(bgSection)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // 핸들 바
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF444455))
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 헤더
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
                    ScheduleRow(
                        item = item,
                        accentColor = accentColor,
                        onCheckedChange = {
                            schedules = schedules.toMutableList().also {
                                it[index] = item.copy(isDone = !item.isDone)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleRow(
    item: ScheduleItem,
    accentColor: Color,
    onCheckedChange: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
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
            text = if (item.time.isNotEmpty()) "${item.title}  (${item.time})" else item.title,
            color = if (item.isDone) Color(0xFF555566) else Color.White,
            fontSize = 14.sp,
            fontWeight = if (item.isDone) FontWeight.Normal else FontWeight.Medium,
            textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val bgColor = Color(0xFF0F0F13)
    val activeColor = Color(0xFFBF9B72)
    val inactiveColor = Color(0xFF666666)

    NavigationBar(containerColor = bgColor, tonalElevation = 0.dp) {
        listOf(
            Triple(0, R.drawable.schedule, "일정"),
            Triple(1, R.drawable.home, "홈"),
            Triple(2, R.drawable.friend, "친구"),
            Triple(3, R.drawable.mypage, "내 정보")
        ).forEach { (index, icon, label) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        tint = if (selectedTab == index) activeColor else inactiveColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = bgColor)
            )
        }
    }
}