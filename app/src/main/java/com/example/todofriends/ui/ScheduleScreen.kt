package com.example.todofriends.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate


data class ScheduleInput(
    val id: Int,
    val name: String = "",
    val startTime: String = "",
    val duration: String = ""
)

data class RecommendedSchedule(
    val time: String,
    val title: String,
    val description: String = ""
)

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {
    val bgColor = Color(0xFF0F0F13)
    val accentColor = Color(0xFFBF9B72)
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        //탭 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            listOf("개인", "팀").forEachIndexed { index, label ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = index }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = label,
                        color = if (selectedTab == index ) accentColor else Color(0xFF555566),
                        fontSize = 18.sp,
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
            0 -> PersonalScheduleTab(accentColor = accentColor, viewModel = viewModel)
            1 -> TeamScheduleTab(accentColor = accentColor, viewModel = viewModel)
        }
    }
}

// ────────────────────────────────────────
// 개인 탭
// ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScheduleTab(accentColor: Color, viewModel: ScheduleViewModel) {
    var inputs by viewModel.personalInputs
    var showRecommend by remember { mutableStateOf(false) }
    val today = LocalDate.now()

    //TimePicker 상태
    var showTimePicker by remember { mutableStateOf(false) }
    var currentInputIndex by remember { mutableStateOf(0) }
    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = true
    )
    // 더미 추천 데이터 (나중에 AI API로 교체)
    val recommendedList = listOf(
        RecommendedSchedule("11:00", "목욕하기"),
        RecommendedSchedule("13:00", "친구와 점심 식사"),
        RecommendedSchedule("15:00", "공부하기"),
        RecommendedSchedule("18:00", "운동")
    )

    //TimePicker 다이얼로그
    if(showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = Color(0xFF16161C),
            title = {
                Text("시작 시간 선택", color = Color.White, fontSize = 16.sp)
            },
            text = {
                TimeInput(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        timeSelectorSelectedContainerColor = Color(0xFFBF9B72),
                        timeSelectorUnselectedContainerColor = Color(0xFF0F0F13),
                        timeSelectorSelectedContentColor = Color(0xFF1C0E06),
                        timeSelectorUnselectedContentColor = Color.White,
                        containerColor = Color(0xFF0F0F13),
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val time = String.format(
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    inputs = inputs.toMutableList().also { l ->
                        l[currentInputIndex] = inputs[currentInputIndex].copy(startTime = time)
                    }
                    showTimePicker = false
                }) {
                    Text("확인", color = Color(0xFFBF9B72))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("취소", color = Color(0xFF888899))
                }
            }
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // 일정 입력 섹션
        item {
            Text("일정 입력", color = Color(0xFF888899), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF16161C))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                inputs.forEachIndexed { index, input ->
                    PersonalInputRow(
                        input = input,
                        accentColor = accentColor,
                        onNameChange = {
                            inputs = inputs.toMutableList().also { l ->
                                l[index] = input.copy(name = it)
                            }
                        },
                        onStartTimeChange = {
                            inputs = inputs.toMutableList().also { l ->
                                l[index] = input.copy(startTime = it)
                            }
                        },
                        onDurationChange = {
                            inputs = inputs.toMutableList().also { l ->
                                l[index] = input.copy(duration = it)
                            }
                        },
                        onDelete = {
                            if (inputs.size > 1) {
                                inputs = inputs.toMutableList().also { l ->
                                    l.removeAt(index)
                                }
                            }
                        },
                        onTimeClick = {
                            currentInputIndex = index
                            showTimePicker = true
                        }
                    )
                }

                // + 일정 추가
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { inputs = inputs + ScheduleInput(id = inputs.size) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("일정 추가", color = accentColor, fontSize = 14.sp)
                }
            }
        }

        // 추천 일정 섹션
        if (showRecommend) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Column {
                        Text("추천 일정", color = Color(0xFF888899), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1C1C14))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            recommendedList.forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.time,
                                        color = accentColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(52.dp)
                                    )
                                    Column {
                                        Text(
                                            text = item.title,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.6f)
                                                .height(3.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(accentColor.copy(alpha = 0.4f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 추천 화면 버튼
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val items = recommendedList.mapIndexed { i, r ->
                                ScheduleItem(
                                    id = i,
                                    title = r.title,
                                    time = r.time,
                                    endTime = calculateEndTime(r.time, "60")
                                )
                            }
                            viewModel.addSchedules(today, items)
                            showRecommend = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "일정에 추가할게😊",
                            color = Color(0xFF1C0E06),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = { /* TODO: AI API 재호출 */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                        border = BorderStroke(1.dp, accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("다시 추천해줘🙁", color = accentColor)
                    }
                }
            }
        } else {
            // 기본 버튼
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val items = inputs
                                .filter { it.name.isNotBlank() }
                                .mapIndexed { i, input ->
                                    ScheduleItem(
                                        id = i,
                                        title = input.name,
                                        time = input.startTime,
                                        endTime = calculateEndTime(input.startTime, input.duration)
                                    )
                                }
                            viewModel.addSchedules(today, items)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "일정 추가할래!",
                            color = Color(0xFF1C0E06),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = { showRecommend = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                        border = BorderStroke(1.dp, accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("추천 받을래!", color = accentColor)
                    }
                }
            }
        }
    }
}

// 개인 일정 입력 행
@Composable
fun PersonalInputRow(
    input: ScheduleInput,
    accentColor: Color,
    onNameChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onDelete: () -> Unit,
    onTimeClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ScheduleTextField(
            value = input.name,
            onValueChange = onNameChange,
            placeholder = "일정 이름",
            modifier = Modifier.weight(2f)
        )

        //시간 피커
        Box(
            modifier = Modifier
                .weight(1.5f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F0F13))
                .clickable { onTimeClick() }
                .padding(horizontal = 10.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = if (input.startTime.isEmpty()) "시작 시간" else input.startTime,
                color = if (input.startTime.isEmpty()) Color(0xFF555566) else Color.White,
                fontSize = 13.sp
            )
        }

        ScheduleTextField(
            value = input.duration,
            onValueChange = onDurationChange,
            placeholder = "분",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "삭제",
            tint = Color(0xFFEF5350),
            modifier = Modifier
                .size(20.dp)
                .clickable { onDelete() }
        )
    }
}

// ────────────────────────────────────────
// 팀 탭
// ────────────────────────────────────────
@Composable
fun TeamScheduleTab(accentColor: Color, viewModel: ScheduleViewModel) {
    var inputs by viewModel.teamInputs
    var showRecommend by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf("투두프렌즈팀") }
    var expanded by remember { mutableStateOf(false) }
    val today = LocalDate.now()

    val teams = listOf("투두프렌즈팀", "스터디팀", "사이드프로젝트팀")

    val recommendedList = listOf(
        RecommendedSchedule("14:00", "팀 미팅", "팀원 모두 가능"),
        RecommendedSchedule("17:00", "코드 리뷰", "팀원 모두 가능")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // 팀 선택 드롭다운
        item {
            Text("팀 선택", color = Color(0xFF888899), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16161C))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedTeam, color = Color.White, fontSize = 15.sp)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = accentColor
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF16161C))
                ) {
                    teams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(team, color = Color.White) },
                            onClick = {
                                selectedTeam = team
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        if (showRecommend) {
            // 팀 추천 결과 (입력 화면 없이 추천만)
            item {
                Text("추천 일정", color = Color(0xFF888899), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1C1C14))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    recommendedList.forEach { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF222218))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.time} - ${addHour(item.time)}",
                                    color = accentColor,
                                    fontSize = 13.sp
                                )
                            }
                            if (item.description.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.description,
                                    color = Color(0xFF888899),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val items = recommendedList.mapIndexed { i, r ->
                                ScheduleItem(id = i,
                                    title = r.title,
                                    time = r.time,
                                    endTime = calculateEndTime(r.time, "60"))
                            }
                            viewModel.addSchedules(today, items)
                            showRecommend = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "일정에 추가할게😊",
                            color = Color(0xFF1C0E06),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = { /* TODO: AI API 재호출 */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                        border = BorderStroke(1.dp, accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("다시 추천해줘🙁", color = accentColor)
                    }
                }
            }
        } else {
            // 팀 일정 입력
            item {
                Text("팀 일정 입력", color = Color(0xFF888899), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF16161C))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    inputs.forEachIndexed { index, input ->
                        TeamInputRow(
                            input = input,
                            accentColor = accentColor,
                            onNameChange = {
                                inputs = inputs.toMutableList().also { l ->
                                    l[index] = input.copy(name = it)
                                }
                            },
                            onDurationChange = {
                                inputs = inputs.toMutableList().also { l ->
                                    l[index] = input.copy(duration = it)
                                }
                            },
                            onDelete = {
                                if (inputs.size > 1) {
                                    inputs = inputs.toMutableList().also { l ->
                                        l.removeAt(index)
                                    }
                                }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { inputs = inputs + ScheduleInput(id = inputs.size) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("일정 추가", color = accentColor, fontSize = 14.sp)
                    }
                }
            }

            item {
                Button(
                    onClick = { showRecommend = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "최적의 일정 추천 받올래!",
                        color = Color(0xFF1C0E06),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// 팀 일정 입력 행
@Composable
fun TeamInputRow(
    input: ScheduleInput,
    accentColor: Color,
    onNameChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ScheduleTextField(
            value = input.name,
            onValueChange = onNameChange,
            placeholder = "일정 이름",
            modifier = Modifier.weight(2f)
        )
        ScheduleTextField(
            value = input.duration,
            onValueChange = onDurationChange,
            placeholder = "소요 시간",
            modifier = Modifier.weight(1.5f),
            keyboardType = KeyboardType.Number
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "삭제",
            tint = Color(0xFFEF5350),
            modifier = Modifier
                .size(20.dp)
                .clickable { onDelete() }
        )
    }
}

// 공통 텍스트 필드
@Composable
fun ScheduleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F0F13))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 13.sp
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        cursorBrush = SolidColor(Color(0xFFBF9B72)),
        singleLine = true,
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(placeholder, color = Color(0xFF555566), fontSize = 13.sp)
            }
            innerTextField()
        }
    )
}

// 시간 +1시간 헬퍼
fun addHour(time: String): String {
    return try {
        val parts = time.split(":")
        val hour = parts[0].toInt() + 1
        "${hour.toString().padStart(2, '0')}:${parts[1]}"
    } catch (e: Exception) {
        time
    }
}