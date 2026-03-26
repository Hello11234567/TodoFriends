package com.example.todofriends.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Friend(
    val id: String,
    val name: String,
    val nickname: String,
    val team: String? = null,
    val initials: String,
    val color: Color
)

data class FriendRequest(
    val id: String,
    val name: String,
    val nickname: String,
    val initials: String,
    val color: Color
)

val dummyFriends = listOf(
    Friend("1", "홍길동", "@gildong", "투두프렌즈팀", "길동", Color(0xFF7B9EA6)),
    Friend("2", "이춘향", "@chunhyang", null, "춘향", Color(0xFFA67B9E)),
    Friend("3", "김유정", "@yujeong", "투두프렌즈팀", "유정", Color(0xFFA6957B)),
    Friend("4", "김다은", "@daeun", "스터디팀", "다은", Color(0xFF7BA67B))
)

val dummyReceivedRequests = listOf(
    FriendRequest("1", "박달이", "@moon", "달이", Color(0xFFA67B7B)),
    FriendRequest("2", "정우주", "@universe", "우주", Color(0xFF7B7BA6))
)

val dummySentRequests = listOf(
    FriendRequest("1", "양태양", "@taeyang", "태양", Color(0xFFA6A67B)),
    FriendRequest("2", "문별이", "@star", "별이", Color(0xFF7BA6A6))
)

val dummySearchResults = listOf(
    Friend("5", "양태양", "@taeyang", null, "태양", Color(0xFFA6A67B)),
    Friend("6", "문별이", "@star", null, "별이", Color(0xFF7BA6A6))
)

@Composable
fun FriendScreen() {
    val bgColor = Color(0xFF0F0F13)
    var isSearchMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        AnimatedContent(
            targetState = isSearchMode,
            transitionSpec =  {
                fadeIn() togetherWith fadeOut()
            }
        ) { searchMode ->
            if (searchMode) {
                FriendSearchScreen(onBack = { isSearchMode = false })
            } else {
                FriendMainScreen(onSearchClick = { isSearchMode = true})
            }
        }
    }
}

@Composable
fun FriendMainScreen(onSearchClick: () -> Unit) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)
    var selectedTab by remember { mutableStateOF(0) }
}