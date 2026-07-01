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
import com.example.nextstep.data.model.CreateCompanyDto
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel

@Composable
fun AdminDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    onAddUserClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
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
    var showAdminEditProfile by rememberSaveable { mutableStateOf(false) }

    val usersViewModel: AdminUsersViewModel = viewModel()
    val companiesViewModel: AdminCompaniesViewModel = viewModel()
    val userDetailViewModel: AdminUserDetailViewModel = viewModel()
    val companyDetailViewModel: AdminCompanyDetailViewModel = viewModel()

    // Always read from ViewModel state
    val usersState by usersViewModel.uiState.collectAsState()
    val companiesState by companiesViewModel.uiState.collectAsState()
    val userDetailState by userDetailViewModel.uiState.collectAsState()
    val companyDetailState by companyDetailViewModel.uiState.collectAsState()

    val selectedUser = usersState.selectedUser
    val selectedCompany = companiesState.selectedCompany

    // ── User detail ──────────────────────────────────────────────────────────
    if (showUserDetail && !showEditUser && selectedUser != null) {
        // Na primeira entrada, inicializar o ViewModel com o utilizador selecionado.
        // Depois disso, usar sempre o estado do ViewModel para refletir atualizações imediatas.
        val displayUser = userDetailState.profile?.takeIf { it.id == selectedUser.id }
            ?: selectedUser.also { userDetailViewModel.setProfile(it) }

        AdminUserDetailScreen(
            profile = displayUser,
            isActionLoading = userDetailState.isActionLoading,
            successMessageRes = userDetailState.successMessageRes,
            errorMessageRes = userDetailState.errorMessageRes,
            onMessageDismiss = { userDetailViewModel.clearMessages() },
            onBackClick = {
                showUserDetail = false
                usersViewModel.clearSelectedUser()
                userDetailViewModel.clearMessages()
            },
            onEditClick = {
                showEditUser = true
            },
            onDeactivate = {
                userDetailViewModel.deactivate("")
                usersViewModel.loadUsers()
            },
            onReactivate = {
                userDetailViewModel.reactivate("")
                usersViewModel.loadUsers()
            },
            onArchive = { reason ->
                // adminId é obtido internamente no Repository
                userDetailViewModel.archive(reason)
                usersViewModel.loadUsers()
            }
        )
        return
    }

    // ── Edit user (new dedicated screen with its own ViewModel) ─────────────
    if (showEditUser && selectedUser != null) {
        AdminEditUserScreen(
            userId = selectedUser.id,
            onBackClick = {
                showEditUser = false
            },
            onSaved = {
                // Recarregar detalhe e lista após guardar
                userDetailViewModel.loadProfile(selectedUser.id)
                usersViewModel.loadUsers()
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
        // Usar sempre o estado do ViewModel para refletir atualizações imediatas
        val displayCompany = companyDetailState.company ?: selectedCompany
        AdminCompanyDetailScreen(
            company = displayCompany,
            isActionLoading = companyDetailState.isActionLoading,
            successMessage = companyDetailState.successMessage,
            successMessageRes = companyDetailState.successMessageRes,
            errorMessage = companyDetailState.errorMessage,
            errorMessageRes = companyDetailState.errorMessageRes,
            onMessageDismiss = { companyDetailViewModel.clearMessages() },
            onBackClick = {
                showCompanyDetail = false
                showCompanyOffers = false
                companiesViewModel.clearSelectedCompany()
                companyDetailViewModel.clearMessages()
            },
            onEditClick = {
                showEditCompany = true
            },
            onDeactivate = {
                companyDetailViewModel.deactivate()
                companiesViewModel.loadCompanies()
            },
            onReactivate = {
                companyDetailViewModel.reactivate()
                companiesViewModel.loadCompanies()
            },
            onArchive = { reason ->
                companyDetailViewModel.archive(reason)
                companiesViewModel.loadCompanies()
            },
            onViewOffers = {
                showCompanyOffers = true
            }
        )
        return
    }

    // ── Edit company ─────────────────────────────────────────────────────────
    if (showEditCompany && selectedCompany != null) {
        // Usar a empresa do detalhe se disponível (pode ter sido atualizada)
        val companyToEdit = companyDetailState.company ?: selectedCompany
        AdminCreateEditCompanyScreen(
            existingCompany = companyToEdit,
            onBackClick = {
                showEditCompany = false
            },
            onSave = { name, nif, area, loc, ph, desc, active ->
                val profileId = companyToEdit.profileId ?: ""
                companiesViewModel.editCompany(
                    companyId = companyToEdit.id,
                    companyProfileId = profileId,
                    name = name,
                    nif = nif,
                    businessArea = area,
                    location = loc,
                    phone = ph,
                    description = desc,
                    isActive = active,
                    onSuccess = { updatedCompany ->
                        // Atualizar o detalhe imediatamente com os dados guardados
                        companyDetailViewModel.resetCompany(updatedCompany)
                        showEditCompany = false
                    },
                    onError = {
                        // Manter o ecrã de edição aberto para o utilizador ver o erro
                    }
                )
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

    // ── Admin Edit Profile ───────────────────────────────────────────────────
    if (showAdminEditProfile) {
        AdminEditProfileScreen(
            onBackClick = {
                showAdminEditProfile = false
            },
            onProfileUpdated = {
                showAdminEditProfile = false
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
                            companyDetailViewModel.resetCompany(company)
                            showCompanyDetail = true
                        }
                    )
                }

                AdminTab.PROFILE -> {
                    AdminProfileScreen(
                        onEditProfileClick = {
                            showAdminEditProfile = true
                        },
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
                label = stringResource(R.string.companies_label)
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