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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.nextstep.data.model.CompanyInternshipDto
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel

enum class CompanyBottomRoutes {
    INTERNSHIPS,
    APPLICATIONS,
    CREATE_OFFER,
    TEAM,
    PROFILE
}

@Composable
fun CompanyDashboardScreen(
    onOfferClick: (String) -> Unit = {},
    onApplicationClick: (String) -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: CompanyDashboardViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val sessionViewModel: SessionViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel()

    var selectedBottomRoute by remember {
        mutableStateOf(CompanyBottomRoutes.INTERNSHIPS)
    }

    var showCompanyEditProfile by rememberSaveable {
        mutableStateOf(false)
    }

    var companyProfileRefreshKey by rememberSaveable {
        mutableStateOf(0)
    }
    var showAddAdvisor by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedAdvisorId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedBottomRoute) {
                CompanyBottomRoutes.INTERNSHIPS -> {
                    CompanyInternshipsContent(
                        internships = state.filteredInternships,
                        selectedStatus = state.selectedStatus,
                        isLoading = state.isLoading,
                        errorMessageRes = state.errorMessageRes,
                        onStatusSelected = viewModel::onStatusSelected,
                        onOfferClick = onOfferClick
                    )
                }

                CompanyBottomRoutes.APPLICATIONS -> {
                    CompanyApplicationsContent(
                        onApplicationClick = onApplicationClick
                    )
                }

                CompanyBottomRoutes.CREATE_OFFER -> {
                    CreateOfferScreen(
                        onBackClick = {
                            selectedBottomRoute = CompanyBottomRoutes.INTERNSHIPS
                        },
                        onOfferCreated = {
                            selectedBottomRoute = CompanyBottomRoutes.INTERNSHIPS
                        }
                    )
                }

                CompanyBottomRoutes.TEAM -> {
                    when {
                        showAddAdvisor -> {
                            AddCompanyAdvisorScreen(
                                onBackClick = {
                                    showAddAdvisor = false
                                },
                                onAdvisorCreated = {
                                    showAddAdvisor = false
                                }
                            )
                        }

                        selectedAdvisorId != null -> {
                            CompanyAdvisorDetailScreen(
                                advisorId = selectedAdvisorId!!,
                                onBackClick = {
                                    selectedAdvisorId = null
                                },
                                onAdvisorDeleted = {
                                    selectedAdvisorId = null
                                }
                            )
                        }

                        else -> {
                            CompanyAdvisorsScreen(
                                onAddClick = {
                                    showAddAdvisor = true
                                },
                                onAdvisorClick = { advisorId ->
                                    selectedAdvisorId = advisorId
                                }
                            )
                        }
                    }
                }

                CompanyBottomRoutes.PROFILE -> {
                    if (showCompanyEditProfile) {
                        CompanyEditProfileScreen(
                            onBackClick = {
                                showCompanyEditProfile = false
                            },
                            onProfileUpdated = {
                                companyProfileRefreshKey++
                                showCompanyEditProfile = false
                            }
                        )
                    } else {
                        CompanyProfileScreen(
                            refreshKey = companyProfileRefreshKey,
                            onEditProfileClick = {
                                showCompanyEditProfile = true
                            },
                            onLogoutClick = {
                                sessionViewModel.logout(
                                    onSuccess = onLogoutSuccess
                                )
                            },
                            onOfferClick = onOfferClick
                        )
                    }
                }
            }
        }

        NextStepBottomBar(
            items = listOf(
                BottomBarItem(
                    route = CompanyBottomRoutes.INTERNSHIPS.name,
                    icon = Icons.Filled.Home,
                    label = stringResource(R.string.company_tab_internships)
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.APPLICATIONS.name,
                    icon = Icons.Filled.Assignment,
                    label = stringResource(R.string.company_tab_applications)
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.CREATE_OFFER.name,
                    icon = Icons.Filled.Add,
                    label = stringResource(R.string.company_tab_create)
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.TEAM.name,
                    icon = Icons.Filled.Groups,
                    label = stringResource(R.string.company_tab_team)
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.PROFILE.name,
                    icon = Icons.Filled.Person,
                    label = stringResource(R.string.company_tab_profile)
                )
            ),
            selectedItem = selectedBottomRoute.name,
            onItemClick = { routeName ->
                val route = CompanyBottomRoutes.valueOf(routeName)
                selectedBottomRoute = route

                if (route != CompanyBottomRoutes.PROFILE) {
                    showCompanyEditProfile = false
                }

                if (route != CompanyBottomRoutes.TEAM) {
                    showAddAdvisor = false
                    selectedAdvisorId = null
                }
            }
        )
    }
}

@Composable
fun CompanyInternshipsContent(
    internships: List<CompanyInternshipDto>,
    selectedStatus: InternshipStatusFilter,
    isLoading: Boolean,
    errorMessageRes: Int?,
    onStatusSelected: (InternshipStatusFilter) -> Unit,
    onOfferClick: (String) -> Unit
) {
    when {
        isLoading -> {
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

        errorMessageRes != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(errorMessageRes),
                    color = Color(0xFFB00020),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
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
                        text = stringResource(R.string.company_manage_internships_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CompanyInternshipStatusTabs(
                        selectedStatus = selectedStatus,
                        onStatusSelected = onStatusSelected
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (internships.isEmpty()) {
                    item {
                        CompanyInternshipsEmptyState(
                            selectedStatus = selectedStatus
                        )
                    }
                } else {
                    items(
                        items = internships,
                        key = { internship ->
                            internship.id
                        }
                    ) { internship ->
                        CompanyInternshipCard(
                            internship = internship,
                            onClick = {
                                onOfferClick(internship.id)
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
fun CompanyInternshipStatusTabs(
    selectedStatus: InternshipStatusFilter,
    onStatusSelected: (InternshipStatusFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3F3F3))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompanyInternshipStatusTab(
            text = stringResource(R.string.company_pending),
            selected = selectedStatus == InternshipStatusFilter.PENDING,
            onClick = {
                onStatusSelected(InternshipStatusFilter.PENDING)
            },
            modifier = Modifier.weight(1f)
        )

        CompanyInternshipStatusTab(
            text = stringResource(R.string.company_completed),
            selected = selectedStatus == InternshipStatusFilter.COMPLETED,
            onClick = {
                onStatusSelected(InternshipStatusFilter.COMPLETED)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CompanyInternshipStatusTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) {
                    Color.White
                } else {
                    Color.Transparent
                }
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            color = Color.Black
        )
    }
}

@Composable
fun CompanyInternshipCard(
    internship: CompanyInternshipDto,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = internship.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompanyInternshipStudentAvatar(
                studentName = internship.studentName
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = internship.studentName,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun CompanyInternshipStudentAvatar(
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
fun CompanyInternshipsEmptyState(
    selectedStatus: InternshipStatusFilter
) {
    val title = if (selectedStatus == InternshipStatusFilter.PENDING) {
        stringResource(R.string.company_no_pending_internships_title)
    } else {
        stringResource(R.string.company_no_completed_internships_title)
    }

    val subtitle = if (selectedStatus == InternshipStatusFilter.PENDING) {
        stringResource(R.string.company_no_pending_internships_subtitle)
    } else {
        stringResource(R.string.company_no_completed_internships_subtitle)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF8A8A8A),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun CompanyPlaceholderContent(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(160.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFF8A8A8A),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
