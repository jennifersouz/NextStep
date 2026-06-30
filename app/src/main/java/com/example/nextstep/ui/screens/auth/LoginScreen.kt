package com.example.nextstep.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.nextstep.data.local.LanguageManager
import com.example.nextstep.ui.components.AuthResponsiveLayout
import com.example.nextstep.ui.components.isLandscape

// Modelo para as opções de idioma
data class LanguageOption(
    val code: String,
    val displayCode: String,
    val flag: String
)

// Função auxiliar para encontrar a Activity de forma segura no Compose
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun LoginScreen(
    onLoginClick: (UserRole) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val viewModel: AuthViewModel = viewModel()
    val state by viewModel.loginState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val landscape = isLandscape()

    if (landscape) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                LoginLanguageSelector(
                    expanded = languageMenuExpanded,
                    onExpandChange = { languageMenuExpanded = it },
                    activity = activity
                )
            }

            AuthResponsiveLayout(
                modifier = Modifier.weight(1f),
                headerContent = {
                    LoginHeader()
                },
                formContent = {
                    LoginForm(
                        state = state,
                        passwordVisible = passwordVisible,
                        onPasswordVisibleChange = { passwordVisible = it },
                        onEmailChange = viewModel::onLoginEmailChange,
                        onPasswordChange = viewModel::onLoginPasswordChange,
                        onRegisterClick = onRegisterClick,
                        onLoginClick = { viewModel.login(onSuccess = onLoginClick) }
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
                .padding(horizontal = 28.dp, vertical = 48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LoginLanguageSelector(
                    expanded = languageMenuExpanded,
                    onExpandChange = { languageMenuExpanded = it },
                    activity = activity
                )
            }

            Spacer(modifier = Modifier.height(70.dp))
            LoginHeader()
            Spacer(modifier = Modifier.height(76.dp))

            LoginForm(
                state = state,
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = { passwordVisible = it },
                onEmailChange = viewModel::onLoginEmailChange,
                onPasswordChange = viewModel::onLoginPasswordChange,
                onRegisterClick = onRegisterClick,
                onLoginClick = { viewModel.login(onSuccess = onLoginClick) }
            )
        }
    }
}

@Composable
private fun LoginLanguageSelector(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    activity: Activity?
) {
    val languages = listOf(
        LanguageOption(code = "pt", displayCode = "PT", flag = "🇵🇹"),
        LanguageOption(code = "en", displayCode = "EN", flag = "🇬🇧")
    )

    Box {
        Row(
            modifier = Modifier
                .clickable { onExpandChange(true) }
                .background(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = stringResource(R.string.change_language),
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .width(100.dp)
                .background(Color.White)
        ) {
            languages.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = option.flag, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option.displayCode,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    },
                    onClick = {
                        onExpandChange(false)
                        LanguageManager.changeLanguage(option.code)
                        activity?.recreate()
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_welcome_back),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoginForm(
    state: LoginUiState,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.email_required),
        fontSize = 18.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChange,
        placeholder = {
            Text(
                text = stringResource(R.string.email_placeholder),
                color = Color(0xFF8A8A8A)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            autoCorrectEnabled = false
        ),
        isError = state.emailError != null,
        supportingText = {
            state.emailError?.let {
                Text(
                    text = stringResource(it),
                    color = Color(0xFFB00020)
                )
            }
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

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = stringResource(R.string.password_required),
        fontSize = 18.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = state.password,
        onValueChange = onPasswordChange,
        placeholder = {
            Text(
                text = stringResource(R.string.password_placeholder),
                color = Color(0xFF8A8A8A)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),
        isError = state.passwordError != null,
        supportingText = {
            state.passwordError?.let {
                Text(
                    text = stringResource(it),
                    color = Color(0xFFB00020)
                )
            }
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = { onPasswordVisibleChange(!passwordVisible) }
            ) {
                Icon(
                    imageVector = if (passwordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = stringResource(R.string.toggle_password_visibility),
                    tint = Color(0xFF6B7280)
                )
            }
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

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = stringResource(R.string.no_account),
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = stringResource(R.string.create_account),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }

    Spacer(modifier = Modifier.height(22.dp))

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

    Button(
        onClick = onLoginClick,
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
            text = stringResource(R.string.continue_button),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
