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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.ui.utils.DateFormatUtils
import com.example.nextstep.ui.utils.Formatters


@Composable
fun AdminUserDetailScreen(
    profile: AdminProfileDto,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeactivate: () -> Unit,
    onReactivate: () -> Unit,
    onArchive: (String?) -> Unit,
    isActionLoading: Boolean = false,
    successMessageRes: Int? = null,
    errorMessageRes: Int? = null,
    onMessageDismiss: () -> Unit = {}
) {
    val isActive = profile.isActive == true
    val isArchived = profile.isArchived

    val locale = LocalConfiguration.current.locales[0]
    val formattedArchivedAt = DateFormatUtils.formatDateTimeForDisplay(profile.archivedAt, locale)

    LaunchedEffect(successMessageRes) {
        if (successMessageRes != null) {
            delay(3000)
            onMessageDismiss()
        }
    }
    LaunchedEffect(errorMessageRes) {
        if (errorMessageRes != null) {
            delay(3000)
            onMessageDismiss()
        }
    }

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

        // Feedback message — única fonte de verdade, temporária
        if (successMessageRes != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(stringResource(successMessageRes), color = Color(0xFF2E7D32), fontSize = 14.sp)
            }
        }
        if (errorMessageRes != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(stringResource(errorMessageRes), color = Color(0xFFB00020), fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar + Name + Status (same visual structure as company detail)
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B2B2B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

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
                            statusLabel = stringResource(R.string.active_status)
                            statusColor = Color(0xFF2E7D32)
                            statusBg = Color(0xFFE8F5E9)
                        }
                        else -> {
                            statusLabel = stringResource(R.string.inactive_status)
                            statusColor = Color(0xFFC62828)
                            statusBg = Color(0xFFFFEBEE)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info card (same style as company detail)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(label = stringResource(R.string.detail_name), value = displayName)
                    DetailRow(label = stringResource(R.string.email_label), value = profile.email ?: "")
                    DetailRow(label = stringResource(R.string.detail_role), value = Formatters.formatRole(profile.role))
                    DetailRow(label = stringResource(R.string.detail_phone), value = profile.phone ?: stringResource(R.string.not_available))

                    val createdAt = profile.createdAt
                        ?.let { DateFormatUtils.formatDateForUi(it) }
                        ?: stringResource(R.string.unknown_label)
                    DetailRow(label = stringResource(R.string.detail_registration_date), value = createdAt)
                }
            }

            if (isArchived) {
                Spacer(modifier = Modifier.height(12.dp))
                if (profile.archivedAt != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailRow(
                                label = stringResource(R.string.archived_at_label),
                                value = formattedArchivedAt ?: stringResource(R.string.not_available)
                            )
                        }
                    }
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

/**
 * Row-style field used inside the info card (matches company detail's DetailRow style).
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF777777), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, color = Color.Black)
    }
}