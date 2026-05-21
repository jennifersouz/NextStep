package com.example.nextstep.ui.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.registerState.collectAsState()
    var roleMenuExpanded by remember { mutableStateOf(false) }

    val roleLabel = when (state.selectedRole) {
        UserRole.STUDENT -> stringResource(R.string.role_student)
        UserRole.COMPANY -> stringResource(R.string.role_company)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 28.dp, vertical = 48.dp)
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

        Spacer(modifier = Modifier.height(36.dp))

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
                        roleMenuExpanded = true
                    },
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.clickable {
                            roleMenuExpanded = true
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
                    roleMenuExpanded = false
                },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                RoleDropdownItem(
                    text = stringResource(R.string.role_student),
                    onClick = {
                        viewModel.onRoleChange(UserRole.STUDENT)
                        roleMenuExpanded = false
                    }
                )

                RoleDropdownItem(
                    text = stringResource(R.string.role_company),
                    onClick = {
                        viewModel.onRoleChange(UserRole.COMPANY)
                        roleMenuExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        when (state.selectedRole) {
            UserRole.STUDENT -> {
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
        }

        RegisterTextField(
            label = stringResource(R.string.email_required),
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = stringResource(R.string.email_placeholder),
            errorMessageRes = state.emailError
        )

        RegisterTextField(
            label = stringResource(R.string.password_required),
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = stringResource(R.string.password_placeholder),
            isPassword = true,
            errorMessageRes = state.passwordError
        )

        RegisterTextField(
            label = stringResource(R.string.confirm_password_required),
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            placeholder = stringResource(R.string.confirm_password_placeholder),
            isPassword = true,
            errorMessageRes = state.confirmPasswordError
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
                if (viewModel.validateRegister()) {
                    onRegisterClick()
                }
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
}

@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    errorMessageRes: Int? = null
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