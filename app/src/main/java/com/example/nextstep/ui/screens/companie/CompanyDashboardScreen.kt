package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyInternshipDto

object CompanyBottomRoutes {
    const val INTERNSHIPS = "company_internships"
    const val APPLICATIONS = "company_applications"
    const val CREATE_OFFER = "company_create_offer"
    const val TEAM = "company_team"
    const val PROFILE = "company_profile"
}

data class CompanyBottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun CompanyDashboardScreen(
    viewModel: CompanyDashboardViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var selectedBottomRoute by rememberSaveable {
        mutableStateOf(CompanyBottomRoutes.INTERNSHIPS)
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
                        state = state,
                        onStatusFilterChange = viewModel::onStatusFilterChange,
                        onRetryClick = viewModel::loadInternships
                    )
                }

                CompanyBottomRoutes.APPLICATIONS -> {
                    CompanyPlaceholderContent(
                        title = stringResource(R.string.company_applications_title),
                        subtitle = stringResource(R.string.company_applications_placeholder)
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
                    CompanyPlaceholderContent(
                        title = stringResource(R.string.company_team_title),
                        subtitle = stringResource(R.string.company_team_placeholder)
                    )
                }

                CompanyBottomRoutes.PROFILE -> {
                    CompanyPlaceholderContent(
                        title = stringResource(R.string.company_profile_title),
                        subtitle = stringResource(R.string.company_profile_placeholder)
                    )
                }
            }
        }

        CompanyBottomBar(
            currentRoute = selectedBottomRoute,
            onItemClick = { route ->
                selectedBottomRoute = route
            }
        )
    }
}

@Composable
fun CompanyInternshipsContent(
    state: CompanyDashboardUiState,
    onStatusFilterChange: (InternshipStatusFilter) -> Unit,
    onRetryClick: () -> Unit
) {
    val filteredInternships = state.filteredInternships
    val errorMessageRes = state.errorMessageRes

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 22.dp,
            end = 22.dp,
            top = 24.dp,
            bottom = 24.dp
        )
    ) {
        item {
            Text(
                text = stringResource(R.string.company_manage_internships_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                InternshipStatusChip(
                    text = stringResource(R.string.company_pending),
                    selected = state.selectedStatus == InternshipStatusFilter.PENDING,
                    onClick = {
                        onStatusFilterChange(InternshipStatusFilter.PENDING)
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                InternshipStatusChip(
                    text = stringResource(R.string.company_completed),
                    selected = state.selectedStatus == InternshipStatusFilter.COMPLETED,
                    onClick = {
                        onStatusFilterChange(InternshipStatusFilter.COMPLETED)
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        when {
            state.isLoading -> {
                item {
                    CompanyLoadingState()
                }
            }

            errorMessageRes != null -> {
                item {
                    CompanyErrorState(
                        message = stringResource(errorMessageRes),
                        onRetryClick = onRetryClick
                    )
                }
            }

            filteredInternships.isEmpty() -> {
                item {
                    CompanyEmptyInternshipsState(
                        selectedStatus = state.selectedStatus
                    )
                }
            }

            else -> {
                items(
                    items = filteredInternships,
                    key = { internship -> internship.id }
                ) { internship ->
                    CompanyInternshipCard(
                        internship = internship,
                        onClick = {
                            // Depois: abrir detalhes do estágio
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
fun InternshipStatusChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFFDFA52) else Color(0xFFF4F4F4),
        label = "status_chip_bg"
    )

    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 9.dp),
        fontSize = 14.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        color = Color.Black
    )
}

@Composable
fun CompanyInternshipCard(
    internship: CompanyInternshipDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudentAvatar(
                studentName = internship.studentName
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = internship.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = internship.studentName,
                    fontSize = 13.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun StudentAvatar(
    studentName: String
) {
    val initials = studentName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { namePart ->
            namePart.first().uppercase()
        }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFF222222)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun CompanyLoadingState() {
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
fun CompanyErrorState(
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
            fontSize = 15.sp,
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
            Text(text = stringResource(R.string.try_again))
        }
    }
}

@Composable
fun CompanyEmptyInternshipsState(
    selectedStatus: InternshipStatusFilter
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (selectedStatus) {
                InternshipStatusFilter.PENDING -> stringResource(R.string.company_no_pending_internships_title)
                InternshipStatusFilter.COMPLETED -> stringResource(R.string.company_no_completed_internships_title)
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (selectedStatus) {
                InternshipStatusFilter.PENDING -> stringResource(R.string.company_no_pending_internships_subtitle)
                InternshipStatusFilter.COMPLETED -> stringResource(R.string.company_no_completed_internships_subtitle)
            },
            fontSize = 15.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CompanyPlaceholderContent(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 42.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

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

@Composable
fun CompanyBottomBar(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        CompanyBottomNavItem(
            route = CompanyBottomRoutes.INTERNSHIPS,
            labelRes = R.string.company_tab_internships,
            selectedIcon = Icons.Filled.School,
            unselectedIcon = Icons.Outlined.School
        ),
        CompanyBottomNavItem(
            route = CompanyBottomRoutes.APPLICATIONS,
            labelRes = R.string.company_tab_applications,
            selectedIcon = Icons.Filled.Assignment,
            unselectedIcon = Icons.Outlined.Assignment
        ),
        CompanyBottomNavItem(
            route = CompanyBottomRoutes.CREATE_OFFER,
            labelRes = R.string.company_tab_create,
            selectedIcon = Icons.Filled.AddCircle,
            unselectedIcon = Icons.Outlined.AddCircleOutline
        ),
        CompanyBottomNavItem(
            route = CompanyBottomRoutes.TEAM,
            labelRes = R.string.company_tab_team,
            selectedIcon = Icons.Filled.People,
            unselectedIcon = Icons.Outlined.People
        ),
        CompanyBottomNavItem(
            route = CompanyBottomRoutes.PROFILE,
            labelRes = R.string.company_tab_profile,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.PersonOutline
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFEAEAEA)
            )
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            CompanyBottomBarItem(
                item = item,
                selected = currentRoute == item.route,
                onClick = {
                    onItemClick(item.route)
                }
            )
        }
    }
}

@Composable
fun CompanyBottomBarItem(
    item: CompanyBottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = stringResource(item.labelRes)

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFFDFA52) else Color.Transparent,
        label = "company_bottom_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.Black else Color(0xFF222222),
        label = "company_bottom_content"
    )

    val itemWidth by animateDpAsState(
        targetValue = if (selected) 92.dp else 44.dp,
        label = "company_bottom_width"
    )

    Row(
        modifier = Modifier
            .width(itemWidth)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = if (selected) 10.dp else 0.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(25.dp)
        )

        if (selected) {
            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}