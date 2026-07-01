package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateUserScreen(
    onBackClick: () -> Unit,
    onUserCreated: () -> Unit,
    viewModel: AdminCreateUserViewModel = viewModel()
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

    LaunchedEffect(Unit) {
        viewModel.loadCompanies()
    }

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            onUserCreated()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCreationState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_user_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_label))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    val generalErrorText = state.generalErrorMessage
                        ?: state.generalErrorMessageRes?.let { stringResource(it) }
                    if (generalErrorText != null) {
                        Text(
                            text = generalErrorText,
                            color = Color(0xFFB00020),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    val successText = state.successMessage
                        ?: state.successMessageRes?.let { stringResource(it) }
                    if (successText != null) {
                        Text(
                            text = successText,
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = viewModel::createUser,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                stringResource(R.string.create_user_button),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── Tipo de utilizador ───────────────────────────────────────────
            Text(stringResource(R.string.user_type_required), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            ExposedDropdownMenuBox(
                expanded = roleMenuExpanded,
                onExpandedChange = { roleMenuExpanded = !roleMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = roleOptions.find { it.first == state.selectedRole }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleMenuExpanded) },
                    isError = state.roleError != null,
                    supportingText = state.roleError?.let {
                        { Text(stringResource(it), color = Color(0xFFB00020)) }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = if (state.roleError != null)
                            Color(0xFFB00020) else Color(0xFFEDEDED),
                        unfocusedTrailingIconColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false },
                    modifier = Modifier.heightIn(max = 240.dp)
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

            // ── Email ────────────────────────────────────────────────────────
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(R.string.email_required)) },
                isError = state.emailError != null,
                supportingText = state.emailError?.let {
                    { Text(stringResource(it), color = Color(0xFFB00020)) }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // ── Password ─────────────────────────────────────────────────────
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(stringResource(R.string.temporary_password_label)) },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let {
                    { Text(stringResource(it), color = Color(0xFFB00020)) }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // ── Telefone (opcional para todos) ───────────────────────────────
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text(stringResource(R.string.phone)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // ── Estado ativo ─────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.isActive,
                    onCheckedChange = viewModel::onIsActiveChange
                )
                Text(stringResource(R.string.active_checkbox_label))
            }

            HorizontalDivider()

            // ── Campos por tipo de utilizador ────────────────────────────────
            when (state.selectedRole) {

                "student" -> {
                    OutlinedTextField(
                        value = state.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        label = { Text(stringResource(R.string.name_required)) },
                        isError = state.firstNameError != null,
                        supportingText = state.firstNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        label = { Text(stringResource(R.string.last_name_required)) },
                        isError = state.lastNameError != null,
                        supportingText = state.lastNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.studentNumber,
                        onValueChange = viewModel::onStudentNumberChange,
                        label = { Text(stringResource(R.string.student_number_required)) },
                        isError = state.studentNumberError != null,
                        supportingText = state.studentNumberError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.course,
                        onValueChange = viewModel::onCourseChange,
                        label = { Text(stringResource(R.string.course_required)) },
                        isError = state.courseError != null,
                        supportingText = state.courseError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.academicYear,
                        onValueChange = viewModel::onAcademicYearChange,
                        label = { Text(stringResource(R.string.academic_year_required)) },
                        isError = state.academicYearError != null,
                        supportingText = state.academicYearError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    // ── Instituição de Ensino (dropdown) ─────────────────────
                    Text(
                        stringResource(R.string.select_education_institution),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (state.isLoadingInstitutions) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(vertical = 8.dp),
                            color = Color.Gray
                        )
                    } else if (state.institutionsLoaded && state.availableInstitutions.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_institutions_available),
                            color = Color(0xFFB00020),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        val selectedInstitution = state.availableInstitutions.find {
                            it.id == state.selectedStudentInstitutionId
                        }
                        val institutionLabel = selectedInstitution?.displayName ?: ""

                        ExposedDropdownMenuBox(
                            expanded = institutionMenuExpanded,
                            onExpandedChange = { institutionMenuExpanded = !institutionMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = institutionLabel,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = institutionMenuExpanded) },
                                isError = state.studentInstitutionError != null,
                                supportingText = state.studentInstitutionError?.let {
                                    { Text(stringResource(it), color = Color(0xFFB00020)) }
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
                                            viewModel.onStudentInstitutionSelected(institution.id, institution.displayName)
                                            institutionMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "teacher" -> {
                    OutlinedTextField(
                        value = state.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        label = { Text(stringResource(R.string.name_required)) },
                        isError = state.firstNameError != null,
                        supportingText = state.firstNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        label = { Text(stringResource(R.string.last_name_required)) },
                        isError = state.lastNameError != null,
                        supportingText = state.lastNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.department,
                        onValueChange = viewModel::onDepartmentChange,
                        label = { Text(stringResource(R.string.department)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // ── Instituição associada (dropdown) ─────────────────────
                    Text(
                        stringResource(R.string.education_institution),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (state.isLoadingInstitutions) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(vertical = 8.dp),
                            color = Color.Gray
                        )
                    } else if (state.institutionsLoaded && state.availableInstitutions.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_institutions_available),
                            color = Color(0xFFB00020),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        val selectedInstitution = state.availableInstitutions.find {
                            it.id == state.institutionProfileId
                        }
                        val institutionLabel = selectedInstitution?.displayName ?: ""

                        ExposedDropdownMenuBox(
                            expanded = institutionMenuExpanded,
                            onExpandedChange = { institutionMenuExpanded = !institutionMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = institutionLabel,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = institutionMenuExpanded) },
                                isError = state.institutionError != null,
                                supportingText = state.institutionError?.let {
                                    { Text(stringResource(it), color = Color(0xFFB00020)) }
                                },
                                placeholder = { Text(stringResource(R.string.select_institution_placeholder)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedTextColor = Color.Black,
                                    unfocusedBorderColor = if (state.institutionError != null)
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
                                            viewModel.onInstitutionChange(institution.id)
                                            institutionMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "company" -> {
                    OutlinedTextField(
                        value = state.companyName,
                        onValueChange = viewModel::onCompanyNameChange,
                        label = { Text(stringResource(R.string.company_name_required)) },
                        isError = state.companyNameError != null,
                        supportingText = state.companyNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.nif,
                        onValueChange = viewModel::onNifChange,
                        label = { Text(stringResource(R.string.nif_required)) },
                        isError = state.nifError != null,
                        supportingText = state.nifError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.businessArea,
                        onValueChange = viewModel::onBusinessAreaChange,
                        label = { Text(stringResource(R.string.business_area)) },
                        isError = state.businessAreaError != null,
                        supportingText = state.businessAreaError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.location,
                        onValueChange = viewModel::onLocationChange,
                        label = { Text(stringResource(R.string.location_required)) },
                        isError = state.locationError != null,
                        supportingText = state.locationError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text(stringResource(R.string.description)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }

                "advisor" -> {
                    OutlinedTextField(
                        value = state.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        label = { Text(stringResource(R.string.name_required)) },
                        isError = state.firstNameError != null,
                        supportingText = state.firstNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        label = { Text(stringResource(R.string.last_name_required)) },
                        isError = state.lastNameError != null,
                        supportingText = state.lastNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.department,
                        onValueChange = viewModel::onDepartmentChange,
                        label = { Text(stringResource(R.string.department)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // ── Empresa associada (dropdown obrigatório) ──────────────
                    Text(
                        stringResource(R.string.company_required),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (state.isLoadingCompanies) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(vertical = 8.dp),
                            color = Color.Gray
                        )
                    } else if (state.availableCompanies.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_companies_available),
                            color = Color(0xFFB00020),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        var companyMenuExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = companyMenuExpanded,
                            onExpandedChange = { companyMenuExpanded = !companyMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = state.selectedCompanyName ?: "",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = companyMenuExpanded) },
                                isError = state.companyErrorRes != null,
                                supportingText = state.companyErrorRes?.let {
                                    { Text(stringResource(it), color = Color(0xFFB00020)) }
                                },
                                placeholder = { Text(stringResource(R.string.select_company_placeholder)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedTextColor = Color.Black,
                                    unfocusedBorderColor = if (state.companyErrorRes != null)
                                        Color(0xFFB00020) else Color(0xFFEDEDED),
                                    unfocusedTrailingIconColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = companyMenuExpanded,
                                onDismissRequest = { companyMenuExpanded = false },
                                modifier = Modifier.heightIn(max = 240.dp)
                            ) {
                                state.availableCompanies.forEach { company ->
                                    DropdownMenuItem(
                                        text = { Text(company.companyName) },
                                        onClick = {
                                            viewModel.onCompanyChange(
                                                company.effectiveId,
                                                company.companyName
                                            )
                                            companyMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "institution" -> {
                    OutlinedTextField(
                        value = state.institutionName,
                        onValueChange = viewModel::onInstitutionNameChange,
                        label = { Text(stringResource(R.string.institution_name_required)) },
                        isError = state.institutionNameError != null,
                        supportingText = state.institutionNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                "admin" -> {
                    OutlinedTextField(
                        value = state.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        label = { Text(stringResource(R.string.name_required)) },
                        isError = state.firstNameError != null,
                        supportingText = state.firstNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        label = { Text(stringResource(R.string.last_name_required)) },
                        isError = state.lastNameError != null,
                        supportingText = state.lastNameError?.let {
                            { Text(stringResource(it), color = Color(0xFFB00020)) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // Espaço extra para o conteúdo não ficar atrás da bottomBar
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}