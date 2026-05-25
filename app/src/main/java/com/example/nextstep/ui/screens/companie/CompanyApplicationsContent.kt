package com.example.nextstep.ui.screens.company

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.nextstep.data.model.CompanyApplicationDto

@Composable
fun CompanyApplicationsContent(
    viewModel: CompanyApplicationsViewModel = viewModel()
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
                CircularProgressIndicator(
                    color = Color.Black
                )
            }
        }

        state.errorMessageRes != null -> {
            val errorRes = state.errorMessageRes

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (errorRes != null) {
                    Text(
                        text = stringResource(errorRes),
                        color = Color(0xFFB00020),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        state.applications.isEmpty() -> {
            CompanyApplicationsEmptyState()
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(
                    top = 28.dp,
                    bottom = 28.dp
                )
            ) {
                item {
                    Text(
                        text = stringResource(R.string.company_applications_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(34.dp))
                }

                item {
                    CompanyApplicationsSectionTitle(
                        title = stringResource(R.string.company_applications_unseen)
                    )
                }

                if (state.unseenApplications.isEmpty()) {
                    item {
                        CompanyApplicationsSmallEmptyMessage(
                            message = stringResource(R.string.company_no_unseen_applications)
                        )
                    }
                } else {
                    items(
                        items = state.unseenApplications,
                        key = { application ->
                            application.id
                        }
                    ) { application ->
                        CompanyApplicationCard(
                            application = application,
                            onClick = {
                                viewModel.markAsViewed(application.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(44.dp))

                    CompanyApplicationsSectionTitle(
                        title = stringResource(R.string.company_applications_seen)
                    )
                }

                if (state.seenApplications.isEmpty()) {
                    item {
                        CompanyApplicationsSmallEmptyMessage(
                            message = stringResource(R.string.company_no_seen_applications)
                        )
                    }
                } else {
                    items(
                        items = state.seenApplications,
                        key = { application ->
                            application.id
                        }
                    ) { application ->
                        CompanyApplicationCard(
                            application = application,
                            onClick = {
                                // Próximo passo: abrir detalhe da candidatura.
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyApplicationsSectionTitle(
    title: String
) {
    Text(
        text = title,
        fontSize = 15.sp,
        color = Color(0xFF8A8A8A)
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun CompanyApplicationCard(
    application: CompanyApplicationDto,
    onClick: () -> Unit
) {
    val studentName = "${application.firstName} ${application.lastName}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
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
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = application.offerTitle,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompanyApplicationStudentAvatar(
                studentName = studentName
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = studentName,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                val course = application.course

                if (!course.isNullOrBlank()) {
                    Text(
                        text = course,
                        fontSize = 12.sp,
                        color = Color(0xFF8A8A8A)
                    )
                }
            }
        }
    }
}

@Composable
fun CompanyApplicationStudentAvatar(
    studentName: String
) {
    val initials = studentName
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
            .size(30.dp)
            .clip(CircleShape)
            .background(Color(0xFF2B2B2B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CompanyApplicationsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.company_no_applications),
            color = Color(0xFF8A8A8A),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CompanyApplicationsSmallEmptyMessage(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = message,
            color = Color(0xFF8A8A8A),
            fontSize = 14.sp
        )
    }
}