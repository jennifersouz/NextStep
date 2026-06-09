package com.example.nextstep.ui.screens.institution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = stringResource(R.string.manage_users),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddUserClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(R.string.add_user),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
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
            } else if (state.users.isEmpty()) {
                Text(
                    text = "Ainda não existem utilizadores.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6B7280)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.users) { user ->
                        UserCard(user = user)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: InstitutionUserDto) {
    // Resolve strings before usage to avoid @Composable invocation issues in certain contexts
    val studentLabel = stringResource(R.string.student)
    val teacherLabel = stringResource(R.string.teacher)
    val pendingLabel = stringResource(R.string.pending)
    val acceptedLabel = stringResource(R.string.accepted)

    val roleLabel = when (user.targetRole) {
        "student" -> studentLabel
        "teacher" -> teacherLabel
        else -> user.targetRole
    }

    val statusLabel = when (user.inviteStatus) {
        "pending" -> pendingLabel
        "accepted" -> acceptedLabel
        else -> user.inviteStatus
    }

    val statusColor = if (user.inviteStatus == "pending") Color(0xFFB00020) else Color(0xFF4CAF50)

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
                        .size(48.dp)
                        .background(
                            color = Color(0xFFFDFA52),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getInitials(user.firstName, user.lastName),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                StatusBadge(status = user.inviteStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = roleLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "•",
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = statusLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val pendingLabel = stringResource(R.string.pending)
    val acceptedLabel = stringResource(R.string.accepted)

    val backgroundColor = when (status) {
        "pending" -> Color(0xFFFFF3E0)
        "accepted" -> Color(0xFFE8F5E9)
        else -> Color(0xFFF5F5F5)
    }

    val textColor = when (status) {
        "pending" -> Color(0xFFFF9800)
        "accepted" -> Color(0xFF4CAF50)
        else -> Color(0xFF6B7280)
    }

    val statusText = when (status) {
        "pending" -> pendingLabel
        "accepted" -> acceptedLabel
        else -> status
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = statusText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

fun getInitials(firstName: String, lastName: String): String {
    return "${firstName.firstOrNull()?.uppercase() ?: ""}${lastName.firstOrNull()?.uppercase() ?: ""}"
}
