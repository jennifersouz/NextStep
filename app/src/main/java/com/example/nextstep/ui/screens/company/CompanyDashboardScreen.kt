package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
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
    onInternStudentClick: (String) -> Unit = {},
    onInternMessageClick: (String) -> Unit = {},
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
                    CompanyOffersScreen(
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
                    CompanyInternStudentsScreen(
                        onBackClick = {
                            selectedBottomRoute = CompanyBottomRoutes.INTERNSHIPS
                        },
                        onStudentClick = onInternStudentClick,
                        onMessageClick = onInternMessageClick
                    )
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
                        CompanyOwnProfileScreen(
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
                    label = stringResource(R.string.tab_internships)
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.APPLICATIONS.name,
                    icon = Icons.Filled.Assignment,
                    label = stringResource(R.string.tab_applications)
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.CREATE_OFFER.name,
                    icon = Icons.Filled.Add,
                    label = stringResource(R.string.tab_create)
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.TEAM.name,
                    icon = Icons.Filled.Groups,
                    label = "Equipa"
                ),
                BottomBarItem(
                    route = CompanyBottomRoutes.PROFILE.name,
                    icon = Icons.Filled.Person,
                    label = stringResource(R.string.tab_profile)
                )
            ),
            selectedItem = selectedBottomRoute.name,
            onItemClick = { routeName ->
                val route = CompanyBottomRoutes.valueOf(routeName)
                selectedBottomRoute = route

                if (route != CompanyBottomRoutes.PROFILE) {
                    showCompanyEditProfile = false
                }
            }
        )
    }
}
