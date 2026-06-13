package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.ui.utils.DateFormatUtils

@Composable
fun AdminUserDetailScreen(
    profile: AdminProfileDto,
    onBackClick: () -> Unit,
    onEditClick: (AdminProfileDto) -> Unit,
    onToggleActive: (String, Boolean) -> Unit,
    onDeleteUser: (String) -> Unit,
    isLoading: Boolean = false
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showToggleDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remover utilizador") },
            text = { Text("Tens a certeza que queres remover este utilizador? Esta ação pode afetar dados relacionados.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteUser(profile.id)
                    }
                ) {
                    Text("Remover", color = Color(0xFFB00020), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showToggleDialog) {
        AlertDialog(
            onDismissRequest = { showToggleDialog = false },
            title = { Text(if (profile.isActive == true) "Desativar conta" else "Ativar conta") },
            text = {
                Text(
                    if (profile.isActive == true)
                        "Esta conta será desativada e o utilizador não poderá aceder à plataforma."
                    else
                        "Esta conta será ativada e o utilizador poderá aceder novamente à plataforma."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showToggleDialog = false
                        onToggleActive(profile.id, profile.isActive != true)
                    }
                ) {
                    Text(
                        if (profile.isActive == true) "Desativar" else "Ativar",
                        color = if (profile.isActive == true) Color(0xFFB00020) else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showToggleDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black
                )
            }

            Text(
                text = "Detalhes do utilizador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
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

                // Fields
                DetailField(label = "Nome", value = displayName)
                Spacer(modifier = Modifier.height(16.dp))
                DetailField(label = "Email", value = profile.email ?: "")
                Spacer(modifier = Modifier.height(16.dp))
                DetailField(label = "Função", value = roleLabel(profile.role ?: ""))
                Spacer(modifier = Modifier.height(16.dp))
                DetailField(label = "Telefone", value = profile.phone ?: "Não disponível")
                Spacer(modifier = Modifier.height(16.dp))

                val createdAt = profile.createdAt?.let { DateFormatUtils.formatDateForUi(it) } ?: "Desconhecida"
                DetailField(label = "Data de registo", value = createdAt)
                Spacer(modifier = Modifier.height(16.dp))

                // Status
                val statusText = if (profile.isActive == true) "Ativo" else "Inativo"
                val statusColor = if (profile.isActive == true) Color(0xFF2E7D32) else Color(0xFFC62828)
                DetailField(label = "Estado da conta", value = statusText, valueColor = statusColor)

                Spacer(modifier = Modifier.height(40.dp))

                // Action buttons
                // Edit button
                Button(
                    onClick = { onEditClick(profile) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar utilizador", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Activate/Deactivate button
                Button(
                    onClick = { showToggleDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (profile.isActive == true) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                        contentColor = if (profile.isActive == true) Color(0xFFE65100) else Color(0xFF2E7D32)
                    )
                ) {
                    Icon(
                        imageVector = if (profile.isActive == true) Icons.Filled.Block else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (profile.isActive == true) "Desativar conta" else "Ativar conta",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Delete button
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFC62828)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remover utilizador", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}