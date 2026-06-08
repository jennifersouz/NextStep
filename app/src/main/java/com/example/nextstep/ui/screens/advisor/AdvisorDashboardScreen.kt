package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.ui.screens.auth.SessionViewModel

@Composable
fun AdvisorDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
    chatsViewModel: AdvisorChatsViewModel = viewModel(),
    sessionViewModel: SessionViewModel = viewModel()
) {
    val chatsState by chatsViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedTab by rememberSaveable {
        mutableStateOf(AdvisorTab.HOME)
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == AdvisorTab.CHAT) {
            chatsViewModel.loadChats()
        }
    }

    DisposableEffect(lifecycleOwner, selectedTab) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && selectedTab == AdvisorTab.CHAT) {
                chatsViewModel.loadChats()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
            when (selectedTab) {
                AdvisorTab.HOME -> AdvisorHomeContent()

                AdvisorTab.CHAT -> AdvisorChatsContent(
                    conversations = chatsState.conversations,
                    isLoading = chatsState.isLoading,
                    errorMessageRes = chatsState.errorMessageRes,
                    onChatClick = onChatClick
                )

                AdvisorTab.NOTIFICATIONS -> AdvisorNotificationsContent()

                AdvisorTab.PROFILE -> AdvisorProfileContent(
                    onLogoutClick = {
                        sessionViewModel.logout(
                            onSuccess = onLogoutSuccess
                        )
                    }
                )
            }
        }

        AdvisorBottomBar(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
            }
        )
    }
}
