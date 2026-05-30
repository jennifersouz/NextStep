package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.StudentSubmittedApplicationDto

@Composable
fun StudentSubmittedApplicationsScreen(
    onBackClick: () -> Unit,
    onApplicationClick: (String) -> Unit,
    viewModel: StudentSubmittedApplicationsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        StudentSubmittedApplicationsHeader(
            onBackClick = onBackClick
        )

        when {
            state.isLoading -> {
                StudentSubmittedApplicationsLoadingState()
            }

            state.errorMessageRes != null -> {
                val errorRes = state.errorMessageRes

                StudentSubmittedApplicationsErrorState(
                    message = if (errorRes != null) {
                        stringResource(errorRes)
                    } else {
                        stringResource(R.string.student_submitted_applications_load_error)
                    },
                    onRetryClick = viewModel::loadApplications
                )
            }

            state.applications.isEmpty() -> {
                StudentSubmittedApplicationsEmptyState()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(
                        top = 22.dp,
                        bottom = 28.dp
                    )
                ) {
                    items(
                        items = state.applications,
                        key = { application ->
                            application.id
                        }
                    ) { application ->
                        StudentSubmittedApplicationCard(
                            application = application,
                            onClick = {
                                onApplicationClick(application.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StudentSubmittedApplicationsHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp,
                end = 16.dp,
                top = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
        }

        Text(
            text = stringResource(R.string.submitted_applications),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun StudentSubmittedApplicationCard(
    application: StudentSubmittedApplicationDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StudentSubmittedApplicationCompanyLogo(
            companyName = application.companyName
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = application.companyName,
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A),
                maxLines = 1
            )

            Text(
                text = application.offerTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1
            )

            Text(
                text = formatSubmittedApplicationDate(application.createdAt),
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = submittedApplicationStatusLabel(application.status),
            fontSize = 12.sp,
            color = submittedApplicationStatusColor(application.status),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun StudentSubmittedApplicationCompanyLogo(
    companyName: String
) {
    val initials = companyName
        .split(" ")
        .filter { part ->
            part.isNotBlank()
        }
        .take(2)
        .joinToString("") { part ->
            part.first().uppercase()
        }
        .ifBlank {
            "?"
        }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFE8392A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StudentSubmittedApplicationsLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Black
        )
    }
}

@Composable
fun StudentSubmittedApplicationsErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(180.dp))

        Text(
            text = message,
            color = Color(0xFFB00020),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.try_again),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onRetryClick()
            }
        )
    }
}

@Composable
fun StudentSubmittedApplicationsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.student_no_submitted_applications),
            color = Color(0xFF8A8A8A),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

fun submittedApplicationStatusLabel(status: String): String {
    return when (status) {
        "accepted" -> "Aceite"
        "rejected" -> "Recusada"
        else -> "Pendente"
    }
}

fun submittedApplicationStatusColor(status: String): Color {
    return when (status) {
        "accepted" -> Color(0xFF2E7D32)
        "rejected" -> Color(0xFFB00020)
        else -> Color(0xFF666666)
    }
}

fun formatSubmittedApplicationDate(rawDate: String?): String {
    if (rawDate.isNullOrBlank()) {
        return ""
    }

    val datePart = rawDate.take(10)
    val parts = datePart.split("-")

    if (parts.size != 3) {
        return datePart
    }

    val year = parts[0]
    val month = parts[1].toIntOrNull()
    val day = parts[2].toIntOrNull()

    if (month == null || day == null) {
        return datePart
    }

    val monthName = when (month) {
        1 -> "janeiro"
        2 -> "fevereiro"
        3 -> "março"
        4 -> "abril"
        5 -> "maio"
        6 -> "junho"
        7 -> "julho"
        8 -> "agosto"
        9 -> "setembro"
        10 -> "outubro"
        11 -> "novembro"
        12 -> "dezembro"
        else -> return datePart
    }

    return "$day de $monthName de $year"
}