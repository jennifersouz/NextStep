package com.example.nextstep.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.ApplicationMessageDto

@Composable
fun ApplicationChatScreen(
    applicationId: String,
    onBackClick: () -> Unit,
    viewModel: ApplicationChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(applicationId) {
        viewModel.start(applicationId)
    }

    DisposableEffect(applicationId) {
        onDispose {
            viewModel.stopRealtime()
        }
    }

    LaunchedEffect(state.messages.size, state.isLoading) {
        if (!state.isLoading && state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 20.dp, top = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }

            Text(
                text = stringResource(R.string.chat_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessageRes != null && state.messages.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(state.errorMessageRes!!),
                        color = Color(0xFFB00020),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (state.messages.isEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(80.dp))

                            Text(
                                text = stringResource(R.string.no_messages_yet),
                                color = Color(0xFF8A8A8A),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(
                            items = state.messages,
                            key = { message ->
                                message.id
                            }
                        ) { message ->
                            ApplicationChatMessageBubble(message = message)
                        }
                    }
                }
            }
        }

        state.errorMessageRes?.let { errorRes ->
            if (state.messages.isNotEmpty() || !state.isSending) {
                Text(
                    text = stringResource(errorRes),
                    color = Color(0xFFB00020),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                )
            }
        }

        ApplicationChatInputBar(
            value = state.messageText,
            onValueChange = viewModel::onMessageChanged,
            isSending = state.isSending,
            onSendClick = viewModel::sendMessage
        )
    }
}

@Composable
private fun ApplicationChatMessageBubble(
    message: ApplicationMessageDto
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isMine) 18.dp else 4.dp,
                        bottomEnd = if (message.isMine) 4.dp else 18.dp
                    )
                )
                .background(
                    if (message.isMine) {
                        Color(0xFFFDFA52)
                    } else {
                        Color(0xFFF1F1F1)
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = Color.Black,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun ApplicationChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    isSending: Boolean,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = stringResource(R.string.type_message),
                    color = Color(0xFF8A8A8A)
                )
            },
            maxLines = 4,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFE1E1E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        IconButton(
            onClick = onSendClick,
            enabled = !isSending && value.isNotBlank()
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
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
