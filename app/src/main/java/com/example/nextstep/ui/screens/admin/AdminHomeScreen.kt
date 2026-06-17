package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.ui.utils.Formatters

@Composable
fun AdminHomeScreen(
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
                AdminHomeContent(state = state)
            }
        }
    }
}

@Composable
private fun AdminHomeContent(
    state: AdminDashboardUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        item {
            AdminHomeHeader(adminName = state.adminName)
        }

        // Statistics cards
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

            // Row 1 of cards
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
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    icon = Icons.Filled.Description,
                    value = state.applicationsCount.toString(),
                    label = stringResource(R.string.applications_label),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2 of cards - Company stats
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
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    icon = Icons.Filled.BusinessCenter,
                    value = state.activeCompaniesCount.toString(),
                    label = stringResource(R.string.active_companies_label),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 3 of cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    icon = Icons.Filled.CheckCircle,
                    value = state.completedEvaluationsCount.toString(),
                    label = stringResource(R.string.completed_evaluations_label),
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    icon = Icons.Filled.People,
                    value = state.usersCount.toString(),
                    label = stringResource(R.string.registered_users_label),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent activities section
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.recent_activities_label),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.recentProfiles.isNotEmpty()) {
            items(state.recentProfiles) { profile ->
                AdminRecentProfileItem(profile = profile)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.no_recent_activities_label),
                    fontSize = 14.sp,
                    color = Color(0xFF777777),
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
private fun AdminHomeHeader(adminName: String) {
    val displayName = adminName.ifBlank { stringResource(R.string.role_admin) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.greeting_format, displayName),
            fontSize = 24.sp,
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEDEDED), RoundedCornerShape(12.dp))
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
private fun AdminRecentProfileItem(profile: ProfileDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF777777))
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = profile.email,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "${stringResource(R.string.function_label)}: ${Formatters.formatRole(profile.role)}",
                fontSize = 12.sp,
                color = Color(0xFF777777)
            )
        }
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