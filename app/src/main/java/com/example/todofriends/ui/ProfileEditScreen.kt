package com.example.todofriends.ui

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todofriends.network.RetrofitClient
import com.example.todofriends.network.AppIdRequest
import kotlinx.coroutines.launch

@Composable
fun ProfileEditScreen(
    onBack: (() -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var appId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // 헤더 (onBack 있을 때만 뒤로가기 표시)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = if (onComplete != null) "아이디 설정" else "프로필 수정",
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

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2540)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (appId.isEmpty()) "?" else appId.take(2),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 앱 ID 입력
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "아이디",
                    color = Color(0xFF888899),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BasicTextField(
                    value = appId,
                    onValueChange = {
                        appId = it
                        errorMessage = ""
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
                        if (appId.isEmpty()) {
                            Text("친구 추가에 사용될 아이디", color = Color(0xFF555566), fontSize = 15.sp)
                        }
                        innerTextField()
                    }
                )
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF6B6B),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 저장 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLoading) Color(0xFF2A2A35) else accentColor)
                    .clickable {
                        if (isLoading || appId.isEmpty()) return@clickable
                        scope.launch {
                            isLoading = true
                            try {
                                val jwt = context
                                    .getSharedPreferences("auth", Context.MODE_PRIVATE)
                                    .getString("jwt", "") ?: ""
                                RetrofitClient.api.updateAppId(
                                    token = "Bearer $jwt",
                                    body = AppIdRequest(appId)
                                )
                                // SharedPreferences isRegistered 업데이트
                                context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("isRegistered", true)
                                    .apply()
                                onComplete?.invoke() ?: onBack?.invoke()
                            } catch (e: Exception) {
                                errorMessage = "이미 사용 중인 아이디예요."
                                isLoading = false
                            }
                        }
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLoading) "저장 중..." else "저장하기",
                    color = if (isLoading) accentColor else Color(0xFF1C0E06),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}