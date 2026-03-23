package com.example.todofriends.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.style.TextAlign
import com.example.todofriends.R
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.YearMonth

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    val bgColor = Color(0xFF0F0F13)
    var selectedTab by remember { mutableStateOf(1) } //홈이 기본 선택

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
            CalendarSection()
        }
    }
}

@Composable
fun CalendarSection() {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    var selectedDate by remember { mutableStateOf<java.time.LocalDate?>(null) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val visibleMonth = state.firstVisibleMonth.yearMonth

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        //월 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${visibleMonth.year}년 ${visibleMonth.monthValue}월",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = when(day) {
                        "일" -> Color(0xFFFF5B5B)
                        "토" -> Color(0xFF5B8FFF)
                        else -> Color(0xFF888888)
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //캘린더
        HorizontalCalendar(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            dayContent = { day ->
                val isSelected = day.date == selectedDate
                val isCurrentMonth = day.position == com.kizitonwose.calendar.core.DayPosition.MonthDate

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFF4CAF50)
                            else Color.Transparent
                        )
                        .clickable { selectedDate = day.date },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> Color.White
                            !isCurrentMonth -> Color(0xFF444444)
                            day.date.dayOfWeek.value == 7 -> Color(0xFFFF5B5B)
                            day.date.dayOfWeek.value == 6 -> Color(0xFF5B8FFF)
                            else -> Color.White
                        },
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        )
    }
}

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val bgColor = Color(0xFF0F0F13)
    val activeColor = Color(0xFF4CAF50)
    val inactiveColor = Color(0xFF666666)

    NavigationBar(
        containerColor = bgColor,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.schedule),
                    contentDescription = "일정",
                    tint = if (selectedTab == 0) activeColor else inactiveColor
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = bgColor
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "홈",
                    tint = if (selectedTab == 1) activeColor else inactiveColor
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = bgColor
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.friend),
                    contentDescription = "친구",
                    tint = if (selectedTab == 2) activeColor else inactiveColor
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = bgColor
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.mypage),
                    contentDescription = "내 정보",
                    tint = if (selectedTab == 3) activeColor else inactiveColor
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = bgColor
            )
        )
    }
}

