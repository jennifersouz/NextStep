package com.example.nextstep.ui.screens.chat

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.ApplicationMessageDto
import com.example.nextstep.ui.utils.DateFormatUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val NextStepYellow = Color(0xFFFDFA52)
private val MessageReceived = Color(0xFFF1F1F1)
private val TextSecondary = Color(0xFF6F7585)
private val BorderGray = Color(0xFFEDEDED)

@Composable
fun ApplicationChatScreen(
    applicationId: String,
    participantName: String? = null,
    onBackClick: () -> Unit,
    viewModel: ApplicationChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(applicationId, participantName) {
        viewModel.start(applicationId, participantName)
    }

    DisposableEffect(applicationId) {
        onDispose {
            viewModel.stopRealtime()
        }
    }

    val chatItems = remember(state.messages) {
        buildChatItems(state.messages, state.currentUserId)
    }

    LaunchedEffect(state.messages.size, state.isLoading) {
        if (!state.isLoading && chatItems.isNotEmpty()) {
            listState.animateScrollToItem(chatItems.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ChatHeader(
            participantName = state.participantName.takeIf { it.isNotBlank() }
                ?: stringResource(R.string.chat_title),
            subtitle = state.internshipTitle.takeIf { it.isNotBlank() } 
                ?: stringResource(R.string.internship_chat_subtitle),
            onBackClick = onBackClick
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading && state.messages.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }

                state.errorMessageRes != null && state.messages.isEmpty() -> {
                    ErrorState(
                        errorRes = state.errorMessageRes!!,
                        onRetry = { viewModel.start(applicationId, participantName) }
                    )
                }

                state.messages.isEmpty() && !state.isLoading -> {
                    EmptyState()
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(
                            items = chatItems,
                            key = { _, item -> item.id }
                        ) { index, item ->
                            when (item) {
                                is ChatListItem.DateSeparator -> {
                                    DateSeparator(label = item.label)
                                }
                                is ChatListItem.MessageItem -> {
                                    MessageBubble(
                                        message = item.message,
                                        isMine = item.isMine
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        MessageInputBar(
            value = state.messageText,
            onValueChange = viewModel::onMessageChanged,
            isSending = state.isSending,
            onSendClick = { viewModel.sendMessage() },
            enabled = state.messageText.isNotBlank()
        )
    }
}

private sealed interface ChatListItem {
    val id: String
    data class DateSeparator(override val id: String, val label: String) : ChatListItem
    data class MessageItem(override val id: String, val message: ApplicationMessageDto, val isMine: Boolean) : ChatListItem
}

private fun buildChatItems(
    messages: List<ApplicationMessageDto>,
    currentUserId: String
): List<ChatListItem> {
    val items = mutableListOf<ChatListItem>()
    var lastDate: String? = null

    messages.forEachIndexed { index, message ->
        val isMine = message.senderProfileId == currentUserId
        val messageDate = getDateFromTimestamp(message.createdAt)

        if (messageDate != lastDate) {
            items.add(
                ChatListItem.DateSeparator(
                    id = "sep_${messageDate}_$index",
                    label = formatDateLabel(message.createdAt)
                )
            )
            lastDate = messageDate
        }

        items.add(
            ChatListItem.MessageItem(
                id = "msg_${message.id.ifBlank { index.toString() }}",
                message = message,
                isMine = isMine
            )
        )
    }

    return items
}

private fun getDateFromTimestamp(timestamp: String): String {
    return try {
        val instant = Instant.parse(timestamp)
        instant.atZone(ZoneId.systemDefault()).toLocalDate().toString()
    } catch (e: Exception) {
        ""
    }
}

private fun formatDateLabel(timestamp: String): String {
    return try {
        val instant = Instant.parse(timestamp)
        val now = Instant.now()
        val zoneId = ZoneId.systemDefault()

        val messageDate = instant.atZone(zoneId).toLocalDate()
        val today = now.atZone(zoneId).toLocalDate()
        val yesterday = today.minusDays(1)

        when (messageDate) {
            today -> "Hoje"
            yesterday -> "Ontem"
            else -> messageDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy"))
        }
    } catch (e: Exception) {
        "Hoje"
    }
}

private fun formatTime(timestamp: String): String {
    return try {
        val instant = Instant.parse(timestamp)
        instant.atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        ""
    }
}

@Composable
private fun ChatHeader(
    participantName: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    // Normalizar nome para exibição (remover + e espaços duplos)
    val displayName = participantName
        .replace("+", " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E5E5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getInitials(displayName.ifEmpty { "Chat" }),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = displayName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderGray)
        )
    }
}

private fun getInitials(name: String): String {
    val parts = name
        .replace("+", " ")
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    return when {
        parts.size >= 2 -> {
            val first = parts.first().first().uppercase()
            val last = parts.last().first().uppercase()
            "$first$last"
        }
        parts.size == 1 -> {
            parts.first().take(2).uppercase()
        }
        else -> "A"
    }
}

@Composable
private fun DateSeparator(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF5F5F5)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ApplicationMessageDto,
    isMine: Boolean
) {
    val bubbleModifier = Modifier
        .fillMaxWidth(0.78f)
        .clip(
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMine) 18.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 18.dp
            )
        )
        .background(
            if (isMine) NextStepYellow else MessageReceived
        )
        .padding(horizontal = 14.dp, vertical = 10.dp)

    val timeText = formatTime(message.createdAt)
    val isRead = message.readAt?.isNotBlank() == true

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (!isMine) 4.dp else 0.dp,
                end = if (isMine) 4.dp else 0.dp
            ),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column {
            Box(modifier = bubbleModifier) {
                Text(
                    text = message.content,
                    color = Color.Black,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(if (isMine) 0.78f else 1f),
                horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
            ) {
                Text(
                    text = timeText,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                )
                if (isMine) {
                    Text(
                        text = if (isRead) "✓✓" else "✓",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    isSending: Boolean,
    onSendClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(R.string.type_message),
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BorderGray,
                    unfocusedBorderColor = BorderGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (enabled && !isSending) NextStepYellow else Color(0xFFE5E5E5))
                    .clickable(enabled = enabled && !isSending) { onSendClick() },
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = stringResource(R.string.send),
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = null,
                tint = Color(0xFFB8B8B8),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_messages),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_messages_description),
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorState(
    errorRes: Int,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(errorRes),
                color = Color(0xFFB00020),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NextStepYellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                    fontSize = 14.sp
                )
            }
        }
    }
}
