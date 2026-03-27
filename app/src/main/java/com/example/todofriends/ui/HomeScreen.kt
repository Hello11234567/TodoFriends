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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth

data class ScheduleItem(
    val id: Int,
    val title: String,
    val time: String,
    val endTime: String = "",
    val isDone: Boolean = false,
    val reactions: List<EmojiReaction> = emptyList(),
    val comments: List<Comment> = emptyList()
)

data class EmojiReaction(
    val emoji: String,
    val count: Int,
    val isSelected: Boolean = false
)

data class Comment(
    val id: Int,
    val userName: String,
    val text: String,
    val time: String
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

@Composable
fun HomeScreen(scheduleViewModel: ScheduleViewModel) {
    val bgColor = Color(0xFF0F0F13)
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val showSchedule = selectedDate != null
    val scheduleMap by scheduleViewModel.scheduleMap.collectAsState()

    // ✅ selectedSchedule을 HomeScreen 레벨로
    var selectedSchedule by remember { mutableStateOf<ScheduleItem?>(null) }

    val calendarWeight by animateFloatAsState(
        targetValue = if (showSchedule) 0.52f else 1f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "calendarWeight"
    )

    // ✅ Box로 감싸서 바텀시트를 전체화면 위에 띄우기
    Box(modifier = Modifier.fillMaxSize()) {
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
                        val schedules = scheduleMap[date] ?: emptyList()
                        ScheduleSection(
                            selectedDate = date,
                            schedules = schedules,
                            onCheckedChange = { index, item ->
                                scheduleViewModel.toggleSchedule(date, index, item)
                            },
                            onDelete = { index, item ->
                                scheduleViewModel.removeSchedule(date, item.id)
                            },
                            onReactionToggle = { scheduleId, emoji ->
                                scheduleViewModel.toggleReaction(date, scheduleId, emoji)
                            },
                            onCommentAdd = { scheduleId, text ->
                                scheduleViewModel.addComment(date, scheduleId, text)
                            },
                            onScheduleTap = { selectedSchedule = it },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // ✅ 바텀시트 전체화면 위에 표시
        selectedSchedule?.let { schedule ->
            val date = selectedDate ?: return@let
            val schedules = scheduleMap[date] ?: emptyList()
            ScheduleDetailBottomSheet(
                schedule = schedules.find { it.id == schedule.id } ?: schedule,
                accentColor = Color(0xFFBF9B72),
                onDismiss = { selectedSchedule = null },
                onReactionToggle = { emoji ->
                    scheduleViewModel.toggleReaction(date, schedule.id, emoji)
                },
                onCommentAdd = { text ->
                    scheduleViewModel.addComment(date, schedule.id, text)
                }
            )
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

@Composable
fun ScheduleSection(
    selectedDate: LocalDate,
    schedules: List<ScheduleItem>,
    onCheckedChange: (Int, ScheduleItem) -> Unit,
    onDelete: (Int, ScheduleItem) -> Unit,
    onReactionToggle: (Int, String) -> Unit,
    onCommentAdd: (Int, String) -> Unit,
    onScheduleTap: (ScheduleItem) -> Unit,
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
                            onDelete = { onDelete(index, item) },
                            onTap = { onScheduleTap(item) }
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
    onDelete: () -> Unit,
    onTap: () -> Unit
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16161C))
                    .clickable { onTap() }
                    .padding(vertical = 4.dp)
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

                    Column(modifier = Modifier.weight(1f)) {
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

                        // 이모지 미리보기
                        if (item.reactions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                item.reactions.filter { it.count > 0 }.forEach { reaction ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xFF2A2518))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${reaction.emoji} ${reaction.count}",
                                            fontSize = 11.sp,
                                            color = Color(0xFFBF9B72)
                                        )
                                    }
                                }
                                if (item.comments.isNotEmpty()) {
                                    Text(
                                        text = "댓글 ${item.comments.size}",
                                        color = Color(0xFF555566),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ScheduleDetailBottomSheet(
    schedule: ScheduleItem,
    accentColor: Color,
    onDismiss: () -> Unit,
    onReactionToggle: (String) -> Unit,
    onCommentAdd: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var showEmojiInput by remember { mutableStateOf(false) }
    var emojiInputText by remember { mutableStateOf("") }
    val emojiFocusRequester = remember { FocusRequester() }

    // 딤 배경
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable { onDismiss() }
    )

    // 바텀시트
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF1A1A24))
                .clickable { }
                .padding(20.dp)
        ) {
            // 핸들
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF444455))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 제목 + 시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = schedule.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (schedule.time.isNotEmpty()) {
                    Text(
                        text = if (schedule.endTime.isNotEmpty()) "${schedule.time} - ${schedule.endTime}"
                        else schedule.time,
                        color = accentColor,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 이모지 반응
            Text(text = "반응", color = Color(0xFF888899), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                schedule.reactions.forEach { reaction ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (reaction.isSelected) accentColor.copy(alpha = 0.2f)
                                else Color(0xFF0F0F13)
                            )
                            .border(
                                1.dp,
                                if (reaction.isSelected) accentColor else Color.Transparent,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onReactionToggle(reaction.emoji) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${reaction.emoji} ${reaction.count}",
                            fontSize = 14.sp,
                            color = if (reaction.isSelected) accentColor else Color.White
                        )
                    }
                }

                // + 이모지 버튼
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0F0F13))
                        .clickable { showEmojiInput = !showEmojiInput }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "+ 이모지", fontSize = 13.sp, color = Color(0xFF555566))
                }
            }

            // 이모지 입력창
            if (showEmojiInput) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = emojiInputText,
                        onValueChange = {
                            if (it.isNotEmpty()) {
                                onReactionToggle(it)
                                emojiInputText = ""
                                showEmojiInput = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F0F13))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .focusRequester(emojiFocusRequester),
                        textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                        cursorBrush = SolidColor(accentColor),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        decorationBox = { innerTextField ->
                            if (emojiInputText.isEmpty()) {
                                Text("이모지 입력...", color = Color(0xFF555566), fontSize = 13.sp)
                            }
                            innerTextField()
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "취소",
                        color = Color(0xFF555566),
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            showEmojiInput = false
                            emojiInputText = ""
                        }
                    )
                }
                LaunchedEffect(Unit) {
                    emojiFocusRequester.requestFocus()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 댓글
            if (schedule.comments.isNotEmpty()) {
                Text(text = "댓글", color = Color(0xFF888899), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    schedule.comments.forEach { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = comment.userName.take(1),
                                    color = accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = comment.userName,
                                        color = accentColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = comment.time,
                                        color = Color(0xFF555566),
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = comment.text,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 댓글 입력창
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0F0F13))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                    cursorBrush = SolidColor(accentColor),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (commentText.isEmpty()) {
                            Text("댓글 달기...", color = Color(0xFF555566), fontSize = 13.sp)
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (commentText.isNotEmpty()) accentColor else Color(0xFF2A2A35))
                        .clickable {
                            if (commentText.isNotEmpty()) {
                                onCommentAdd(commentText)
                                commentText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "전송",
                        tint = if (commentText.isNotEmpty()) Color(0xFF1C0E06) else Color(0xFF555566),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}