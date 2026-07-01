package com.example.nextstep.ui.screens.admin

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminActivitiesScreen(
    onBackClick: () -> Unit = {},
    viewModel: AdminActivitiesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.activities),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.errorMessage ?: "",
                                color = Color(0xFFB00020),
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.retry_label),
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                }

                state.activities.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F9F9))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_activities),
                            fontSize = 14.sp,
                            color = Color(0xFF777777)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        items(state.activities) { activity ->
                            AdminActivitiesItem(activity = activity)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminActivitiesItem(activity: RecentActivityUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(activityIconBackground(activity.type)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = activityIcon(activity.type),
                contentDescription = null,
                tint = activityIconTint(activity.type),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(activity.type.titleRes(), activity.name),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            val subtitle = buildString {
                append(activity.email)
                val formattedTime = activity.createdAt?.let { formatActivityTimestamp(it) }
                if (!formattedTime.isNullOrBlank()) {
                    append(" · ")
                    append(formattedTime)
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun activityIcon(type: RecentActivityType): ImageVector = when (type) {
    RecentActivityType.STUDENT_CREATED -> Icons.Filled.PersonAdd
    RecentActivityType.TEACHER_CREATED -> Icons.Filled.Star
    RecentActivityType.ADVISOR_CREATED -> Icons.Filled.WorkHistory
    RecentActivityType.COMPANY_CREATED -> Icons.Filled.Business
    RecentActivityType.INSTITUTION_CREATED -> Icons.Filled.School
}

private fun activityIconBackground(type: RecentActivityType): Color = when (type) {
    RecentActivityType.STUDENT_CREATED -> Color(0xFFE3F2FD)
    RecentActivityType.TEACHER_CREATED -> Color(0xFFF3E5F5)
    RecentActivityType.ADVISOR_CREATED -> Color(0xFFFFF3E0)
    RecentActivityType.COMPANY_CREATED -> Color(0xFFE8F5E9)
    RecentActivityType.INSTITUTION_CREATED -> Color(0xFFFFFCE4)
}

private fun activityIconTint(type: RecentActivityType): Color = when (type) {
    RecentActivityType.STUDENT_CREATED -> Color(0xFF1565C0)
    RecentActivityType.TEACHER_CREATED -> Color(0xFF7B1FA2)
    RecentActivityType.ADVISOR_CREATED -> Color(0xFFE65100)
    RecentActivityType.COMPANY_CREATED -> Color(0xFF2E7D32)
    RecentActivityType.INSTITUTION_CREATED -> Color(0xFFF9A825)
}

private fun formatActivityTimestamp(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val now = Instant.now()
        val diffMinutes = ChronoUnit.MINUTES.between(instant, now)
        val diffHours = ChronoUnit.HOURS.between(instant, now)
        val diffDays = ChronoUnit.DAYS.between(instant, now)

        when {
            diffMinutes < 1 -> "agora"
            diffMinutes < 60 -> "${diffMinutes}m"
            diffHours < 24 -> "${diffHours}h"
            diffDays < 7 -> "${diffDays}d"
            else -> {
                val zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                zdt.format(formatter)
            }
        }
    } catch (e: Exception) {
        isoString
    }
}
