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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyUpdateDto
import com.example.nextstep.data.model.AdminProfileUpdateDto
import com.example.nextstep.data.model.CreateCompanyDto
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel

@Composable
fun AdminDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    onAddUserClick: () -> Unit = {},
    sessionViewModel: SessionViewModel = viewModel()
) {
    var selectedTab by rememberSaveable { mutableStateOf(AdminTab.HOME) }

    // Navigation flags
    var showUserDetail by rememberSaveable { mutableStateOf(false) }
    var showEditUser by rememberSaveable { mutableStateOf(false) }
    var showCompanyDetail by rememberSaveable { mutableStateOf(false) }
    var showEditCompany by rememberSaveable { mutableStateOf(false) }
    var showCreateCompany by rememberSaveable { mutableStateOf(false) }
    var showCompanyOffers by rememberSaveable { mutableStateOf(false) }

    val usersViewModel: AdminUsersViewModel = viewModel()
    val companiesViewModel: AdminCompaniesViewModel = viewModel()

    // Always read from ViewModel state
    val usersState by usersViewModel.uiState.collectAsState()
    val companiesState by companiesViewModel.uiState.collectAsState()

    val selectedUser = usersState.selectedUser
    val selectedCompany = companiesState.selectedCompany

    // ── User detail ──────────────────────────────────────────────────────────
    if (showUserDetail && !showEditUser && selectedUser != null) {
        AdminUserDetailScreen(
            profile = selectedUser,
            onBackClick = {
                showUserDetail = false
                usersViewModel.clearSelectedUser()
            },
            onEditClick = {
                showEditUser = true
            },
            onToggleActive = { userId, isActive ->
                usersViewModel.setUserActive(userId, isActive)
            },
            onDeleteUser = { userId ->
                usersViewModel.deleteUser(userId)
                showUserDetail = false
            }
        )
        return
    }

    // ── Edit user ────────────────────────────────────────────────────────────
    if (showEditUser && selectedUser != null) {
        AdminCreateEditUserScreen(
            existingProfile = selectedUser,
            onBackClick = {
                showEditUser = false
            },
            onSave = { firstName, lastName, phone, role, isActive ->
                usersViewModel.updateUser(
                    selectedUser.id,
                    AdminProfileUpdateDto(
                        firstName = firstName.ifBlank { null },
                        lastName = lastName.ifBlank { null },
                        phone = phone.ifBlank { null },
                        role = role.ifBlank { null },
                        isActive = isActive
                    )
                )
                showEditUser = false
            }
        )
        return
    }

    // ── Company offers ───────────────────────────────────────────────────────
    if (showCompanyOffers && selectedCompany != null) {
        AdminCompanyOffersScreen(
            companyProfileId = selectedCompany.profileId ?: "",
            companyName = selectedCompany.companyName ?: "",
            onBackClick = { showCompanyOffers = false }
        )
        return
    }

    // ── Company detail ───────────────────────────────────────────────────────
    if (showCompanyDetail && !showEditCompany && !showCreateCompany && selectedCompany != null) {
        AdminCompanyDetailScreen(
            company = selectedCompany,
            onBackClick = {
                showCompanyDetail = false
                showCompanyOffers = false
                companiesViewModel.clearSelectedCompany()
            },
            onEditClick = {
                showEditCompany = true
            },
            onToggleActive = { companyId, isActive ->
                companiesViewModel.setCompanyActive(companyId, isActive)
            },
            onDeleteCompany = { companyId ->
                companiesViewModel.deleteCompany(companyId)
                showCompanyDetail = false
            },
            onViewOffers = {
                showCompanyOffers = true
            }
        )
        return
    }

    // ── Edit company ─────────────────────────────────────────────────────────
    if (showEditCompany && selectedCompany != null) {
        AdminCreateEditCompanyScreen(
            existingCompany = selectedCompany,
            onBackClick = {
                showEditCompany = false
            },
            onSave = { name, nif, area, loc, ph, desc, active ->
                companiesViewModel.updateCompany(
                    selectedCompany.id,
                    AdminCompanyUpdateDto(
                        companyName = name.ifBlank { null },
                        nif = nif?.ifBlank { null },
                        businessArea = area?.ifBlank { null },
                        location = loc?.ifBlank { null },
                        phone = ph?.ifBlank { null },
                        description = desc?.ifBlank { null },
                        isActive = active
                    )
                )
                showEditCompany = false
            }
        )
        return
    }

    // ── Create company ───────────────────────────────────────────────────────
    if (showCreateCompany) {
        AdminCreateEditCompanyScreen(
            existingCompany = null,
            onBackClick = {
                showCreateCompany = false
            },
            onSave = { name, nif, area, loc, ph, desc, active ->
                companiesViewModel.createCompany(
                    CreateCompanyDto(
                        companyName = name,
                        nif = nif,
                        businessArea = area,
                        location = loc,
                        phone = ph,
                        description = desc,
                        isActive = active
                    )
                )
                showCreateCompany = false
            }
        )
        return
    }

    // ── Main scaffold ────────────────────────────────────────────────────────
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            AdminBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab -> selectedTab = tab }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AdminTab.HOME -> AdminHomeScreen()

                AdminTab.USERS -> {
                    AdminUsersScreen(
                        viewModel = usersViewModel,
                        onUserClick = { profile ->
                            usersViewModel.selectUser(profile)
                            showUserDetail = true
                        },
                        onAddUserClick = onAddUserClick
                    )
                }

                AdminTab.COMPANIES -> {
                    AdminCompaniesScreen(
                        viewModel = companiesViewModel,
                        onCompanyClick = { company ->
                            companiesViewModel.selectCompany(company)
                            showCompanyDetail = true
                        }
                    )
                }

                AdminTab.PROFILE -> {
                    AdminProfileScreen(
                        onLogoutClick = {
                            sessionViewModel.logout(onSuccess = onLogoutSuccess)
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
