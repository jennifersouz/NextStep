package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyAdvisorDto

@Composable
fun CompanyAdvisorDetailScreen(
    advisorId: String,
    onBackClick: () -> Unit,
    onAdvisorDeleted: () -> Unit,
    viewModel: CompanyAdvisorDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(advisorId) {
        viewModel.loadAdvisor(advisorId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text(text = stringResource(R.string.delete_advisor))
            },
            text = {
                Text(text = stringResource(R.string.delete_advisor_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAdvisor(
                            advisorId = advisorId,
                            onSuccess = onAdvisorDeleted
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_advisor),
                        color = Color(0xFFB00020),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }

        state.errorMessageRes != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    textAlign = TextAlign.Center
                )
            }
        }

        state.advisor != null -> {
            CompanyAdvisorDetailContent(
                advisor = state.advisor!!,
                isDeleting = state.isDeleting,
                onBackClick = onBackClick,
                onDeleteClick = {
                    showDeleteDialog = true
                }
            )
        }
    }
}

@Composable
private fun CompanyAdvisorDetailContent(
    advisor: CompanyAdvisorDto,
    isDeleting: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            CompanyAdvisorAvatar(
                name = advisor.name,
                size = 116
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = advisor.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(52.dp))

            AdvisorInfoBlock(
                label = stringResource(R.string.advisor_email),
                value = advisor.email
            )

            Spacer(modifier = Modifier.height(22.dp))

            AdvisorInfoBlock(
                label = stringResource(R.string.advisor_phone),
                value = advisor.phone.orEmpty().ifBlank {
                    stringResource(R.string.not_available)
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            AdvisorInfoBlock(
                label = stringResource(R.string.advisor_department),
                value = advisor.department.orEmpty().ifBlank {
                    stringResource(R.string.not_available)
                }
            )

            Spacer(modifier = Modifier.height(46.dp))

            OutlinedButton(
                onClick = onDeleteClick,
                enabled = !isDeleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFB00020)
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.delete_advisor),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AdvisorInfoBlock(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            color = Color(0xFF8A8A8A),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = Color.Black,
            fontSize = 15.sp,
            lineHeight = 21.sp
        )
    }
}