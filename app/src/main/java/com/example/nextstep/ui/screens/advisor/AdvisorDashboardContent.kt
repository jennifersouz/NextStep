package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Groups

@Composable
fun AdvisorHomeContent(
    onViewAllStudentsClick: () -> Unit = {},
    onStudentClick: (String) -> Unit = {}
) {
    AdvisorHomeScreen(
        onViewAllStudentsClick = onViewAllStudentsClick,
        onStudentClick = onStudentClick
    )
}

@Composable
fun AdvisorStudentsContent(
    onStudentClick: (String) -> Unit = {}
) {
    AdvisorStudentsScreen(onStudentClick = onStudentClick)
}

@Composable
fun AdvisorTasksContent(
    onTaskClick: (String) -> Unit = {}
) {
    AdvisorTasksScreen(
        onTaskClick = { task -> onTaskClick(task.applicationId) }
    )
}

@Composable
fun AdvisorMessagesContent(
    onChatClick: (String) -> Unit = {}
) {
    AdvisorMessagesScreen(onChatClick = onChatClick)
}

@Composable
fun AdvisorProfileContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.profile),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.advisor_profile_placeholder),
            fontSize = 15.sp,
            color = Color(0xFF777777)
        )
    }
}

@Composable
fun AdvisorBottomBar(
    selectedTab: AdvisorTab,
    onTabSelected: (AdvisorTab) -> Unit
) {
    NextStepBottomBar(
        items = listOf(
            BottomBarItem(
                route = AdvisorTab.HOME.name,
                icon = Icons.Filled.Home,
                label = stringResource(R.string.home)
            ),
            BottomBarItem(
                route = AdvisorTab.STUDENTS.name,
                icon = Icons.Filled.Groups,
                label = stringResource(R.string.students)
            ),
            BottomBarItem(
                route = AdvisorTab.TASKS.name,
                icon = Icons.Filled.Assignment,
                label = stringResource(R.string.tasks)
            ),
            BottomBarItem(
                route = AdvisorTab.MESSAGES.name,
                icon = Icons.AutoMirrored.Filled.Chat,
                label = stringResource(R.string.messages)
            ),
            BottomBarItem(
                route = AdvisorTab.PROFILE.name,
                icon = Icons.Filled.Person,
                label = stringResource(R.string.profile)
            )
        ),
        selectedItem = selectedTab.name,
        onItemClick = { tabName ->
            onTabSelected(AdvisorTab.valueOf(tabName))
        }
    )
}
