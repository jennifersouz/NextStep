package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditUserScreen(
    userId: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AdminEditUserViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var roleMenuExpanded by remember { mutableStateOf(false) }
    var institutionMenuExpanded by remember { mutableStateOf(false) }

    val roleOptions = listOf(
        "student" to stringResource(R.string.role_student),
        "teacher" to stringResource(R.string.role_teacher),
        "company" to stringResource(R.string.role_company),
        "advisor" to stringResource(R.string.role_advisor),
        "institution" to stringResource(R.string.role_institution),
        "admin" to stringResource(R.string.role_admin)
    )

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
                    contentDescription = stringResource(R.string.back_label),
                    tint = Color.Black
                )
            }

            Text(
                text = stringResource(R.string.edit_user_title),
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
        val errorText = state.errorMessage ?: state.errorMessageRes?.let { stringResource(it) }
        if (errorText != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(errorText, color = Color(0xFFB00020), fontSize = 14.sp)
            }
        }

        val successText = state.successMessage ?: state.successMessageRes?.let { stringResource(it) }
        if (successText != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(successText, color = Color(0xFF2E7D32), fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isCompany) {
                // ── Empresa: mostrar apenas "Nome da empresa" ──
                OutlinedTextField(
                    value = state.companyName,
                    onValueChange = viewModel::onCompanyNameChange,
                    label = { Text(stringResource(R.string.company_name_required)) },
                    placeholder = { Text(stringResource(R.string.company_name_placeholder)) },
                    isError = state.companyNameError != null,
                    supportingText = state.companyNameError?.let { resId ->
                        { Text(stringResource(resId), color = Color(0xFFB00020)) }
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
            } else {
                // ── Pessoa: mostrar Nome e Apelido ──
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = viewModel::onFirstNameChange,
                    label = { Text(stringResource(R.string.name_required)) },
                    placeholder = { Text(stringResource(R.string.name_placeholder_text)) },
                    isError = state.firstNameError != null,
                    supportingText = state.firstNameError?.let { resId ->
                        { Text(stringResource(resId), color = Color(0xFFB00020)) }
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
                    label = { Text(stringResource(R.string.last_name_required)) },
                    placeholder = { Text(stringResource(R.string.last_name_placeholder_text)) },
                    isError = state.lastNameError != null,
                    supportingText = state.lastNameError?.let { resId ->
                        { Text(stringResource(resId), color = Color(0xFFB00020)) }
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
            }

            // ── Instituição de Ensino (dropdown, apenas alunos) ─────────────────
            if (state.isStudent) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    stringResource(R.string.select_education_institution),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (state.isLoadingInstitutions) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(vertical = 8.dp),
                        color = Color.Gray
                    )
                } else if (state.availableInstitutions.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_institutions_available),
                        color = Color(0xFFB00020),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = institutionMenuExpanded,
                        onExpandedChange = { institutionMenuExpanded = !institutionMenuExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val selectedInstitution = state.availableInstitutions.find {
                            it.id == state.selectedInstitutionId
                        }
                        val institutionLabel = selectedInstitution?.displayName
                            ?: state.selectedInstitutionName

                        OutlinedTextField(
                            value = institutionLabel,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = institutionMenuExpanded) },
                            isError = state.studentInstitutionError != null,
                            supportingText = state.studentInstitutionError?.let { resId ->
                                { Text(stringResource(resId), color = Color(0xFFB00020)) }
                            },
                            placeholder = { Text(stringResource(R.string.select_institution_placeholder)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = Color.Black,
                                unfocusedBorderColor = if (state.studentInstitutionError != null)
                                    Color(0xFFB00020) else Color(0xFFEDEDED),
                                unfocusedTrailingIconColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = institutionMenuExpanded,
                            onDismissRequest = { institutionMenuExpanded = false },
                            modifier = Modifier.heightIn(max = 240.dp)
                        ) {
                            state.availableInstitutions.forEach { institution ->
                                DropdownMenuItem(
                                    text = { Text(institution.displayName) },
                                    onClick = {
                                        viewModel.onInstitutionSelected(institution.id, institution.displayName)
                                        institutionMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email — apenas leitura
            OutlinedTextField(
                value = state.email,
                onValueChange = {},
                label = { Text(stringResource(R.string.email_label)) },
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
                text = stringResource(R.string.email_not_editable),
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A),
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Telefone
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text(stringResource(R.string.phone)) },
                placeholder = { Text(stringResource(R.string.phone_placeholder_text)) },
                isError = state.phoneError != null,
                supportingText = state.phoneError?.let { resId ->
                    { Text(stringResource(resId), color = Color(0xFFB00020)) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                text = stringResource(R.string.function_label),
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
                            contentDescription = stringResource(R.string.select),
                            tint = Color(0xFF333333)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { roleMenuExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    isError = state.roleError != null,
                    supportingText = state.roleError?.let { resId ->
                        { Text(stringResource(resId), color = Color(0xFFB00020)) }
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
                text = stringResource(
                    R.string.account_status_format,
                    stringResource(if (state.isActive) R.string.active_status_label else R.string.inactive_status_label)
                ),
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
                        text = stringResource(R.string.save_changes),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}