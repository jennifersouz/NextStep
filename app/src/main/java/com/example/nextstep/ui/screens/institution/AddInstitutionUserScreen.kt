package com.example.nextstep.ui.screens.institution

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun AddInstitutionUserScreen(
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: AddInstitutionUserViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val inviteCreatedMessage = stringResource(R.string.invite_created_success)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar(inviteCreatedMessage)
            onSuccess()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.register_user),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            UserTypeSelector(
                selectedType = state.selectedType,
                onTypeSelected = viewModel::onTypeSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            RegisterTextField(
                label = stringResource(R.string.name_required),
                value = state.firstName,
                onValueChange = viewModel::onFirstNameChange,
                placeholder = stringResource(R.string.name_placeholder),
                errorMessageRes = state.firstNameError
            )

            RegisterTextField(
                label = stringResource(R.string.last_name_required),
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = stringResource(R.string.last_name_placeholder),
                errorMessageRes = state.lastNameError
            )

            RegisterTextField(
                label = stringResource(R.string.email_required),
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = stringResource(R.string.email_placeholder),
                errorMessageRes = state.emailError,
                keyboardType = KeyboardType.Email
            )

            RegisterTextField(
                label = stringResource(R.string.phone),
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                placeholder = stringResource(R.string.phone_placeholder),
                errorMessageRes = state.phoneError,
                keyboardType = KeyboardType.Phone
            )

            if (state.selectedType == UserType.STUDENT) {
                RegisterTextField(
                    label = stringResource(R.string.student_number),
                    value = state.studentNumber,
                    onValueChange = viewModel::onStudentNumberChange,
                    placeholder = stringResource(R.string.student_number_placeholder),
                    errorMessageRes = state.studentNumberError,
                    keyboardType = KeyboardType.Number
                )

                RegisterTextField(
                    label = stringResource(R.string.course),
                    value = state.course,
                    onValueChange = viewModel::onCourseChange,
                    placeholder = stringResource(R.string.course_placeholder),
                    errorMessageRes = state.courseError
                )

                RegisterTextField(
                    label = stringResource(R.string.year),
                    value = state.academicYear,
                    onValueChange = viewModel::onAcademicYearChange,
                    placeholder = stringResource(R.string.year_placeholder),
                    errorMessageRes = state.academicYearError,
                    keyboardType = KeyboardType.Number
                )
            }

            if (state.selectedType == UserType.TEACHER) {
                RegisterTextField(
                    label = stringResource(R.string.department),
                    value = state.department,
                    onValueChange = viewModel::onDepartmentChange,
                    placeholder = stringResource(R.string.department_placeholder),
                    errorMessageRes = state.departmentError
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::createInvite,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                ),
                enabled = !state.isLoading
            ) {
                Text(
                    text = if (state.isLoading) {
                        stringResource(R.string.sending)
                    } else {
                        stringResource(R.string.register_user)
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun UserTypeSelector(
    selectedType: UserType,
    onTypeSelected: (UserType) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.user_type_required),
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        UserTypeButton(
            text = stringResource(R.string.student),
            isSelected = selectedType == UserType.STUDENT,
            onClick = { onTypeSelected(UserType.STUDENT) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        UserTypeButton(
            text = stringResource(R.string.teacher),
            isSelected = selectedType == UserType.TEACHER,
            onClick = { onTypeSelected(UserType.TEACHER) }
        )
    }
}

@Composable
fun UserTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                Color(0xFFFDFA52)
            } else {
                Color(0xFFE5E5E5)
            },
            contentColor = Color.Black
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}

@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessageRes: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text
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
            errorMessageRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = Color(0xFFB00020)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
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

enum class UserType {
    STUDENT,
    TEACHER
}