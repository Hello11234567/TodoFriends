package com.example.todofriends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun ProfileEditScreen(onBack: () -> Unit) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)

    // TODO: API 연동 시 서버에서 현재 닉네임 불러오기
    // GET /api/user/me → nickname으로 초기값 설정
    var nickname by remember { mutableStateOf("김유정") }
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        //헤더
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
                text = "프로필 수정",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // TODO: API 연동 시 프로필 이미지 업로드 기능 추가
            // 갤러리에서 선택 → Multipart로 서버 업로드
            // PATCH /api/user/profile/image
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2540)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nickname.take(2),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            //닉네임 입력
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "닉네임",
                    color = Color(0xFF888899),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BasicTextField(
                    value = nickname,
                    onValueChange = {
                        nickname = it
                        isSaved = false // 수정하면 저장 상태 초기화
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16161C))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp
                    ),
                    cursorBrush = SolidColor(accentColor),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if(nickname.isEmpty()) {
                            Text("닉네임 입력", color = Color(0xFF555566), fontSize = 15.sp)
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //이메일 (수정 불가)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "이메일",
                    color = Color(0xFF888899),
                    fontSize = 13.sp,
                    modifier= Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16161C))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // TODO: API 연동 시 서버에서 받아온 이메일로 교체
                    Text(
                        text = "yujeong@gmail.com",
                        color = Color(0xFF555566),
                        fontSize = 15.sp
                    )
                }
                Text(
                    text = "이메일은 수정할 수 없어요",
                    color = Color(0xFF444455),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            //저장 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSaved) Color(0xFF2A2A35) else accentColor)
                    .clickable {
                        isSaved = true
                        // TODO: API 연동 - PATCH /api/user/profile
                        // body: { nickname: nickname }
                        // 성공 시 isSaved = true, 실패 시 에러 메시지 표시
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSaved) "저장됐어요 ✓" else "저장하기",
                    color = if (isSaved) accentColor else Color(0xFF1C0E06),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}