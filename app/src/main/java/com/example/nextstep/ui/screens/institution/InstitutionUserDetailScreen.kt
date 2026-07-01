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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.nextstep.data.model.InstitutionUserDetailDto

@Composable
fun InstitutionUserDetailScreen(
    profileId: String,
    role: String,
    inviteId: String,
    onBackClick: () -> Unit,
    viewModel: InstitutionUserDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(profileId, role, inviteId) {
        viewModel.loadUserDetail(
            profileId = if (profileId == "no_profile") null else profileId,
            role = role,
            inviteId = if (inviteId == "no_invite") null else inviteId
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
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

                state.errorMessageRes != null && state.userDetail == null -> {
                    InstitutionUserDetailErrorState(message = stringResource(state.errorMessageRes!!))
                }

                state.userDetail != null -> {
                    InstitutionUserDetailContent(
                        detail = state.userDetail!!,
                        isPendingInvite = state.isPendingInvite
                    )
                }
            }
        }
    }
}

@Composable
private fun InstitutionUserDetailContent(
    detail: InstitutionUserDetailDto,
    isPendingInvite: Boolean
) {
    val roleLabel = when (detail.targetRole.lowercase().trim()) {
        "student" -> stringResource(R.string.student)
        "teacher" -> stringResource(R.string.teacher)
        else -> detail.targetRole
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // Avatar with initials
        val initials = getUserDetailInitials(detail)
        val fullName = "${detail.firstName.orEmpty()} ${detail.lastName.orEmpty()}".trim()

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = Color(0xFFEFEFEF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (fullName.isNotBlank()) fullName else detail.email,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(12.dp))

            // Status badge
            when {
                isPendingInvite || detail.inviteStatus?.lowercase()?.trim() == "pending" -> {
                    DetailBadge(
                        label = stringResource(R.string.pending),
                        containerColor = Color(0xFFFFF8CC),
                        textColor = Color(0xFF7A5D00)
                    )
                }
                detail.institutionArchivedAt != null -> {
                    DetailBadge(
                        label = "Arquivado",
                        containerColor = Color(0xFFF3F4F6),
                        textColor = Color(0xFF6B7280)
                    )
                }
                detail.isActive -> {
                    DetailBadge(
                        label = "Ativo",
                        containerColor = Color(0xFFE7F7EC),
                        textColor = Color(0xFF1B7F3A)
                    )
                }
                else -> {
                    DetailBadge(
                        label = "Inativo",
                        containerColor = Color(0xFFF3F4F6),
                        textColor = Color(0xFF6B7280)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${roleLabel} · ${detail.email}",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Personal info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F8F8)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                UserDetailRow(
                    label = stringResource(R.string.email),
                    value = detail.email
                )

                if (!detail.phone.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    UserDetailRow(
                        label = stringResource(R.string.phone),
                        value = detail.phone!!
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                UserDetailRow(
                    label = stringResource(R.string.function_label),
                    value = roleLabel
                )

                if (isPendingInvite || detail.inviteStatus?.lowercase()?.trim() == "pending") {
                    Spacer(modifier = Modifier.height(12.dp))
                    UserDetailRow(
                        label = stringResource(R.string.status),
                        value = stringResource(R.string.pending)
                    )

                    if (!detail.createdAt.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        UserDetailRow(
                            label = "Data do convite",
                            value = detail.createdAt!!
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Este utilizador ainda não concluiu o registo.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    UserDetailRow(
                        label = stringResource(R.string.account_status),
                        value = if (detail.isActive) {
                            stringResource(R.string.active_status)
                        } else {
                            stringResource(R.string.inactive_status)
                        }
                    )

                    if (!detail.createdAt.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        UserDetailRow(
                            label = stringResource(R.string.registration_date),
                            value = detail.createdAt!!
                        )
                    }
                }
            }
        }

        // Academic info (for students)
        if (detail.targetRole.lowercase().trim() == "student" && !isPendingInvite) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.academic_information),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!detail.studentNumber.isNullOrBlank()) {
                        UserDetailRow(
                            label = stringResource(R.string.student_number),
                            value = detail.studentNumber!!
                        )
                    }

                    if (!detail.course.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        UserDetailRow(
                            label = stringResource(R.string.course),
                            value = detail.course!!
                        )
                    }

                    if (detail.academicYear != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        UserDetailRow(
                            label = stringResource(R.string.academic_year),
                            value = "${detail.academicYear}º ano"
                        )
                    }

                    if (!detail.educationInstitution.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        UserDetailRow(
                            label = stringResource(R.string.education_institution),
                            value = detail.educationInstitution!!
                        )
                    }
                }
            }
        }

        // Professional info (for teachers)
        if (detail.targetRole.lowercase().trim() == "teacher" && !isPendingInvite) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    if (!detail.department.isNullOrBlank()) {
                        UserDetailRow(
                            label = stringResource(R.string.department),
                            value = detail.department!!
                        )
                    }
                }
            }
        }

        // Archived info
        if (detail.institutionArchivedAt != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Este utilizador foi arquivado pela instituição. O histórico foi mantido.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun UserDetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun DetailBadge(label: String, containerColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
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
private fun InstitutionUserDetailErrorState(message: String) {
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
                onClick = { /* Retry is handled by LaunchedEffect */ },
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

private fun getUserDetailInitials(detail: InstitutionUserDetailDto): String {
    val fullName = "${detail.firstName.orEmpty()} ${detail.lastName.orEmpty()}".trim()
    return if (fullName.isNotBlank()) {
        val parts = fullName.split(" ")
        val first = parts.firstOrNull()?.firstOrNull()?.uppercase() ?: ""
        val last = parts.lastOrNull()?.firstOrNull()?.uppercase() ?: ""
        "$first$last"
    } else {
        detail.email.firstOrNull()?.uppercase() ?: "?"
    }
}