package com.example.nextstep.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.ui.components.AuthResponsiveLayout
import com.example.nextstep.ui.components.isLandscape

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.registerState.collectAsState()
    var roleMenuExpanded by remember { mutableStateOf(false) }

    val landscape = isLandscape()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.register_success)

    LaunchedEffect(state.isRegisterSuccess) {
        if (state.isRegisterSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            onRegisterSuccess()
        }
    }

    val roleLabel = when (state.selectedRole) {
        UserRole.STUDENT -> stringResource(R.string.role_student)
        UserRole.COMPANY -> stringResource(R.string.role_company)
        UserRole.ADVISOR -> stringResource(R.string.role_advisor)
        UserRole.INSTITUTION -> stringResource(R.string.role_institution)
        UserRole.TEACHER -> stringResource(R.string.role_teacher)
        UserRole.EMPLOYEE -> stringResource(R.string.role_employee)
        UserRole.ADMIN -> stringResource(R.string.role_admin)
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    ) { innerPadding ->
        if (landscape) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(innerPadding)
            ) {
                AuthResponsiveLayout(
                    modifier = Modifier.weight(1f),
                    headerContent = {
                        RegisterHeader()
                    },
                    formContent = {
                        RegisterForm(
                            state = state,
                            roleLabel = roleLabel,
                            roleMenuExpanded = roleMenuExpanded,
                            onRoleMenuExpandChange = { roleMenuExpanded = it },
                            viewModel = viewModel,
                            onLoginClick = onLoginClick
                        )
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(innerPadding)
                    .padding(horizontal = 28.dp, vertical = 48.dp)
            ) {
                RegisterHeader()

                Spacer(modifier = Modifier.height(36.dp))

                RegisterForm(
                    state = state,
                    roleLabel = roleLabel,
                    roleMenuExpanded = roleMenuExpanded,
                    onRoleMenuExpandChange = { roleMenuExpanded = it },
                    viewModel = viewModel,
                    onLoginClick = onLoginClick
                )
            }
        }
    }
}

@Composable
private fun RegisterHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.create_account_title),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.create_account_subtitle),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 17.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RegisterForm(
    state: RegisterUiState,
    roleLabel: String,
    roleMenuExpanded: Boolean,
    onRoleMenuExpandChange: (Boolean) -> Unit,
    viewModel: AuthViewModel,
    onLoginClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.user_type_required),
        fontSize = 18.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column {
        OutlinedTextField(
            value = roleLabel,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onRoleMenuExpandChange(true)
                },
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.clickable {
                        onRoleMenuExpandChange(true)
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD9D9D9),
                unfocusedBorderColor = Color(0xFFD9D9D9),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        DropdownMenu(
            expanded = roleMenuExpanded,
            onDismissRequest = {
                onRoleMenuExpandChange(false)
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            RoleDropdownItem(
                text = stringResource(R.string.role_student),
                onClick = {
                    viewModel.onRoleChange(UserRole.STUDENT)
                    onRoleMenuExpandChange(false)
                }
            )

            RoleDropdownItem(
                text = stringResource(R.string.role_company),
                onClick = {
                    viewModel.onRoleChange(UserRole.COMPANY)
                    onRoleMenuExpandChange(false)
                }
            )

            RoleDropdownItem(
                text = stringResource(R.string.role_advisor),
                onClick = {
                    viewModel.onRoleChange(UserRole.ADVISOR)
                    onRoleMenuExpandChange(false)
                }
            )

            RoleDropdownItem(
                text = stringResource(R.string.role_institution),
                onClick = {
                    viewModel.onRoleChange(UserRole.INSTITUTION)
                    onRoleMenuExpandChange(false)
                }
            )

            RoleDropdownItem(
                text = stringResource(R.string.role_teacher),
                onClick = {
                    viewModel.onRoleChange(UserRole.TEACHER)
                    onRoleMenuExpandChange(false)
                }
            )

            RoleDropdownItem(
                text = stringResource(R.string.role_employee),
                onClick = {
                    viewModel.onRoleChange(UserRole.EMPLOYEE)
                    onRoleMenuExpandChange(false)
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    when (state.selectedRole) {
        UserRole.STUDENT -> {
            StudentRegisterInfoBox()

            RegisterTextField(
                label = stringResource(R.string.name_required),
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = stringResource(R.string.name_placeholder),
                errorMessageRes = state.nameError
            )

            RegisterTextField(
                label = stringResource(R.string.last_name_required),
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = stringResource(R.string.last_name_placeholder),
                errorMessageRes = state.lastNameError
            )

            RegisterTextField(
                label = stringResource(R.string.student_number_required),
                value = state.studentNumber,
                onValueChange = viewModel::onStudentNumberChange,
                placeholder = stringResource(R.string.student_number_placeholder),
                errorMessageRes = state.studentNumberError
            )

            RegisterTextField(
                label = stringResource(R.string.course_required),
                value = state.course,
                onValueChange = viewModel::onCourseChange,
                placeholder = stringResource(R.string.course_placeholder),
                errorMessageRes = state.courseError
            )

            RegisterTextField(
                label = stringResource(R.string.year_required),
                value = state.year,
                onValueChange = viewModel::onYearChange,
                placeholder = stringResource(R.string.year_placeholder),
                errorMessageRes = state.yearError
            )
        }

        UserRole.COMPANY -> {
            RegisterTextField(
                label = stringResource(R.string.company_name_required),
                value = state.companyName,
                onValueChange = viewModel::onCompanyNameChange,
                placeholder = stringResource(R.string.company_name_placeholder),
                errorMessageRes = state.companyNameError
            )

            RegisterTextField(
                label = stringResource(R.string.nif_required),
                value = state.nif,
                onValueChange = viewModel::onNifChange,
                placeholder = stringResource(R.string.nif_placeholder),
                errorMessageRes = state.nifError
            )

            RegisterTextField(
                label = stringResource(R.string.area_required),
                value = state.area,
                onValueChange = viewModel::onAreaChange,
                placeholder = stringResource(R.string.area_placeholder),
                errorMessageRes = state.areaError
            )

            RegisterTextField(
                label = stringResource(R.string.location_required),
                value = state.location,
                onValueChange = viewModel::onLocationChange,
                placeholder = stringResource(R.string.location_placeholder),
                errorMessageRes = state.locationError
            )
        }

        UserRole.ADVISOR -> {
            AdvisorRegisterInfoBox()
        }

        UserRole.INSTITUTION -> {
            RegisterTextField(
                label = stringResource(R.string.institution_name_required),
                value = state.institutionName,
                onValueChange = viewModel::onInstitutionNameChange,
                placeholder = stringResource(R.string.institution_name_placeholder),
                errorMessageRes = state.institutionNameError
            )

            RegisterTextField(
                label = stringResource(R.string.nif_required),
                value = state.institutionNif,
                onValueChange = viewModel::onInstitutionNifChange,
                placeholder = stringResource(R.string.nif_placeholder),
                errorMessageRes = state.institutionNifError
            )

            RegisterTextField(
                label = stringResource(R.string.locality),
                value = state.institutionLocality,
                onValueChange = viewModel::onInstitutionLocalityChange,
                placeholder = stringResource(R.string.location_placeholder),
                errorMessageRes = state.institutionLocalityError
            )

            RegisterTextField(
                label = stringResource(R.string.address),
                value = state.institutionAddress,
                onValueChange = viewModel::onInstitutionAddressChange,
                placeholder = stringResource(R.string.location_placeholder),
                errorMessageRes = state.institutionAddressError
            )

            RegisterTextField(
                label = stringResource(R.string.phone),
                value = state.institutionPhone,
                onValueChange = viewModel::onInstitutionPhoneChange,
                placeholder = stringResource(R.string.phone_placeholder),
                errorMessageRes = state.institutionPhoneError
            )
        }

        UserRole.TEACHER -> {
            TeacherRegisterInfoBox()

            RegisterTextField(
                label = stringResource(R.string.name_required),
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = stringResource(R.string.name_placeholder),
                errorMessageRes = state.nameError
            )

            RegisterTextField(
                label = stringResource(R.string.last_name_required),
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = stringResource(R.string.last_name_placeholder),
                errorMessageRes = state.lastNameError
            )

            RegisterTextField(
                label = stringResource(R.string.department_required),
                value = state.teacherDepartment,
                onValueChange = viewModel::onTeacherDepartmentChange,
                placeholder = stringResource(R.string.department_placeholder),
                errorMessageRes = state.teacherDepartmentError
            )

            RegisterTextField(
                label = stringResource(R.string.phone),
                value = state.teacherPhone,
                onValueChange = viewModel::onTeacherPhoneChange,
                placeholder = stringResource(R.string.phone_placeholder),
                errorMessageRes = state.teacherPhoneError
            )
        }

        UserRole.EMPLOYEE -> {
            EmployeeRegisterInfoBox()

            RegisterTextField(
                label = stringResource(R.string.name_required),
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = stringResource(R.string.name_placeholder),
                errorMessageRes = state.nameError
            )

            RegisterTextField(
                label = stringResource(R.string.last_name_required),
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = stringResource(R.string.last_name_placeholder),
                errorMessageRes = state.lastNameError
            )

            RegisterTextField(
                label = stringResource(R.string.department_required),
                value = state.teacherDepartment,
                onValueChange = viewModel::onTeacherDepartmentChange,
                placeholder = stringResource(R.string.department_placeholder),
                errorMessageRes = state.teacherDepartmentError
            )

            RegisterTextField(
                label = stringResource(R.string.phone),
                value = state.teacherPhone,
                onValueChange = viewModel::onTeacherPhoneChange,
                placeholder = stringResource(R.string.phone_placeholder),
                errorMessageRes = state.teacherPhoneError
            )
        }

        UserRole.ADMIN -> {
            // Admin registration is not allowed from the UI
        }
    }

    RegisterTextField(
        label = stringResource(R.string.email_required),
        value = state.email,
        onValueChange = viewModel::onEmailChange,
        placeholder = stringResource(R.string.email_placeholder),
        errorMessageRes = state.emailError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            autoCorrectEnabled = false
        )
    )

    RegisterTextField(
        label = stringResource(R.string.password_required),
        value = state.password,
        onValueChange = viewModel::onPasswordChange,
        placeholder = stringResource(R.string.password_placeholder),
        isPassword = true,
        errorMessageRes = state.passwordError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        )
    )

    RegisterTextField(
        label = stringResource(R.string.confirm_password_required),
        value = state.confirmPassword,
        onValueChange = viewModel::onConfirmPasswordChange,
        placeholder = stringResource(R.string.confirm_password_placeholder),
        isPassword = true,
        errorMessageRes = state.confirmPasswordError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        )
    )

    state.generalError?.let { errorRes ->
        Text(
            text = stringResource(errorRes),
            color = Color(0xFFB00020),
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Button(
        onClick = {
            viewModel.register()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFDFA52),
            contentColor = Color.Black
        )
    ) {
        Text(
            text = stringResource(R.string.create_account_button),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.already_have_account),
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = stringResource(R.string.login),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.clickable {
                onLoginClick()
            }
        )
    }
}

@Composable
private fun StudentRegisterInfoBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFFDE8),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = stringResource(R.string.student_register_title),
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.student_invite_required_info),
            color = Color(0xFF6B7280),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun AdvisorRegisterInfoBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFFDE8),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = stringResource(R.string.advisor_register_title),
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.advisor_register_description),
            color = Color(0xFF6B7280),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun TeacherRegisterInfoBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFFDE8),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = stringResource(R.string.teacher_register_title),
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.teacher_register_description),
            color = Color(0xFF6B7280),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun EmployeeRegisterInfoBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFFDE8),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = stringResource(R.string.employee_register_title),
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.employee_register_description),
            color = Color(0xFF6B7280),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    errorMessageRes: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Text(
        text = label,
        fontSize = 17.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF8A8A8A)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        isError = errorMessageRes != null,
        supportingText = {
            errorMessageRes?.let {
                Text(
                    text = stringResource(it),
                    color = Color(0xFFB00020)
                )
            }
        },
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD9D9D9),
            unfocusedBorderColor = Color(0xFFD9D9D9),
            errorBorderColor = Color(0xFFB00020),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
fun RoleDropdownItem(
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        onClick = onClick,
        modifier = Modifier.height(42.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    )
}