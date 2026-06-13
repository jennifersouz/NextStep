package com.example.nextstep.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
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
        // Landscape: Row com header à esquerda e formulário à direita
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
        ) {
            // Seletor de idioma no topo direito
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
        // Portrait: layout original em coluna com scroll
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
    Box {
        Row(
            modifier = Modifier
                .clickable { onExpandChange(true) }
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = stringResource(R.string.change_language),
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.width(70.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.language_code_pt),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onClick = {
                    onExpandChange(false)
                    LanguageManager.changeLanguage("pt")
                    activity?.recreate()
                },
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.language_code_en),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onClick = {
                    onExpandChange(false)
                    LanguageManager.changeLanguage("en")
                    activity?.recreate()
                },
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            )
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
