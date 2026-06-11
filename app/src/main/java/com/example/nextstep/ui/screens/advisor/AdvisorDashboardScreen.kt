package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AdvisorDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    onChatClick: (String, String) -> Unit = { _, _ -> },
    onEditProfileClick: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    notificationsViewModel: AdvisorNotificationsViewModel = viewModel()
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(AdvisorTab.HOME)
    }
    
    val notificationsState by notificationsViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                AdvisorTab.HOME -> AdvisorHomeContent(
                    onViewAllStudentsClick = {
                        selectedTab = AdvisorTab.STUDENTS
                    },
                    onStudentClick = onStudentClick,
                    onNotificationsClick = onNotificationsClick,
                    unreadNotificationsCount = notificationsState.unreadCount
                )

                AdvisorTab.STUDENTS -> AdvisorStudentsContent(
                    onStudentClick = onStudentClick
                )

                AdvisorTab.TASKS -> AdvisorTasksContent(
                    onTaskClick = onStudentClick
                )

                AdvisorTab.MESSAGES -> AdvisorMessagesContent(
                    onChatClick = onChatClick
                )

                AdvisorTab.PROFILE -> {
                    AdvisorProfileScreen(
                        onEditProfileClick = onEditProfileClick,
                        onLogoutClick = onLogoutSuccess
                    )
                }
            }
        }

        AdvisorBottomBar(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
            }
        )
    }
}
