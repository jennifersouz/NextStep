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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.data.model.UpdateProfileDto

@Composable
fun AdminCreateEditUserScreen(
    existingProfile: ProfileDto? = null,
    onBackClick: () -> Unit,
    onSave: (firstName: String, lastName: String, phone: String, role: String, isActive: Boolean) -> Unit
) {
    val isEditing = existingProfile != null

    var firstName by remember {
        mutableStateOf(existingProfile?.firstName ?: "")
    }
    var lastName by remember {
        mutableStateOf(existingProfile?.lastName ?: "")
    }
    var phone by remember {
        mutableStateOf(existingProfile?.phone ?: "")
    }
    var selectedRole by remember {
        mutableStateOf(existingProfile?.role ?: "student")
    }
    var isActive by remember {
        mutableStateOf(existingProfile?.isActive ?: true)
    }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var roleError by remember { mutableStateOf<String?>(null) }

    val roleOptions = listOf(
        "student" to "Aluno",
        "teacher" to "Docente",
        "company" to "Empresa",
        "admin" to "Administrador"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
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
                    contentDescription = "Voltar",
                    tint = Color.Black
                )
            }

            Text(
                text = if (isEditing) "Editar utilizador" else "Criar utilizador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        if (!isEditing) {
            Text(
                text = "Nota: A criação de contas com login real requer uma Edge Function segura no servidor. Aqui podes gerir apenas dados de perfis existentes.",
                fontSize = 13.sp,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // First name
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstNameError = null
                },
                label = { Text("Nome *") },
                placeholder = { Text("Insira o nome") },
                isError = firstNameError != null,
                supportingText = firstNameError?.let { { Text(it, color = Color(0xFFB00020)) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Last name
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apelido") },
                placeholder = { Text("Insira o apelido") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email (read-only when editing)
            if (isEditing && existingProfile != null) {
                OutlinedTextField(
                    value = existingProfile.email,
                    onValueChange = {},
                    label = { Text("Email") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFEDEDED),
                        disabledBorderColor = Color(0xFFEDEDED),
                        disabledContainerColor = Color(0xFFF5F5F5)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Telefone") },
                placeholder = { Text("Insira o telefone") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Role dropdown
            Text(
                text = "Função *",
                fontSize = 14.sp,
                color = Color(0xFF8A8A8A),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Box {
                val selectedLabel = roleOptions.find { it.first == selectedRole }?.second ?: selectedRole

                OutlinedTextField(
                    value = selectedLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Selecionar",
                            tint = Color(0xFF333333)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { roleMenuExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    isError = roleError != null,
                    supportingText = roleError?.let { { Text(it, color = Color(0xFFB00020)) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFEDEDED),
                        focusedBorderColor = Color(0xFF333333)
                    ),
                    enabled = false,
                    singleLine = true
                )

                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    roleOptions.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedRole = value
                                roleMenuExpanded = false
                                roleError = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Active toggle info
            Text(
                text = "Estado da conta: ${if (isActive) "Ativo" else "Inativo"}",
                fontSize = 14.sp,
                color = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = {
                    var hasError = false
                    if (firstName.isBlank()) {
                        firstNameError = "O nome é obrigatório."
                        hasError = true
                    }
                    if (!hasError) {
                        onSave(firstName, lastName, phone, selectedRole, isActive)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isEditing) "Guardar alterações" else "Criar utilizador",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}