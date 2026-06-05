package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextstep.R

@Composable
fun AdvisorDashboardScreen(
    onLogoutSuccess: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(
            onClick = onLogoutSuccess,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = stringResource(R.string.logout),
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.advisor_dashboard_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = stringResource(R.string.advisor_dashboard_placeholder),
                fontSize = 15.sp,
                color = Color(0xFF777777),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}