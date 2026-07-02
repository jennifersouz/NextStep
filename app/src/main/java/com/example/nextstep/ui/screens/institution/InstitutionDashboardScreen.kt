package com.example.nextstep.ui.screens.institution

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel

@Composable
fun InstitutionDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    onAddUserClick: () -> Unit = {},
    onUserClick: (profileId: String?, role: String) -> Unit = { _, _ -> },
    sessionViewModel: SessionViewModel = viewModel()
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(InstitutionTab.HOME)
    }

    var showInstitutionEditProfile by rememberSaveable {
        mutableStateOf(false)
    }

    var institutionProfileRefreshKey by rememberSaveable {
        mutableStateOf(0)
    }

    var usersRefreshKey by rememberSaveable {
        mutableStateOf(0)
    }

    var previousTab by rememberSaveable {
        mutableStateOf<InstitutionTab?>(null)
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == InstitutionTab.USERS && previousTab != InstitutionTab.USERS) {
            usersRefreshKey++
        }
        previousTab = selectedTab
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            InstitutionBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    if (tab != InstitutionTab.PROFILE) {
                        showInstitutionEditProfile = false
                    }
                    if (tab == InstitutionTab.USERS) {
                        usersRefreshKey++
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                InstitutionTab.HOME -> {
                    InstitutionHomeScreen(
                        onAddUserClick = onAddUserClick,
                        onViewUsersClick = { selectedTab = InstitutionTab.USERS },
                        onStudentsClick = { selectedTab = InstitutionTab.USERS },
                        onTeachersClick = { selectedTab = InstitutionTab.USERS }
                    )
                }

                InstitutionTab.USERS -> {
                    InstitutionUsersScreen(
                        onAddUserClick = onAddUserClick,
                        onBackClick = null,
                        showBackButton = false,
                        refreshTrigger = usersRefreshKey,
                        onUserClick = onUserClick
                    )
                }

                InstitutionTab.PROFILE -> {
                    if (showInstitutionEditProfile) {
                        InstitutionEditProfileScreen(
                            onBackClick = {
                                showInstitutionEditProfile = false
                            },
                            onProfileUpdated = {
                                institutionProfileRefreshKey++
                                showInstitutionEditProfile = false
                            }
                        )
                    } else {
                        InstitutionProfileScreen(
                            refreshKey = institutionProfileRefreshKey,
                            onEditProfileClick = {
                                showInstitutionEditProfile = true
                            },
                            onLogoutClick = {
                                sessionViewModel.logout(
                                    onSuccess = onLogoutSuccess
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InstitutionBottomBar(
    selectedTab: InstitutionTab,
    onTabSelected: (InstitutionTab) -> Unit
) {
    NextStepBottomBar(
        items = listOf(
            BottomBarItem(
                route = InstitutionTab.HOME.name,
                icon = Icons.Filled.Home,
                label = stringResource(com.example.nextstep.R.string.tab_home)
            ),
            BottomBarItem(
                route = InstitutionTab.USERS.name,
                icon = Icons.Filled.Person,
                label = stringResource(com.example.nextstep.R.string.tab_users)
            ),
            BottomBarItem(
                route = InstitutionTab.PROFILE.name,
                icon = Icons.Filled.Person,
                label = stringResource(com.example.nextstep.R.string.tab_profile)
            )
        ),
        selectedItem = selectedTab.name,
        onItemClick = { tabName ->
            onTabSelected(InstitutionTab.valueOf(tabName))
        }
    )
}

enum class InstitutionTab {
    HOME,
    USERS,
    PROFILE
}