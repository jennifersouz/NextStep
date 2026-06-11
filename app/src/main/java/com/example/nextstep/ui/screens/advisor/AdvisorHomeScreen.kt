package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorActivityDto
import com.example.nextstep.data.model.AdvisorAssignedStudentDto
import com.example.nextstep.data.model.AdvisorSummaryDto

@Composable
fun AdvisorHomeScreen(
    onViewAllStudentsClick: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    viewModel: AdvisorHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessage != null -> {
                AdvisorHomeErrorContent(
                    message = state.errorMessage ?: "",
                    onRetry = { viewModel.loadHomeData() }
                )
            }

            else -> {
                AdvisorHomeContent(
                    state = state,
                    onViewAllStudentsClick = onViewAllStudentsClick,
                    onStudentClick = onStudentClick
                )
            }
        }
    }
}

@Composable
private fun AdvisorHomeContent(
    state: AdvisorHomeUiState,
    onViewAllStudentsClick: () -> Unit,
    onStudentClick: (String) -> Unit
) {
    val greeting = stringResource(R.string.advisor_home_greeting, state.advisorName)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        item {
            AdvisorHomeHeader(greeting = greeting)
        }

        // Summary section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            AdvisorSectionHeader(title = stringResource(R.string.summary))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdvisorSummaryCard(
                    icon = Icons.Filled.Groups,
                    value = state.summary.assignedStudentsCount.toString(),
                    label = stringResource(R.string.students_in_guidance),
                    modifier = Modifier.weight(1f)
                )

                AdvisorSummaryCard(
                    icon = Icons.Outlined.CheckCircle,
                    value = state.summary.activeInternshipsCount.toString(),
                    label = stringResource(R.string.active_internships),
                    modifier = Modifier.weight(1f)
                )

                AdvisorSummaryCard(
                    icon = Icons.Filled.WorkHistory,
                    value = state.summary.pendingTasksCount.toString(),
                    label = stringResource(R.string.pending_tasks),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // My students section
        item {
            Spacer(modifier = Modifier.height(32.dp))
            AdvisorSectionHeader(
                title = stringResource(R.string.my_students),
                actionText = stringResource(R.string.view_all),
                onActionClick = onViewAllStudentsClick
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Student preview cards (max 3)
        val previewStudents = state.students.take(3)
        if (previewStudents.isNotEmpty()) {
            items(previewStudents) { student ->
                AdvisorStudentPreviewCard(
                    student = student,
                    onClick = { onStudentClick(student.applicationId) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Add horizontal padding for cards
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }
        } else {
            item {
                AdvisorHomeEmptySection(
                    text = stringResource(R.string.no_assigned_students),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Recent activities section
        item {
            Spacer(modifier = Modifier.height(32.dp))
            AdvisorSectionHeader(title = stringResource(R.string.recent_activities))
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.recentActivities.isNotEmpty()) {
            items(state.recentActivities) { activity ->
                AdvisorActivityItem(activity = activity)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                AdvisorHomeEmptySection(
                    text = stringResource(R.string.no_recent_activities),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Bottom padding
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AdvisorHomeHeader(greeting: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = stringResource(R.string.advisor_role),
                fontSize = 14.sp,
                color = Color(0xFF777777)
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable { /* TODO: navigate to notifications */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = stringResource(R.string.notifications),
                tint = Color(0xFF333333),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AdvisorHomeErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                color = Color(0xFFB00020),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.try_again),
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}

@Composable
private fun AdvisorHomeEmptySection(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color(0xFF777777),
        modifier = modifier
    )
}