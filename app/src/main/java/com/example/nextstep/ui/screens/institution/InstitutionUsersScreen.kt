package com.example.nextstep.ui.screens.institution

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.InstitutionUserDto

@Composable
fun InstitutionUsersScreen(
    onBackClick: () -> Unit = {},
    onAddUserClick: () -> Unit = {},
    viewModel: InstitutionUsersViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val filteredUsers = filterUsers(state.users, state.selectedFilter)
    
    val searchedUsers = if (state.searchQuery.isBlank()) {
        filteredUsers
    } else {
        val query = state.searchQuery.trim().lowercase()
        filteredUsers.filter { user ->
            val fullName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".lowercase()
            fullName.contains(query) ||
                user.email.lowercase().contains(query) ||
                user.course.orEmpty().lowercase().contains(query) ||
                user.department.orEmpty().lowercase().contains(query) ||
                user.studentNumber.orEmpty().lowercase().contains(query)
        }
    }

    val sortedUsers = searchedUsers.sortedWith(
        compareBy<InstitutionUserDto> { isInviteAccepted(it) }
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
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

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

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(InstitutionUserFilter.entries) { filter ->
                    val isSelected = state.selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectFilter(filter) },
                        label = {
                            Text(text = institutionUserFilterLabel(filter))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFDFA52),
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFFF3F3F3),
                            labelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search),
                        color = Color(0xFF6B7280)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFDFA52),
                    cursorColor = Color.Black
                )
            )

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
                    isFiltered = state.selectedFilter != InstitutionUserFilter.ALL || state.searchQuery.isNotBlank()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(sortedUsers) { user ->
                        InstitutionUserCard(user = user)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InstitutionUserCard(user: InstitutionUserDto) {
    val accepted = isInviteAccepted(user)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9F9F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = Color(0xFFEFEFEF),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getUserInitials(user),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = displayInstitutionUserName(user),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                InviteStatusBadge(isAccepted = accepted)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = institutionUserSubtitle(user),
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            if (accepted) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun InviteStatusBadge(isAccepted: Boolean) {
    val label = if (isAccepted) {
        stringResource(R.string.accepted)
    } else {
        stringResource(R.string.pending)
    }

    val containerColor = if (isAccepted) {
        Color(0xFFE7F7EC)
    } else {
        Color(0xFFFFF8CC)
    }

    val textColor = if (isAccepted) {
        Color(0xFF1B7F3A)
    } else {
        Color(0xFF7A5D00)
    }

    Box(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
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

private fun isInviteAccepted(user: InstitutionUserDto): Boolean {
    return !user.acceptedAt.isNullOrBlank() ||
        user.inviteStatus.lowercase().trim() == "accepted"
}

private fun displayInstitutionUserName(user: InstitutionUserDto): String {
    val fullName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim()
    return if (fullName.isNotBlank()) {
        fullName
    } else {
        user.email
    }
}

@Composable
private fun institutionUserSubtitle(user: InstitutionUserDto): String {
    val roleLabel = when (user.targetRole.lowercase().trim()) {
        "student" -> stringResource(R.string.student)
        "teacher" -> stringResource(R.string.teacher)
        else -> user.targetRole
    }

    val accepted = isInviteAccepted(user)

    if (!accepted) {
        return "$roleLabel · ${stringResource(R.string.pending_invite)}"
    }

    val detail = when (user.targetRole.lowercase().trim()) {
        "student" -> user.course.orEmpty()
        "teacher" -> user.department.orEmpty()
        else -> ""
    }

    return if (detail.isNotBlank()) {
        "$roleLabel · $detail"
    } else {
        roleLabel
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

private fun filterUsers(
    users: List<InstitutionUserDto>,
    filter: InstitutionUserFilter
): List<InstitutionUserDto> {
    return when (filter) {
        InstitutionUserFilter.ALL -> users
        InstitutionUserFilter.STUDENTS -> users.filter { 
            it.targetRole.lowercase().trim() == "student" 
        }
        InstitutionUserFilter.TEACHERS -> users.filter { 
            it.targetRole.lowercase().trim() == "teacher" 
        }
        InstitutionUserFilter.PENDING -> users.filter { !isInviteAccepted(it) }
        InstitutionUserFilter.ACCEPTED -> users.filter { isInviteAccepted(it) }
    }
}

@Composable
private fun institutionUserFilterLabel(filter: InstitutionUserFilter): String {
    return when (filter) {
        InstitutionUserFilter.ALL -> stringResource(R.string.filter_all)
        InstitutionUserFilter.STUDENTS -> stringResource(R.string.student)
        InstitutionUserFilter.TEACHERS -> stringResource(R.string.teacher)
        InstitutionUserFilter.PENDING -> stringResource(R.string.pending)
        InstitutionUserFilter.ACCEPTED -> stringResource(R.string.accepted)
    }
}