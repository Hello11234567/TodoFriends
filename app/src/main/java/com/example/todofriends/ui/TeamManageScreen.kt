package com.example.todofriends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//팀 데이터 클래스
data class Team(
    val id: String,
    val name: String,
    val members: List<Friend>
)

//더미 팀 데이터 (나중에 API 연동으로 교체)
val dummyTeamList = listOf(
    Team(
        id = "1",
        name = "투두프렌즈팀",
        members = listOf(
            Friend("1", "홍길동", "@gildong", "투두프렌즈팀", "길동", Color(0xFF7B9EA6)),
            Friend("3", "김유정", "@yujeong", "투두프렌즈팀", "유정", Color(0xFFA6957B))
        )
    ),
    Team(
        id = "2",
        name = "스터디팀",
        members = listOf(
            Friend("4", "김다은", "@daeun", "스터디팀", "다은", Color(0xFF7BA67B))
        )
    )
)
@Composable
fun TeamManageScreen(onBack: () -> Unit) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)

    var teams by remember { mutableStateOf(dummyTeamList) }
    var showLeaveDialog by remember { mutableStateOf<Team?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTeamName by remember { mutableStateOf("") }
    var expandedTeamId by remember { mutableStateOf<String?>(null) }

    //팀 나가기 다이얼로그
    showLeaveDialog?.let { team ->
        AlertDialog(
            onDismissRequest = { showLeaveDialog = null },
            containerColor = Color(0xFF1A1A24),
            title = {
                Text(
                    text = "팀 나가기",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "${team.name}에서 나갈까요?",
                    color = Color(0xFF888899),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        teams = teams.filter { it.id != team.id }
                        showLeaveDialog = null
                        // TODO: API 연동 - DELETE /api/teams/{teamId}/leave
                    }
                ) {
                    Text("나가기", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = null }) {
                    Text("취소", color = Color(0xFF888899))
                }
            }
        )
    }

    //팀 만들기 다이얼로그
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newTeamName = ""
            },
            containerColor = Color(0xFF1A1A24),
            title = {
                Text(
                    text = "팀 만들기",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                BasicTextField(
                    value = newTeamName,
                    onValueChange = { newTeamName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F0F13))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                    cursorBrush = SolidColor(accentColor),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (newTeamName.isEmpty()) {
                            Text("팀 이름 입력", color = Color(0xFF555566), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        innerTextField()
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTeamName.isNotBlank()) {
                            val newTeam = Team(
                                id = (teams.size + 1).toString(),
                                name = newTeamName,
                                members = emptyList()
                            )
                            teams = teams + newTeam
                            showCreateDialog = false
                            newTeamName = ""
                            // TODO: API 연동 - POST /api/teams
                        }
                    }
                ) {
                    Text("만들기", color = accentColor, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    newTeamName = ""
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
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "팀 관리",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            //팀 만들기 버튼
            Box(
               modifier = Modifier
                   .clip(RoundedCornerShape(8.dp))
                   .background(accentColor)
                   .clickable { showCreateDialog = true }
                   .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF1C0E06),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "팀 만들기",
                        color = Color(0xFF1C0E06),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)

        if(teams.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "소속된 팀이 없어요",
                    color = Color(0xFF555566),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(teams) { team ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF16161C))
                    ) {
                        //팀 헤더
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedTeamId =
                                        if (expandedTeamId == team.id) null else team.id
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //팀 아이콘
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(accentColor.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = team.name.take(1),
                                    color = accentColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = team.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "팀원 ${team.members.size}명",
                                    color = Color(0xFF666677),
                                    fontSize = 12.sp
                                )
                            }

                            //나가기 버튼
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF2A2A35))
                                    .clickable { showLeaveDialog = team }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "나가기",
                                    color = Color(0xFFEF5350),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        //팀원 목록 (펼쳐지면 표시)
                        if (expandedTeamId == team.id && team.members.isNotEmpty()) {
                            HorizontalDivider(
                                color = Color(0xFF2A2A35),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            team.members.forEachIndexed { index, member ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(member.color),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = member.initials,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = member.name,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = member.nickname,
                                            color = Color(0xFF666677),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                if (index < team.members.size - 1) {
                                    HorizontalDivider(
                                        color = Color(0xFF2A2A35),
                                        thickness = 0.5.dp,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}