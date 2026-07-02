package com.example.nextstep.ui.screens.institution

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun InstitutionHomeScreen(
    onAddUserClick: () -> Unit,
    onViewUsersClick: () -> Unit,
    onStudentsClick: () -> Unit = {},
    onTeachersClick: () -> Unit = {},
    viewModel: InstitutionHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHome()
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }

        state.errorMessageRes != null -> {
            InstitutionHomeErrorState(
                message = stringResource(state.errorMessageRes!!),
                onRetryClick = { viewModel.loadHome() }
            )
        }

        else -> {
            InstitutionHomeContent(
                state = state,
                onAddUserClick = onAddUserClick,
                onViewUsersClick = onViewUsersClick,
                onStudentsClick = onStudentsClick,
                onTeachersClick = onTeachersClick
            )
        }
    }
}

@Composable
private fun InstitutionHomeContent(
    state: InstitutionHomeUiState,
    onAddUserClick: () -> Unit,
    onViewUsersClick: () -> Unit,
    onStudentsClick: () -> Unit = {},
    onTeachersClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 96.dp)
    ) {
        Text(
            text = stringResource(R.string.institution_area),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.institutionName.isNotBlank()) {
            Text(
                text = stringResource(R.string.institution_home_greeting, state.institutionName),
                fontSize = 17.sp,
                color = Color.Black
            )
        }

        Text(
            text = stringResource(R.string.institution_home_description),
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        if (state.users.isEmpty()) {
            InstitutionEmptyState(onAddUserClick = onAddUserClick)
        } else {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InstitutionSummaryCard(
                    title = stringResource(R.string.students_summary),
                    value = state.totalStudents.toString(),
                    modifier = Modifier.weight(1f),
                    onClick = onStudentsClick.takeIf { state.totalStudents > 0 }
                )
                InstitutionSummaryCard(
                    title = stringResource(R.string.teachers_summary),
                    value = state.totalTeachers.toString(),
                    modifier = Modifier.weight(1f),
                    onClick = onTeachersClick.takeIf { state.totalTeachers > 0 }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InstitutionSummaryCard(
                    title = stringResource(R.string.pending_invites),
                    value = state.pendingInvites.toString(),
                    modifier = Modifier.weight(1f)
                )
                InstitutionSummaryCard(
                    title = stringResource(R.string.accepted_invites),
                    value = state.acceptedInvites.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.quick_actions),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAddUserClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(R.string.add_user),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onViewUsersClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.view_users),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.latest_invites),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.latestInvites.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_invites_yet),
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                state.latestInvites.forEach { user ->
                    LatestInviteCard(user = user)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.users.size > state.latestInvites.size) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = onViewUsersClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.view_all),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstitutionSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        enabled = onClick != null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun LatestInviteCard(user: InstitutionUserDto) {
    val studentLabel = stringResource(R.string.student)
    val teacherLabel = stringResource(R.string.teacher)
    val pendingLabel = stringResource(R.string.pending)
    val acceptedLabel = stringResource(R.string.accepted)

    val roleLabel = when (user.targetRole) {
        "student" -> studentLabel
        "teacher" -> teacherLabel
        else -> user.targetRole
    }

    val isAccepted = user.acceptedAt != null || user.inviteStatus == "accepted"

    val hasName = !user.firstName.isNullOrBlank() && !user.lastName.isNullOrBlank()
    val displayName = if (hasName) {
        "${user.firstName} ${user.lastName}"
    } else {
        user.email
    }
    val displayEmail = if (hasName) user.email else ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(name = displayName)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (displayEmail.isNotBlank()) {
                    Text(
                        text = displayEmail,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InviteTypeBadge(label = roleLabel)
                    InviteStatusBadge(
                        label = if (isAccepted) acceptedLabel else pendingLabel,
                        isAccepted = isAccepted
                    )
                }
            }
        }
    }
}

@Composable
private fun InitialsAvatar(name: String, modifier: Modifier = Modifier) {
    val initials = name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFE5E7EB)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun InviteTypeBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFEEF2FF))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4F46E5)
        )
    }
}

@Composable
private fun InviteStatusBadge(label: String, isAccepted: Boolean) {
    val (bgColor, textColor) = if (isAccepted) {
        Color(0xFFDCFCE7) to Color(0xFF166534)
    } else {
        Color(0xFFFEF3C7) to Color(0xFF92400E)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
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
private fun InstitutionEmptyState(onAddUserClick: () -> Unit) {
    Spacer(modifier = Modifier.height(48.dp))

    Text(
        text = stringResource(R.string.no_institution_users_title),
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.no_institution_users_description),
        fontSize = 14.sp,
        color = Color(0xFF6B7280),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onAddUserClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFDFA52),
            contentColor = Color.Black
        )
    ) {
        Text(
            text = stringResource(R.string.add_user),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InstitutionHomeErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message,
                color = Color(0xFFB00020),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Text(text = stringResource(R.string.try_again))
            }
        }
    }
}