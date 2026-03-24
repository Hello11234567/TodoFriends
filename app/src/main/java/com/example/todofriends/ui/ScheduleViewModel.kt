package com.example.todofriends.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class ScheduleViewModel : ViewModel() {
    private val _scheduleMap = MutableStateFlow<Map<LocalDate, List<ScheduleItem>>>(emptyMap())
    val scheduleMap: StateFlow<Map<LocalDate, List<ScheduleItem>>> = _scheduleMap.asStateFlow()

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

    //날짜별 일정 조회
    fun getSchedules(date: LocalDate): List<ScheduleItem> {
        return _scheduleMap.value[date] ?: emptyList()
    }
}