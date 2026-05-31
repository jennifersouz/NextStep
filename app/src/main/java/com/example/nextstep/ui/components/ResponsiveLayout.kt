package com.example.nextstep.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * Devolve true se o dispositivo estiver em landscape.
 */
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp > configuration.screenHeightDp
}

/**
 * Layout responsivo para ecrãs de autenticação (Login, Register).
 *
 * Portrait → Column normal (headerContent em cima, formContent em baixo).
 * Landscape → Row dividida a 50/50:
 *   - Lado esquerdo: headerContent centrado verticalmente.
 *   - Lado direito: formContent com scroll vertical e imePadding.
 */
@Composable
fun AuthResponsiveLayout(
    modifier: Modifier = Modifier,
    headerContent: @Composable () -> Unit,
    formContent: @Composable () -> Unit
) {
    if (isLandscape()) {
        Row(
            modifier = modifier.fillMaxSize()
        ) {
            // Lado esquerdo — logo / título / subtítulo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                headerContent()
            }

            // Lado direito — formulário com scroll e suporte ao teclado
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                formContent()
            }
        }
    } else {
        // Portrait — comportamento original
        headerContent()
        formContent()
    }
}

/**
 * Layout responsivo para ecrãs de perfil (StudentProfile, CompanyProfile, CompanyEditProfile).
 *
 * Portrait → Column normal (headerContent em cima, bodyContent em baixo).
 * Landscape → Row dividida a 40/60:
 *   - Lado esquerdo: headerContent (avatar/logo/nome) centrado.
 *   - Lado direito: bodyContent (detalhes/listas/formulários) com scroll.
 */
@Composable
fun ProfileResponsiveLayout(
    modifier: Modifier = Modifier,
    headerContent: @Composable () -> Unit,
    bodyContent: @Composable () -> Unit
) {
    if (isLandscape()) {
        Row(
            modifier = modifier.fillMaxSize()
        ) {
            // Lado esquerdo — avatar / logo / nome
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                headerContent()
            }

            // Lado direito — detalhes / listas / formulários com scroll
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                bodyContent()
            }
        }
    } else {
        // Portrait — comportamento original
        headerContent()
        bodyContent()
    }
}
