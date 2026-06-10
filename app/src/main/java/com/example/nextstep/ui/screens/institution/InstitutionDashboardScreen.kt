package com.example.nextstep.ui.screens.institution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel

@Composable
fun InstitutionDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    onUsersClick: () -> Unit = {},
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
                InstitutionTab.HOME -> InstitutionHomeContent()

                InstitutionTab.USERS -> InstitutionUsersContent(
                    onUsersClick = onUsersClick
                )

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
fun InstitutionHomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.institution_area),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.institution_home_empty),
            fontSize = 16.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InstitutionUsersContent(
    onUsersClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.manage_users),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onUsersClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDFA52),
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(R.string.add_user),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
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
                label = stringResource(R.string.tab_home)
            ),
            BottomBarItem(
                route = InstitutionTab.USERS.name,
                icon = Icons.Filled.People,
                label = stringResource(R.string.tab_users)
            ),
            BottomBarItem(
                route = InstitutionTab.PROFILE.name,
                icon = Icons.Filled.Person,
                label = stringResource(R.string.tab_profile)
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