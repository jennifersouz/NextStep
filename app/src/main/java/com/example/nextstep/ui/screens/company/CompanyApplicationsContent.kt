package com.example.nextstep.ui.screens.company

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyApplicationDto
import com.example.nextstep.ui.screens.admin.AppFilterDropdown

@Composable
fun CompanyApplicationsContent(
    onApplicationClick: (String) -> Unit,
    viewModel: CompanyApplicationsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
            val filteredApplications = state.applications.filter { app ->
                matchesApplicationFilter(app, state.selectedFilter)
            }

            if (isLandscape) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(
                        top = 28.dp,
                        bottom = 28.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            Text(
                                text = stringResource(R.string.company_applications_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            AppFilterDropdown(
                                label = stringResource(R.string.filter_status),
                                selectedOption = stringResource(state.selectedFilter.labelRes()),
                                options = ApplicationStatusFilter.entries,
                                optionLabel = { stringResource(it.labelRes()) },
                                onOptionSelected = viewModel::selectFilter,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${stringResource(state.selectedFilter.labelRes())} (${filteredApplications.size})",
                                fontSize = 14.sp,
                                color = Color(0xFF777777)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (filteredApplications.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
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
                        }
                    }
                }
            } else {
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

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        AppFilterDropdown(
                            label = stringResource(R.string.filter_status),
                            selectedOption = stringResource(state.selectedFilter.labelRes()),
                            options = ApplicationStatusFilter.entries,
                            optionLabel = { stringResource(it.labelRes()) },
                            onOptionSelected = viewModel::selectFilter,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Text(
                            text = "${stringResource(state.selectedFilter.labelRes())} (${filteredApplications.size})",
                            fontSize = 14.sp,
                            color = Color(0xFF777777)
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
}

private fun matchesApplicationFilter(
    application: CompanyApplicationDto,
    filter: ApplicationStatusFilter
): Boolean {
    val status = determineApplicationStatus(application)

    return when (filter) {
        ApplicationStatusFilter.ALL -> true
        ApplicationStatusFilter.TO_REVIEW -> !application.viewedByCompany
        ApplicationStatusFilter.PENDING -> status == ApplicationStatus.PENDING
        ApplicationStatusFilter.ACCEPTED -> status == ApplicationStatus.ACCEPTED
        ApplicationStatusFilter.REJECTED -> status == ApplicationStatus.REJECTED
        ApplicationStatusFilter.WAITING_STUDENT -> status == ApplicationStatus.WAITING_STUDENT
        ApplicationStatusFilter.WITH_ADVISOR -> status == ApplicationStatus.WITH_ADVISOR
    }
}

@Composable
private fun companyApplicationOperationalStatus(application: CompanyApplicationDto): String {
    return when {
        !application.viewedByCompany -> stringResource(R.string.status_unread)
        else -> {
            val appStatus = determineApplicationStatus(application)
            when (appStatus) {
                ApplicationStatus.PENDING -> stringResource(R.string.status_pending)
                ApplicationStatus.ACCEPTED -> stringResource(R.string.status_accepted)
                ApplicationStatus.REJECTED -> stringResource(R.string.status_rejected)
                ApplicationStatus.WAITING_STUDENT -> stringResource(
                    R.string.status_waiting_student_acceptance
                )
                ApplicationStatus.WITH_ADVISOR -> stringResource(R.string.status_with_advisor)
                ApplicationStatus.UNKNOWN -> application.status
            }
        }
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
