package com.example.todofriends.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class ScheduleViewModel : ViewModel() {
    private val _scheduleMap = MutableStateFlow<Map<LocalDate, List<ScheduleItem>>>(emptyMap())
    val scheduleMap: StateFlow<Map<LocalDate, List<ScheduleItem>>> = _scheduleMap.asStateFlow()
    var personalInputs = mutableStateOf(listOf(ScheduleInput(id = 0)))
    var teamInputs = mutableStateOf(listOf(ScheduleInput(id = 0)))

    //일정 추가
    fun addSchedules(date: LocalDate, items: List<ScheduleItem>) {
        val current = _scheduleMap.value.toMutableMap()
        val existing = current[date]?.toMutableList() ?: mutableListOf()
        val nextId = (existing.maxOfOrNull { it.id } ?: 0) + 1
        val newItems = items.mapIndexed { i, item ->
            item.copy(id = nextId + i)
        }
        existing.addAll(newItems)
        current[date] = existing
        _scheduleMap.value = current
    }

    //일정 체크/언체크
    fun toggleSchedule(date: LocalDate, index: Int, item: ScheduleItem) {
        val current = _scheduleMap.value.toMutableMap()
        val list= current[date]?.toMutableList() ?: return
        list[index] = item.copy(isDone = !item.isDone)
        current[date] = list
        _scheduleMap.value = current
    }

    fun removeSchedule(date: LocalDate, scheduleId: Int) {
        val current = _scheduleMap.value.toMutableMap()
        val list = current[date]?.toMutableList() ?: return
        list.removeIf { it.id == scheduleId }
        current[date] = list
        _scheduleMap.value = current
    }

    fun toggleReaction(date: LocalDate, scheduleId: Int, emoji: String) {
        val current = _scheduleMap.value.toMutableMap()
        val list = current[date]?.toMutableList() ?: return
        val scheduleIndex = list.indexOfFirst { it.id == scheduleId }
        if (scheduleIndex == -1) return

        val schedule = list[scheduleIndex]
        val existingReaction = schedule.reactions.find { it.emoji == emoji }

        val newReactions = if (existingReaction != null) {
            // 이미 있는 이모지면 토글
            schedule.reactions.map {
                if (it.emoji == emoji) it.copy(
                    count = if (it.isSelected) it.count - 1 else it.count + 1,
                    isSelected = !it.isSelected
                ) else it
            }.filter { it.count > 0 || it.isSelected }
        } else {
            // 새 이모지 추가
            schedule.reactions + EmojiReaction(emoji = emoji, count = 1, isSelected = true)
        }

        list[scheduleIndex] = schedule.copy(reactions = newReactions)
        current[date] = list
        _scheduleMap.value = current
    }

    // 댓글 추가
    fun addComment(date: LocalDate, scheduleId: Int, text: String) {
        val current = _scheduleMap.value.toMutableMap()
        val list = current[date]?.toMutableList() ?: return
        val scheduleIndex = list.indexOfFirst { it.id == scheduleId }
        if (scheduleIndex == -1) return

        val schedule = list[scheduleIndex]
        val newComment = Comment(
            id = schedule.comments.size + 1,
            userName = "나",  // TODO: 로그인 유저 이름으로 교체
            text = text,
            time = "방금"
        )
        list[scheduleIndex] = schedule.copy(comments = schedule.comments + newComment)
        current[date] = list
        _scheduleMap.value = current
    }
}