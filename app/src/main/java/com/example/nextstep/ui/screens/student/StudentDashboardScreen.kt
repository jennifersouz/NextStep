package com.example.nextstep.ui.screens.student

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.CompanyLogo
import com.example.nextstep.ui.components.InternshipOfferCard
import com.example.nextstep.ui.components.NextStepBottomBar
import com.example.nextstep.ui.screens.auth.SessionViewModel

object StudentBottomRoutes {
    const val HOME = "home"
    const val INTERNSHIPS = "internships"
    const val NOTIFICATIONS = "notifications"
    const val MESSAGES = "messages"
    const val PROFILE = "profile"
}

@Composable
fun StudentDashboardScreen(
    onOfferClick: (String) -> Unit = {},
    onSubmittedApplicationsClick: () -> Unit = {},
    onSentRequestsClick: () -> Unit = {},
    onApplicationNotificationClick: (String) -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: StudentDashboardViewModel = viewModel(),
    onChatClick: (String) -> Unit = {},
    onInternshipClick: (String) -> Unit = {},
    onCompanyClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val filteredOffers = state.filteredOffers
    val errorMessage = state.errorMessage
    val lifecycleOwner = LocalLifecycleOwner.current

    val sessionViewModel: SessionViewModel = viewModel()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadUnreadNotificationsCount()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var selectedBottomRoute by rememberSaveable {
        mutableStateOf(StudentBottomRoutes.HOME)
    }

    var showStudentSettings by rememberSaveable {
        mutableStateOf(false)
    }

    var showStudentEditProfile by rememberSaveable {
        mutableStateOf(false)
    }

    var profileRefreshKey by rememberSaveable {
        mutableStateOf(0)
    }

    var showStudentSavedOffers by rememberSaveable {
        mutableStateOf(false)
    }

    var showFiltersSheet by rememberSaveable {
        mutableStateOf(false)
    }

    if (showFiltersSheet) {
        StudentOffersFilterSheet(
            state = state,
            onDismiss = {
                showFiltersSheet = false
            },
            onClearFilters = viewModel::clearFilters,
            onAreaSelected = viewModel::onAreaFilterSelected,
            onWorkModeSelected = viewModel::onWorkModeFilterSelected,
            onLocationSelected = viewModel::onLocationFilterSelected,
            onOnlyWithVacanciesChange = viewModel::onOnlyWithVacanciesChange
        )
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
                StudentBottomRoutes.HOME -> {
                    StudentOffersContent(
                        state = state,
                        filteredOffers = filteredOffers,
                        errorMessage = errorMessage,
                        onSearchChange = viewModel::onSearchChange,
                        onRetryClick = viewModel::loadOffers,
                        onFilterClick = {
                            showFiltersSheet = true
                        },
                        onOfferClick = onOfferClick,
                        onCompanyClick = onCompanyClick
                    )
                }

                StudentBottomRoutes.INTERNSHIPS -> {
                    StudentInternshipsScreen(
                        onInternshipClick = onInternshipClick
                    )
                }

                StudentBottomRoutes.NOTIFICATIONS -> {
                    StudentNotificationsScreen(
                        onNotificationClick = onApplicationNotificationClick,
                        onUnreadCountChanged = { count ->
                            Log.d(
                                "NOTIF_DEBUG",
                                "Dashboard recebeu novo contador = $count"
                            )
                            viewModel.setUnreadNotificationsCount(count)
                        }
                    )
                }

                StudentBottomRoutes.MESSAGES -> {
                    StudentMessagesScreen(
                        onConversationClick = onChatClick
                    )
                }

                StudentBottomRoutes.PROFILE -> {
                    when {
                        showStudentSavedOffers -> {
                            StudentSavedOffersScreen(
                                onBackClick = {
                                    showStudentSavedOffers = false
                                },
                                onOfferClick = onOfferClick,
                                onCompanyClick = { companyId ->
                                    Log.d("CompanyProfileDebug", "Navegando dos Salvos. ID enviado: $companyId")
                                    onCompanyClick(companyId)
                                }
                            )
                        }

                        showStudentEditProfile -> {
                            StudentEditProfileScreen(
                                onBackClick = {
                                    showStudentEditProfile = false
                                },
                                onProfileUpdated = {
                                    profileRefreshKey++
                                    showStudentEditProfile = false
                                    showStudentSettings = false
                                    showStudentSavedOffers = false
                                }
                            )
                        }

                        showStudentSettings -> {
                            StudentSettingsScreen(
                                onBackClick = {
                                    showStudentSettings = false
                                },
                                onEditProfileClick = {
                                    showStudentEditProfile = true
                                }
                            )
                        }

                else -> {
                            StudentProfileScreen(
                                refreshKey = profileRefreshKey,
                                onSentRequestsClick = onSentRequestsClick,
                                onSavedInternshipsClick = {
                                    showStudentSavedOffers = true
                                },
                                onSubmittedApplicationsClick = onSubmittedApplicationsClick,
                                onEditProfileClick = {
                                    showStudentEditProfile = true
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

        NextStepBottomBar(
            items = listOf(
                BottomBarItem(
                    route = StudentBottomRoutes.HOME,
                    icon = Icons.Filled.Home,
                    label = stringResource(R.string.tab_home)
                ),
                BottomBarItem(
                    route = StudentBottomRoutes.INTERNSHIPS,
                    icon = Icons.Filled.Work,
                    label = stringResource(R.string.tab_internships)
                ),
                BottomBarItem(
                    route = StudentBottomRoutes.NOTIFICATIONS,
                    icon = Icons.Filled.Notifications,
                    label = stringResource(R.string.tab_notifications),
                    badgeCount = state.unreadNotificationsCount
                ),
                BottomBarItem(
                    route = StudentBottomRoutes.MESSAGES,
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = stringResource(R.string.tab_chats)
                ),
                BottomBarItem(
                    route = StudentBottomRoutes.PROFILE,
                    icon = Icons.Filled.Person,
                    label = stringResource(R.string.tab_profile)
                )
            ),
            selectedItem = selectedBottomRoute,
            onItemClick = { route ->
                selectedBottomRoute = route

                if (route != StudentBottomRoutes.PROFILE) {
                    showStudentSettings = false
                    showStudentEditProfile = false
                    showStudentSavedOffers = false
                }

                if (route == StudentBottomRoutes.NOTIFICATIONS) {
                    viewModel.loadUnreadNotificationsCount()
                }
            }
        )
    }
}

@Composable
fun StudentOffersContent(
    state: StudentDashboardUiState,
    filteredOffers: List<OfferDto>,
    errorMessage: String?,
    onSearchChange: (String) -> Unit,
    onRetryClick: () -> Unit,
    onFilterClick: () -> Unit,
    onOfferClick: (String) -> Unit,
    onCompanyClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 22.dp,
            end = 22.dp,
            top = 22.dp,
            bottom = 24.dp
        )
    ) {
        item {
            SearchBar(
                value = state.searchQuery,
                onValueChange = onSearchChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterButton(
                    activeFiltersCount = state.activeFiltersCount,
                    onClick = onFilterClick
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(
                        R.string.results_count,
                        filteredOffers.size
                    ),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        when {
            state.isLoading -> {
                item {
                    LoadingState()
                }
            }

            errorMessage != null -> {
                item {
                    ErrorState(
                        message = errorMessage,
                        onRetryClick = onRetryClick
                    )
                }
            }

            filteredOffers.isEmpty() -> {
                item {
                    EmptyOffersState(
                        hasSearchQuery = state.searchQuery.isNotBlank()
                    )
                }
            }

            else -> {
                items(
                    items = filteredOffers,
                    key = { offer ->
                        offer.id
                    }
                ) { offer ->
                    InternshipOfferCard(
                        offer = offer,
                        onClick = {
                            onOfferClick(offer.id)
                        },
                        onCompanyClick = {
                            offer.companyProfileId?.let { onCompanyClick(it) }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = Color(0xFF7A7A7A),
                fontSize = 22.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                tint = Color(0xFF7A7A7A),
                modifier = Modifier.size(32.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(10.dp)),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF4F4F4),
            unfocusedContainerColor = Color(0xFFF4F4F4),
            disabledContainerColor = Color(0xFFF4F4F4),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun FilterButton(
    activeFiltersCount: Int = 0,
    onClick: () -> Unit
) {
    val label = if (activeFiltersCount > 0) {
        "${stringResource(R.string.filter)} ($activeFiltersCount)"
    } else {
        stringResource(R.string.filter)
    }

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color(0xFF8A8A8A),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Black
        )
    }
}

@Composable
fun EmptyOffersState(
    hasSearchQuery: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (hasSearchQuery) {
                stringResource(R.string.no_search_results_title)
            } else {
                stringResource(R.string.no_offers_title)
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasSearchQuery) {
                stringResource(R.string.no_search_results_subtitle)
            } else {
                stringResource(R.string.no_offers_subtitle)
            },
            fontSize = 16.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFFB00020),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDFA52),
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(R.string.try_again)
            )
        }
    }
}

@Composable
fun StudentPlaceholderContent(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 42.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(140.dp))

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentOffersFilterSheet(
    state: StudentDashboardUiState,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit,
    onAreaSelected: (String?) -> Unit,
    onWorkModeSelected: (String?) -> Unit,
    onLocationSelected: (String?) -> Unit,
    onOnlyWithVacanciesChange: (Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_offers_title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.clear_filters),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.clickable {
                        onClearFilters()
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            StudentFilterSection(
                title = stringResource(R.string.area),
                options = state.availableAreas,
                selectedOption = state.selectedArea,
                onOptionSelected = onAreaSelected,
                optionLabel = { area ->
                    displayAreaLabel(area)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            StudentFilterSection(
                title = stringResource(R.string.work_mode),
                options = state.availableWorkModes,
                selectedOption = state.selectedWorkMode,
                onOptionSelected = onWorkModeSelected,
                optionLabel = { workMode ->
                    displayWorkModeLabel(workMode)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            StudentFilterSection(
                title = stringResource(R.string.location),
                options = state.availableLocations,
                selectedOption = state.selectedLocation,
                onOptionSelected = onLocationSelected
            )

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(
                color = Color(0xFFEAEAEA)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOnlyWithVacanciesChange(!state.onlyWithVacancies)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.only_with_vacancies),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.only_with_vacancies_subtitle),
                        fontSize = 13.sp,
                        color = Color(0xFF8A8A8A)
                    )
                }

                Switch(
                    checked = state.onlyWithVacancies,
                    onCheckedChange = onOnlyWithVacanciesChange
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(R.string.apply_filters),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StudentFilterSection(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    optionLabel: @Composable (String) -> String = { option -> option }
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                StudentFilterChip(
                    text = stringResource(R.string.filter_all),
                    selected = selectedOption == null,
                    onClick = {
                        onOptionSelected(null)
                    }
                )
            }

            items(
                items = options,
                key = { option -> option }
            ) { option ->
                StudentFilterChip(
                    text = optionLabel(option),
                    selected = selectedOption == option,
                    onClick = {
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
            )
        }
    )
}

@Composable
fun displayWorkModeLabel(
    workMode: String
): String {
    return when (workMode.lowercase().trim()) {
        "onsite", "on-site", "presencial" -> stringResource(R.string.work_mode_onsite)
        "remote", "remoto" -> stringResource(R.string.work_mode_remote)
        "hybrid", "hibrido", "híbrido" -> stringResource(R.string.work_mode_hybrid)
        else -> workMode
    }
}

@Composable
fun displayAreaLabel(
    area: String
): String {
    return when (area.lowercase().trim()) {
        "management", "gestao", "gestão" -> stringResource(R.string.area_management)
        "design" -> stringResource(R.string.area_design)
        "data", "dados" -> stringResource(R.string.area_data)
        "it", "informática", "informatica" -> stringResource(R.string.area_it)
        "marketing" -> stringResource(R.string.area_marketing)
        "other", "outra", "outro" -> stringResource(R.string.area_other)
        else -> area
    }
}