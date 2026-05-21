package com.example.nextstep.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nextstep.R

@Composable
fun SplashScreen() {

    Box( //conteudo no centro
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFA52)), //0xFF + FDFA52, opacidade da cor
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo NextStep",
            modifier = Modifier.size(240.dp)
        )
    }
}