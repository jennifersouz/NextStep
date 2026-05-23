package com.example.nextstep.ui.screens.student

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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.nextstep.data.model.OfferDto

object StudentBottomRoutes {
    const val HOME = "home"
    const val INTERNSHIPS = "internships"
    const val NOTIFICATIONS = "notifications"
    const val MESSAGES = "messages"
    const val PROFILE = "profile"
}

data class StudentBottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun StudentDashboardScreen(
    onOfferClick: (String) -> Unit = {},
    viewModel: StudentDashboardViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filteredOffers = state.filteredOffers
    val errorMessage = state.errorMessage

    var selectedBottomRoute by rememberSaveable {
        mutableStateOf(StudentBottomRoutes.HOME)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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
                    onValueChange = viewModel::onSearchChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterButton(
                        onClick = {
                            // Depois: abrir filtros reais
                        }
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
                            onRetryClick = viewModel::loadOffers
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
                    items(filteredOffers) { offer ->
                        InternshipOfferCard(
                            offer = offer,
                            onClick = {
                                onOfferClick(offer.id)                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        StudentBottomBar(
            currentRoute = selectedBottomRoute,
            onItemClick = { route ->
                selectedBottomRoute = route
                // Depois ligamos cada item a uma rota/ecrã real.
            }
        )
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
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.filter),
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
fun InternshipOfferCard(
    offer: OfferDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
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
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompanyLogo(
                companyName = offer.companyName
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = offer.companyName,
                    fontSize = 16.sp,
                    color = Color(0xFF8A8A8A)
                )

                Text(
                    text = offer.title,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = offer.location,
                        fontSize = 17.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CompanyLogo(
    companyName: String
) {
    val initials = companyName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(Color(0xFFFDFA52))
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
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
            Text(text = stringResource(R.string.try_again))
        }
    }
}

@Composable
fun StudentBottomBar(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        StudentBottomNavItem(
            route = StudentBottomRoutes.HOME,
            labelRes = R.string.home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        StudentBottomNavItem(
            route = StudentBottomRoutes.INTERNSHIPS,
            labelRes = R.string.internships,
            selectedIcon = Icons.Filled.Work,
            unselectedIcon = Icons.Outlined.WorkOutline
        ),
        StudentBottomNavItem(
            route = StudentBottomRoutes.NOTIFICATIONS,
            labelRes = R.string.notifications,
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.NotificationsNone
        ),
        StudentBottomNavItem(
            route = StudentBottomRoutes.MESSAGES,
            labelRes = R.string.messages,
            selectedIcon = Icons.Filled.Chat,
            unselectedIcon = Icons.Outlined.ChatBubbleOutline
        ),
        StudentBottomNavItem(
            route = StudentBottomRoutes.PROFILE,
            labelRes = R.string.profile,
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
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            StudentBottomBarItem(
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
fun StudentBottomBarItem(
    item: StudentBottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = stringResource(item.labelRes)

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            Color(0xFFFDFA52)
        } else {
            Color.Transparent
        },
        label = "bottom_bar_background"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            Color.Black
        } else {
            Color(0xFF222222)
        },
        label = "bottom_bar_content"
    )

    val itemWidth by animateDpAsState(
        targetValue = if (selected) 116.dp else 44.dp,
        label = "bottom_bar_width"
    )

    Row(
        modifier = Modifier
            .width(itemWidth)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable {
                onClick()
            }
            .padding(horizontal = if (selected) 10.dp else 0.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(26.dp)
        )

        if (selected) {
            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}