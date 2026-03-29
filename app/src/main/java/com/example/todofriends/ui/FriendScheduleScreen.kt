package com.example.todofriends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth

// 더미 친구 일정 데이터 (나중에 API 연동으로 교체)
fun getDummyFriendSchedule(date: LocalDate): List<ScheduleItem> {
    return when (date.dayOfMonth) {
        27 -> listOf(
            ScheduleItem(id = 1, title = "아침 운동", time = "07:00", endTime = "08:00"),
            ScheduleItem(id = 2, title = "팀 미팅", time = "14:00", endTime = "15:00"),
            ScheduleItem(id = 3, title = "저녁 식사", time = "18:00", endTime = "19:00")
        )
        else -> emptyList()
    }
}

@Composable
fun FriendScheduleScreen(
    friend: Friend,  // ✅ 오타 수정
    onBack: () -> Unit
) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var schedules by remember(selectedDate) {  // ✅ val → var
        mutableStateOf(getDummyFriendSchedule(selectedDate))
    }
    var selectedSchedule by remember { mutableStateOf<ScheduleItem?>(null) }
    var showTeamInviteDialog by remember { mutableStateOf(false) }
    var selectedTeamForInvite by remember { mutableStateOf<String?>(null) }

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val today = remember { LocalDate.now() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    Box(modifier = Modifier.fillMaxSize()) {
        //팀 초대 다이얼로그
        if (showTeamInviteDialog) {
            AlertDialog(
                onDismissRequest = { showTeamInviteDialog = false },
                containerColor = Color(0xFF1A1A24),
                title = {
                    Text(
                        text = "${friend.name}을 팀에 초대",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dummyTeams.forEach { team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedTeamForInvite == team)
                                            accentColor.copy(alpha = 0.2f)
                                        else Color(0xFF0F0F13)
                                    )
                                    .clickable { selectedTeamForInvite = team }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selectedTeamForInvite == team) accentColor
                                            else Color(0xFF555566)
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = team,
                                    color = if (selectedTeamForInvite == team) accentColor else Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showTeamInviteDialog = false
                            selectedTeamForInvite = null
                            // TODO: API 연동 - POST /api/teams/{teamId}/invite/{friendId}
                        }
                    ) {
                        Text(
                            "초대하기",
                            color = if (selectedTeamForInvite != null) accentColor else Color(0xFF555566),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTeamInviteDialog = false
                        selectedTeamForInvite = null
                    }) {
                        Text("취소", color = Color(0xFF888899))
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {
            // 상단 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(friend.color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.initials,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = friend.nickname,
                        color = Color(0xFF888899),
                        fontSize = 12.sp
                    )
                }

                //팀 초대 버튼
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .clickable { showTeamInviteDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "팀 초대",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)

            // ✅ LazyColumn을 Row 밖으로
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // 캘린더
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "${state.firstVisibleMonth.yearMonth.year}년 ${state.firstVisibleMonth.yearMonth.monthValue}월",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->  // ✅ it → day
                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    color = when (day) {
                                        "일" -> Color(0xFFFF6B6B).copy(alpha = 0.7f)
                                        "토" -> Color(0xFF6B9FFF).copy(alpha = 0.7f)
                                        else -> Color(0xFF666677)
                                    },
                                    fontSize = 12.sp
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

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> accentColor
                                                isToday -> Color(0xFF4A3828)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { selectedDate = day.date },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.date.dayOfMonth.toString(),
                                        color = when {
                                            isSelected -> Color(0xFF1C0E06)
                                            isToday -> Color(0xFFE8C9A0)
                                            !isCurrentMonth -> Color(0xFF444455)
                                            day.date.dayOfWeek.value == 7 -> Color(0xFFFF6B6B)
                                            day.date.dayOfWeek.value == 6 -> Color(0xFF6B9FFF)
                                            else -> Color.White
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                    }
                }

                item {
                    HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(16.dp)
                                .background(accentColor, shape = RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 ${friend.name}의 일정",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (schedules.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "이 날은 일정이 없어요 🗓️",
                                color = Color(0xFF555566),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                items(schedules) { schedule ->
                    FriendScheduleItemCard(
                        schedule = schedule,
                        accentColor = accentColor,
                        onTap = { selectedSchedule = schedule }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // 바텀시트 팝업
        selectedSchedule?.let { schedule ->
            FriendScheduleBottomSheet(
                schedule = schedules.find { it.id == schedule.id } ?: schedule,
                accentColor = accentColor,
                onDismiss = { selectedSchedule = null },
                onReactionToggle = { emoji ->
                    schedules = schedules.map { s ->
                        if (s.id == schedule.id) {
                            val existing = s.reactions.find { it.emoji == emoji }
                            val newReactions = if (existing != null) {
                                s.reactions.map {
                                    if (it.emoji == emoji) it.copy(
                                        count = if (it.isSelected) it.count - 1 else it.count + 1,
                                        isSelected = !it.isSelected
                                    ) else it
                                }.filter { it.count > 0 || it.isSelected }
                            } else {
                                s.reactions + EmojiReaction(emoji, 1, true)
                            }
                            s.copy(reactions = newReactions)
                        } else s
                    }
                    selectedSchedule = schedules.find { it.id == schedule.id }
                },
                onCommentAdd = { text ->
                    schedules = schedules.map { s ->
                        if (s.id == schedule.id) {
                            s.copy(
                                comments = s.comments + Comment(
                                    id = s.comments.size + 1,
                                    userName = "나",
                                    text = text,
                                    time = "방금"
                                )
                            )
                        } else s
                    }
                    selectedSchedule = schedules.find { it.id == schedule.id }
                }
            )
        }
    }
}

@Composable
fun FriendScheduleItemCard(
    schedule: ScheduleItem,
    accentColor: Color,
    onTap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF16161C))
            .clickable { onTap() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = schedule.title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (schedule.endTime.isNotEmpty()) "${schedule.time} - ${schedule.endTime}"
                else schedule.time,
                color = accentColor,
                fontSize = 13.sp
            )
        }

        if (schedule.reactions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                schedule.reactions.filter { it.count > 0 }.forEach { reaction ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2A2518))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${reaction.emoji} ${reaction.count}",
                            fontSize = 12.sp,
                            color = accentColor
                        )
                    }
                }
                if (schedule.comments.isNotEmpty()) {
                    Text(
                        text = "댓글 ${schedule.comments.size}",
                        color = Color(0xFF555566),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun FriendScheduleBottomSheet(
    schedule: ScheduleItem,
    accentColor: Color,
    onDismiss: () -> Unit,
    onReactionToggle: (String) -> Unit,
    onCommentAdd: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var showEmojiPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable { onDismiss() }
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF1A1A24))
                .clickable { }
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF444455))
            )

            Spacer(modifier = Modifier.height(16.dp))

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

                // ✅ 이모지 그리드 픽커로 교체
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0F0F13))
                        .clickable { showEmojiPicker = !showEmojiPicker }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (showEmojiPicker) "닫기" else "+ 이모지",
                        fontSize = 13.sp,
                        color = Color(0xFF555566)
                    )
                }
            }

            if (showEmojiPicker) {
                Spacer(modifier = Modifier.height(10.dp))
                EmojiPickerSection(
                    accentColor = accentColor,
                    onEmojiSelected = { emoji ->
                        onReactionToggle(emoji)
                        showEmojiPicker = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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