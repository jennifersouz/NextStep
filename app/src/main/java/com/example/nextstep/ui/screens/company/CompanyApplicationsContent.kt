package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
    onApplicationClick: (String) -> Unit,
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
            val filteredApplications = filterCompanyApplications(
                applications = state.applications,
                selectedFilter = state.selectedFilter
            )

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

                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    CompanyApplicationsFilterChips(
                        selectedFilter = state.selectedFilter,
                        onFilterSelected = viewModel::selectFilter,
                        applications = state.applications
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (filteredApplications.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.no_applications_for_filter),
                            color = Color(0xFF777777),
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(
                        items = filteredApplications,
                        key = { it.id }
                    ) { application ->
                        CompanyApplicationCard(
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
private fun CompanyApplicationsFilterChips(
    selectedFilter: CompanyApplicationFilter,
    onFilterSelected: (CompanyApplicationFilter) -> Unit,
    applications: List<CompanyApplicationDto>
) {
    val filters = CompanyApplicationFilter.entries

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters) { filter ->
            val count = filterCompanyApplications(
                applications = applications,
                selectedFilter = filter
            ).size

            FilterChip(
                selected = selectedFilter == filter,
                onClick = {
                    onFilterSelected(filter)
                },
                label = {
                    Text(
                        text = "${companyApplicationFilterLabel(filter)} ($count)"
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFDFA52),
                    selectedLabelColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun companyApplicationFilterLabel(
    filter: CompanyApplicationFilter
): String {
    return when (filter) {
        CompanyApplicationFilter.ALL -> stringResource(R.string.filter_all)
        CompanyApplicationFilter.UNREAD -> stringResource(R.string.filter_unread)
        CompanyApplicationFilter.PENDING -> stringResource(R.string.filter_pending)
        CompanyApplicationFilter.WAITING_STUDENT_ACCEPTANCE -> stringResource(
            R.string.filter_waiting_student_acceptance
        )
        CompanyApplicationFilter.NEEDS_ADVISOR -> stringResource(R.string.filter_needs_advisor)
        CompanyApplicationFilter.WITH_ADVISOR -> stringResource(R.string.filter_with_advisor)
        CompanyApplicationFilter.REJECTED -> stringResource(R.string.filter_rejected)
    }
}

private fun filterCompanyApplications(
    applications: List<CompanyApplicationDto>,
    selectedFilter: CompanyApplicationFilter
): List<CompanyApplicationDto> {
    return when (selectedFilter) {
        CompanyApplicationFilter.ALL -> applications

        CompanyApplicationFilter.UNREAD -> applications.filter {
            !it.viewedByCompany
        }

        CompanyApplicationFilter.PENDING -> applications.filter {
            it.status.lowercase().trim() in listOf("pending", "pendente")
        }

        CompanyApplicationFilter.WAITING_STUDENT_ACCEPTANCE -> applications.filter {
            it.status.lowercase().trim() in listOf("accepted", "aceite") &&
                !it.studentPresenceConfirmed
        }

        CompanyApplicationFilter.NEEDS_ADVISOR -> applications.filter {
            it.status.lowercase().trim() in listOf("accepted", "aceite") &&
                it.studentPresenceConfirmed &&
                it.advisorProfileId.isNullOrBlank()
        }

        CompanyApplicationFilter.WITH_ADVISOR -> applications.filter {
            !it.advisorProfileId.isNullOrBlank()
        }

        CompanyApplicationFilter.REJECTED -> applications.filter {
            it.status.lowercase().trim() in listOf("rejected", "recusada", "rejeitada")
        }
    }
}

@Composable
private fun companyApplicationOperationalStatus(application: CompanyApplicationDto): String {
    val status = application.status.lowercase().trim()

    return when {
        !application.viewedByCompany -> stringResource(R.string.status_unread)

        status in listOf("pending", "pendente") -> stringResource(R.string.status_pending)

        status in listOf("accepted", "aceite") &&
            !application.studentPresenceConfirmed -> stringResource(
            R.string.status_waiting_student_acceptance
        )

        status in listOf("accepted", "aceite") &&
            application.studentPresenceConfirmed &&
            application.advisorProfileId.isNullOrBlank() -> stringResource(
            R.string.status_needs_advisor
        )

        !application.advisorProfileId.isNullOrBlank() -> stringResource(R.string.status_with_advisor)

        status in listOf("rejected", "recusada", "rejeitada") -> stringResource(
            R.string.status_rejected
        )

        else -> application.status
    }
}

@Composable
fun CompanyApplicationCard(
    application: CompanyApplicationDto,
    onClick: () -> Unit
) {
    val studentName = "${application.firstName} ${application.lastName}"
    val operationalStatus = companyApplicationOperationalStatus(application)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = application.offerTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = operationalStatus,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            CompanyApplicationStudentAvatar(studentName = studentName)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = studentName, fontSize = 14.sp, color = Color.Black)
                application.course?.let {
                    Text(text = it, fontSize = 12.sp, color = Color(0xFF8A8A8A))
                }
            }
        }
    }
}

@Composable
fun CompanyApplicationStudentAvatar(studentName: String) {
    val initials = studentName.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }.ifBlank { "?" }

    Box(
        modifier = Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF2B2B2B)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initials, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
