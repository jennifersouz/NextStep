package com.example.nextstep.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import com.example.nextstep.R
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel
import com.example.nextstep.data.repository.TeacherProfileRepository

@Composable
fun TeacherDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onRequestClick: (String) -> Unit = {},
    onChatClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    sessionViewModel: SessionViewModel = viewModel(),
    notificationsViewModel: TeacherNotificationsViewModel = viewModel()
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(TeacherTab.HOME)
    }

    val notificationsState by notificationsViewModel.uiState.collectAsState()
    
    var teacherName by remember { mutableStateOf("") }
    val repo = remember { TeacherProfileRepository() }
    
    LaunchedEffect(Unit) {
        repo.getTeacherProfile().onSuccess { profile ->
            teacherName = profile.displayName
        }
    }

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
                TeacherTab.HOME -> TeacherHomeScreen(
                    teacherName = teacherName,
                    onNotificationsClick = onNotificationsClick,
                    unreadNotificationsCount = notificationsState.unreadCount
                )

                TeacherTab.REQUESTS -> TeacherRequestsScreen(
                    onRequestClick = onRequestClick
                )

                TeacherTab.STUDENTS -> TeacherStudentsPlaceholder()
                TeacherTab.MESSAGES -> TeacherMessagesScreen(
                    onChatClick = onChatClick
                )
                TeacherTab.PROFILE -> TeacherProfileScreen(
                    onEditProfileClick = onEditProfileClick,
                    onLogoutClick = {
                        sessionViewModel.logout(onSuccess = onLogoutSuccess)
                    }
                )
            }
        }

        TeacherBottomBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}

@Composable
private fun TeacherStudentsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize()) {
        // TODO: Implement TeacherStudentsScreen
    }
}

@Composable
fun TeacherBottomBar(
    selectedTab: TeacherTab,
    onTabSelected: (TeacherTab) -> Unit
) {
    NextStepBottomBar(
        items = listOf(
            BottomBarItem(
                route = TeacherTab.HOME.name,
                icon = Icons.Filled.Home,
                label = stringResource(R.string.home)
            ),
            BottomBarItem(
                route = TeacherTab.REQUESTS.name,
                icon = Icons.AutoMirrored.Filled.Assignment,
                label = stringResource(R.string.requests)
            ),
            BottomBarItem(
                route = TeacherTab.STUDENTS.name,
                icon = Icons.Filled.Groups,
                label = stringResource(R.string.students)
            ),
            BottomBarItem(
                route = TeacherTab.MESSAGES.name,
                icon = Icons.AutoMirrored.Filled.Chat,
                label = stringResource(R.string.messages)
            ),
            BottomBarItem(
                route = TeacherTab.PROFILE.name,
                icon = Icons.Filled.Person,
                label = stringResource(R.string.profile)
            )
        ),
        selectedItem = selectedTab.name,
        onItemClick = { tabName ->
            onTabSelected(TeacherTab.valueOf(tabName))
        }
    )
}