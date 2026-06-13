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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val roleOptions = listOf(
    "student" to "Aluno",
    "teacher" to "Docente",
    "company" to "Empresa",
    "advisor" to "Orientador",
    "institution" to "Instituição",
    "admin" to "Administrador"
)

@Composable
fun AdminEditUserScreen(
    userId: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AdminEditUserViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var roleMenuExpanded by remember { mutableStateOf(false) }

    // Carregar utilizador apenas quando o userId mudar
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    // Consumir eventos one-shot (ex: UserSaved) - não fica preso no state
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AdminEditUserEvent.UserSaved -> {
                    onSaved()
                }
            }
        }
    }

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
                text = "Editar utilizador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (state.isSaving) {
                Spacer(modifier = Modifier.width(12.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
            }
        }

        // Loading state
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
            return
        }

        // Error messages
        if (state.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(state.errorMessage ?: "", color = Color(0xFFB00020), fontSize = 14.sp)
            }
        }

        if (state.successMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(state.successMessage ?: "", color = Color(0xFF2E7D32), fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Nome
            OutlinedTextField(
                value = state.firstName,
                onValueChange = viewModel::onFirstNameChange,
                label = { Text("Nome *") },
                placeholder = { Text("Insira o nome") },
                isError = state.firstNameError != null,
                supportingText = state.firstNameError?.let {
                    { Text(it, color = Color(0xFFB00020)) }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333)
                ),
                singleLine = true,
                enabled = !state.isSaving
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apelido
            OutlinedTextField(
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                label = { Text("Apelido") },
                placeholder = { Text("Insira o apelido") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333)
                ),
                singleLine = true,
                enabled = !state.isSaving
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email — apenas leitura
            OutlinedTextField(
                value = state.email,
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

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "O email não pode ser alterado nesta tela.",
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A),
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Telefone
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Telefone") },
                placeholder = { Text("Insira o telefone") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEDEDED),
                    focusedBorderColor = Color(0xFF333333)
                ),
                singleLine = true,
                enabled = !state.isSaving
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Função
            Text(
                text = "Função *",
                fontSize = 14.sp,
                color = Color(0xFF8A8A8A),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Box {
                val selectedLabel = roleOptions.find { it.first == state.role }?.second ?: state.role

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
                    isError = state.roleError != null,
                    supportingText = state.roleError?.let {
                        { Text(it, color = Color(0xFFB00020)) }
                    },
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
                                viewModel.onRoleChange(value)
                                roleMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estado ativo
            Text(
                text = "Estado da conta: ${if (state.isActive) "Ativo" else "Inativo"}",
                fontSize = 14.sp,
                color = if (state.isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Guardar
            Button(
                onClick = { viewModel.saveUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Guardar alterações",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}