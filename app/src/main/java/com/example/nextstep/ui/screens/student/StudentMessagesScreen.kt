package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.nextstep.data.model.StudentChatConversationDto

@Composable
fun StudentMessagesScreen(
    onConversationClick: (String) -> Unit,
    viewModel: StudentMessagesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadConversations()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }

        state.errorMessageRes != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            StudentMessagesContent(
                conversations = state.conversations,
                onConversationClick = onConversationClick
            )
        }
    }
}

@Composable
private fun StudentMessagesContent(
    conversations: List<StudentChatConversationDto>,
    onConversationClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 32.dp,
            bottom = 110.dp
        )
    ) {
        item {
            Text(
                text = stringResource(R.string.messages),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (conversations.isEmpty()) {
            item {
                StudentMessagesEmptyState()
            }
        } else {
            items(
                items = conversations,
                key = { conversation ->
                    conversation.id
                }
            ) { conversation ->
                StudentConversationCard(
                    conversation = conversation,
                    onClick = {
                        onConversationClick(conversation.id)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun StudentConversationCard(
    conversation: StudentChatConversationDto,
    onClick: () -> Unit
) {
    val companyName = conversation.companyName.orEmpty()
        .ifBlank { stringResource(R.string.company) }

    val offerTitle = conversation.offerTitle.orEmpty()
        .ifBlank { stringResource(R.string.not_available) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE1E1E1),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFFDFA52)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = companyName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = offerTitle,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = statusLabel(conversation.status),
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A)
            )
        }
    }
}

@Composable
private fun StudentMessagesEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            tint = Color(0xFF8A8A8A),
            modifier = Modifier.size(42.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.student_messages_empty_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.student_messages_empty_subtitle),
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun statusLabel(
    status: String?
): String {
    return when (status?.lowercase()) {
        "pending" -> stringResource(R.string.student_application_status_pending)
        "accepted" -> stringResource(R.string.student_application_status_accepted)
        "rejected" -> stringResource(R.string.student_application_status_rejected)
        else -> status.orEmpty()
    }
}