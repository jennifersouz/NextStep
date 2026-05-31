package com.example.nextstep.ui.navigation

import androidx.navigation.NavController

/**
 * Tenta fazer popBackStack(). Se a stack estiver vazia (sem destino anterior),
 * navega para [fallbackRoute] em vez de fechar a app...
 *
 * Usar sempre que um ecrã pode ser aberto a partir de múltiplos pontos de entrada
 * (dashboard, notificações, estágios guardados, deep links, etc....)
 */
fun NavController.safePopBack(fallbackRoute: String) {
    val popped = popBackStack()
    if (!popped) {
        navigate(fallbackRoute) {
            launchSingleTop = true
            popUpTo(fallbackRoute) {
                inclusive = false
            }
        }
    }
}
