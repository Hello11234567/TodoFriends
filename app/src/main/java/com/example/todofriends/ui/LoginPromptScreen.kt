package com.example.todofriends.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todofriends.R

@Composable
fun LoginPromptScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val accentColor = Color(0xFFBF9B72)
    val bgColor = Color(0xFF0F0F13)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF1A1A24))
                .border(1.dp, Color(0xFF2D2D3D), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.splash_logo1),
                contentDescription = "로고",
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        //타이틀
        Text(
            text = "투두프렌즈",
            color = accentColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        //설명
        Text(
            text = "로그인 후 이용할 수 있어요\n카카오 계정으로 간편하게 시작하세요",
            color = Color(0xFF888899),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        //로그인 버튼
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFFEE500))
                .clickable {
                    //LoginActivity로 이동
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
                .padding(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_kakao),
                    contentDescription = null,
                    tint = Color(0xFF191919),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "카카오로 시작하기",
                    color = Color(0xFF3A1D1D),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 나중에 하기
        Text(
            text = "나중에 할게요",
            color = Color(0xFF555566),
            fontSize = 13.sp,
            modifier = Modifier.clickable { onBack() }
        )
    }
}