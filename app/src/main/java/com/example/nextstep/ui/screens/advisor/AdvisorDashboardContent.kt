package com.example.nextstep.ui.screens.advisor

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorAssignedApplicationDto
import com.example.nextstep.ui.components.BottomBarItem
import com.example.nextstep.ui.components.NextStepBottomBar

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

@Composable
fun AdvisorBottomBar(
    selectedTab: AdvisorTab,
    onTabSelected: (AdvisorTab) -> Unit
) {
    NextStepBottomBar(
        items = listOf(
            BottomBarItem(
                route = AdvisorTab.HOME.name,
                icon = Icons.Filled.Home,
                label = stringResource(R.string.home)
            ),
            BottomBarItem(
                route = AdvisorTab.CHAT.name,
                icon = Icons.AutoMirrored.Filled.Chat,
                label = stringResource(R.string.tab_chats)
            ),
            BottomBarItem(
                route = AdvisorTab.NOTIFICATIONS.name,
                icon = Icons.Filled.Notifications,
                label = stringResource(R.string.tab_notifications)
            ),
            BottomBarItem(
                route = AdvisorTab.PROFILE.name,
                icon = Icons.Filled.Person,
                label = stringResource(R.string.tab_profile)
            )
        ),
        selectedItem = selectedTab.name,
        onItemClick = { tabName ->
            onTabSelected(AdvisorTab.valueOf(tabName))
        }
    )
}
