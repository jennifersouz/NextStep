package com.example.nextstep.ui.screens.advisor

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorAssignedApplicationDto

@Composable
fun AdvisorHomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.advisor_area),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.advisor_home_empty),
            fontSize = 15.sp,
            color = Color(0xFF777777)
        )
    }
}

@Composable
fun AdvisorChatsContent(
    conversations: List<AdvisorAssignedApplicationDto>,
    isLoading: Boolean,
    errorMessageRes: Int?,
    onChatClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.chats),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            errorMessageRes != null -> {
                Text(
                    text = stringResource(errorMessageRes),
                    color = Color(0xFFB00020),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            conversations.isEmpty() -> {
                Text(
                    text = stringResource(R.string.no_chats_yet),
                    color = Color(0xFF777777),
                    fontSize = 15.sp
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = conversations,
                        key = { conversation ->
                            conversation.applicationId
                        }
                    ) { conversation ->
                        AdvisorChatCard(
                            conversation = conversation,
                            onClick = {
                                onChatClick(conversation.applicationId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdvisorChatCard(
    conversation: AdvisorAssignedApplicationDto,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdvisorStudentAvatar(fullName = conversation.studentFullName)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.studentFullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                conversation.course
                    ?.takeIf { it.isNotBlank() }
                    ?.let { course ->
                        Text(
                            text = course,
                            fontSize = 13.sp,
                            color = Color(0xFF777777)
                        )
                    }

                conversation.offerTitle
                    ?.takeIf { it.isNotBlank() }
                    ?.let { offerTitle ->
                        Text(
                            text = offerTitle,
                            fontSize = 13.sp,
                            color = Color(0xFF777777),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
            }

            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDFA52)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        conversation.lastMessage
            ?.takeIf { it.isNotBlank() }
            ?.let { lastMessage ->
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = lastMessage,
                    fontSize = 14.sp,
                    color = Color(0xFF555555),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
    }
}

@Composable
fun AdvisorNotificationsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.notifications),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.advisor_notifications_placeholder),
            fontSize = 15.sp,
            color = Color(0xFF777777)
        )
    }
}

@Composable
fun AdvisorProfileContent(
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = stringResource(R.string.logout),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.advisor_profile_placeholder),
            fontSize = 15.sp,
            color = Color(0xFF777777)
        )
    }
}

@Composable
fun AdvisorStudentAvatar(fullName: String) {
    val initials = fullName
        .split(" ")
        .filter { part -> part.isNotBlank() }
        .take(2)
        .joinToString("") { part -> part.first().uppercase() }
        .ifBlank { "?" }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFF2B2B2B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class AdvisorBottomNavItem(
    val tab: AdvisorTab,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AdvisorBottomBar(
    selectedTab: AdvisorTab,
    onTabSelected: (AdvisorTab) -> Unit
) {
    val items = listOf(
        AdvisorBottomNavItem(
            tab = AdvisorTab.HOME,
            labelRes = R.string.home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        AdvisorBottomNavItem(
            tab = AdvisorTab.CHAT,
            labelRes = R.string.chat,
            selectedIcon = Icons.AutoMirrored.Filled.Chat,
            unselectedIcon = Icons.AutoMirrored.Outlined.Chat
        ),
        AdvisorBottomNavItem(
            tab = AdvisorTab.NOTIFICATIONS,
            labelRes = R.string.notifications,
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.NotificationsNone
        ),
        AdvisorBottomNavItem(
            tab = AdvisorTab.PROFILE,
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
            AdvisorBottomBarItem(
                item = item,
                selected = selectedTab == item.tab,
                onClick = {
                    onTabSelected(item.tab)
                }
            )
        }
    }
}

@Composable
private fun AdvisorBottomBarItem(
    item: AdvisorBottomNavItem,
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
        label = "advisor_bottom_background"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            Color.Black
        } else {
            Color(0xFF222222)
        },
        label = "advisor_bottom_content"
    )

    val itemWidth by animateDpAsState(
        targetValue = if (selected) {
            116.dp
        } else {
            44.dp
        },
        label = "advisor_bottom_width"
    )

    Column(
        modifier = Modifier
            .width(itemWidth)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) {
                item.selectedIcon
            } else {
                item.unselectedIcon
            },
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
