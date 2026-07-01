package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun AdminHomeScreen(
    onNavigateToCompanies: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onSeeAllActivities: () -> Unit = {},
    viewModel: AdminDashboardViewModel = viewModel()
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
                AdminHomeErrorContent(
                    message = state.errorMessage ?: "",
                    onRetry = { viewModel.loadDashboard() }
                )
            }

            else -> {
                AdminHomeContent(
                    state = state,
                    onNavigateToCompanies = onNavigateToCompanies,
                    onNavigateToUsers = onNavigateToUsers,
                    onSeeAllActivities = onSeeAllActivities
                )
            }
        }
    }
}

@Composable
private fun AdminHomeContent(
    state: AdminDashboardUiState,
    onNavigateToCompanies: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onSeeAllActivities: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            AdminHomeHeader(adminName = state.adminName)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.admin_management_panel),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF555555),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    icon = Icons.Filled.BusinessCenter,
                    value = state.activeInternshipsCount.toString(),
                    label = stringResource(R.string.active_internships_label),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCompanies
                )
                AdminStatCard(
                    icon = Icons.Filled.Description,
                    value = state.applicationsCount.toString(),
                    label = stringResource(R.string.applications_label),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToUsers
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    icon = Icons.Filled.Business,
                    value = state.totalCompaniesCount.toString(),
                    label = stringResource(R.string.total_companies_label),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCompanies
                )
                AdminStatCard(
                    icon = Icons.Filled.BusinessCenter,
                    value = state.activeCompaniesCount.toString(),
                    label = stringResource(R.string.active_companies_label),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCompanies
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    icon = Icons.Filled.WorkspacePremium,
                    value = state.publishedOffersCount.toString(),
                    label = stringResource(R.string.detail_published_offers),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCompanies
                )
                AdminStatCard(
                    icon = Icons.Filled.People,
                    value = state.usersCount.toString(),
                    label = stringResource(R.string.registered_users_label),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToUsers
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_activities_label),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = stringResource(R.string.view_all),
                    fontSize = 13.sp,
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onSeeAllActivities() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.recentActivitiesLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF777777), modifier = Modifier.size(24.dp))
                }
            }
        } else if (state.recentActivities.isNotEmpty()) {
            items(state.recentActivities) { activity ->
                AdminRecentActivityItem(activity = activity)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9F9F9))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_recent_activities_label),
                        fontSize = 14.sp,
                        color = Color(0xFF777777)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AdminHomeHeader(adminName: String) {
    val displayName = adminName.ifBlank { stringResource(R.string.role_admin) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.greeting_format, displayName),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun AdminStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEDEDED), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF9C4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF8D6E00),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = value,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF777777),
            maxLines = 2
        )
    }
}

@Composable
private fun AdminRecentActivityItem(activity: RecentActivityUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
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

@Composable
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

@Composable
private fun AdminHomeErrorContent(
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
                text = stringResource(R.string.retry_label),
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}
