package com.example.nextstep.ui.screens.admin

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.ui.utils.DateFormatUtils
import com.example.nextstep.ui.utils.roleToDisplayName


@Composable
fun AdminUserDetailScreen(
    profile: AdminProfileDto,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeactivate: () -> Unit,
    onReactivate: () -> Unit,
    onArchive: (String?) -> Unit,
    isActionLoading: Boolean = false,
    successMessage: String? = null,
    errorMessage: String? = null,
    onMessageDismiss: () -> Unit = {}
) {
    val isActive = profile.isActive == true
    val isArchived = profile.isArchived

    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showReactivateDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }

    fun dismissDialogs() {
        showDeactivateDialog = false
        showReactivateDialog = false
        showArchiveDialog = false
    }

    // Deactivate dialog
    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { dismissDialogs() },
            title = { Text(stringResource(R.string.deactivate_access)) },
            text = { Text(stringResource(R.string.deactivate_user_description)) },
            confirmButton = {
                TextButton(onClick = {
                    dismissDialogs()
                    onDeactivate()
                }) {
                    Text(stringResource(R.string.deactivate_action), color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { dismissDialogs() }) { Text(stringResource(R.string.cancel_action)) }
            }
        )
    }

    // Reactivate dialog
    if (showReactivateDialog) {
        AlertDialog(
            onDismissRequest = { dismissDialogs() },
            title = { Text(stringResource(R.string.reactivate_access)) },
            text = { Text(stringResource(R.string.reactivate_user_description)) },
            confirmButton = {
                TextButton(onClick = {
                    dismissDialogs()
                    onReactivate()
                }) {
                    Text(stringResource(R.string.reactivate_action), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { dismissDialogs() }) { Text(stringResource(R.string.cancel_action)) }
            }
        )
    }

    // Archive dialog
    if (showArchiveDialog) {
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { dismissDialogs() },
            title = { Text(stringResource(R.string.remove_from_platform)) },
            text = {
                Column {
                    Text(stringResource(R.string.remove_user_description))
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text(stringResource(R.string.reason_optional)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    dismissDialogs()
                    onArchive(reason.ifBlank { null })
                }) {
                    Text(stringResource(R.string.remove_action), color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { dismissDialogs() }) { Text(stringResource(R.string.cancel_action)) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_label),
                    tint = Color.Black
                )
            }
            Text(
                text = stringResource(R.string.user_details_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (isActionLoading) {
                Spacer(modifier = Modifier.width(12.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
            }
        }

        // Feedback messages — amigáveis, sem detalhes técnicos
        if (successMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(successMessage, color = Color(0xFF2E7D32), fontSize = 14.sp)
            }
        }
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(errorMessage, color = Color(0xFFB00020), fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar
            val displayName = listOfNotNull(profile.firstName, profile.lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { profile.email ?: profile.id }

            val initials = displayName
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() }
                .ifBlank { "?" }

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2B2B2B))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status badge
            val statusLabel: String
            val statusColor: Color
            val statusBg: Color
            when {
                isArchived -> {
                    statusLabel = stringResource(R.string.archived_status)
                    statusColor = Color(0xFF6D4C41)
                    statusBg = Color(0xFFEFEBE9)
                }
                isActive -> {
                    statusLabel = stringResource(R.string.active_status_label)
                    statusColor = Color(0xFF2E7D32)
                    statusBg = Color(0xFFE8F5E9)
                }
                else -> {
                    statusLabel = stringResource(R.string.inactive_status_label)
                    statusColor = Color(0xFFC62828)
                    statusBg = Color(0xFFFFEBEE)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = statusLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fields
            DetailField(label = stringResource(R.string.detail_name), value = displayName)
            Spacer(modifier = Modifier.height(16.dp))
            DetailField(label = stringResource(R.string.email_label), value = profile.email ?: "")
            Spacer(modifier = Modifier.height(16.dp))
            DetailField(label = stringResource(R.string.detail_role), value = roleToDisplayName(profile.role))
            Spacer(modifier = Modifier.height(16.dp))
            DetailField(label = stringResource(R.string.detail_phone), value = profile.phone ?: stringResource(R.string.not_available))
            Spacer(modifier = Modifier.height(16.dp))

            val createdAt = profile.createdAt
                ?.let { DateFormatUtils.formatDateForUi(it) }
                ?: stringResource(R.string.unknown_label)
            DetailField(label = stringResource(R.string.detail_registration_date), value = createdAt)

            if (isArchived) {
                Spacer(modifier = Modifier.height(16.dp))
                if (profile.archivedAt != null) {
                    DetailField(label = stringResource(R.string.archived_at_label), value = profile.archivedAt)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFBE9E7))
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.user_removed_message),
                        fontSize = 14.sp,
                        color = Color(0xFF6D4C41)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Editar utilizador — sempre visível para utilizadores não-arquivados
            if (!isArchived) {
                Button(
                    onClick = onEditClick,
                    enabled = !isActionLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1A1A),
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.edit_user_action), fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action buttons — controlados exclusivamente por estado
            // Arquivado → sem botões
            // Ativo     → Desativar + Remover da plataforma
            // Inativo   → Reativar  + Remover da plataforma
            if (!isArchived) {
                if (isActive) {
                    Button(
                        onClick = { showDeactivateDialog = true },
                        enabled = !isActionLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFF3E0),
                            contentColor = Color(0xFFE65100)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Block,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.deactivate_access), fontWeight = FontWeight.Medium)
                    }
                } else {
                    Button(
                        onClick = { showReactivateDialog = true },
                        enabled = !isActionLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.reactivate_access), fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Remover da plataforma — sempre visível para não-arquivados
                Button(
                    onClick = { showArchiveDialog = true },
                    enabled = !isActionLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBE9E7),
                        contentColor = Color(0xFFBF360C)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Archive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.remove_from_platform), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
private fun DetailField(label: String, value: String, valueColor: Color = Color.Black) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF8A8A8A))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, color = valueColor, fontWeight = FontWeight.Medium)
    }
}
