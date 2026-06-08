package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R

@Composable
fun StudentAssignedAdvisorSection(
    applicationId: String,
    advisorName: String? = null,
    advisorEmail: String? = null,
    advisorPhone: String? = null,
    advisorDepartment: String? = null,
    viewModel: StudentAssignedAdvisorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val hasPreloadedAdvisor = !advisorName.isNullOrBlank()

    LaunchedEffect(applicationId) {
        if (!hasPreloadedAdvisor) {
            viewModel.loadAssignedAdvisor(applicationId)
        }
    }

    val displayName = advisorName?.takeIf { it.isNotBlank() }
        ?: state.assignedAdvisor?.advisorName
    val displayEmail = advisorEmail?.takeIf { it.isNotBlank() }
        ?: state.assignedAdvisor?.advisorEmail
    val displayPhone = advisorPhone?.takeIf { it.isNotBlank() }
        ?: state.assignedAdvisor?.advisorPhone
    val displayDepartment = advisorDepartment?.takeIf { it.isNotBlank() }
        ?: state.assignedAdvisor?.advisorDepartment

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.assigned_advisor),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            !hasPreloadedAdvisor && state.isLoading -> {
                CircularProgressIndicator(
                    color = Color.Black
                )
            }

            displayName.isNullOrBlank() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F8F8)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.no_assigned_advisor),
                        color = Color(0xFF777777),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F8F8)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = displayName,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (!displayDepartment.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = displayDepartment,
                                color = Color(0xFF777777),
                                fontSize = 14.sp
                            )
                        }

                        if (!displayEmail.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = displayEmail,
                                color = Color(0xFF777777),
                                fontSize = 14.sp
                            )
                        }

                        if (!displayPhone.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = displayPhone,
                                color = Color(0xFF777777),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
