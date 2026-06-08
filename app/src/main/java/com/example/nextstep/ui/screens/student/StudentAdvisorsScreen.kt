package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.StudentAvailableAdvisorDto

@Composable
fun StudentAdvisorsScreen(
    onBackClick: () -> Unit,
    viewModel: StudentAdvisorsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

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

        else -> {
            StudentAdvisorsContent(
                state = state,
                onBackClick = onBackClick,
                onSearchChange = viewModel::onSearchChange,
                onSendRequest = viewModel::sendRequest
            )
        }
    }
}

@Composable
private fun StudentAdvisorsContent(
    state: StudentAdvisorsUiState,
    onBackClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onSendRequest: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 36.dp,
            bottom = 110.dp
        )
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.Black
                    )
                }

                Text(
                    text = stringResource(R.string.advisors),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search),
                        color = Color(0xFF8A8A8A)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF8A8A8A)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF4F4F4),
                    unfocusedContainerColor = Color(0xFFF4F4F4)
                )
            )

            if (state.errorMessageRes != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(state.errorMessageRes),
                    color = Color(0xFFB00020),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.filteredAdvisors.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_advisors_available),
                    color = Color(0xFF8A8A8A),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 40.dp)
                )
            }
        } else {
            items(
                items = state.filteredAdvisors,
                key = { advisor ->
                    advisor.advisorProfileId
                }
            ) { advisor ->
                StudentAdvisorRow(
                    advisor = advisor,
                    isSending = state.sendingAdvisorId == advisor.advisorProfileId,
                    onSendRequest = {
                        onSendRequest(advisor.advisorProfileId)
                    }
                )

                Spacer(modifier = Modifier.height(22.dp))
            }
        }
    }
}

@Composable
private fun StudentAdvisorRow(
    advisor: StudentAvailableAdvisorDto,
    isSending: Boolean,
    onSendRequest: () -> Unit
) {
    val hasRequest = !advisor.requestStatus.isNullOrBlank()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdvisorCircleAvatar(
            name = advisor.name
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = advisor.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!advisor.department.isNullOrBlank()) {
                Text(
                    text = advisor.department,
                    fontSize = 13.sp,
                    color = Color(0xFF8A8A8A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Button(
            onClick = onSendRequest,
            enabled = !hasRequest && !isSending,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDFA52),
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFFEAEAEA),
                disabledContentColor = Color(0xFF777777)
            )
        ) {
            Text(
                text = when {
                    isSending -> stringResource(R.string.sending)
                    advisor.requestStatus == "pending" -> stringResource(R.string.pending)
                    advisor.requestStatus == "accepted" -> stringResource(R.string.accepted)
                    advisor.requestStatus == "rejected" -> stringResource(R.string.rejected)
                    else -> stringResource(R.string.send_request)
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AdvisorCircleAvatar(
    name: String
) {
    val initials = name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFFFDFA52))
            .border(
                width = 1.dp,
                color = Color(0xFFE1E1E1),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }
}