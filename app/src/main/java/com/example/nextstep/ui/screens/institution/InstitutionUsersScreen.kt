package com.example.nextstep.ui.screens.institution

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.InstitutionUserDto
import com.example.nextstep.ui.screens.admin.AdminSearchBar
import com.example.nextstep.ui.screens.admin.AppFilterDropdown
import com.example.nextstep.ui.screens.admin.UserStatusFilter
import com.example.nextstep.ui.screens.admin.UserTypeFilter
import com.example.nextstep.ui.screens.admin.labelRes
import com.example.nextstep.ui.utils.isInstitutionArchived
import com.example.nextstep.ui.utils.isInviteAccepted

@Composable
fun InstitutionUsersScreen(
    onAddUserClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    showBackButton: Boolean = false,
    refreshTrigger: Int = 0,
    onUserClick: (profileId: String?, role: String) -> Unit = { _, _ -> },
    viewModel: InstitutionUsersViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedInviteToDelete by remember { mutableStateOf<InstitutionUserDto?>(null) }

    LaunchedEffect(refreshTrigger) {
        viewModel.loadUsers()
    }

    InstitutionUsersContent(
        state = state,
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        onAddUserClick = onAddUserClick,
        onTypeFilterChange = viewModel::onTypeFilterChange,
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onSearchChange = viewModel::updateSearchQuery,
        onDeleteInvite = { user -> selectedInviteToDelete = user },
        onUserClick = onUserClick
    )

    if (selectedInviteToDelete != null) {
        AlertDialog(
            onDismissRequest = { selectedInviteToDelete = null },
            title = {
                Text(text = stringResource(R.string.delete_invite_title))
            },
            text = {
                Text(text = stringResource(R.string.delete_invite_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteInvite(selectedInviteToDelete!!)
                        selectedInviteToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = Color(0xFFB00020)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedInviteToDelete = null }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
internal fun InstitutionUsersContent(
    state: InstitutionUsersUiState,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onAddUserClick: () -> Unit,
    onTypeFilterChange: (UserTypeFilter) -> Unit,
    onStatusFilterChange: (UserStatusFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onDeleteInvite: (InstitutionUserDto) -> Unit = {},
    onUserClick: (profileId: String?, role: String) -> Unit = { _, _ -> }
) {
    val sortedUsers = state.filteredUsers.sortedWith(
        compareBy<InstitutionUserDto> { it.isInviteAccepted() }
            .thenByDescending { it.createdAt.orEmpty() }
    )

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton && onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }

                Text(
                    text = stringResource(R.string.manage_users),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onAddUserClick,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDFA52),
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_user),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            AdminSearchBar(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                placeholder = stringResource(R.string.search_users),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppFilterDropdown(
                    label = stringResource(R.string.user_type_filter_label),
                    selectedOption = stringResource(state.selectedTypeFilter.labelRes()),
                    options = listOf(UserTypeFilter.ALL, UserTypeFilter.STUDENTS, UserTypeFilter.TEACHERS),
                    optionLabel = { stringResource(it.labelRes()) },
                    onOptionSelected = onTypeFilterChange,
                    modifier = Modifier.weight(1f)
                )

                AppFilterDropdown(
                    label = stringResource(R.string.user_status_filter_label),
                    selectedOption = stringResource(state.selectedStatusFilter.labelRes()),
                    options = listOf(UserStatusFilter.ALL, UserStatusFilter.PENDING, UserStatusFilter.ACCEPTED, UserStatusFilter.ARCHIVED),
                    optionLabel = { stringResource(it.labelRes()) },
                    onOptionSelected = onStatusFilterChange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Text(
                    text = stringResource(R.string.loading),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6B7280)
                )
            } else if (sortedUsers.isEmpty()) {
                InstitutionUsersEmptyState(
                    isFiltered = state.selectedTypeFilter != UserTypeFilter.ALL || state.selectedStatusFilter != UserStatusFilter.ALL || state.searchQuery.isNotBlank()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(sortedUsers) { user ->
                        InstitutionUserCard(
                            user = user,
                            onDeleteInvite = onDeleteInvite,
                            onClick = {
                                val role = user.targetRole
                                val profileId = user.profileId

                                Log.d(
                                    "InstitutionUsers",
                                    "Open user profileId=$profileId role=$role inviteStatus=${user.inviteStatus}"
                                )

                                onUserClick(profileId, role)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InstitutionUserCard(
    user: InstitutionUserDto,
    onDeleteInvite: (InstitutionUserDto) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val profileArchived = user.institutionArchivedAt != null
    val inviteAccepted = !user.acceptedAt.isNullOrBlank() ||
        user.inviteStatus?.trim()?.lowercase() == "accepted" ||
        user.profileId != null
    val isPending = !inviteAccepted && !profileArchived

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarWithInitials(user = user)

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cardPrimaryText(user),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black
                    )

                    val subtitle = buildAnnotatedString {
                        append(userRoleLabel(user))
                        if (profileArchived && inviteAccepted) {
                            append(" · ")
                            append(stringResource(R.string.invite_accepted_before))
                        }
                    }
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                ProfileStateBadge(user = user)

                if (isPending) {
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick = { onDeleteInvite(user) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = stringResource(R.string.delete_invite),
                            tint = Color(0xFFB3261E)
                        )
                    }
                }
            }

            if (inviteAccepted || profileArchived) {
                Text(
                    text = user.email,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AvatarWithInitials(user: InstitutionUserDto) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(Color(0xFFEFEFEF), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getUserInitials(user),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun ProfileStateBadge(user: InstitutionUserDto) {
    val profileArchived = user.institutionArchivedAt != null
    val inviteAccepted = user.isInviteAccepted()

    val label = when {
        profileArchived -> stringResource(R.string.archived_status)
        !user.isActive -> stringResource(R.string.inactive_status)
        inviteAccepted -> stringResource(R.string.accepted)
        else -> stringResource(R.string.pending)
    }

    val containerColor = when {
        profileArchived -> Color(0xFFF3F4F6)
        !user.isActive -> Color(0xFFFFEBEE)
        inviteAccepted -> Color(0xFFE7F7EC)
        else -> Color(0xFFFFF8CC)
    }

    val textColor = when {
        profileArchived -> Color(0xFF6B7280)
        !user.isActive -> Color(0xFFC62828)
        inviteAccepted -> Color(0xFF1B7F3A)
        else -> Color(0xFF7A5D00)
    }

    Box(
        modifier = Modifier
            .background(color = containerColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun InstitutionUsersEmptyState(isFiltered: Boolean) {
    val title = if (isFiltered) {
        stringResource(R.string.no_users_found)
    } else {
        stringResource(R.string.no_users_yet)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        if (!isFiltered) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_users_yet_description),
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun cardPrimaryText(user: InstitutionUserDto): String {
    val fullName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim()
    return if (fullName.isNotBlank()) fullName else user.email
}

@Composable
private fun userRoleLabel(user: InstitutionUserDto): String {
    return when (user.targetRole.lowercase().trim()) {
        "student" -> stringResource(R.string.student)
        "teacher" -> stringResource(R.string.teacher)
        else -> user.targetRole
    }
}

private fun getUserInitials(user: InstitutionUserDto): String {
    val fullName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim()
    return if (fullName.isNotBlank()) {
        val parts = fullName.split(" ")
        val first = parts.firstOrNull()?.firstOrNull()?.uppercase() ?: ""
        val last = parts.lastOrNull()?.firstOrNull()?.uppercase() ?: ""
        "$first$last"
    } else {
        user.email.firstOrNull()?.uppercase() ?: "?"
    }
}
