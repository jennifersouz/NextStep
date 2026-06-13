package com.example.nextstep.ui.utils

/**
 * Converte um role técnico (armazenado na base de dados em inglês)
 * para o nome de exibição traduzido no idioma ativo.
 *
 * Exemplo:
 *   roleToDisplayName("student") -> "Aluno" (pt) ou "Student" (en)
 *
 * A normalização é feita internamente: trim() + lowercase().
 *
 * Para usar com stringResource, consulte [roleStringResourceId].
 */
fun roleToDisplayName(role: String?): String {
    return when (role?.trim()?.lowercase()) {
        "student" -> "Aluno"
        "company" -> "Empresa"
        "teacher" -> "Docente"
        "advisor" -> "Orientador"
        "institution" -> "Instituição"
        "admin" -> "Administrador"
        else -> role ?: ""
    }
}

/**
 * Versão em inglês de [roleToDisplayName].
 * Útil se a UI precisar de mostrar o role em inglês independentemente do locale.
 */
fun roleToDisplayNameEn(role: String?): String {
    return when (role?.trim()?.lowercase()) {
        "student" -> "Student"
        "company" -> "Company"
        "teacher" -> "Teacher"
        "advisor" -> "Advisor"
        "institution" -> "Institution"
        "admin" -> "Administrator"
        else -> role ?: ""
    }
}