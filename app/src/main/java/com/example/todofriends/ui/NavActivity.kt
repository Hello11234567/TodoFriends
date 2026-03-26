/**
 * 로그인 후 진입하는 네비게이션 허브 액티비티
 * 바텀 네비게이션을 통해 홈, 일정, 친구, 내 정보 화면을 관리
 */

package com.example.todofriends.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todofriends.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

//네비게이션 경로 정의
sealed class Screen(val route: String) {
    object Schedule : Screen("schedule")
    object Home : Screen("home")
    object Friend : Screen("friend")
    object MyPage : Screen("mypage")
}

class NavActivity : ComponentActivity() {
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContent {
            NavScreen()
        }
    }
}

@Composable
fun NavScreen() {
    val navController = rememberNavController()
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val bgColor = Color(0xFF0F0F13)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Triple(Screen.Schedule, R.drawable.icon_schedule, "일정"),
        Triple(Screen.Home, R.drawable.icon_home, "홈"),
        Triple(Screen.Friend, R.drawable.icon_friend, "친구"),
        Triple(Screen.MyPage, R.drawable.icon_mypage, "내 정보")
    )

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            NavigationBar(
                containerColor = bgColor,
                tonalElevation = 0.dp
            ) {
                items.forEach { (screen, icon, label) ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == screen.route
                    } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = label,
                                tint = if (isSelected) Color.Unspecified
                                else Color(0xFFa07848).copy(alpha = 0.4f)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = bgColor
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .padding(paddingValues)
                .background(bgColor)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(scheduleViewModel = scheduleViewModel)
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen(viewModel = scheduleViewModel)
            }
            composable(Screen.Friend.route) {
               FriendScreen()
            }
            composable(Screen.MyPage.route) {
                MyPageScreen()
            }
        }
    }
}

@Composable
fun MyPageScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F13))
    )
}