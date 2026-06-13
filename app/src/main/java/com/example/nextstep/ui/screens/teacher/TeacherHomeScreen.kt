package com.example.nextstep.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.TeacherOrientationRequestDto
import com.example.nextstep.ui.utils.DateFormatUtils

@Composable
fun TeacherHomeScreen(
    teacherName: String = "",
    onNotificationsClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    onRequestClick: (String) -> Unit = {},
    onSeeAllRequestsClick: () -> Unit = {},
    onSeeAllStudentsClick: () -> Unit = {},
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

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        if (state.isLoading && state.recentRequests.isEmpty() && state.recentActivities.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
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
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TeacherSummaryCard(
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            value = state.pendingRequestsCount.toString(),
                            label = stringResource(R.string.teacher_pending_requests),
                            onClick = onSeeAllRequestsClick,
                            modifier = Modifier.weight(1f)
                        )
                        TeacherSummaryCard(
                            icon = Icons.Filled.Groups,
                            value = state.studentsFollowedCount.toString(),
                            label = stringResource(R.string.teacher_students_followed),
                            onClick = onSeeAllStudentsClick,
                            modifier = Modifier.weight(1f)
                        )
                        TeacherSummaryCard(
                            icon = Icons.Outlined.CheckCircle,
                            value = state.pendingEvaluationsCount.toString(),
                            label = stringResource(R.string.teacher_pending_evaluations),
                            onClick = onSeeAllStudentsClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Recent Requests Section
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    TeacherSectionHeader(
                        title = stringResource(R.string.teacher_recent_requests),
                        onSeeAllClick = onSeeAllRequestsClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.recentRequests.isEmpty()) {
                    item {
                        TeacherEmptyState(
                            text = stringResource(R.string.teacher_no_pending_requests),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    items(state.recentRequests) { request ->
                        RecentRequestItem(
                            request = request,
                            onClick = { onRequestClick(request.applicationId) }
                        )
                    }
                }

                // Recent Activities Section
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    TeacherSectionHeader(title = stringResource(R.string.teacher_recent_activities))
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.recentActivities.isEmpty()) {
                    item {
                        TeacherEmptyState(
                            text = stringResource(R.string.teacher_no_recent_activities),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    items(state.recentActivities) { activity ->
                        ActivityItem(activity = activity)
                    }
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
            .padding(start = 24.dp, end = 12.dp, top = 20.dp),
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
                color = Color.Gray
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
                            containerColor = Color(0xFFFDFA52),
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
                    modifier = Modifier.size(26.dp)
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 13.sp,
                maxLines = 2,
                minLines = 2
            )
        }
    }
}

@Composable
private fun RecentRequestItem(
    request: TeacherOrientationRequestDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDFA52)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = request.studentName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.studentName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = request.offerTitle,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ActivityItem(activity: TeacherActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        val (icon, color) = when(activity.type) {
            TeacherActivityType.NEW_REQUEST -> Icons.AutoMirrored.Filled.Assignment to Color(0xFF2196F3)
            TeacherActivityType.REQUEST_ACCEPTED -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
            TeacherActivityType.REQUEST_REJECTED -> Icons.Default.CheckCircle to Color(0xFFF44336)
            TeacherActivityType.NEW_MESSAGE -> Icons.AutoMirrored.Filled.Chat to Color(0xFFFF9800)
            TeacherActivityType.EVALUATION_SAVED -> Icons.Default.CheckCircle to Color(0xFF9C27B0)
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            activity.description?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = DateFormatUtils.formatDateTimeForUi(activity.timestamp),
                fontSize = 11.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun TeacherSectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        if (onSeeAllClick != null) {
            Text(
                text = stringResource(R.string.view_all),
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
    }
}

@Composable
private fun TeacherEmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = modifier
    )
}
