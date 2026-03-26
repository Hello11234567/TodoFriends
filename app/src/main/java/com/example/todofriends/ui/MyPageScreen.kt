package com.example.todofriends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
fun MyPageScreen() {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)

    //더미 데이터
    val userName = "김유정"
    val userEmail = "yujeong@gmail.com"
    val userInitial = "유정"
    val completedCount = 42
    val friendCount = 4
    val teamCount = 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F0F13))
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E28))
                        .then(Modifier.clip(CircleShape)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInitial,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = userEmail,
                    color = Color(0xFFBBBBCC),
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF16161C))
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 완료한 일정
            StatItem(count = completedCount, label = "완료한 일정", accentColor = accentColor)

            // 구분선
            Box(
                modifier = Modifier
                    .width(0.5.dp)
                    .height(40.dp)
                    .background(Color(0xFF2A2A35))
            )

            // 친구 수
            StatItem(count = friendCount, label = "친구", accentColor = accentColor)

            // 구분선
            Box(
                modifier = Modifier
                    .width(0.5.dp)
                    .height(40.dp)
                    .background(Color(0xFF2A2A35))
            )

            // 팀 수
            StatItem(count = teamCount, label = "팀", accentColor = accentColor)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ────────────────────────────────────────
        // 메뉴 목록
        // ────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF16161C))
                .padding(horizontal = 4.dp)
        ) {
            MenuItemRow(label = "프로필 수정", onClick = { /* TODO: 프로필 수정 화면 */ })
            HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            MenuItemRow(label = "알림 설정", onClick = { /* TODO: 알림 설정 화면 */ })
            HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            MenuItemRow(label = "친구 관리", onClick = { /* TODO: 친구 관리 화면 */ })
            HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            MenuItemRow(label = "팀 관리", onClick = { /* TODO: 팀 관리 화면 */ })
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ────────────────────────────────────────
        // 로그아웃
        // ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF16161C))
                .clickable { /* TODO: 로그아웃 */ }
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "로그아웃",
                color = Color(0xFFEF5350),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StatItem(count: Int, label: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            color = accentColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = Color(0xFF888899),
            fontSize = 13.sp
        )
    }
}

@Composable
fun MenuItemRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 15.sp
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF444455),
            modifier = Modifier.size(20.dp)
        )
    }
}