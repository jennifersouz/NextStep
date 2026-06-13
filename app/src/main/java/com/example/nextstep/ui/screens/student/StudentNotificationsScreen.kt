package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.StudentNotificationItem
import java.time.Duration
import java.time.OffsetDateTime

@Composable
fun StudentNotificationsScreen(
    onNotificationClick: (type: String, applicationId: String) -> Unit = { _, _ -> },
    onUnreadCountChanged: (Int) -> Unit = {},
    viewModel: StudentNotificationsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(state.unreadCount) {
        onUnreadCountChanged(state.unreadCount)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadNotifications()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }

        state.errorMessageRes != null -> {
            val errorRes = state.errorMessageRes

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (errorRes != null) {
                    Text(
                        text = stringResource(errorRes),
                        color = Color(0xFFB00020),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        state.notifications.isEmpty() -> {
            StudentNotificationsEmptyState()
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 30.dp,
                    bottom = 28.dp
                )
            ) {
                item {
                    Text(
                        text = stringResource(R.string.notifications),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(34.dp))
                }

                items(
                    items = state.notifications,
                    key = { item ->
                        "${item.id}_${item.type}"
                    }
                ) { item ->
                    StudentNotificationItemRow(
                        notification = item,
                        onClick = {
                            viewModel.markAsSeen(
                                notification = item,
                                onLocalStateChanged = onUnreadCountChanged,
                                onSuccess = {
                                    item.applicationId?.let { appId ->
                                        onNotificationClick(item.type, appId)
                                    }
                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(26.dp))
                }
            }
        }
    }
}

@Composable
fun StudentNotificationItemRow(
    notification: StudentNotificationItem,
    onClick: () -> Unit
) {
    when (notification) {
        is StudentNotificationItem.ViewBased -> {
            ViewBasedNotificationItem(
                notification = notification.notification,
                onClick = onClick
            )
        }
        is StudentNotificationItem.TableBased -> {
            TableBasedNotificationItem(
                notification = notification.notification,
                onClick = onClick
            )
        }
    }
}

@Composable
fun ViewBasedNotificationItem(
    notification: com.example.nextstep.data.model.StudentNotificationDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (notification.isUnread) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8505B))
            )
        } else {
            Spacer(modifier = Modifier.width(7.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        StudentNotificationCompanyLogo(
            companyName = notification.companyName.orEmpty().ifBlank { "?" }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.companyName.orEmpty().ifBlank {
                        notification.offerTitle.orEmpty()
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = relativeNotificationTime(notification.sortDate),
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = studentNotificationMessage(notification),
                fontSize = 16.sp,
                color = Color(0xFF8A8A8A)
            )
        }
    }
}

@Composable
fun TableBasedNotificationItem(
    notification: com.example.nextstep.data.model.NotificationDto,
    onClick: () -> Unit
) {
    val icon = when (notification.type) {
        "message" -> Icons.AutoMirrored.Filled.Chat
        "evaluation" -> Icons.Default.Star
        "teacher_assigned" -> Icons.Default.Person
        else -> Icons.Default.Notifications
    }

    val iconBgColor = when (notification.type) {
        "message" -> Color(0xFFFFF8E1)
        "evaluation" -> Color(0xFFE3F2FD)
        "teacher_assigned" -> Color(0xFFE8F5E9)
        else -> Color(0xFFF4F4F4)
    }

    val iconColor = when (notification.type) {
        "message" -> Color(0xFFF57F17)
        "evaluation" -> Color(0xFF1976D2)
        "teacher_assigned" -> Color(0xFF388E3C)
        else -> Color(0xFF8A8A8A)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8505B))
            )
        } else {
            Spacer(modifier = Modifier.width(7.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = relativeNotificationTime(notification.createdAt.orEmpty()),
                    fontSize = 14.sp,
                    color = Color(0xFF8A8A8A)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notification.message,
                fontSize = 16.sp,
                color = Color(0xFF8A8A8A)
            )
        }
    }
}

@Composable
fun StudentNotificationCompanyLogo(
    companyName: String
) {
    val initials = companyName
        .split(" ")
        .filter { part ->
            part.isNotBlank()
        }
        .take(2)
        .joinToString("") { part ->
            part.first().uppercase()
        }
        .ifBlank {
            "?"
        }

    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xFFE8392A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StudentNotificationsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.student_no_notifications),
            color = Color(0xFF8A8A8A),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun studentNotificationMessage(
    notification: com.example.nextstep.data.model.StudentNotificationDto
): String {
    return when (notification.type) {
        "advisor_assigned" -> stringResource(R.string.advisor_assigned_notification_message)
        else -> when (notification.status) {
            "accepted" -> stringResource(R.string.notification_application_accepted)
            "rejected" -> stringResource(R.string.notification_application_rejected)
            else -> stringResource(R.string.notification_application_updated)
        }
    }
}

fun relativeNotificationTime(
    rawDate: String?
): String {
    if (rawDate.isNullOrBlank()) return ""

    return try {
        val date = OffsetDateTime.parse(rawDate)
        val now = OffsetDateTime.now()
        val duration = Duration.between(date, now)

        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()

        when {
            minutes < 1 -> "agora"
            minutes < 60 -> "${minutes}m"
            hours < 24 -> "${hours}h"
            else -> "${days}d"
        }
    } catch (_: Exception) {
        ""
    }
}
