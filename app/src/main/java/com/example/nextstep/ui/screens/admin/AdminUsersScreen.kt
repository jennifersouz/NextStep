package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.ui.utils.roleToDisplayName

@Composable
fun AdminUsersScreen(
    viewModel: AdminUsersViewModel = viewModel(),
    onUserClick: (AdminProfileDto) -> Unit = {},
    onAddUserClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Refresh when screen becomes visible/resumed (useful when returning from creation screen)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadUsers()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddUserClick,
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_user))
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // Header
            Text(
                text = stringResource(R.string.users_label),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Search bar
            AdminSearchBar(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = stringResource(R.string.search_user),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter chips
            AdminUsersFilterChips(
                selectedFilter = state.selectedFilter,
                onFilterClick = { viewModel.onFilterChange(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            when {
                state.isLoading && state.users.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }

                state.errorMessage != null && state.users.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.errorMessage ?: "",
                            color = Color(0xFFB00020),
                            fontSize = 15.sp
                        )
                    }
                }

                state.filteredUsers.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_users_found_label),
                            fontSize = 15.sp,
                            color = Color(0xFF777777)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.filteredUsers) { profile ->
                            AdminUserListItem(
                                profile = profile,
                                onClick = { onUserClick(profile) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(96.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsersFilterChips(
    selectedFilter: AdminUsersFilter,
    onFilterClick: (AdminUsersFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AdminUsersFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFFFDFA52) else Color(0xFFF5F5F5))
                    .clickable { onFilterClick(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(filter.labelRes),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AdminUserListItem(
    profile: AdminProfileDto,
    onClick: () -> Unit
) {
    val displayName = listOfNotNull(profile.firstName, profile.lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { profile.email ?: profile.id }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with initials
        val initials = displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "?" }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF2B2B2B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = profile.email ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFF777777),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role badge
                Text(
                    text = roleToDisplayName(profile.role),
                    fontSize = 12.sp,
                    color = Color(0xFF555555),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Status badge: Archived > Active > Inactive
                val statusLabel: String
                val statusColor: Color
                val statusBg: Color
                if (profile.isArchived) {
                    statusLabel = stringResource(R.string.archived_status)
                    statusColor = Color(0xFF6D4C41)
                    statusBg = Color(0xFFEFEBE9)
                } else if (profile.isActive == true) {
                    statusLabel = stringResource(R.string.active_status_label)
                    statusColor = Color(0xFF2E7D32)
                    statusBg = Color(0xFFE8F5E9)
                } else {
                    statusLabel = stringResource(R.string.inactive_status_label)
                    statusColor = Color(0xFFC62828)
                    statusBg = Color(0xFFFFEBEE)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(R.string.details_label),
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(20.dp)
        )
    }
}