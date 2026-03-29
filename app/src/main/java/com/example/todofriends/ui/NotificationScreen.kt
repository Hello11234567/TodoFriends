package com.example.todofriends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun NotificationSCreen (onBack: () -> Unit) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)

    var scheduleAlarm by remember { mutableStateOf(true) }
    var reactionAlarm by remember { mutableStateOf(true) }
    var friendRequestAlarm by remember { mutableStateOf(true) }
    var friendAcceptAlarm by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        //상단 헤더
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
            Spacer(modifier =  Modifier.width(12.dp))
            Text(
                text = "알림 설정",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            //알림 토글 목록
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF16161C))
            ) {
                NotificationToggleItem(
                    title = "일정 알림",
                    description = "일정 시작 전 알림을 받아요",
                    isEnabled = scheduleAlarm,
                    accentColor = accentColor,
                    isFirst = true,
                    onToggle = {
                        scheduleAlarm = !scheduleAlarm
                        // TODO: API 연동 - PATCH/api/user/notification
                    }
                )
                HorizontalDivider(
                    color = Color(0xFF2A2A35),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                NotificationToggleItem(
                    title = "친구 반응 알림",
                    description = "이모지/댓글 알림을 받아요",
                    isEnabled = reactionAlarm,
                    accentColor = accentColor,
                    onToggle = {
                        reactionAlarm = !reactionAlarm
                        // TODO: API 연동
                    }
                )
                HorizontalDivider(
                    color = Color(0xFF2A2A35),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                NotificationToggleItem(
                    title = "친구 신청 알림",
                    description = "새 친구 신청 알림을 받아요",
                    isEnabled = friendRequestAlarm,
                    accentColor = accentColor,
                    isLast = true,
                    onToggle = {
                        friendRequestAlarm = !friendRequestAlarm
                        // TODO: API 연동
                    }
                )
                HorizontalDivider(
                    color = Color(0xFF2A2A35),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                NotificationToggleItem(
                    title = "친구 수락 알림",
                    description = "친구 수락 알림을 받아요",
                    isEnabled = friendAcceptAlarm,
                    accentColor = accentColor,
                    isLast = true,
                    onToggle = {
                        friendAcceptAlarm = !friendAcceptAlarm
                        // TODO: API 연동
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    accentColor: Color,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = Color(0xFF555566),
                fontSize = 12.sp
            )
        }

        //토글 스위치
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (isEnabled) accentColor else Color(0xFF2A2A35))
                .clickable { onToggle() },
            contentAlignment = if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) Color(0xFF1C0E06) else Color(0xFF555566))
            )
        }
    }
}