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

//더미 완료 일정 (나중에 API 연동으로 교체)
val dummyCompletedSchedules = listOf(
    Pair("아침 운동", "3월 27일"),
    Pair("팀 미팅", "3월 26일"),
    Pair("공부하기", "3월 25일"),
    Pair("저녁 식사", "3월 24일"),
    Pair("운동", "3월 23일")
)

val dummyTeams = listOf("투두프렌즈팀", "스터디팀")
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

    //팝업 상태
    var selectedStat by remember { mutableStateOf<String?>(null) }

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

        //통계 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF16161C))
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 완료한 일정
            StatItem(
               count = completedCount,
                label = "완료한 일정",
                accentColor = accentColor,
                isSelected = selectedStat == "완료한 일정",
                onClick = {
                    selectedStat = if (selectedStat == "완료한 일정") null else "완료한 일정"
                }
            )

            // 구분선
            Box(
                modifier = Modifier
                    .width(0.5.dp)
                    .height(40.dp)
                    .background(Color(0xFF2A2A35))
            )

            // 친구 수
            StatItem(
                count = friendCount,
                label = "친구",
                accentColor = accentColor,
                isSelected = selectedStat == "친구",
                onClick = {
                    selectedStat = if (selectedStat == "친구") null else "친구"
                }
            )

            // 구분선
            Box(
                modifier = Modifier
                    .width(0.5.dp)
                    .height(40.dp)
                    .background(Color(0xFF2A2A35))
            )

            // 팀 수
            StatItem(
                count = teamCount,
                label = "팀",
                accentColor = accentColor,
                isSelected = selectedStat == "팀",
                onClick = {
                    selectedStat = if (selectedStat == "팀") null else "팀"
                }
            )
        }

        //통계 팝업 리스트 (선택시 펼쳐짐)
        if (selectedStat != null) {
            StatDetailPanel(
                statType = selectedStat!!,
                accentColor = accentColor
            )
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

//통계 상세 패널
@Composable
fun StatDetailPanel(
    statType: String,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A24))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(14.dp)
                        .background(accentColor, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statType,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        //내용
        when (statType) {
            "완료한 일정" -> {
                dummyCompletedSchedules.forEachIndexed { index, (title, date) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.6f))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = title, color = Color.White, fontSize = 13.sp)
                        }
                        Text(text = date, color = Color.White, fontSize = 13.sp)
                    }
                    if (index < dummyCompletedSchedules.size - 1) {
                        HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)
                    }
                }
            }
            "친구" -> {
                dummyFriends.forEachIndexed { index, friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = friend.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = friend.nickname, color = Color(0xFF555566), fontSize = 11.sp)
                        }
                        friend.team?.let {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = it, color = accentColor, fontSize = 10.sp)
                            }
                        }
                    }
                    if (index < dummyFriends.size - 1) {
                        HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)
                    }
                }
            }
            "팀" -> {
                dummyTeams.forEachIndexed { index, team ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.6f)),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = team, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    if (index < dummyTeams.size - 1) {
                        HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}
@Composable
fun StatItem(
    count: Int,
    label: String,
    accentColor: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = count.toString(),
            color = if (isSelected) Color.White else accentColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) accentColor else Color(0xFF888899),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )

        //선택 표시
        if(isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(accentColor, RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
fun MenuItemRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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