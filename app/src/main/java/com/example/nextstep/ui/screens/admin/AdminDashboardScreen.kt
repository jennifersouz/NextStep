package com.example.nextstep.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.data.model.UpdateProfileDto
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel

@Composable
fun AdminDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    sessionViewModel: SessionViewModel = viewModel()
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(AdminTab.HOME)
    }

    var selectedUser by remember { mutableStateOf<ProfileDto?>(null) }
    var showEditUser by remember { mutableStateOf(false) }

    val usersViewModel: AdminUsersViewModel = viewModel()

    // Handle user detail screen
    if (selectedUser != null && !showEditUser) {
        AdminUserDetailScreen(
            profile = selectedUser!!,
            onBackClick = { selectedUser = null },
            onEditClick = { profile ->
                selectedUser = profile
                showEditUser = true
            },
            onToggleActive = { userId, isActive ->
                usersViewModel.setUserActive(userId, isActive)
                selectedUser = selectedUser?.copy(isActive = isActive)
            },
            onDeleteUser = { userId ->
                usersViewModel.deleteUser(userId)
                selectedUser = null
            }
        )
        return
    }

    // Handle edit user screen
    if (selectedUser != null && showEditUser) {
        AdminCreateEditUserScreen(
            existingProfile = selectedUser,
            onBackClick = {
                showEditUser = false
            },
            onSave = { firstName, lastName, phone, role, isActive ->
                usersViewModel.updateUser(
                    selectedUser!!.id,
                    UpdateProfileDto(
                        firstName = firstName,
                        lastName = lastName,
                        phone = phone
                    )
                )
                // Refresh user data by setting it as updated locally
                selectedUser = selectedUser?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone
                )
                showEditUser = false
            }
        )
        return
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            AdminBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
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
                AdminTab.HOME -> {
                    AdminHomeScreen()
                }

                AdminTab.USERS -> {
                    AdminUsersScreen(
                        viewModel = usersViewModel,
                        onUserClick = { profile ->
                            selectedUser = profile
                        }
                    )
                }

                AdminTab.COMPANIES -> {
                    AdminCompaniesScreen()
                }

                AdminTab.PROFILE -> {
                    AdminProfileScreen(
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

@Composable
fun AdminBottomBar(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    NextStepBottomBar(
        items = listOf(
            BottomBarItem(
                route = AdminTab.HOME.name,
                icon = Icons.Filled.Home,
                label = stringResource(R.string.tab_home)
            ),
            BottomBarItem(
                route = AdminTab.USERS.name,
                icon = Icons.Filled.People,
                label = stringResource(R.string.tab_users)
            ),
            BottomBarItem(
                route = AdminTab.COMPANIES.name,
                icon = Icons.Filled.Business,
                label = "Empresas"
            ),
            BottomBarItem(
                route = AdminTab.PROFILE.name,
                icon = Icons.Filled.Person,
                label = stringResource(R.string.tab_profile)
            )
        ),
        selectedItem = selectedTab.name,
        onItemClick = { tabName ->
            onTabSelected(AdminTab.valueOf(tabName))
        }
    )
}