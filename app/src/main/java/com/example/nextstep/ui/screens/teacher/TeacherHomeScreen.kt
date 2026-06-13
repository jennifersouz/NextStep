package com.example.nextstep.ui.screens.teacher

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun TeacherHomeScreen(
    teacherName: String = "",
    onNotificationsClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    viewModel: TeacherHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val displayName = teacherName.ifBlank { stringResource(R.string.teacher_role) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadDashboard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        if (state.isLoading && state.pendingRequestsCount == 0 && state.studentsFollowedCount == 0) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                item {
                    TeacherHomeHeader(
                        name = displayName,
                        unreadCount = unreadNotificationsCount,
                        onNotificationsClick = onNotificationsClick
                    )
                }

                // Summary Cards
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TeacherSummaryCard(
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            value = state.pendingRequestsCount.toString(),
                            label = stringResource(R.string.teacher_pending_requests),
                            modifier = Modifier.weight(1f)
                        )
                        TeacherSummaryCard(
                            icon = Icons.Filled.Groups,
                            value = state.studentsFollowedCount.toString(),
                            label = stringResource(R.string.teacher_students_followed),
                            modifier = Modifier.weight(1f)
                        )
                        TeacherSummaryCard(
                            icon = Icons.Outlined.CheckCircle,
                            value = state.pendingEvaluationsCount.toString(),
                            label = stringResource(R.string.teacher_pending_evaluations),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Recent Requests Section
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    TeacherSectionHeader(title = stringResource(R.string.teacher_recent_requests))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TeacherEmptyState(
                        text = stringResource(R.string.teacher_no_pending_requests),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                // Recent Activities Section
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    TeacherSectionHeader(title = stringResource(R.string.teacher_recent_activities))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TeacherEmptyState(
                        text = stringResource(R.string.teacher_no_recent_activities),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TeacherHomeHeader(
    name: String,
    unreadCount: Int,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 8.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.teacher_home_greeting, name),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(R.string.teacher_role),
                fontSize = 14.sp,
                color = Color(0xFF777777)
            )
        }

        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier.size(48.dp)
        ) {
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(
                            containerColor = TeacherUiColors.YellowAccent,
                            contentColor = Color.Black
                        ) {
                            Text(text = if (unreadCount > 9) "9+" else unreadCount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notifications),
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun TeacherSummaryCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF777777),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun TeacherSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = modifier.padding(horizontal = 24.dp)
    )
}

@Composable
private fun TeacherEmptyState(
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
