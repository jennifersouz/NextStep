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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.data.model.AdminCompanyDto

@Composable
fun AdminCompanyDetailScreen(
    company: AdminCompanyDto,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeactivate: () -> Unit = {},
    onReactivate: () -> Unit = {},
    onArchive: (String?) -> Unit = {},
    onViewOffers: (AdminCompanyDto) -> Unit = {},
    isActionLoading: Boolean = false,
    successMessage: String? = null,
    errorMessage: String? = null,
    onMessageDismiss: () -> Unit = {}
) {
    val isActive = company.isActive == true
    val isArchived = company.isArchived

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
            title = { Text("Desativar acesso") },
            text = { Text("Esta ação bloqueia temporariamente o acesso da empresa, mas mantém os dados e o histórico na plataforma.") },
            confirmButton = {
                TextButton(onClick = { dismissDialogs(); onDeactivate() }) {
                    Text("Desativar", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { dismissDialogs() }) { Text("Cancelar") } }
        )
    }

    // Reactivate dialog
    if (showReactivateDialog) {
        AlertDialog(
            onDismissRequest = { dismissDialogs() },
            title = { Text("Reativar acesso") },
            text = { Text("Esta ação permite que a empresa volte a aceder à plataforma. As ofertas antigas não serão reativadas automaticamente.") },
            confirmButton = {
                TextButton(onClick = { dismissDialogs(); onReactivate() }) {
                    Text("Reativar", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { dismissDialogs() }) { Text("Cancelar") } }
        )
    }

    // Archive dialog
    if (showArchiveDialog) {
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { dismissDialogs() },
            title = { Text("Remover da plataforma") },
            text = {
                Column {
                    Text("Esta ação remove a empresa da lista principal, bloqueia o acesso e desativa as ofertas ativas. As candidaturas e o histórico serão mantidos.")
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Motivo (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { dismissDialogs(); onArchive(reason.ifBlank { null }) }) {
                    Text("Remover", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { dismissDialogs() }) { Text("Cancelar") } }
        )
    }

    // NOTE: Permanent delete was intentionally removed. The app does not support permanent deletion.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.Black)
            }
            Text("Detalhes da Empresa", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            if (isActionLoading) {
                Spacer(modifier = Modifier.width(12.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
            }
        }

        // Feedback messages
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
                .padding(24.dp)
        ) {
            // Company avatar and name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B2B2B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Business,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = company.companyName ?: "Empresa",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    val statusLabel: String
                    val statusColor: Color
                    val statusBg: Color
                    when {
                        isArchived -> {
                            statusLabel = "Arquivada"
                            statusColor = Color(0xFF6D4C41)
                            statusBg = Color(0xFFEFEBE9)
                        }
                        isActive -> {
                            statusLabel = "Ativa"
                            statusColor = Color(0xFF2E7D32)
                            statusBg = Color(0xFFE8F5E9)
                        }
                        else -> {
                            statusLabel = "Inativa"
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

            // Company details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(label = "Nome", value = company.companyName ?: "")
                    company.nif?.let { DetailRow(label = "NIF", value = it) }
                    company.businessArea?.let { DetailRow(label = "Área de Negócio", value = it) }
                    company.location?.let { DetailRow(label = "Localização", value = it) }
                    company.phone?.let { DetailRow(label = "Telefone", value = it) }
                    company.description?.let { DetailRow(label = "Descrição", value = it) }
                    company.offersCount?.let { DetailRow(label = "Ofertas Publicadas", value = it.toString()) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                onClick = { if (!isActionLoading) onEditClick() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = null,
                        tint = Color(0xFF8D6E00),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Editar empresa", fontSize = 15.sp, color = Color.Black, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCCCCCC))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Deactivate / Reactivate — apenas para empresas não arquivadas
            if (!isArchived) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = {
                        if (!isActionLoading) {
                            if (isActive) showDeactivateDialog = true
                            else showReactivateDialog = true
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Filled.Block else Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = when {
                                isActionLoading -> Color(0xFFCCCCCC)
                                isActive -> Color(0xFFE65100)
                                else -> Color(0xFF2E7D32)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when {
                                isActionLoading -> "A atualizar..."
                                isActive -> "Desativar acesso"
                                else -> "Reativar acesso"
                            },
                            fontSize = 15.sp,
                            color = if (isActionLoading) Color(0xFF999999) else Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCCCCCC))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Archive (Remove from platform)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = { if (!isActionLoading) showArchiveDialog = true }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Archive,
                            contentDescription = null,
                            tint = if (isActionLoading) Color(0xFFCCCCCC) else Color(0xFFBF360C),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Remover da plataforma",
                            fontSize = 15.sp,
                            color = if (isActionLoading) Color(0xFF999999) else Color(0xFFBF360C),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCCCCCC))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // View offers
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                onClick = { onViewOffers(company) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Business,
                        contentDescription = null,
                        tint = Color(0xFF555555),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Ver ofertas da empresa", fontSize = 15.sp, color = Color.Black, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCCCCCC))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF777777), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, color = Color.Black)
    }
}
