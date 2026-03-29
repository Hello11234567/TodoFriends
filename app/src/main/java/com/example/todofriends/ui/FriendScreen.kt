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
import androidx.compose.material.icons.filled.PlayArrow
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

// TODO: API 연동 시 삭제
// GET /api/friends → FriendViewModel.getFriends()로 교체
val dummyFriends = listOf(
    Friend("1", "홍길동", "@gildong", "투두프렌즈팀", "길동", Color(0xFF7B9EA6)),
    Friend("2", "이춘향", "@chunhyang", null, "춘향", Color(0xFFA67B9E)),
    Friend("3", "김유정", "@yujeong", "투두프렌즈팀", "유정", Color(0xFFA6957B)),
    Friend("4", "김다은", "@daeun", "스터디팀", "다은", Color(0xFF7BA67B))
)

// TODO: API 연동 시 삭제
// GET /api/friends/requests/received → FriendViewModel.getReceivedRequests()로 교체
val dummyReceivedRequests = listOf(
    FriendRequest("1", "박달이", "@moon", "달이", Color(0xFFA67B7B)),
    FriendRequest("2", "정우주", "@universe", "우주", Color(0xFF7B7BA6))
)

// TODO: API 연동 시 삭제
// GET /api/friends/requests/sent → FriendViewModel.getSentRequests()로 교체
val dummySentRequests = listOf(
    FriendRequest("1", "양태양", "@taeyang", "태양", Color(0xFFA6A67B)),
    FriendRequest("2", "문별이", "@star", "별이", Color(0xFF7BA6A6))
)

// TODO: API 연동 시 삭제
// GET /api/users/search?nickname={nickname} → FriendViewModel.searchUsers()로 교체
val dummySearchResults = listOf(
    Friend("5", "양태양", "@taeyang", null, "태양", Color(0xFFA6A67B)),
    Friend("6", "문별이", "@star", null, "별이", Color(0xFF7BA6A6))
)

@Composable
fun FriendScreen() {
    val bgColor = Color(0xFF0F0F13)
    var isSearchMode by remember { mutableStateOf(false) }
    var selectedFriend by remember { mutableStateOf<Friend?>(null) }  // ✅ 추가

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        when {
            selectedFriend != null -> {
                FriendScheduleScreen(
                    friend = selectedFriend!!,
                    onBack = { selectedFriend = null }
                )
            }
            isSearchMode -> {
                FriendSearchScreen(onBack = { isSearchMode = false })
            }
            else -> {
                FriendMainScreen(
                    onSearchClick = { isSearchMode = true },
                    onFriendClick = { selectedFriend = it }
                )
            }
        }
    }
}

@Composable
fun FriendMainScreen(
    onSearchClick: () -> Unit,
    onFriendClick: (Friend) -> Unit  // ✅ 추가
) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Text(
            text = "친구",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF16161C))
                .clickable { onSearchClick() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF555566),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "닉네임으로 검색", color = Color(0xFF555566), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("친구 목록", "친구 신청").forEachIndexed { index, label ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = index }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        color = if (selectedTab == index) accentColor else Color(0xFF555566),
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(2.dp)
                            .background(
                                if (selectedTab == index) accentColor else Color.Transparent,
                                RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)

        when (selectedTab) {
            0 -> FriendListTab(accentColor = accentColor, onFriendClick = onFriendClick)
            1 -> FriendRequestTab(accentColor = accentColor)
        }
    }
}

@Composable
fun FriendListTab(accentColor: Color, onFriendClick: (Friend) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // TODO: API 연동 시 dummyFriends → FriendViewModel.friends로 교체
        items(dummyFriends) { friend ->
            FriendListItem(
                friend = friend,
                accentColor = accentColor,
                onFriendClick = onFriendClick
            )
        }
    }
}

@Composable
fun FriendListItem(
    friend: Friend,
    accentColor: Color,
    onFriendClick: (Friend) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFriendClick(friend) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: API 연동 시 friend.profileImage로 교체
        // AsyncImage(model = friend.profileImage, ...)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(friend.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = friend.initials,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = friend.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                // TODO: API 연동 시 서버에서 받은 팀 정보로 교체
                friend.team?.let { team ->
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = team,
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = friend.nickname, color = Color(0xFF666677), fontSize = 13.sp)
        }

        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color(0xFF444455),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun FriendRequestTab(accentColor: Color) {
    // TODO: API 연동 시 ViewModel에서 받아오기
    // GET /api/friends/requests/received
    // GET /api/friends/requests/sent
    var receivedRequests by remember { mutableStateOf(dummyReceivedRequests) }
    var sentRequests by remember { mutableStateOf(dummySentRequests) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "받은 신청",
                color = Color(0xFF888899),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        if (receivedRequests.isEmpty()) {
            item {
                Text(
                    text = "받은 친구 신청이 없어요",
                    color = Color(0xFF555566),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        } else {
            items(receivedRequests) { request ->
                ReceivedRequestItem(
                    request = request,
                    accentColor = accentColor,
                    onAccept = {
                        receivedRequests = receivedRequests.filter { it.id != request.id }
                        // TODO: API 연동 - PATCH /api/friends/requests/{requestId}/accept
                        // FriendViewModel.acceptRequest(request.id) 호출
                    },
                    onReject = {
                        receivedRequests = receivedRequests.filter { it.id != request.id }
                        // TODO: API 연동 - DELETE /api/friends/requests/{requestId}
                        // FriendViewModel.rejectRequest(request.id) 호출
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "보낸 신청",
                color = Color(0xFF888899),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        if (sentRequests.isEmpty()) {
            item {
                Text(
                    text = "보낸 친구 신청이 없어요",
                    color = Color(0xFF555566),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        } else {
            items(sentRequests) { request ->
                SentRequestItem(
                    request = request,
                    onCancel = {
                        sentRequests = sentRequests.filter { it.id != request.id }
                        // TODO: API 연동 - DELETE /api/friends/requests/{requestId}
                        // FriendViewModel.cancelRequest(request.id) 호출
                    }
                )
            }
        }
    }
}

@Composable
fun ReceivedRequestItem(
    request: FriendRequest,
    accentColor: Color,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: API 연동 시 request.profileImage로 교체
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(request.color),
            contentAlignment = Alignment.Center
        ) {
            Text(text = request.initials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = request.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = request.nickname, color = Color(0xFF666677), fontSize = 13.sp)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor)
                .clickable { onAccept() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("수락", color = Color(0xFF1C0E06), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2A2A35))
                .clickable { onReject() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("거절", color = Color(0xFF888899), fontSize = 13.sp)
        }
    }
}

@Composable
fun SentRequestItem(
    request: FriendRequest,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: API 연동 시 request.profileImage로 교체
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(request.color),
            contentAlignment = Alignment.Center
        ) {
            Text(text = request.initials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = request.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = request.nickname, color = Color(0xFF666677), fontSize = 13.sp)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2A2A35))
                .clickable { onCancel() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("취소", color = Color(0xFF888899), fontSize = 13.sp)
        }
    }
}

@Composable
fun FriendSearchScreen(onBack: () -> Unit) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)
    var searchQuery by remember { mutableStateOf("") }
    var recentSearches by remember { mutableStateOf(listOf("홍길동", "이춘향")) }
    var searchResults by remember { mutableStateOf<List<Friend>>(emptyList()) }
    var hasSearched by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
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

            BasicTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotBlank()) {
                        // TODO: API 연동 - GET /api/users/search?nickname={it}
                        // FriendViewModel.searchUsers(it) 호출 후 결과로 교체
                        searchResults = dummySearchResults.filter { friend ->
                            friend.name.contains(it) || friend.nickname.contains(it)
                        }
                        hasSearched = true
                    } else {
                        searchResults = emptyList()
                        hasSearched = false
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF16161C))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                cursorBrush = SolidColor(accentColor),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text("닉네임으로 검색", color = Color(0xFF555566), fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )

            if (searchQuery.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "검색어 지우기",
                    tint = Color(0xFF555566),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            searchQuery = ""
                            searchResults = emptyList()
                            hasSearched = false
                        }
                )
            }
        }

        HorizontalDivider(color = Color(0xFF2A2A35), thickness = 0.5.dp)

        if (!hasSearched) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("최근 검색어", color = Color(0xFF888899), fontSize = 13.sp)
                    Text(
                        "전체삭제",
                        color = Color(0xFF555566),
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { recentSearches = emptyList() }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                recentSearches.forEach { query ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = query
                                // TODO: API 연동 - GET /api/users/search?nickname={query}
                                searchResults = dummySearchResults.filter { friend ->
                                    friend.name.contains(query) || friend.nickname.contains(query)
                                }
                                hasSearched = true
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF555566),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = query,
                            color = Color(0xFF888899),
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = Color(0xFF555566),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    recentSearches = recentSearches.filter { it != query }
                                }
                        )
                    }
                }
            }
        } else {
            Text(
                text = "검색 결과",
                color = Color(0xFF888899),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "검색 결과가 없습니다",
                        color = Color(0xFF555566),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn {
                    items(searchResults) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // TODO: API 연동 시 friend.profileImage로 교체
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(friend.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = friend.initials,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(friend.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                Text(friend.nickname, color = Color(0xFF666677), fontSize = 13.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(accentColor)
                                    .clickable {
                                        // TODO: API 연동 - POST /api/friends/request/{userId}
                                        // FriendViewModel.sendFriendRequest(friend.id) 호출
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("신청", color = Color(0xFF1C0E06), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}