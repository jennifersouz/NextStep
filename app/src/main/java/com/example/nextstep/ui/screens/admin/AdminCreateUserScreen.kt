package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateUserScreen(
    onBackClick: () -> Unit,
    onUserCreated: () -> Unit,
    viewModel: AdminCreateUserViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var roleMenuExpanded by remember { mutableStateOf(false) }

    val roleOptions = listOf(
        "student" to "Aluno",
        "teacher" to "Docente",
        "company" to "Empresa",
        "admin" to "Administrador"
    )

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            onUserCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo utilizador", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Role Selection
            Text("Tipo de utilizador *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Box {
                OutlinedTextField(
                    value = roleOptions.find { it.first == state.selectedRole }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { roleMenuExpanded = true },
                    enabled = false,
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color(0xFFEDEDED),
                        disabledTrailingIconColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                DropdownMenu(expanded = roleMenuExpanded, onDismissRequest = { roleMenuExpanded = false }) {
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

            // Common Fields
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password temporária *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = state.isActive, onCheckedChange = viewModel::onIsActiveChange)
                Text("Estado ativo")
            }

            HorizontalDivider()

            // Role-specific fields
            when (state.selectedRole) {
                "student" -> {
                    OutlinedTextField(value = state.firstName, onValueChange = viewModel::onFirstNameChange, label = { Text("Nome *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.lastName, onValueChange = viewModel::onLastNameChange, label = { Text("Apelido") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.studentNumber, onValueChange = viewModel::onStudentNumberChange, label = { Text("Número de aluno") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.course, onValueChange = viewModel::onCourseChange, label = { Text("Curso") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.academicYear, onValueChange = viewModel::onAcademicYearChange, label = { Text("Ano académico") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.educationInstitution, onValueChange = viewModel::onEducationInstitutionChange, label = { Text("Instituição de ensino") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
                "teacher" -> {
                    OutlinedTextField(value = state.firstName, onValueChange = viewModel::onFirstNameChange, label = { Text("Nome *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.lastName, onValueChange = viewModel::onLastNameChange, label = { Text("Apelido") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.department, onValueChange = viewModel::onDepartmentChange, label = { Text("Departamento") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.institutionProfileId, onValueChange = viewModel::onInstitutionProfileIdChange, label = { Text("ID Instituição associada") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
                "company" -> {
                    OutlinedTextField(value = state.companyName, onValueChange = viewModel::onCompanyNameChange, label = { Text("Nome da empresa *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.nif, onValueChange = viewModel::onNifChange, label = { Text("NIF *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.businessArea, onValueChange = viewModel::onBusinessAreaChange, label = { Text("Área de negócio") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.location, onValueChange = viewModel::onLocationChange, label = { Text("Localização") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.description, onValueChange = viewModel::onDescriptionChange, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 3)
                }
                "admin" -> {
                    OutlinedTextField(value = state.firstName, onValueChange = viewModel::onFirstNameChange, label = { Text("Nome *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state.lastName, onValueChange = viewModel::onLastNameChange, label = { Text("Apelido") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.errorMessage != null) {
                Text(state.errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }
            if (state.successMessage != null) {
                Text(state.successMessage!!, color = Color(0xFF2E7D32), fontSize = 14.sp)
            }

            Button(
                onClick = viewModel::createUser,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Criar utilizador", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}