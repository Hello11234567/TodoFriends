package com.example.todofriends.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import com.example.todofriends.BuildConfig

object OpenAiService {
    private val client = OkHttpClient()
    private const val API_URL = "https://api.openai.com/v1/chat/completions"

    suspend fun getPersonalScheduleRecommendation(
        schedules: List<Map<String, String>>,
        existingSchedules: List<Map<String, String>> = emptyList(),
        selectedDate: String = java.time.LocalDate.now().toString()
    ): List<Map<String, String>> {
        val prompt = buildPersonalPrompt(schedules, existingSchedules, selectedDate)

        val requestBody = JSONObject().apply {
            put("model", "gpt-4o-mini")
            put("temperature", 0.9)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "당신은 일정 관리 전문가입니다. 사용자의 일정을 분석하고 최적의 시간에 배치해주세요. 반드시 JSON 형식으로만 응답하세요.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("max_tokens", 1000)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return emptyList()
            parseResponse(responseBody)
        } catch (e:Exception) {
            emptyList()
        }
    }

    suspend fun getTeamScheduleRecommendation(
        schedules: List<Map<String, String>>,
        membersSchedules: List<Map<String, String>>,
        mySchedules: List<Map<String, String>>,
        dateRange: String,
        startDate: String,
        endDate: String
    ): List<Map<String, String>> {

        val prompt = buildTeamPrompt(
            schedules,
            membersSchedules,
            mySchedules,
            dateRange,
            startDate,
            endDate
        )

        val requestBody = JSONObject().apply {
            put("model", "gpt-4o-mini")
            put("temperature", 0.9)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "당신은 팀 일정 관리 전문가입니다. 팀원들의 일정을 분석하고 모두가 가능한 최적의 시간을 추천해주세요. 반드시 JSON 형식으로만 응답하세요.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("max_tokens", 1000)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return emptyList()
            parseResponse(responseBody)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun buildPersonalPrompt(
        schedules: List<Map<String, String>>,
        existingSchedules: List<Map<String, String>>,
        selectedDate: String
    ): String {
        val schedulesText = schedules.joinToString("\n") { schedule ->
            "- ${schedule["name"]} (소요시간: ${schedule["duration"]}분, 선호시간: ${schedule["preferredTime"]}, 우선순위: ${schedule["priority"]})"
        }

        val existingText = if (existingSchedules.isNotEmpty()) {
            "\n\n기존 일정:\n" + existingSchedules.joinToString("\n") {s ->
                "- ${s["title"]} (${s["startTime"]} - ${s["endTime"]})"
            }
        } else ""

        val currentTime = java.time.LocalTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        return """
            다음 일정들을 오늘 하루에 최적으로 배치해주세요.$existingText
            
            현재 시간: $currentTime
            
            배치할 일정:
            $schedulesText
            
            조건:
            1. 우선순위 높음은 선호 시간대 중 가장 좋은 시간에 배치
            2. 기존 일정과 겹치지 않게 배치
            3. 오전: 06:00-12:00, 오후: 12:00-18:00, 저녁: 18:00-22:00
            4. 반드시 선호 시간대 안에 배치할 것, 선호 시간대를 벗어나면 안됨
            5. 현재 시간 이후로만 추천(이미 지난 시간은 추천하지 말 것)
            5. 이전과 다른 새로운 시간 배치로 추천해주세요.
            
            반드시 아래 JSON 형식으로만 응답하세요:
            {
                "schedules": [
                    {"title": "일정이름", "startTime": "HH:mm", "endTime": "HH:mm"},
                    ...
                ]
            }
        """.trimIndent()
    }

    private fun buildTeamPrompt(
        schedules: List<Map<String, String>>,
        membersSchedules: List<Map<String, String>>,
        mySchedules: List<Map<String, String>>,
        dateRange: String,
        startDate: String,
        endDate: String
    ): String {
        val schedulesText = schedules.joinToString("\n") { schedule ->
            "- ${schedule["name"]} (소요시간: ${schedule["duration"]}분)"
        }

        val mySchedulesText = if (mySchedules.isNotEmpty()) {
            mySchedules.joinToString("\n") { s->
                "- ${s["date"]} ${s["startTime"]}-${s["endTime"]} ${s["title"]}"
            }
        } else "없음"

        //팀원별로 일정 그룹화
        val memberGroups = membersSchedules.groupBy { it["userName"] ?: "팀원" }
        val membersText = if (memberGroups.isNotEmpty()) {
            memberGroups.entries.joinToString("\n\n") { (name, scheduleList) ->
                "$name 일정:\n" + scheduleList.joinToString("\n") { s ->
                    "- ${s["date"]} ${s["startTime"]}-${s["endTime"]} ${s["title"]}"
                }
            }
        } else "팀원 일정 없음"

        val currentTime = java.time.LocalTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        val currentDate = java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy년 MM월 d일 (E)"))

        return """
            팀 일정을 최적으로 배치해주세요.
            
            오늘 날짜: $currentDate 
            현재 시간: $currentTime
            날짜 범위: $startDate - $endDate ($dateRange)
            
            내 일정:
            $mySchedulesText
            
            팀원들의 일정:
            $membersText
            
            배치할 팀 일정:
            $schedulesText
            
            조건:
            1. 모든 팀원과 나의 일정이 없는 날짜와 시간에 배치
            2. 업무 시간 내 배치 (9:00-22:00)
            3. 현재 시간 이후로만 추천(이미 지난 시간은 추천하지 말 것)
            4. 오늘 날짜 기준으로 정확한 날짜와 요일 계산할 것
            5. 이전과 다른 새로운 시간 배치로 추천해주세요.
            
            반드시 아래 JSON 형식으로만 응답하세요:
            {
            "schedules": [
                {
                    "title": "일정이름",
                    "date": "N월 N일 (요일)",
                    "startTime": "HH:mm",
                    "endTime": "HH:mm",
                    "description": "모든 팀원 가능"
                },
                ...
            ]
        }
        """.trimIndent()
    }

    private fun parseResponse(responseBody: String): List<Map<String, String>> {
        return try {
            val jsonResponse = JSONObject(responseBody)
            val content = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            val cleanContent = content
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val resultJson = JSONObject(cleanContent)
            val schedulesArray = resultJson.getJSONArray("schedules")

            val result = mutableListOf<Map<String, String>>()
            for (i in 0 until schedulesArray.length()) {
                val item = schedulesArray.getJSONObject(i)
                val map = mutableMapOf<String, String>()
                item.keys().forEach { key ->
                map[key] = item.getString(key)
                }
                result.add(map)
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }
}