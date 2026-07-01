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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminProfileDto

@Composable
fun AdminCreateEditUserScreen(
    existingProfile: AdminProfileDto? = null,
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
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var roleError by remember { mutableStateOf<String?>(null) }

    val roleOptions = listOf(
        "student" to stringResource(R.string.role_student),
        "teacher" to stringResource(R.string.role_teacher),
        "company" to stringResource(R.string.role_company),
        "advisor" to stringResource(R.string.role_advisor),
        "institution" to stringResource(R.string.role_institution),
        "admin" to stringResource(R.string.role_admin)
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
                    contentDescription = stringResource(R.string.back_label),
                    tint = Color.Black
                )
            }

            Text(
                text = if (isEditing) stringResource(R.string.edit_user_title) else stringResource(R.string.create_user_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        if (!isEditing) {
            Text(
                text = stringResource(R.string.note_account_creation),
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
                    firstName = it.filter { c -> c.isLetter() || c.isWhitespace() }
                    firstNameError = null
                },
                label = { Text(stringResource(R.string.name_required)) },
                placeholder = { Text(stringResource(R.string.name_placeholder_text)) },
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
                onValueChange = {
                    lastName = it.filter { c -> c.isLetter() || c.isWhitespace() }
                    lastNameError = null
                },
                label = { Text(stringResource(R.string.last_name_required)) },
                placeholder = { Text(stringResource(R.string.last_name_placeholder_text)) },
                isError = lastNameError != null,
                supportingText = lastNameError?.let { { Text(it, color = Color(0xFFB00020)) } },
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
            if (isEditing) {
                OutlinedTextField(
                    value = existingProfile?.email ?: "",
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

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it.filter { c -> c.isDigit() }.take(9)
                    phoneError = null
                },
                isError = phoneError != null,
                supportingText = phoneError?.let { { Text(it, color = Color(0xFFB00020)) } },
                label = { Text(stringResource(R.string.phone)) },
                placeholder = { Text(stringResource(R.string.phone_placeholder_text)) },
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
                text = stringResource(R.string.function_label),
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
                            contentDescription = stringResource(R.string.select),
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
                text = stringResource(
                    R.string.account_status_format,
                    stringResource(if (isActive) R.string.active_status_label else R.string.inactive_status_label)
                ),
                fontSize = 14.sp,
                color = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            val nameRequiredError = stringResource(R.string.name_required_error)
            val lastNameRequiredError = stringResource(R.string.last_name_required_error)
            val onlyLettersError = stringResource(R.string.error_only_letters)
            val nameTooShortError = stringResource(R.string.error_name_too_short)
            val phoneLengthError = stringResource(R.string.error_phone_length)

            // Save button
            Button(
                onClick = {
                    var hasError = false
                    if (firstName.isBlank()) {
                        firstNameError = nameRequiredError
                        hasError = true
                    } else if (firstName.length < 2) {
                        firstNameError = nameTooShortError
                        hasError = true
                    } else if (!firstName.all { it.isLetter() || it.isWhitespace() }) {
                        firstNameError = onlyLettersError
                        hasError = true
                    }
                    if (lastName.isBlank() && selectedRole != "company") {
                        lastNameError = lastNameRequiredError
                        hasError = true
                    } else if (selectedRole != "company" && lastName.length < 2) {
                        lastNameError = nameTooShortError
                        hasError = true
                    } else if (selectedRole != "company" && !lastName.all { it.isLetter() || it.isWhitespace() }) {
                        lastNameError = onlyLettersError
                        hasError = true
                    }
                    if (phone.isNotBlank() && phone.length != 9) {
                        phoneError = phoneLengthError
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
                    text = if (isEditing) stringResource(R.string.save_changes) else stringResource(R.string.create_user_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}