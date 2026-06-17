package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.ui.components.ProfileAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    profileId: String,
    type: String,
    onBackClick: () -> Unit,
    viewModel: ProfileDetailViewModel = viewModel()
) {
    android.util.Log.d("ProfileDebug", "Screen received profileId=$profileId type=$type")
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(profileId, type) {
        viewModel.loadProfile(profileId, type)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Black
                    )
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        color = Color.Red
                    )
                }
                uiState.profile != null -> {
                    ProfileDetailContent(profile = uiState.profile!!)
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailContent(profile: ProfileDetailData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAvatar(
            name = profile.name,
            size = 124.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = profile.name,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = stringResource(R.string.email), value = profile.email.ifBlank { stringResource(R.string.not_specified) })

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE5E5E5))
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(label = stringResource(R.string.phone), value = profile.phone?.ifBlank { null } ?: stringResource(R.string.not_specified))

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE5E5E5))
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(label = stringResource(R.string.department), value = profile.department?.ifBlank { null } ?: stringResource(R.string.not_specified))

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE5E5E5))
                Spacer(modifier = Modifier.height(12.dp))

                val roleLabel = when (profile.roleType) {
                    "advisor" -> stringResource(R.string.role_advisor)
                    "teacher" -> stringResource(R.string.role_teacher)
                    else -> ""
                }
                InfoRow(label = stringResource(R.string.function_label), value = roleLabel)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}
